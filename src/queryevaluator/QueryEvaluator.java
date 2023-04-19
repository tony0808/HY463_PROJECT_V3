package queryevaluator;

import java.io.IOException;
import java.util.HashMap;

public class QueryEvaluator {
	
	private String query;
	private VocabularyLoader vocLoader;
	
	public QueryEvaluator(String query, String vocabularyDirectory) throws IOException { 
		this.query = query;
		this.vocLoader = new VocabularyLoader(vocabularyDirectory);
	}
}
