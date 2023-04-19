package indexbuilder;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

public class IndexBuilder {
	
	private static final String VOCABULARYNAME = "Vocabulary.txt";
	private static final String DOCUMENTSNAME = "Documents.txt";
	private static final String LABELSNAME = "Labels.txt";
	private static final String POSTINGLISTNAME = "PostingList.txt";
	
	private String targetDirectory;
	private String corpusDirectory;
	private InvertedFileBuilder invertedBuilder;
	
	public IndexBuilder(String corpusDirectory, String targetDirectory) {
		this.corpusDirectory = corpusDirectory;
		this.targetDirectory = targetDirectory;
		this.invertedBuilder = new InvertedFileBuilder(this.corpusDirectory, this.targetDirectory);
	}
	
	public void buildIndex() throws UnsupportedEncodingException, IOException {
		invertedBuilder.buildInvertedFile();
		buildVocabularyAndPostingFile();
		buildDocumentsFile();
		buildLabelsFile();
	}
	
	public void buildLabelsFile() throws IOException {
		HashMap<Integer, String> labelNamesMap = this.invertedBuilder.getLabelIdToNameMap();
		FileWriter fwriter = new FileWriter(new File(this.targetDirectory + "\\" + LABELSNAME));
		for(Map.Entry<Integer, String> entry : labelNamesMap.entrySet()) {
			int labelid = entry.getKey();
			String labelName = entry.getValue();
			fwriter.write(labelid + " " + labelName + "\n");
		}
		fwriter.close();
	}
	
	public void buildDocumentsFile() throws IOException {
		HashMap<Integer, String> documentNamesMap = this.invertedBuilder.getDocIdToNameMap();
		FileWriter fwriter = new FileWriter(new File(this.targetDirectory + "\\" + DOCUMENTSNAME));
		for(Map.Entry<Integer, String> entry : documentNamesMap.entrySet()) {
			int docid = entry.getKey();
			String docName = entry.getValue();
			fwriter.write(docid + " " + docName + "\n");
		}
		fwriter.close();
	}
	
	private void buildVocabularyAndPostingFile() throws IOException {
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














