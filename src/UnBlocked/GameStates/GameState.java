package UnBlocked.GameStates;
/**
 * Class meant to represent a State in the game.
 * 
 * Author: Luke Sullivan
 * Last Edit: 1/8/2022
 */
import UnBlocked.Graphics.Screen;

public abstract class GameState
{
    public abstract void start();
    public abstract void end();

    public abstract void update();
    public abstract void render(Screen screen);
}
