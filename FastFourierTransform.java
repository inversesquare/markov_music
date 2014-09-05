package markov_music;

public class FastFourierTransform {
    
    public static final int FORWARD_TRANSFORM = 1;
    public static final int INVERSE_TRANSFORM = -1;
    
    /*
     * The return array stores the frequencies as follows,
     * for the case that ret.length == 2 * N
     * and delta is the sampling time duration
     * 
     * ret[0] = real part of zero frequency
     * ret[1] = imaginary part of zero frequency
     * ret[2] = real part of frequency == 1 / (N * delta)
     * ret[3] = imaginary part of frequency == 1 / (N * delta)
     * ...
     * ret[N+1] = real part of frequency == 1 / (2 * delta)
     * ret[N+2] = imaginary part of frequency == 1 / (2 * delta)
     * ...
     * ret[2*N-1] = real part of frequency == - 1 / (N * delta)
     * ret[2*N] = imaginary part of frequency == - 1 / (N * delta)
     */
    
    public static double [] four1(double [] data, int isign)
    {
        if (!IsPowerOfTwo(data.length)) {
            throw new IllegalArgumentException("Input data must be an array with a power of two length.");
        }

        double [] ret = new double[data.length + 1];
        
        int nn = data.length / 2;
        int n = 0;
        int mmax = 0;
        int m = 0;
        int i = 0;
        int j = 0;
        int istep = 0;
        
        double wtemp = 0.0;
        double wr = 0.0;
        double wpr = 0.0;
        double wpi = 0.0;
        double wi = 0.0;
        double theta = 0.0;
        
        double tempr = 0.0;
        double tempi = 0.0;
        
        // Seed the return value with the input
        // The original Numerical Recipes algorithm operates in-place
        // on an array that starts at index 1 (wat.)
        System.arraycopy(data, 0, ret, 1, data.length);
        
        n = nn << 1;
        j = 1;
        for (i = 1; i < n; i += 2)
        {
            if (j > i) {
                swap(ret, j, i);
                swap(ret, j + 1, i + 1);
            }
            m = n >> 1;
            while (m >= 2 && j > m)
            {
                j -= m;
                m >>= 1;
            }
            j += m;
        }
        
        mmax = 2;
        while (n > mmax)
        {
            istep = mmax << 1;
            theta = isign * (Math.PI * 2.0 / mmax);
            wtemp = Math.sin(0.5 * theta);
            wpr = -2.0 * wtemp * wtemp;
            wpi = Math.sin(theta);
            wr = 1.0;
            wi = 0.0;
            for (m = 1; m < mmax; m += 2)
            {
                for (i = m; i <= n; i += istep)
                {
                    j = i + mmax;
                    tempr = (wr * ret[j]) - (wi * ret[j+1]);
                    tempi = (wr * ret[j+1]) + (wi * ret[j]);
                    ret[j] = ret[i] - tempr;
                    ret[j+1] = ret[i+1] - tempi;
                    ret[i] += tempr;
                    ret[i+1] += tempi;
                }
                wtemp = wr;
                wr = (wtemp * wpr) - (wi * wpi) + wr;
                wi = (wi * wpr) + (wtemp * wpi) + wi;
            }
            mmax = istep;
        }
        
        return ret;
    }
    
    private static final void swap(double[] data, int i, int j)
    {
        double tmp = data[i];
        data[i] = data[j];
        data[j] = tmp;
    }
    
    public static final boolean IsPowerOfTwo(int x)
    {
        return (x != 0) && ((x & (x - 1)) == 0);
    }

}
