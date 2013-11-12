package griddoor.util;

import java.awt.HeadlessException;
import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;

public class EnvironmentUtil {
	public static String getStringFromSystemClipboard() throws HeadlessException, UnsupportedFlavorException, IOException{
		//DataFlavor[] dfs = Toolkit.getDefaultToolkit().getSystemClipboard().getAvailableDataFlavors();
		//Toolkit.getDefaultToolkit().getSystemClipboard().getContents(new Object());
		return (String) Toolkit.getDefaultToolkit().getSystemClipboard().getData(DataFlavor.stringFlavor);
	}
	
	public static void setStringToSystemClipboard(String str){
		Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(str), null);
	}

}
