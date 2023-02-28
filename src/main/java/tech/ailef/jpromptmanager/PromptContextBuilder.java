package tech.ailef.jpromptmanager;

import java.util.HashMap;
import java.util.Map;

/**
 * A simple utility class to build a Map&lt;String, String&gt; that can be passed
 * to the templating engine.
 * 
 */
public class PromptContextBuilder {
	private Map<String, String> context = new HashMap<>();
	
	public PromptContextBuilder set(String key, String value) {
		this.context.put(key, value);
		return this;
	}
	
	public Map<String, String> build() {
		return context;
	}
	
}
