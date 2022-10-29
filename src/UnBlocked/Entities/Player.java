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
import UnBlocked.Graphics.Screen;
import UnBlocked.Graphics.Sprite;
import UnBlocked.Graphics.SpriteSheet;
import UnBlocked.Graphics.Sprites;
import UnBlocked.Graphics.Animations.FrameAnimation_Timer;
import UnBlocked.Graphics.Animations.Functional_FrameAnimation;
import UnBlocked.Graphics.SpriteRenderer.ScRoSpriteRenderer;

public class Player extends Entity
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
    private Sprite sprite_blink,
    sprite_drop, sprite_holdDrop;
    private Sprite[] sprite_preDrop = new Sprite[2],
    sprite_preHoldDrop = new Sprite[2];
    private Sprite sprite_turnAround;

    //Animations.
    private Functional_FrameAnimation
    anim_backWalk,
    anim_bonk;
    private Functional_FrameAnimation[] anim_climb =  new Functional_FrameAnimation[2];
    private Functional_FrameAnimation
    anim_drop,
    anim_fall,
    anim_fallLand,
    anim_holdBonk,
    anim_holdClimb,
    anim_holdDrop,
    anim_holdFallLand,
    anim_holdHop,
    anim_holdIdleLook;
    private Functional_FrameAnimation[] anim_holdRun = new Functional_FrameAnimation[2];
    private Functional_FrameAnimation
    anim_hop,
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

    //Currently used animations.
    private Functional_FrameAnimation 
    currentIdle_Anim = null,
    currentAction_Anim = null;

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

    public static final int MAX_BLINK_TIME = 200, MAX_BLINK_DURATION = 7;
    private int idleTimer = MAX_BLINK_DURATION + 1;
    private byte blinkX, blinkY;

    private byte currentFoot = 0;

    public static final byte FALLING_NOT = 0,
    FALLING_DROP = 1,
    FALLING_FALL = 2;
    private byte falling = FALLING_NOT;
    private int fallingY = 0;

    private boolean neededToTurnAround = false,
    bonking = false;
    private Block block = null;

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
        anim_bonk = Functional_FrameAnimation.load
        (
            "Player/player_bonk", playerSprites,
            //
            this::bonk
        );
        //
        anim_climb[0] = Functional_FrameAnimation.load
        (
            "Player/player_climb0", playerSprites,
            //
            this::climb_Start,
            this::climb_Jump,
            this::climb_Finish
        );
        anim_climb[1] = Functional_FrameAnimation.load
        (
            "Player/player_climb1", playerSprites,
            //
            this::climb_Start,
            this::climb_Jump,
            this::climb_Finish
        );
        //
        anim_drop = Functional_FrameAnimation.load
        (
            "Player/player_drop", playerSprites,
            //
            this::drop,
            this::drop
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
        anim_holdBonk = Functional_FrameAnimation.load
        (
            "Player/player_holdBonk", playerSprites,
            //
            this::bonk
        );
        //
        anim_holdClimb = Functional_FrameAnimation.load
        (
            "Player/player_holdClimb", playerSprites,
            //
            this::doNothing
        );
        anim_holdDrop = Functional_FrameAnimation.load
        (
            "Player/player_holdDrop", playerSprites,
            //
            this::drop
        );
        anim_holdFallLand = Functional_FrameAnimation.load
        (
            "Player/player_holdFallLand", playerSprites,
            //
            this::doNothing
        );
        //
        anim_holdHop = Functional_FrameAnimation.load
        (
            "Player/player_holdHop", playerSprites,
            this::hop
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
        anim_hop = Functional_FrameAnimation.load
        (
            "Player/player_hop", playerSprites,
            this::hop
        );
        //
        anim_idle = Functional_FrameAnimation.load
        (
            "Player/player_idle", playerSprites,
            //
            this::idle,
            //{System.out.println("High Blink");},
            this::idle_breathe
            //{System.out.println("Low Blink");},
            //this::idle
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
        anim_spinJump = Functional_FrameAnimation.load
        (
            "Player/player_spinJump", playerSprites,
            this::doNothing
        );
        /*
        anim_zipLine0,
        anim_zipLine1;
        */

        sprite_blink = playerSprites[12];
        sprite_turnAround = playerSprites[133];

        spriteRenderer = new ScRoSpriteRenderer(anim_idle.getSprite(0), 0, 0,
        1.0f, 1.0f, 0.0f, 8, 8, true, false);

        currentIdle_Anim = anim_idle;
    }

    
    @Override
    public boolean isSolid(){return false;}


    private static final float SPEED_MUL_DEC = 0.315f;
    private float speedMul = 1.0f;

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
            //Previous command check for animation purposes.
            boolean actionMade = this.currentCommand != CMD_NONE;

            //Get current command.
            this.currentCommand = commandBuffer[current_commandIndex];
            commandBuffer[current_commandIndex] = CMD_NONE;

            //If there was a command queued up...
            if(this.currentCommand != CMD_NONE)
            {
                inAnAction = true;
                idleTimer = MAX_BLINK_DURATION + 1;
                currentIdle_Anim.resetAnim();

                //System.out.println(current_commandIndex);

                //Run it.
                switch(currentCommand)
                {
                    case CMD_LEFT:
                    {
                        executeRun(false);
                    }
                    break;

                    case CMD_RIGHT:
                    {
                        executeRun(true);
                    }
                    break;

                    case CMD_CLIMB:
                    {
                        executeClimb();
                    }
                    break;

                    case CMD_PICKUP:
                    {
                        /*
                        //Check tile to the Player's bottom.
                        boolean tileID = level.isSlid(tileX, tileY+1);

                        //If the tile is not solid, move there.
                        if(tileID == 0)
                        {
                            level.setPlayerPosition(tileX, tileY, tileX, tileY+1);
                            tileY++;
                            position.y = tileY << Level.TILE_BITS;
                        }
                        */

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
                idleTimer++;
                currentIdle_Anim.update(timeMod, spriteRenderer);
                //spriteRenderer.setSprite(Sprites.flatSprite);
            }
        }

        //
        //Running Commands.
        //
        if(inAnAction)
        {
            if(speedMul - SPEED_MUL_DEC <= 1.0f){speedMul = 1.0f;}
            else{speedMul -= SPEED_MUL_DEC;}
            System.out.println(speedMul);
            currentAction_Anim.update(speedMul, spriteRenderer);
            //spriteRenderer.addXOffset(8);
            //spriteRenderer.addYOffset(8);
        }
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


    private void executeRun(boolean right)
    {
        //Going left or right?
        int tx = (right) ? tileX+1 : tileX-1;

        //Set sprite flip.
        if(!right && spriteFlip != Sprite.FLIP_X)
        {
            spriteFlip = Sprite.FLIP_X;
            spriteRenderer.setFlip(spriteFlip);
            neededToTurnAround = true;
        }
        else if(right && spriteFlip != Sprite.FLIP_NONE)
        {
            spriteFlip = Sprite.FLIP_NONE;
            spriteRenderer.setFlip(spriteFlip);
            neededToTurnAround = true;
        }

        //Check tile to the direction the Player is moving in.
        boolean solid = level.isSolid(tx, tileY);

        //Set animation.
        currentAction_Anim = anim_run[currentFoot];

        //Set Target Position.
        targetPosition.x = (tx) << Level.TILE_BITS;

        //If the tile is solid, bonk into it.
        if(solid){bonking = true;}
        //Otherwise, if there isn't a floor underneath Target X, fall.
        else if(!level.isSolid(tx, tileY+1))
        {
            falling = FALLING_DROP;
            speedMul = 3.0f;
        }
    }

    private void executeClimb()
    {
        //Check tile to the Player's front and front-up.
        int tx = (spriteFlip == Sprite.FLIP_X) ? tileX-1 : tileX+1;

        boolean sFront = level.isSolid(tx, tileY),
        sFrontAbove = level.isSolid(tx, tileY-1),
        sAbove = level.isSolid(tileX, tileY-1);

        //If the front tile is solid and ones above it and the player are not.
        if(sFront && !sFrontAbove && !sAbove)
        {
            //Set animation.
            currentAction_Anim = anim_climb[currentFoot];

            //Set Target Position.
            targetPosition.x = tx << Level.TILE_BITS;
            targetPosition.y = (tileY-1) << Level.TILE_BITS;

            //Slight X offset.
            if(spriteFlip == Sprite.FLIP_NONE){position.x++;}
        }
        //Otherwise, hop helplessly.
        else
        {
            //Set animation.
            currentAction_Anim = anim_hop;

            //Set Target Position and yVelocity.
            targetPosition.y = tileY << Level.TILE_BITS;
            yVelocity = INITIAL_Y_VELOCITY * 0.75f;
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

    private float yVelocity = 0.0f;

    public void climb_Start(float timeMod, byte loopStatus){yVelocity = INITIAL_Y_VELOCITY;}

    private static final float INITIAL_Y_VELOCITY = -3.0f,
    GRAVITY = 0.4f,
    CLIMB_X = 0.9f;

    public void climb_Jump(float timeMod, byte loopStatus)
    {
        int tx = (spriteFlip == Sprite.FLIP_X) ? tileX-1 : tileX+1;

        if(yVelocity <= INITIAL_Y_VELOCITY)
        {
            //Set level position.
            level.setPlayerPosition(tileX, tileY, tx, tileY-1);

            //Apply initial offset.
            position.x += (spriteFlip == Sprite.FLIP_X) ? -4 : 4;
            position.y -= 8.0f;
        }

        //Loop this frame until the player hits the ground.
        if(currentAction_Anim.getFrame() >= 3)
        {currentAction_Anim.setFrame(3);}

        if(yVelocity > 0.0f && position.y + yVelocity >= targetPosition.y)
        {
            //Set level position.
            level.setPlayerPosition(tileX, tileY, tx, tileY-1);

            //Set actual position.
            setTilePosition(tx, tileY-1);
            yVelocity = 0.0f;

            //Advance animation.
            currentAction_Anim.setFrame(4);
        }
        else
        {
            position.y += yVelocity;
            yVelocity += GRAVITY;

            switch(spriteFlip)
            {
                case Sprite.FLIP_X:
                {
                    if(position.x - CLIMB_X > targetPosition.x){position.x -= CLIMB_X;}
                    else{position.x = targetPosition.x;}
                }
                break;

                case Sprite.FLIP_NONE:
                {
                    if(position.x + CLIMB_X < targetPosition.x){position.x += CLIMB_X;}
                    else{position.x = targetPosition.x;}
                }
                break;
            }
        }
    }

    public void climb_Finish(float timeMod, byte loopStatus)
    {
        if(loopStatus == FrameAnimation_Timer.HAS_ENDED)
        {
            currentAction_Anim.resetAnim();
            inAnAction = false;
            currentFoot = (byte)((currentFoot+1) % 2);
        }
        else
        {
            //Check for another climb command.
            this.currentCommand = commandBuffer[current_commandIndex];
            //
            if(this.currentCommand == CMD_CLIMB)
            {
                //Reset current animation and change foot.
                currentAction_Anim.resetAnim();
                currentFoot = (byte)((currentFoot+1) % 2);

                //Advance command buffer.
                commandBuffer[current_commandIndex] = CMD_NONE;
                current_commandIndex = (current_commandIndex+1) % commandBuffer.length;

                //Execute climb.
                executeClimb();
            }
        }
    }

    public void idle(float timeMod, byte loopStatus)
    {
        blinkX = (byte)((spriteFlip == Sprite.FLIP_X) ? -14 : -6);
        blinkY = -17;
    }

    public void idle_breathe(float timeMod, byte loopStatus)
    {
        blinkX = (byte)((spriteFlip == Sprite.FLIP_X) ? -14 : -6);
        blinkY = -16;
    }

    private static final float BONK_RECOIL_X = 0.5f;
    public void bonk(float timeMod, byte loopStatus)
    {
        if(currentAction_Anim.getFrame() > 0)
        {position.x += (spriteFlip == Sprite.FLIP_X) ? BONK_RECOIL_X : -BONK_RECOIL_X;}
        //
        if(loopStatus == FrameAnimation_Timer.HAS_ENDED)
        {
            setTileX(tileX);
            bonking = false;
            currentAction_Anim.resetAnim();
            neededToTurnAround = false;
            inAnAction = false;
            currentFoot = (byte)((currentFoot+1) % 2);
        }
    }

    public void drop(float timeMod, byte loopStatus)
    {
        if(currentAction_Anim.getFrame() <= 0)
        {
            if(position.y + yVelocity >= targetPosition.y)
            {
                level.setPlayerPosition(tileX, tileY, tileX, fallingY);
                setTileY(fallingY);
                currentAction_Anim.setFrame(1);
            }
            else
            {
                position.y += yVelocity;
                yVelocity += GRAVITY;
                currentAction_Anim.setFrame(0);
            }
        }
        else if(loopStatus == FrameAnimation_Timer.HAS_ENDED)
        {
            currentAction_Anim.resetAnim();
            yVelocity = 0.0f;
            falling = FALLING_NOT;
            inAnAction = false;
        }
        else if(currentAction_Anim.getFrame() > 1)
        {
            //Check for another run command.
            this.currentCommand = commandBuffer[current_commandIndex];
            //
            if(this.currentCommand == CMD_LEFT)
            {
                //Reset current animation.
                currentAction_Anim.resetAnim();
                yVelocity = 0.0f;
                falling = FALLING_NOT;

                //Advance command buffer.
                commandBuffer[current_commandIndex] = CMD_NONE;
                current_commandIndex = (current_commandIndex+1) % commandBuffer.length;

                //Execute run.
                executeRun(false);
            }
            else if(this.currentCommand == CMD_RIGHT)
            {
                //Reset current animation.
                currentAction_Anim.resetAnim();
                yVelocity = 0.0f;
                falling = FALLING_NOT;

                //Advance command buffer.
                commandBuffer[current_commandIndex] = CMD_NONE;
                current_commandIndex = (current_commandIndex+1) % commandBuffer.length;

                //Execute run.
                executeRun(true);
            }
        }
    }
    
    public void hop(float timeMod, byte loopStatus)
    {
        //Loop frame until the player lands.
        currentAction_Anim.setFrame(0);

        if(yVelocity > 0.0f && position.y + yVelocity >= targetPosition.y)
        {
            //Set actual position.
            setTilePosition(tileX, tileY);
            yVelocity = 0.0f;

            //Get out of this animation.
            inAnAction = false;
        }
        else
        {
            position.y += yVelocity;
            yVelocity += GRAVITY;
        }
    }

    private static final float RUN_X = 0.9f, BONK_X = 14.0f;
    public void run(float timeMod, byte loopStatus)
    {
        if(currentAction_Anim.getFrame() < 2 && neededToTurnAround)
        {
            spriteRenderer.setOffset(-8, -14);
            spriteRenderer.setSprite(sprite_turnAround);
        }
        //
        if(currentAction_Anim.getFrame() >= 2 && falling == FALLING_DROP)
        {
            currentAction_Anim.getFrameSprite(2, spriteRenderer);
        }

        switch(currentCommand)
        {
            case CMD_LEFT:
            {
                if(bonking && position.x - (RUN_X * timeMod) < targetPosition.x + BONK_X)
                {
                    position.x = targetPosition.x + BONK_X;
                    currentAction_Anim.resetAnim();
                    currentAction_Anim = anim_bonk;
                }
                else if(loopStatus == FrameAnimation_Timer.HAS_ENDED)
                {
                    //Set position in Level.
                    level.setPlayerPosition(tileX, tileY, tileX-1, tileY);

                    //Set position.
                    setTileX(tileX-1);

                    //Reset animation.
                    currentAction_Anim.resetAnim();
                    neededToTurnAround = false;
                    currentFoot = (byte)((currentFoot+1) % 2);

                    if(falling == FALLING_NOT){inAnAction = false;}
                    else if(falling == FALLING_DROP)
                    {
                        fallingY = fallCheck(tileX, tileY);
                        targetPosition.y = fallingY << Level.TILE_BITS;
                        currentAction_Anim = anim_drop;
                    }
                }
                else if(position.x - (RUN_X * timeMod) > targetPosition.x){position.x -= (RUN_X * timeMod);}
                else{position.x = targetPosition.x;}
            }
            break;

            case CMD_RIGHT:
            {
                if(bonking && position.x + (RUN_X * timeMod) > targetPosition.x - BONK_X)
                {
                    position.x = targetPosition.x - BONK_X;
                    currentAction_Anim.resetAnim();
                    currentAction_Anim = anim_bonk;
                }
                else if(loopStatus == FrameAnimation_Timer.HAS_ENDED)
                {
                    //Set position in Level.
                    level.setPlayerPosition(tileX, tileY, tileX+1, tileY);

                    //Set position.
                    setTileX(tileX+1);

                    currentAction_Anim.resetAnim();
                    neededToTurnAround = false;
                    currentFoot = (byte)((currentFoot+1) % 2);

                    if(falling == FALLING_NOT){inAnAction = false;}
                    else if(falling == FALLING_DROP)
                    {
                        fallingY = fallCheck(tileX, tileY);
                        targetPosition.y = fallingY << Level.TILE_BITS;
                        currentAction_Anim = anim_drop;
                    }
                }
                else if(position.x + (RUN_X * timeMod) < targetPosition.x){position.x += (RUN_X * timeMod);}
                else{position.x = targetPosition.x;}
            }
            break;
        }
    }

    private int fallCheck(int tx, int ty)
    {
        for(int f = tileY-1; f < level.getHeight(); f++)
        {
            boolean st = level.isSolid(tileX, f);

            if(st)
            {
                return f-1;
            }
        }

        return level.getHeight() << Level.TILE_BITS;
    }

    Vector4f color = new Vector4f(0.0f, 0.5f, 0.5f, 0.2f);
    Vector4f color1 = new Vector4f(1.0f, 0.5f, 0.5f, 1.0f);

    Vector2f visualPosition = new Vector2f();

    @Override
    public void render(Screen screen, int x, int y, float scale)
    {
        screen.fillRect(tileX << Level.TILE_BITS, tileY << Level.TILE_BITS, Level.TILE_SIZE, Level.TILE_SIZE, color, true);

        //Set visual position.
        visualPosition.x = position.x+16;
        visualPosition.y = position.y+16;

        //Render main sprite.
        spriteRenderer.render(screen, visualPosition, scale);

        //If blinking, render blink.
        if(!inAnAction && idleTimer % MAX_BLINK_TIME <= MAX_BLINK_DURATION)
        {
            float xs = spriteRenderer.getXScale() * scale, ys = spriteRenderer.getYScale() * scale;
            //
            screen.renderSprite_Sc
            (
                (int)((visualPosition.x + blinkX) * scale),
                (int)((visualPosition.y + blinkY) * scale),
                0,
                sprite_blink, spriteRenderer.getFlip(), 0, 0, xs, ys, true
            );
        }
        
        screen.fillRect((int)position.x, (int)position.y, 1, 1, color1, true);
    }

    public void renderBlock(Screen screen, float scale)
    {
        if(block != null){block.render(screen, scale);}
    }
}
