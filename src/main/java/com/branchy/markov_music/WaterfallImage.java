package main.java.com.branchy.markov_music;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import main.java.com.branchy.libdata.LogWriter;
import main.java.com.branchy.libimage.RainbowColor;
import main.java.com.branchy.libmath.PowerSpectrumWaterfall;

/** Transform a power spectrum waterfall into an output .jpg
 *  
 * @author      Justin Libby <justin.libby @ gmail.com>
 * @version     1.0
 * @since       2014-09-21
 */

public class WaterfallImage {
    /**
     * Full system path to output file
     */
    private File file_out;
    
    /**
     * Buffered image for writing out
     */

    private BufferedImage img;
    
    /**
     * The input power spectrum waterfall
     */

    private PowerSpectrumWaterfall psw;
    
    /**
     * Log writer for logging errors/exceptions
     */

    private LogWriter log;
    
    /**
     * Width of the output image == number of chunks (coarse time steps) in the waterfall
     */

    private int width;
    /**
     * Height of the output image == number of bins in the waterfall's log frequency scale
     */

    private int height;
    /**
     * Pixels per grid spacing
     */
    private int ppg;
    
    /**
     * Grid of calculated color data:
     * [color channel][column][row]
     */
    private int [][][] grid;
    
    /**
     * Indicies for the color channels
     */
    private static int RED = 0;
    private static int GREEN = 1;
    private static int BLUE = 2;
    private static int NUM_COLOR_CHANNELS = 3;
    
    /**
     * Write out the .jpg file at instantiation time
     * 
     * @param psw_in                     Source power spectrum waterfall
     * @param pixels_per_grid_spacing    Number of pixels per grid spacing, e.g. 2. Higher number == zooming in
     * @param file_path_out              Full filesystem path of output .jpg
     * @param log_in                     Log writer to log errors
     */
    // XXX - probably better to move image writing to
    // its own method
    public WaterfallImage(
            PowerSpectrumWaterfall psw_in,
            int pixels_per_grid_spacing,
            String file_path_out,
            LogWriter log_in
            )
    {
        psw = psw_in;
        log = log_in;
        ppg = pixels_per_grid_spacing;

        // Set up the grid
        width = psw.GetNumChunks();
        height = psw.GetSpectraLogSize();
        grid = new int[NUM_COLOR_CHANNELS][width][height];
        
        // Populate the color values
        PopulateGrid();
        
        // Prepare an image buffer with the correct dimensions and colors
        SetupBufferedImage();

        // Write out the .jpg
        file_out = new File(file_path_out);
        WriteImage();
    }
    
    /**
     * Helper to test if a row/col index is out of bounds
     * 
     * @param col    Grid column index to test
     * @param row    Grid row index to test
     * @return       True if one of the indicies is invalid.
     */
    private boolean indexIsInvalid(int col, int row)
    {
        if (row > height - 1 || col > width - 1) {
            return true;
        }
        if (row < 0 || col < 0)
        {
            return true;
        }
        return false;
    }
    
    /**
     * Helper to pull a single color from a row and column
     * 
     * @param col    Grid coulmn index
     * @param row    Grid row index
     * @return       Color value for the given row/col
     */
    private Color GetColor(int col, int row)
    {
        if (indexIsInvalid(col, row))
        {
            return null;
        }
        int red = grid[RED][col][row];
        int green = grid[GREEN][col][row];
        int blue = grid[BLUE][col][row];
        return new Color(red, green, blue);
    }
    
    /**
     * Helper to set a single color in the grid
     * 
     * @param col    Grid column index
     * @param row    Grid row index
     * @param c      Color value to set
     */
    private void SetColor(int col, int row, Color c)
    {
        if (indexIsInvalid(col, row) || c == null)
        {
            return;
        }
        
        grid[RED][col][row] = c.getRed();
        grid[GREEN][col][row] = c.getGreen();
        grid[BLUE][col][row] = c.getBlue();
    }
    
    /**
     * Helper to write out the image
     */
    private void WriteImage()
    {
        try {
            ImageIO.write(img, "jpg", file_out);
        } catch (IOException e) {
            log.write("Error writing output image: " + e.getMessage());
        }
    }
    
    /**
     * Helper to set up the correct dimensions and coloring of the
     * buffered image used for writing
     */
    public void SetupBufferedImage()
    {
        img = new BufferedImage(width * ppg, height * ppg, BufferedImage.TYPE_INT_RGB);
        
        for (int col = 0; col < img.getWidth(); col++) {
            for (int row = 0; row < img.getHeight(); row++) {
                int r = 0;
                int g = 0;
                int b = 0;
                Color c = GetColor(col / ppg, row / ppg);
                if (c != null)
                {
                    r = c.getRed();
                    g = c.getGreen();
                    b = c.getBlue();
                }
                int colInt = (r << 16) | (g << 8) | b;
                img.setRGB(col, row, colInt);
            }
        }
    }
    
    /**
     * Populate the grid from the input power spectra waterfall
     */
    private void PopulateGrid() {
        double [] oneCol = null;
        // Taking the max and min works ok, but does not highlight the dynamic range well
//        double delta = psw.GetMaxLogPower() - psw.GetMinLogPower();
//        double min = psw.GetMinLogPower();
        
        // Choose a threshold of 1.5 times the stddev
        // TODO: make this a setting for the caller?
        double delta = psw.GetStdDevLogPower() * 1.5;
        double min = psw.GetMedianLogPower() - (psw.GetStdDevLogPower() * 0.5);
        
        for (int gcol = 0; gcol < width; gcol++)
        {
            // Grab the log spectra for this time chunk
            oneCol = psw.GetOneLogSpectra(gcol);
            for (int grow = 0; grow < height; grow++)
            {
                // Normalize power to range [0, 1] -> subtract min and divide by delta
                // Low frequencies go on the "bottom" of the image, so flip
                double pow = (oneCol[height - grow - 1] - min) / delta;
                // Rainbow will saturate on red for values > 1.0, blue for values < 0.0
                Color c = RainbowColor.GetRainbow(pow);
                SetColor(gcol, grow, c);
            }
        }
    }

}
