package UnBlocked;
/**
 * This is the class that manages Mouse input.
 * 
 * Author: Luke Sullivan
 * Last Edit: 3/9/2022
 */
import static org.lwjgl.glfw.GLFW.*;

import UnBlocked.Graphics.Screen;

public class Mouse
{
    //Array of Pressed Buttons.
    private final boolean[] buttonsHeld = new boolean[GLFW_MOUSE_BUTTON_LAST],
    buttonsPressed = new boolean[buttonsHeld.length];

    //Screen for the Mouse to calculate to.
    private Screen screen;

    //If the mouse was moving
    private boolean isMoving = false;

    //Cursor Coordinates
    private double x = 0, y = 0;

    //Scroll direction
    private byte scroll = 0;

    /**Mouse Constructor.*/
    public Mouse(){}

    public void setScreen(Screen screen){this.screen = screen;}

    /*
     * Mouse Buttons
     */

    /**This is the function GLFW will refer to whenever a Mouse Button is pressed or released.*/
    public void mouseButtonCallback(long window, int button, int action, int mods)
    {
        if(action == GLFW_PRESS)
        {
            buttonsHeld[button] = true;
            buttonsPressed[button] = true;
        }
        else if(action == GLFW_RELEASE)
        {
            buttonsHeld[button] = false;
            buttonsPressed[button] = false;
        }
    }


    /**
     * Checks if the given button is Held Down.
     * 
     * @param button should a GLFW_MOUSE_BUTTON Constant.
     * @return true if the button was held down during this check.
     */
    public boolean buttonHeld(int button)
    {
        buttonsPressed[button] = false;
        return buttonsHeld[button];
    }

    /**
     * Same as buttonHeld(), but disables the button upon retrival, allowing for Input-Buffering.
     * 
     * @param button should a GLFW_MOUSE_BUTTON Constant.
     * @return true if the button was pressed during this check.
     */
    public boolean buttonPressed(int button)
    {
        boolean result = buttonsPressed[button];
        buttonsPressed[button] = false;
        //buttonsHeld[button] = false;
        return result;
    }

    public void pressButton(int button)
    {
        buttonsPressed[button] = true;
        buttonsHeld[button] = true;
    }


    /*
     * Mouse Cursor
     */

    /**This is the function GLFW will refer to whenever the Mouse Cursor moves.*/
    public void mouseCursorCallback(long window, double x, double y)
    {
        if(screen.maintainAspectRatio())
        {
            this.x = ((x - screen.getViewportX()) / screen.getViewportWidth()) * screen.getWidth();
            this.y = ((y - screen.getViewportY()) / screen.getViewportHeight()) * screen.getHeight();
        }
        else
        {
            int[] width = new int[1], height = new int[1];
            glfwGetWindowSize(window, width, height);

            this.x = (x/width[0]) * screen.getWidth();
            this.y = (y/height[0]) * screen.getHeight();
        }

        isMoving = true;
        //System.out.println(getX() + " " + getY());
    }

    /**Returns if the mouse is moving.*/
    public boolean isMoving(){return isMoving;}
    
    /**Returns Mouse's X-Position.*/
    public double getX()
    {
        isMoving = false;
        return x;
    }

    /**Returns Mouse's Y-Position.*/
    public double getY()
    {
        isMoving = false;
        return y;
    }


    /*
     * Mouse Scroll Wheel
     */

    /**This is the function GLFW will refer to whenever the Mouse Scroll-Wheel is used.*/
    public void mouseScrollCallback(long window, double xOffset, double yOffset)
    {
        //This program isn't going to do anything with horizontal scroll input, so ignore it.
        scroll = (byte)((yOffset < 0) ? -1 : 1);
    }

    /**Returns which direction the Scroll-Wheel was scrolled in.*/
    public byte getScroll()
    {
        byte result = scroll;
        scroll = 0;
        return result;
    }

    public byte getScroll_Continue(){return scroll;}

    public void setScroll(byte scroll){this.scroll = scroll;}
    public void resetScroll(){scroll = 0;}
}
