package markov_music;

public class PowerSpectrumWaterfall {
    
    private double [][] spectra;
    private double [] freq;
    private int chunk_size;
    private int spectra_size;
    private int num_chunks;
    private double sampling_frequency;
    
    public PowerSpectrumWaterfall(short [] data, double sampling_frequency_in, int chunk_size_in)
    {
        chunk_size = chunk_size_in;
        spectra_size = chunk_size / 2;
        sampling_frequency = sampling_frequency_in;
        
        if (!FastFourierTransform.IsPowerOfTwo(chunk_size) || chunk_size < 8) {
            throw new IllegalArgumentException("Input data must be an array with a power of two length.");
        }
        
        // The last chunk will be padded with zeroes
        num_chunks = (data.length / chunk_size) + 1;
        spectra = new double[num_chunks][spectra_size];
        
        double [] tmp_data = new double[chunk_size];
        double [] tmp_pow = new double[spectra_size];
        freq = new double[spectra_size];
        double [] fft = null;
        int idx = 0;
        for (int i = 0; i < num_chunks; i++)
        {
            // Populate this chunk for FFT
            for (int j = 0; j < chunk_size; j++)
            {
                idx = (i * chunk_size) + j;
                if (idx < data.length)
                {
                    tmp_data[j] = (double)data[idx];
                } else {
                    tmp_data[j] = 0.0;
                }
            }
            
            fft = FastFourierTransform.four1(tmp_data, FastFourierTransform.FORWARD_TRANSFORM);
            
            // Convert the real and imaginary parts of the FFT to a power spectrum
            for (int j = 0; j < tmp_pow.length; j++)
            {
                double real = fft[j*2];
                double imag = fft[(j*2)+1];
                tmp_pow[j] = Math.sqrt((real * real) + (imag * imag));
            }
            
            // Fold over the spectrum in frequency space.
            // zero frequency was near zero index, negative frequencies near zero are near N-1
            for (int j = 0; j < spectra_size; j++)
            {
                freq[j] = j * (sampling_frequency / (spectra_size * 2.0));
                if (j % 2 == 0)
                {
                    spectra[i][j] = tmp_pow[j / 2];
                } else {
                    spectra[i][j] = tmp_pow[tmp_pow.length - 1 - (j / 2)];
                }
            }
        }
    }
    
    public int GetChunkSize()
    {
        return chunk_size;
    }
    
    public int GetSpectraSize()
    {
        return spectra_size;
    }
    
    public int GetNumChunks()
    {
        return num_chunks;
    }
    
    public double[] GetOneSpectra(int i)
    {
        return spectra[i].clone();
    }
    
    public double[] GetFrequency()
    {
        return freq.clone();
    }

}
