package tech.ailef.jpromptmanager.examples;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import tech.ailef.jpromptmanager.PromptStep;
import tech.ailef.jpromptmanager.exceptions.JPromptManagerException;
import tech.ailef.jpromptmanager.prompts.Prompt;

/**
 * An example prompt class that deserializes the output of a single step into a POJO. 
 */
public class ExampleCreateShop extends Prompt<Shop> {
	private static final Gson gson = new Gson();
	
	@Override
	/**
	 * Builds a Shop object by deserializing the output of the first (and only)
	 * step in the prompt. When prompted correctly and with low temperature, LLMs
	 * almost always produce valid JSON code; nevertheless, it may be wise to
	 * account for them anyway as shown below.
	 */
	public Shop getOutput() {
		PromptStep userJson = steps.get(0);
		
		try {
			return gson.fromJson(userJson.getResponse(), Shop.class);
		} catch (JsonSyntaxException e) {
			throw new JPromptManagerException("Error parsing LLM output as JSON: received " + userJson.getResponse(), e);
		}
	}

}
