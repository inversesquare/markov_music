package markov_music;

// XXX - include amplitude?

public class MusicalNote {
    public double frequency;
    public String name;
    public int octave;
    public String sharpFlat;
    
    public MusicalNote(double f, String n, int oct, String shFl)
    {
        frequency = f;
        name = n;
        octave = oct;
        sharpFlat = shFl;
    }
    
    public String FullName()
    {
        return name + String.format("%d", octave) + sharpFlat;
    }

    public boolean equals(Object obj) {
        if (!(obj instanceof MusicalNote))
        {
            return false;
        }
        if (obj == this)
        {
            return true;
        }

        MusicalNote mn = (MusicalNote) obj;
        return (frequency == mn.frequency &&
                name.equals(mn.name) &&
                octave == mn.octave &&
                sharpFlat.equals(mn.sharpFlat));
    }
}
