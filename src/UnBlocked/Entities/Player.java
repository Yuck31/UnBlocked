package UnBlocked.Entities;
/**
 * 
 */
import static org.lwjgl.glfw.GLFW.*;

import org.joml.Vector2f;
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
import UnBlocked.Graphics.Animations.FrameAnimation_Timer;
import UnBlocked.Graphics.Animations.Functional_FrameAnimation;
import UnBlocked.Graphics.SpriteRenderer.ScRoSpriteRenderer;

public class Player extends Entity implements TileRenderable
{
    //Controller Reference.
    private transient Controller controller = Game.controller;

    //Target position.
    private Vector2f targetPosition = new Vector2f();

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
    anim_holdIdleLook;
    private Functional_FrameAnimation[] anim_holdRun = new Functional_FrameAnimation[2];
    private Functional_FrameAnimation
    anim_idle,
    anim_idleLook,
    anim_idleSDeck0,
    anim_idleSDeck1,
    anim_idleSleep,
    anim_idleWake,
    anim_idleYawn,
    anim_pickUp;
    private Functional_FrameAnimation[] anim_run = new Functional_FrameAnimation[2];
    private Functional_FrameAnimation
    anim_spinJump,
    anim_zipLine0,
    anim_zipLine1;

    private Functional_FrameAnimation currentAnim = null;

    //Command Buffer.
    public static final byte CMD_NONE = 0,
    CMD_LEFT = 1,
    CMD_RIGHT = 2,
    CMD_CLIMB = 3,
    CMD_PICKUP = 4,
    CMD_USE = 5;
    private int input_commandIndex = 0, current_commandIndex = input_commandIndex;
    private byte[] commandBuffer = new byte[128];
    private byte currentCommand = CMD_NONE;

    //Action related stuff.
    private boolean inAnAction = false;
    private byte spriteFlip = Sprite.FLIP_NONE;

    public static final int MAX_BLINK_TIME = 200, MAX_BLINK_DURATION = 10;
    private int idleTimer = MAX_BLINK_DURATION + 1;
    private byte blinkX, blinkY;

    private byte currentFoot = 0;

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
            this::doNothing
        );
        anim_climb1 = Functional_FrameAnimation.load
        (
            "Player/player_climb1", playerSprites,
            //
            this::doNothing
        );
        //
        anim_dropLand = Functional_FrameAnimation.load
        (
            "Player/player_dropLand", playerSprites,
            //
            this::doNothing
        );
        anim_fall = Functional_FrameAnimation.load
        (
            "Player/player_fall", playerSprites,
            //
            this::doNothing
        );
        anim_fallLand = Functional_FrameAnimation.load
        (
            "Player/player_fallLand", playerSprites,
            //
            this::doNothing
        );
        //
        anim_holdClimb = Functional_FrameAnimation.load
        (
            "Player/player_holdClimb", playerSprites,
            //
            this::doNothing
        );
        anim_holdDropLand = Functional_FrameAnimation.load
        (
            "Player/player_holdDropLand", playerSprites,
            //
            (timeMod, loopStatus) -> {return;}
        );
        anim_holdFallLand = Functional_FrameAnimation.load
        (
            "Player/player_holdFallLand", playerSprites,
            //
            this::doNothing
        );
        //
        anim_holdIdleLook = Functional_FrameAnimation.load
        (
            "Player/player_holdIdleLook", playerSprites,
            this::doNothing
        );
        //
        anim_holdRun[0] = Functional_FrameAnimation.load
        (
            "Player/player_holdRun0", playerSprites,
            this::run,
            this::run,
            this::run
        );
        anim_holdRun[1] = Functional_FrameAnimation.load
        (
            "Player/player_holdRun1", playerSprites,
            this::run,
            this::run,
            this::run
        );
        //
        anim_idle = Functional_FrameAnimation.load
        (
            "Player/player_idle", playerSprites,
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
        */
        anim_run[0] = Functional_FrameAnimation.load
        (
            "Player/player_run0", playerSprites,
            this::run,
            this::run,
            this::run
        );
        anim_run[1] = Functional_FrameAnimation.load
        (
            "Player/player_run1", playerSprites,
            this::run,
            this::run,
            this::run
        );
        /*
        anim_spinJump,
        anim_zipLine0,
        anim_zipLine1;
        */

        spriteRenderer = new ScRoSpriteRenderer(anim_idle.getSprite(0), 0, 0,
        1.0f, 1.0f, 0.0f, 0, 0, true, false);

        currentAnim = anim_idle;
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
            //Last command check for animation purposes.
            boolean actionMade = currentCommand != CMD_NONE;

            //Get current command.
            this.currentCommand = commandBuffer[current_commandIndex];
            commandBuffer[current_commandIndex] = CMD_NONE;

            //If there was a command queued up...
            if(this.currentCommand != CMD_NONE)
            {
                inAnAction = true;
                idleTimer = 0;

                //System.out.println(current_commandIndex);

                //Run it.
                switch(currentCommand)
                {
                    case CMD_LEFT:
                    {
                        //Check tile to the Player's left.
                        int tileID = level.getSolidTileID(tileX-1, tileY);

                        //If the tile is not solid, move there.
                        if(tileID == 0)
                        {
                            //Set sprite flip.
                            spriteFlip = Sprite.FLIP_X;
                            spriteRenderer.setFlip(spriteFlip);

                            //Set animation.
                            currentAnim = anim_run[currentFoot];

                            //Set Target Position.
                            targetPosition.x = (tileX-1) << Level.TILE_BITS;
                        }
                        //Otherwise, bonk into it.
                        else
                        {
                            inAnAction = false;
                        }
                    }
                    break;

                    case CMD_RIGHT:
                    {
                        //Check tile to the Player's left.
                        int tileID = level.getSolidTileID(tileX+1, tileY);

                        //If the tile is not solid, move there.
                        if(tileID == 0)
                        {
                            //Set sprite flip.
                            spriteFlip = Sprite.FLIP_NONE;
                            spriteRenderer.setFlip(spriteFlip);

                            //Set animation.
                            currentAnim = anim_run[currentFoot];

                            //Set Target Position.
                            targetPosition.x = (tileX+1) << Level.TILE_BITS;
                        }
                        //Otherwise, bonk into it.
                        else
                        {
                            inAnAction = false;
                        }
                    }
                    break;

                    case CMD_CLIMB:
                    {
                        //Check tile to the Player's top.
                        int tileID = level.getSolidTileID(tileX, tileY-1);

                        //If the tile is not solid, move there.
                        if(tileID == 0)
                        {
                            level.setPlayerPosition(tileX, tileY, tileX, tileY-1);
                            tileY--;
                            position.y = tileY << Level.TILE_BITS;
                        }

                        inAnAction = false;
                    }
                    break;

                    case CMD_PICKUP:
                    {
                        //Check tile to the Player's bottom.
                        int tileID = level.getSolidTileID(tileX, tileY+1);

                        //If the tile is not solid, move there.
                        if(tileID == 0)
                        {
                            level.setPlayerPosition(tileX, tileY, tileX, tileY+1);
                            tileY++;
                            position.y = tileY << Level.TILE_BITS;
                        }

                        inAnAction = false;
                    }
                    break;

                    case CMD_USE:
                    {
                        level.setPlayerPosition(tileX, tileY, 3, 3);
                        tileX = 3;
                        tileY = 3;
                        position.x = tileX << Level.TILE_BITS;
                        position.y = tileY << Level.TILE_BITS;

                        inAnAction = false;
                    }
                    break;
                }

                //Increment Command Index.
                current_commandIndex = (current_commandIndex+1) % commandBuffer.length;
            }

            //Otherwise, Idle animations.
            else
            {
                if(actionMade){currentAnim = anim_idle;}
                idleTimer++;
                //anim_idle.update(timeMod, spriteRenderer);
                //spriteRenderer.setSprite(Sprites.flatSprite);
            }
        }

        //
        //Running Commands.
        //
        //else
        //{
            currentAnim.update(timeMod, spriteRenderer);
        //}
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

    public void doNothing(float timeMod, byte loopStatus){return;}

    public void backStep(float timeMod, byte loopStatus)
    {

    }

    public void idle(float timeMod, byte loopStatus)
    {

    }

    public void idle_breathe(float timeMod, byte loopStatus)
    {

    }

    private static float runX = 1.125f;
    public void run(float timeMod, byte loopStatus)
    {
        switch(currentCommand)
        {
            case CMD_LEFT:
            {
                if(position.x - runX <= targetPosition.x && loopStatus == FrameAnimation_Timer.HAS_ENDED)
                {
                    setTilePosition(tileX-1, tileY);
                    currentAnim.resetAnim();
                    inAnAction = false;
                    currentFoot = (byte)((currentFoot+1) % 2);
                }
                else{position.x -= runX;}
            }
            break;

            case CMD_RIGHT:
            {
                if(position.x + runX >= targetPosition.x && loopStatus == FrameAnimation_Timer.HAS_ENDED)
                {
                    setTilePosition(tileX+1, tileY);
                    currentAnim.resetAnim();
                    inAnAction = false;
                    currentFoot = (byte)((currentFoot+1) % 2);
                }
                else{position.x += runX;}
            }
            break;
        }
    }


    Vector4f color = new Vector4f(0.0f, 0.5f, 0.5f, 1.0f);

    @Override
    public void render(Screen screen, int x, int y, float scale)
    {
        screen.fillRect(tileX << Level.TILE_BITS, tileY << Level.TILE_BITS, Level.TILE_SIZE, Level.TILE_SIZE, color, true);

        //Render main sprite.
        spriteRenderer.render(screen, position, scale);

        //If blinking, render blink.
        if(idleTimer % MAX_BLINK_TIME <= MAX_BLINK_DURATION)
        {

        }
        //screen.renderSprite_Sc
    }
}
