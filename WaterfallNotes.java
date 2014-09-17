package markov_music;

import java.util.ArrayList;
import java.util.List;

public class WaterfallNotes {
    private String notes_file_out;
    private PowerSpectrumWaterfall psw;
    private LogWriter log;
    
    private int width; // width - number of chunks
    private List<MusicalNote> notes;
    private List<List<MusicalNote>> recordedNotes;
    
    public WaterfallNotes(
            PowerSpectrumWaterfall psw_in,
            String file_path_out,
            LogWriter log_in
            )
    {
        LoadNotes();
        psw = psw_in;
        log = log_in;
        width = psw.GetNumChunks();
        notes_file_out = file_path_out;
        
        recordedNotes = new ArrayList<List<MusicalNote>>(width);
        
        PopulateNotes();
        WriteNotes();
    }
    
    
    private void PopulateNotes()
    {
        double median = psw.GetMedianLogPower();
        double stddev = psw.GetStdDevLogPower();
        double [] tmp;
        double [] freq = psw.GetLogFrequency();
        List<MusicalNote> goodNotes = null;
        MusicalNote note = null;
        
        for (int i = 0; i < width; i++)
        {
            tmp = psw.GetOneLogSpectra(i);
            goodNotes = new ArrayList<MusicalNote>();
            for (int j = 0; j < tmp.length; j++)
            {
                // Threshold for being considered a real note in the song
                if (tmp[j] > (median + (1.5 * stddev)))
                {
                    // XXX - this could be a simple calculation, not an O(N) lookup
                    note = LookupNote(Math.pow(10, freq[j]));
                    if (note != null)
                    {
                        goodNotes.add(note);
                    }
                }
            }
            
            recordedNotes.add(goodNotes);
        }
    }
    
    private MusicalNote LookupNote(double freq)
    {
        MusicalNote ret = null;
        
        for (MusicalNote n : notes)
        {
            // half steps are about 6% apart
            // look for frequencies that are within 1% of center
            if ((freq < n.frequency * 1.01) &&
                (freq > n.frequency * 0.99))
            {
                return n;
            }
        }
        
        return ret;
    }
    
    private void WriteNotes()
    {
        List<String> headers = new ArrayList<String>();
        headers.add("Time");
        for (MusicalNote n : notes)
        {
            headers.add(n.FullName());
        }
        DataWriter dw = new DataWriter(notes_file_out, headers);
        
        int counter = 0;
        double[] time = psw.GetTime();
        List<String> oneLine = null;
        for (List<MusicalNote> goodNotes : recordedNotes)
        {
            oneLine = new ArrayList<String>();
            oneLine.add(String.format("%014.4f", time[counter]));
            for (MusicalNote n : notes)
            {
                if (goodNotes.contains(n))
                {
                    oneLine.add("1");
                } else {
                    oneLine.add("0");
                }
            }
            dw.write(DataWriter.join(oneLine, "\t"));
            counter += 1;
        }
    }
    
    private void LoadNotes()
    {
        notes = new ArrayList<MusicalNote>();

        // Lowest and highest octaves aren't really helpful for this application
        /*
        notes.add(new MusicalNote(32.70, "C", 1, ""));
        notes.add(new MusicalNote(34.65, "D", 1, "b"));
        notes.add(new MusicalNote(36.71, "D", 1, ""));
        notes.add(new MusicalNote(38.89, "E", 1, "b"));
        notes.add(new MusicalNote(41.20, "E", 1, ""));
        notes.add(new MusicalNote(43.65, "F", 1, ""));
        notes.add(new MusicalNote(46.25, "G", 1, "b"));
        notes.add(new MusicalNote(49.00, "G", 1, ""));
        notes.add(new MusicalNote(51.91, "A", 1, "b"));
        notes.add(new MusicalNote(55.0, "A", 1, ""));
        notes.add(new MusicalNote(58.27, "B", 1, "b"));
        notes.add(new MusicalNote(61.74, "B", 1, ""));
        */
        notes.add(new MusicalNote(65.41, "C", 2, ""));
        notes.add(new MusicalNote(69.30, "D", 2, "b"));
        notes.add(new MusicalNote(73.42, "D", 2, ""));
        notes.add(new MusicalNote(77.78, "E", 2, "b"));
        notes.add(new MusicalNote(82.41, "E", 2, ""));
        notes.add(new MusicalNote(87.31, "F", 2, ""));
        notes.add(new MusicalNote(92.50, "G", 2, "b"));
        notes.add(new MusicalNote(98.00, "G", 2, ""));
        notes.add(new MusicalNote(103.83, "A", 2, "b"));
        notes.add(new MusicalNote(110.00, "A", 2, ""));
        notes.add(new MusicalNote(116.54, "B", 2, "b"));
        notes.add(new MusicalNote(123.47, "B", 2, ""));
        
        notes.add(new MusicalNote(130.81, "C", 3, ""));
        notes.add(new MusicalNote(138.59, "D", 3, "b"));
        notes.add(new MusicalNote(146.83, "D", 3, ""));
        notes.add(new MusicalNote(155.56, "E", 3, "b"));
        notes.add(new MusicalNote(164.81, "E", 3, ""));
        notes.add(new MusicalNote(174.61, "F", 3, ""));
        notes.add(new MusicalNote(185.0, "G", 3, "b"));
        notes.add(new MusicalNote(196.0, "G", 3, ""));
        notes.add(new MusicalNote(207.65, "A", 3, "b"));
        notes.add(new MusicalNote(220.0, "A", 3, ""));
        notes.add(new MusicalNote(233.08, "B", 3, "b"));
        notes.add(new MusicalNote(246.94, "B", 3, ""));
        
        notes.add(new MusicalNote(261.63, "C", 4, ""));
        notes.add(new MusicalNote(277.18, "D", 4, "b"));
        notes.add(new MusicalNote(293.66, "D", 4, ""));
        notes.add(new MusicalNote(311.13, "E", 4, "b"));
        notes.add(new MusicalNote(329.63, "E", 4, ""));
        notes.add(new MusicalNote(349.23, "F", 4, ""));
        notes.add(new MusicalNote(369.99, "G", 4, "b"));
        notes.add(new MusicalNote(392.0, "G", 4, ""));
        notes.add(new MusicalNote(415.30, "A", 4, "b"));
        notes.add(new MusicalNote(440.0, "A", 4, ""));
        notes.add(new MusicalNote(466.16, "B", 4, "b"));
        notes.add(new MusicalNote(493.88, "B", 4, ""));
        
        notes.add(new MusicalNote(523.25, "C", 5, ""));
        notes.add(new MusicalNote(554.37, "D", 5, "b"));
        notes.add(new MusicalNote(587.33, "D", 5, ""));
        notes.add(new MusicalNote(622.25, "E", 5, "b"));
        notes.add(new MusicalNote(659.25, "E", 5, ""));
        notes.add(new MusicalNote(698.46, "F", 5, ""));
        notes.add(new MusicalNote(739.99, "G", 5, "b"));
        notes.add(new MusicalNote(783.99, "G", 5, ""));
        notes.add(new MusicalNote(830.61, "A", 5, "b"));
        notes.add(new MusicalNote(880.0, "A", 5, ""));
        notes.add(new MusicalNote(932.33, "B", 5, "b"));
        notes.add(new MusicalNote(987.77, "B", 5, ""));

        notes.add(new MusicalNote(1046.5, "C", 6, ""));
        notes.add(new MusicalNote(1108.73, "D", 6, "b"));
        notes.add(new MusicalNote(1174.66, "D", 6, ""));
        notes.add(new MusicalNote(1244.51, "E", 6, "b"));
        notes.add(new MusicalNote(1318.51, "E", 6, ""));
        notes.add(new MusicalNote(1396.91, "F", 6, ""));
        notes.add(new MusicalNote(1479.98, "G", 6, "b"));
        notes.add(new MusicalNote(1567.98, "G", 6, ""));
        notes.add(new MusicalNote(1661.22, "A", 6, "b"));
        notes.add(new MusicalNote(1760.0, "A", 6, ""));
        notes.add(new MusicalNote(1864.66, "B", 6, "b"));
        notes.add(new MusicalNote(1975.53, "B", 6, ""));

        notes.add(new MusicalNote(2093.0, "C", 7, ""));
        notes.add(new MusicalNote(2217.46, "D", 7, "b"));
        notes.add(new MusicalNote(2349.32, "D", 7, ""));
        notes.add(new MusicalNote(2489.02, "E", 7, "b"));
        notes.add(new MusicalNote(2637.02, "E", 7, ""));
        notes.add(new MusicalNote(2793.83, "F", 7, ""));
        notes.add(new MusicalNote(2959.96, "G", 7, "b"));
        notes.add(new MusicalNote(3135.96, "G", 7, ""));
        notes.add(new MusicalNote(3322.44, "A", 7, "b"));
        notes.add(new MusicalNote(3520.0, "A", 7, ""));
        notes.add(new MusicalNote(3729.31, "B", 7, "b"));
        notes.add(new MusicalNote(3951.07, "B", 7, ""));

        /*
        notes.add(new MusicalNote(4186.01, "C", 8, ""));
        notes.add(new MusicalNote(4434.92, "D", 8, "b"));
        notes.add(new MusicalNote(4698.63, "D", 8, ""));
        notes.add(new MusicalNote(4978.03, "E", 8, "b"));
        notes.add(new MusicalNote(5274.04, "E", 8, ""));
        notes.add(new MusicalNote(5587.65, "F", 8, ""));
        notes.add(new MusicalNote(5919.91, "G", 8, "b"));
        notes.add(new MusicalNote(6271.93, "G", 8, ""));
        notes.add(new MusicalNote(6644.88, "A", 8, "b"));
        notes.add(new MusicalNote(7040.0, "A", 8, ""));
        notes.add(new MusicalNote(7458.62, "B", 8, "b"));
        notes.add(new MusicalNote(7902.13, "B", 8, ""));
        */
    }
}
