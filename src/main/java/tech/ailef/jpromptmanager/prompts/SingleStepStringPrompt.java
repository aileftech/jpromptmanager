package tech.ailef.jpromptmanager.prompts;

public class SingleStepStringPrompt extends Prompt<String> {

	@Override
	public String getOutput() {
		return getSteps().get(0).getResponse();
	}

}
