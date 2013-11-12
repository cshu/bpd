
#include <stdio.h>
#include <string.h>
#include <stdlib.h>
#include <ctype.h>

//dir1 dir2 filename diffTool
//it will see if filename is under dir1 or dir2, and then find its counterpart, and then launch "diffTool filename counterpart"
//dir1 and dir2 must not contain space (which makes enclosing double quotes necessary, double quotes makes matching string more troublesome)


#define commandpromptmaxstrlen_win2k 2047
static char fnmbuf[commandpromptmaxstrlen_win2k];
int main(int argc, char **argv) {
  if(argc!=5)return 1;
  fnmbuf[0]='"';fnmbuf[1]='"';
  strcpy(fnmbuf+2,argv[4]);
  strcat(fnmbuf, "\" ");
  strcat(fnmbuf, argv[3]);
  strcat(fnmbuf, " ");

  size_t i=0;
  for(;argv[1][i]!=0;i++)
    argv[1][i]=tolower((unsigned char)argv[1][i]);
  size_t si0=i;
  i=0;
  for(;argv[2][i]!=0;i++)
    argv[2][i]=tolower((unsigned char)argv[2][i]);
  size_t si1=i;
  i=0;
  for(;argv[3][i]!=0;i++)
    argv[3][i]=tolower((unsigned char)argv[3][i]);

  if(si0<i && memcmp(argv[1],argv[3],si0)==0){
    strcat(fnmbuf,argv[2]);
    strcat(fnmbuf,argv[3]+si0);
    strcat(fnmbuf,"\"");
    system(fnmbuf);
    return 0;
  }else if(si1<i && memcmp(argv[2],argv[3],si1)==0){
    strcat(fnmbuf,argv[1]);
    strcat(fnmbuf,argv[3]+si1);
    strcat(fnmbuf,"\"");
    system(fnmbuf);
    return 0;
  }
  return 1;
}
