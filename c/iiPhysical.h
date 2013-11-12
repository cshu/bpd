
#include <stdio.h>
#include <time.h>

static int globalint0;
static unsigned char pucBuf24[0xFFFFFF];

//todo remove?
//static const char scChOne = 1;
//static const char scChZero = 0;

static void hideConsWin(void);
static void showConsWin(void);
static int sendUntilBytes(const void *bytesToSend, size_t bytesLen);
static int recvUntilBytes(void *recvbuf, size_t bytesLenToRecv);

static unsigned char precvuc[3];
static int recvUnsigned24(uint_least32_t *punsigned){
    int retintbuf;
    retintbuf = recvUntilBytes(precvuc, 3);
    if(ISRETVALERR)
        return retintbuf;
    *punsigned = precvuc[0]+precvuc[1]*256+precvuc[2]*65536;
    return RETVALSUCCESS;
}
static int recvWithUcBuf24(uint_least32_t *plen){
    int retintbuf;
    retintbuf = recvUnsigned24(plen);
    if(ISRETVALERR)
        return retintbuf;
    if(*plen)
        return recvUntilBytes(pucBuf24, *plen);
    return RETVALSUCCESS;
}

static int sendLen24(const void *bytesToSend, uint_least32_t len){
    int retintbuf;
    precvuc[0] = len%256;
    precvuc[1] = (len/256)%256;
    precvuc[2] = len/65536;
    retintbuf = sendUntilBytes(precvuc, 3);
    if(ISRETVALERR)
        return retintbuf;
    return sendUntilBytes(bytesToSend, len);
}
static int sendLen24WithLenChk(const void *bytesToSend, uint_least32_t len){
    if(len)
        return sendLen24(bytesToSend, len);
    else
        return sendUntilBytes(SEVENEXPLICITNULLITERAL, 3);
}
#define SENDLEN24WITHLENCHKs(bytesToSend, len) {if(len){retintbuf=sendLen24(bytesToSend, len);}else{retintbuf=sendUntilBytes(SEVENEXPLICITNULLITERAL, 3);}}

#define RECVWITHVAR(v) recvUntilBytes(&v, sizeof v)

#define SENDWITHVAR(v) sendUntilBytes(&v, sizeof v)


#include "sqliteUtility.h"

static char tmbuf[15];

#define LOGLITERALs(lit)\
{showConsWin();\
STRFTIMES15MAX(tmbuf);puts(tmbuf);\
fputs("ERROR: " __FILE__ ",", stdout);\
fputs(__func__, stdout);\
puts("," XSTR(__LINE__) ". LITE: " lit);}

#define LOGERRs \
{showConsWin();\
STRFTIMES15MAX(tmbuf);puts(tmbuf);\
fputs("ERROR: " __FILE__ ",", stdout);\
fputs(__func__, stdout);\
fputs("," XSTR(__LINE__) ". CODE: ", stdout);\
printf("%d", retintbuf);\
putchar('\n');}

#define LOGMESSAGEs(mes)\
{showConsWin();\
STRFTIMES15MAX(tmbuf);puts(tmbuf);\
fputs("ERROR: " __FILE__ ",", stdout);\
fputs(__func__, stdout);\
fputs("," XSTR(__LINE__) ". MESS: ", stdout);\
puts(mes);}

//note checking return value of sqlite3_finalize might be unnecessary according to documentation. but still check it for safety.
#define SLFINALIZEs(stmt)\
{retintbuf = sqlite3_finalize(stmt);\
if(retintbuf != SQLITE_OK){\
    LOGERRs\
}}

#define IF_ISRETVALERR_LOGNRETURNs \
if(ISRETVALERR){\
    LOGERRs\
    return retintbuf;\
}

#define IF_ISRETVALERR_LOGNFINALIZENRETURNs \
if(ISRETVALERR){\
    LOGERRs\
    SLFINALIZEs(pssbuf)\
    return retintbuf;\
}

#define IFNOT_SQLITE_OK_LOGNRETURNs \
if (retintbuf != SQLITE_OK) {\
    LOGERRs\
    return RETVALFAILURE;\
}

#define IFNOT_SQLITE_OK_LOGNFINALIZENRETURNs \
if (retintbuf != SQLITE_OK) {\
    LOGERRs\
    SLFINALIZEs(pssbuf)\
    return RETVALFAILURE;\
}



//NOTE if your query is a readonly query like selecting, then you can use SQLITE_STATIC for binding as long as you don't change the value before finalizing
//if your query update something in db, then always use SQLITE_TRANSIENT for binding unless you are binding constant objects like string literal
//for safety, sqlite documentation is ambiguous sometimes
//BUT WAIT!!! maybe you should always use SQLITE_TRANSIENT unless you are binding constant object??? E.g.: the same static char array is passed to sqlite over and over, when sqlite realizes the address is the same as the last one, it assumes that the value to bind is still the same value??!!
