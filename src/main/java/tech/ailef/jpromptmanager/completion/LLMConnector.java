package tech.ailef.jpromptmanager.completion;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.IContext;

import tech.ailef.jpromptmanager.JPromptManager;
import tech.ailef.jpromptmanager.PromptContextBuilder;
import tech.ailef.jpromptmanager.PromptStep;
import tech.ailef.jpromptmanager.PromptTemplate;
import tech.ailef.jpromptmanager.exceptions.JPromptManagerException;
import tech.ailef.jpromptmanager.prompts.Prompt;
/**
 * Generic interface that defines methods needed to interact with a LLM. 
 * 
 * Currently, the only available implementation connects to OpenAI api.
 * You can provide an implementation for the two abstract methods if you want
 * to build a custom connector to any other LLM.
 */
public interface LLMConnector {
	public static final Logger logger = LogManager.getLogger(LLMConnector.class);
	
	public static final String PROMPT_TOKEN = "\n\n__#PROMPT#__\n\n";
	
	public static final String COMPLETION_TOKEN = "\n\n__#COMPLETION#__\n\n";
	
	public static final TemplateEngine TE = new TemplateEngine();
	
	/**
	 * Performs a request to the underlying LLM and returns the response
	 * @param prompt	the prompt text
	 * @param params	the (optional) params sent along the request (e.g., temperature, model id, max tokens, etc...)
	 * @return	the raw LLM response
	 */
	public String complete(String prompt, Map<String, String> params);
	
	/**
	 * Returns a (possibly empty) map containing the default values for the request params.
	 * These values will be applied to all requests, except when a different value for the
	 * same parameter is set explicitly on a &lt;step&gt; tag, as the latter will take precedence.
	 */
	public Map<String, String> getDefaultParams();
	
	/**
	 * Default implementation that takes care of performing template variable substitution and
	 * chaining the different steps correctly. 
	 * @param <T>	the type of the final object returned by running this prompt
	 * @param promptClass	the class of the prompt
	 * @param context	the variables for the templating engine
	 * @param jPrompt the jPrompt instance	
	 * @return on object of type T as specified in the prompt class
	 */
	public default <T> T complete(Class<? extends Prompt<T>> promptClass, IContext context, JPromptManager jPrompt) {
		Prompt<T> prompt;
		try {
			prompt = promptClass.getConstructor().newInstance();
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
				| NoSuchMethodException | SecurityException e) {
			throw new JPromptManagerException("Error when calling no-args constructor for class " + promptClass, e);
		}
		
		StringBuilder incrementalPrompt = new StringBuilder();
		
		PromptTemplate promptTemplate = jPrompt.getPromptTemplate(prompt.getName());
		
		if (promptTemplate == null) {
			throw new JPromptManagerException("Unable to find prompt template for " + prompt.getName());
		}
		
		for (int i = 0; i < promptTemplate.getSteps().size(); i++) {
			PromptStep promptStep = promptTemplate.getSteps().get(i);
			
			String processedPrompt = PROMPT_TOKEN + TE.process(promptStep.getTemplate(), context).trim() + COMPLETION_TOKEN;
			promptStep.setPrompt(processedPrompt);
			incrementalPrompt.append(processedPrompt);

			// Get the default request params and then merge (override)
			// with the values (if any) taken from the current step
			Map<String, String> requestParams = getDefaultParams();
			requestParams.putAll(promptStep.getParams());
			
			String currentPrompt = incrementalPrompt.toString();
			logger.info(
				prompt.getName() + ":" + (i + 1) + "/" + promptTemplate.getSteps().size() + ":" + promptStep.getName()
				+ " ~" + (currentPrompt.length() / 3) + " tokens | params: " + requestParams 
			);
			
			String responseText = complete(currentPrompt, requestParams);
			
			incrementalPrompt.append(responseText);
			promptStep.setResponse(responseText);
			prompt.addStep(promptStep);
		}
		
		return prompt.getOutput();
	}
	
	/**
	 * Default implementation for prompt completion with no template variable substitution
	 * @param <T>
	 * @param prompt
	 * @return
	 */
	public default <T> T complete(Class<? extends Prompt<T>> prompt, JPromptManager jPrompt) {
		return complete(prompt, new PromptContextBuilder().build(), jPrompt);
	}
}
