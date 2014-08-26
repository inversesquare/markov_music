package markov_music;

public class MarkovMusic {
    
    // http://stackoverflow.com/questions/22114699/decode-mp3-file-with-jlayer-on-android-devices
    // http://stackoverflow.com/questions/12099114/decoding-mp3-files-with-jlayer

    public static void main(String[] args) {
        String input_path = "D:\\data\\demo\\markov_music\\input\\";
        String output_path = "D:\\data\\demo\\markov_music\\output\\";
        
//        String file_in = input_path + "440Hz_44100Hz_16bit_05sec.mp3";
        String file_in = input_path + "Handel - Water Music Suite.mp3";
        String file_out = output_path + "output.mp3";
        String log_file = output_path + "log.txt";
        
        LogWriter log = new LogWriter(log_file);
        
        Mp3Wrapper mp3 = new Mp3Wrapper(file_in, log, 360);
        
        int len = mp3.size();
        
        short [] data = mp3.data_left();
        
        log.close();

    }

}
