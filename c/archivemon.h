
#undef __STRICT_ANSI__
#include <stdio.h>
#include <string.h>
#include <stdlib.h>
#include <time.h>
#include <io.h>

#define GETCHARTILLLFs(cbuf,ifchar,ifnotchar) cbuf=getchar();if(cbuf=='\n'){ifnotchar}else if(getchar()=='\n'){ifchar}else{while(getchar()!='\n'){}ifnotchar}

#define MEMCMPBACKWARDs(s1,s2,nminus1,caseGt,caseEq,caseLt) for(int i=nminus1;;i--){if(i<0){caseEq break;}if(((unsigned char*)(s1))[i]>((unsigned char*)(s2))[i]){caseGt break;}else if(((unsigned char*)s1)[i]<((unsigned char*)s2)[i]){caseLt break;}}
