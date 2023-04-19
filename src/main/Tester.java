package main;

import java.io.IOException;
import java.util.concurrent.ExecutionException;
import indexer.IndexBuilder;
import queryevaluator.PostingFileScanner;
import queryevaluator.VocabularyLoader;

public class Tester {
	public static void main(String[] args) throws IOException, InterruptedException, ExecutionException {
//		IndexBuilder ibuilder = new IndexBuilder("C:\\MiniCollection", "C:\\CollectionIndex");
//		ibuilder.buildIndex();
		
		VocabularyLoader vload = new VocabularyLoader("C:\\CollectionIndex");
		int df = vload.getDF("what");
		int dptr = vload.getDPTR("what");
		PostingFileScanner pFscanner = new PostingFileScanner("C:\\CollectionIndex", df, dptr);
		pFscanner.initRelevantDocumentsBlock();
		pFscanner.printDocumentBlock();
	}
	
	public static void print(Object obj) {
		System.out.println(obj);
	}
}












