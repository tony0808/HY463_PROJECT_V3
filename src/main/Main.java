package main;

import java.io.IOException;
import java.io.RandomAccessFile;

import indexbuilder.IndexBuilder;
import indexbuilder.IndexFileReaderWriter;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class Main {
	// 4577, 4532, 4262, 3641
	public static void main(String[] args) throws IOException, InterruptedException, ExecutionException {

		IndexFileReaderWriter.mergeTwoPartialIndexes("C:\\CollectionIndex\\testA.txt", "C:\\CollectionIndex\\testB.txt", "C:\\CollectionIndex\\out.txt");
	}
}












