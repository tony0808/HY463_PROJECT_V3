package queryevaluator;

import java.io.IOException;

public class Main {
	
	public static void main(String[] args) throws IOException {
		String parentDirectory = "C:\\CollectionIndex";
		QueryEvaluator qeval = new QueryEvaluator("accuraci", parentDirectory);
		qeval.printRelevantDocuments();
	}
	
	public static void print(Object obj) {
		System.out.println(obj);
	}
}
