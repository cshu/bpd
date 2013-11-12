//note although invoking sqlite3_column_blob() followed by sqlite3_column_bytes() is recommended, you can invert the order of invocation
//as long as your sqlite3_column_blob doesn't cause any conversion
//you can find examples of such invocations in SQLite source code and "The Definitive Guide to SQLite" by Mike Owens



#define ISRETVALOFSQLITEEXECERR retintbuf

static int slBindBlob(sqlite3_stmt *pStmt, int i, const void *zData, int nData, void (*xDel)(void*)){
    if(nData)
        return sqlite3_bind_blob(pStmt, i, zData, nData, xDel);
    else
        return sqlite3_bind_zeroblob(pStmt, i, -1);
}

static char *ucaseConvLuToAlphanum(long unsigned int l){
    long unsigned int sum = 36;
    for (long unsigned int len = 1;;) {
        if(l<sum){
            char *str = base36enc(l);
            long unsigned int diff = len-strlen(str);
            if(!diff)
                return str;
            char *valtoret = griddoor_malloc(len+1);
            if(!valtoret){
                griddoor_free(str);
                return NULL;
            }
            strcpy(valtoret+diff,str);
            griddoor_free(str);
            return memset(valtoret, '0', diff);
        }else{
            l-=sum;
            sum*=36;
            len++;
        }

    }
}

static void lcaseAlphanumIncrement(unsigned char *restrict uc, uint_least32_t *restrict size){
    int lastInd = *size-1;
    begin:
    if(uc[lastInd]=='9')
        uc[lastInd] = 'a';
    else if(uc[lastInd]=='z'){
        uc[lastInd] = '0';
        if(lastInd){
            lastInd--;
            goto begin;
        }
        uc[*size] = '0';
        ++*size;
    }else
        uc[lastInd]++;
}

static void tableOrColNameIncrement(unsigned char *restrict uc, int *restrict size) {
    if (*size == 1) {
        if (uc[0] == 'z') {
            uc[0] = 'a';
            uc[1] = '0';
            *size = 2;
        } else {
            uc[0]++;
        }
    } else if (*size == 2) {
        if (uc[1] == '9') {
            uc[1] = '0';
            if (uc[0] == 'z') {
                uc[0] = 'a';
                uc[2] = '0';
                *size = 3;
            } else {
                uc[0]++;
            }
        } else {
            uc[1]++;
        }
    } else {
        int lastInd = *size - 1;
        while (1) {
            if (uc[lastInd] == 'z') {
                uc[lastInd] = '0';
                lastInd--;
                if (lastInd == 1) {
                    if (uc[1] == '9') {
                        uc[1] = '0';
                        if (uc[0] == 'z') {
                            uc[0] = 'a';
                            uc[*size] = '0';
                            ++*size;
                        } else {
                            uc[0]++;
                        }
                    } else {
                        uc[1]++;
                    }
                    return;
                }
            } else if (uc[lastInd] == '9') {
                uc[lastInd] = 'a';
                return;
            } else {
                uc[lastInd]++;
                return;
            }
        }
    }
}
