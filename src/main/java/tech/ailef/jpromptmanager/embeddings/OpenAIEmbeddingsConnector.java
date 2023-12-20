package tech.ailef.jpromptmanager.embeddings;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import com.theokanning.openai.embedding.Embedding;
import com.theokanning.openai.embedding.EmbeddingRequest;
import com.theokanning.openai.embedding.EmbeddingResult;
import com.theokanning.openai.service.OpenAiService;

import tech.ailef.jpromptmanager.exceptions.JPromptManagerException;

public class OpenAIEmbeddingsConnector {
	private OpenAiService service;

	private String model;
	
//	private int maxRetries;
	
	public OpenAIEmbeddingsConnector(String apiKey, int timeout, int maxRetries, String model) {
		service = new OpenAiService(apiKey, Duration.ofSeconds(timeout));
//		this.maxRetries = maxRetries;
		
		if (model == null)
			throw new JPromptManagerException("Must specify which OpenAI model to use");
		
		this.model = model;
	}
	
	public List<EmbeddingVector> embeddings(List<String> lines) {
		List<EmbeddingVector> results = new ArrayList<>();
		
		System.out.println("COMPUTING EMBEDDINSG FOR " + lines.size() + " ROWS....");
		EmbeddingRequest build = EmbeddingRequest.builder()
			.model(model)
			.input(lines)
			.build();
		EmbeddingResult r = service.createEmbeddings(build);
		System.out.println("GOT " + r.getData().size() + " VECTORS ");
		
		for (int i = 0; i < r.getData().size(); i++) {
			Embedding embedding = r.getData().get(i);
			EmbeddingVector embeddingVector = new EmbeddingVector(embedding.getEmbedding());
			results.add(embeddingVector);
		}
		
		return results;
	
	}
	
}
