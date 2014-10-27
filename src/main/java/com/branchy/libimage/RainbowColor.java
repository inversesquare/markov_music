package main.java.com.branchy.libimage;

import java.awt.Color;

/** Provides a rainbow color palette.
 *  
 * @author      Justin Libby <justin.libby @ gmail.com>
 * @version     1.0
 * @since       2014-09-12
 */

public class RainbowColor {
    
    /**
     * The anchor values of RGB for our rainbow.
     * TODO: let the caller provide the anchor colors for the rainbow
     */
    private static Color RED = new Color(255, 0, 0);
    private static Color GREEN = new Color(0, 255, 0);
    private static Color BLUE = new Color(0, 0, 255);
    
    /**
     * Given a fraction from the range [0,1], return a color
     * from the rainbow palette.
     * 
     * @param fraction    A data value normalized to the range [0, 1]
     * @return            The rainbow color corresponding to the data value.
     */
    public static Color GetRainbow(double fraction)
    {
        Color ret = null;
        
        if (fraction < 0.0)
        {
            fraction = 0.0;
        }
        
        if (fraction > 1.0)
        {
            fraction = 1.0;
        }
        
        if (fraction <= 0.5)
        {
            ret = fadeToHSV(BLUE, GREEN, fraction*2.0);
        } else {
            ret = fadeToHSV(GREEN, RED, 2.0*(fraction - 0.5));
        }
        
        return ret;
    }
    
    /**
     * Given two colors, return a third color that is linearly between them
     * by a fraction [0, 1] in HSV space.
     * 
     * @param base      The first color
     * @param other     The second color
     * @param fraction  Fraction between the two colors [0, 1]
     * @return          The interpolated color
     */
    private static Color fadeToHSV(Color base, Color other, double fraction) {
        // Convert both colors into HSV space
        ColorConvert hsv1 = new ColorConvert(base);
        ColorConvert hsv3 = new ColorConvert(other);
        
        // Linear interpolation between the two values
        double hue = hsv1.GetHue() + (hsv3.GetHue() - hsv1.GetHue())*fraction;
        double saturation = 
                hsv1.GetSaturation() + (hsv3.GetSaturation() - hsv1.GetSaturation())*fraction;
        double value =
                hsv1.GetValue() + (hsv3.GetValue() - hsv1.GetValue())*fraction;

        // Convert back to RGB space
        ColorConvert hsv2 = new ColorConvert(hue, saturation, value);
        return hsv2.GetColor();
    }

}
