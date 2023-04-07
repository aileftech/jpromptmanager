package tech.ailef.jpromptmanager.completion;

import java.time.Duration;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import com.theokanning.openai.completion.chat.ChatCompletionChoice;
import com.theokanning.openai.completion.chat.ChatCompletionRequest;
import com.theokanning.openai.completion.chat.ChatCompletionResult;
import com.theokanning.openai.completion.chat.ChatMessage;
import com.theokanning.openai.service.OpenAiService;

import tech.ailef.jpromptmanager.exceptions.JPromptManagerException;

/**
 * An implementation of the LLMConnector that allows to make requests
 * to the ChatGPT OpenAI endpoints. 
 *
 */
public class OpenAIChatGPTConnector implements LLMConnector {
	private OpenAiService service;

	private String model;
	
	private String systemPrompt;
	
	/**
	 * Builds the connector to ChatGPT with the required parameters.
	 * @param apiKey	OpenAI secret key
	 * @param timeout	timeout for requests, 0 means no timeout
	 * @param model	the OpenAI model to use (this setting will be overridden if an individual `<step>` tag provides a different value),
	 */
	public OpenAIChatGPTConnector(String apiKey, int timeout, String model) {
		this(apiKey, timeout, model, "You are a helpful assistant.");
	}
	
	/**
	 * Builds the connector to ChatGPT with the required parameters.
	 * @param apiKey	OpenAI secret key
	 * @param timeout	timeout for requests, 0 means no timeout
	 * @param model	the OpenAI model to use (this setting will be overridden if an individual `<step>` tag provides a different value),
	 * @param systemPrompt the initial system prompt that gets prepended to each conversation (see https://platform.openai.com/docs/guides/chat)
	 */
	public OpenAIChatGPTConnector(String apiKey, int timeout, String model, String systemPrompt) {
		service = new OpenAiService(apiKey, Duration.ofSeconds(timeout));
		
		if (model == null)
			throw new JPromptManagerException("Must specify which OpenAI model to use");
		
		this.model = model;
		this.systemPrompt = systemPrompt;
	}

	/**
	 * Requests a completion for the given text/params to OpenAI. 
	 */
	@Override
	public String complete(String prompt, Map<String, String> params) {
		int maxTokens = Integer.parseInt(params.get("maxTokens"));
		double temperature = Double.parseDouble(params.get("temperature"));
		
		String[] messages = prompt.split("(" + LLMConnector.PROMPT_TOKEN + ")|(" + LLMConnector.COMPLETION_TOKEN + ")");
		
		AtomicInteger count = new AtomicInteger(0);
		List<ChatMessage> chatMessages = Arrays.asList(messages).stream().filter(x -> x.trim().length() > 0)
			.map(x -> x.trim())
			.map(x -> {
				if (count.get() % 2 == 0) {
					return new ChatMessage("user", x);
				} else {
					return new ChatMessage("assistant", x);
				}
			})
			.collect(Collectors.toList());
		chatMessages.add(0, new ChatMessage("system", systemPrompt));
		
		
		ChatCompletionRequest completionRequest = ChatCompletionRequest.builder()
			.messages(chatMessages)
			.maxTokens(maxTokens)
			.temperature(temperature)
			.model(model)
			.build();
		
		ChatCompletionResult chatCompletion = service.createChatCompletion(completionRequest);
		ChatCompletionChoice choice = chatCompletion.getChoices().get(0);
			
		return choice.getMessage().getContent();
	}

	@Override
	public Map<String, String> getDefaultParams() {
		Map<String, String> params = new HashMap<>();
		params.put("model", model);
		params.put("temperature", "0");
		params.put("maxTokens", "256");
		return params;
	}
}
