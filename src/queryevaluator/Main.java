package queryevaluator;

import java.io.IOException;

import java.util.Scanner;

public class Main {
	
	public static void main(String[] args) throws IOException {
		if(args.length != 1) {
			System.out.println("You must provide the full path of the directory that contains the files created by the indexer.");
			System.exit(1);
		}
		Scanner scanner = new Scanner(System.in);
		String parentDirectory = args[0];
		QueryEvaluator qeval = new QueryEvaluator(parentDirectory);
		String query;
		String type;
		System.out.println("Enter the type : ");
		type = scanner.nextLine();
		System.out.println("Enter the synopsis : ");
		query = scanner.nextLine();
		qeval.setQuery(query);
		qeval.evaluateQuery();
		scanner.close();
	}
}
