package tech.ailef.jpromptmanager.prompts;

import java.util.Map;
import java.util.stream.Collectors;

public class MultiStepStringPrompt extends Prompt<Map<String, String>> {

	@Override
	public Map<String, String> getOutput() {
		return getSteps().stream().collect(Collectors.toMap(s -> s.getName(), s -> s.getResponse()));
	}

}
