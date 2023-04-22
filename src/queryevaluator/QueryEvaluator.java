package queryevaluator;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;

public class QueryEvaluator {
	
	private String query;
	private String parentDirectory;
	private HashMap<Long, Double> queryVector;
	private HashMap<String, VocabData> vocabulary;
	private DocumentVectorFileScanner docVectorFileScanner;
	private QueryProcessor queryProcessor;
	private PostingFileScanner pFscanner;
	private Vectorizer vectorizer;
	private Scorer scorer;
	
	public QueryEvaluator(String parentDirectory) throws IOException {
		this.vocabulary = (new VocabularyLoader(parentDirectory)).getVocabulary();
		this.docVectorFileScanner = new DocumentVectorFileScanner(parentDirectory);
		this.pFscanner = new PostingFileScanner(parentDirectory);
		this.queryProcessor = new QueryProcessor(this.vocabulary);
		this.vectorizer = new Vectorizer(this.vocabulary);
		this.parentDirectory = parentDirectory;
		this.queryVector = new HashMap<>();
		this.scorer = new Scorer(parentDirectory);
	}
	
	public void setQuery(String query) { this.query = query; }
	
	public void evaluateQuery() throws IOException {	
		processQuery();
		setQueryVector();
		HashSet<Integer> docIds = getUnionOfRelevantDocumentsIds(this.query.split(" "));
		for(Integer docid : docIds) { calculateDocumentScore(docid); }
		this.scorer.printScores();
	}
	
	private void calculateDocumentScore(int docid) throws IOException {
		HashMap<Long, Double> documentVector = this.docVectorFileScanner.getDocumentVector(docid);
		double cosSim = this.scorer.calculateScore(this.queryVector, documentVector, docid);
		this.scorer.addScore(cosSim, docid);
	}
	
	private void setQueryVector() throws IOException {
		this.vectorizer.setNumDocs((new DocumentFileScanner(this.parentDirectory)).getSize());
		this.vectorizer.setWords(this.query.split(" "));
		this.queryVector = this.vectorizer.getVector();
	}
	
	private void processQuery() {
		queryProcessor.setQuery(query);
		query = queryProcessor.getProcessedQuery();
		if(query.length() == 0) { System.out.println("Your query after processing is empty. Provide more information."); System.exit(1); }
	}
	
	private HashSet<Integer> getUnionOfRelevantDocumentsIds(String[] queryWords) throws IOException {
		HashSet<Integer> docIdsSet = new HashSet<>();
		VocabData vdata;
		int df;
		int dptr;
		for(String query : queryWords) {
			vdata = this.vocabulary.get(query);
			df = vdata.getDF();
			dptr = vdata.getDPTR();
			this.pFscanner.setDF(df);
			this.pFscanner.setDPTR(dptr);
			for(int i : this.pFscanner.getRelevantDocIds()) docIdsSet.add(i);
		}
		return docIdsSet;
	}
}










