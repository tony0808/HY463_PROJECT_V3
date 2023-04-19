package indexer;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;

public class DirectoryScanner {
	
	private ArrayList<String> documentPaths;
	private String directoryPath;
	
	public DirectoryScanner() {}
	
	public DirectoryScanner(String directoryPath) { 
		this.documentPaths = new ArrayList<String>(); 
		this.directoryPath = directoryPath; 
		init_documentPaths_recursive(new File(this.directoryPath));
	}
	
	public void setDirectoryPath(String directoryPath) { this.directoryPath = directoryPath; }
	public ArrayList<String> get_document_paths() { return this.documentPaths; }
	
	public void deleteFilesExceptOne(String fileToKeep) throws IOException {
		File directory = new File(this.directoryPath);
		if(directory.isDirectory()) {
			File[] files = directory.listFiles();
			for(File file : files) {
				if(!file.getAbsoluteFile().toString().equals(fileToKeep)) {
					Files.delete(file.toPath());
				}
			}
		}
	}
	
	private void init_documentPaths_recursive(File folder) {
		for(File fileEntry : folder.listFiles()) {
			if(fileEntry.isDirectory()) { init_documentPaths_recursive(fileEntry); }
			else { this.documentPaths.add(fileEntry.getAbsolutePath()); }
		}
	}
}