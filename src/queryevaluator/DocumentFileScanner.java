package queryevaluator;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;

import indexer.IndexBuilder;

public class DocumentFileScanner {
	
	private String parentDirectory;
	private int[] docids;
	
	public DocumentFileScanner() { }
	public DocumentFileScanner(String parentDirectory) throws IOException {
		this.parentDirectory = parentDirectory;
		loadDocumentIds();
	}
	
	public void setParentDirectory(String parentDirectory) { this.parentDirectory = parentDirectory; }
	public int[] getDocumentIds() { return this.docids; }
	public int getSize() { return this.docids.length; }
	
	public double getDocumentNorm(int docid) throws IOException {
		String documentsFile = this.parentDirectory + "\\" + IndexBuilder.DOCUMENTSNAME;
		RandomAccessFile freader = new RandomAccessFile(documentsFile, "r");
		double docnorm = 0;
		String line;
		while((line = freader.readLine()) != null) { if(Integer.parseInt(line.split(" ")[0]) == docid) { docnorm = Double.parseDouble(line.split(" ")[2]); } }
		freader.close();
		return docnorm;
	}
	
	public String getDocumentName(int docid) throws IOException {
		String documentsFile = this.parentDirectory + "\\" + IndexBuilder.DOCUMENTSNAME;
		RandomAccessFile freader = new RandomAccessFile(documentsFile, "r");
		String docname = null;
		String line;
		while((line = freader.readLine()) != null) { if(Integer.parseInt(line.split(" ")[0]) == docid) { docname = line.split(" ")[1]; } }
		freader.close();
		return docname;
	}
	
	private void loadDocumentIds() throws IOException {
		String documentsFile = this.parentDirectory + "\\" + IndexBuilder.DOCUMENTSNAME;
		RandomAccessFile freader = new RandomAccessFile(documentsFile, "r");
		ArrayList<Integer> docids = new ArrayList<Integer>();
		int docid;
		String line;
		while((line = freader.readLine()) != null) {
			docid = Integer.parseInt(line.split(" ")[0]);
			docids.add(docid);
		}
		this.docids = new int[docids.size()];
		for(int i=0; i<docids.size(); i++) { this.docids[i] = docids.get(i); }
		freader.close();
	}
}







