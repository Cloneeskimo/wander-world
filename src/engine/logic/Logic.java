package engine.logic;

import engine.Window;

public interface Logic {

    void init(Window window);
    void input();
    void update(float dT);
    void render();
    void cleanup();

}
