package org.spoutcraft.launcher.Logging;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SystemListenerStream extends ByteArrayOutputStream { 
 
    private String lineSeparator; 
 
    private Logger logger; 
    private Level level;
    public SystemListenerStream(Logger logger, Level level) { 
        super(); 
        this.logger = logger; 
        this.level = level; 
        lineSeparator = System.getProperty("line.separator"); 
    } 
 
    public void flush() throws IOException { 
 
        String record; 
        synchronized(this) { 
            super.flush(); 
            record = this.toString(); 
            super.reset(); 
 
            if (record.length() == 0 || record.equals(lineSeparator)) { 
                return; 
            }
            
            logger.logp(level, "", "", record); 
        } 
    } 
} 