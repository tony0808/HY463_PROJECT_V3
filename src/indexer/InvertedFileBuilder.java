package indexer;

import java.util.Map;
import java.util.TreeMap;
import java.io.IOException;
import java.io.RandomAccessFile;

import mitos.stemmer.Stemmer;
import java.io.UnsupportedEncodingException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;
import java.io.File;

public class InvertedFileBuilder {
	
	protected static final String INVERTEDFILENAME = "InvertedFile.txt";
	private final long MAX_BLOCK_SIZE = 200L * 1024L * 1024L; 
	
	Queue<String> partialFileQueue;
	private int partialFileIndex;
	private String targetDirectory;
	private TreeMap<Integer, String> documents;
	private TreeMap<Integer, String> labels;
	private TreeMap<String, PostingList> block;
	private long currentBlockSize;
	
	public InvertedFileBuilder(String documentsDirectory, String targetDirectory) {
		initClassData(targetDirectory);
		initDocumentIdMap((new DirectoryScanner(documentsDirectory)).get_document_paths());
		initLabelIdMap();
		Stemmer.Initialize();
	}
	
	public TreeMap<Integer, String> getDocIdToNameMap() { return this.documents; }
	public TreeMap<Integer, String> getLabelIdToNameMap() { return this.labels; }
	
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
					this.currentBlockSize += token.getKey().length() + postingList.getSize();
					if(this.currentBlockSize > MAX_BLOCK_SIZE) { writePartialIndexToDisk(); }
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
		this.currentBlockSize = 0;
	}
	
	private void mergePartialIndexes() throws IOException {
		while(this.partialFileQueue.size() > 1) {
			this.partialFileIndex += 1;
			String partialFileOut = this.targetDirectory + "\\"  + this.partialFileIndex + ".txt";
			String partialFileA = this.partialFileQueue.remove();
			String partialFileB = this.partialFileQueue.remove();
			mergeTwoPartialIndexes(partialFileA, partialFileB, partialFileOut);
			this.partialFileQueue.add(partialFileOut);
		}
		String mergedFilename = this.partialFileQueue.remove();
		deletePartialIndexFiles(mergedFilename);
		renameFinalMergedFile(mergedFilename, this.targetDirectory + "\\" + INVERTEDFILENAME);
	}
	
	private void mergeTwoPartialIndexes(String fileA, String fileB, String fileOut) throws IOException {
		RandomAccessFile freaderA = new RandomAccessFile(fileA, "r");
		RandomAccessFile freaderB = new RandomAccessFile(fileB, "r");
		RandomAccessFile fwriter = new RandomAccessFile(fileOut, "rw");
		
		String[] blockA = InvertedFileReaderWriter.getWordBlock(freaderA);
		String[] blockB = InvertedFileReaderWriter.getWordBlock(freaderB);
		int mergeBlockResult;
		while((blockA != null) || (blockB != null)) {
			if(blockA == null) {
				InvertedFileReaderWriter.writeBlockToDisk(fwriter, blockB);
				blockB = InvertedFileReaderWriter.getWordBlock(freaderB);
			}
			else if(blockB == null) {
				InvertedFileReaderWriter.writeBlockToDisk(fwriter, blockA);
				blockA = InvertedFileReaderWriter.getWordBlock(freaderA);
			}
			else {
				mergeBlockResult = InvertedFileReaderWriter.mergeBlocks(blockA, blockB, fwriter);
				if(mergeBlockResult == InvertedFileReaderWriter.EQUAL_BLOCKS) {
					blockA = InvertedFileReaderWriter.getWordBlock(freaderA);
					blockB = InvertedFileReaderWriter.getWordBlock(freaderB);
				}
				else if(mergeBlockResult == InvertedFileReaderWriter.BLOCKA_LESS) { blockA = InvertedFileReaderWriter.getWordBlock(freaderA); }
				else if(mergeBlockResult == InvertedFileReaderWriter.BLOCKB_LESS) { blockB = InvertedFileReaderWriter.getWordBlock(freaderB); }
				else { System.out.println("code should not be here (1)"); System.exit(1); }
			}
		}
		freaderA.close();
		freaderB.close();
		fwriter.close();
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
	
	private void initDocumentIdMap(ArrayList<String> documents) {
		int docid = 1;
		this.documents = new TreeMap<>();
		for(String document : documents) { this.documents.put(docid, document); docid += 1; }
	}
	
	private void initLabelIdMap() {
		int labelid = 1;
		this.labels = new TreeMap<>();
		for(String label : XmlLabelReader.labelNames) { this.labels.put(labelid, label); labelid += 1; }
	}
	
	private void initClassData(String targetDirectory) {
		this.partialFileQueue = new LinkedList<>();
		this.partialFileIndex = 0;
		this.targetDirectory = targetDirectory;
		this.block = new TreeMap<>();
		this.currentBlockSize = 0L;
	}
}
