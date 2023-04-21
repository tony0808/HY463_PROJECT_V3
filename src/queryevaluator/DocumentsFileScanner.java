package queryevaluator;

import java.io.IOException;
import java.io.RandomAccessFile;

import indexer.IndexBuilder;

public class DocumentsFileScanner {
	
	private String parentDirectory;
	private String[] relevantDocumentsBlock;
	private int[] docIds;
	private int num_docs;
	
	public DocumentsFileScanner(String parentDirectory) throws IOException { this.parentDirectory = parentDirectory; setNumDocs(); }
	
	public DocumentsFileScanner(String parentDirectory, int[] docIds) throws IOException {
		this(parentDirectory);
		this.parentDirectory = parentDirectory;
		this.docIds = docIds;
	}
	
	public int getNumDocs() { return this.num_docs; }
	public void setDocIds(int[] docIds) { this.docIds = docIds; }
	
	public String[] getRelevantDocuments() {
		String[] relevantDocs = new String[this.relevantDocumentsBlock.length];
		for(int i=0; i<this.relevantDocumentsBlock.length; i++) { relevantDocs[i] = this.relevantDocumentsBlock[i].split(" ")[1]; }
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
			if(isDocIdRelevant(docid)) { sb.append(line).append("\n"); }
		}
		this.relevantDocumentsBlock = sb.toString().split("\n");
		freader.close();
	}
	
	public double getDocumentNorm(int docid) throws IOException {
		String documentsFile = this.parentDirectory + "\\" + IndexBuilder.DOCUMENTSNAME;
		RandomAccessFile freader = new RandomAccessFile(documentsFile, "r");
		String line;
		String[] parsedLine;
		int id;
		double norm = 0;
		while((line = freader.readLine()) != null) {
			parsedLine = line.split(" ");
			id = Integer.parseInt(parsedLine[0]);
			if(id == docid) { norm = Double.parseDouble(parsedLine[2]); }
		}
		freader.close();
		return norm;
	}
	
	private boolean isDocIdRelevant(int docid) {
		for(Integer id : this.docIds) { if(id == docid) return true; }
		return false;
	}
	
	private void setNumDocs() throws IOException {
		String documentsFile = this.parentDirectory + "\\" + IndexBuilder.DOCUMENTSNAME;
		RandomAccessFile freader = new RandomAccessFile(documentsFile, "r");
		int size = 0;
		while(freader.readLine() != null) { size++; }
		this.num_docs = size;
		freader.close();
	}
}




