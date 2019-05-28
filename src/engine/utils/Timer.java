package engine.utils;

public class Timer {

    //Data
    private double lastLoopTime;

    //Initialization Method
    public void init() { this.lastLoopTime = Timer.getTime(); }

    public float getDeltaTime() {
        double currentTime = Timer.getTime();
        float deltaTime = (float)(currentTime - this.lastLoopTime);
        this.lastLoopTime = currentTime;
        return deltaTime;
    }

    //Time Acquisition Method
    public static double getTime() { return System.nanoTime() / 1000_000_000.0; }

    //Accessors
    public double getLastLoopTime() { return this.lastLoopTime; }
}
