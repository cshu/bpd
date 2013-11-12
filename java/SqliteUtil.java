package griddoor.util;

public class SqliteUtil {
	/**
	 * return ` when using ` to escape, if char is 0 then no escape is needed
	 * @param str
	 * @return
	 */
	public static char makeBindValToFindStrWithLike(String str, StringBuilder sb){
		sb.append('%');
		int pInd = str.indexOf('%');
		if(pInd == -1){
			int uInd = str.indexOf('_');
			if(uInd == -1){
				sb.append(str);
				sb.append('%');
				return 0;
			}
		}
		int strLen = str.length();
		for(int i=0;i<strLen;i++){
			char c = str.charAt(i);
			switch(c){
			case '%':
			case '_':
			case '`':
				sb.append('`');
			}
			sb.append(c);
		}
		sb.append('%');
		return '`';
	}
}
