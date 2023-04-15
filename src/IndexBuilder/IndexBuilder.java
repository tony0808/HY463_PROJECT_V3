package indexbuilder;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

public class IndexBuilder {
	
	private String corpusDirectoryPath;
	private ArrayList<String> documents;
	private String[] labels;
	private HashMap<String, HashMap<String, PostingEntry>> block;
	
	public IndexBuilder(String corpusDirectoryPath) { 
		this.corpusDirectoryPath = corpusDirectoryPath; 
		this.block = new HashMap<>();
		initializeResources();
	}
	
	private void initializeResources() {
		DirectoryScanner dirScanner = new DirectoryScanner(this.corpusDirectoryPath);
		this.documents = dirScanner.get_document_paths();
		this.labels = XmlLabelReader.labelNames;
	}
	
	public void printIndex() {
		for(Map.Entry<String, HashMap<String, PostingEntry>> entry : this.block.entrySet()) {
			System.out.println(entry.getKey());
			for(Map.Entry<String, PostingEntry> posting : entry.getValue().entrySet()) {
				System.out.println("\t" + posting.getKey());
				System.out.println("\t\t" + posting.getValue().getLabelPositionsMap());
			}
		}
	}
	
	public void buildIndex() throws UnsupportedEncodingException, IOException {
		for(String document : this.documents) {
			for(String label : this.labels) {
				XmlLabelReader labelReader = new XmlLabelReader(document);
				String text = labelReader.getLabelText(label);
				Tokenizer tokenizer = new Tokenizer(text);
				for(Map.Entry<String, ArrayList<Integer>> token : tokenizer.get_tokens().entrySet()) {
					String word = token.getKey();
					ArrayList<Integer> positions = token.getValue();
					HashMap<String, PostingEntry> postingList = this.block.get(word);
					if(postingList == null) {
						HashMap<String, ArrayList<Integer>> labelWordPositionsMap = new HashMap<String, ArrayList<Integer>>();
						labelWordPositionsMap.put(label, positions);
						PostingEntry postingEntry = new PostingEntry(labelWordPositionsMap, positions.size());
						postingList = new HashMap<String, PostingEntry>();
						postingList.put(document, postingEntry);
					}
					else {
						PostingEntry postingEntry = postingList.get(document);
						if(postingEntry == null) {
							HashMap<String, ArrayList<Integer>> labelWordPositionsMap = new HashMap<String, ArrayList<Integer>>();
							labelWordPositionsMap.put(label, positions);
							postingEntry = new PostingEntry(labelWordPositionsMap, positions.size());
							postingList.put(document, postingEntry);
						}
						else {
							postingEntry.getLabelPositionsMap().put(label, positions);
						}
						postingList.put(document, postingEntry);
					}
					this.block.put(word, postingList);
				}
			}
		}
	}
}










