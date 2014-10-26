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
     * b for flat, N for natural
     * All notes are classified as either natural or flat.
     * e.g. F# => Gb
     */
    public enum Accidental {
        b, N
    }
    
    /**
     * All possible names for notes
     */
    public enum NoteNames {
        A, B, C, D, E, F, G
    }

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
    private NoteNames name;

    /**
     * Octave of the note. e.g. 4
     */
    private int octave;
    
    /**
     * Indicates if the note is sharp or flat. "b" => flat, "N" => natural
     * All notes are classified as either natural or flat.
     * e.g. F# => Gb
     */
    private Accidental accidental;
    
    /**
     * 
     * @param f    Frequency of the note, in Hertz
     * @param n    Name of the note, e.g. "C"
     * @param oct  Octave of the note, e.g. 4
     * @param acc  Accidental of the note, e.g. "b"
     */
    private MusicalNote(
            double f,
            NoteNames n,
            int oct,
            Accidental acc
            )
    {
        frequency = f;
        name = n;
        octave = oct;
        accidental = acc;
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
        return name + String.format("%d", octave) + accidental;
    }

    /**
     * Equality test for musical note.
     * <p>
     * This test does not compare amplitude.
     *
     * @param  obj Object to compare to this note.
     * @return     True if the object is the same note.
     */
    // XXX - Create a new class so we can properly do equality
    // and use that class as a key in a map
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
                accidental.equals(mn.accidental));
    }

    /**
     * Comparison test for musical note.
     * <p>
     * This test only compares frequency for sorting.
     *
     * @param  n   Musical note to compare to.
     * @return     -1 if this note is lower in frequency, 0 if equal, 1 if this note is higher
     */
    // Needed for sorting the list of notes 
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
    // XXX - Might be worth doing this faster:
    // divide or multiply freq by 2 until we're within a specific octave
    // then only walk down one octave worth of notes.
    // might not be worth the added complexity
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
     * One and only List of all valid musical notes for the application.
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
     * 
     * This could be done by starting with a base frequency for each of the 
     * 12 notes in an octave and then multiplying
     * by two for each new octave, but I'd have to deal with rounding error.
     */
    static {
        allNotes = new ArrayList<MusicalNote>();

        allNotes.add(new MusicalNote(32.70, NoteNames.C, 1, Accidental.N));
        allNotes.add(new MusicalNote(34.65, NoteNames.D, 1, Accidental.b));
        allNotes.add(new MusicalNote(36.71, NoteNames.D, 1, Accidental.N));
        allNotes.add(new MusicalNote(38.89, NoteNames.E, 1, Accidental.b));
        allNotes.add(new MusicalNote(41.20, NoteNames.E, 1, Accidental.N));
        allNotes.add(new MusicalNote(43.65, NoteNames.F, 1, Accidental.N));
        allNotes.add(new MusicalNote(46.25, NoteNames.G, 1, Accidental.b));
        allNotes.add(new MusicalNote(49.00, NoteNames.G, 1, Accidental.N));
        allNotes.add(new MusicalNote(51.91, NoteNames.A, 1, Accidental.b));
        allNotes.add(new MusicalNote(55.0, NoteNames.A, 1, Accidental.N));
        allNotes.add(new MusicalNote(58.27, NoteNames.B, 1, Accidental.b));
        allNotes.add(new MusicalNote(61.74, NoteNames.B, 1, Accidental.N));

        allNotes.add(new MusicalNote(65.41, NoteNames.C, 2, Accidental.N));
        allNotes.add(new MusicalNote(69.30, NoteNames.D, 2, Accidental.b));
        allNotes.add(new MusicalNote(73.42, NoteNames.D, 2, Accidental.N));
        allNotes.add(new MusicalNote(77.78, NoteNames.E, 2, Accidental.b));
        allNotes.add(new MusicalNote(82.41, NoteNames.E, 2, Accidental.N));
        allNotes.add(new MusicalNote(87.31, NoteNames.F, 2, Accidental.N));
        allNotes.add(new MusicalNote(92.50, NoteNames.G, 2, Accidental.b));
        allNotes.add(new MusicalNote(98.00, NoteNames.G, 2, Accidental.N));
        allNotes.add(new MusicalNote(103.83, NoteNames.A, 2, Accidental.b));
        allNotes.add(new MusicalNote(110.00, NoteNames.A, 2, Accidental.N));
        allNotes.add(new MusicalNote(116.54, NoteNames.B, 2, Accidental.b));
        allNotes.add(new MusicalNote(123.47, NoteNames.B, 2, Accidental.N));
        
        allNotes.add(new MusicalNote(130.81, NoteNames.C, 3, Accidental.N));
        allNotes.add(new MusicalNote(138.59, NoteNames.D, 3, Accidental.b));
        allNotes.add(new MusicalNote(146.83, NoteNames.D, 3, Accidental.N));
        allNotes.add(new MusicalNote(155.56, NoteNames.E, 3, Accidental.b));
        allNotes.add(new MusicalNote(164.81, NoteNames.E, 3, Accidental.N));
        allNotes.add(new MusicalNote(174.61, NoteNames.F, 3, Accidental.N));
        allNotes.add(new MusicalNote(185.0, NoteNames.G, 3, Accidental.b));
        allNotes.add(new MusicalNote(196.0, NoteNames.G, 3, Accidental.N));
        allNotes.add(new MusicalNote(207.65, NoteNames.A, 3, Accidental.b));
        allNotes.add(new MusicalNote(220.0, NoteNames.A, 3, Accidental.N));
        allNotes.add(new MusicalNote(233.08, NoteNames.B, 3, Accidental.b));
        allNotes.add(new MusicalNote(246.94, NoteNames.B, 3, Accidental.N));
        
        allNotes.add(new MusicalNote(261.63, NoteNames.C, 4, Accidental.N));
        allNotes.add(new MusicalNote(277.18, NoteNames.D, 4, Accidental.b));
        allNotes.add(new MusicalNote(293.66, NoteNames.D, 4, Accidental.N));
        allNotes.add(new MusicalNote(311.13, NoteNames.E, 4, Accidental.b));
        allNotes.add(new MusicalNote(329.63, NoteNames.E, 4, Accidental.N));
        allNotes.add(new MusicalNote(349.23, NoteNames.F, 4, Accidental.N));
        allNotes.add(new MusicalNote(369.99, NoteNames.G, 4, Accidental.b));
        allNotes.add(new MusicalNote(392.0, NoteNames.G, 4, Accidental.N));
        allNotes.add(new MusicalNote(415.30, NoteNames.A, 4, Accidental.b));
        allNotes.add(new MusicalNote(440.0, NoteNames.A, 4, Accidental.N));
        allNotes.add(new MusicalNote(466.16, NoteNames.B, 4, Accidental.b));
        allNotes.add(new MusicalNote(493.88, NoteNames.B, 4, Accidental.N));
        
        allNotes.add(new MusicalNote(523.25, NoteNames.C, 5, Accidental.N));
        allNotes.add(new MusicalNote(554.37, NoteNames.D, 5, Accidental.b));
        allNotes.add(new MusicalNote(587.33, NoteNames.D, 5, Accidental.N));
        allNotes.add(new MusicalNote(622.25, NoteNames.E, 5, Accidental.b));
        allNotes.add(new MusicalNote(659.25, NoteNames.E, 5, Accidental.N));
        allNotes.add(new MusicalNote(698.46, NoteNames.F, 5, Accidental.N));
        allNotes.add(new MusicalNote(739.99, NoteNames.G, 5, Accidental.b));
        allNotes.add(new MusicalNote(783.99, NoteNames.G, 5, Accidental.N));
        allNotes.add(new MusicalNote(830.61, NoteNames.A, 5, Accidental.b));
        allNotes.add(new MusicalNote(880.0, NoteNames.A, 5, Accidental.N));
        allNotes.add(new MusicalNote(932.33, NoteNames.B, 5, Accidental.b));
        allNotes.add(new MusicalNote(987.77, NoteNames.B, 5, Accidental.N));

        allNotes.add(new MusicalNote(1046.5, NoteNames.C, 6, Accidental.N));
        allNotes.add(new MusicalNote(1108.73, NoteNames.D, 6, Accidental.b));
        allNotes.add(new MusicalNote(1174.66, NoteNames.D, 6, Accidental.N));
        allNotes.add(new MusicalNote(1244.51, NoteNames.E, 6, Accidental.b));
        allNotes.add(new MusicalNote(1318.51, NoteNames.E, 6, Accidental.N));
        allNotes.add(new MusicalNote(1396.91, NoteNames.F, 6, Accidental.N));
        allNotes.add(new MusicalNote(1479.98, NoteNames.G, 6, Accidental.b));
        allNotes.add(new MusicalNote(1567.98, NoteNames.G, 6, Accidental.N));
        allNotes.add(new MusicalNote(1661.22, NoteNames.A, 6, Accidental.b));
        allNotes.add(new MusicalNote(1760.0, NoteNames.A, 6, Accidental.N));
        allNotes.add(new MusicalNote(1864.66, NoteNames.B, 6, Accidental.b));
        allNotes.add(new MusicalNote(1975.53, NoteNames.B, 6, Accidental.N));

        allNotes.add(new MusicalNote(2093.0, NoteNames.C, 7, Accidental.N));
        allNotes.add(new MusicalNote(2217.46, NoteNames.D, 7, Accidental.b));
        allNotes.add(new MusicalNote(2349.32, NoteNames.D, 7, Accidental.N));
        allNotes.add(new MusicalNote(2489.02, NoteNames.E, 7, Accidental.b));
        allNotes.add(new MusicalNote(2637.02, NoteNames.E, 7, Accidental.N));
        allNotes.add(new MusicalNote(2793.83, NoteNames.F, 7, Accidental.N));
        allNotes.add(new MusicalNote(2959.96, NoteNames.G, 7, Accidental.b));
        allNotes.add(new MusicalNote(3135.96, NoteNames.G, 7, Accidental.N));
        allNotes.add(new MusicalNote(3322.44, NoteNames.A, 7, Accidental.b));
        allNotes.add(new MusicalNote(3520.0, NoteNames.A, 7, Accidental.N));
        allNotes.add(new MusicalNote(3729.31, NoteNames.B, 7, Accidental.b));
        allNotes.add(new MusicalNote(3951.07, NoteNames.B, 7, Accidental.N));

        allNotes.add(new MusicalNote(4186.01, NoteNames.C, 8, Accidental.N));
        allNotes.add(new MusicalNote(4434.92, NoteNames.D, 8, Accidental.b));
        allNotes.add(new MusicalNote(4698.63, NoteNames.D, 8, Accidental.N));
        allNotes.add(new MusicalNote(4978.03, NoteNames.E, 8, Accidental.b));
        allNotes.add(new MusicalNote(5274.04, NoteNames.E, 8, Accidental.N));
        allNotes.add(new MusicalNote(5587.65, NoteNames.F, 8, Accidental.N));
        allNotes.add(new MusicalNote(5919.91, NoteNames.G, 8, Accidental.b));
        allNotes.add(new MusicalNote(6271.93, NoteNames.G, 8, Accidental.N));
        allNotes.add(new MusicalNote(6644.88, NoteNames.A, 8, Accidental.b));
        allNotes.add(new MusicalNote(7040.0, NoteNames.A, 8, Accidental.N));
        allNotes.add(new MusicalNote(7458.62, NoteNames.B, 8, Accidental.b));
        allNotes.add(new MusicalNote(7902.13, NoteNames.B, 8, Accidental.N));
    }
}
