package queryevaluator;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.TreeMap;

import indexer.IndexBuilder;

public class VocabularyLoader {
	private class VocabData {
		private int df;
		private int dPtr;
		public VocabData(int df, int dPtr) { this.df = df; this.dPtr = dPtr; }
		int getDF() { return this.df; }
		int getDPTR() { return this.dPtr; }
	}
	
	private TreeMap<String, VocabData> vocabulary;
	private String targetDirectory;
	
	public VocabularyLoader(String targetDirectory) throws IOException { 
		this.vocabulary = new TreeMap<>(); 
		this.targetDirectory = targetDirectory; 
		loadVocabulary(); 
	}
	
	public int getDF(String word) { return  this.vocabulary.containsKey(word) ? this.vocabulary.get(word).getDF() : -1; }
	public int getDPTR(String word) { return  this.vocabulary.containsKey(word) ? this.vocabulary.get(word).getDPTR() : -1; }
	
	private void loadVocabulary() throws IOException {
		String vocabFilename = this.targetDirectory + "\\" + IndexBuilder.VOCABULARYNAME;
		RandomAccessFile freader = new RandomAccessFile(vocabFilename, "r");
		String line;
		while((line = freader.readLine()) != null) {
			String word = line.split(" ")[0];
			int df = Integer.parseInt(line.split(" ")[1]);
			int dptr = Integer.parseInt(line.split(" ")[2]);
			VocabData vocabData = new VocabData(df, dptr);
			this.vocabulary.put(word, vocabData);
		}
		freader.close();
	}
}















