public class Timer {
    private String name;
    private long start;
    private long end;

    public Timer(String name) {
        this.name = name;
    }

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

    public float getTimeHours(){
        return (end - start) / (60*60*1000F);
    }

    public String getName() {
        return name;
    }
}
