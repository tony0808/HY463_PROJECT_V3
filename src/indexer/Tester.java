package indexer;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Tester {
	
	public static void main(String[] args) throws NumberFormatException, IOException {
//		String targetDirectory = "C:\\CollectionIndex";
//		String coprusDirectory = "C:\\MiniCollection";
//		IndexBuilder ibuilder = new IndexBuilder(coprusDirectory, targetDirectory);
//		ibuilder.buildIndex();
		String fileA = "C:\\CollectionIndex\\testA.txt";
//		String fileB = "C:\\CollectionIndex\\testB.txt";
//		String fileC = "C:\\CollectionIndex\\testOut.txt";
//		InvertedFileReaderWriter.mergeTwoPartialIndexes(fileA, fileB, fileC);
		RandomAccessFile freader = new RandomAccessFile(fileA, "r");
		HashMap<Integer, ArrayList<Integer>> testVec = buildDocumentVectorMap(freader);
		print(testVec);
		printMap(testVec);
	}
	
	public static void printMap(HashMap<Integer, ArrayList<Integer>> map) {
		for(Map.Entry<Integer, ArrayList<Integer>> entry : map.entrySet()) {
			int docid = entry.getKey();
			ArrayList<Integer> lst = entry.getValue();
			print(docid);
			for(int i=0; i<lst.size(); i++) System.out.print(lst.get(i) + " ");
			print(" ");
		}
	}
	
	public static HashMap<Integer, ArrayList<Integer>> buildDocumentVectorMap(RandomAccessFile freader) throws IOException {
		HashMap<Integer, ArrayList<Integer>> documentVectorMap = new HashMap<>();
		ArrayList<Integer> docVector;
		String[] block;
		int[] docIds;
		int docid;
		while((block = InvertedFileReaderWriter.getWordBlock(freader)) != null) {
			docIds = InvertedFileReaderWriter.getDocIDList(block);
			for(int i=0; i<docIds.length; i++) {
				print("id : " + docIds[i]);
				docid = docIds[i];
				docVector = documentVectorMap.get(docid);
				if(docVector == null) {
					docVector = new ArrayList<Integer>();
				}
				docVector.add(InvertedFileReaderWriter.getTFfromDocumentEntry(block[i+1]));
				documentVectorMap.put(docid, docVector);
			}
		}
		return documentVectorMap;
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
