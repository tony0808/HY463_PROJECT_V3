package main;

import java.io.IOException;
import java.io.RandomAccessFile;

import indexbuilder.InvertedFileBuilder;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import indexbuilder.VocabularyBuilder;

public class Tester {
	// 4577, 4532, 4262, 3641
	public static void main(String[] args) throws IOException, InterruptedException, ExecutionException {
		String corpusPath = "C:\\MiniCollection";
		String targetDirectory = "C:\\CollectionIndex";
		String vocabularyName = "Vocabulary.txt";
//		long ts = System.currentTimeMillis();
//		InvertedFileBuilder ibuilder = new InvertedFileBuilder(corpusPath, targetDirectory);
//		ibuilder.buildInvertedFile();
//		long tf = System.currentTimeMillis();
//		double time = (tf - ts) / 1000.0;
//		System.out.println(time);
		VocabularyBuilder vbuilder = new VocabularyBuilder("C:\\CollectionIndex\\InvertedFile.txt", vocabularyName, targetDirectory);
		vbuilder.buildVocabulary();
	}
}












