package markov_music;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import javazoom.jl.decoder.Bitstream;
import javazoom.jl.decoder.BitstreamException;
import javazoom.jl.decoder.Decoder;
import javazoom.jl.decoder.DecoderException;
import javazoom.jl.decoder.Header;
import javazoom.jl.decoder.SampleBuffer;


public class MarkovMusic {
	
	// http://stackoverflow.com/questions/22114699/decode-mp3-file-with-jlayer-on-android-devices
	// http://stackoverflow.com/questions/12099114/decoding-mp3-files-with-jlayer

	public static void main(String[] args) {
        String input_path = "D:\\data\\demo\\markov_music\\input\\";
        String output_path = "D:\\data\\demo\\markov_music\\output\\";
        
        String file_in = input_path + "Handel - Water Music Suite.mp3";
        String file_out = output_path + "output.mp3";
        String log_file = output_path + "log.txt";
        
        LogWriter log = new LogWriter(log_file);
        
        float totalMs = 0;
        boolean seeking = true;
        int startMs = 0;
        int maxMs = 5000;
        short[] data;
        
        int bufferSize = 1024;
        try {
            InputStream inputStream = new BufferedInputStream(new FileInputStream(file_in), bufferSize);
            Bitstream bitstream = new Bitstream(inputStream);
            Decoder decoder = new Decoder();
            SampleBuffer output = null;

            boolean done = false;
            while (! done) {
                Header frameHeader = bitstream.readFrame();
                if (frameHeader == null) {
                    done = true;
                } else {
                    totalMs += frameHeader.ms_per_frame();

                    if (totalMs >= startMs) {
                        seeking = false;
                    }

                    if (!seeking) {
                        output = (SampleBuffer) decoder.decodeFrame(frameHeader, bitstream);

                        log.write("LEN: " + output.getBufferLength());

                        if (output.getSampleFrequency() != 44100 || output.getChannelCount() != 2) {
                            log.write("mono or non-44100 MP3 not supported");
                        }
                    }

                    if (totalMs >= (startMs + maxMs)) {
                        done = true;
                    }
                }
                bitstream.closeFrame();
            }

            data = output.getBuffer();
        } catch (IOException e) {
        	log.write(e.getMessage());
        } catch (BitstreamException e) {
			log.write(e.getMessage());
		} catch (DecoderException e) {
			log.write(e.getMessage());
		}        
        
        log.close();

	}

}
