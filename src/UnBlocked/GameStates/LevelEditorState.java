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

    //States.
    public static final byte
    STATE_EDIT = 0,
    STATE_LOAD = 1,
    STATE_SAVEAS = 2,
    STATE_TEST = 3;
    private byte currentState = STATE_EDIT;

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
        //Update Level animations.
        level.updateAnimations();

        //Current state.
        switch(currentState)
        {
            case STATE_EDIT:
            {
                //Left.
                if(controller.inputPressed(0, Controller.menu_LEFT))
                {cursor_setTilePosition(tileX-1, tileY);}

                //Right.
                if(controller.inputPressed(0, Controller.menu_RIGHT))
                {cursor_setTilePosition(tileX+1, tileY);}

                //Up.
                if(controller.inputPressed(0, Controller.menu_UP))
                {cursor_setTilePosition(tileX, tileY-1);}

                //Down.
                if(controller.inputPressed(0, Controller.menu_DOWN))
                {cursor_setTilePosition(tileX, tileY+1);}
            }
            break;
        }
    }

    private void cursor_setTilePosition(int tx, int ty)
    {
        //Set tile position.
        this.tileX = (tx < 0) ? level.getWidth()-1 : (tx > level.getWidth()-1) ? 0 : tx;
        this.tileY = (ty < 0) ? level.getHeight()-1 : (ty > level.getHeight()-1) ? 0 : ty;

        //Set cursor postion for camera.
        cursor_WorldPosition.set(tileX << Level.TILE_BITS, tileY << Level.TILE_BITS);

        //TODO Play sound.
    }

    @Override
    public void end()
    {

    }

    @Override
    public void render(Screen screen)
    {
        //Render Level.

        //Render Cursor.

        //Render UI.
    }
}
