package queryevaluator;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.HashMap;
import java.util.TreeMap;

import indexer.IndexBuilder;

public class DocumentVectorFileScanner {
	
	private HashMap<Integer, TreeMap<Long, Double>> documentsVectorMap;
	private String parentDirectory;
	private int[] docIds;
	
	public DocumentVectorFileScanner(String parentDirectory) throws IOException {
		this.parentDirectory = parentDirectory;
		this.documentsVectorMap = new HashMap<>();
	}
	
	public void setDocIds(int[] docIds) { this.docIds = docIds; }
	public TreeMap<Long, Double> getDocumentVector(int docid) { return this.documentsVectorMap.get(docid); }

	public void buildDocumentsVectorMap() throws IOException {
		String docsVectorFile = this.parentDirectory + "\\" + IndexBuilder.DOCVECTORFILENAME;
		RandomAccessFile freader = new RandomAccessFile(docsVectorFile, "r");
		TreeMap<Long, Double> docVector;
		String line;
		int index = 0;
		int docid;
		while((line = freader.readLine()) != null) {
			docid = getDocId(line);
			if(index == this.docIds.length) break;
			if(docid == this.docIds[index]) {
				docVector = getDocumentVector(line);
				this.documentsVectorMap.put(docid, docVector);
				index++;
			}
		}
		freader.close();
	}
	
	private TreeMap<Long, Double> getDocumentVector(String line) {
		TreeMap<Long, Double> vector = new TreeMap<Long, Double>();
		String[] parsedLine = line.split(" ");
		String[] vectorEntries;
		long index;
		double weight;
		for(int i=1; i<parsedLine.length; i++) {
			vectorEntries = parsedLine[i].split(",");
			index = Long.parseLong(vectorEntries[0]);
			weight = Double.parseDouble(vectorEntries[1]);
			vector.put(index, weight);
		}
		return vector;
	}
	
	private int getDocId(String str) {
		String docid = "";
		for(char c : str.toCharArray()) {
			if(c == ' ') break;
			docid += c;
		}
		return Integer.parseInt(docid);
	}
}










