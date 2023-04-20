package queryevaluator;

import java.io.IOException;
import java.io.RandomAccessFile;

import indexer.IndexBuilder;

public class PostingFileScanner {
	
	private String postingFileDirectory;
	private String[] relevantDocumentsBlock;
	private int df;
	private int dptr;
	
	public PostingFileScanner(String postingFileDirectory) { this.postingFileDirectory = postingFileDirectory; }
	
	public PostingFileScanner(String postingFileDirectory, int df, int dptr) { 
		this.postingFileDirectory = postingFileDirectory;
		this.df = df;
		this.dptr = dptr;
	}
	
	public void setDF(int df) { this.df = df; }
	public void setDPTR(int dptr) { this.dptr = dptr; }
	
	public int[] getRelevantDocIds() throws IOException {
		buildRelevantDocumentsBlock();
		StringBuilder sb = new StringBuilder();
		for(String line : this.relevantDocumentsBlock) {
			sb.append(line.split(" ")[0]).append(",");
		}
		String[] docIdsStr = sb.toString().split(",");
		int[] docIdsInt = new int[docIdsStr.length];
		for(int i=0; i<docIdsStr.length; i++) { docIdsInt[i] = Integer.parseInt(docIdsStr[i]); }
		return docIdsInt;
	}
	
	private void buildRelevantDocumentsBlock() throws IOException {
		String postingFile = this.postingFileDirectory + "\\" + IndexBuilder.POSTINGFILENAME;
		RandomAccessFile freader = new RandomAccessFile(postingFile, "r");
		StringBuilder sb = new StringBuilder();
		String line;
		int docCount = 0;
		freader.seek(this.dptr);
		while((line = freader.readLine()) != null) {
			if(++docCount == this.df + 1) { break; } 
			sb.append(line).append("\n");
		}
		this.relevantDocumentsBlock = sb.toString().split("\n");
		freader.close();
	}
	
	public void printDocumentBlock() {
		for(String str : this.relevantDocumentsBlock) {
			System.out.println(str);
		}
	}
}





