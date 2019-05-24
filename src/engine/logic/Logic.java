package engine.logic;

import engine.Window;

public interface Logic {

    void init();
    void input();
    void update();
    void render();
    void cleanup();

}
