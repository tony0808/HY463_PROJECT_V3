package queryevaluator;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.HashMap;

import indexer.IndexBuilder;

public class VocabularyLoader {
	
	private String parentDirectory;
	private HashMap<String, VocabData> vocabulary;
	
	public VocabularyLoader(String parentDirectory) throws IOException { 
		this.parentDirectory = parentDirectory; 
		this.vocabulary = new HashMap<>();
		loadVocabulary();
	}
	
	public HashMap<String, VocabData> getVocabulary() { return this.vocabulary; }
	
	private void loadVocabulary() throws IOException {
		String vocabFilename = this.parentDirectory + "\\" + IndexBuilder.VOCABULARYNAME;
		RandomAccessFile freader = new RandomAccessFile(vocabFilename, "r");
		VocabData vocabData;
		String line;
		String word;
		int df;
		int dptr;
		int index = 0;
		while((line = freader.readLine()) != null) {
			word = line.split(" ")[0];
			df = Integer.parseInt(line.split(" ")[1]);
			dptr = Integer.parseInt(line.split(" ")[2]);
			vocabData = new VocabData(df, dptr, index++);
			this.vocabulary.put(word, vocabData);
		}
		freader.close();
	}
}
