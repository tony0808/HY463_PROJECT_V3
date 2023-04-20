package queryevaluator;

import java.io.IOException;
import java.util.HashSet;
import java.util.TreeSet;

public class QueryEvaluator {
	
	private String query;
	private VocabularyLoader vocLoader;
	private PostingFileScanner pFscanner;
	private DocumentsFileScanner docScanner;
	
	public QueryEvaluator(String query, String parentDirectory) throws IOException { 
		this.query = query;
		this.vocLoader = new VocabularyLoader(parentDirectory);
		this.pFscanner = new PostingFileScanner(parentDirectory);
		this.docScanner = new DocumentsFileScanner(parentDirectory);
	}
	
	public void setQuery(String query) { this.query = query; }
	
	public TreeSet<Integer> getUnionOfDocIds() throws IOException {
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
	
	public void printRelevantDocuments() throws IOException {
		int df = this.vocLoader.getDF(query);
		int dptr = this.vocLoader.getDPTR(query);
		if(df == -1 || dptr == -1) { System.out.println("Query is not found in vocabulary :("); System.exit(1);}
		this.pFscanner.setDF(df);
		this.pFscanner.setDPTR(dptr);
		int[] docIds = pFscanner.getRelevantDocIds();
		this.docScanner.setDocIds(docIds);
		this.docScanner.buildRelevantDocumentsBlock();
		String[] docNames = this.docScanner.getRelevantDocuments();
		for(int relDoc : docIds) {
			System.out.println(relDoc);
		}
	}
}














