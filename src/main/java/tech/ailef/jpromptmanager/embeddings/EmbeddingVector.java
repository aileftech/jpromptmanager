package tech.ailef.jpromptmanager.embeddings;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class EmbeddingVector {
	private List<Double> vector;
	
	public EmbeddingVector() {
	}
	
	public EmbeddingVector(List<Double> vector) {
		this.vector = vector;
	}

	public List<Double> getVector() {
		return vector;
	}
	
}
