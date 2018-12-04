package com.tfs.ConsoleApp;

import java.io.Console;
import java.io.File;
import java.io.IOException;
import java.awt.GraphicsEnvironment;
import java.net.URISyntaxException;
import com.tfs.utilities.Log;

public class Main {
	public static void main(String[] args) throws IOException,
			InterruptedException, URISyntaxException {
		Console console = System.console();
		if (console == null && !GraphicsEnvironment.isHeadless()) {
			String filename = new File(Main.class.getProtectionDomain().getCodeSource()
					.getLocation().getPath()).getName();
			Log.info(Main.class.getProtectionDomain().getCodeSource().getLocation().getPath());
			Log.info(filename);
			Runtime.getRuntime().exec(new String[] { "cmd", "/c", "start", "cmd","/k","java -jar TestAutomatiionStatusUpdateUtility.jar"});
			
		} else {
			InteractiveConsole.main(new String[0]);
		}
		
		
		
	}
}