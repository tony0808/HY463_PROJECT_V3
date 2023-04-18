package indexbuilder;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

public class InvertedFileReaderWriter {
	
	public static void writeIndexToDisk(String filename, TreeMap<String, PostingList> block) throws IOException {
		FileWriter fwriter = new FileWriter(new File(filename));
		for(Map.Entry<String, PostingList> index : block.entrySet()) {
			String word = index.getKey();
			fwriter.write("w " + word + " " + index.getValue().getPostingList().size() + "\n");
			HashMap<Integer, HashMap<Integer, ArrayList<Integer>>> postingList = index.getValue().getPostingList();
			for(Map.Entry<Integer, HashMap<Integer, ArrayList<Integer>>> posting : postingList.entrySet()) {
				fwriter.write("d " + posting.getKey() + " " + index.getValue().getTf(posting.getKey()) + " " + posting.getValue().size() + "\n");
				for(Map.Entry<Integer, ArrayList<Integer>> positions : posting.getValue().entrySet()) {
					fwriter.write("l " + positions.getKey() + "\n");
					fwriter.write("p " + positions.getValue() + "\n");
				}
			}
		}
		fwriter.close();
	}
	
	public static void mergeTwoPartialIndexes(String fileInA, String fileInB, String fileOut) throws IOException {
		RandomAccessFile freaderA = new RandomAccessFile(fileInA, "r");
		RandomAccessFile freaderB = new RandomAccessFile(fileInB, "r");
		RandomAccessFile fwriter = new RandomAccessFile(fileOut, "rw");
		
		String[] blockA = getBlock(freaderA);
		String[] blockB = getBlock(freaderB);
		
		while((blockA != null) || (blockB != null)) {
			if(blockA == null) {
				writeBlockToDisk(blockB, fwriter);
				blockB = getBlock(freaderB);
			}
			else if(blockB == null) {
				writeBlockToDisk(blockA, fwriter);
				blockA = getBlock(freaderA);
			}
			else {
				int res = mergeTwoBlocks(blockA, blockB, fwriter);
				if(res < 0) {
					blockA = getBlock(freaderA);
				}
				else if(res > 0) {
					blockB = getBlock(freaderB);
				}
				else {
					blockA = getBlock(freaderA);
					blockB = getBlock(freaderB);
				}
			}
		}
		freaderA.close();
		freaderB.close();
		fwriter.close();
	}
	
	private static int mergeTwoBlocks(String[] blockA, String[] blockB, RandomAccessFile fwriter) throws IOException {
		String wordA = getWord(blockA);
		String wordB = getWord(blockB);
		if(wordA.compareTo(wordB) < 0) {
			writeBlockToDisk(blockA, fwriter);
			return -1;
		}
		else if(wordA.compareTo(wordB) > 0) {
			writeBlockToDisk(blockB, fwriter);
			return 1;
		}
		else {
			// merge document lists 
			int mergedDocFreq = getDF(blockA) + getDF(blockB) - getNumberOfSameDocIds(blockA, blockB);
			String mergedString = "w " + wordA + " " + mergedDocFreq + '\n';
			fwriter.writeBytes(mergedString);
			mergeDocumentLists(blockA, blockB, fwriter);
			return 0;
		}
	}
	
	private static int getNumberOfSameDocIds(String[] blockA, String[] blockB) {
		ArrayList<Integer> docIndexesA = getDocIndexes(blockA);
		ArrayList<Integer> docIndexesB = getDocIndexes(blockB);
		Integer[] docIdsA = new Integer[docIndexesA.size()];
		Integer[] docIdsB = new Integer[docIndexesB.size()];
		for(int i=0; i<docIndexesA.size(); i++) { docIdsA[i] = getDocId(blockA, docIndexesA.get(i)); }
		for(int i=0; i<docIndexesB.size(); i++) { docIdsB[i] = getDocId(blockB, docIndexesB.get(i)); }
		int count = 0, i = 0, j = 0;
		while(i < docIdsA.length && j < docIdsB.length) {
			if(docIdsA[i] == docIdsB[j]) { count++; i++; j++; }
			else if(docIdsA[i] < docIdsB[j]) { i++; }
			else { j++; }
		}
		return count;
	}
	
	private static void writeBlockToDisk(String[] block, RandomAccessFile fwriter) throws IOException {
		for(String str : block) fwriter.writeBytes(str + '\n');
	}
	
	private static void mergeDocumentLists(String[] blockA, String[] blockB, RandomAccessFile fwriter) throws IOException {
		ArrayList<Integer> docListA = getDocIndexes(blockA);
		ArrayList<Integer> docListB = getDocIndexes(blockB);	
		int i = 0;
		int j = 0;
		while((i!=docListA.size()) || (j!=docListB.size())) {
			if(i == docListA.size()) {
				writeDocumentEntryToDisk(blockB, docListB.get(j), fwriter);
				j++;
			}
			else if(j == docListB.size()) {
				writeDocumentEntryToDisk(blockA, docListA.get(i), fwriter);
				i++;
			}
			else {
				int docIdA = getDocId(blockA, docListA.get(i));
				int docIdB = getDocId(blockB, docListB.get(j));
				if(docIdA < docIdB) {
					writeDocumentEntryToDisk(blockA, docListA.get(i), fwriter);
					i++;
				}
				else if(docIdA > docIdB) {
					writeDocumentEntryToDisk(blockB, docListB.get(j), fwriter);
					j++;
				}
				else {
					// merge labels list
	 				int mergedLabelFreq = getLF(blockA, docListA.get(i)) + getLF(blockB, docListB.get(j));
					int mergedTermFreq = getTF(blockA, docListA.get(i)) + getTF(blockB, docListB.get(j));
					String mergedString = "d " + docIdA + " " + mergedTermFreq + " " + mergedLabelFreq + '\n';
					fwriter.writeBytes(mergedString);
					mergeLabelsList(blockA, blockB, docListA.get(i), docListB.get(j), fwriter);
					i++;
					j++;
				}
			}
			
		}
	}
	
	private static void mergeLabelsList(String[] blockA, String blockB[], int docIndexA, int docIndexB, RandomAccessFile fwriter) throws IOException {
		ArrayList<Integer >labelListA = getLabelIndexes(blockA, docIndexA);
		ArrayList<Integer> labelListB = getLabelIndexes(blockB, docIndexB);
		int i=0;
		int j=0;
		while((i!=labelListA.size()) || (j!=labelListB.size())) {
			if(i == labelListA.size()) {
				writeLabelEntryToDisk(blockB, labelListB.get(j), fwriter);
				j++;
			}
			else if(j == labelListB.size()) {
				writeLabelEntryToDisk(blockA, labelListA.get(i), fwriter);
				i++;
			}
			else {
				int labelidA = getLabelid(blockA, labelListA.get(i));
				int labelidB = getLabelid(blockB, labelListB.get(j));
				if(labelidA < labelidB) {
					writeLabelEntryToDisk(blockA, labelListA.get(i), fwriter);
					i++;
				}
				else if(labelidA > labelidB) {
					writeLabelEntryToDisk(blockB, labelListB.get(j), fwriter);
					j++;
				}
				else {
					System.out.println("should not be here. big error");
					System.exit(1);
				}
			}
		}
	}
	
	private static void writeLabelEntryToDisk(String[] block, int index, RandomAccessFile fwriter) throws IOException {
		fwriter.writeBytes(block[index] + '\n');
		fwriter.writeBytes(block[index+1] + '\n');
	}
	
	private static void writeDocumentEntryToDisk(String[] block, int index, RandomAccessFile fwriter) throws IOException {
		int lf = getLF(block, index);
		fwriter.writeBytes(block[index] + '\n');
		for(int i=1; i<=lf*2; i++) {
			fwriter.writeBytes(block[index+i] + '\n');
		}
	}
		
	private static ArrayList<Integer> getLabelIndexes(String[] block, int docIndex) {
		ArrayList<Integer> labelIndexes = new ArrayList<Integer>();
		int lf = getLF(block, docIndex);
		int count = 0;
		for(int i=docIndex; i<block.length; i++) {
			if(count == lf) break;
			if(block[i].charAt(0) == 'l') {
				count += 1;
				labelIndexes.add(i);
			}
		}
		return labelIndexes;
	}
	
	private static ArrayList<Integer> getDocIndexes(String[] block) {
		ArrayList<Integer> docIndexes = new ArrayList<Integer>();
		for(int i=0; i<block.length; i++) {
			String line = block[i];
			if(line.charAt(0) == 'd') {
				docIndexes.add(i);
			}
		}
		return docIndexes;
	}
	
	private static int getLabelid(String[] block, int index) {
		String label = block[index];
		return Integer.parseInt(label.split(" ")[1]);
	}
	
	private static int getLF(String[] block, int index) {
		return Integer.parseInt(block[index].split(" ")[3]);
	}
	
	private static int getTF(String[] block, int index) {
		return Integer.parseInt(block[index].split(" ")[2]);
	}
	
	private static int getDocId(String[] block, int index) {
		return Integer.parseInt(block[index].split(" ")[1]);
	}
	
	private static int getDF(String[] block) {
		return Integer.parseInt(block[0].split(" ")[2]);
	}
	
	private static String getWord(String[] block) {
		return block[0].split(" ")[1];
	}
	
	static String[] getBlock(RandomAccessFile freader) throws IOException {
		String line;
		StringBuilder sb = new StringBuilder();
		boolean blockIsRead = false;
		long prevFilePointer = 0;
		while((line = freader.readLine()) != null) {
			if(line.charAt(0) == 'w') {
				if(blockIsRead) { freader.seek(prevFilePointer); break; }
				else { sb.append(line).append("\n"); blockIsRead = true; }
			}
			else { sb.append(line).append("\n"); }
			prevFilePointer = freader.getFilePointer();
		}
		if (!blockIsRead && sb.length() == 0) { return null; }
		return sb.toString().split("\n");
	}
}
