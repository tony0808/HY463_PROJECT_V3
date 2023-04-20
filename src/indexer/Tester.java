package indexer;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Tester {
	
	public static void main(String[] args) throws NumberFormatException, IOException {
		String targetDirectory = "C:\\CollectionIndex";
		String coprusDirectory = "C:\\MiniCollection";
		IndexBuilder ibuilder = new IndexBuilder(coprusDirectory, targetDirectory);
		ibuilder.buildIndex();
	}
}
