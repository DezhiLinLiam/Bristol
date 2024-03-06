#pragma once

#include "general.h"
#include "lisp.h"

typedef enum { LBRACK, RBRACK, CAR, CDR, CONS, PLUS, LENGTH, LESS, GREATER, EQUAL, SET, PRINT, VAR, STRING, NIL, LITERAL, IF, WHILE, SWITCH, CASE, ERROR } Type;

typedef struct Token Token;

struct Token
{
	Type type;
	char name[100];
};

void parse_prog(Token* tokens, int num);
int parse_token(FILE* fp, Token* tokens);
void check_token(Token* token, char* name);
int check_literal(char* name);
void parse_instrcts(Token* tokens, int num, int* cur);
void parse_instrct(Token* tokens, int num, int* cur);
void parse_func(Token* tokens, int num, int* cur);
int parse_retfunc(Token* tokens, int num, int* cur);
int parse_iofunc(Token* tokens, int num, int* cur);
int parse_if(Token* tokens, int num, int* cur);
int parse_loop(Token* tokens, int num, int* cur);
int parse_switch(Token* tokens, int num, int* cur);
int parse_listfunc(Token* tokens, int num, int* cur);
int parse_intfunc(Token* tokens, int num, int* cur);
int parse_boolfunc(Token* tokens, int num, int* cur);
void parse_list(Token* tokens, int num, int* cur);
int parse_set(Token* tokens, int num, int* cur);
int parse_print(Token* tokens, int num, int* cur);
