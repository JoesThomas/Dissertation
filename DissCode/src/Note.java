class Note {
    private int pitch;
    private int volume;
    private int duration;

    int getPitch() {
        return pitch;
    }

    int getVolume() {
        return volume;
    }

    int getDuration() {
        return duration;
    }

    Note(int pitch, int volume, int duration) {
        this.pitch = pitch;
        this.volume = volume;
        this.duration = duration;

    }
}
