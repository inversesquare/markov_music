package main.java.com.branchy.markov_music;

import main.java.com.branchy.libmath.PowerSpectrumWaterfall;
import main.java.com.branchy.libnote.MusicalNote;
import main.java.com.branchy.libnote.MusicalNoteGrid;

/** Map the notes from a waterfall spectrum into a note grid
 *  
 * @author      Justin Libby <justin.libby @ gmail.com>
 * @version     1.0
 * @since       2014-09-12
 */
public class WaterfallToGrid {

    /**
     * Given a power spectrum grid, try to calculate a note grid of detected notes.
     * 
     * @param psw            The source power spectra
     * @param num_stddev     Threshold of note detection, expressed in number of standard deviations above the median
     * @return               Calculated note grid
     */
    public static MusicalNoteGrid WaterfallToNoteGrid(
            PowerSpectrumWaterfall psw,
            double num_stddev
            )
    {
        // Construct a grid based on the number of coarse time chunks in the waterfall
        MusicalNoteGrid noteGrid = new MusicalNoteGrid(psw.GetNumChunks(), psw.GetTime());
        
        // Median power in the waterfall, used to test for valid notes
        double median = psw.GetMedianLogPower();
        // Standard deviation of the waterfall's power. Used to calculate a threshold for valid notes.
        double stddev = psw.GetStdDevLogPower();
        
        // The array of frequency values in this waterfall. Needed to look up notes.
        double [] freq = psw.GetLogFrequency();
        
        for (int i = 0; i < psw.GetNumChunks(); i++)
        {
            // A single power spectra from the waterfall
            double [] tmp = psw.GetOneLogSpectra(i);

            for (int j = 0; j < tmp.length; j++)
            {
                // Threshold for being considered a real note in the song
                if (tmp[j] > (median + (num_stddev * stddev)))
                {
                    // XXX - this could be a simple calculation, not an O(N) lookup
                    MusicalNote note = MusicalNote.LookupNote(Math.pow(10, freq[j]));
                    if (note != null)
                    {
                        // This note has passed the frequency test
                        // Initialize the note amplitude to zero
                        note.SetAmplitude(0.0);
                        if (tmp[j] > 0.0)
                        {
                            // Note amplitude should switch back to linear space from log space
                            // Scale back the PSD to create a more human readable amplitude number
                            note.SetAmplitude(Math.pow(10, (tmp[j] - 3.0)));
                        }
                        noteGrid.AddOneNote(note, i);
                    }
                }
            }
        }
        
        return noteGrid;
    }
    
}
