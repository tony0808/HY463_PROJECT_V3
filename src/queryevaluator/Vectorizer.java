package queryevaluator;

import java.util.HashMap;
import java.util.Map;

public class Vectorizer {
	
	private String[] words;
	private int num_docs;
	private HashMap<String, VocabData> vocabulary;
	public Vectorizer(HashMap<String, VocabData> vocabulary) { this.vocabulary = vocabulary; }
	
	public void setWords(String[] words) { this.words = words; }
	public void setNumDocs(int num_docs) { this.num_docs = num_docs; }
	
	public HashMap<Long, Double> getVector() {
		HashMap<Long, Double> vector = new HashMap<>();
		long index;
		String word;
		double tf, idf, weight;
		HashMap<String, Integer> tfMap = getTFmap();
		int maxTF = getMaxTF(tfMap);
		for(Map.Entry<String, Integer> entry : tfMap.entrySet()) {
			word = entry.getKey();
			tf = (double) entry.getValue() / maxTF;
			idf = getIDF(this.vocabulary.get(word).getDF());
			weight = tf * idf;
			index = this.vocabulary.get(word).getIndex();
			vector.put(index, weight);
		}
		return vector;
	}
	
	private HashMap<String, Integer> getTFmap() {
		HashMap<String, Integer> tfMap = new HashMap<>();
		for(String word : this.words) {
			if(!tfMap.containsKey(word)) { tfMap.put(word, 1); }
			else { tfMap.put(word, tfMap.get(word)+1); }
		}
		return tfMap;
	}
	
	private int getMaxTF(HashMap<String, Integer> tfMap) {
		int tf, maxTF = 0;
		for(Map.Entry<String, Integer> entry : tfMap.entrySet()) { tf = entry.getValue(); if(tf > maxTF) { maxTF = tf; } }
		return maxTF;
	}
	
	private double getIDF(int df) { return Math.log((double) num_docs / df); }
}
