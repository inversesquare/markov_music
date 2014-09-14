package markov_music;

import java.awt.Color;
import java.util.Arrays;

public class MarkovMusic {
    
    // http://stackoverflow.com/questions/22114699/decode-mp3-file-with-jlayer-on-android-devices
    // http://stackoverflow.com/questions/12099114/decoding-mp3-files-with-jlayer

    public static void main(String[] args) {
        String input_path = "D:\\data\\demo\\markov_music\\input\\";
        String output_path = "D:\\data\\demo\\markov_music\\output\\";
        
//        String file_in = input_path + "440hz.mp3";
        String file_in = input_path + "Handel - Water Music Suite.mp3";
        String file_out = output_path + "output.mp3";
        String log_file = output_path + "log.txt";
        String data_file = output_path + "fft.txt";
        String image_file = output_path + "waterfall.jpg";
        
        LogWriter log = new LogWriter(log_file);
        
        Mp3Wrapper mp3 = new Mp3Wrapper(file_in, log, 360);
        
        int len = mp3.size();
        
        short [] data = mp3.data_left();
        
        int chunkSize = 8192;
        int num_freq_log = 1280;
        double freq_min = 110.0;
        double freq_max = 2000.0;
        try {
            PowerSpectrumWaterfall psw = new PowerSpectrumWaterfall(
                    data,
                    mp3.sampleFrequency(),
                    chunkSize,
                    num_freq_log,
                    freq_min,
                    freq_max
                    );
            DataWriter dw = new DataWriter(data_file, Arrays.asList("Frequency", "Power"));
            double [] sample = psw.GetOneLogSpectra(10);  // psw.GetOneSpectra(10);
            double [] freq = psw.GetLogFrequency(); // psw.GetFrequency();
            for (int i = 0; i < sample.length; i++)
            {
                dw.write(DataWriter.join(Arrays.asList(format(freq[i]), format(sample[i])), "\t"));
            }
            dw.close();
            WaterfallImage wfi = new WaterfallImage(psw, 1, image_file, log);
        } catch (Exception ex)
        {
            log.write(ex.getMessage());
        }

/*      FFT test
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
        */
        
        
        /* color test
        String color_test_file = output_path + "color_text.txt";
        DataWriter dw_color = new DataWriter(color_test_file, Arrays.asList("Value", "Red", "Green", "Blue"));
        for (int i = 0; i < 100; i++)
        {
            double f = i / 100.0;
            Color c = RainbowColor.GetRainbow(f);
            dw_color.write(DataWriter.join(Arrays.asList(format(f), format(c.getRed()), format(c.getGreen()), format(c.getBlue())), "\t"));
        }
        dw_color.close();
        */
        
        log.close();

    }
    
    private static String format(double x)
    {
        return String.format("%014.4f", x);
    }

}
