package indexer;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class FileWordReader {
	
	private String[] words;
	
	public FileWordReader(String filename) {
		try {
			BufferedReader reader;
			reader = new BufferedReader(new FileReader(filename));
			StringBuilder sb = new StringBuilder();
			String line;
			while((line = reader.readLine()) != null) { sb.append(line); sb.append(","); }
			reader.close();
			this.words =  sb.toString().split(",");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public String[] getWords() { return this.words; } 
}














