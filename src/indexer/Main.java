package indexer;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

public class Main {
	
	public static void main(String[] args) throws UnsupportedEncodingException, IOException {
		if(args.length != 2) {
			System.out.println("You need give two arguements:");
			System.out.println("1.Path to the directory that contains the documents.");
			System.out.println("2.Path to the directory that the generated files will be saved..!");
			System.exit(1);
		}
		System.out.println("corpus path : " + args[0]);
		System.out.println("target directory : " + args[1]);
		String coprusDirectory = args[0];
		String targetDirectory = args[1];
		long startTime = System.currentTimeMillis();
		IndexBuilder ibuilder = new IndexBuilder(coprusDirectory, targetDirectory);
		ibuilder.buildIndex();
		long endTime = System.currentTimeMillis();
		double time = (endTime - startTime) / 1000.0;
		System.out.println("Time taken to create index files : " + time);
	}
}
