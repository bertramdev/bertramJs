package com.bertramlabs.js.parser;

public enum Token {
	ERROR,
	EOF,
	EOL,
	LP,
	RP,
	LC,
	RC,
	LB,
	RB,
	SEMI,
	COMMA,
	COLON,
	DOT,
	EXPORT,
	IMPORT,
	BITOR,
	BITXOR,
	BITAND,
	LSH,
	RSH,
	URSH,
	ASSIGN,
	ASSIGN_BITOR,
	ASSIGN_BITXOR,
	ASSIGN_BITAND,
	ASSIGN_LSH,
	ASSIGN_RSH,
	ASSIGN_URSH,
	ASSIGN_ADD,
	ASSIGN_SUB,
	ASSIGN_MUL,
	ASSIGN_DIV,
	ASSIGN_MOD,
	INC,
	DEC,
	OR,
	AND,
	BREAK,
	CONTINUE,
	CASE,
	SWITCH,
	WHILE,
	DO,
	FOR,
	TRUE,
	FALSE,
	IF,
	ELSE,
	RETURN,
	VAR,
	FUNCTION,
	NAME,
	STRING,
	TRY,
	THROW,
	THIS,
	TYPEOF,
	INSTANCEOF,
	VOID,
	YIELD,
	CATCH,
	FINALLY,
	RESERVED
}