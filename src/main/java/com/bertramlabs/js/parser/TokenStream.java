package com.bertramlabs.js.parser;
import java.io.Reader;


/**
* Class for reading a character stream and generating tokens as well as token data;
*/
public class TokenStream {

	private String fileName = "unknown";
	private long lineNo = 0;
	private Reader inputReader;

	public TokenStream(String fileName, long lineNo, Reader inputReader) {
		this.fileName = fileName;
		this.lineNo = lineNo;
		this.inputReader = inputReader;
	}


	private char readChar() {
		//TODO: Implement
	}

	private void consume() {
		//Consume current token and move on;
	}


	public Token nextToken() {
		char c = readChar();

		/*Stages of Parsing a javascript File
		* - We want to strip out whitespace
		* 
		*/
	}
}
