package indexer;

import java.io.IOException;

public class Tester {
	
	public static void main(String[] args) throws NumberFormatException, IOException {
		String targetDirectory = "C:\\CollectionIndex";
		String coprusDirectory = "C:\\MiniCollection";
		IndexBuilder ibuilder = new IndexBuilder(coprusDirectory, targetDirectory);
		ibuilder.buildIndex();
//		String fileA = "C:\\CollectionIndex\\testA.txt";
//		String fileB = "C:\\CollectionIndex\\testB.txt";
//		String fileC = "C:\\CollectionIndex\\testOut.txt";
//		InvertedFileReaderWriter.mergeTwoPartialIndexes(fileA, fileB, fileC);
	
//		String[] block = parseDocEntry("1 1 3 l 4 p [1, 2, 3, 4, 5] l 4 p [1, 2, 3, 4, 5] l 4 p [1, 2, 3, 4, 5] l 4 p [1, 2, 3, 4, 5] l 5 p[1, 123123] l 612312 p[1]");
//		int[] arr = getLabelIDList(block);
//		printBlock(block);
//		for(int i : arr) print(i);
	}
	
	public static int[] getLabelIDList(String[] block) {
		int[] labelIDList = new int[block.length - 1];
		for(int i=1; i<block.length; i++) {
			labelIDList[i-1] = getLabelID(block[i]);
		}
		return labelIDList;
	}
	
	public static int getLabelID(String labelEntry) {
		return Integer.parseInt(labelEntry.split(" ")[1]);
	}
	
	public static void printBlock(String[] arr) {
		for(String str : arr) print(str);
	}
	
	public static String[] parseDocEntry(String docentry) {
		StringBuilder sb = new StringBuilder();
		for(char c : docentry.toCharArray()) {
			if(c == 'l') {
				sb.append("\n");
			}
			sb.append(c);
		}
		return sb.toString().split("\n");
	}
	
	public static int getLF(String docentry) {
		int i=0;
		int start=0, end=0;
		int countWhiteSpace = 0;
		while(i < docentry.length()) {
			if(countWhiteSpace == 2 && start == 0) {
				start=i;
			}
			if(countWhiteSpace == 3) {
				end=i-1;
				break;
			}
			if(docentry.charAt(i) == ' ') {
				countWhiteSpace++;
			}
			i++;
		}
		return Integer.parseInt(docentry.substring(start, end));
	}
	
	
	public static void print(Object obj) {
		System.out.println(obj);
	}
}
