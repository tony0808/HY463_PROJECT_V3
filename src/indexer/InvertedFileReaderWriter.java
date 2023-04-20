package indexer;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;

public class InvertedFileReaderWriter {
	
	static final int EQUAL_BLOCKS = 0;
	static final int BLOCKA_LESS = 1;
	static final int BLOCKB_LESS = 2;
	
	public static void writeIndexToDisk(String filename, TreeMap<String, PostingList> block) throws IOException {
		FileWriter fwriter = new FileWriter(new File(filename));
		for(Map.Entry<String, PostingList> index : block.entrySet()) {
			String word = index.getKey();
			fwriter.write(word + " " + index.getValue().getPostingList().size() + "\n");
			TreeMap<Integer, TreeMap<Integer, ArrayList<Integer>>> postingList = index.getValue().getPostingList();
			for(Map.Entry<Integer, TreeMap<Integer, ArrayList<Integer>>> posting : postingList.entrySet()) {
				fwriter.write(posting.getKey() + " " + index.getValue().getTf(posting.getKey()) + " " + posting.getValue().size());
				for(Map.Entry<Integer, ArrayList<Integer>> positions : posting.getValue().entrySet()) {
					fwriter.write(" l " + positions.getKey() + " p " + positions.getValue());
				}
				fwriter.write("\n");
			}
		}
		fwriter.close();
	}
	
	public static int mergeBlocks(String[] blockA, String[] blockB, RandomAccessFile fwriter) throws IOException {
		int result = -1;
		String wordA = getWord(blockA);
		String wordB = getWord(blockB);
		if(wordA.compareTo(wordB) < 0) {
			writeBlockToDisk(fwriter, blockA);
			result = BLOCKA_LESS;
		}
		else if(wordA.compareTo(wordB) > 0) {
			writeBlockToDisk(fwriter, blockB);
			result = BLOCKB_LESS;
 		}
		else {
			mergeDocumentsList(blockA, blockB, fwriter);
			result = EQUAL_BLOCKS;
		}
		return result;
	}
	
	public static void mergeDocumentsList(String[] blockA, String[] blockB, RandomAccessFile fwriter) throws IOException {
		int[] docidAList = getDocIDList(blockA);
		int[] docidBList = getDocIDList(blockB);
		int similarDocIDsCount = countSimilarIntegers(docidAList, docidBList);
		int dfA = getDF(blockA);
		int dfB = getDF(blockB);
		String wordLine = getWord(blockA) + " " + (dfA + dfB - similarDocIDsCount) + "\n";
		fwriter.writeBytes(wordLine);
		int i=0, j=0;
		while((i != docidAList.length) || (j != docidBList.length)) {
			if(i == docidAList.length) {
				fwriter.writeBytes(blockB[j+1] + "\n");
				j++;
			}
			else if(j == docidBList.length) {
				fwriter.writeBytes(blockA[i+1] + "\n");
				i++;
			}
			else {
				if(docidAList[i] < docidBList[j]) {
					fwriter.writeBytes(blockA[i+1] + "\n");
					i++;
				}
				else if(docidAList[i] > docidBList[j]) {
					fwriter.writeBytes(blockB[j+1] + "\n");
					j++;
				}
				else {
					mergeLabelsList(blockA[i+1], blockB[j+1], fwriter);
					i++;
					j++;
				}
			}
		}
	}
	
	public static void mergeLabelsList(String docEntryA, String docEntryB, RandomAccessFile fwriter) throws IOException {
		String[] docBlockA = getDocumentBlock(docEntryA);
		String[] docBlockB = getDocumentBlock(docEntryB);
		int[] labelListIDA = getLabelIDList(docBlockA);
		int[] labelListIDB = getLabelIDList(docBlockB);
		int tfA = getTF(docBlockA);
		int tfB = getTF(docBlockB);
		int labelListsLen = labelListIDA.length + labelListIDB.length;
		String documentLine = getDocID(docEntryA) + " " + (tfA + tfB) + " " + labelListsLen;
		fwriter.writeBytes(documentLine);
		int i=0, j=0;
		while((i != labelListIDA.length) || (j != labelListIDB.length)) {
			if(i == labelListIDA.length) {
				fwriter.writeBytes(" " + docBlockB[j+1]);
				j++;
			}
			else if(j == labelListIDB.length) {
				fwriter.writeBytes(" " + docBlockA[i+1]);
				i++;
			}
			else {
				if(labelListIDA[i] < labelListIDB[j]) {
					fwriter.writeBytes(" " + docBlockA[i+1]);
					i++;
				}
				else if(labelListIDA[i] > labelListIDB[j]) {
					fwriter.writeBytes(" " + docBlockB[j+1]);
					j++;
				}
				else {
					print("code definetely should not be here.");
					System.exit(2);
				}
			}
		}
		fwriter.write('\n');
	}
	
	public static void writeBlockToDisk(RandomAccessFile fwriter, String[] block) throws IOException {
		for(String str : block) {
			fwriter.writeBytes(str + "\n");
		}
	}
	
	public static void printBlock(String[] block) {
		for(String str : block) print(str);
	}
	
	public static String[] getDocumentBlock(String docentry) {
		StringBuilder sb = new StringBuilder();
		for(char c : docentry.toCharArray()) {
			if(c == 'l') {
				sb.append("\n");
			}
			sb.append(c);
		}
		String[] docBlock = sb.toString().split("\n");
		for(int i=0; i<docBlock.length; i++) {
			docBlock[i] = docBlock[i].trim();
		}
		return docBlock;
	}
	
	public static String[] getWordBlock(RandomAccessFile freader) throws IOException {
		StringBuilder sb = new StringBuilder();
		String line = freader.readLine();
		if(line == null) return null;
		sb.append(line).append("\n");
		int df = Integer.parseInt(line.split(" ")[1]);
		for(int i=0; i<df; i++) {
			line = freader.readLine();
			sb.append(line).append("\n");
		}
		return sb.toString().split("\n");
	}
	
	public static boolean isWordLine(String line) {
		return (line.charAt(0) == 'w') ? true : false;
	}
	
	public static int countSimilarIntegers(int[] listA, int[] listB) {
		int i=0, j=0;
		int count=0;
		while(i < listA.length && j < listB.length) {
			if(listA[i] == listB[j]) {
				count++;
				i++;
				j++;
			}
			else if(listA[i] < listB[j]) {
				i++;
			}
			else {
				j++;
			}
		}
		return count;
	}
	
	public static int[] getLabelIDList(String[] block) {
		int[] labelIDList = new int[block.length - 1];
		for(int i=1; i<block.length; i++) {
			labelIDList[i-1] = getLabelID(block[i]);
		}
		return labelIDList;
	}
	
	public static int[] getDocIDList(String[] block) {
		int[] docIDList = new int[block.length - 1];
		for(int i=1; i<block.length; i++) {
			docIDList[i-1] = getDocID(block[i]);
		}
		return docIDList;
	}
	
	public static int[] getTFList(String[] block) {
		int[] tfList = new int[block.length - 1];
		for(int i=1; i<block.length; i++) {
			tfList[i-1] = getTFfromDocumentEntry(block[i]);
		}
		return tfList;
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
	
	public static String getWord(String[] block) {
		return block[0].split(" ")[0];
	}
	
	public static int getLabelID(String labelEntry) {
		return Integer.parseInt(labelEntry.split(" ")[1]);
	}
	
	public static int getDocID(String line) {
		int i=0;
		while(i < line.length() && line.charAt(i) != ' ') i++;
		return Integer.parseInt(line.substring(0, i));
	}
	
	public static int getDF(String[] block) {
		return Integer.parseInt(block[0].split(" ")[1]);
	}
	
	public static int getTFfromDocumentEntry(String docEntry) {
		return Integer.parseInt(docEntry.split(" ")[1]);
	}
	
	public static int getTF(String[] block) {
		return Integer.parseInt(block[0].split(" ")[1]);
	}
	
	public static void print(Object obj) {
		System.out.println(obj);
	}
}










