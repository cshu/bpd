#pragma once

#include <stdio.h>

#define READWHOLEFILEs(str,longSiz,caseFopenReturnsNull,caseEmptyFile,caseMallocFail) \
{\
    FILE *filetoread = fopen(filename,"rb");\
    if(filetoread){\
        fseek(filetoread,0,SEEK_END);\
        longSiz = ftell(filetoread);\
        if(longSiz){\
            str = malloc(longSiz);\
            if(str){\
                fseek(filetoread, 0, SEEK_SET);\
                fread(str,longSiz,1,filetoread);\
                fclose(filetoread);\
            }else{\
                caseMallocFail\
            }\
        }else{\
            caseEmptyFile\
        }\
    }else{\
        caseFopenReturnsNull\
    }\
}

