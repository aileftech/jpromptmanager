package tech.ailef.jpromptmanager.examples;

import java.nio.file.Paths;
import java.util.Map;

import org.thymeleaf.context.IContext;

import tech.ailef.jpromptmanager.JPromptManager;
import tech.ailef.jpromptmanager.PromptContextBuilder;
import tech.ailef.jpromptmanager.completion.LLMConnector;
import tech.ailef.jpromptmanager.completion.OpenAIGPT3Connector;

/**
 * A set of examples.
 */
public class ExampleMain {
	/**
	 * Example main that shows the most common use cases.
	 */
	public static void main(String[] args) {
		/*
		 * Instantiate OpenAI connector with OpenAI secret key, timeout (0 for no timeout), model id
		 * Both GPT3 and ChatGPT connector are supported. ChatGPT connector supports an optional
		 * 4th argument as the system prompt: a text that is prepended to each conversation in order
		 * to give general guidance to the model (see https://platform.openai.com/docs/guides/chat).
		 */
//		LLMConnector openAI = new OpenAIChatGPTConnector("OPENAI_KEY", 0, "gpt-3.5-turbo");
		LLMConnector openAI = new OpenAIGPT3Connector("OPENAI_KEY", 0, "text-davinci-003");
		
		/*
		 * Create JPromptManager instance using the connector. By default, this will try to load
		 * prompts from a `prompts.xml` file in the classpath. You can pass a Path object to
		 * load prompts from a file in a different location, such as:
		 *
		 * JPromptManager jPrompt = new JPromptManager(openAI, Paths.get("path/to/prompts.xml"));
		 */
		JPromptManager jPrompt = new JPromptManager(openAI, Paths.get("src/main/resources/prompts-examples.xml"));

		/*
		 * The example prompt templates take the two arguments `shopType` and `country`.
		 * Let's initialize two different settings to seed our prompts later.
		 */
		IContext frenchCarShop = 
			new PromptContextBuilder().set("shopType", "car repair").set("country", "France").build();
		IContext chineseFruitShop = 
			new PromptContextBuilder().set("shopType", "fruit shop").set("country", "China").build();
		
		/*
		 * Completion of single step prompt. Will return a plain String with the response.
		 */
		String tagline1 = jPrompt.complete(ExampleCreateTagline.class, frenchCarShop);
		System.out.println("Tagline for the French car shop: " + tagline1);
		
		String tagline2 = jPrompt.complete(ExampleCreateTagline.class, chineseFruitShop);
		System.out.println("Tagline for the Chinese fruit shop: " + tagline2);
		
		/*
		 * Completion of multi-step prompt. Will return a Map where the step names are mapped
		 * to their responses. 
		 */
		Map<String, String> complete = jPrompt.complete(ExampleCreateTaglineLocation.class, frenchCarShop);
		System.out.println(complete);
		
		/*
		 * Completion of a prompt that maps the output to a custom POJO.
		 */
		Shop shop = jPrompt.complete(ExampleCreateShop.class, frenchCarShop);
		System.out.println(shop);
		
		/*
		 * Completion of a prompt that maps the output to a custom POJO. This uses
		 * custom deserialization logic to produce the final Shop object. 
		 */
		Shop shop1 = jPrompt.complete(
			ExampleCreateShopMultiStep.class, 
			chineseFruitShop
		);
		System.out.println(shop1);
	}
}
