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
	
	private static final String VOCABULARYNAME = "Vocabulary.txt";
	private static final String DOCUMENTSNAME = "Documents.txt";
	private static final String LABELSNAME = "Labels.txt";
	private static final String POSTINGFILENAME = "PostingFile.txt";
	
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
		//invertedBuilder.buildInvertedFile();
		//buildVocabularyAndPostingFile();
		buildDocumentsFile();
		//buildLabelsFile();
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
	
	private void buildDocumentsFile() throws IOException {
		buildDocumentNormMap();
		HashMap<Integer, String> documentNamesMap = this.invertedBuilder.getDocIdToNameMap();
		FileWriter fwriter = new FileWriter(new File(this.targetDirectory + "\\" + DOCUMENTSNAME));
		for(Map.Entry<Integer, String> entry : documentNamesMap.entrySet()) {
			int docid = entry.getKey();
			String docName = entry.getValue();
			Double docNorm = this.documentNormMap.get(docid);
			fwriter.write(docid + " " + docName + " " + docNorm + "\n");
		}
		fwriter.close();
	}
	
	private void buildVocabularyAndPostingFile() throws IOException {
		RandomAccessFile freader = new RandomAccessFile(this.targetDirectory + "\\" + InvertedFileBuilder.INVERTEDFILENAME, "r");
		RandomAccessFile vocabWriter = new RandomAccessFile(this.targetDirectory + "\\" + VOCABULARYNAME, "rw");
		RandomAccessFile postingWriter = new RandomAccessFile(this.targetDirectory + "\\" + POSTINGFILENAME, "rw");

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
	
	private void buildDocumentNormMap() throws IOException {
		initDocumentNormMap();
		RandomAccessFile freader = new RandomAccessFile(this.targetDirectory + "\\" + InvertedFileBuilder.INVERTEDFILENAME, "r");
		String[] block;
		while((block = InvertedFileReaderWriter.getBlock(freader)) != null) {
			for(String line : block) {
				if(line.charAt(0) == 'd') {
					int docid = Integer.parseInt(line.split(" ")[1]);
					int tf = Integer.parseInt(line.split(" ")[2]);
					ArrayList<Integer> docVector = this.documentVectorMap.get(docid);
					docVector.add(tf);
				}
			}
		}
		calculateDocumentNorms();
	}
	
	private void calculateDocumentNorms() {
		for(Map.Entry<Integer, ArrayList<Integer>> docVectorEntry : this.documentVectorMap.entrySet()) {
			int docid = docVectorEntry.getKey();
			ArrayList<Integer> docVector = docVectorEntry.getValue();
			double norm = 0.0;
			for(int tf : docVector) {
				norm += tf * tf;
			}
			norm = Math.sqrt(norm);
			norm = Math.round(norm * Math.pow(10, 3));
			this.documentNormMap.put(docid, norm);
		}
	}
	
	private void initDocumentNormMap() {
		Set<Integer> docIdsSet = this.invertedBuilder.getDocIdToNameMap().keySet();
		for(Integer docid : docIdsSet) {
			this.documentVectorMap.put(docid, new ArrayList<Integer>());
		}
	}
}














