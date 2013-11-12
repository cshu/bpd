#pragma once

//note macro ending with s means the macro has sequence point, doesn't need to add semicolon after it, and probably cannot be assigned as a right value.
#define XSTR(s) STR(s)
#define STR(s) #s
#define COMMA ,

#define THIRTYONEEXPLICITNULLITERAL "\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0"

#define GETBITATINDEX(integertypval,ind) (integertypval & (1 << ind))

#define RETVALSUCCESS 0
#define RETVALFAILURE 1
#define RETVALNOOP 2
#define RETVALMEMALLOCFAIL 3
#define RETVALINVALIDSTATE 4

#define RETVALTAUTOLOGY 252
#define RETVALOXYMORON 253
#define RETVALNOTFOUND 254
#define ISRETVALERR retintbuf


#include <stdlib.h>
#include <stdint.h>
#include <stddef.h>
#include <string.h>

#define ARRSIZE(arr) ((sizeof arr) / (sizeof *arr))
#define ARRWITHSIZE(arr) arr, ARRSIZE(arr)
#define LITERALcommaSIZE(str) str,sizeof str
#define LITERALcommaLEN(str) str,sizeof str-1

#define MEMCPYnADVANCEs(s1,s2,n) {size_t st_MEMCPYNADVANCEs = n;memcpy(s1,s2,st_MEMCPYNADVANCEs);s1+=st_MEMCPYNADVANCEs;}
#define MEMCPYLITERALnADVANCEs(s1,s2) {memcpy(s1,s2,sizeof s2-1);s1+=sizeof s2-1;}
#define MEMCPYLITERALNULnADVANCEs(s1,s2) {memcpy(s1,s2,sizeof s2);s1+=sizeof s2;}

#define MEMCPYifLEN(d,s,n) (n?memcpy(d,s,n):d)
#define MEMCMPifLEN(x,y,n) (n?memcmp(x,y,n):0)

#define STRFTIMES15MAX(buf) strftime(buf,15,"%Y%m%d%H%M%S",gmtime(&(time_t){time(NULL)}))
#define DIFFTIMENOW0 difftime(time(NULL),(time_t)0))

static void *griddoor_malloc(size_t size){
    return malloc(size);
}

static void griddoor_free(void *ptr){
    free(ptr);
}

struct pfunc{
    int (*pf)(void *);
    void *params;//this might point to a struct, or simply objects
    int *retval;
};


struct darraylist{
    void *obj;
    size_t size;
    size_t cap;
    size_t (*lengthenCapSuggest)(struct darraylist *);
    uint_least32_t lengthenParams;
};

#define DARR_ADD(ptype,darr,val)\
do{\
    if(darr->cap - darr->size<sizeof(ptype)){\
        size_t newCap = (darr->lengthenCapSuggest)(darr);\
        if(newCap < sizeof(ptype) + darr->size)\
            newCap = sizeof(ptype) + darr->size;\
        void *newObj = realloc(darr->obj, newCap);\
        if(newObj){\
            darr->obj=newObj;\
            darr->cap=newCap;\
        }else{\
            retintbuf = RETVALMEMALLOCFAIL;\
            break;\
        }\
    }\
    *(ptype)((unsigned char *)darr+darr->size)=val;\
    darr->size+=sizeof(ptype);\
    retintbuf = RETVALSUCCESS;\
}while(0);

static int darr_memcpy(struct darraylist *darr, const void *restrict s, size_t n){
    if(darr->cap - darr->size<n){
        size_t newCap = (darr->lengthenCapSuggest)(darr);
        if(newCap < n + darr->size)
            newCap = n + darr->size;
        void *newObj = realloc(darr->obj, newCap);
        if(newObj){
            darr->obj=newObj;
            darr->cap=newCap;
        }else
            return RETVALMEMALLOCFAIL;
    }
    memcpy((unsigned char *)darr+darr->size, s, n);
    darr->size+=n;
    return RETVALSUCCESS;
}

static int darr_ensureCapacity(struct darraylist *darr, size_t leastCap){
    if(darr->cap<leastCap){
        size_t newCap = (darr->lengthenCapSuggest)(darr);
        if(newCap<leastCap)
            newCap = leastCap;
        void *newObj = realloc(darr->obj, newCap);
        if(newObj){
            darr->obj=newObj;
            darr->cap=newCap;
        }else
            return RETVALMEMALLOCFAIL;
    }
    return RETVALSUCCESS;
}

static int darr_reallocWithCapacity(struct darraylist *darr, size_t newCap){
    void *newObj = realloc(darr->obj, newCap);
    if(newObj){
        darr->obj=newObj;
        darr->cap=newCap;
        return RETVALSUCCESS;
    }else
        return RETVALMEMALLOCFAIL;
}

static int darr_trimToSize(struct darraylist *darr){
    if(darr->cap>darr->size){
        void *newObj = realloc(darr->obj, darr->size);
        if(newObj){
            darr->obj=newObj;
            darr->cap=darr->size;
        }else
            return RETVALMEMALLOCFAIL;
    }
    return RETVALSUCCESS;
}

//note lengthenParams high-order part must be greater than low-order part which mustn't be zero
static size_t darr_lengthenDividedTimes(struct darraylist *darr){
    return (darr->cap / (darr->lengthenParams & 0xFFFF) + 1) * (darr->lengthenParams >> 16 & 0xFFFF);
}

static size_t darr_lengthenAdd(struct darraylist *darr){
    return darr->cap + darr->lengthenParams;
}

static size_t darr_lengthenTimesTwo(struct darraylist *darr){
    return darr->cap*2;
}

static char* gd_strdup(const char *s1){
    char *valtoret = griddoor_malloc(strlen(s1)+1);
    if(!valtoret)
        return NULL;
    return strcpy(valtoret,s1);
}
