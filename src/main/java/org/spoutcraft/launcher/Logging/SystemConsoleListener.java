package org.spoutcraft.launcher.Logging;

import java.io.PrintStream;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.LogManager;
import java.util.logging.Logger;
import java.util.logging.StreamHandler;

public class SystemConsoleListener {
	
	public void initialize() throws Exception {
	        LogManager logManager = LogManager.getLogManager();
	        logManager.reset();
	        
	        Handler fileHandler = new FileHandler("log", 10000, 5, true);
	        fileHandler.setFormatter(new ClientLoggerFormatter());
	        Logger.getLogger("").addHandler(fileHandler);   
	       
	    	PrintStream stdout = System.out;
	    	/*PrintStream stderr = System.err;*/
	    	
	    	Handler ConsoleHandle = new StreamHandler(stdout, new ClientLoggerFormatter());
	    	Logger.getLogger("").addHandler(ConsoleHandle);   
	    	
	    	/*Handler ErrHandle = new StreamHandler(stderr, new ClientLoggerFormatter());
	    	Logger.getLogger("").addHandler(ErrHandle);  */
	    	
	    	Logger logger;
	    	SystemListenerStream los;

	    	logger = Logger.getLogger("stdout");
	    	los = new SystemListenerStream(logger, SystemListenerLevel.STDOUT);
	    	System.setOut(new PrintStream(los, true));
	    	
	    	/*logger = Logger.getLogger("stderr");
	    	los= new SystemListenerStream(logger, SystemListenerLevel.STDERR);
	    	System.setErr(new PrintStream(los, true));*/
	    	
	}
}
