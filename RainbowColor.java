package markov_music;

import java.awt.Color;

public class RainbowColor {
    
    private static Color RED = new Color(255, 0, 0);
    private static Color GREEN = new Color(0, 255, 0);
    private static Color BLUE = new Color(0, 0, 255);
    
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
    
    private static Color fadeToHSV(Color base, Color other, double fraction) {
        ColorConvert hsv1 = new ColorConvert(base);
        ColorConvert hsv3 = new ColorConvert(other);
        /*
         * I'm not sure we need to worry about the hue wrapping
        double h = 0.5;
        double hdelta = Math.abs(hsv1.hue - hsv3.hue);
        // hue wraps around in the range [0,1], so be sure to wrap the short way
        if (hdelta > 0.5) {
            if (hsv1.hue > hsv3.hue)
                h = hsv1.hue + hdelta*fraction;
            else
                h = hsv3.hue + hdelta*fraction;
            if (h > 1.0)
                h -= 1.0;
        }
        */
        double hue = hsv1.hue + (hsv3.hue-hsv1.hue)*fraction;
        double saturation = hsv1.saturation + (hsv3.saturation-hsv1.saturation)*fraction;
        double value = hsv1.value + (hsv3.value-hsv1.value)*fraction;
        ColorConvert hsv2 = new ColorConvert(hue, saturation, value);
        return hsv2.GetColor();
    }

}
