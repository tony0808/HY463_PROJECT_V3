package queryevaluator;

import java.io.IOException;
import java.io.RandomAccessFile;

import indexer.IndexBuilder;

public class DocumentsFileScanner {
	
	private String documentsFileDirectory;
	private String[] relevantDocumentsBlock;
	private Integer[] docIds;
	
	public DocumentsFileScanner(String documentsFileDirectory) { this.documentsFileDirectory = documentsFileDirectory; }
	
	public DocumentsFileScanner(String documentsFileDirectory, Integer[] docIds) {
		this.documentsFileDirectory = documentsFileDirectory;
		this.docIds = docIds;
	}
	
	public void setDocIds(Integer[] docIds) { this.docIds = docIds; }
	
	public String[] getRelevantDocuments() {
		String[] relevantDocs = new String[this.relevantDocumentsBlock.length];
		for(int i=0; i<this.relevantDocumentsBlock.length; i++) {
			relevantDocs[i] = this.relevantDocumentsBlock[i].split(" ")[1];
		}
		return relevantDocs;
	}
	
	public void initRelevantDocumentsBlock() throws IOException {
		String documentsFile = this.documentsFileDirectory + "\\" + IndexBuilder.DOCUMENTSNAME;
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




