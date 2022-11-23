package UnBlocked.GameStates;
/**
 * 
 */
import org.joml.Vector2f;
import org.joml.Vector4f;

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

    //Placing mode.
    public static final byte PLACING_PLAYGROUND_TILES = 0,
    PLACING_BACKGROUND_TILES = 1,
    PLACING_ENTITIES = 2;
    private byte placingMode = PLACING_PLAYGROUND_TILES;

    //What Tiles/Entity we have selected.
    private byte[] currentPlacings = new byte[3];

    //private byte[]

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
        level = new Level();
    }

    @Override
    public void update()
    {
        //Current state.
        switch(currentState)
        {
            case STATE_EDIT:
            {
                //Left.
                if(controller.menu_InputHeld(0, Controller.menu_LEFT))
                {cursor_setTilePosition(tileX-1, tileY);}

                //Right.
                if(controller.menu_InputHeld(0, Controller.menu_RIGHT))
                {cursor_setTilePosition(tileX+1, tileY);}

                //Up.
                if(controller.menu_InputHeld(0, Controller.menu_UP))
                {cursor_setTilePosition(tileX, tileY-1);}

                //Down.
                if(controller.menu_InputHeld(0, Controller.menu_DOWN))
                {cursor_setTilePosition(tileX, tileY+1);}

                //Place Tile/Entity.
                if(controller.menu_InputHeld(0, Controller.menu_CONFIRM, false))
                {
                    switch(placingMode)
                    {
                        case PLACING_PLAYGROUND_TILES:
                        cursor_setPlaygroundTile(true);
                        break;

                        case PLACING_BACKGROUND_TILES:
                        cursor_setBackgroundTile(true);
                        break;

                        case PLACING_ENTITIES:
                        cursor_setEntity(true);
                        break;
                    }
                }
                
                //Tiles/Entities toggle.
                boolean leftTab = controller.menu_InputHeld(0, Controller.menu_LEFT_TAB),
                rightTab = controller.menu_InputHeld(0, Controller.menu_RIGHT_TAB);
                if(rightTab && !leftTab)
                {
                    //Change mode.
                    placingMode = (byte)((placingMode+1) % 3);
                    System.out.println(placingMode);

                    //TODO Play sound.
                }
                else if(leftTab && !rightTab)
                {
                    //Change mode.
                    placingMode = (byte)((placingMode-1 < 0) ? PLACING_ENTITIES : (placingMode-1));
                    System.out.println(placingMode);

                    //TODO Play sound.
                }
            }
            break;

            case STATE_TEST:
            {
                //Update level normally.
                level.update();

                //Check end test input.
            }
            break;
        }

        //Update level animations.
        if(currentState != STATE_TEST){level.updateAnimations();}
    }

    /**Sets cursor position.*/
    private void cursor_setTilePosition(int tx, int ty, boolean playSound)
    {
        //Set tile position.
        this.tileX = (tx < 0) ? level.getWidth()-1 : (tx > level.getWidth()-1) ? 0 : tx;
        this.tileY = (ty < 0) ? level.getHeight()-1 : (ty > level.getHeight()-1) ? 0 : ty;

        //Set cursor postion for camera.
        cursor_WorldPosition.set(tileX << Level.TILE_BITS, tileY << Level.TILE_BITS);

        //TODO Play sound.
        if(playSound){}
    }

    private void cursor_setTilePosition(int tx, int ty)
    {cursor_setTilePosition(tx, ty, true);}


    /**Sets a playground tile.*/
    private void cursor_setPlaygroundTile(boolean playSound)
    {
        //Set tile.
        level.setPlaygroundTile(tileX, tileY, currentPlacings[PLACING_PLAYGROUND_TILES]);

        //TODO Play sound.
        if(playSound){}
    }

    /**Sets a background tile.*/
    private void cursor_setBackgroundTile(boolean playSound)
    {
        //Set tile.
        level.setBackgroundTile(tileX, tileY, currentPlacings[PLACING_BACKGROUND_TILES]);

        //TODO Play sound.
        if(playSound){}
    }

    /**Sets an entity.*/
    private void cursor_setEntity(boolean playSound)
    {
        //Set tile.
        level.setEntityID(tileX, tileY, currentPlacings[PLACING_ENTITIES]);

        //TODO Play sound.
        if(playSound){}
    }


    @Override
    public void end()
    {

    }

    private Vector4f rectColor = new Vector4f(1.0f, 1.0f, 1.0f, 1.0f);

    @Override
    public void render(Screen screen)
    {
        //Render Level.
        level.render(screen);
        screen.drawRect(0, 0, level.getWidth() << Level.TILE_BITS, level.getHeight() << Level.TILE_BITS, rectColor, true);

        //Render Cursor.
        screen.drawRect((int)cursor_WorldPosition.x, (int)cursor_WorldPosition.y,
        Level.TILE_SIZE, Level.TILE_SIZE, rectColor, true);

        //Render UI.
        switch(placingMode)
        {
            case PLACING_PLAYGROUND_TILES:
            {
                //for(int i = 0; i < level.num_playgroundTiles)
            }
            break;

            case PLACING_BACKGROUND_TILES:
            {
                
            }
            break;

            case PLACING_ENTITIES:
            {
                
            }
            break;
        }
    }
}
