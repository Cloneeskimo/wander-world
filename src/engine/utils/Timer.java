package engine.utils;

public class Timer {

    //Data
    private double lastLoopTime;

    //Init Method
    public void init() { this.lastLoopTime = Timer.getTime(); }

    /**
     * @return the amount of time (delta time) since the last call of this method
     */
    public float getDeltaTime() {
        double currentTime = Timer.getTime();
        float deltaTime = (float)(currentTime - this.lastLoopTime);
        this.lastLoopTime = currentTime;
        return deltaTime;
    }

    /**
     * @return the current time
     */
    public static double getTime() { return System.nanoTime() / 1000_000_000.0; }

    /**
     * @return the time that getDeltaTime() was called last
     */
    public double getLastLoopTime() { return this.lastLoopTime; }
}
