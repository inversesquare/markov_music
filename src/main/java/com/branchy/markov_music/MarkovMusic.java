package main.java.com.branchy.markov_music;

import java.awt.Color;
import java.util.Arrays;

import main.java.com.branchy.libdata.DataWriter;
import main.java.com.branchy.libdata.LogWriter;
import main.java.com.branchy.libimage.RainbowColor;
import main.java.com.branchy.libmath.FastFourierTransform;
import main.java.com.branchy.libmath.PowerSpectrumWaterfall;
import main.java.com.branchy.libmp3.Mp3Wrapper;
import main.java.com.branchy.libmp3.WavWrapper;
import main.java.com.branchy.libnote.MusicalNoteGrid;

public class MarkovMusic {
    
    // http://stackoverflow.com/questions/22114699/decode-mp3-file-with-jlayer-on-android-devices
    // http://stackoverflow.com/questions/12099114/decoding-mp3-files-with-jlayer

    private static String input_path = "D:\\data\\demo\\markov_music\\input\\";
    private static String output_path = "D:\\data\\demo\\markov_music\\output\\";

//  String file_in = input_path + "440hz.mp3";
//  String file_in = input_path + "Handel - Water Music Suite.mp3";
//  String file_in = input_path + "dragostea.mp3";
    private static String file_in = input_path + "let_it_go.mp3";
//    private static String file_in = input_path + "fools_rush_in.mp3";
    private static String log_file = output_path + "log.txt";
    private static String data_file = output_path + "fft.txt";
    private static String image_file = output_path + "waterfall.jpg";
    private static String notes_file = output_path + "notes.txt";
//    private static String file_out = output_path + "output.mp3";
    private static String wav_file_out = output_path + "output.wav";
    
    private static LogWriter log;
    
    private static int chunkSize = 8192; // 8192 - number of 44kHz samples per FFT. 8192 / 44100 = 0.19 seconds
    private static int num_freq_log = 1280; // 1280 - number of frequency bins in log space
    private static double freq_min = 55.0; // 110.0 - minimum frequency to look for
    private static double freq_max = 3000.0; // 2000.0 - maximum frequency to look for
    private static double num_stddev = 0.8;  // 1.5 - threshold for detecting notes: number of standard deviations above the mean
    
    private static double wavSampleRate = 44100.0; // 44100.0 - sample rate for the output wav file

    public static void main(String[] args) {
        log = new LogWriter(log_file);
        
        Mp3Wrapper mp3 = new Mp3Wrapper(file_in, log, 360);
        int len = mp3.size();
        
        short [] data = mp3.data_both();
        
        try {
            PowerSpectrumWaterfall psw = new PowerSpectrumWaterfall(
                    data,
                    mp3.sampleFrequency(),
                    chunkSize,
                    num_freq_log,
                    freq_min,
                    freq_max
                    );
//            WaterfallTest(psw);
            WaterfallImage wfi = new WaterfallImage(psw, 1, image_file, log);
            MusicalNoteGrid mng = WaterfallToGrid.WaterfallToNoteGrid(psw, num_stddev);
            mng.WriteNotes(notes_file);
            double [] waveform = mng.GenerateWaveform(wavSampleRate);
            WavWrapper.WriteWav(wav_file_out, waveform);
        } catch (Exception ex)
        {
            log.write(ex.getMessage());
        }
        log.close();

    }
    
    private static void WaterfallTest(PowerSpectrumWaterfall psw)
    {
        DataWriter dw = new DataWriter(data_file, Arrays.asList("Frequency", "Power"));
        double [] sample = psw.GetOneLogSpectra(10);  // psw.GetOneSpectra(10);
        double [] freq = psw.GetLogFrequency(); // psw.GetFrequency();
        for (int i = 0; i < sample.length; i++)
        {
            dw.write(DataWriter.join(Arrays.asList(format(freq[i]), format(sample[i])), "\t"));
        }
        dw.close();
    }
    
    private static void FFTTest(short[] data)
    {
        int N = 4096;
        double [] test = new double[N];
        for (int i = 0; i < N; i++)
        {
            test[i] = (double)data[i+(N*3)];
        }
        
        DataWriter dw = new DataWriter(data_file, Arrays.asList("Real", "Imaginary", "Power"));
        double [] fft = FastFourierTransform.four1(test, FastFourierTransform.FORWARD_TRANSFORM);

        for (int i = 0; i < (fft.length / 2); i++)
        {
            double real = fft[i*2];
            double imag = fft[(i*2)+1];
            double pow = Math.sqrt((real * real) + (imag * imag));
            dw.write(
                    DataWriter.join(
                            Arrays.asList(format(real), format(imag), format(pow)),
                            "\t")
                    );
        }
        dw.close();
    }
    
    private static void ColorTest()
    {
        String color_test_file = output_path + "color_text.txt";
        DataWriter dw_color = new DataWriter(color_test_file, Arrays.asList("Value", "Red", "Green", "Blue"));
        for (int i = 0; i < 100; i++)
        {
            double f = i / 100.0;
            Color c = RainbowColor.GetRainbow(f);
            dw_color.write(DataWriter.join(Arrays.asList(format(f), format(c.getRed()), format(c.getGreen()), format(c.getBlue())), "\t"));
        }
        dw_color.close();
    }
    
    private static String format(double x)
    {
        return String.format("%014.4f", x);
    }

}
