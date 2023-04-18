package main;

import java.io.FileNotFoundException;
import java.io.IOException;
import indexbuilder.DirectoryScanner;
import java.io.RandomAccessFile;

import indexbuilder.InvertedFileBuilder;
import indexbuilder.InvertedFileReaderWriter;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class Main {
	// 4577, 4532, 4262, 3641
	public static void main(String[] args) throws IOException, InterruptedException, ExecutionException {
		String fileIn = "C:\\CollectionIndex\\out.txt";
		String fileOut = "C:\\CollectionIndex\\error2.txt";
		fileTest(fileIn, fileOut);
		//IndexFileReaderWriter.mergeTwoPartialIndexes("C:\\CollectionIndex\\5.txt", "C:\\CollectionIndex\\6.txt", "C:\\testErrors\\out.txt");
			
	}
	
	public static void fileTest(String fileIn, String fileOut) throws IOException {
		RandomAccessFile freader = new RandomAccessFile(fileIn, "r");
		RandomAccessFile fwriter = new RandomAccessFile(fileOut, "rw");
		String line;
		while((line = freader.readLine()) != null) {
			if(line.charAt(0) == 'd') {
			
				
				fwriter.writeBytes(line + '\n');
			}
		}
		freader.close();
		fwriter.close();
	}
	
}












