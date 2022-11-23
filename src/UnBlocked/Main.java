package UnBlocked;
/**
 * Program entry point.
 * 
 * Author: Luke Sullivan
 * Last Edit: 10/18/2022
 */

public class Main
{
    public static void main(String[] args)
    {
        Game game = Game.instantiate(Game.SCREEN_WIDTH, Game.SCREEN_HEIGHT);
        game.start(new UnBlocked.GameStates.TestState());
        //game.start(new UnBlocked.GameStates.LevelEditorState());
        //game.start();
    }
}