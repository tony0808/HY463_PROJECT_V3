package indexer;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

public class Main {
	
	public static void main(String[] args) throws UnsupportedEncodingException, IOException {
		if(args.length != 2) {
			System.out.println("You need give two arguements:");
			System.out.println("1.Path to the directory that contains the documents.");
			System.out.println("2.Path to the directory that the generated files will be saved.");
			System.exit(1);
		}
		IndexBuilder ibuilder = new IndexBuilder(args[0], args[1]);
		ibuilder.buildIndex();
	}
}
