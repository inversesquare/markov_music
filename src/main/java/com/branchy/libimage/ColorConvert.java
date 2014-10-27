package main.java.com.branchy.libimage;

import java.awt.Color;

/** Converts color from RGB space to HSV space
 *  <p>
 *  The java.awt.Color class works in RGB color space.
 *  In order to calculate a nice rainbow, we need to 
 *  linearly interpolate in HSV (hue, saturation, value) space.
 *  
 * @author      Justin Libby <justin.libby @ gmail.com>
 * @version     1.0
 * @since       2014-09-12
 */

public class ColorConvert {
    /**
     * RGB values
     */
    private int red;
    private int green;
    private int blue;
    
    /**
     * HSV values
     */
    private double hue;
    private double saturation;
    private double value;
    
    /**
     * @return   The RGB version of the color stored in this class.
     */
    public Color GetColor()
    {
        return new Color(red, green, blue);
    }
    
    /**
     * Given a color in RGB, populate the HSV values at instantiation time.
     * 
     * @param c   The standard java.awt.Color to convert.
     */
    public ColorConvert(Color c)
    {
        red = c.getRed();
        green = c.getGreen();
        blue = c.getBlue();
        
        // Normalize the colors to [0, 1]
        double r = red / 255.0;
        double g = green / 255.0;
        double b = blue / 255.0;
        
        // Find the max and min out of the RGB values
        double cmax = Math.max(Math.max(r, g), b);
        double cmin = Math.min(Math.min(r, g), b);
        
        // What follows is a standard calculation to convert RGB to HSV
        // http://en.wikipedia.org/wiki/HSL_and_HSV
        // Note: units for hue are [0, 1], not [0, 360] as most implementations
        hue = cmax;
        saturation = cmax;
        value = cmax;
        
        double delta = cmax - cmin;
        // Avoid dividing by zero
        saturation = cmax == 0.0 ? 0.0 : delta / cmax;
        
        if (cmax == cmin)
        {
            hue = 0.0;
        } else {
            if (r == cmax)
            {
                hue = (g - b) / delta + (g < b ? 6 : 0);
            }
            if (g == cmax)
            {
                hue = (b - r) / delta + 2;
            }
            if (b == cmax)
            {
                hue = (r - g) / delta + 4;
            }
        }
        hue /= 6;
    }
    
    /**
     * Given HSV values, populate the RGB values at instantiation time
     * 
     * @param hue_in
     * @param saturation_in
     * @param value_in
     */
    public ColorConvert(double hue_in, double saturation_in, double value_in)
    {
        hue = hue_in;
        saturation = saturation_in;
        value = value_in;
        
        // What follows is a standard calcuation to convert HSV to RGB
        int i = (int)Math.floor(hue * 6);
        
        double f = hue * 6 - i;
        double p = value * (1 - saturation);
        double q = value * (1 - f * saturation);
        double t = value * (1 - (1 - f) * saturation);

        double r = 0.0;
        double g = 0.0;
        double b = 0.0;
        
        switch (i % 6) {
            case 0: 
                r = value;
                g = t;
                b = p;
                break;
            case 1:
                r = q;
                g = value;
                b = p;
                break;
            case 2:
                r = p;
                g = value;
                b = t;
                break;
            case 3:
                r = p;
                g = q;
                b = value;
                break;
            case 4:
                r = t;
                g = p;
                b = value;
                break;
            case 5:
                r = value;
                g = p;
                b = q;
                break;
        }
        
        red = (int)(r * 255);
        green = (int)(g * 255);
        blue = (int)(b * 255);
        
        // Safety check
        red = red > 255 ? 255 : red;
        red = red < 0 ? 0 : red;
        green = green > 255 ? 255 : green;
        green = green < 0 ? 0 : green;
        blue = blue > 255 ? 255 : blue;
        blue = blue < 0 ? 0 : blue;
    }
    
    /**
     * @return    The Hue for the color stored in this class.
     */
    public double GetHue()
    {
        return hue;
    }
    
    /**
     * @return    The Saturation for the color stored in this class.
     */
    public double GetSaturation()
    {
        return saturation;
    }
    
    /**
     * @return    The Value for the color stored in this class.
     */
    public double GetValue()
    {
        return value;
    }
}
