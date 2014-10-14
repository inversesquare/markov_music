package markov_music;

// Map the notes from a waterfall spectrum into a note grid

public class WaterfallToGrid {

    public static MusicalNoteGrid WaterfallToNoteGrid(
            PowerSpectrumWaterfall psw,
            double num_stddev
            )
    {
        MusicalNoteGrid noteGrid = new MusicalNoteGrid(psw.GetNumChunks(), psw.GetTime());
        int width = psw.GetNumChunks();
        double median = psw.GetMedianLogPower();
        double stddev = psw.GetStdDevLogPower();
        double [] tmp;
        double [] freq = psw.GetLogFrequency();
        MusicalNote note = null;
        
        for (int i = 0; i < width; i++)
        {
            tmp = psw.GetOneLogSpectra(i);
            for (int j = 0; j < tmp.length; j++)
            {
                // Threshold for being considered a real note in the song
                if (tmp[j] > (median + (num_stddev * stddev)))
                {
                    // XXX - this could be a simple calculation, not an O(N) lookup
                    note = noteGrid.LookupNote(Math.pow(10, freq[j]));
                    if (note != null)
                    {
                        // Note amplitude should switch back to linear space from log space
                        // Divide the PSD value by 10 to create a more human readable amplitude number
                        note.amplitude = 0.0;
                        if (tmp[j] > 0.0)
                        {
                            note.amplitude = Math.pow(10, (tmp[j] - 3.0));
                        }
                        noteGrid.AddOneNote(note, i);
                    }
                }
            }
        }
        
        return noteGrid;
    }
    
}
