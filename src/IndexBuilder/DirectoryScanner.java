package indexbuilder;

import java.io.File;
import java.util.ArrayList;

public class DirectoryScanner {
	
	private ArrayList<String> documentPaths;
	private String directoryPath;
	
	public DirectoryScanner(String directoryPath) { 
		this.documentPaths = new ArrayList<String>(); 
		this.directoryPath = directoryPath; 
		init_documentPaths_recursive(new File(this.directoryPath));
	}
	
	public ArrayList<String> get_document_paths() { return this.documentPaths; }
	
	private void init_documentPaths_recursive(File folder) {
		for(File fileEntry : folder.listFiles()) {
			if(fileEntry.isDirectory()) { init_documentPaths_recursive(fileEntry); }
			else { this.documentPaths.add(fileEntry.getAbsolutePath()); }
		}
	}
}










