package indexer;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

public class DocumentVectorFileBuilder {
	
	private String targetDirectory;
	private int num_docs;
	
	public DocumentVectorFileBuilder(String targetDirectory) {
		this.num_docs = -1;
		this.targetDirectory = targetDirectory;
	}
	
	public DocumentVectorFileBuilder(String targetDirectory, int num_docs) {
		this.targetDirectory = targetDirectory;
		this.num_docs = num_docs;
	}
	
	public void setNumDocs(int num_docs) {
		this.num_docs = num_docs;
	}
	
	public void buildDocumentVectorFile() throws IOException {
		RandomAccessFile freader = new RandomAccessFile(this.targetDirectory + "\\" + IndexBuilder.INVERTEDFILENAME, "r");
		RandomAccessFile fwriter = new RandomAccessFile(this.targetDirectory + "\\" + IndexBuilder.DOCVECTORFILENAME, "rw");
		HashMap<Integer, TreeMap<Long, Double>> documentVectorsMap = buildDocumentVectorsMap(freader);
		for(Map.Entry<Integer, TreeMap<Long, Double>> entry : documentVectorsMap.entrySet()) {
			int docid = entry.getKey();
			TreeMap<Long, Double> weightsMap = entry.getValue();
			fwriter.writeBytes(String.valueOf(docid));
			for(Map.Entry<Long, Double> weightsEntry : weightsMap.entrySet()) {
				long index = weightsEntry.getKey();
				double weight = weightsEntry.getValue();
				fwriter.writeBytes(" " + index + "," + weight);
			}
			fwriter.writeBytes("\n");
		}
		freader.close();
		fwriter.close();
	}
	
	private HashMap<Integer, TreeMap<Long, Double>> buildDocumentVectorsMap(RandomAccessFile freader) throws IOException{
		HashMap<Integer, TreeMap<Long, Double>> documentVectors = new HashMap<>();
		TreeMap<Long, Double> weights;
		String[] block;
		int[] tFs;
		int[] docIds;
		double[] tfNorm;
		double weight, tf, idf;
		long index = 0L;
		int docid;
		while((block = InvertedFileReaderWriter.getWordBlock(freader)) != null) {
			docIds = InvertedFileReaderWriter.getDocIDList(block);
			tFs = InvertedFileReaderWriter.getTFList(block);
			tfNorm = getTFnormalized(tFs);
			idf = getIDF(InvertedFileReaderWriter.getDF(block));
			for(int i=0; i<docIds.length; i++) {
				docid = docIds[i];
				tf = tfNorm[i];
				weights = documentVectors.get(docid);
				if(weights == null) { weights = new TreeMap<Long, Double>(); }
				weight = tf * idf;
				weight = (double) (Math.round(weight*1000.0)/1000.0);
				weights.put(index, weight);
				documentVectors.put(docid, weights);
			}
			index++;
		}
		return documentVectors;
	}
	
	private double[] getTFnormalized(int[] tf) {
		int maxTF = getMaxTF(tf);
		double[] tfNorm = new double[tf.length];
		for(int i=0; i<tf.length; i++) { tfNorm[i] = (double) tf[i] / maxTF; }
		return tfNorm;
	}
	
	private double getIDF(int df) { return Math.log(this.num_docs / (double)df); }
	
	private int getMaxTF(int[] tf) {
		int max = tf[0];
		for(int i=0; i<tf.length; i++) { if(tf[i] > max) { max = tf[i]; } }
		return max;
	}
}









