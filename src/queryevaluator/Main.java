package queryevaluator;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.HashSet;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;

import indexer.IndexBuilder;

public class Main {
	
	public static void main(String[] args) throws IOException {
		String parentDirectory = "C:\\CollectionIndex";
		QueryEvaluator qeval = new QueryEvaluator("ha ha haasdad", parentDirectory);
		qeval.setQueryScores();
	}
	
	public static void print(Object obj) {
		System.out.println(obj);
	}
}
