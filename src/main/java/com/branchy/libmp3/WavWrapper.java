package main.java.com.branchy.libmp3;

import java.io.ByteArrayInputStream;
import java.io.File;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;

public class WavWrapper {
    
    private static final int wavSampleRate = 44100;
    private static final double shortMax = Short.MAX_VALUE;     // 32,767
    
    public static void WriteWav(String file_out, double [] waveform)
    {
        // 16-bit, mono, signed PCM, little Endian
        AudioFormat format = new AudioFormat(wavSampleRate, 16, 1, true, false);
        byte[] data = new byte[2 * waveform.length];
        for (int i = 0; i < waveform.length; i++) {
            int temp = (short) (waveform[i] * shortMax);
            data[2*i + 0] = (byte) temp;
            data[2*i + 1] = (byte) (temp >> 8);
        }

        ByteArrayInputStream bais = new ByteArrayInputStream(data);
        AudioInputStream ais = new AudioInputStream(bais, format, waveform.length);
        try {
            AudioSystem.write(ais, AudioFileFormat.Type.WAVE, new File(file_out));
        } catch (Exception ex)
        {
            String s = ex.getMessage();
        }
    }

}
