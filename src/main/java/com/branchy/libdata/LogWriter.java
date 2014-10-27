package main.java.com.branchy.libdata;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class LogWriter {
    protected BufferedWriter log_writer;
    
    public LogWriter(String log_file_path) {
        File log_file = new File(log_file_path);
        try {
            log_writer = new BufferedWriter(new FileWriter(log_file));
        } catch (IOException e) {
            e.printStackTrace();
        }        
    }
    
    public void write(String s) {
        try {
            log_writer.write(s + "\n");
            log_writer.flush();
        } catch (IOException e) {
        }
    }
    
    public void close() {
        try {
            log_writer.flush();
            log_writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
