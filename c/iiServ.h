

#include "iiPhysical.h"

#undef _WIN32_WINNT
#define _WIN32_WINNT _WIN32_WINNT_WINXP

#include "sapi.h"

static SOCKET ClientSocket = INVALID_SOCKET;

static int StartLoading(void) {
    HANDLE hFile;
    hFile = CreateFile(dbfullfilename, GENERIC_READ, FILE_SHARE_READ, NULL, OPEN_EXISTING, 0, NULL);
    if (hFile == INVALID_HANDLE_VALUE) {
        LOGLITERALs("CreateFile returned INVALID_HANDLE_VALUE")
        return RETVALFAILURE;
    }
    LARGE_INTEGER li;
    if (GetFileSizeEx(hFile, &li)) {
    } else {
        LOGLITERALs("GetFileSizeEx failed")
        CloseHandle(hFile);
        return RETVALFAILURE;
    }
    FILETIME ftCreate, ftAccess, ftWrite;
    if (GetFileTime(hFile, &ftCreate, &ftAccess, &ftWrite)) {
        CloseHandle(hFile);
    } else {
        LOGLITERALs("GetFileTime failed")
        CloseHandle(hFile);
        return RETVALFAILURE;
    }

    FILE *sizeAndWTFile = fopen(dbsizentimefile, "rb");
    if (!sizeAndWTFile) {
        LOGLITERALs("fopen failed")
        return RETVALFAILURE;
    }
    LARGE_INTEGER readLi;
    fread(&readLi, 8, 1, sizeAndWTFile);
    FILETIME ftWriteRead;
    fread(&ftWriteRead, 8, 1, sizeAndWTFile);
    fclose(sizeAndWTFile);
    if (readLi.QuadPart != li.QuadPart) {
        LOGLITERALs("size is not consistent")
        return RETVALFAILURE;
    }
    if (ftWriteRead.dwHighDateTime != ftWrite.dwHighDateTime || ftWriteRead.dwLowDateTime != ftWrite.dwLowDateTime) {
        LOGLITERALs("write time is not consistent")
        return RETVALFAILURE;
    }

    sqliteConfigOpenPragmaBeginTran();
    //todo: check
    return RETVALSUCCESS;
}

static int UpdateAfterStop(void) {
    HANDLE hFile;
    hFile = CreateFile(dbfullfilename, GENERIC_READ, FILE_SHARE_READ, NULL, OPEN_EXISTING, 0, NULL);
    if (hFile == INVALID_HANDLE_VALUE) {
        LOGLITERALs("CreateFile returned INVALID_HANDLE_VALUE")
        return RETVALFAILURE;
    }
    LARGE_INTEGER li;
    if (GetFileSizeEx(hFile, &li)) {
    } else {
        LOGLITERALs("GetFileSizeEx failed")
        CloseHandle(hFile);
        return RETVALFAILURE;
    }
    FILETIME ftCreate, ftAccess, ftWrite;
    if (GetFileTime(hFile, &ftCreate, &ftAccess, &ftWrite)) {
        CloseHandle(hFile);
    } else {
        LOGLITERALs("GetFileTime failed")
        CloseHandle(hFile);
        return RETVALFAILURE;
    }

    FILE *sizeAndWTFile = fopen(dbsizentimefile, "wb");
    if (!sizeAndWTFile) {
        LOGLITERALs("fopen failed")
        return RETVALFAILURE;
    }
    fwrite(&li, 8, 1, sizeAndWTFile);
    fwrite(&ftWrite, 8, 1, sizeAndWTFile);
    fclose(sizeAndWTFile);//undone check return value!!!!
    return RETVALSUCCESS;
}

static void hideConsWin(void){
    ShowWindow(GetConsoleWindow(), SW_HIDE);
}

static void showConsWin(void){
    ShowWindow(GetConsoleWindow(), SW_SHOW);
}

static int sendUntilBytes(const void *bytesToSend, size_t bytesLen) {
    do {
        int retintbuf = send(ClientSocket, bytesToSend, bytesLen, 0);
        if (retintbuf == SOCKET_ERROR) {
            retintbuf = WSAGetLastError();
            LOGERRs
            closesocket(ClientSocket);
            WSACleanup();
            return RETVALFAILURE;
        }
        bytesToSend = (const char *)bytesToSend + retintbuf;
        bytesLen -= retintbuf;
    } while (bytesLen > 0);
    return RETVALSUCCESS;
}

static int recvUntilBytes(void *recvbuf, size_t bytesLenToRecv) {
    do {
        int retintbuf = recv(ClientSocket, recvbuf, bytesLenToRecv, 0);
        if (retintbuf == SOCKET_ERROR) {
            retintbuf = WSAGetLastError();
            LOGERRs
            closesocket(ClientSocket);
            WSACleanup();
            return RETVALFAILURE;
        }
        recvbuf = (char *)recvbuf + retintbuf;
        bytesLenToRecv -= retintbuf;
    } while (bytesLenToRecv);
    return RETVALSUCCESS;
}
