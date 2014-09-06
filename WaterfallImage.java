package markov_music;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class WaterfallImage {
    private File file_out;
    private BufferedImage img;
    private PowerSpectrumWaterfall psw;
    private LogWriter log;
    
    private int width; // width - number of chunks
    private int height; // height in the frequency direction
    private int ppg; // Pixels per grid spacing
    private int [][][] grid;
    private static int RED = 0;
    private static int GREEN = 1;
    private static int BLUE = 2;
    private static int NUM_COLOR_CHANNELS = 3;
    
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
        width = psw.GetNumChunks();
        height = psw.GetSpectraSize();
        
        grid = new int[NUM_COLOR_CHANNELS][width][height];
        
        PopulateGrid();
        SetupBufferedImage();
        file_out = new File(file_path_out);
        WriteImage();
    }
    
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
    
    private void WriteImage()
    {
        try {
            ImageIO.write(img, "jpg", file_out);
        } catch (IOException e) {
            log.write("Error writing output image: " + e.getMessage());
        }
    }
    
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
    
    private void PopulateGrid() {
        double [] oneCol = null;
        double delta = psw.GetMaxPower() - psw.GetMinPower();
        double min = psw.GetMinPower();
        double pow = 0.0;

        for (int gcol = 0; gcol < width; gcol++)
        {
            oneCol = psw.GetOneSpectra(gcol);
            for (int grow = 0; grow < height; grow++)
            {
                // Normalize power to range [0, 1]
                pow = (oneCol[grow] - min) / delta;
                Color c = RainbowColor.GetRainbow(pow);
                SetColor(gcol, grow, c);
            }
        }
    }

}
