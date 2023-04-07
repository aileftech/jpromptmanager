package tech.ailef.jpromptmanager.completion;

import java.time.Duration;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import com.theokanning.openai.completion.CompletionChoice;
import com.theokanning.openai.completion.CompletionRequest;
import com.theokanning.openai.completion.CompletionResult;
import com.theokanning.openai.service.OpenAiService;

import tech.ailef.jpromptmanager.exceptions.JPromptManagerException;

/**
 * An implementation of the LLMConnector that allows to make requests
 * to the GPT-3 OpenAI endpoints. 
 *
 */
public class OpenAIGPT3Connector implements LLMConnector {
	private OpenAiService service;

	private String model;
	
	/**
	 * Builds the connector with the provided parameters.
	 * @param apiKey	OpenAI secret key
	 * @param timeout	timeout for requests, 0 means no timeout
	 * @param model	the OpenAI model to use (this setting will be overridden if an individual `<step>` tag provides a different value)
	 */
	public OpenAIGPT3Connector(String apiKey, int timeout, String model) {
		if (model == null)
			throw new JPromptManagerException("Must specify which OpenAI model to use");

		service = new OpenAiService(apiKey, Duration.ofSeconds(timeout));
		this.model = model;
	}

	/**
	 * Requests a completion for the given text/params to OpenAI. 
	 */
	@Override
	public String complete(String prompt, Map<String, String> params) {
		// Cast parameters to correct type
		double temperature = Double.parseDouble(params.get("temperature"));
		double topP = Double.parseDouble(params.get("topP"));
		String model = params.get("model");
		int maxTokens = Integer.parseInt(params.get("maxTokens"));
		
		CompletionRequest completionRequest = CompletionRequest.builder()
		        .prompt(prompt)
		        .stop(Arrays.asList(LLMConnector.PROMPT_TOKEN))
		        .temperature(temperature)
		        .topP(topP)
		        .model(model)
		        .maxTokens(maxTokens)
		        .n(1)
		        .echo(false)
		        .build();
		
		CompletionResult createCompletion = service.createCompletion(completionRequest);
		CompletionChoice choice = createCompletion.getChoices().get(0);
		
		return choice.getText();
	}

	@Override
	public Map<String, String> getDefaultParams() {
		Map<String, String> params = new HashMap<>();
		params.put("model", model);
		params.put("temperature", "0");
		params.put("topP", "1");
		params.put("maxTokens", "256");
		return params;
	}
}
