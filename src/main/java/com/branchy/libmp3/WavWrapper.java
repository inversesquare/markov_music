package main.java.com.branchy.libmp3;

import java.io.ByteArrayInputStream;
import java.io.File;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;

/** 
 *  Wrapper class to write raw data to a .wav file
 *  
 * @author      Justin Libby <justin.libby @ gmail.com>
 * @version     1.0
 * @since       2014-10-12
 */

public class WavWrapper {
    
    /**
     * Sample rate in Hertz. Caller has no choice.
     */
    private static final int wavSampleRate = 44100;

    /**
     * Max value for output data
     */
    private static final double shortMax = Short.MAX_VALUE;     // 32,767
    
    /**
     * Write normalized raw data out to a wav file.
     * 
     * @param file_out     Full file system path to output file, e.g. C:\output\foo.wav
     * @param waveform     Normalized [0, 1] data array to write out.
     */
    public static void WriteWav(String file_out, double [] waveform)
    {
        // 16-bit, mono, signed PCM, little Endian
        AudioFormat format = new AudioFormat(wavSampleRate, 16, 1, true, false);
        // Allocate the output data, two bytes per sample
        byte[] data = new byte[2 * waveform.length];
        for (int i = 0; i < waveform.length; i++) {
            // Scale data up to full range of short type
            int temp = (short) (waveform[i] * shortMax);
            data[2*i + 0] = (byte) temp;
            data[2*i + 1] = (byte) (temp >> 8);
        }

        // Write the data out
        ByteArrayInputStream bais = new ByteArrayInputStream(data);
        AudioInputStream ais = new AudioInputStream(bais, format, waveform.length);
        try {
            AudioSystem.write(ais, AudioFileFormat.Type.WAVE, new File(file_out));
        } catch (Exception ex)
        {
            // XXX - log this or throw
            String s = ex.getMessage();
        }
    }

}
