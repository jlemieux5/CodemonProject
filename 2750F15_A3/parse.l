%{
#include "y.tab.h"
#include <stdio.h>
#include <stdlib.h>

extern FILE* yyin;
int afterOpcode;

%}


%option case-insensitive
%%


begin@[0-9]+	{yylval.digit = yytext[6] - '0'; return TOK_BEGIN;}
dat|mov|add|sub|mul|div|mod|jmp|jmz|jmn|djn|seq|sne|slt|set|clr|frk|nop|rnd {afterOpcode = 1; yylval.opcode = strdup(yytext); return TOK_OPCODE;}

\( 					{return TOK_OB;}
\) 					{return TOK_CB;}
(\+|\*|\-|\%|\/)	{yylval.addMode = yytext[0];
					if(afterOpcode == 1 && yytext[0] == '*'){ afterOpcode = 0; return TOK_ADDMODE;} else{return TOK_OPERATOR;}}
.+:					{yylval.opcode = strdup(yytext);  return TOK_SLABEL;}
!.* 				{}
,					{afterOpcode = 1; return TOK_COMMA;}
-?[0-9]+ 			{ afterOpcode = 0; yylval.digit = atoi(yytext); return TOK_DIGIT;}
[$|#|\[|\]|\*|@|\{|\}|<|>] {yylval.addMode = yytext[0]; afterOpcode = 0; return TOK_ADDMODE;}
[a-zA-Z][a-zA-Z0-9]+	{yylval.opcode = strdup(yytext);  return TOK_LABEL;}
[;]						{return TOK_SEMICOLON;}
[ \t] {}
\n	  {}
. {}
%%

