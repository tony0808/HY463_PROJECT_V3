package queryevaluator;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.HashMap;

import indexer.IndexBuilder;

public class DocumentVectorFileScanner {
	
	private String parentDirectory;
	
	public DocumentVectorFileScanner(String parentDirectory)  { 
		this.parentDirectory = parentDirectory; 
	}
	
	public HashMap<Long, Double> getDocumentVector(int docid) throws IOException {
		String docsVectorFile = this.parentDirectory + "\\" + IndexBuilder.DOCVECTORFILENAME;
		RandomAccessFile freader = new RandomAccessFile(docsVectorFile, "r");
		HashMap<Long, Double> docVector = null;
		String line;
		while((line = freader.readLine()) != null) {
			if(Integer.parseInt(line.split(" ")[0]) == docid) {
				docVector = parseVector(line);
			}
		}
		freader.close();
		return docVector;
	}
	
	private HashMap<Long, Double> parseVector(String line) {
		HashMap<Long, Double> docVector = new HashMap<Long, Double>();
		String[] parsedLine = line.split(" ");
		String[] vectorEntry;
		long index;
		double weight;
		for(int i=1; i<parsedLine.length; i++) {
			vectorEntry = parsedLine[i].split(",");
			index = Long.parseLong(vectorEntry[0]);
			weight = Double.parseDouble(vectorEntry[1]);
			docVector.put(index, weight);
		}
		return docVector;
	}
}
