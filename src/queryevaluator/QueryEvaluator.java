package queryevaluator;

import java.io.IOException;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;

public class QueryEvaluator {
	
	private String query;
	private VocabularyLoader vocLoader;
	private PostingFileScanner pFscanner;
	private DocumentsFileScanner docScanner;
	private DocumentVectorFileScanner docVecScanner;
	
	private TreeMap<Long, Double> queryVector;
	private TreeMap<Double, Integer> scoresMap;
	
	public QueryEvaluator(String query, String parentDirectory) throws IOException { 
		this.query = query;
		this.scoresMap = new TreeMap<Double, Integer>(Comparator.reverseOrder());
		this.queryVector = new TreeMap<Long, Double>();
		this.vocLoader = new VocabularyLoader(parentDirectory);
		this.pFscanner = new PostingFileScanner(parentDirectory);
		this.docScanner = new DocumentsFileScanner(parentDirectory);
		this.docVecScanner = new DocumentVectorFileScanner(parentDirectory);
		proceessQuery();
	}
	
	public TreeMap<Double, Integer> getQueryScores() { return this.scoresMap; }
	public void setQuery(String query) { this.query = query; proceessQuery(); }
	
	public void printQueryScores() throws IOException {
		int docid;
		double score;
		HashMap<Integer, String> docnames = this.docScanner.getDocumentNames();
		String docname;
        int maxLength = 0;
        int padding;
        for (String value : docnames.values()) { maxLength = Math.max(maxLength, value.length()); }
		for(Map.Entry<Double, Integer> entry : this.scoresMap.entrySet()) {
			score = entry.getKey();
			docid = entry.getValue();
			docname = docnames.get(docid);
			padding = maxLength - docname.length();
            System.out.println(docname + ": " + " ".repeat(padding) + score);
		}
	}
	
	public void setQueryScores() throws IOException {
		double innerProduct;
		double magnitudeProduct;
		double score;
		int index = 0;
		TreeMap<Long, Double> docVector;
		TreeSet<Integer> docIDUnion = getUnionOfDocIds();
		int[] docIds = new int[docIDUnion.size()];
		setDocumentVectorMap(docIDUnion);
		setQueryVector();
		for(int docid : docIDUnion) {
			docVector = this.docVecScanner.getDocumentVector(docid);
			if((innerProduct = getInnerProduct(docVector)) == 0) continue;
			magnitudeProduct = getMagProduct(docid);
			score = innerProduct / magnitudeProduct;
			this.scoresMap.put(score, docid);
			docIds[index++] = docid;
		}
		this.docScanner.setDocIds(docIds);
	}
	
	private void proceessQuery() {
		this.query = (new QueryProcessor(this.query, this.vocLoader)).getProcessedQuery();
		if(this.query.length() == 0) {
			System.out.println("Your query is empty after processing it. Expand it a little.");
			System.exit(1);
		}
	}
	
	private void setDocumentVectorMap(TreeSet<Integer> docsids) throws IOException {
		int[] docIds = new int[docsids.size()];
		int index = 0;
		for(int docid : docsids) { docIds[index++] = docid; }
		this.docVecScanner.setDocIds(docIds);
		this.docVecScanner.buildDocumentsVectorMap();
	}
	
	private void setQueryVector() {
		String word;
		long index;
		int df;
		double tf, idf, weight;
		HashMap<String, Integer> tfMap = getTFmap();
		int maxTF = getMaxTF(tfMap);
		for(Map.Entry<String, Integer> entry : tfMap.entrySet()) {
			word = entry.getKey();
			index = this.vocLoader.getIndex(word);
			df = this.vocLoader.getDF(word);
			idf = getIDF(df);
			tf = (double) tfMap.get(word) / maxTF;
			weight = tf * idf;
			this.queryVector.put(index, weight);
		}
	}
	
	private double getMagProduct(int docid) throws IOException {
		double queryMag = getQueryMagnitude();
		double documentMag = this.docScanner.getDocumentNorm(docid);
		return queryMag * documentMag;
	}
	
	private double getInnerProduct(TreeMap<Long, Double> docVector) {
		double innerProd = 0;
		double vectorWeight, queryWeight;
		long index;
		for(Map.Entry<Long, Double> vectorEntry : docVector.entrySet()) {
			index = vectorEntry.getKey();
			if(!this.queryVector.containsKey(index)) continue;
			vectorWeight = vectorEntry.getValue();
			queryWeight = this.queryVector.get(index);
			innerProd += vectorWeight * queryWeight;
		}
		return innerProd;
	}
	
	private HashMap<String, Integer> getTFmap() {
		HashMap<String, Integer> tfMap = new HashMap<>();
		String[] words = this.query.split(" ");
		for(String word : words) {
			if(!tfMap.containsKey(word)) { tfMap.put(word, 1); }
			else { tfMap.put(word, tfMap.get(word)+1); }
		}
		return tfMap;
	}
	
	private TreeSet<Integer> getUnionOfDocIds() throws IOException {
		TreeSet<Integer> docIDUnion = new TreeSet<Integer>();
		int df, dptr;
		String[] words = this.query.split(" ");
		for(String word : words) {
			df = this.vocLoader.getDF(word);
			dptr = this.vocLoader.getDPTR(word);
			if(df == -1 || dptr == -1) { System.out.println("'" + word + "'" + " is not found in vocabulary :("); System.exit(1);}
			this.pFscanner.setDF(df);
			this.pFscanner.setDPTR(dptr);
			for(int i : this.pFscanner.getRelevantDocIds()) {
				docIDUnion.add(i);
			}
		}
		return docIDUnion;
	}
	
	private int getMaxTF(HashMap<String, Integer> tfMap) {
		int maxTF = 0;
		int tf;
		for(Map.Entry<String, Integer> entry : tfMap.entrySet()) {
			tf = entry.getValue();
			if(tf > maxTF) { maxTF = tf; }
		}
		return maxTF;
	}
	
	private double getIDF(int df) { return Math.log((double) this.docScanner.getNumDocs() / df); }
	
	private double getQueryMagnitude() {
		double mag = 0;
		for(Map.Entry<Long, Double> entry : this.queryVector.entrySet()) { mag += entry.getValue() * entry.getValue(); }
		return Math.sqrt(mag);
	}
}














