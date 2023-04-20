package indexer;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

public class IndexBuilder {
	
	public static final String VOCABULARYNAME = "Vocabulary.txt";
	public static final String DOCUMENTSNAME = "Documents.txt";
	public static final String LABELSNAME = "Labels.txt";
	public static final String POSTINGFILENAME = "PostingFile.txt";
	
	private String targetDirectory;
	private String corpusDirectory;
	private InvertedFileBuilder invertedBuilder;
	
	public String getVocabularyName() { return VOCABULARYNAME; }
	public String getDocumentsFileName() { return DOCUMENTSNAME; }
	public String getLabelsFileName() { return LABELSNAME; }
	public String getPostingFileName() { return POSTINGFILENAME; }
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
	
	private void buildVocabularyAndPostingFile() throws IOException {
		RandomAccessFile freader = new RandomAccessFile(this.targetDirectory + "\\" + InvertedFileBuilder.INVERTEDFILENAME, "r");
		RandomAccessFile vocabWriter = new RandomAccessFile(this.targetDirectory + "\\" + VOCABULARYNAME, "rw");
		RandomAccessFile postingWriter = new RandomAccessFile(this.targetDirectory + "\\" + POSTINGFILENAME, "rw");
		
		StringBuilder sb = new StringBuilder();
		long docPointer = 0;
		String[] block;
		while((block = InvertedFileReaderWriter.getWordBlock(freader)) != null) {
			String wordLine = block[0];
			sb.append(wordLine).append(" ").append(docPointer).append("\n");
			vocabWriter.writeBytes(sb.toString());
			for(int i=1; i<block.length; i++) {
				postingWriter.writeBytes(block[i] + "\n");
			}
			sb.setLength(0);
			docPointer += (block.length - 1) + getTotalBlockSize(block);
		}
		freader.close();
		vocabWriter.close();
		postingWriter.close();
	}
	
	private void buildDocumentsFile() throws IOException {
		RandomAccessFile freader = new RandomAccessFile(this.targetDirectory + "\\" + InvertedFileBuilder.INVERTEDFILENAME, "r");
		FileWriter fwriter = new FileWriter(new File(this.targetDirectory + "\\" + DOCUMENTSNAME));
		TreeMap<Integer, String> documents = this.invertedBuilder.getDocIdToNameMap();
		HashMap<Integer, ArrayList<Integer>> docVectorMap = buildDocumentVectorMap(freader);
		HashMap<Integer, Double> documentNormMap = buildDocumentNormMap(docVectorMap);
		for(Map.Entry<Integer, String> documentEntry : documents.entrySet()) {
			int docid = documentEntry.getKey();
			String documentName = documentEntry.getValue();
			double norm = documentNormMap.get(docid);
			fwriter.write(docid + " " + documentName + " " + norm + "\n");
		}
		fwriter.close();
		freader.close();
	}
	
	private HashMap<Integer, Double> buildDocumentNormMap(HashMap<Integer, ArrayList<Integer>> documentVectorMap) {
		HashMap<Integer, Double> documentNormMap = new HashMap<>();
		ArrayList<Integer> docVector;
		int docid;
		double norm;
		for(Map.Entry<Integer, ArrayList<Integer>> entry : documentVectorMap.entrySet()) {
			docid = entry.getKey();
			docVector = entry.getValue();
			norm = 0.0;
			for(int n : docVector) {
				norm += n * n;
			}
			documentNormMap.put(docid, Math.sqrt(norm));
		}
		return documentNormMap;
	}
	
	private HashMap<Integer, ArrayList<Integer>> buildDocumentVectorMap(RandomAccessFile freader) throws IOException {
		HashMap<Integer, ArrayList<Integer>> documentVectorMap = new HashMap<>();
		ArrayList<Integer> docVector;
		String[] block;
		int[] docIds;
		int docid;
		while((block = InvertedFileReaderWriter.getWordBlock(freader)) != null) {
			docIds = InvertedFileReaderWriter.getDocIDList(block);
			for(int i=0; i<docIds.length; i++) {
				docid = docIds[i];
				docVector = documentVectorMap.get(docid);
				if(docVector == null) {
					docVector = new ArrayList<Integer>();
				}
				docVector.add(InvertedFileReaderWriter.getTFfromDocumentEntry(block[i+1]));
				documentVectorMap.put(docid, docVector);
			}
		}
		return documentVectorMap;
	}
	
	private void buildLabelsFile() throws IOException {
		TreeMap<Integer, String> labelNamesMap = this.invertedBuilder.getLabelIdToNameMap();
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














