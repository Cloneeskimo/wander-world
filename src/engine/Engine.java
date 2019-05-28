package engine;

import engine.logic.Logic;
import engine.utils.Timer;
import engine.utils.Utils;

//Error Codes Used: 0

public class Engine implements Runnable {

    //Static Data
    private static final int MAX_FPS = 60;
    private static final int MAX_UPS = 30;

    //Data
    private Logic logic;
    private Timer timer;
    private Window window;
    private Thread loopThread;

    //Constructor
    public Engine(Logic startingLogic) {
        this.loopThread = new Thread(this, "LOOP_THREAD"); //create loop thread
        this.window = new Window(Window.DEFAULT_WIDTH, Window.DEFAULT_HEIGHT, Window.DEFAULT_TITLE + Utils.VERSION
                + "b" + Utils.BUILD_NO, Window.DEFAULT_VYSNC); //create window
        this.logic = startingLogic; //set logic reference
        this.timer = new Timer(); //create timer
    }

    //Initialization Method
    public void init() {
        this.window.init(); //initialize window
        this.logic.init(this.window); //initialize current logic
        this.timer.init(); //initialize timer
    }

    //Start Method
    public void start() {
        if (System.getProperty("os.name").contains("Mac")) { //if mac,
            System.setProperty("java.awt.headless", "true"); //set awt headless to true
            this.loopThread.run(); //run on same thread
        } else this.loopThread.start(); //otherwise, start the thread
    }

    //Run Method
    @Override
    public void run() {
        try {
            this.init(); //initialize the engine
            this.loop(); //run the loop
        } finally {
            this.cleanup(); //cleanup at the end
        }
    }

    //Logic-Changing Method
    /**
     * @param logic the logic to change the engine to follow
     * @param init whether to call the logic's init function
     * @param cleanup whether to cleanup the previous logic
     */
    public void changeLogic(Logic logic, boolean init, boolean cleanup) {
        if (cleanup) this.logic.cleanup(); //cleanup previous logic if cleanup is true
        this.logic = logic; //set logic reference
        if (init) this.logic.init(this.window); //initialize logic if init is true
    }

    //Game Loop Method
    private void loop() {

        //timekeeping variables
        float deltaTime, accumulation = 0f;
        final float interval = 1f / Engine.MAX_UPS;

        //loop until window needs to close
        while (!this.window.shouldClose()) {

            //timekeeping
            deltaTime = this.timer.getDeltaTime();
            accumulation += deltaTime;

            //input
            this.input();

            //update
            while (accumulation >= interval) {
                this.update(deltaTime);
                accumulation -= interval;
            }

            //render
            this.render();
            if (!this.window.isVSync()) this.sync();
        }
    }

    //Manual Sync Method
    private void sync() {
        double endTime = this.timer.getLastLoopTime() + (1f / Engine.MAX_FPS); //get end time of current loop
        while (Timer.getTime() < endTime) { //until that time comes
            try {
                Thread.sleep(1); //sleep for a millisecond
            } catch (Exception e) {
                Utils.error("Unable to manual sync: " + e.getMessage(), "engine.Engine", 0, Utils.WARNING);
            }
        }
    }

    //Input, Update, Render Methods
    private void input() { this.logic.input(); }
    private void update(float dT) { this.logic.update(dT); }
    private void render() { this.logic.render(); this.window.swapBuffers(); }

    //Cleanup Method
    private void cleanup() {
        this.logic.cleanup(); //cleanup the logic
    }
}
