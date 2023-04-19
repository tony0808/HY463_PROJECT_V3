package main;

import java.io.IOException;
import java.util.concurrent.ExecutionException;
import indexer.IndexBuilder;

public class Tester {
	public static void main(String[] args) throws IOException, InterruptedException, ExecutionException {
		IndexBuilder ibuilder = new IndexBuilder("C:\\MiniCollection", "C:\\CollectionIndex");
		ibuilder.buildIndex();
	}
}












