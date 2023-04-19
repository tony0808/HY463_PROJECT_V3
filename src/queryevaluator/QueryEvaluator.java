package queryevaluator;

import java.io.IOException;
import java.util.HashMap;

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
	
	public void printRelevantDocuments() throws IOException {
		int df = this.vocLoader.getDF(query);
		int dptr = this.vocLoader.getDPTR(query);
		if(df == -1 || dptr == -1) { System.out.println("Query is not found in vocabulary :("); System.exit(1);}
		this.pFscanner.setDF(df);
		this.pFscanner.setDPTR(dptr);
		this.pFscanner.initRelevantDocumentsBlock();
		Integer[] docIds = pFscanner.getRelevantDocumentIds();
		this.docScanner.setDocIds(docIds);
		this.docScanner.initRelevantDocumentsBlock();
		String[] docNames = this.docScanner.getRelevantDocuments();
		for(String relDoc : docNames) {
			System.out.println(relDoc);
		}
	}
}














