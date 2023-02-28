package tech.ailef.jpromptmanager;

import java.util.List;

/**
 * A prompt template which includes variables that have to be replaced
 * with actual values before executing.
 * 
 */
public class PromptTemplate {
	private List<PromptStep> steps;
	
	public PromptTemplate(List<PromptStep> steps) {
		this.steps = steps;
	}

	public List<PromptStep> getSteps() {
		return steps;
	}
}
