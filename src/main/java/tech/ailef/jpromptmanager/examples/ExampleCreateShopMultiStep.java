package tech.ailef.jpromptmanager.examples;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import tech.ailef.jpromptmanager.exceptions.JPromptManagerException;
import tech.ailef.jpromptmanager.prompts.Prompt;

/**
 * An example of a prompt class that deserializes the output of multiple
 * steps into a POJO.
 */
public class ExampleCreateShopMultiStep extends Prompt<Shop> {
	private static final Gson gson = new Gson();
	
	@Override
	/**
	 * Builds a Shop object by deserializing the output of the first step in the prompt,
	 * and then filling the remaining info from step 2. 
	 * When prompted correctly and with low temperature, LLMs almost always produce valid JSON code; 
	 * nevertheless, it may be wise to account for them anyway as shown below.
	 */
	public Shop getOutput() {
		try {
			Shop fromJson = gson.fromJson(steps.get(0).getResponse(), Shop.class);
			
			String shopHistory = steps.get(1).getResponse();
			fromJson.setShopHistory(shopHistory);
			
			return fromJson;
		} catch (JsonSyntaxException e) {
			throw new JPromptManagerException("Error parsing LLM output as JSON: received " + steps.get(0).getResponse(), e);
		}
	}

}
