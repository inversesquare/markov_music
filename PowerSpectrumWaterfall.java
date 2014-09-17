package markov_music;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PowerSpectrumWaterfall {
    
    private double [][] spectra;
    private double [][] spectra_log;
    private double [] freq;
    private double [] freq_log;
    // Passed in by user to set bounds on logarithmic frequency resize
    private double freq_log_min;
    private double freq_log_max;
    private int num_freq_log;
    private double [] time;
    private int chunk_size;
    private int spectra_size;
    private int num_chunks;
    private double sampling_frequency;
    private double max_power;
    private double min_power;
    private double max_log_power; // max and min for the log/log grid
    private double min_log_power;
    private double median_log_power;
    private double stddev_log_power;
    
    public PowerSpectrumWaterfall(
            short [] data,
            double sampling_frequency_in,
            int chunk_size_in,
            int num_freq_log_in,
            double freq_min,
            double freq_max)
    {
        num_freq_log = num_freq_log_in;
        chunk_size = chunk_size_in;
        spectra_size = chunk_size / 2;
        sampling_frequency = sampling_frequency_in;
        max_power = 0.0;
        min_power = 1000000.0;
        max_log_power = 0.0;
        min_log_power = 1000000.0;
        median_log_power = -1.0;
        stddev_log_power = -1.0;
        freq_log_max = freq_max;
        freq_log_min = freq_min;
        
        if (!FastFourierTransform.IsPowerOfTwo(chunk_size) || chunk_size < 8) {
            throw new IllegalArgumentException("Input data must be an array with a power of two length.");
        }
        
        if (freq_min <= 0.0)
        {
            throw new IllegalArgumentException("Minimum frequency must be a positive, nonzero number.");
        }
        
        // 75% overlap of the chunks makes for a smooth waterfall
        // The last chunk will be padded with zeroes
        num_chunks = (4 * (data.length / chunk_size)) + 1;
        spectra = new double[num_chunks][spectra_size];
        spectra_log = new double[num_chunks][num_freq_log];
        
        double [] tmp_data = new double[chunk_size];
        double [] tmp_pow = new double[spectra_size];
        freq = new double[spectra_size];
        freq_log = new double[num_freq_log];
        time = new double[num_chunks];
        double [] fft = null;
        int idx = 0;
        for (int i = 0; i < num_chunks; i++)
        {
            // Only moving half a chunk every step
            time[i] = (chunk_size / 4.0) * (i / sampling_frequency_in);
            // Populate this chunk for FFT
            for (int j = 0; j < chunk_size; j++)
            {
                idx = ((i * chunk_size) / 4) + j;
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
                double real = fft[(j * 2) + 1];
                double imag = fft[(j * 2) + 2];
                tmp_pow[j] = Math.sqrt((real * real) + (imag * imag));
            }
            
            // Fold over the spectrum in frequency space.
            // zero frequency was near zero index, negative frequencies near zero are near N-1
            for (int j = 0; j < spectra_size; j++)
            {
                freq[j] = j * (sampling_frequency / (spectra_size * 4.0));
                
                if (j % 2 == 0)
                {
                    spectra[i][j] = tmp_pow[j / 2];
                } else {
                    spectra[i][j] = tmp_pow[tmp_pow.length - 1 - (j / 2)];
                }
                
                // Compress the power spectrum for easier analysis
                spectra[i][j] = Math.log10(spectra[i][j] + 2);
                
                if (max_power < spectra[i][j])
                {
                    max_power = spectra[i][j];
                }
                if (min_power > spectra[i][j])
                {
                    min_power = spectra[i][j];
                }
            }
            
            // Resize the result into a log array
            PopulateLogSpectra(i);
        }
    }
    
    private void PopulateLogSpectra(int chunk_num)
    {
        double [] source = spectra[chunk_num];
        // keep track of the number of data from source that get binned
        // into the spectra_log column so we can normalize it out later
        double [] freq_log_counter = new double[freq_log.length];
        
        double max_freq_log = Math.log10(freq_log_max);
        double min_freq_log = Math.log10(freq_log_min);
        double delta = (max_freq_log - min_freq_log) / (freq_log.length);
        
        // Populate the new frequency array in log space
        for (int j = 0; j < freq_log.length; j++)
        {
            freq_log[j] = (j * delta) + min_freq_log;
        }
        
        // For each data in the source, find the appropriate bin
        // in freq_log and spectra_log
        double tmp = 0.0;
        int bin = 0;
        for (int j = 0; j < source.length; j++)
        {
            if (freq[j] <= 0.0)
            {
                continue;
            }
            tmp = Math.log10(freq[j]);
            bin = (int)((tmp - min_freq_log)/delta);
            if (bin >= 0 && bin < freq_log.length)
            {
                spectra_log[chunk_num][bin] += source[j];
                freq_log_counter[bin] += 1;
            }
        }
        
        // Now normalize the values
        double first_nonzero = 0.0;
        for (int j = 0; j < freq_log_counter.length; j++)
        {
            if (freq_log_counter[j] > 1) {
                spectra_log[chunk_num][j] /= freq_log_counter[j];
                if (max_log_power < spectra_log[chunk_num][j])
                {
                    max_log_power = spectra_log[chunk_num][j];
                }
                if (min_log_power > spectra_log[chunk_num][j])
                {
                    min_log_power = spectra_log[chunk_num][j];
                }
            }
            
            if (first_nonzero == 0.0 && spectra_log[chunk_num][j] != 0.0)
            {
                first_nonzero = spectra_log[chunk_num][j];
            }
        }
        
        // Fill in missing values with the nearest neighbor
        for (int j = 0; j < freq_log_counter.length; j++)
        {
            if (spectra_log[chunk_num][j] == 0.0)
            {
                spectra_log[chunk_num][j] = first_nonzero;
            } else
            {
                first_nonzero = spectra_log[chunk_num][j];
            }
        }
        
    }
    
    public double GetMedianLogPower()
    {
        // Don't calculate this more than once
        if (median_log_power > 0.0)
        {
            return median_log_power;
        }
        List<Double> tmp = ListifyLogGrid();
        Collections.sort(tmp);
        median_log_power = tmp.get((spectra_log[0].length * spectra_log.length) / 2);
        
        return median_log_power;
    }
    
    public double GetStdDevLogPower()
    {
        // Don't calculate this more than once
        if (stddev_log_power > 0.0)
        {
            return stddev_log_power;
        }
        
        List<Double> tmp = ListifyLogGrid();
        double mean = 0.0;
        for (Double d : tmp)
        {
            mean += d;
        }
        mean /= tmp.size();
        
        double variance = 0.0;
        for (Double d : tmp)
        {
            variance += (mean - d)*(mean - d);
        }
        variance /= tmp.size();
        stddev_log_power = Math.sqrt(variance);
        return stddev_log_power;
    }
    
    private List<Double> ListifyLogGrid()
    {
        List<Double> tmp = new ArrayList<Double>(spectra_log[0].length * spectra_log.length);
        for (int i = 0; i < spectra_log.length; i++)
        {
            for (int j = 0; j < spectra_log[0].length; j++)
            {
                tmp.add(spectra_log[i][j]);
            }
        }
        return tmp;
    }
    
    public int GetChunkSize()
    {
        return chunk_size;
    }
    
    public int GetSpectraSize()
    {
        return spectra_size;
    }
    
    public int GetSpectraLogSize()
    {
        return num_freq_log;
    }
    
    public int GetNumChunks()
    {
        return num_chunks;
    }
    
    public double[] GetOneSpectra(int i)
    {
        return spectra[i].clone();
    }
    
    public double[] GetOneLogSpectra(int i)
    {
        return spectra_log[i].clone();
    }
    
    public double[] GetFrequency()
    {
        return freq.clone();
    }
    
    public double[] GetLogFrequency()
    {
        return freq_log.clone();
    }
    
    public double[] GetTime()
    {
        return time.clone();
    }
    
    public double GetMaxPower()
    {
        return max_power;
    }
    
    public double GetMinPower()
    {
        return min_power;
    }
    
    public double GetMaxLogPower()
    {
        return max_log_power;
    }
    
    public double GetMinLogPower()
    {
        return min_log_power;
    }

}
