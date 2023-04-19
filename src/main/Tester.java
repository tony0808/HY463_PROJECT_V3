package main;

import java.io.IOException;
import java.util.concurrent.ExecutionException;
import indexer.IndexBuilder;
import queryevaluator.DocumentsFileScanner;
import queryevaluator.PostingFileScanner;
import queryevaluator.QueryEvaluator;
import queryevaluator.VocabularyLoader;

public class Tester {
	public static void main(String[] args) throws IOException, InterruptedException, ExecutionException {
//		IndexBuilder ibuilder = new IndexBuilder("C:\\MiniCollection", "C:\\CollectionIndex");
//		ibuilder.buildIndex();	
		QueryEvaluator queryEval = new QueryEvaluator("abstract", "C:\\CollectionIndex");
		queryEval.printRelevantDocuments();
	}
	
	public static void print(Object obj) {
		System.out.println(obj);
	}
}












