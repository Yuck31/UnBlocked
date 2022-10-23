package UnBlocked.Entities;
/**
 * 
 */
import static org.lwjgl.glfw.GLFW.*;

import org.joml.Vector3f;
import org.joml.Vector4f;

import UnBlocked.Controller;
import UnBlocked.Game;
import UnBlocked.Level;
import UnBlocked.Tile;
import UnBlocked.TileRenderable;
import UnBlocked.Graphics.Screen;
import UnBlocked.Graphics.Sprite;
import UnBlocked.Graphics.SpriteSheet;
import UnBlocked.Graphics.Sprites;
import UnBlocked.Graphics.Animations.Functional_FrameAnimation;
import UnBlocked.Graphics.SpriteRenderer.ScRoSpriteRenderer;

public class Player extends Entity implements TileRenderable
{
    //Controller Reference.
    private transient Controller controller = Game.controller;

    //SpriteRenderer.
    private ScRoSpriteRenderer spriteRenderer;

    //Sprites.
    protected SpriteSheet playerSheet;
    protected Sprite[] playerSprites;

    //Specific Sprites.
    //private Sprite 

    //Animations.
    private Functional_FrameAnimation
    anim_backWalk,
    anim_climb0,
    anim_climb1,
    anim_dropLand,
    anim_fall,
    anim_fallLand,
    anim_holdClimb,
    anim_holdDropLand,
    anim_holdFallLand,
    anim_holdIdleLook,
    anim_holdRun0,
    anim_holdRun1,
    anim_idle,
    anim_idleLook,
    anim_idleSDeck0,
    anim_idleSDeck1,
    anim_idleSleep,
    anim_idleWake,
    anim_idleYawn,
    anim_pickUp,
    anim_run0,
    anim_run1,
    anim_spinJump,
    anim_zipLine0,
    anim_zipLine1;

    //Command Buffer.
    public static final byte CMD_NONE = 0,
    CMD_LEFT = 1,
    CMD_RIGHT = 2,
    CMD_CLIMB = 3,
    CMD_PICKUP = 4,
    CMD_USE = 5;
    private int input_commandIndex = 0, current_commandIndex = input_commandIndex;
    private byte[] commandBuffer = new byte[128];

    //Action related stuff.
    private boolean inAnAction = false;
    private byte spriteFlip = Sprite.FLIP_NONE;
    private int idleTimer = 0;
    //public static final int MAX_BLINK_TIME 

    /**Constructor.*/
    public Player(int x, int y)
    {
        super(x, y);

        //Load Sprite Layout
        playerSheet = Sprites.global_EntitySheet("Player/PlayerSheet");
        playerSprites = playerSheet.loadLayout("player");

        //Load Animations.
        anim_backWalk = Functional_FrameAnimation.load
        (
            "Player/player_backWalk", playerSprites,
            //
            this::doNothing, this::backStep
        );
        //
        anim_climb0 = Functional_FrameAnimation.load
        (
            "Player/player_climb0", playerSprites,
            //
            (timeMod) -> {return;}
        );
        anim_climb1 = Functional_FrameAnimation.load
        (
            "Player/player_climb1", playerSprites,
            //
            (timeMod) -> {return;}
        );
        //
        anim_dropLand = Functional_FrameAnimation.load
        (
            "Player/player_dropLand", playerSprites,
            //
            (timeMod) -> {return;}
        );
        anim_fall = Functional_FrameAnimation.load
        (
            "Player/player_fall", playerSprites,
            //
            (timeMod) -> {return;}
        );
        anim_fallLand = Functional_FrameAnimation.load
        (
            "Player/player_fallLand", playerSprites,
            //
            (timeMod) -> {return;}
        );
        //
        anim_holdClimb = Functional_FrameAnimation.load
        (
            "Player/player_holdClimb", playerSprites,
            //
            (timeMod) -> {return;}
        );
        anim_holdDropLand = Functional_FrameAnimation.load
        (
            "Player/player_holdDropLand", playerSprites,
            //
            (timeMod) -> {return;}
        );
        anim_holdFallLand = Functional_FrameAnimation.load
        (
            "Player/player_holdFallLand", playerSprites,
            //
            (timeMod) -> {return;}
        );
        //
        anim_holdIdleLook = Functional_FrameAnimation.load
        (
            "Player/player_holdIdleLook", playerSprites,
            this::doNothing
        );
        /*
        anim_holdRun0,
        anim_holdRun1,
        */
        anim_idle = Functional_FrameAnimation.load
        (
            "Player/player_backWalk", playerSprites,
            //
            this::idle,
            //{System.out.println("High Blink");},
            this::idle,
            //{System.out.println("Low Blink");},
            this::idle_breathe
            //{System.out.println("High Blink and Exhale");}
        
        );
        anim_idleLook = Functional_FrameAnimation.load
        (
            "Player/player_idleLook", playerSprites,
            this::doNothing
        );
        /*
        anim_idleSDeck0,
        anim_idleSDeck1,
        anim_idleSleep,
        anim_idleWake,
        anim_idleYawn,
        anim_pickUp,
        anim_run0,
        anim_run1,
        anim_spinJump,
        anim_zipLine0,
        anim_zipLine1;
        */

        spriteRenderer = new ScRoSpriteRenderer(anim_idle.getSprite(0), 0, 0,
        1.0f, 1.0f, 0.0f, 0, 0, true, false);
    }

    public void init(Level level)
    {
        this.level = level;
    }

    @Override
    /**Update function.*/
    public void update(float timeMod)
    {
        //
        //Get input and add it to queue.
        //
        getInput();
        //System.out.println(input_Horizontal);
        byte cmd = 0;

        //Vertical Input.
        if(input_Vertical <= -1){cmd = CMD_CLIMB; input_Vertical = 0;}
        else if(input_Vertical >= 1){cmd = CMD_PICKUP; input_Vertical = 0;}
        addCommand(cmd);
        cmd = CMD_NONE;
        
        //Horizontal Input.
        if(input_Horizontal <= -1){cmd = CMD_LEFT; input_Horizontal = 0;}
        else if(input_Horizontal >= 1){cmd = CMD_RIGHT; input_Horizontal = 0;}
        addCommand(cmd);
        cmd = CMD_NONE;

        //Use input.
        if(input_Use){cmd = CMD_USE;}
        addCommand(cmd);
        cmd = CMD_NONE;

        //
        //Idle
        //
        if(!inAnAction)
        {
            //If there is a command queued up...
            byte currentCommand = commandBuffer[current_commandIndex];
            if(currentCommand != CMD_NONE)
            {
                System.out.println(currentCommand);

                //Run it.
                switch(currentCommand)
                {
                    case CMD_LEFT:
                    {
                        //Check tile to the Player's left.
                        byte tileID = level.getTileID(tileX-1, tileY);

                        //If the tile is not solid, move there.
                        //if(!(tileID >= Tile.SOLID_0 && tileID <= Tile.SOLID_7))
                        {
                            spriteFlip = Sprite.FLIP_X;
                            spriteRenderer.setFlip(spriteFlip);
                            tileX--;
                            position.x = tileX << Level.TILE_BITS;
                        }
                    }
                    break;

                    case CMD_RIGHT:
                    {
                        //Check tile to the Player's right.
                        byte tileID = level.getTileID(tileX+1, tileY);

                        //If the tile is not solid, move there.
                        //if(!(tileID >= Tile.SOLID_0 && tileID <= Tile.SOLID_7))
                        {
                            spriteFlip = Sprite.FLIP_NONE;
                            spriteRenderer.setFlip(spriteFlip);
                            tileX++;
                            System.out.println(position.x);
                            position.x = tileX << Level.TILE_BITS;
                        }
                    }
                    break;

                    case CMD_CLIMB:
                    break;

                    case CMD_PICKUP:
                    break;

                    case CMD_USE:
                    break;
                }

                //Increment Command Index.
                current_commandIndex = (current_commandIndex+1) % commandBuffer.length;
            }

            //Otherwise, Idle animations.
            else
            {

            }
        }

        //
        //Running Commands.
        //
    }

    /**Adds a command to the command buffer.*/
    private void addCommand(byte cmd)
    {
        if(cmd != 0)
        {
            commandBuffer[input_commandIndex] = cmd;
            input_commandIndex = (input_commandIndex+1) % commandBuffer.length;
        }
    }

    //Inputs.
    private byte input_Horizontal = 0, input_Vertical = 0;
    private boolean stickX = false, stickY = false;
    private boolean input_Use = false;

    /**Function for getting input.*/
    private void getInput()
    {
        //Movement
        boolean l = controller.inputPressed(0, Controller.action_LEFT),
        r = controller.inputPressed(0, Controller.action_RIGHT),
        u = controller.inputPressed(0, Controller.action_UP),
        d = controller.inputPressed(0, Controller.action_DOWN);

        //System.out.println(l);

        //If D-Pad isn't being used, use control stick instead.
        if(!l && !r && !u && !d)
        {
            float stick_xAxes = controller.getAxes(0, GLFW_GAMEPAD_AXIS_LEFT_X);
            if(!stickX)
            {
                if(stick_xAxes < -controller.getDeadZone()){input_Horizontal = -1; stickX = true;}
                else if(stick_xAxes > controller.getDeadZone()){input_Horizontal = 1; stickX = true;}
            }
            else if(stick_xAxes >= -controller.getDeadZone() && stick_xAxes <= controller.getDeadZone()){stickX = false;}
            //
            float stick_yAxes = controller.getAxes(0, GLFW_GAMEPAD_AXIS_LEFT_Y);
            if(!stickY)
            {
                if(stick_yAxes < -controller.getDeadZone()){input_Vertical = -1; stickY = true;}
                else if(stick_yAxes > controller.getDeadZone()){input_Vertical = 1; stickY = true;}
            }
            else if(stick_yAxes >= -controller.getDeadZone() && stick_yAxes <= controller.getDeadZone()){stickY = false;}
        }
        else 
        {
            //Horizontal
            if(l && !r){input_Horizontal = -1;}
            else if(!l && r){input_Horizontal = 1;}
            else{input_Horizontal = 0;}

            //Vertical
            if(u && !d){input_Vertical = -1;}
            else if(!u && d){input_Vertical = 1;}
            else{input_Vertical = 0;}
        }

        input_Use = controller.inputPressed(0, Controller.action_USE);
    }

    public void doNothing(float timeMod){return;}

    public void backStep(float timeMod)
    {

    }

    public void idle(float timeMod)
    {

    }

    public void idle_breathe(float timeMod)
    {

    }


    @Override
    public void render(Screen screen, int x, int y, float scale)
    {
        spriteRenderer.render(screen, position, scale);
    }
}
