package main.java.com.branchy.libdata;

import java.util.List;

public class DataWriter extends LogWriter {

    public DataWriter(String log_file_path, List<String> headers) {
        super(log_file_path);
        String s = join(headers, "\t");
        write(s);
    }
    
    static public String join(List<String> list, String delimiter)
    {
       StringBuilder sb = new StringBuilder();
       boolean initial_iteration = true;
       for (String s : list)
       {
          if (initial_iteration)
          {
             initial_iteration = false;
          }
          else
          {
             sb.append(delimiter);
          }
          sb.append(s);
       }
       return sb.toString();
    }

}
