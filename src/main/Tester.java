package main;

import java.io.IOException;
import java.io.RandomAccessFile;

import indexbuilder.IndexBuilder;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class Tester {
	// 4577, 4532, 4262, 3641
	public static void main(String[] args) throws IOException, InterruptedException, ExecutionException {
		String corpusPath = "C:\\MiniCollection";
		String targetDirectory = "C:\\CollectionIndex";
		long ts = System.currentTimeMillis();
		IndexBuilder ibuilder = new IndexBuilder(corpusPath, targetDirectory);
		ibuilder.createIndex();
		long tf = System.currentTimeMillis();
		double time = (tf - ts) / 1000.0;
		System.out.println(time);
	}
}












