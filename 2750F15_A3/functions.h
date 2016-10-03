#include <stdio.h>
#include <stdlib.h>
#include <ctype.h>
#include <string.h>
#include "arch.h"
#include "common.h"
#include <stdio.h>
#include <stdlib.h>
#include <ctype.h>
#include <string.h>
#include <jni.h>

typedef struct commandList
{
    int commandNumber;
    int hasLabel;
    uint32 begin;
    uint64 opcode;
    uint64 firstNum;
    uint64 firstAddressMode;
    uint64 secondNum;
    uint64 secondAddressMode;
    uint64 binaryInstruction;
    char label[10];
    struct commandList *next;
}commandList;


int assembleCodemon(char * fileName, char * filePath);

int runTestMode(char * fileName, int limit);

int runSelfMode(char * fileName, char *fileNameTwo, int limit);

int runPVPMode(char * fileName, int pvpMode);

int getTheReport(int reportID, char *fileName);

/* Parses a input file past all white space and newlines, returns FILE pointer to next value that isn't whitespace */
char readWhiteSpace(FILE* fp);

/* Parses a input file to the next line when a comment indicator is detected(!) */
FILE* readComment(FILE* fp);

/* Tests to see if the opcode given is valid, if it is, returns the value for that opcode (in decimal) , if not a valid opcode returns -1*/
uint64 testOpcode(FILE* fp, char opcodeVal);

/* Takes a struct that holds all commands and a string, then searches for that specific label in all instructions. Returns the instruction number if the label is found, if not returns -1 */
int findLabel(commandList * command, char label[]);

/* Takes a file pointer pointed at a number and returns the number(if number is negative it will fix that number) */
uint64 readVal(FILE* fp, char numVal);

/* Takes a character that should be an address mode and returns the proper value for the address mode given */
uint64 readAddressMode(FILE* fp, char numVal);

/* Parses through the file to find all labels and their instruction number and stores them into a structure which it returns*/
commandList * labelParse(char * fileName, char *filePath);

/* Creates a commandList linked list (struct to store instructions)*/
commandList * createCommandList();

/* Adds a command to the linked list with the command number , if it has a label and the label itself */
void addCommandToList(commandList * command, int commandNumber, char label[], int hasLabel);

/* Parses through the file to store all required values for a codemon in a struct called commandList*/
commandList * fullParse(char * fileName, char * filePath);

/* Checks to see if a instruction number has a label, returns 1 if it does and 0 if not*/
int checkLabel(commandList * command, int commandNumber);

/* Add all the values gathered in decimal in the parsing step to the linked list that stores all values*/
void addValuesToCommand(commandList * command, int commandCount, uint64 opcode, uint64 firstmode, uint64 firstVal, uint64 secondMode, uint64 secondVal);

/* Tests all the opcodes provided to make sure they do not have invalid addressing modes */
void testOpcodeMode(commandList * command);

/* Takes all values gathered in parsing and combines them together to make a binary command and then fwrites them to stdout*/
void makeBinaryCommand(commandList * command, char *filePath);

/* Takes a label and returns the instruction number if a label was found, returns -1 if no label was found */
int findLabel(commandList *command, char label[]);

/* Gets the amount of instructions from the codemon and returns that number */
int instructionAmount(commandList * command);

/* Takes a commandList and frees all memory associated with it */
int CLDestroy(commandList *cl);