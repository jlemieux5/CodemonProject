#include "functions.h"
#include "arch.h"
#include "common.h"
#include <jni.h>
#include "codemon.h"
#include "y.tab.h"
extern FILE* yyin;
extern char* labeList[50];
int mode = 0;
char *filePathSend;

int yyparse(int* flag);

JNIEXPORT jint JNICALL Java_Codemon_parseCodemon
  (JNIEnv *env, jclass classA, jstring fileName, jstring filePath)
{
    const char *cfileName = (*env)->GetStringUTFChars(env, fileName, NULL);
    const char *cfilePath = (*env)->GetStringUTFChars(env, filePath, NULL);
    int parseWork = assembleCodemon((char*)cfileName, (char*)cfilePath);
    (*env)->ReleaseStringUTFChars(env, fileName, cfileName);
    (*env)->ReleaseStringUTFChars(env, filePath, cfilePath);
    return parseWork;
}

JNIEXPORT jint JNICALL Java_Codemon_runTestCodemon
  (JNIEnv *env, jclass classA, jstring fileName, jint turnLimit)
{
    int reportID = 0;
    const char *cfileName = (*env)->GetStringUTFChars(env, fileName, NULL);
    reportID = runTestMode((char*)cfileName, turnLimit);
    (*env)->ReleaseStringUTFChars(env, fileName, cfileName);

    return reportID;
}

JNIEXPORT jint JNICALL Java_Codemon_runSelfCodemon
  (JNIEnv *env, jclass classA, jstring fileName, jstring fileNameTwo, jint turnLimit)
{
    int reportID = 0;
    const char *cfileName = (*env)->GetStringUTFChars(env, fileName, NULL);
    const char *cfileNameTwo = (*env)->GetStringUTFChars(env, fileNameTwo, NULL);
    reportID = runSelfMode((char*)cfileName, (char*)cfileNameTwo, turnLimit);
    (*env)->ReleaseStringUTFChars(env, fileName, cfileName);
    (*env)->ReleaseStringUTFChars(env, fileNameTwo, cfileNameTwo);

    return reportID;
}

JNIEXPORT jint JNICALL Java_Codemon_runPVPCodemon
  (JNIEnv *env, jclass classA, jstring fileName, jint pvpMode)
{
    int reportID = 0;
    const char *cfileName = (*env)->GetStringUTFChars(env, fileName, NULL);
    reportID = runPVPMode((char*)cfileName, pvpMode);
    (*env)->ReleaseStringUTFChars(env, fileName, cfileName);

    return reportID;
}

JNIEXPORT jint JNICALL Java_Codemon_getReport
  (JNIEnv * env, jclass classA, jint reportid, jstring reportName)
{
    const char *cfileName = (*env)->GetStringUTFChars(env, reportName, NULL);
    int reportSuccess = getTheReport(reportid, (char*)cfileName);
    (*env)->ReleaseStringUTFChars(env, reportName, cfileName);
    return reportSuccess;
}


int assembleCodemon(char * fileName, char * filePath)
{
    int parseTest = 0;
     /* Calls yyparse twice, first time to define labels and second time for the rest of the file*/
    if(fileName == NULL)
    {
        return 1;
    }
    filePathSend = filePath;
    FILE* fp = fopen(fileName, "r");
    yyin = fp;
    mode = 0;
    parseTest = yyparse(0);
    fclose(fp);
    fp = fopen(fileName, "r");
    mode = 1;
    parseTest = yyparse(0);

    fclose(fp);
    return parseTest;
}

int runTestMode(char * fileName, int limit)
{
    /* If given -t as an arg then parse through the binary file to fill the codemon with binary instructions and then submit it to the server */
    int reportid = -1;
    int i = 0;
    int *beginPointer = malloc(sizeof(int));
    uint64 *binaryPointer = malloc(sizeof(uint64));
    FILE *fp;
    struct Codemon_pkg firstCodemon;
    char *codemonName;
    char *split = ".";

    fp = fopen(fileName, "r");
    codemonName = strtok(fileName, split);
    firstCodemon.begin = *beginPointer;
    fread(beginPointer, 1, sizeof(int), fp);
        
    while(fread(binaryPointer, 1, sizeof(uint64), fp) == sizeof(uint64))
    {    
        firstCodemon.program[i] = *binaryPointer;
        i++;
    }
                
    firstCodemon.lines = i;
    strcpy(firstCodemon.name, codemonName);
    reportid = runTest(&firstCodemon, NULL, limit);

    fclose(fp);
    free(beginPointer);
    free(binaryPointer);

    return reportid;
}

int runSelfMode(char * fileName, char *fileNameTwo, int limit)
{
    /* If given -s as an arg then parse through the first binary file to fill the first codemon with binary instructions, then parse through the second binary file to
    fill the second codemon and submit both of them to the server. Also test to make sure the turn limit is under MAXTURNS */
    
    int reportid = 0;
    int i = 0;
    int *beginPointer = malloc(sizeof(int));
    uint64 *binaryPointer = malloc(sizeof(uint64));
    FILE *fp;
    struct Codemon_pkg firstCodemon;
    struct Codemon_pkg secondCodemon;
    char *codemonName;
    char *split = ".";

    fp = fopen(fileName, "r");
    codemonName = strtok(fileName, split);
    firstCodemon.begin = *beginPointer;
    fread(beginPointer, 1, sizeof(int), fp);
    
    while(fread(binaryPointer, 1, sizeof(uint64), fp) == sizeof(uint64))
    {    
        firstCodemon.program[i] = *binaryPointer;
        i++;
    }
    
    
    firstCodemon.lines = i;
    strcpy(firstCodemon.name, codemonName);
    fclose(fp);
    
    i = 0;
    fp = fopen(fileNameTwo, "r");
    codemonName = strtok(fileNameTwo, split);
    secondCodemon.begin = *beginPointer;
    fread(beginPointer, 1, sizeof(int), fp);
    
    while(fread(binaryPointer, 1, sizeof(uint64), fp) == sizeof(uint64))
    {    
        secondCodemon.program[i] = *binaryPointer;
        i++;
    }
            
    secondCodemon.lines = i;
    strcpy(secondCodemon.name, codemonName);
    fclose(fp);
    
    reportid = runTest(&firstCodemon, &secondCodemon, limit);

    free(beginPointer);
    free(binaryPointer);

    return reportid;
}

int runPVPMode(char * fileName, int pvpMode)
{
    /* If given -p as an arg then parse through the binary file to fill the codemon with binary instructions and then submit it to the server */
    int reportid = 0;
    int i = 0;
    int *beginPointer = malloc(sizeof(int));
    uint64 *binaryPointer = malloc(sizeof(uint64));
    FILE *fp;
    struct Codemon_pkg firstCodemon;
    char *codemonName;
    char *split = ".";

    fp = fopen(fileName, "r");
    codemonName = strtok(fileName, split);
    firstCodemon.begin = *beginPointer;
    fread(beginPointer, 1, sizeof(int), fp);
    
    while(fread(binaryPointer, 1, sizeof(uint64), fp) == sizeof(uint64))
    {    
        firstCodemon.program[i] = *binaryPointer;
        i++;
    }
            
    firstCodemon.lines = i;
    strcpy(firstCodemon.name, codemonName);
    reportid = runPvP(&firstCodemon, pvpMode);

    fclose(fp);
    free(beginPointer);
    free(binaryPointer);

    return reportid;
}

int getTheReport(int reportID, char * fileName)
{
    FILE * fp = fopen(fileName, "ab+");
    int reportSuccess = getReport(reportID, fp); 
    fclose(fp);
    return reportSuccess;
}






















 