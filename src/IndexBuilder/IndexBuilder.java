package indexbuilder;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;

public class IndexBuilder {
	
	private static final String VOCABULARYNAME = "Vocabulary.txt";
	private static final String DOCUMENTSNAME = "Documents.txt";
	private static final String POSTINGLISTNAME = "PostingList.txt";
	
	private String targetDirectory;
	private String corpusDirectory;
	private InvertedFileBuilder invBuilder;
	
	public IndexBuilder(String corpusDirectory, String targetDirectory) {
		this.corpusDirectory = corpusDirectory;
		this.targetDirectory = targetDirectory;
		this.invBuilder = new InvertedFileBuilder(this.corpusDirectory, this.targetDirectory);
	}
	
	public void buildIndex() throws UnsupportedEncodingException, IOException {
		// this.invBuilder.buildInvertedFile();
		// buildVocabularyAndPostingFile();
		buildDocumentsFile(this.invBuilder.getDocIdToNameMap());
	}
	
	
	public void buildDocumentsFile(HashMap<Integer, String> docIdToNameMap) {
		// good
	}
	
	public void buildVocabularyAndPostingFile() throws IOException {
		RandomAccessFile freader = new RandomAccessFile(this.targetDirectory + "\\" + InvertedFileBuilder.INVERTEDFILENAME, "r");
		RandomAccessFile vocabWriter = new RandomAccessFile(this.targetDirectory + "\\" + VOCABULARYNAME, "rw");
		RandomAccessFile postingWriter = new RandomAccessFile(this.targetDirectory + "\\" + POSTINGLISTNAME, "rw");

		StringBuilder sb = new StringBuilder();
		long docPointer = 1;
		String[] block;
		while((block = InvertedFileReaderWriter.getBlock(freader)) != null) {
			String wordLine = block[0];
			String word = wordLine.split(" ")[1];
			String df = wordLine.split(" ")[2];
			sb.append(word).append(" ").append(df).append(" ").append(docPointer).append("\n");
			vocabWriter.writeBytes(sb.toString());
			for(int i=1; i<block.length; i++) {
				postingWriter.writeBytes(block[i] + '\n');
			}
			sb.setLength(0);
			docPointer += block.length - 1;
		}
		freader.close();
		vocabWriter.close();
		postingWriter.close();
	}
}














