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

public class Mp3Wrapper {
    private LogWriter _log;
    private short[] _data_left;
    private short[] _data_right;
    private String _file_name;
    private int maxMs;
    private static int bufferSize = 4096;
    private int _frameCount;
    private int _bufferLength;
    
    public Mp3Wrapper(String file_name, LogWriter log, int max_seconds)
    {
        _log = log;
        _file_name = file_name;
        maxMs = max_seconds * 1000;
        _allocateData();
        _readData();
    }
    
    private void _allocateData()
    {
        try {
            InputStream inputStream = new BufferedInputStream(new FileInputStream(_file_name), bufferSize);
            Bitstream bitstream = new Bitstream(inputStream);
            Decoder decoder = new Decoder();
            SampleBuffer output = null;

            // Scan the input once to get total size of output buffer
            int startMs = 0;
            boolean seeking = true;
            float totalMs = 0;
            boolean done = false;
            _frameCount = 0;
            _bufferLength = 1;
            while (!done) {
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
                        _bufferLength = output.getBufferLength() > _bufferLength ? output.getBufferLength() : _bufferLength;
                        if (output.getSampleFrequency() != 44100)
                        {
                            _log.write("Warning: sample frequency is " + output.getSampleFrequency());
                        }
                        if (output.getChannelCount() != 2)
                        {
                            _log.write("Warning: number of channels is " + output.getChannelCount());
                        }
                    }

                    if (totalMs >= (startMs + maxMs)) {
                        done = true;
                    }
                }
                _frameCount += 1;
                bitstream.closeFrame();
            }
            // Allocate storage for the whole file and run through again
            _data_left = new short[((_frameCount + 1) * _bufferLength)/2];
            _data_right = new short[((_frameCount + 1) * _bufferLength)/2];
        } catch (IOException e) {
            _log.write(e.getMessage());
        } catch (BitstreamException e) {
            _log.write(e.getMessage());
        } catch (DecoderException e) {
            _log.write(e.getMessage());
        }
    }
    
    private void _readData()
    {
        try {
            InputStream inputStream = new BufferedInputStream(new FileInputStream(_file_name), bufferSize);
            Bitstream bitstream = new Bitstream(inputStream);
            Decoder decoder = new Decoder();
            SampleBuffer output = null;

            // Scan the input once to get total size of output buffer
            short[] tmp;
            int startMs = 0;
            boolean seeking = true;
            float totalMs = 0;
            boolean done = false;
            int frameNum = 0;
            
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
                        tmp = output.getBuffer();
                        // Copy to the big buffer
                        int idx = 0;
                        for (int i = 0; i < tmp.length - 1; i += 2, idx++)
                        {
                            _data_left[idx + (frameNum * _bufferLength / 2)] = tmp[i];
                            _data_right[idx + (frameNum * _bufferLength / 2)] = tmp[i+1];
                        }
                    }

                    if (totalMs >= (startMs + maxMs)) {
                        done = true;
                    }
                }
                bitstream.closeFrame();
                frameNum += 1;
            }
        } catch (IOException e) {
            _log.write(e.getMessage());
        } catch (BitstreamException e) {
            _log.write(e.getMessage());
        } catch (DecoderException e) {
            _log.write(e.getMessage());
        }
    }
    
    public short[] data_left()
    {
        return _data_left;
    }
    
    public short[] data_right()
    {
        return _data_right;
    }
    
    public int size()
    {
        return _data_left.length;
    }

}
