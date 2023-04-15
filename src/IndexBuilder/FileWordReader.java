package indexbuilder;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class FileWordReader {
	
	private String[] words;
	
	public FileWordReader(String filename) throws IOException {
		BufferedReader reader = new BufferedReader(new FileReader(filename));
		StringBuilder sb = new StringBuilder();
		String line;
		while((line = reader.readLine()) != null) { sb.append(line); sb.append("\\n"); }
		reader.close();
		this.words =  sb.toString().split("\n");
	}
	
	public String[] getWords() { return this.words; } 
}














