package engine;

import engine.logic.Logic;
import engine.utils.Utils;

public class Engine implements Runnable {

    //Data
    private Logic logic;
    private Window window;
    private Thread loopThread;

    //Constructor
    public Engine(Logic startingLogic) {
        this.loopThread = new Thread(this, "LOOP_THREAD"); //create loop thread
        this.window = new Window(Window.DEFAULT_WIDTH, Window.DEFAULT_HEIGHT, Window.DEFAULT_TITLE + Utils.VERSION
                + "b" + Utils.BUILD_NO, Window.DEFAULT_VYSNC); //create window
        this.logic = startingLogic; //set logic reference
    }

    //Initialization Method
    public void init() {
        this.window.init(); //initialize window
        this.logic.init(this.window); //initialize current logic
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

        //loop until window needs to close
        while (!this.window.shouldClose()) {

            //input, update, render
            this.input();
            this.update();
            this.render();
        }
    }

    //Input, Update, Render Methods
    private void input() { this.logic.input(); }
    private void update() { this.logic.update(); }
    private void render() { this.logic.render(); this.window.swapBuffers(); }

    //Cleanup Method
    private void cleanup() {
        this.logic.cleanup(); //cleanup the logic
    }
}
