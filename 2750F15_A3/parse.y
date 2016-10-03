%{
#include <stdio.h>
#include <ctype.h>
#include <string.h>
extern FILE* yyin;
extern int mode;
extern char *filePathSend;
typedef unsigned long long uint64;
typedef unsigned int uint32;

int yylex(void);

/* Determines what address mode was provided based on the TOKEN and returns the proper uint64 for it*/
uint64 readAddressMode(char numVal);

/* Determines what opcode was provided based on the TOKEN and returns the proper uint64 for it*/
uint64 readOpcode(char* opcode);

/* Takes all uint64s in the codemonLine struct and bitshifts until a proper binary instruction is made */
void makeBinaryInstruction();

/* takes the begin statement and stores it in the struct */
void makeBegin(int begin);

/* writes all lines to a codemon file */
void writeCodemonFile();

/* Fills in values if an address mode or digit was not provided */
void fillValues();

/* evaluates an expression based on its operator and digits */
int evalExpression();

/* Change a negative value into a positive one (to send to the server) */
void changeNegativeVal();

/* Transfroms a label provided to its proper digit value */
int evalLabel(char * label);


/* Struct to store all info, to be used to write to the codemon file later */
typedef struct codemonLine
{
	uint32 begin;
	uint64 opcode;
	uint64 firstDigit;
	uint64 firstAddMode;
	uint64 secondDigit;
	uint64 secondAddMode;
	uint64 binaryInstruction;	
}codemonLine;

int tempDigit = 0;
int tempDigitTwo = 0;
int hasVal = 0;
char operator;
codemonLine storageLine;
uint64 codemonProgram[50];
char *labeList[50];
int lines = 0;

void yyerror(const char* string) {
	//fprintf(stderr, "Error %s\n", string);
}
%}

%token TOK_BEGIN TOK_OPCODE TOK_OB TOK_CB TOK_OPERATOR TOK_SLABEL TOK_COMMA TOK_DIGIT TOK_ADDMODE TOK_SEMICOLON TOK_LABEL

%union {
	int digit;
	char* opcode;
	char addMode;
}
%%
final: commands {if(mode ==1){ writeCodemonFile();}  lines = 0;}
commands:  commands command 
		|
		;
command: full_stmt  | begin_stmt 
	;		


begin_stmt: TOK_BEGIN TOK_SEMICOLON {if(mode ==1){makeBegin(yylval.digit);}}	
	;

full_stmt: label_stmt opcode_stmt faddmode_stmt fdigit_stmt TOK_COMMA saddmode_stmt sdigit_stmt TOK_SEMICOLON {if(mode ==1){if(storageLine.firstDigit > 8192 || storageLine.secondDigit > 8192)
		{return 1;} makeBinaryInstruction(); codemonProgram[lines] = storageLine.binaryInstruction;} lines++;} | label_stmt opcode_stmt faddmode_stmt fdigit_stmt TOK_SEMICOLON {if(mode ==1){fillValues(); makeBinaryInstruction(); codemonProgram[lines] = storageLine.binaryInstruction;} lines++;}
	;

label_stmt: TOK_SLABEL {
	if(mode == 0){
	
	yylval.opcode[strlen(yylval.opcode)-1] = 0;
	labeList[lines] = malloc(strlen(yylval.opcode) + 1);
	strcpy(labeList[lines], yylval.opcode);
	}free(yylval.opcode);
}| 
	;
	
opcode_stmt: TOK_OPCODE {if(mode ==1){ storageLine.opcode = readOpcode(yylval.opcode);} free(yylval.opcode);}
	;
	
faddmode_stmt: TOK_ADDMODE {if(mode ==1){ storageLine.firstAddMode = readAddressMode(yylval.addMode);}} | {if(mode ==1){storageLine.firstAddMode = 0;}} 
	;
saddmode_stmt: TOK_ADDMODE {if(mode ==1){storageLine.secondAddMode = readAddressMode(yylval.addMode);}} | {if(mode ==1){storageLine.secondAddMode = 0;}} 
	;
fdigit_stmt: expression_stmt {if(mode ==1){ if(tempDigit < 0){changeNegativeVal();} storageLine.firstDigit = tempDigit;  hasVal = 0;}} | {if(mode ==1){ storageLine.firstDigit = 0;}}
	;

sdigit_stmt: expression_stmt {if(mode ==1){if(tempDigit < 0){changeNegativeVal();} storageLine.secondDigit = tempDigit; hasVal = 0;}} | {if(mode ==1){ storageLine.secondDigit = 0;}}
	;

expression_stmt: expression_stmt operator_stmt expression_stmt {}
	;
expression_stmt: TOK_OB expression_stmt TOK_CB {}
	;
expression_stmt: TOK_DIGIT {if(mode ==1){
	if(hasVal == 0){tempDigit = yylval.digit; hasVal = 1;}else{tempDigitTwo = yylval.digit; evalExpression(); }}} | TOK_LABEL {if(mode == 1)
	{if(hasVal == 0)
	{
		tempDigit = evalLabel(yylval.opcode); 
		if(tempDigit == -9999)
			return 1;
		hasVal = 1;
	}
	else{
	tempDigitTwo = evalLabel(yylval.opcode);
	if(tempDigit == -9999)
		return 1; 
	evalExpression();
	}
	}free(yylval.opcode);}

	;
operator_stmt: TOK_OPERATOR {operator = yylval.addMode;}
	;

%%


void fillValues()
{
	switch(storageLine.opcode)
	{
		case 0:
			storageLine.secondDigit = storageLine.firstDigit;
			storageLine.secondAddMode = storageLine.firstAddMode;
			storageLine.firstDigit = 0;
			storageLine.firstAddMode = 1;
			break;
		default:
			storageLine.secondDigit = 0;
			storageLine.secondAddMode = 1;
			break;
	}
}

uint64 readOpcode(char* opcodeStr)
{
	uint64 opcode = 0;
	
	for(int i = 0; opcodeStr[i]; i++){
 	 	opcodeStr[i] = tolower(opcodeStr[i]);
	}
	
	if(strcmp(opcodeStr, "dat") == 0)
		opcode = 0;
	else if(strcmp(opcodeStr, "mov") == 0)
		opcode = 1;
	else if(strcmp(opcodeStr, "add") == 0)
		opcode = 2;
	else if(strcmp(opcodeStr, "sub") == 0)
		opcode = 3;
	else if(strcmp(opcodeStr, "mul") == 0)
		opcode = 4;
	else if(strcmp(opcodeStr, "div") == 0)
		opcode = 5;
	else if(strcmp(opcodeStr, "mod") == 0)
		opcode = 6;
	else if(strcmp(opcodeStr, "jmp") == 0)
		opcode = 7;
	else if(strcmp(opcodeStr, "jmz") == 0)
		opcode = 8;
	else if(strcmp(opcodeStr, "jmn") == 0)
		opcode = 9;
	else if(strcmp(opcodeStr, "djn") == 0)
		opcode = 10;
	else if(strcmp(opcodeStr, "seq") == 0)
		opcode = 11;
	else if(strcmp(opcodeStr, "sne") == 0)
		opcode = 12;
	else if(strcmp(opcodeStr, "slt") == 0)
		opcode = 14;
	else if(strcmp(opcodeStr, "clr") == 0)
		opcode = 15;
	else if(strcmp(opcodeStr, "frk") == 0)
		opcode = 16;
	else if(strcmp(opcodeStr, "nop") == 0)
		opcode = 17;
	else if(strcmp(opcodeStr, "rnd") == 0)
		opcode = 18;
		
	return opcode;
}

void changeNegativeVal()
{	
	tempDigit = 8192 + tempDigit;
}

void makeBinaryInstruction()
{

	storageLine.binaryInstruction = storageLine.secondDigit;
    storageLine.secondAddMode = storageLine.secondAddMode << 25;
    storageLine.firstDigit = storageLine.firstDigit << 29;
    storageLine.firstAddMode = storageLine.firstAddMode << 54;
    storageLine.opcode = storageLine.opcode << 58;
    storageLine.binaryInstruction = ((((storageLine.binaryInstruction | storageLine.secondAddMode) | storageLine.firstDigit) | storageLine.firstAddMode) | storageLine.opcode);
}

void writeCodemonFile()
{
	FILE *fp1 = fopen(filePathSend, "w");
	fwrite(&storageLine.begin, 1, sizeof(uint32), fp1);
	
	for(int i = 0; i < lines; i++)
	{

		fwrite(&codemonProgram[i], 1, sizeof(uint64), fp1);
	}
	lines = 0;
	fclose(fp1);
}

int evalExpression()
{
	if(operator == '+')
		tempDigit = tempDigit + tempDigitTwo;
	else if(operator == '-')
		tempDigit = tempDigit - tempDigitTwo;
	else if(operator == '*')
		tempDigit = tempDigit * tempDigitTwo;
	else if(operator == '/')
		tempDigit = tempDigit / tempDigitTwo;
	else if(operator == '%')
		tempDigit = tempDigit % tempDigitTwo;	

	return 1;
}

int evalLabel(char * label)
{
	int index = 0;

	while(index < 10) 
	{
		
		if(labeList[index] == NULL)
		{
			index++;
		}
		else
		{
			if(strcmp(labeList[index], label) == 0)
			{
				return index - lines;
			}
			else
				index++;
		}
	}

	return -9999;
}

void makeBegin(int beginVal)
{
	storageLine.begin = beginVal;
}

uint64 readAddressMode(char numVal)
{
    uint64 mode = 0;
    
    switch(numVal)
    {
        case '#':
            mode = 1;
            break;
        case '[':
            mode = 2;
            break;
        case ']':
            mode = 3;
            break;
        case '*':
            mode = 4;
            break;
        case '@':
            mode = 5;
            break;
        case '{':
            mode = 6;
            break;
        case '}':
            mode = 7;
            break;
        case '<':
            mode = 8;
            break;
        case '>':
            mode = 9;
            break;
        default:
            break;
    }
    
    return mode;
}

