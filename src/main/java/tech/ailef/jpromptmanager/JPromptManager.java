package tech.ailef.jpromptmanager;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;
import org.thymeleaf.context.IContext;

import tech.ailef.jpromptmanager.completion.LLMConnector;
import tech.ailef.jpromptmanager.exceptions.JPromptManagerException;
import tech.ailef.jpromptmanager.prompts.Prompt;

/**
 * 
 *
 */
public class JPromptManager {
	/**
	 * The underlying connector that is able to make requests to the desired LLM
	 */
	private LLMConnector llmConnector;
	
	/**
	 * The prompts as loaded from the XML file
	 */
	private Map<String, PromptTemplate> prompts = new HashMap<>();
	
	/**
	 * Configuration objects
	 */
	private JPromptManagerOptions options;

	/**
	 * Initializes a JPromptManager instance with the provided connector.
	 * This constructor tries to load the prompts from a `prompts.xml` file
	 * in the classpath, and fails if not present.
	 * @param llmConnector	an instance of a LLM connector
	 */
	public JPromptManager(LLMConnector llmConnector) {
		this.llmConnector = llmConnector;
		this.options = new JPromptManagerOptions();
		loadPrompts(getClass().getClassLoader().getResourceAsStream("prompts.xml"));
	}
	
	public JPromptManager(LLMConnector llmConnector, JPromptManagerOptions options) {
		this.llmConnector = llmConnector;
		this.options = options;
		loadPrompts(getClass().getClassLoader().getResourceAsStream("prompts.xml"));
	}
	
	/**
	 * Initializes a JPromptManager instance with the provided connector and prompts file.
	 * @param llmConnector	an instance of a LLM connector
	 * @param promptFile	the XML file to load prompts from
	 */
	public JPromptManager(LLMConnector llmConnector, Path promptFile) {
		this.llmConnector = llmConnector;
		this.options = new JPromptManagerOptions();
		try {
			loadPrompts(new FileInputStream(promptFile.toFile()));
		} catch (FileNotFoundException e) {
			throw new JPromptManagerException("Unable to file prompts file " + promptFile, e);
		}
	}
	
	/**
	 * Initializes a JPromptManager instance with the provided connector and prompts file.
	 * @param llmConnector	an instance of a LLM connector
	 * @param promptFile	the XML file to load prompts from
	 * @param options	configuration object for JPromptManager
	 */
	public JPromptManager(LLMConnector llmConnector, Path promptFile, JPromptManagerOptions options) {
		this.options = options;
		this.llmConnector = llmConnector;
		this.options = options;
		try {
			loadPrompts(new FileInputStream(promptFile.toFile()));
		} catch (FileNotFoundException e) {
			throw new JPromptManagerException("Unable to file prompts file " + promptFile, e);
		}
	}

	/**
	 * Completes the given prompt 
	 * @param <T>	the output type of this prompt
	 * @param prompt	the class of the prompt to complete
	 * @return	an object of type T as specified by the prompt class implementation
	 */
	public <T> T complete(Class<? extends Prompt<T>> prompt) {
		return llmConnector.complete(prompt, this);
	}
	
	/**
	 * Completes the given prompt with the given context (i.e. variables
	 * to be replaced by the templating engine).
	 *  
	 * @param <T>	the output type of this prompt
	 * @param prompt	the class of the prompt to complete
	 * @param context	a Map containing the variables to replace in the template
	 * @return	an object of type T as specified by the prompt class implementation
	 */
	public <T> T complete(Class<? extends Prompt<T>> prompt, IContext context) {
		return llmConnector.complete(prompt, context, this);
	}
	
	/**
	 * Returns the {@link PromptTemplate} with the given name, null if missing.
	 * @param name	the name of the prompt to get
	 * @return
	 */
	public PromptTemplate getPromptTemplate(String name) {
		return prompts.get(name);
	}

	/**
	 * Loads prompts from an XML file. This step will perform some basic data validation
	 * and can throw a {@link JPromptManagerException} if some of these checks fail.
	 * @param fileStream
	 */
	private void loadPrompts(InputStream fileStream) throws JPromptManagerException {
		SAXReader reader = new SAXReader();
		
		try {
			Document document = reader.read(fileStream);
			
			List<Node> prompts = document.selectNodes("/prompts/prompt");
			
			prompts.forEach(p -> {
				Element promptElement = (Element)p;
				
				String promptClass = promptElement.attributeValue("type");
				if (promptClass == null)
					throw new JPromptManagerException("Found prompt object with missing `type` attribute: " + promptElement.asXML());
				
				List<PromptStep> steps = new ArrayList<>();
				
				List<Node> stepNodes = p.selectNodes("step");
				stepNodes.forEach(step -> {
					// Remove leading spaces caused by indentation in XML file
					// Also removing <step> tags that are included when using asXML()
					// which is needed to avoid having thymeleaf tags not present
					String normalizedText = Arrays.stream(
						step.asXML().split("\\n")).map(l -> l.trim())
							.filter(l -> !l.startsWith("<step") && !l.startsWith("</step"))
							.collect(Collectors.joining("\n")
					);
					
					Element stepElement = (Element)step;
					
					Map<String, String> stepParams = new HashMap<>();
					stepElement.attributes().forEach(attribute -> {
						String attrName = attribute.getName();
						String attrValue = attribute.getValue();
						stepParams.put(attrName, attrValue);
					});
					
					PromptStep newStep = new PromptStep(normalizedText);
					newStep.setParams(stepParams);
					
					String stepName = stepElement.attributeValue("name", null);
					if (stepName == null)
						throw new JPromptManagerException("Found step object with missing `name` attribute; found: " + stepElement.asXML());
					
					newStep.setName(stepName);
					
					steps.add(newStep);
				});
				
				
				Set<String> distinctNames = 
					steps.stream().map(PromptStep::getName).collect(Collectors.toSet());
				if (steps.size() != distinctNames.size()) {
					throw new JPromptManagerException("All prompt steps must have unique names: invalid prompt `" + promptClass);
				}
				
				this.prompts.put(promptClass, new PromptTemplate(steps));
			});
		} catch (DocumentException e) {
			throw new JPromptManagerException(e);
		}
	}
	
	public JPromptManagerOptions getOptions() {
		return options;
	}
}
