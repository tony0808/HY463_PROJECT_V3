package indexbuilder;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.ArrayList;
import java.util.HashMap;

public class Tokenizer {
	
	private static final String WORD_REGEX = "\\b\\w+\\b";
	private String text;
	private HashMap<String, ArrayList<Integer>> wordPositionMap;
	
	public Tokenizer(String text) { this.text = text; }
	
	private void tokenize() {
		
	}
}	
















