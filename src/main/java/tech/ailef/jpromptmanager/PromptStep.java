package tech.ailef.jpromptmanager;

import java.util.Map;

import tech.ailef.jpromptmanager.completion.LLMConnector;

/**
 * A step in a prompt.
 *
 */
public class PromptStep {
	/**
	 * Prompt template text
	 */
	private String template;
	
	/**
	 * Prompt text after variable interpolation
	 */
	private String prompt;

	/**
	 * The raw text response to this step
	 */
	private String response;
	
	/**
	 * The name of the current step
	 */
	private String name;
	
	/**
	 * A set of parameters that are used to customize the request (for this step only) 
	 * to the LLM. These can include temperature, topP, model id or whatever your
	 * LLM requires. They are loaded from the attributes on the <step> XML elements
	 * and override those returned by {@link LLMConnector#getDefaultParams()}.
	 */
	private Map<String, String> params;
	
	public PromptStep(String template) {
		this(template, null);
	}
	
	public PromptStep(String template, String name) {
		this.template = template;
		this.name = name;
	}

	/**
	 * Returns the template for this prompt step.
	 * @return	the template text for this prompt step (it includes ${variables})
	 */
	public String getTemplate() {
		return template;
	}

	/**
	 * Returns the text of this prompt after variable interpolation
	 * @return the text of this prompt after variable interpolation
	 */
	public String getPrompt() {
		return prompt;
	}

	/**
	 * Returns the LLM response to this step
	 * @return
	 */
	public String getResponse() {
		return response;
	}

	/**
	 * Returns the name of this step
	 * @return
	 */
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public void setResponse(String response) {
		this.response = response;
	}
	
	public void setPrompt(String prompt) {
		this.prompt = prompt;
	}
	
	public Map<String, String> getParams() {
		return params;
	}
	
	/**
	 * Returns a set of parameters that are used to customize the request (for this step only) 
	 * to the LLM. These can include temperature, topP, model id or whatever your
	 * LLM requires. They are loaded from the attributes on the &lt;step&gt; XML elements
	 * and override those returned by {@link LLMConnector#getDefaultParams()}.
	 */
	public void setParams(Map<String, String> params) {
		this.params = params;
	}
}
