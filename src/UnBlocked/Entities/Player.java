package UnBlocked.Entities;
/**
 * 
 */
import static org.lwjgl.glfw.GLFW.*;

import org.joml.Vector3f;
import org.joml.Vector4f;

import UnBlocked.Controller;
import UnBlocked.Game;
import UnBlocked.Graphics.Sprite;
import UnBlocked.Graphics.SpriteSheet;
import UnBlocked.Graphics.Sprites;
import UnBlocked.Graphics.Animations.Functional_FrameAnimation;

public class Player extends Entity
{
    //Controller Reference.
    private transient Controller controller = Game.controller;

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
            (timeMod) -> {return;}
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
            "player_backWalk", playerSprites,
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
    }


    @Override
    /**Update function.*/
    public void update(float timeMod)
    {

    }

    //Inputs.
    private float input_xAxes = 0.0f, input_yAxes = 0.0f;

    /**Function for getting input.*/
    private void getInput()
    {
        //Movement
        boolean l = controller.inputHeld(0, Controller.action_LEFT),
        r = controller.inputHeld(0, Controller.action_RIGHT),
        u = controller.inputHeld(0, Controller.action_UP),
        d = controller.inputHeld(0, Controller.action_DOWN);

        //If D-Pad isn't being used, use control stick instead.
        if(!l && !r && !u && !d)
        {
            input_xAxes = controller.getAxes(0, GLFW_GAMEPAD_AXIS_LEFT_X);
            input_yAxes = controller.getAxes(0, GLFW_GAMEPAD_AXIS_LEFT_Y);
        }
        else 
        {
            //Horizontal
            if(l && !r){input_xAxes = -1.0f;}
            else if(!l && r){input_xAxes = 1.0f;}
            else{input_xAxes = 0.0f;}

            //Vertical and movement accuracy (sorry, no fast diagonals here)
            if(u && !d)
            {
                if(input_xAxes < 0.0f)
                {
                    //input_xAxes = -CollisionObject.DIAGONAL_AXES;
                    //input_yAxes = -CollisionObject.DIAGONAL_AXES;
                }
                else if(input_xAxes > 0.0f)
                {
                    //input_xAxes = CollisionObject.DIAGONAL_AXES;
                    //input_yAxes = -CollisionObject.DIAGONAL_AXES;
                }
                else{input_yAxes = -1.0f;}
            }
            else if(!u && d)
            {
                if(input_xAxes < 0.0f)
                {
                    //input_xAxes = -CollisionObject.DIAGONAL_AXES;
                    //input_yAxes = CollisionObject.DIAGONAL_AXES;
                }
                else if(input_xAxes > 0.0f)
                {
                    //input_xAxes = CollisionObject.DIAGONAL_AXES;
                    //input_yAxes = CollisionObject.DIAGONAL_AXES;
                }
                else{input_yAxes = 1.0f;}
            }
            else{input_yAxes = 0.0f;}
        }
    }

    public void doNothing(float timeMod){return;}

    public void idle(float timeMod)
    {

    }

    public void idle_breathe(float timeMod)
    {

    }
}
