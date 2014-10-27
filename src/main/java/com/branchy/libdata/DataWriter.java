package main.java.com.branchy.libdata;

import java.util.List;

/** Write tab delimited data out to a flat file.
 *  
 * @author      Justin Libby <justin.libby @ gmail.com>
 * @version     1.0
 * @since       2014-08-08
 */

public class DataWriter extends LogWriter {
    /**
     * Delimiter to use for the data file.
     * I like tabs, so caller gets no choice for now.
     */
    private static String delimiter = "\t";

    /**
     * At instantiation time, write out the tab delimited file headers
     * This will create the file if it doesn't exist
     * or blow away an existing file.
     * 
     * @param log_file_path
     * @param headers
     */
    // XXX - DataWriter should detect existing contents and not re-write header?
    // XXX - TODO: write a parser for reading in data
    // Parse column headers and create a Map for column header -> lists of column data?
    public DataWriter(String log_file_path, List<String> headers) {
        super(log_file_path);
        String s = join(headers, delimiter);
        write(s);
    }
    
    /**
     * Write out a single line of delimited data to the data file
     * 
     * @param list   List of string data to write
     */
    public void WriteData(List<String> list)
    {
        write(join(list, delimiter));
    }
    
    /**
     * Joins a list of strings together given a delimiter
     * 
     * @param list       The list of strings to join
     * @param delimiter  Delimtier to use, e.g. \t
     * @return
     */
    static public String join(List<String> list, String delimiter)
    {
       StringBuilder sb = new StringBuilder();
       // Deal with the fencepost:
       // first time through, don't append delimtier in front
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
