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
		QueryEvaluator qeval = new QueryEvaluator("abbrevi", parentDirectory);
		RandomAccessFile freader = new RandomAccessFile(parentDirectory + "\\" + IndexBuilder.DOCVECTORFILENAME, "r");
		String teststr = "1 0,1.0 1,1.1 2,1.2 3,1.3";
		TreeMap<Long, Double> vec = parse(teststr);
		printMap(vec);
	}
	
	public static void printMap(TreeMap<Long, Double> map) {
		for(Map.Entry<Long, Double> entry : map.entrySet()) {
			System.out.println("(" + entry.getKey() + ", " + entry.getValue() + ")");
		}
	}
	
	public static TreeMap<Long, Double> parse(String line) {
		TreeMap<Long, Double> vector = new TreeMap<Long, Double>();
		String[] parsedLine = line.split(" ");
		String[] vectorEntries;
		long index;
		double weight;
		for(int i=1; i<parsedLine.length; i++) {
			vectorEntries = parsedLine[i].split(",");
			index = Long.parseLong(vectorEntries[0]);
			weight = Double.parseDouble(vectorEntries[1]);
			vector.put(index, weight);
		}
		return vector;
	}
	
	public static int test(String str) {
		String docid = "";
		for(char c : str.toCharArray()) {
			if(c == ' ') break;
			docid += c;
		}
		return Integer.parseInt(docid);
	}
	
	public static void print(Object obj) {
		System.out.println(obj);
	}
}
