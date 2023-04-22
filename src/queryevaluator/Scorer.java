package queryevaluator;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

public class Scorer {
	
	private DocumentFileScanner docFileScanner;
	private TreeMap<Double, Integer> scores;
	
	public Scorer(String parentDirectory) {
		this.scores = new TreeMap<>(Collections.reverseOrder()); 
		this.docFileScanner = new DocumentFileScanner();
		this.docFileScanner.setParentDirectory(parentDirectory);
	}
	
	public void addScore(double score, int docid) throws IOException { this.scores.put(score, docid); }
	
	public void printScores() throws IOException {
		String docname;
		double score;
		int docid;
		for(Map.Entry<Double, Integer> entry : this.scores.entrySet()) {
			score = entry.getKey();
			docid = entry.getValue();
			docname = this.docFileScanner.getDocumentName(docid);
			System.out.println("Score : " + score + "\n" + docname + "\n");
		}
	}
	
	public double calculateScore(HashMap<Long, Double> queryVec, HashMap<Long, Double> docVec, int docid) throws IOException {
		double innerProduct = getInnerProduct(queryVec, docVec);
		double magProduct = getMagProduct(queryVec, docVec, docid);
		return innerProduct / magProduct;
	}
	
	private double getMagProduct(HashMap<Long, Double> queryVec, HashMap<Long, Double> docVec, int docid) throws IOException {
		double queryVecMag = getVectorMag(queryVec);
		double docVecMag = this.docFileScanner.getDocumentNorm(docid);
		return queryVecMag * docVecMag;
	}
	
	private double getInnerProduct(HashMap<Long, Double> vecA, HashMap<Long, Double> vecB) {
		double innerProduct = 0.0;
		long index;
		for(Map.Entry<Long, Double> vecAentry : vecA.entrySet()) {
			index = vecAentry.getKey();
			if(vecB.get(index) == null) continue;
			innerProduct += vecA.get(index) * vecB.get(index);
		}
		return innerProduct;
	}
	
	private double getVectorMag(HashMap<Long, Double> vec) {
		double mag = 0.0;
		for(Map.Entry<Long, Double> entry : vec.entrySet()) { mag += entry.getValue() * entry.getValue(); }
		return Math.sqrt(mag);
	}
}




