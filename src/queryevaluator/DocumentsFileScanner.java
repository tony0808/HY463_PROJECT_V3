package queryevaluator;

import java.io.IOException;
import java.io.RandomAccessFile;

import indexer.IndexBuilder;

public class DocumentsFileScanner {
	
	private String parentDirectory;
	private String[] relevantDocumentsBlock;
	private int[] docIds;
	
	public DocumentsFileScanner(String parentDirectory) { this.parentDirectory = parentDirectory; }
	
	public DocumentsFileScanner(String parentDirectory, int[] docIds) {
		this.parentDirectory = parentDirectory;
		this.docIds = docIds;
	}
	
	public void setDocIds(int[] docIds) { this.docIds = docIds; }
	
	public String[] getRelevantDocuments() {
		String[] relevantDocs = new String[this.relevantDocumentsBlock.length];
		for(int i=0; i<this.relevantDocumentsBlock.length; i++) {
			relevantDocs[i] = this.relevantDocumentsBlock[i].split(" ")[1];
		}
		return relevantDocs;
	}
	
	public void buildRelevantDocumentsBlock() throws IOException {
		String documentsFile = this.parentDirectory + "\\" + IndexBuilder.DOCUMENTSNAME;
		RandomAccessFile freader = new RandomAccessFile(documentsFile, "r");
		StringBuilder sb = new StringBuilder();
		String line;
		int docid;
		while((line = freader.readLine()) != null) {
			docid = Integer.parseInt(line.split(" ")[0]);
			if(isDocIdRelevant(docid)) {
				sb.append(line).append("\n");
			}
		}
		this.relevantDocumentsBlock = sb.toString().split("\n");
		freader.close();
	}
	
	private boolean isDocIdRelevant(int docid) {
		for(Integer id : this.docIds) { if(id == docid) return true; }
		return false;
	}
}




