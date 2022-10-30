package UnBlocked.GameStates;
/**
 * 
 */
import org.joml.Vector2f;

import UnBlocked.Controller;
import UnBlocked.Game;
import UnBlocked.Level;
import UnBlocked.Graphics.Screen;

public class LevelEditorState extends GameState
{
    //Input.
    private transient Controller controller = Game.controller;

    //Level.
    private Level level;

    //Cursor.
    private int tileX = 0, tileY = 0;
    private Vector2f cursor_WorldPosition = new Vector2f();

    /**Constructor.*/
    public LevelEditorState()
    {

    }

    @Override
    public void start()
    {

    }

    @Override
    public void update()
    {
        level.updateAnimations();
    }

    @Override
    public void end()
    {

    }

    @Override
    public void render(Screen screen)
    {
        
    }
}
