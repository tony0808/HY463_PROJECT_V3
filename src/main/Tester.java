package main;

import java.io.IOException;
import indexbuilder.Tokenizer;
import java.io.UnsupportedEncodingException;
import indexbuilder.XmlLabelReader;
import indexbuilder.FileWordReader;
import indexbuilder.IndexBuilder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Tester {

	public static void main(String[] args) throws IOException {
		String corpusPath = "C:\\SmallCollection\\Topic_1\\0";
		IndexBuilder ibuilder = new IndexBuilder(corpusPath);
		ibuilder.buildIndex();
		ibuilder.printIndex();
	}
	

}








