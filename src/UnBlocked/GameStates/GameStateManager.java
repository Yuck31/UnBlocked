package UnBlocked.GameStates;
/**
 * This manages GameStates...
 * 
 * Author: Luke Sullivan
 * Last Edit: 9/23/2021
 */
import UnBlocked.Game;
import UnBlocked.Graphics.*;
//import JettersR.Audio.*;

public class GameStateManager
{
    //private Game game;
    //private AudioManager audioManager;

    private GameState currentGameState = null;

    /**Constructor.*/
    public GameStateManager()
    {

    }

    private boolean started = false;
    public void start(Game game, GameState firstGameState)
    {
        //This function will only work ONCE
        if(started){return;}

        //Assign Game instance and AudioManager
        //this.game = game;
        //this.audioManager = game.getAudioManager();

        //Begin the game
        this.currentGameState = firstGameState;
        this.currentGameState.start();
    }

    public void setGameState(GameState state)
    {
        currentGameState = null;
        currentGameState = state;
        currentGameState.start();
    }

    public void update()
    {
        currentGameState.update();
    }

    public void render(Screen screen)
    {
        currentGameState.render(screen);
    }
}
