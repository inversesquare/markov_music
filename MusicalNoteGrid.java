package markov_music;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/** Used to create a primitive song: Musical notes on a fixed grid.
 *  <p>
 *  At each time step, there is a list of musical notes that represent
 *  the chord playing.
 *  
 * @author      Justin Libby <justin.libby @ gmail.com>
 * @version     1.0
 * @since       2014-09-12
 */

public class MusicalNoteGrid {
    
    /**
     * Number of time steps in the grid. e.g. 536
     */
    private int width;
    
    /**
     * The notes that make up the song. These are sparsely stored in a list of lists.
     */
    private List<List<MusicalNote>> recordedNotes;
    
    /**
     * Time, in seconds, for each step in the song.
     * This is an array so we don't have to deal with boxing/unboxing for doing maths.
     */
    private double [] time;
    
    public MusicalNoteGrid(int num_chunks, double [] time_in)
    {
        width = num_chunks;

        // Keep our own copy of the time array
        time = time_in.clone();

        // Initialize the recorded notes to a list of empty lists
        recordedNotes = new ArrayList<List<MusicalNote>>(width);
        for (int i = 0; i < width; i++)
        {
            recordedNotes.add(new ArrayList<MusicalNote>());
        }
    }
    
    /**
     * Adds a single note to the grid at a specific chunk.
     * @param n          Musical note to add.
     * @param chunkNum   Location in the grid.
     */
    public void AddOneNote(MusicalNote n, int chunkNum)
    {
        recordedNotes.get(chunkNum).add(n);
    }
    
    /**
     * Write this grid out to an array that represents the acoustic waveform
     * 
     * @param  wavSampleRate   The sample frequency of the output array, e.g. 44100.0 Hz
     * @return                 The waveform array, scaled between +/- 1.0
     */
    public double[] GenerateWaveform(double wavSampleRate)
    {
        // Number of short timesteps in the total output waveform array
        int num_ticks = (int)(wavSampleRate * totalTime());
        double [] waveform = new double [num_ticks];

        // Holds a piece of the waveform for each chunk time step
        double [] chunkTmp = new double[(int)(deltaTime() * wavSampleRate)];
        
        // Counter to keep track of the time index
        int counter = 0;
        for (List<MusicalNote> goodNotes : recordedNotes)
        {
            // Reset the temp storage
            for (int i = 0; i < chunkTmp.length; i++)
            {
                chunkTmp[i] = 0.0;
            }
            // Add each good note to the output waveform in this time chunk
            for (MusicalNote note : goodNotes)
            {
                addOneFreq(
                        note.Frequency(),
                        note.Amplitude(),
                        chunkTmp,
                        1.0 / wavSampleRate,
                        deltaTime() * counter
                        );
            }
            // Copy the temporary chunk waveform into the total waveform
            System.arraycopy(chunkTmp, 0, waveform, counter * chunkTmp.length, chunkTmp.length);
            counter += 1;
        }
        
        // Now normalize the waveform to +/- 1.0
        // Search for the largest value
        double max = 0.0;
        for (int i = 0; i < waveform.length; i++)
        {
            if (Math.abs(waveform[i]) > max)
            {
                max = Math.abs(waveform[i]);
            }
        }
        
        // Normalize the output by dividing by the largest value
        for (int i = 0; i < waveform.length; i++)
        {
            waveform[i] /= max;
        }
        
        return waveform;
    }
    
    /**
     * @return  Total duration of the "song"
     */
    private double totalTime()
    {
        return deltaTime() * time.length;
    }
    
    /**
     * @return  Amount of time between steps in the grid, expressed in seconds
     */
    private double deltaTime()
    {
        // XXX - This makes the (hopefully valid) assumption that there are at least two time steps.
        return (time[1] - time[0]);
    }
    
    /**
     * Helper function for GenerateWaveForm
     * Adds a single frequency to the output array
     * 
     * @param freq    Frequency to add
     * @param amp     Amplitude to add (arbitrary units)
     * @param tmp     Reference to the array to be mutated
     * @param dt      Amount of time between steps in the tmp array, expressed in seconds
     * @param t       Total time, in seconds, as of the start of the tmp array. Important for phase matching.
     */
    private static void addOneFreq(
            double freq,
            double amp,
            double [] tmp,
            double dt,
            double t
            )
    {
        // Keep the phase consistent for each note
        double phase = 2.0 * Math.PI * freq * t;
        double twoPIfDT = 2.0 * Math.PI * freq * dt;
        for (int i = 0; i < tmp.length; i++)
        {
            tmp[i] += amp * Math.sin((twoPIfDT * i) + phase);
        }
    }
    
    /**
     * Write all the recorded notes out to a file
     * <p>
     * This output file is a sparsely populated, tab delimited
     * record of the grid.
     * @param notes_file_out    Full path of the output file
     */
    // XXX - TODO: write a method to read this file back in!
    public void WriteNotes(String notes_file_out)
    {
        // Construct the column headers, one for time plus one for each valid note
        List<String> headers = new ArrayList<String>();
        headers.add("Time");
        for (MusicalNote n : MusicalNote.allNotes)
        {
            headers.add(n.FullName());
        }
        DataWriter dw = new DataWriter(notes_file_out, headers);
        
        // This counter will keep track of our position in the time array
        int counter = 0;

        // Walk down the list of all recorded notes
        for (List<MusicalNote> goodNotes : recordedNotes)
        {
            // oneLine will keep our running list of output note time + amplitudes
            List<String> oneLine = new ArrayList<String>();

            // First element is the time, in seconds, in the song
            oneLine.add(String.format("%014.4f", time[counter]));

            // Handle degenerate case of no good notes
            if (goodNotes.size() == 0)
            {
                for (MusicalNote n : MusicalNote.allNotes)
                {
                    oneLine.add("0");
                }
                dw.write(DataWriter.join(oneLine, "\t"));
                // Increment the time array counter
                counter += 1;
                // Move on to the next chunk
                continue;
            }
            
            // XXX - This used to be a simple lookup:
            // Is the current good note in the Set of all good notes?
            // After introducing amplitude, this doesn't work.
            // The .equals() test doesn't check for equality because of this.
            // TODO: split MusicalNote into two classes, one without amplitude
            // Use the non-amplitude containing class to test for
            // inclusion in the set of all musical notes
            
            // Sort the song's notes to compare against the master list of notes
            // The master list is already sorted
            // Why? We need to fill in the valid amplitudes and "0"s in order
            Collections.sort(goodNotes);

            // This is an index into the list of goodNotes
            // It will point to the note we're about to add to oneLine
            // Initialize the index of our good note to zero
            // ... but we may not be able to write out the first note.
            int goodNote_index = 0;
            
            // Grab the first note from the now sorted chord
            MusicalNote test_note = goodNotes.get(0);
            
            // Deal with the case where the lowest good note may be below the scale
            if (test_note.Frequency() < MusicalNote.MinNoteFreq())
            {
                // Walk up the list of good notes until we find one in the scale of valid notes
                while (test_note.Frequency() < MusicalNote.MinNoteFreq())
                {
                    goodNote_index += 1;
                    if (goodNote_index < goodNotes.size())
                    {
                        // Index is still in safe range to grab the next good note
                        test_note = goodNotes.get(goodNote_index);
                    } else {
                        // We ran off the end of the good notes list.
                        // That's ok - the follow up comparison tests will get no matches
                        // and we'll end up with a row of no notes.
                        // This would happen if all of the notes were lower than the lowest valid note.
                        break;
                    }
                }
            }
            
            // XXX - Room for performance improvement.
            // Walk up all valid notes to construct our output oneLine
            // Either write a "0" for no match, or the amplitude of the note
            for (MusicalNote n : MusicalNote.allNotes)
            {
                // Note is off the scale - move on
                // We already tested for notes below the scale
                if (goodNote_index >= goodNotes.size())
                {
                    oneLine.add("0");
                    continue;
                }
                
                // Found a matching note
                if (test_note.Frequency() == n.Frequency())
                {
                    // Write out the amplitude, in arbitrary units
                    oneLine.add(String.format("%014.4f", test_note.Amplitude()));
                    // Increment our index into the good note list
                    // before moving to the next valid note
                    goodNote_index += 1;
                    // Don't run off the end
                    if (goodNote_index >= goodNotes.size())
                    {
                        continue;
                    }
                    // Set the next test note
                    test_note = goodNotes.get(goodNote_index);
                    continue;
                }
                
                // No match
                oneLine.add("0");
            }
            dw.write(DataWriter.join(oneLine, "\t"));
            // Increment the time array counter
            counter += 1;
        }
    }
    


}
