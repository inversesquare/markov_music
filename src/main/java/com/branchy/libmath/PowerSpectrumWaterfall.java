package main.java.com.branchy.libmath;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/** Takes in a set of data and calculates a series of power spectra on the data.
 * <p>
 * Useful for visualizing the frequency spectra of a set of data over time.
 * 
 * @author      Justin Libby <justin.libby @ gmail.com>
 * @version     1.0
 * @since       2014-09-12
 */

public class PowerSpectrumWaterfall {
    
    /**
     * The raw power spectra data
     */
    private double [][] spectra;
    /**
     * Power spectra re-sampled to a logarithmic frequency scale
     * and a logarithmic power scale.
     */
    private double [][] spectra_log;
    /**
     * Frequency scale for each spectra, expressed in Hertz
     */
    private double [] freq;
    /**
     * Logarithmic frequency scale. Note: different size from freq[]
     */
    private double [] freq_log;
    /**
     * Provided by the caller to set the bounds on the logarithmic frequency scale
     */
    private double freq_log_min;
    private double freq_log_max;
    private int num_freq_log;
    /**
     * Time, in seconds, corresponding to each step in the spectra data
     */
    private double [] time;
    /**
     * Number of samples of raw data that will constitute one spectra
     */
    private int chunk_size;
    /**
     * Actual size of one spectra -> chunk_size / 2
     */
    private int spectra_size;
    /**
     * Total number of spectra generated, given the input data size
     */
    private int num_chunks;
    /**
     * Sampling frequency of the input data, in Hertz.
     */
    private double sampling_frequency;
    /**
     * Max, min, median and stddev power for the two grids
     */
    private double max_power;
    private double min_power;
    private double max_log_power; // max and min for the log/log grid
    private double min_log_power;
    private double median_log_power;
    private double stddev_log_power;
    
    /**
     * Populate the spectra for a new PowerSpectrumWaterfall.
     * Calculations are all done at instantiation time.
     * 
     * @param data                     Input array of short data, in arbitrary units.
     * @param sampling_frequency_in    Sampling frequency, in Hertz, of the data.
     * @param chunk_size_in            Number of input samples to include in a single spectra. Must be a power of 2.
     * @param num_freq_log_in          Number of bins to use when calculating the logarithmic frequency scale.
     * @param freq_min                 Minimum frequency to calculate for the output power spectra.
     * @param freq_max                 Maximum frequency to calculate for the output power spectra.
     */
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
        // Initialize statistics to junk values
        // XXX - do this more eloquently
        max_power = 0.0;
        min_power = 1000000.0;
        max_log_power = 0.0;
        min_log_power = 1000000.0;
        median_log_power = -1.0;
        stddev_log_power = -1.0;
        
        // Set the min and max for the log frequency scale
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
        // Set up the output arrays
        spectra = new double[num_chunks][spectra_size];
        spectra_log = new double[num_chunks][num_freq_log];
        
        // Temporary arrays for calculating each spectra
        double [] tmp_data = new double[chunk_size];
        double [] tmp_pow = new double[spectra_size];
        
        // Initialize the frequency and time arrays
        freq = new double[spectra_size];
        freq_log = new double[num_freq_log];
        time = new double[num_chunks];
        
        // Index to keep track of where we pull the source data
        // Because the spectra overlap each other
        int idx = 0;
        // Iterate over each output spectra we will be calculating
        for (int i = 0; i < num_chunks; i++)
        {
            // Only moving a fraction of a chunk every step
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
            
            // Do the transform
            double [] fft = FastFourierTransform.four1(tmp_data, FastFourierTransform.FORWARD_TRANSFORM);
            
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
                
                // Grab the raw transform points from the zero index side
                // and from the far end of the array to calculate this single
                // positive frequency bin.
                if (j % 2 == 0)
                {
                    spectra[i][j] = tmp_pow[j / 2];
                } else {
                    spectra[i][j] = tmp_pow[tmp_pow.length - 1 - (j / 2)];
                }
                
                // Compress the power spectrum for easier analysis
                // Add two to avoid log(0)
                spectra[i][j] = Math.log10(spectra[i][j] + 2);
                
                // Keep hunting for global max and min
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
    
    /**
     * Helper to calculate the log frequency/log power array.
     * 
     * @param chunk_num    Coarse time index in the spectra array.
     */
    private void PopulateLogSpectra(int chunk_num)
    {
        double [] source = spectra[chunk_num];
        // keep track of the number of data from source that get binned
        // into the spectra_log column so we can normalize it out later
        double [] freq_log_counter = new double[freq_log.length];
        
        // Convert our bounds into log frequency space
        double max_freq_log = Math.log10(freq_log_max);
        double min_freq_log = Math.log10(freq_log_min);
        // Delta in log frequency space - needed to do a linear
        // interpolation in log space
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
            // Shouldn't happen
            if (freq[j] <= 0.0)
            {
                continue;
            }
            // Calculate the correct bin
            tmp = Math.log10(freq[j]);
            bin = (int)((tmp - min_freq_log)/delta);
            // If this data fits in a bin, add it and bump the counter
            if (bin >= 0 && bin < freq_log.length)
            {
                spectra_log[chunk_num][bin] += source[j];
                freq_log_counter[bin] += 1;
            }
        }
        

        // Keeps track of the first non-zero log spectra value
        // Lower frequency bins may be empty, and that looks "bad"
        // so we'll just fill them in with the nearest neighbor
        double first_nonzero = 0.0;
        // Now normalize the values based on the count per bin
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
            
            // Populate our "fill in" value that's nearest to zero frequency
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
                // Reset the nearest neighbor higher up the frequency scale
                first_nonzero = spectra_log[chunk_num][j];
            }
        }
        
    }
    
    /**
     * @return    Median power of the log spectra grid
     */
    public double GetMedianLogPower()
    {
        // Don't calculate this more than once
        if (median_log_power > 0.0)
        {
            return median_log_power;
        }
        // Calculate the median by shoving
        // all the values into a big list, sorting, and picking the middle
        List<Double> tmp = ListifyLogGrid();
        Collections.sort(tmp);
        median_log_power = tmp.get((spectra_log[0].length * spectra_log.length) / 2);
        
        return median_log_power;
    }
    
    /**
     * @return    The standard deviation of the log spectra grid
     */
    public double GetStdDevLogPower()
    {
        // Don't calculate this more than once
        if (stddev_log_power > 0.0)
        {
            return stddev_log_power;
        }
        
        // Calculate the median by shoving all the values into a big list
        // and running the stddev calculation
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
    
    /**
     * @return    List representation of the log spectra grid.
     */
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
    
    /**
     * @return     Number of elements in the logarithmic frequency scale
     */
    public int GetSpectraLogSize()
    {
        return num_freq_log;
    }
    
    /**
     * @return    Number of coarse time steps in the spectra grid
     */
    public int GetNumChunks()
    {
        return num_chunks;
    }
    
    /**
     * Returns a copy of one log spectra.
     * 
     * @param i   Index of the spectra to return
     * @return    The spectra
     */
    public double[] GetOneLogSpectra(int i)
    {
        return spectra_log[i].clone();
    }
    
    /**
     * @return    The logarithmic frequency scale for the spectra grid
     */
    public double[] GetLogFrequency()
    {
        return freq_log.clone();
    }
    
    /**
     * @return    The time scale for the spectra grid
     */
    public double[] GetTime()
    {
        return time.clone();
    }

}
