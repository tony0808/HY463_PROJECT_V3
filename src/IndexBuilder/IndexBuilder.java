package indexbuilder;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import mitos.stemmer.Stemmer;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

public class IndexBuilder {
	
	private final int MAX_BLOCK_SIZE = 50 * 1024; // 50KB
	
	private String targetDirectory;
	private HashMap<Integer, String> documents;
	private HashMap<Integer, String> labels;
	private TreeMap<String, PostingList> block;
	
	public IndexBuilder(String documentsDirectory, String targetDirectory) {
		this.targetDirectory = targetDirectory;
		this.block = new TreeMap<>();
		initDocumentIdMap((new DirectoryScanner(documentsDirectory)).get_document_paths());
		initLabelIdMap();
		Stemmer.Initialize();
	}
	
	public void createIndex() throws UnsupportedEncodingException, IOException {
		int partialFileIndex = 0;
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
					
					if(getBlockSize() > MAX_BLOCK_SIZE) {
						partialFileIndex += 1;
						String partialFilename = this.targetDirectory + "\\" + partialFileIndex + ".txt";
						writeIndexToDisk(partialFilename);
						this.block = new TreeMap<String, PostingList>();
					}
				}
			}
		}
		partialFileIndex += 1;
		String partialFilename = this.targetDirectory + "\\" + partialFileIndex + ".txt";
		writeIndexToDisk(partialFilename);
	}
	
	public void writeIndexToDisk(String filename) throws IOException {
		FileWriter fwriter = new FileWriter(new File(filename));
		for(Map.Entry<String, PostingList> index : this.block.entrySet()) {
			String word = index.getKey();
			fwriter.write(word + "\n");
			HashMap<Integer, HashMap<Integer, ArrayList<Integer>>> postingList = index.getValue().getPostingList();
			for(Map.Entry<Integer, HashMap<Integer, ArrayList<Integer>>> posting : postingList.entrySet()) {
				// String document = this.documents.get(posting.getKey());
				fwriter.write("\t" + posting.getKey() + " : " + index.getValue().getTf(posting.getKey()) + "\n");
				for(Map.Entry<Integer, ArrayList<Integer>> positions : posting.getValue().entrySet()) {
					// String label = this.labels.get(positions.getKey());
					fwriter.write("\t\t" + positions.getKey() + " : " + positions.getValue() + "\n");
				}
			}
		}
		fwriter.close();
	}
	
	public long getBlockSize() {
		long size = 0L;
		for(String word : this.block.keySet()) {
			size += word.length();
			size += this.block.get(word).getSize();
		}
		return size;
	}
	
	
	public void printIndex() {
		for(Map.Entry<String, PostingList> index : this.block.entrySet()) {
			String word = index.getKey();
			System.out.println(word);
			HashMap<Integer, HashMap<Integer, ArrayList<Integer>>> postingList = index.getValue().getPostingList();
			for(Map.Entry<Integer, HashMap<Integer, ArrayList<Integer>>> posting : postingList.entrySet()) {
				String document = this.documents.get(posting.getKey());
				System.out.println("\t" + document);
				for(Map.Entry<Integer, ArrayList<Integer>> positions : posting.getValue().entrySet()) {
					String label = this.labels.get(positions.getKey());
					System.out.println("\t\t" + label);
					System.out.println("\t\t" + positions.getValue());
				}
			}
		}
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
}
