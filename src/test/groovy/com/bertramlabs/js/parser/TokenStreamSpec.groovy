package com.bertramlabs.js.parser


import spock.lang.Specification
import java.io.ByteArrayInputStream
import java.io.InputStreamReader

/**
 * TODO: write doc
 */
class TokenStreamSpec extends Specification {

    def "it can be created"() {
        given:
			InputStream is = new ByteArrayInputStream( "var a = 0;".bytes);
			def reader = new InputStreamReader(is)
		when:
			def tokenStream = new TokenStream("test",0,reader)
		then:
			tokenStream.fileName == "test"
			tokenStream.inputReader != null
    }

	def "it can be created with an input stream"() {
		given:
			InputStream is = new ByteArrayInputStream( "var a = 0;".bytes);
		when:
			def tokenStream = new TokenStream("test",0,is)
		then:
			tokenStream.fileName == "test"
			tokenStream.inputReader != null
	}

	def "it can be created with a source string"() {
		given:
			def source = "var a = 0;"
		when:
			def tokenStream = new TokenStream("test",0,source)
		then:
			tokenStream.fileName == "test"
			tokenStream.inputReader != null
	}

	def "detects end of file as stream"() {
		given:
			def jsScript = ""; //Empty
			def tokenStream = new TokenStream("test",0,jsScript)
		when:
			def token = tokenStream.nextToken();
		then:
			token == Token.EOF
	}


	def "getCharSkipSpace should only return characters that are not whitespace"() {
		given:
			def jsScript = "     \tA";
			def tokenStream = new TokenStream("test",0,jsScript)
		when:
			int c = tokenStream.getCharSkipSpace();
			int e = tokenStream.getCharSkipSpace();
		then:
			c == 'A'
			e == -1
	}

	def "should only get an EOF character on a single line comment"() {
		given:
			def jsScript = "//This is a test";
			def tokenStream = new TokenStream("test",0,jsScript)
		when:
			def token = tokenStream.nextToken();
		then:
			token == Token.EOF
	}

	def "should only get an EOF character on a multi line comment"() {
		given:
			def jsScript = "/*This is a test*/";
			def tokenStream = new TokenStream("test",0,jsScript)
		when:
			def token = tokenStream.nextToken();
		then:
			token == Token.EOF
	}

	def "should get a line seperator token on a multi line comment"() {
		given:
			def jsScript = "/*This is\n a test**/";
			def tokenStream = new TokenStream("test",0,jsScript)
		when:
			def token = tokenStream.nextToken();
		then:
			token == Token.EOL
	}

	/**
	* Tests that Punctuator Tokens Are Returned
	* See ECMA-262 7.7
	*/
	def "should return Punctuators"() {
		expect:
			new TokenStream("test",0,punct).nextToken() == token
		where:
		punct | token
		"{"   | Token.LC
		"}"   | Token.RC
		"("   | Token.LP
		")"   | Token.RP
		"["   | Token.LB
		"]"   | Token.RB
		"."   | Token.DOT
		";"   | Token.SEMI
		","   | Token.COMMA
		">"   | Token.GT
		"<"   | Token.LT
		"<="  | Token.LE
		">="  | Token.GE
		"=="  | Token.EQ
		"!="  | Token.NE
		"===" | Token.EQ_EXACT
		"!==" | Token.NE_EXACT
		"+"   | Token.ADD
		"-"   | Token.SUB
		"*"   | Token.MUL
		"%"   | Token.MOD
		"++"  | Token.INC
		"--"  | Token.DEC
		">>"  | Token.RSH
		"<<"  | Token.LSH
		">>>" | Token.URSH
		"&"   | Token.BITAND
		"|"   | Token.BITOR
		"^"   | Token.BITXOR
		"!"   | Token.NOT
		"~"   | Token.BITNOT
		"&&"  | Token.AND
		"||"  | Token.OR
		"?"   | Token.QUESTION
		":"   | Token.COLON
		"="   | Token.ASSIGN
		"+="  | Token.ASSIGN_ADD
		"-="  | Token.ASSIGN_SUB
		"*="  | Token.ASSIGN_MUL
		"%="  | Token.ASSIGN_MOD
		"<<=" | Token.ASSIGN_LSH
		">>=" | Token.ASSIGN_RSH
		">>>="| Token.ASSIGN_URSH
		"&="  | Token.ASSIGN_BITAND
		"|="  | Token.ASSIGN_BITOR
		"^="  | Token.ASSIGN_BITXOR
		"/"   | Token.DIV
		"/="  | Token.ASSIGN_DIV
	}


	def "should return boolean literals"() {
		expect:
			new TokenStream("test",0,punct).nextToken() == token
		where:
		punct   | token
		"true"  | Token.TRUE
		"false" | Token.FALSE
	}

	def "should return null literal"() {
		expect:
			new TokenStream("test",0,punct).nextToken() == token
		where:
		punct   | token
		"null"  | Token.NULL
	}
}
