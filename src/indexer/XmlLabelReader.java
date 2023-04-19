package indexer;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

import gr.uoc.csd.hy463.NXMLFileReader;
import java.util.HashSet;
import java.util.ArrayList;

public class XmlLabelReader {
	
	public static final String[] labelNames = {"pmcid", "publisher", "journal", "abstract", "categories", "authors", "title", "body"};
	private final NXMLFileReader xmlFile;
  
	public XmlLabelReader(String documentPath) throws UnsupportedEncodingException, IOException {
		this.xmlFile = new NXMLFileReader(new File(documentPath));
	}
	
	public String getLabelText(String labelName) {
		if(labelName.equals("publisher")) { return this.xmlFile.getPublisher(); }
		else if(labelName.equals("journal")) { return this.xmlFile.getJournal(); }
		else if(labelName.equals("abstract")) { return this.xmlFile.getAbstr(); }
		else if(labelName.equals("title")) { return this.xmlFile.getTitle(); }
		else if(labelName.equals("pmcid")) { return this.xmlFile.getPMCID(); }
		else if(labelName.equals("body")) { return this.xmlFile.getBody(); }
		else if(labelName.equals("categories")) { return get_categories(); }
		else if(labelName.equals("authors")) { return get_authors(); }
		return null;
	}
	
	private String get_authors() {
		ArrayList<String> authors = this.xmlFile.getAuthors();
		StringBuilder sb = new StringBuilder();
		for(String author : authors) { sb.append(author); sb.append(", "); }
		return ((sb == null) ? "" : sb.toString());
	}
	
	private String get_categories() {
		HashSet<String> categories = this.xmlFile.getCategories();
		StringBuilder sb = new StringBuilder();
		for(String category : categories) { sb.append(category); sb.append(", "); }
		return ((sb == null) ? "" : sb.toString());
	}
}





