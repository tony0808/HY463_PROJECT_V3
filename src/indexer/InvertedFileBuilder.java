package indexer;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.io.IOException;
import mitos.stemmer.Stemmer;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;
import java.io.File;

public class InvertedFileBuilder {
	
	protected static final String INVERTEDFILENAME = "InvertedFile.txt";
	private final int MAX_BLOCK_SIZE = 200 * 1024; // KB
	
	Queue<String> partialFileQueue;
	private int partialFileIndex;
	private String targetDirectory;
	private HashMap<Integer, String> documents;
	private HashMap<Integer, String> labels;
	private TreeMap<String, PostingList> block;
	
	public InvertedFileBuilder(String documentsDirectory, String targetDirectory) {
		initClassData(targetDirectory);
		initDocumentIdMap((new DirectoryScanner(documentsDirectory)).get_document_paths());
		initLabelIdMap();
		Stemmer.Initialize();
	}
	
	public HashMap<Integer, String> getDocIdToNameMap() { return this.documents; }
	public HashMap<Integer, String> getLabelIdToNameMap() { return this.labels; }
	
	public void buildInvertedFile() throws UnsupportedEncodingException, IOException {
		for(int docid = 1; docid <= this.documents.size(); docid++) {
			for(int labelid = 1; labelid <= this.labels.size(); labelid++) {
				Tokenizer tokenizer = new Tokenizer((new XmlLabelReader(documents.get(docid))).getLabelText(this.labels.get(labelid)));
				for(Map.Entry<String, ArrayList<Integer>> token : tokenizer.get_tokens().entrySet()) {
					PostingList postingList = block.get(token.getKey());
					if(postingList == null) {
						postingList = new PostingList();
						postingList.addNewPostingList(docid, labelid, token.getValue());
					}
					else {
						if(!postingList.hasPositionsList(docid)) { postingList.createNewPositionList(docid); } 
						postingList.addPositionsList(docid, labelid, token.getValue());
					}
					this.block.put(token.getKey(), postingList);
					if(getBlockSize() > MAX_BLOCK_SIZE) { writePartialIndexToDisk(); }
				}
			}
		}
		writePartialIndexToDisk();
		mergePartialIndexes();
	}
	
	private void writePartialIndexToDisk() throws IOException {
		this.partialFileIndex += 1;
		String partialFilename = this.targetDirectory + "\\" + this.partialFileIndex + ".txt";
		this.partialFileQueue.add(partialFilename);
		InvertedFileReaderWriter.writeIndexToDisk(partialFilename, this.block);
		this.block = new TreeMap<String, PostingList>();
	}
	
	private void mergePartialIndexes() throws IOException {
		while(this.partialFileQueue.size() > 1) {
			this.partialFileIndex += 1;
			String partialFileOut = this.targetDirectory + "\\"  + this.partialFileIndex + ".txt";
			String partialFileA = this.partialFileQueue.remove();
			String partialFileB = this.partialFileQueue.remove();
			InvertedFileReaderWriter.mergeTwoPartialIndexes(partialFileA, partialFileB, partialFileOut);
			this.partialFileQueue.add(partialFileOut);
		}
		String mergedFilename = this.partialFileQueue.remove();
		System.out.println(mergedFilename);
		deletePartialIndexFiles(mergedFilename);
		renameFinalMergedFile(mergedFilename, this.targetDirectory + "\\" + INVERTEDFILENAME);
	}
	
	private void deletePartialIndexFiles(String fileToKeep) throws IOException {
		DirectoryScanner dirScanner = new DirectoryScanner();
		dirScanner.setDirectoryPath(this.targetDirectory);
		dirScanner.deleteFilesExceptOne(fileToKeep);
	}
	
	private void renameFinalMergedFile(String oldFileName, String newFileName) {
		File oldFile = new File(oldFileName);
		File newFile = new File(newFileName);
		oldFile.renameTo(newFile);
	}
	
	private long getBlockSize() {
		long size = 0L;
		for(String word : this.block.keySet()) {
			size += word.length();
			size += this.block.get(word).getSize();
		}
		return size;
	}
	
	private void initDocumentIdMap(ArrayList<String> documents) {
		int docid = 1;
		this.documents = new HashMap<>();
		for(String document : documents) { this.documents.put(docid, document); docid += 1; }
	}
	
	private void initLabelIdMap() {
		int labelid = 1;
		this.labels = new HashMap<>();
		for(String label : XmlLabelReader.labelNames) { this.labels.put(labelid, label); labelid += 1; }
	}
	
	private void initClassData(String targetDirectory) {
		this.partialFileQueue = new LinkedList<>();
		this.partialFileIndex = 0;
		this.targetDirectory = targetDirectory;
		this.block = new TreeMap<>();
	}
}
