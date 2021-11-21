public class Timer {
    private long start;
    private long end;

    public void start() {
        start = System.currentTimeMillis();
    }

    public void end() {
        end = System.currentTimeMillis();
    }

    public float getTimeSeconds() {
        return (end - start) / 1000F;
    }

    public float getTimeMinutes() {
        return (end - start) / (60*1000F);
    }
}
