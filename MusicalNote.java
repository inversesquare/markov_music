package markov_music;

// XXX - include amplitude?

public class MusicalNote implements Comparable<MusicalNote>{
    public double frequency;
    public String name;
    public int octave;
    public String sharpFlat;
    public double amplitude;
    
    public MusicalNote(double f, String n, int oct, String shFl)
    {
        frequency = f;
        name = n;
        octave = oct;
        sharpFlat = shFl;
        amplitude = 1.0;
    }
    
    public String FullName()
    {
        return name + String.format("%d", octave) + sharpFlat;
    }

    // XXX - don't compare amplitude when testing for equality
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

    @Override
    public int compareTo(MusicalNote n) {
        if (frequency < n.frequency)
        {
            return -1;
        }
        if (frequency > n.frequency)
        {
            return 1;
        }
        return 0;
    }
}
