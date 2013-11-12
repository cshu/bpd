package griddoor.util;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;

/*
HUMAN READABLE VALUES

hrv as file extension (or txt or whatever or no extension)

intend to be both human readable and machine readable.

can be used as an alternative to .ini or .cfg

as the name indicates, it's just an array of values.

1. it's a text file format, so it must end with \n

2. each value is represented as literal (easier reading and writing)

3. first char of the file, must be a char between 1-9, specifying the number of \n contained in the separator

4. the first X lines (excluding first char), which ends with \n, is the separator for this file (separator includes the last \n) (if first char is '1' and the first line is just a single \n, then \n is the separator)

5. after the first separator starting the first value, and it goes on so every values is preceded by a separator

6. 2 consecutive separators indicate an empty value between them

7. if the last value doesn't end with \n, you have to append an extra empty value (which could be just useless)



e.g. 1:
1+
path+
d:\+
name+
hardly a name+

e.g. 2:
1
path
d:\
name
hardly a name

e.g. 3:
2
*******
path
*******
d:\
*******
name
*******
hardly a name
*******
*/

public class HumanReadableValuesUtil {
	public static int getValues(Path filepath, Collection<String> coll) throws IOException{
		byte[] byt = Files.readAllBytes(filepath);
		if(byt[0]<0x31||byt[0]>0x39)
			return 1;
		int numofl = byt[0]-0x30;
		String str = new String(byt, StandardCharsets.UTF_8);
		int off = 1;
		for(;numofl>0;numofl--)
			off = str.indexOf('\n', off)+1;
		String sep = str.substring(1, off);
		while(true){
			int newoff = str.indexOf(sep, off);
			if(newoff==-1){
				coll.add(str.substring(off));
				return 0;
			}
			coll.add(str.substring(off,newoff));
			off = newoff+sep.length();
			if(off==str.length())
				return 0;
		}
	}

}
