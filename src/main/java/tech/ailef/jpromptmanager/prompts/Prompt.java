package tech.ailef.jpromptmanager.prompts;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import tech.ailef.jpromptmanager.PromptStep;

/**
 * A class providing an implementation for a given prompt type, as defined
 * in the XML file. The `getOutput()` method has to be implemented.
 *
 * @param <T>	the return type of this prompt
 */
public abstract class Prompt<T> {
	protected List<PromptStep> steps = new ArrayList<>();
	
	protected String name;
	
	/**
	 * This method must use the data that has been populated in the
	 * `steps` list by the LLM calls, in order to build and return an
	 * object of type T. 
	 * @return	the object build using the prompt output
	 */
	public abstract T getOutput();
	
	public List<PromptStep> getSteps() {
		return Collections.unmodifiableList(steps);
	}
	
	/**
	 * Returns the step with the given name
	 * @param name	the name of the step
	 * @return	the step with the given name, if found
	 */
	public Optional<PromptStep> getStep(String name) {
		return steps.stream().filter(s -> s.getName().equals(name)).findFirst();
	}
	
	public String getName() {
		return getClass().getSimpleName();
	}
	
	/**
	 * Adds a step to this prompt. Used when appending the output
	 * of a LLM request.
	 * @param step	the step to add to this prompt
	 */
	public void addStep(PromptStep step) {
		this.steps.add(step);
	}
	
}
