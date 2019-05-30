package engine;

import engine.utils.MouseInput;

public interface Logic {

    void init(Window window) throws Exception;
    void input();
    void update(float dT, MouseInput mouseInput);
    void render();
    void cleanup();
}
