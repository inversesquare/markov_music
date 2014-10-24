package markov_music;

import java.util.ArrayList;
import java.util.List;

/** A single musical note.
 *  <p>
 *  Currently no check to ensure the note frequency actually
 *  resides within the correct octave range, or that the note
 *  is named appropriately.
 *  
 * @author      Justin Libby <justin.libby @ gmail.com>
 * @version     1.0
 * @since       2014-08-08
 */

public class MusicalNote implements Comparable<MusicalNote>{

    /**
     * Frequency of the note, expressed in Hertz. e.g. 440.0
     */
    private double frequency;
    
    /**
     * Amplitude of the note in arbitrary units.
     */
    private double amplitude;
    
    /**
     * Name of the note. e.g. F
     */
    private String name;

    /**
     * Octave of the note. e.g. 4
     */
    private int octave;
    
    /**
     * Indicates if the note is sharp or flat. "b" => flat, "" => natural
     * All notes are classified as either natural or flat.
     * e.g. F# => Gb
     */
    private String sharpFlat;
    
    public MusicalNote(
            double f,
            String n,
            int oct,
            String shFl
            )
    {
        frequency = f;
        name = n;
        octave = oct;
        sharpFlat = shFl;
        amplitude = 1.0;
    }
    
    /**
     * @return Frequency of the note, expressed in Hertz. e.g. 440.0
     */
    public double Frequency()
    {
        return frequency;
    }
    
    /**
     * @return Amplitude of the note in arbitrary units.
     */
    public double Amplitude()
    {
        return amplitude;
    }
    
    /**
     * @param amp    Sets this note's amplitude in arbitrary units.
     */
    public void SetAmplitude(double amp)
    {
        amplitude = amp;
    }
    
    /**
     * @return Full formatted name of the note for writing to log file.
     */
    public String FullName()
    {
        return name + String.format("%d", octave) + sharpFlat;
    }

    /**
     * Equality test for musical note.
     * <p>
     * This test does not compare amplitude.
     *
     * @param  obj Object to compare to this note.
     * @return     True if the object is the same note.
     */
    public boolean equals(Object obj) {
        if (!(obj instanceof MusicalNote))
        {
            return false;
        }
        if (obj == this)
        {
            return true;
        }

        MusicalNote mn = (MusicalNote) obj;
        return (frequency == mn.frequency &&
                name.equals(mn.name) &&
                octave == mn.octave &&
                sharpFlat.equals(mn.sharpFlat));
    }

    /**
     * Comparison test for musical note.
     * <p>
     * This test only compares frequency for sorting.
     *
     * @param  n   Musical note to compare to.
     * @return     -1 if this note is lower in frequency, 0 if equal, 1 if this note is higher
     */
    @Override
    public int compareTo(MusicalNote n) {
        if (frequency < n.frequency)
        {
            return -1;
        }
        if (frequency > n.frequency)
        {
            return 1;
        }
        return 0;
    }

    /**
     * Given a frequency, return one of the valid notes if it is a close match.
     * <p>
     * Searches the list of valid notes and returns one if it is within 1% in frequency.
     *
     * @param  freq    Frequency to search for.
     * @return         The nearest valid note, or null if no match was found.
     */
    public static MusicalNote LookupNote(double freq)
    {
        MusicalNote ret = null;
        
        for (MusicalNote n : MusicalNote.allNotes)
        {
            // half steps are about 6% apart
            // look for frequencies that are within 1% of center
            if ((freq < n.Frequency() * 1.01) &&
                (freq > n.Frequency() * 0.99))
            {
                return n;
            }
        }
        
        return ret;
    }
    
    /**
     * List of all valid musical notes for the application.
     */
    static List<MusicalNote> allNotes;
    
    /**
     * @return    The lowest frequency in the list of legitimate notes.
     */
    public static double MinNoteFreq()
    {
        return allNotes.get(0).frequency;
    }
    
    /**
     * Loaded once when the class is loaded,
     * this populates the list of all valid musical notes.
     */
    static {
        allNotes = new ArrayList<MusicalNote>();

        allNotes.add(new MusicalNote(32.70, "C", 1, ""));
        allNotes.add(new MusicalNote(34.65, "D", 1, "b"));
        allNotes.add(new MusicalNote(36.71, "D", 1, ""));
        allNotes.add(new MusicalNote(38.89, "E", 1, "b"));
        allNotes.add(new MusicalNote(41.20, "E", 1, ""));
        allNotes.add(new MusicalNote(43.65, "F", 1, ""));
        allNotes.add(new MusicalNote(46.25, "G", 1, "b"));
        allNotes.add(new MusicalNote(49.00, "G", 1, ""));
        allNotes.add(new MusicalNote(51.91, "A", 1, "b"));
        allNotes.add(new MusicalNote(55.0, "A", 1, ""));
        allNotes.add(new MusicalNote(58.27, "B", 1, "b"));
        allNotes.add(new MusicalNote(61.74, "B", 1, ""));

        allNotes.add(new MusicalNote(65.41, "C", 2, ""));
        allNotes.add(new MusicalNote(69.30, "D", 2, "b"));
        allNotes.add(new MusicalNote(73.42, "D", 2, ""));
        allNotes.add(new MusicalNote(77.78, "E", 2, "b"));
        allNotes.add(new MusicalNote(82.41, "E", 2, ""));
        allNotes.add(new MusicalNote(87.31, "F", 2, ""));
        allNotes.add(new MusicalNote(92.50, "G", 2, "b"));
        allNotes.add(new MusicalNote(98.00, "G", 2, ""));
        allNotes.add(new MusicalNote(103.83, "A", 2, "b"));
        allNotes.add(new MusicalNote(110.00, "A", 2, ""));
        allNotes.add(new MusicalNote(116.54, "B", 2, "b"));
        allNotes.add(new MusicalNote(123.47, "B", 2, ""));
        
        allNotes.add(new MusicalNote(130.81, "C", 3, ""));
        allNotes.add(new MusicalNote(138.59, "D", 3, "b"));
        allNotes.add(new MusicalNote(146.83, "D", 3, ""));
        allNotes.add(new MusicalNote(155.56, "E", 3, "b"));
        allNotes.add(new MusicalNote(164.81, "E", 3, ""));
        allNotes.add(new MusicalNote(174.61, "F", 3, ""));
        allNotes.add(new MusicalNote(185.0, "G", 3, "b"));
        allNotes.add(new MusicalNote(196.0, "G", 3, ""));
        allNotes.add(new MusicalNote(207.65, "A", 3, "b"));
        allNotes.add(new MusicalNote(220.0, "A", 3, ""));
        allNotes.add(new MusicalNote(233.08, "B", 3, "b"));
        allNotes.add(new MusicalNote(246.94, "B", 3, ""));
        
        allNotes.add(new MusicalNote(261.63, "C", 4, ""));
        allNotes.add(new MusicalNote(277.18, "D", 4, "b"));
        allNotes.add(new MusicalNote(293.66, "D", 4, ""));
        allNotes.add(new MusicalNote(311.13, "E", 4, "b"));
        allNotes.add(new MusicalNote(329.63, "E", 4, ""));
        allNotes.add(new MusicalNote(349.23, "F", 4, ""));
        allNotes.add(new MusicalNote(369.99, "G", 4, "b"));
        allNotes.add(new MusicalNote(392.0, "G", 4, ""));
        allNotes.add(new MusicalNote(415.30, "A", 4, "b"));
        allNotes.add(new MusicalNote(440.0, "A", 4, ""));
        allNotes.add(new MusicalNote(466.16, "B", 4, "b"));
        allNotes.add(new MusicalNote(493.88, "B", 4, ""));
        
        allNotes.add(new MusicalNote(523.25, "C", 5, ""));
        allNotes.add(new MusicalNote(554.37, "D", 5, "b"));
        allNotes.add(new MusicalNote(587.33, "D", 5, ""));
        allNotes.add(new MusicalNote(622.25, "E", 5, "b"));
        allNotes.add(new MusicalNote(659.25, "E", 5, ""));
        allNotes.add(new MusicalNote(698.46, "F", 5, ""));
        allNotes.add(new MusicalNote(739.99, "G", 5, "b"));
        allNotes.add(new MusicalNote(783.99, "G", 5, ""));
        allNotes.add(new MusicalNote(830.61, "A", 5, "b"));
        allNotes.add(new MusicalNote(880.0, "A", 5, ""));
        allNotes.add(new MusicalNote(932.33, "B", 5, "b"));
        allNotes.add(new MusicalNote(987.77, "B", 5, ""));

        allNotes.add(new MusicalNote(1046.5, "C", 6, ""));
        allNotes.add(new MusicalNote(1108.73, "D", 6, "b"));
        allNotes.add(new MusicalNote(1174.66, "D", 6, ""));
        allNotes.add(new MusicalNote(1244.51, "E", 6, "b"));
        allNotes.add(new MusicalNote(1318.51, "E", 6, ""));
        allNotes.add(new MusicalNote(1396.91, "F", 6, ""));
        allNotes.add(new MusicalNote(1479.98, "G", 6, "b"));
        allNotes.add(new MusicalNote(1567.98, "G", 6, ""));
        allNotes.add(new MusicalNote(1661.22, "A", 6, "b"));
        allNotes.add(new MusicalNote(1760.0, "A", 6, ""));
        allNotes.add(new MusicalNote(1864.66, "B", 6, "b"));
        allNotes.add(new MusicalNote(1975.53, "B", 6, ""));

        allNotes.add(new MusicalNote(2093.0, "C", 7, ""));
        allNotes.add(new MusicalNote(2217.46, "D", 7, "b"));
        allNotes.add(new MusicalNote(2349.32, "D", 7, ""));
        allNotes.add(new MusicalNote(2489.02, "E", 7, "b"));
        allNotes.add(new MusicalNote(2637.02, "E", 7, ""));
        allNotes.add(new MusicalNote(2793.83, "F", 7, ""));
        allNotes.add(new MusicalNote(2959.96, "G", 7, "b"));
        allNotes.add(new MusicalNote(3135.96, "G", 7, ""));
        allNotes.add(new MusicalNote(3322.44, "A", 7, "b"));
        allNotes.add(new MusicalNote(3520.0, "A", 7, ""));
        allNotes.add(new MusicalNote(3729.31, "B", 7, "b"));
        allNotes.add(new MusicalNote(3951.07, "B", 7, ""));

        allNotes.add(new MusicalNote(4186.01, "C", 8, ""));
        allNotes.add(new MusicalNote(4434.92, "D", 8, "b"));
        allNotes.add(new MusicalNote(4698.63, "D", 8, ""));
        allNotes.add(new MusicalNote(4978.03, "E", 8, "b"));
        allNotes.add(new MusicalNote(5274.04, "E", 8, ""));
        allNotes.add(new MusicalNote(5587.65, "F", 8, ""));
        allNotes.add(new MusicalNote(5919.91, "G", 8, "b"));
        allNotes.add(new MusicalNote(6271.93, "G", 8, ""));
        allNotes.add(new MusicalNote(6644.88, "A", 8, "b"));
        allNotes.add(new MusicalNote(7040.0, "A", 8, ""));
        allNotes.add(new MusicalNote(7458.62, "B", 8, "b"));
        allNotes.add(new MusicalNote(7902.13, "B", 8, ""));
    }
}
