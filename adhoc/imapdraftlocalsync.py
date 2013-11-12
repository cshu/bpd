

#undone add special SUBJECTs list, if the SUBJECT is not found, fails.
#for special SUBJECTs, lauch merge tool, and block until merge tool terminates

#undone!! change the argv to constants instead!!! your sensitive strings can be seen from Window Title!!!!
FILENM_INITIAL_LIMIT = 90

import re
import email
import os
import imaplib
import sys
import struct

if len(sys.argv)!=6:
    print("usage: svr addr passwd path mode")
    sys.exit(0)
os.chdir(sys.argv[4])
print('Mode: '+sys.argv[5])
timesi = {}
with open('time-si','rb') as iobw:#exception raised if it doesn't exist
    filebytes = iobw.read()
off = 0;
while len(filebytes)>off:
    fnmlen = struct.unpack('H',filebytes[off:off+2])[0]
    off+=2;
    fnm = filebytes[off:off+fnmlen];
    off+=fnmlen;
    timesi[fnm] = filebytes[off:off+16];
    off+=16;
#print('reading local files...');
locdic={}
for fnm in os.listdir():#note this fnm is str, not bytes!!!
    if os.path.isfile(fnm):
        if fnm=='time-si':
            continue;
        ftimesi = struct.pack('d',os.path.getmtime(fnm))+struct.pack('q',os.path.getsize(fnm));
        if fnm.encode() not in timesi:
            if ''!=input(b'new file at local, type any character(s) to abort, filename: '+fnm.encode()):
                sys.exit(0)
        else:
            if timesi[fnm.encode()]!=ftimesi:
                if ''!=input(b'time or size change found, type any character(s) to abort, filename: '+fnm.encode()):
                    sys.exit(0)
            del timesi[fnm.encode()]
        with open(fnm,'rb') as iobw:
            filebytes = iobw.read()
        subjst = filebytes.index(b' ')#note sometimes subject contains \n (though you cannot see in on mail.google.com), so you include the num of lines in subject at the beginning of the file
        inumoflines = int(filebytes[:subjst])
        subjst+=1
        subjends=subjst
        while True:
            subjends = filebytes.index(b'\n',subjends)
            inumoflines-=1
            if inumoflines==0: break;
            subjends+=1
        #last \n or \r\n is the separator between subj and payload
        payloadst = subjends+1
        if filebytes[subjends-1]==ord('\r'):
            subjends -= 1
        if subjends<=subjst:
            print('loc subject is empty: ',fnm.encode());
            sys.exit(0)
        subj = filebytes[subjst:subjends]
        if subj.isspace():
            print('loc subject is spaces: ',fnm.encode());
            sys.exit(0)
        if subj in locdic:
            print("loc duplicate subject:",subj)
            sys.exit(0)
        locdic[subj]=(filebytes[payloadst:],fnm.encode(),ftimesi)
        #doesn't check payload bc empty payload is allowed?
    else:
        print("loc Not a file:",fnm.encode())
        sys.exit(0)
if len(timesi):
    print('recorded file(s) are missing:')
    for k in timesi.keys():
        print(k)
    if ''!=input('type any character(s) to abort:'):
        sys.exit(0)
#print('reading server drafts...');
svrdic={}
conn = imaplib.IMAP4_SSL(sys.argv[1], port = 993)
conn.login(sys.argv[2],sys.argv[3])
conn.select('[Gmail]/Drafts')
(retcode, messages) = conn.search(None, "ALL")
for num in messages[0].split():
    rv, data = conn.fetch(num, '(RFC822)')
    if rv != 'OK':
        print("rv is not 'OK'!! its value is:",rv)
        sys.exit(0)
    msg = email.message_from_bytes(data[0][1])
    decode = email.header.decode_header(msg['Subject'])[0]
    subj = decode[0]
    if type(subj) is str:
        subj = subj.encode()
    elif type(subj) is not bytes:
        print("unsupported type of subject:",str(type(subj)))
        sys.exit(0)
    if not subj or subj.isspace():
        print("svr subject is emtpy or spaces:",subj)
        sys.exit(0)
    if subj in svrdic:
        print("svr duplicate subject:",subj)
        sys.exit(0)
    svrdic[subj]=msg.get_payload(decode=True)

if sys.argv[5]=='dnload':
    conn.close()
    conn.logout()
    tstobewritten = []
    filestodel = []
    filestowri = []
    for s,l in locdic.items():
        if s in svrdic:
            if svrdic[s]==l[0]:
                tstobewritten.append(struct.pack('H',len(l[1])))
                tstobewritten.append(l[1])
                tstobewritten.append(l[2])
            else:
                filestowri.append((s,svrdic[s],l[1]))
                print("to be overwritten at local:",s)
            del svrdic[s]
        else:
            filestodel.append(l[1])
            print("to be deleted: ",s)
    setofsnewfnms = set()
    for s,l in svrdic.items():
        snewfnm = re.sub('[^0-9a-zA-Z]+','_',s.decode())[:FILENM_INITIAL_LIMIT]; snfncopy = snewfnm;
        suffixnum=2;
        while os.path.exists(snewfnm) or snewfnm in setofsnewfnms:
            snewfnm=snfncopy+str(suffixnum)
            suffixnum+=1
        setofsnewfnms.add(snewfnm)
        filestowri.append((s,l,snewfnm.encode()))
        print("new file to be created at local:",s)
    if filestodel or filestowri:
        if ''!=input('type any character(s) to abort:'):
            sys.exit(0)
    else:
        #note if file time is changed somehow, but file content doesn't change, then the time-si file is not refreshed!
        #maybe you should refresh it, to make it up to date?
        print('nothing to download, done.')
        sys.exit(0)

    print('Start changing files!! If it fails after this point then the files are probably corrupted!!')
    for fnm in filestodel:
        os.remove(fnm.decode())
    print('deletion finished')
    with open('time-si','wb') as tsbw:
        for s,l,fnm in filestowri:
            sfnm = fnm.decode()
            numoflinesinsubj = str(s.count(b'\n')+1).encode()+b' ';
            with open(sfnm,'wb') as iobw:
                iobw.write(numoflinesinsubj);iobw.write(s);iobw.write(b'\r\n');iobw.write(l);
            tsbw.write(struct.pack('H',len(fnm)))
            tsbw.write(fnm);
            tsbw.write(struct.pack('d',os.path.getmtime(sfnm)));
            tsbw.write(struct.pack('q',os.path.getsize(sfnm)));
        print('writing payload files finished.')
        for ts in tstobewritten:
            tsbw.write(ts);
    print('All done.')
else:
    print("mode is not recognized");sys.exit(0);
#check mode, if =="dnload", simply overwrite everything local, extra files in local is deleted
#              but if any local file's time and size doesn't match the recorded time and size, warn for confirmation!
#              after overwriting, update local time and size records
#            if "upload", upload everything, extra drafts are deleted
#              list the local files whose time and size don't match the recorded time and size, press enter to continue.
#              after uploading, update local time and size records
#            if "diff", show diagram of difference, ask about each file/draft that is unique or different
#            else print("mode is not recognized");sys.exit(0);
