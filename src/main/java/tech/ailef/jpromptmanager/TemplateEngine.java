package tech.ailef.jpromptmanager;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A simple templating engine that performs variable substitution on text.
 * Variables are enclosed with this ${syntax}.
 *
 */
public class TemplateEngine {
	private static final Pattern variablePattern = Pattern.compile("\\$\\{(.+?)\\}");
	
	public String process(String template, Map<String, String> params) {
		if (params == null || params.isEmpty()) return template;
		
		Matcher matcher = variablePattern.matcher(template);
		
		String result = template;
		while (matcher.find()) {
			result = result.replace(matcher.group(0), params.getOrDefault(matcher.group(1), ""));
		}
		
		return result;
	}
}
