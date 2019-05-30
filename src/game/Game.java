package game;

import engine.Engine;
import engine.Logic;
import game.logic.WorldLogic;

public class Game {

    //Build Number, Version
    public static final int BUILD_NO = 21;
    public static final String VERSION = "dev0";

    //Main Method
    public static void main(String[] args) {

        //create window, logic, engine, start engine
        Logic logic = new WorldLogic();
        Engine engine = new Engine(logic);
        engine.start();
    }
}
