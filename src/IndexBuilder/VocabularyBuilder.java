package indexbuilder;

import java.io.IOException;
import java.io.RandomAccessFile;

public class VocabularyBuilder {

	private String invertedFile;
	private String targetDirectory;
	private String vocabularyName;
	
	public VocabularyBuilder(String invertedFile, String vocabularyName, String targetDirectory) {
		this.targetDirectory = targetDirectory;
		this.vocabularyName = vocabularyName;
		this.invertedFile = invertedFile;
	}
	
	public void buildVocabulary() throws IOException {
		RandomAccessFile freader = new RandomAccessFile(this.invertedFile, "r");
		RandomAccessFile fwriter = new RandomAccessFile(this.targetDirectory + "\\" + this.vocabularyName, "rw");
		String line;
		while((line = freader.readLine()) != null) {
			if(line.charAt(0) == 'w') {
				String[] parsedWordLine = line.split(" ");
				String word = parsedWordLine[1];
				String df = parsedWordLine[2];
				line = freader.readLine();
				String docId = line.split(" ")[1];
				fwriter.writeBytes(word + " " + df + " " + docId + '\n');
			}
		}
		freader.close();
		fwriter.close();
	}
}





