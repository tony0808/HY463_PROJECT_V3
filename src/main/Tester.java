package main;

import java.io.IOException;
import java.io.RandomAccessFile;

import indexbuilder.IndexBuilder;
import indexbuilder.InvertedFileBuilder;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class Tester {
	public static void main(String[] args) throws IOException, InterruptedException, ExecutionException {
		IndexBuilder ibuilder = new IndexBuilder("C:\\MiniCollection", "C:\\CollectionIndex");
		ibuilder.buildIndex();
	}
}












