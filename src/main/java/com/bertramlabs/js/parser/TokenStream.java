package com.bertramlabs.js.parser;
import java.io.Reader;
import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.io.InputStream;
import java.io.IOException;


/**
* Class for reading a character stream and generating tokens as well as token data;
* Objective: To read a String or Reader to produce tokens that can be consumed by a parser
* @author David Estes
*/
public class TokenStream {

	private final static int EOF_CHAR = -1;
	private final static char BYTE_ORDER_MARK = '\uFEFF';
	private final static char LINE_SEP_CHAR   = '\u2028';
	private final static char PARA_SEP_CHAR   = '\u2029';

	private String fileName = "unknown";
	private Reader inputReader;
	private int lineNo = 0;
	private int col = 0;
	private int lineStart=0;
	private int lineEndChar=-1;
	private int sourceCursor;
	private int sourceEnd;
	private char[] sourceBuffer;
	private char[] backtrackBuffer;
	private int backtrackCursor=0;


	public TokenStream(String fileName, int lineNo, String sourceString) {
		this(fileName,lineNo, new InputStreamReader(new ByteArrayInputStream(sourceString.getBytes())));
	}

	public TokenStream(String fileName, int lineNo, InputStream is) {
		this(fileName, lineNo, new InputStreamReader(is));
	}

	public TokenStream(int lineNo, Reader inputReader) {
		this("unknown",lineNo, inputReader);
	}

	public TokenStream(String fileName, int lineNo, Reader inputReader) {
		this.fileName = fileName;
		this.lineNo = lineNo;
		this.inputReader = inputReader;
		this.sourceBuffer = new char[512];
		this.backtrackBuffer = new char[3];
		this.sourceEnd = 0;
		this.sourceCursor = 0;
	}




	public String getFileName() {
		return this.fileName;
	}

	public long getLineNo() {
		return this.lineNo;
	}

	public Reader getInputReader() {
		return this.inputReader;
	}

	private void consume() {
		//Consume current token and move on;
	}

	/**
	* Skips all characters pertaining to a comment.
	* Per Ecmascript 5.1 Section 7.4
	* TODO: Should we copy comment contents into a buffer?
	*/
	private int scanComment(boolean multiline) throws IOException {
		boolean lineTerminatorFound = false;
		for(;;) {
			int c=getChar();
			if(c == EOF_CHAR) {
				ungetChar(c);
				return 0;
			}
			if(c == '\n') {
				if(!multiline) {
					ungetChar(c);
					return 0;
				}
				lineTerminatorFound = true;
			}
			if(c == '*' && multiline) {
				c = getChar();
				if(c == '/') {
					//We finished skipping the comment
					break;
				} else {
					ungetChar(c);
				}
			}
		}
		if(lineTerminatorFound) {
			return '\n';
		} else {
			return 0;
		}
	}

	public Token nextToken() {
		try {
			int c;
			for(;;) {
				for(;;) {
					c = getCharSkipSpace();
					if(c == EOF_CHAR) {
						return Token.EOF;
					} else if(c == '\n') {
						//Per Ecmascript-262 Line Terminators
						return Token.EOL;
					}
					break;
				}


				// Test for Comments
				if(c == '/') { //Check if the prequalifying character for a comment is detected
					int d = getChar();
					if(d == '*') {
						//Multiline Comment
						d = scanComment(true);
						if(d == '\n') {
							return Token.EOL;
						}
						continue;
					} else if(d == '/') {
						//Singleline Comment
						scanComment(false);
						continue;
					} else {
						ungetChar(d);
					}
				}

				//TODO: Test if String (Quotation)
				//TODO: Test if Numeric Literal
				//TODO: Test if Alpha (Identifier)


			}

		} catch(IOException e) {
			//TODO: Notify Error
			return Token.EOF;
		}

		/*Stages of Parsing a javascript File
		* - We want to strip out whitespace
		*
		*/
		// return null;
	}


	/**
	* Retreives the next character in the stream that is not a whitespace character
	*/
	public int getCharSkipSpace() throws IOException {
		int c = 0;
		while(c == 0 || isJSSpace(c)) {
			c = getChar();
		}
		return c;
	}

	public int getChar() throws IOException{
			return getChar(true);
	}

	public int getChar(boolean skipFormattingChars) throws IOException {
		int c;
		if(backtrackCursor > 0) {
			c = backtrackBuffer[--backtrackCursor];
			return c;
		}
		for(;;) {
				if(sourceCursor == sourceEnd) {
					if(!fillBuffer()) {
						return EOF_CHAR;
					}
				}
				col++;
				c = sourceBuffer[sourceCursor++];

				if(lineEndChar != -1) {
					if(lineEndChar == '\r' && c == '\n') {
						lineEndChar = c;
						continue;
					}
					lineEndChar = -1;
					col = 0;
					lineNo++;
					lineStart = sourceCursor;
				}

				if(c<= 127) {
					if(c == '\r' || c == '\n' || c == LINE_SEP_CHAR || c == PARA_SEP_CHAR) {
						lineEndChar = c;
						c = '\n';
					}
				} else {
					// TODO WE NEED TO CHECK AND SKIP
				}
				return c;
		}

	}

	public void ungetChar(int c) {
		if(backtrackCursor >= backtrackBuffer.length) {
			char[] tmp = new char[backtrackBuffer.length * 2];
			System.arraycopy(backtrackBuffer,0,tmp,0,backtrackCursor-1);
		}
		backtrackBuffer[backtrackCursor] = (char)c;
		backtrackCursor++;
	}

	public boolean fillBuffer() throws IOException{
		int n = 0;
		if(sourceEnd == sourceBuffer.length) { //If we have reached the end of the buffer
			if(lineStart != 0) { //If we are in the middle of a line we wipe to beginning of line
				System.arraycopy(sourceBuffer,lineStart,sourceBuffer,0,sourceEnd - lineStart);
				sourceEnd -= lineStart;
				sourceCursor -= lineStart;
				lineStart = 0;
			} else { //If we filled the buffer and still in the middle of a line, we need to increase buffer size
				char[] tmp = new char[sourceBuffer.length * 2];
				System.arraycopy(sourceBuffer,0,tmp,0,sourceEnd);
				sourceBuffer = tmp;
			}
		}

		n = inputReader.read(sourceBuffer,sourceEnd,sourceBuffer.length - sourceEnd);

		if(n < 0) {
			return false;
		}
		sourceEnd += n;
		return true;
	}

	/**
	* Returns true if the character is a javascript whitespace character per ECMASCRIPT
	*/
    static boolean isJSSpace(int c)
    {
        if (c <= 127) {
            return c == 0x20 || c == 0x9 || c == 0xC || c == 0xB;
        } else {
            return c == 0xA0 || c == BYTE_ORDER_MARK
                || Character.getType((char)c) == Character.SPACE_SEPARATOR;
        }
    }

    private static boolean isJSFormatChar(int c)
    {
        return c > 127 && Character.getType((char)c) == Character.FORMAT;
    }

}
