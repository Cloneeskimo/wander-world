package engine;

import engine.logic.Logic;

public class Engine implements Runnable {

    //Data
    private Logic logic;
    private Window window;
    private Thread loopThread;

    //Constructor
    public Engine(Logic startingLogic) {
        this.loopThread = new Thread(this, "LOOP_THREAD");
        this.window = new Window();
        this.logic = startingLogic;
    }

    //Initialization Method
    public void init() {
        this.window.init();
        this.logic.init();
    }

    //Start Method
    public void start() {
        if (System.getProperty("os.name").contains("Mac")) {
            System.setProperty("java.awt.headless", "true");
            this.loopThread.run();
        } else this.loopThread.start();
    }

    //Run Method
    @Override
    public void run() {
        try {
            this.init();
            this.loop();
        } finally {
            this.cleanup();
        }
    }

    //Logic-Changing Method
    public void changeLogic(Logic logic, boolean init) {
        this.logic = logic;
        if (init) this.logic.init();
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
    private void render() { this.logic.render(); }

    //Cleanup Method
    private void cleanup() {
        this.logic.cleanup();
    }
}
