package indexer;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class IndexBuilder {
	
	public static final String VOCABULARYNAME = "Vocabulary.txt";
	public static final String DOCUMENTSNAME = "Documents.txt";
	public static final String LABELSNAME = "Labels.txt";
	public static final String POSTINGFILENAME = "PostingFile.txt";
	
	private HashMap<Integer, ArrayList<Integer>> documentVectorMap;
	private HashMap<Integer, Double> documentNormMap;
	
	private String targetDirectory;
	private String corpusDirectory;
	private InvertedFileBuilder invertedBuilder;
	
	public String getVocabularyName() { return VOCABULARYNAME; }
	public String getDocumentsFileName() { return DOCUMENTSNAME; }
	public String getLabelsFileName() { return LABELSNAME; }
	public String getPostingFileName() { return POSTINGFILENAME; }
	public IndexBuilder(String corpusDirectory, String targetDirectory) {
		this.documentNormMap = new HashMap<>();
		this.documentVectorMap = new HashMap<>();
		this.corpusDirectory = corpusDirectory;
		this.targetDirectory = targetDirectory;
		this.invertedBuilder = new InvertedFileBuilder(this.corpusDirectory, this.targetDirectory);
	}
	
	public void buildIndex() throws UnsupportedEncodingException, IOException {
		invertedBuilder.buildInvertedFile();
//		buildVocabularyAndPostingFile();
//		buildDocumentsFile();
//		buildLabelsFile();
	}
	
	private void buildLabelsFile() throws IOException {
		HashMap<Integer, String> labelNamesMap = this.invertedBuilder.getLabelIdToNameMap();
		FileWriter fwriter = new FileWriter(new File(this.targetDirectory + "\\" + LABELSNAME));
		for(Map.Entry<Integer, String> entry : labelNamesMap.entrySet()) {
			int labelid = entry.getKey();
			String labelName = entry.getValue();
			fwriter.write(labelid + " " + labelName + "\n");
		}
		fwriter.close();
	}
	
	private int getTotalBlockSize(String[] block) {
		int size = 0;
		for(int i=1; i<block.length; i++) {
			size += block[i].length();
		}
		return size;
	}
}














