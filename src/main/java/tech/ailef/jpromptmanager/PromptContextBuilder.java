package tech.ailef.jpromptmanager;

import org.thymeleaf.context.Context;
import org.thymeleaf.context.IContext;

/**
 * A simple utility class to build a Map&lt;String, String&gt; that can be passed
 * to the templating engine.
 * 
 */
public class PromptContextBuilder {
	private Context context = new Context();
	
	public PromptContextBuilder set(String key, Object value) {
		this.context.setVariable(key, value);
		return this;
	}
	
	public IContext build() {
		return context;
	}
	
}
