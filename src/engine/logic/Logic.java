package engine.logic;

import engine.Window;
import engine.utils.MouseInput;

public interface Logic {

    void init(Window window);
    void input();
    void update(float dT, MouseInput mouseInput);
    void render();
    void cleanup();

}
