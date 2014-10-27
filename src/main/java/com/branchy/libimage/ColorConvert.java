package main.java.com.branchy.libimage;

import java.awt.Color;

public class ColorConvert {
    public int red;
    public int green;
    public int blue;
    public double hue;
    public double saturation;
    public double value;
    
    public Color GetColor()
    {
        return new Color(red, green, blue);
    }
    
    // Given a color in RGB, populate the HSV values
    public ColorConvert(Color c)
    {
        red = c.getRed();
        green = c.getGreen();
        blue = c.getBlue();
        
        double r = red / 255.0;
        double g = green / 255.0;
        double b = blue / 255.0;
        
        double cmax = Math.max(Math.max(r, g), b);
        double cmin = Math.min(Math.min(r, g), b);
        hue = cmax;
        saturation = cmax;
        value = cmax;
        
        double delta = cmax - cmin;
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
    
    // Given HSV, populate the RGB values
    public ColorConvert(double hue_in, double saturation_in, double value_in)
    {
        hue = hue_in;
        saturation = saturation_in;
        value = value_in;
        
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
}
