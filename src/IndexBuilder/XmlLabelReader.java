package IndexBuilder;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

import gr.uoc.csd.hy463.NXMLFileReader;
import java.util.HashSet;
import java.util.ArrayList;

public class XmlLabelReader {
	
	private final NXMLFileReader xmlFile;
    private ArrayList<String> labelNames;
	
	public XmlLabelReader(String documentPath) throws UnsupportedEncodingException, IOException {
		labelNames.add("publisher"); labelNames.add("journal");
		labelNames.add("abstract"); labelNames.add("title");
		labelNames.add("pmcid"); labelNames.add("body");
		labelNames.add("categories"); labelNames.add("authors");
		this.xmlFile = new NXMLFileReader(new File(documentPath));
	}
	
	public ArrayList<String> getLabelNames() { return this.labelNames; }
	
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





