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
    private Sprite sprite_turnAround,  sprite_holdTurnAround;

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
    anim_holdIdle,
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
    anim_pickUpBlock,
    anim_putDownBlock;
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
    //
    private byte blockOffsetX = 0, blockOffsetY = -1;
    private Block block = null;
    private boolean holdingBlock = false,
    puttingAbove = false;

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
            this::climb_Start,
            this::climb_Jump,
            this::climb_Finish
        );
        anim_holdDrop = Functional_FrameAnimation.load
        (
            "Player/player_holdDrop", playerSprites,
            //
            this::drop,
            this::drop//TODO Should only be one function.
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
        anim_holdIdle = Functional_FrameAnimation.load
        (
            "Player/player_holdIdle", playerSprites,
            this::idle
        );
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
        */
        anim_pickUpBlock = Functional_FrameAnimation.load
        (
            "Player/player_pickUpBlock", playerSprites,
            this::pickUpBlock
        );
        anim_putDownBlock = Functional_FrameAnimation.load
        (
            "Player/player_putDownBlock", playerSprites,
            this::putDownBlock
        );
        //
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
        sprite_holdTurnAround = playerSprites[132];

        spriteRenderer = new ScRoSpriteRenderer(anim_idle.getSprite(0), 0, 0,
        1.0f, 1.0f, 0.0f, 5, 10, true, false);

        currentIdle_Anim = anim_idle;
    }

    
    @Override
    public boolean isSolid(){return false;}

    public byte getBlockOffsetX(){return blockOffsetX;}
    public byte getBlockOffsetY(){return blockOffsetY;}


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
            //Block fall check.
            if(!holdingBlock && block != null)
            {
                if(block.fall(timeMod, yVelocity))
                {
                    level.setEntityID(block.tileX, block.tileY, (byte)0);
                    level.setEntityID(block.tileX, Block.targetY, (byte)8);
                    block.setTileY(Block.targetY);
                    //
                    block = null;
                    yVelocity = 0.0f;
                }
                else
                {
                    yVelocity += GRAVITY;
                    return;
                }
            }

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
                        int tx = (spriteFlip == Sprite.FLIP_X) ? tileX-1 : tileX+1;
                        //
                        if(block == null)
                        {
                            //Check Entity in front of Player.
                            Entity e = level.getEntity(tx, tileY);

                            //Is a block there?
                            if(e instanceof Block)
                            {
                                Block b = (Block)e;

                                //Check tiles above the block and player.
                                boolean sAboveBlock = level.isSolid(tx, tileY-1),
                                sAbovePlayer = level.isSolid(tileX, tileY-1);

                                if(!sAboveBlock && !sAbovePlayer)
                                {
                                    //Set animation.
                                    currentAction_Anim = anim_pickUpBlock;

                                    //Set Block.
                                    this.block = b;
                                    this.block.setTilePosition(tx, tileY);
                                    level.setEntityID(tx, tileY, (byte)0);
                                    holdingBlock = true;

                                    //Set idle animation.
                                    currentIdle_Anim = anim_holdIdle;
                                }
                            }
                            //Flail?
                            else{inAnAction = false;}
                        }
                        else
                        {
                            //Check tiles in the way of the Block.
                            boolean sFrontBlock = level.isSolid(tx, tileY-1),
                            sFrontPlayer = level.isSolid(tx, tileY);

                            //Can't put down.
                            if(sFrontBlock)
                            {
                                //TODO Struggle helplessly.
                                inAnAction = false;
                            }
                            //Can put down.
                            else
                            {
                                //Can put above check.
                                if(sFrontPlayer){puttingAbove = true;}
                                
                                //Set idle animation.
                                currentIdle_Anim = anim_idle;

                                //Set animation.
                                currentAction_Anim = anim_putDownBlock;
                            }
                        }
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
            //System.out.println(speedMul);
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
        if(holdingBlock){currentAction_Anim = anim_holdRun[currentFoot];}
        else
        {
            //TODO If not holding a block, RNG Roll for spin jump.
            int r = Game.RANDOM.nextInt(8);
            if(r == 0)
            {
                //System.out.println("SPEEN");
                currentAction_Anim = anim_run[currentFoot];
            }
            else{currentAction_Anim = anim_run[currentFoot];}
        }

        //Set Target Position.
        targetPosition.x = (tx) << Level.TILE_BITS;

        //If the tile is solid, bonk into it.
        if(solid){bonking = true;}
        else
        {
            if(holdingBlock && level.isSolid(tx, tileY-1))
            {
                //TODO Drop block.
                holdingBlock = false;
            }

            //If there isn't a floor underneath Target X, fall.
            if(!level.isSolid(tx, tileY+1))
            {
                falling = FALLING_DROP;
                speedMul = 3.0f;
            }
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
            //if(holdingBlock){currentAction_Anim = anim_holdClimb;}
            //else
            {currentAction_Anim = anim_climb[currentFoot];}

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
            if(holdingBlock){currentAction_Anim = anim_holdHop;}
            else{currentAction_Anim = anim_hop;}

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

            //As well to block.
            if(holdingBlock){block.setPosition(position.x, position.y - 15.0f);}
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

            //Set block position.
            if(holdingBlock){block.setPosition(position.x, position.y - 15.0f);}

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

            if(holdingBlock){block.setPosition(position.x, position.y - 15.0f);}
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
        {
            //Set player position.
            position.x += (spriteFlip == Sprite.FLIP_X) ? BONK_RECOIL_X : -BONK_RECOIL_X;

            //Set block position.
            if(holdingBlock){block.setX(position.x);}
        }
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
                //Set level position.
                level.setPlayerPosition(tileX, tileY, tileX, fallingY);

                //Set player position.
                setTileY(fallingY);

                //Set block position.
                if(holdingBlock){block.setY(position.y - 15.0f);}

                //Advance animation.
                currentAction_Anim.setFrame(1);
            }
            else
            {
                //Set position.
                position.y += yVelocity;
                yVelocity += GRAVITY;

                //Set block position.
                if(holdingBlock){block.setY(position.y - 15.0f);}

                //Loop frame.
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
            if(holdingBlock){block.setY(position.y - 15.0f);}

            //Get out of this animation.
            inAnAction = false;
        }
        else
        {
            position.y += yVelocity;
            yVelocity += GRAVITY;
            if(holdingBlock){block.setY(position.y - 15.0f);}
        }
    }

    private static final float RUN_X = 0.9f, BONK_X = 14.0f;
    public void run(float timeMod, byte loopStatus)
    {
        if(currentAction_Anim.getFrame() < 2 && neededToTurnAround)
        {
            if(holdingBlock)
            {
                spriteRenderer.setOffset(-8, -14);
                spriteRenderer.setSprite(sprite_holdTurnAround);
            }
            else
            {
                spriteRenderer.setOffset(-8, -14);
                spriteRenderer.setSprite(sprite_turnAround);
            }
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
                    //
                    if(holdingBlock){currentAction_Anim = anim_holdBonk;}
                    else{currentAction_Anim = anim_bonk;}
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
                        //
                        if(holdingBlock){currentAction_Anim = anim_holdDrop;}
                        else{currentAction_Anim = anim_drop;}
                    }
                }
                else if(position.x - (RUN_X * timeMod) > targetPosition.x)
                {
                    position.x -= (RUN_X * timeMod);
                    if(holdingBlock){block.setX(position.x);}
                }
                else
                {
                    position.x = targetPosition.x;
                    if(holdingBlock){block.setX(position.x);}
                }
            }
            break;

            case CMD_RIGHT:
            {
                if(bonking && position.x + (RUN_X * timeMod) > targetPosition.x - BONK_X)
                {
                    position.x = targetPosition.x - BONK_X;
                    currentAction_Anim.resetAnim();
                    //
                    if(holdingBlock){currentAction_Anim = anim_holdBonk;}
                    else{currentAction_Anim = anim_bonk;}
                }
                else if(loopStatus == FrameAnimation_Timer.HAS_ENDED)
                {
                    //Set position in Level.
                    level.setPlayerPosition(tileX, tileY, tileX+1, tileY);

                    //Set position.
                    setTileX(tileX+1);

                    //Block check.

                    currentAction_Anim.resetAnim();
                    neededToTurnAround = false;
                    currentFoot = (byte)((currentFoot+1) % 2);

                    if(falling == FALLING_NOT){inAnAction = false;}
                    else if(falling == FALLING_DROP)
                    {
                        fallingY = fallCheck(tileX, tileY);
                        targetPosition.y = fallingY << Level.TILE_BITS;
                        //
                        if(holdingBlock){currentAction_Anim = anim_holdDrop;}
                        else{currentAction_Anim = anim_drop;}
                    }
                }
                else if(position.x + (RUN_X * timeMod) < targetPosition.x)
                {
                    position.x += (RUN_X * timeMod);
                    if(holdingBlock){block.setX(position.x);}
                }
                else
                {
                    position.x = targetPosition.x;
                    if(holdingBlock){block.setX(position.x);}
                }
            }
            break;
        }
    }

    private static final float[][] PICKUP_BLOCK_POSITIONS =
    {
        {16.0f, 0.0f,},
        {8.0f, -6.0f,},
        {0.0f, -18.0f,},
        {0.0f, -14.0f,},
    };

    int oldBlockTX = -1, oldBlockTY = -1;

    public void pickUpBlock(float timeMod, byte loopStatus)
    {
        float bx = PICKUP_BLOCK_POSITIONS[currentAction_Anim.getFrame()][0],
        by = PICKUP_BLOCK_POSITIONS[currentAction_Anim.getFrame()][1];

        if(holdingBlock)
        {
            block.setPosition
            (
                position.x + ((spriteFlip == Sprite.FLIP_X) ? -(spriteRenderer.getSprite().getWidth() + bx) : bx),
                position.y + by
            );

            blockOffsetX = (byte)(((int)bx >> Level.TILE_BITS));
            blockOffsetY = (byte)(((int)by >> Level.TILE_BITS));
        }

        if(loopStatus == FrameAnimation_Timer.HAS_ENDED)
        {
            blockOffsetX = 0;
            blockOffsetY = -1;
            //
            block.setX(position.x);
            block.setY(position.y - 15.0f);
            //
            currentIdle_Anim = anim_holdIdle;
            currentAction_Anim.resetAnim();
            inAnAction = false;
        }
    }

    public void putDownBlock(float timeMod, byte loopStatus)
    {
        float bx = PICKUP_BLOCK_POSITIONS[3 - currentAction_Anim.getFrame()][0],
        by = PICKUP_BLOCK_POSITIONS[3 - currentAction_Anim.getFrame()][1];

        if(holdingBlock)
        {
            block.setPosition
            (
                position.x + ((spriteFlip == Sprite.FLIP_X) ? -(spriteRenderer.getSprite().getWidth() + bx) : bx),
                position.y + by
            );

            blockOffsetX = (byte)(((int)bx >> Level.TILE_BITS));
            blockOffsetY = (byte)(((int)by >> Level.TILE_BITS));
        }

        if(currentAction_Anim.getFrame() > 1 && puttingAbove)
        {
            int tx = (spriteFlip == Sprite.FLIP_X) ? tileX-1 : tileX+1;
            level.setEntityID(tx, tileY-1, (byte)8);
            block.setTilePosition(tx, tileY-1);

            puttingAbove = false;
            holdingBlock = false;
            block = null;
            //
            currentIdle_Anim = anim_idle;
            currentAction_Anim.resetAnim();
            inAnAction = false;
        }
        else if(loopStatus == FrameAnimation_Timer.HAS_ENDED)
        {
            int tx = (spriteFlip == Sprite.FLIP_X) ? tileX-1 : tileX+1;
            block.setTilePosition(tx, tileY);

            holdingBlock = false;
            //
            blockOffsetX = 0;
            blockOffsetY = -1;
            //
            if(!level.isSolid(tx, tileY+1))
            {Block.targetY = fallCheck(tx, tileY+1);}
            else
            {
                level.setEntityID(tx, tileY, (byte)8);
                block = null;
            }
            //
            currentIdle_Anim = anim_idle;
            currentAction_Anim.resetAnim();
            inAnAction = false;
        }
    }

    private int fallCheck(int tx, int ty)
    {
        for(int f = ty; f < level.getHeight(); f++)
        {
            boolean st = level.isSolid(tx, f);

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
        visualPosition.x = position.x+9;
        visualPosition.y = position.y+8;

        //Render main sprite.
        //spriteRenderer.setOffset(0, 0);
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
        if(block != null)
        {
            //Render Block.
            block.render(screen, scale);

            //TODO Render front arm.
        }
    }
}
