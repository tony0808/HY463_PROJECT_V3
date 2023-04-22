package queryevaluator;

import java.io.IOException;
import java.io.RandomAccessFile;
import indexer.IndexBuilder;

public class PostingFileScanner {
	
	private String parentDirectory;
	private int df;
	private int dptr;
	
	public PostingFileScanner(String parentDirectory) { this.parentDirectory = parentDirectory; }
	
	public void setDF(int df) { this.df = df; }
	public void setDPTR(int dptr) { this.dptr = dptr; }
	
	public int[] getRelevantDocIds() throws IOException {
		String[] relevantDocumentsBlock = getRelevanDocumentsBlock();
		StringBuilder sb = new StringBuilder();
		for(String line : relevantDocumentsBlock) { sb.append(line.split(" ")[0]).append(","); }
		String[] docIdsStr = sb.toString().split(",");
		int[] docIdsInt = new int[docIdsStr.length];
		for(int i=0; i<docIdsStr.length; i++) { docIdsInt[i] = Integer.parseInt(docIdsStr[i]); }
		return docIdsInt;
	}
	
	private String[] getRelevanDocumentsBlock() throws IOException {
		String postingFile = this.parentDirectory + "\\" + IndexBuilder.POSTINGFILENAME;
		RandomAccessFile freader = new RandomAccessFile(postingFile, "r");
		StringBuilder sb = new StringBuilder();
		String line;
		int docCount = 0;
		freader.seek(this.dptr);
		while((line = freader.readLine()) != null) {
			if(++docCount == this.df + 1) { break; } 
			sb.append(line).append("\n");
		}
		freader.close();
		return sb.toString().split("\n");
	}
}
