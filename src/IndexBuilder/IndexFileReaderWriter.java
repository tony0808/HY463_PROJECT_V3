package indexbuilder;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

public class IndexFileReaderWriter {
	
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
	}
	
	private static int mergeTwoBlocks(String[] blockA, String[] blockB, RandomAccessFile fwriter) throws IOException {
		String wordA = getBlockWord(blockA);
		String wordB = getBlockWord(blockB);
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
			int mergedDocFreq = getDocFreq(blockA) + getDocFreq(blockB);
			String mergedString = "w " + wordA + " " + mergedDocFreq + '\n';
			fwriter.writeBytes(mergedString);
			mergeDocumentLists(blockA, blockB, fwriter);
			return 0;
		}
	}
	
	private static void writeBlockToDisk(String[] block, RandomAccessFile fwriter) throws IOException {
		for(String str : block) fwriter.writeBytes(str + '\n');
	}
	
	private static void mergeDocumentLists(String[] blockA, String[] blockB, RandomAccessFile fwriter) throws IOException {
		Integer[] docListA = getDocIndexes(blockA);
		Integer[] docListB = getDocIndexes(blockB);	
		int i = 0;
		int j = 0;
		while((i!=docListA.length) || (j!=docListB.length)) {
			if(i == docListA.length) {
				writeDocumentEntryToDisk(blockB, docListB[j], getLabelFreq(blockB, docListB[j]), fwriter);
				j++;
			}
			else if(j == docListB.length) {
				writeDocumentEntryToDisk(blockA, docListA[i], getLabelFreq(blockA, docListA[i]), fwriter);
				i++;
			}
			else if(getDocId(blockA, docListA[i]) < getDocId(blockB, docListB[j])) {
				writeDocumentEntryToDisk(blockA, docListA[i], getLabelFreq(blockA, docListA[i]), fwriter);
				i++;
			}
			else if(getDocId(blockA, docListA[i]) > getDocId(blockB, docListB[j])) {
				writeDocumentEntryToDisk(blockB, docListB[j], getLabelFreq(blockB, docListB[j]), fwriter);
				j++;
			}
			else {
				// merge labels list
				int mergedLabelFreq = getLabelFreq(blockA, docListA[i]) + getLabelFreq(blockB, docListB[j]);
				int mergedTermFreq = getTermFreq(blockA, docListA[i]) + getTermFreq(blockB, docListB[j]);
				String mergedString = "d " + getDocId(blockA, docListA[i]) + " " + mergedTermFreq + " " + mergedLabelFreq + '\n';
				fwriter.writeBytes(mergedString);
				mergeLabelsList(blockA, docListA[i], blockB, docListB[j], fwriter);
				i++;
				j++;
			}
		}
	}
	
	private static void mergeLabelsList(String[] blockA, int docIndexA, String blockB[], int docIndexB, RandomAccessFile fwriter) throws IOException {
		Integer[] labelListA = getLabelIndexes(blockA, docIndexA);
		Integer[] labelListB = getLabelIndexes(blockB, docIndexB);
		int i=0;
		int j=0;
		while((i!=labelListA.length) || (j!=labelListB.length)) {
			if(i == labelListA.length) {
				writeLabelEntryToDisk(blockB, labelListB[j], fwriter);
				j++;
			}
			else if(j == labelListB.length) {
				writeLabelEntryToDisk(blockA, labelListA[i], fwriter);
				i++;
			}
			else if(getLabelid(blockA, labelListA[i]) < getLabelid(blockB, labelListB[j])) {
				writeLabelEntryToDisk(blockA, labelListA[i], fwriter);
				i++;
			}
			else if(getLabelid(blockA, labelListA[i]) > getLabelid(blockB, labelListB[j])) {
				writeLabelEntryToDisk(blockB, labelListB[j], fwriter);
				j++;
			}
			else {
				System.out.println("should not be here. big error");
				System.exit(1);
			}
		}
	}
	
	private static void writeLabelEntryToDisk(String[] block, int index, RandomAccessFile fwriter) throws IOException {
		fwriter.writeBytes(block[index] + '\n');
		fwriter.writeBytes(block[index+1] + '\n');
	}
	
	private static void writeDocumentEntryToDisk(String[] block, int index, int lf, RandomAccessFile fwriter) throws IOException {
		fwriter.writeBytes(block[index] + '\n');
		for(int i=1; i<=lf*2; i++) {
			fwriter.writeBytes(block[index+i] + '\n');
		}
	}
		
	private static Integer[] getLabelIndexes(String[] block, int docIndex) {
		int lf = getLabelFreq(block, docIndex);
		Integer[] arr = new Integer[lf];
		for(int i=0; i<lf; i++) {
			arr[i] = docIndex + 1 + 2*i;
		}
		return arr;
	}
	
	private static Integer[] getDocIndexes(String[] block) {
		int index = 1;
		int df = getDocFreq(block);
		Integer[] indexes = new Integer[df];
		for(int i=0; i<df; i++) { 
			indexes[i] = index;
			index += getLabelFreq(block, index) * 2 + 1; 
		}
		return indexes;
	}
	
	private static int getLabelid(String[] block, int index) {
		String label = block[index];
		return Integer.parseInt(label.split(" ")[1]);
	}
	
	private static int getLabelFreq(String[] block, int index) {
		return Integer.parseInt(block[index].split(" ")[3]);
	}
	
	private static int getTermFreq(String[] block, int index) {
		return Integer.parseInt(block[index].split(" ")[2]);
	}
	
	private static int getDocId(String[] block, int index) {
		return Integer.parseInt(block[index].split(" ")[1]);
	}
	
	private static int getDocFreq(String[] block) {
		if(block == null) return -1;
		return Integer.parseInt(block[0].split(" ")[2]);
	}
	
	private static String getBlockWord(String[] block) {
		if(block == null) return null;
		return block[0].split(" ")[1];
	}
	
	private static String[] getBlock(RandomAccessFile freader) throws IOException {
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
