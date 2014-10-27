package main.java.com.branchy.libdata;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/** Write log information out to a flat file.
 *  
 * @author      Justin Libby <justin.libby @ gmail.com>
 * @version     1.0
 * @since       2014-08-08
 */

public class LogWriter {
    /**
     * The buffered writer that will do the work.
     */
    protected BufferedWriter log_writer;
    
    /**
     * Open the file for writing at instantiation time.
     * 
     * @param log_file_path   Full filesystem path to the log file, e.g. C:\logs\foo.log
     */
    public LogWriter(String log_file_path) {
        File log_file = new File(log_file_path);
        try {
            log_writer = new BufferedWriter(new FileWriter(log_file));
        } catch (IOException e) {
            e.printStackTrace();
        }        
    }
    
    /**
     * Write a single line to the log file.
     * 
     * @param s    String to write. A newline character will be automatically appended.
     */
    public void write(String s) {
        try {
            log_writer.write(s + "\n");
            log_writer.flush();
        } catch (IOException e) {
        }
    }
    
    /**
     * Closes the log and flushes the contents to disk.
     */
    public void close() {
        try {
            log_writer.flush();
            log_writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
