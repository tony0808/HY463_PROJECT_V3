package main;

import java.io.IOException;
import java.io.RandomAccessFile;

import indexbuilder.IndexBuilder;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class Tester {
	// 4577, 4532, 4262, 3641
	public static void main(String[] args) throws IOException, InterruptedException, ExecutionException {
		String corpusPath = "C:\\SmallCollection";
		String targetDirectory = "C:\\CollectionIndex";
		long ts = System.currentTimeMillis();
		IndexBuilder ibuilder = new IndexBuilder(corpusPath, targetDirectory);
		ibuilder.createIndex();
		long tf = System.currentTimeMillis();
		double time = (tf - ts) / 1000.0;
		System.out.println(time);
		//mergeTwoPartialIndexes("C:\\CollectionIndex\\1.txt", "C:\\CollectionIndex\\2.txt", "C:\\CollectionIndex\\out.txt");
	}
	
	public static void mergeTwoPartialIndexes(String fileA, String fileB, String fileOut) throws IOException {
		RandomAccessFile freaderA = new RandomAccessFile(fileA, "r");
		RandomAccessFile freaderB = new RandomAccessFile(fileB, "r");
		RandomAccessFile fwriterOut = new RandomAccessFile(fileOut, "rw");
		
		
		String postingListA = getNextWordPostingList(freaderA);
		String postingListB = getNextWordPostingList(freaderB);
		
		String wordA = getWordFromPostingList(postingListA);
		String wordB = getWordFromPostingList(postingListB);
		
		for(int i=0; i<3; i++) {
			if(wordA.compareTo(wordB) > 0) {
				fwriterOut.writeBytes(postingListB);
				postingListB = getNextWordPostingList(freaderB);
				wordB = getWordFromPostingList(postingListB);
			}
			else if(wordA.compareTo(wordB) < 0) {
				fwriterOut.writeBytes(postingListA);
				postingListA = getNextWordPostingList(freaderA);
				wordA = getWordFromPostingList(postingListA);
			}
			else {
				
				String[] documentListA = getDocumentList(postingListA);
				// String[] documentListB = getDocumentList(postingListB);
		
				break;
			}
		}
		
		
		
		fwriterOut.close();
	}
	
	public static int getDocumentListId(String documentList) {
		print(documentList);
		StringBuilder sb = new StringBuilder();
		for(char c : documentList.toCharArray()) { if(c == '\n') break; else sb.append(c); }
		return Integer.parseInt(sb.toString().split(" ")[1]);
	}
	
	public static String[] getDocumentList(String postingList) {
		StringBuilder sb = new StringBuilder();
		int df = Integer.parseInt(postingList.split(" ")[2]);
		int index = -1;
		String[] documentEntries = new String[df];
		for(char c : postingList.toCharArray()) {
			
		}
		return null;
	}
	
	public static String getWordFromPostingList(String postingList) {
		StringBuilder sb = new StringBuilder();
		for(char c : postingList.toCharArray()) { if(c == '\n') break; else sb.append(c); }
		return sb.toString().split(" ")[1];
	}
	
	public static String getNextWordPostingList(RandomAccessFile freader) throws IOException {
		String wordInfoLine = freader.readLine();
		StringBuilder wordInfoBlock = new StringBuilder();
		while(true) {
			wordInfoBlock.append(wordInfoLine);
			wordInfoLine = freader.readLine();
			if(wordInfoLine == null) { return null; }
			if(wordInfoLine.charAt(0) == 'w') { freader.seek(freader.getFilePointer() - wordInfoLine.length() - 1); break; }
			wordInfoBlock.append("\n");
		}
		return wordInfoBlock.toString();
	}
	
	
	public static void print(Object obj) {
		System.out.println(obj);
	}
}












