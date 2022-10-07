package UnBlocked;
/**
 * Main Input Device for the game.
 * 
 * Author: Luke Sullivan
 * Last Edit: 10/6/2022
 */
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;

import org.lwjgl.glfw.GLFWGamepadState;

//import static org.lwjgl.system.MemoryUtil.*;
import static org.lwjgl.glfw.GLFW.*;

//import UnBlocked.Data.Profile;

public class Controller
{
    //Array of booleans tracking which keys are pressed
    private final boolean[] keyHeld = new boolean[350],
    keyPressed = new boolean[keyHeld.length];

    public static final byte MAX_PLAYERS = 4;
    private final String[] controllerPresent = new String[MAX_PLAYERS];

    //Two Custom Buttons for Triggers
    public static final int LEFT_TRIGGER = GLFW_GAMEPAD_BUTTON_LAST+1,
    RIGHT_TRIGGER = GLFW_GAMEPAD_BUTTON_LAST+2;


    //IDs for Menu Actions.
    public static final int MAX_MENU_ACTIONS = 7,
    menu_UP = 0,
    menu_DOWN = 1,
    menu_LEFT = 2,
    menu_RIGHT = 3,
    //
    menu_CONFIRM = 4,
    menu_CANCEL = 5,
    menu_SPECIAL = 6;


    //ID Numbers for all the different actions a key can be bound to
    public transient static final byte MAX_ACTIONS = 11,
    action_UP = 0,
    action_DOWN = 1,
    action_LEFT = 2,
    action_RIGHT = 3,
    //
    action_USE = 4;


    //Array storing Keyboard Key controls.
    public int[][] gameKeys = new int[Controller.MAX_PLAYERS][MAX_ACTIONS];

    //Array storing Controller Button controls.
    public int[] gameButtons = new int[MAX_ACTIONS];


    //Global Menu Keys.
    public static final int[][] menu_Keys =
    {
        //Player 1/5
        {
            GLFW_KEY_W, GLFW_KEY_S, GLFW_KEY_A, GLFW_KEY_D,
            GLFW_KEY_F, GLFW_KEY_G, GLFW_KEY_Q
        },
        //Player 2/6
        {
            GLFW_KEY_I, GLFW_KEY_K, GLFW_KEY_J, GLFW_KEY_L,
            GLFW_KEY_SEMICOLON, GLFW_KEY_APOSTROPHE, GLFW_KEY_U
        },
        //Player 3/7
        {
            GLFW_KEY_UP, GLFW_KEY_DOWN, GLFW_KEY_LEFT, GLFW_KEY_RIGHT,
            GLFW_KEY_KP_0, GLFW_KEY_KP_DECIMAL, GLFW_KEY_RIGHT_CONTROL
        },
        //Player 4/8
        {
            GLFW_KEY_KP_8, GLFW_KEY_KP_5, GLFW_KEY_KP_4, GLFW_KEY_KP_6,
            GLFW_KEY_KP_ADD, GLFW_KEY_KP_SUBTRACT, GLFW_KEY_KP_7
        },
    };

    public static final int[] menu_Buttons =
    {
        GLFW_GAMEPAD_BUTTON_DPAD_UP, GLFW_GAMEPAD_BUTTON_DPAD_DOWN, GLFW_GAMEPAD_BUTTON_DPAD_LEFT, GLFW_GAMEPAD_BUTTON_DPAD_RIGHT,
        GLFW_GAMEPAD_BUTTON_A, GLFW_GAMEPAD_BUTTON_B, GLFW_GAMEPAD_BUTTON_Y
    };

    //Gee, thanks guide button...
    private GLFWGamepadState[] controllerStates = new GLFWGamepadState[MAX_PLAYERS];

    /**Constructor.*/
    public Controller()
    {
        for(int i = 0; i < controllerStates.length; i++)
        {controllerStates[i] = GLFWGamepadState.calloc();}

        //checkForControllers();
    }

    //Buttons
    private ByteBuffer[] buttonHeld = new ByteBuffer[MAX_PLAYERS];

    //Single Frame Button Inputs
    private byte[][] buttonPressed = new byte[MAX_PLAYERS][GLFW_GAMEPAD_BUTTON_LAST+1];

    //Axes
    private FloatBuffer[] axes = new FloatBuffer[MAX_PLAYERS];

    /**Checks for Button and Axes input*/
    public void update()
    {
        //for(int i = 0; i < 1; i++)
        for(int i = 0; i < MAX_PLAYERS; i++)
        {
            //If a controller is plugged in.
            if(glfwGetGamepadState(i, controllerStates[i]))
            {
                //Get Button Hold Inputs
                buttonHeld[i] = controllerStates[i].buttons();
                ByteBuffer buffer = buttonHeld[i];

                //Get Button Press Inputs
                for(int j = 0; j < buffer.capacity(); j++)
                {
                    byte b = buffer.get();

                    if(b == 0){buttonPressed[i][j] = 0;}
                    else if(buttonPressed[i][j] != 2){buttonPressed[i][j] = 1;}
                }

                //Get Axes
                axes[i] = controllerStates[i].axes();

                /*
                System.out.println("Buttons: " + buttonHeld[i].get(0) + "" + buttonHeld[i].get(1) + ""+ buttonHeld[i].get(2) + "" + buttonHeld[i].get(3)
                + "" + buttonHeld[i].get(4) + "" + buttonHeld[i].get(5) + "" + buttonHeld[i].get(6) + "" + buttonHeld[i].get(7)
                + "" + buttonHeld[i].get(8) + "" + buttonHeld[i].get(9) + "" + buttonHeld[i].get(10) + "" + buttonHeld[i].get(11)
                + "" + buttonHeld[i].get(12) + ""  + buttonHeld[i].get(13) + ""  + buttonHeld[i].get(14));

                System.out.println("Axes: " + axes[i].get(0) + " " + axes[i].get(1) + " "
                + axes[i].get(2) + " " + axes[i].get(3) + " "
                + axes[i].get(4) + " " + axes[i].get(5));
                */
            }
            //else if(i == 0){System.out.println(glfwGetGamepadState(i, controllerStates[i]));}
        }
    }

    /**To be used on program startup*/
    public void checkForControllers()
    {
        //Strings are stored instead of booleans so that way, the program
        //can differentiate between an XBOX 360 and PS4 controller, for example.

        //Controller GUID Keywords: Xbox, NSW

        for(int i = 0; i < MAX_PLAYERS; i++)
        {
            //System.out.println(glfwJoystickPresent(i));
            controllerPresent[i] = glfwGetJoystickName(i);
            //System.out.println(controllerPresent[i]);
        }
    }

    /*
     * Keyboard Stuff
     */

    /**This is this function GLFW refers to when recieving Keyboard input*/
    public void keyCallback(long window, int key, int scancode, int action, int mods)
    {
        //If in a Typing Prompt, check for BackSpace and ignore important actions
        if(typingMode != TYPING_NONE)
        {
            if(action == GLFW_PRESS || action == GLFW_REPEAT)
            {
                if(key == GLFW_KEY_LEFT && cursorIndex > 0){cursorIndex--;}
                else if(key == GLFW_KEY_RIGHT && cursorIndex < stringObject.length()){cursorIndex++;}
                //
                if(key == GLFW_KEY_BACKSPACE && stringObject.length() > 0 && cursorIndex > 0)
                {
                    String s0 = stringObject.substring(0, cursorIndex-1),
                    s1 = stringObject.substring(cursorIndex);
                    //
                    stringObject = s0 + s1;
                    cursorIndex--;
                    //System.out.println(stringObject);
                }
                else if(key == GLFW_KEY_ENTER){typingMode = TYPING_NONE;}
            }
            return;
        }

        //Otherwise, check important actions
        if(key == GLFW_KEY_UNKNOWN){return;}
        //
        if(action == GLFW_PRESS)
        {
            keyHeld[key] = true;
            keyPressed[key] = true;
        }
        else if(action == GLFW_RELEASE)
        {
            keyHeld[key] = false;
            keyPressed[key] = false;
        }
    }

    /**Checks if a key is being held.*/
    public boolean isKeyHeld(int keyCode){return keyHeld[keyCode];}

    /**Same thing as isKeyHeld(), but disables the key upon retrival, allowing for Input-Buffering.*/
    public boolean isKeyPressed(int keyCode)
    {
        boolean result = keyPressed[keyCode];
        keyPressed[keyCode] = false;
        return result;
    }

    public static final byte TYPING_NONE = 0,
    TYPING_ANY = 1,
    TYPING_INT = 2,
    TYPING_FLOAT = 4,
    TYPING_LETTER = 5;

    private byte typingMode = TYPING_NONE;
    private int cursorIndex = 0;
    private String stringObject;

    /**Allows for typing in-program without external API.*/
    public void charCallback(long window, int codepoint)
    {
        char c = (char)codepoint;
        //
        switch(typingMode)
        {
            case TYPING_NONE:
            return;

            case TYPING_ANY:
            //Allow any character.
            break;

            case TYPING_INT:
            //Only allow numbers.
            if(c < '0' || c > '9'){return;}
            break;

            case TYPING_FLOAT:
            //Only allow numbers and decimal points.
            if((c < '0' || c > '9') && c != '.'){return;}
            break;

            case TYPING_LETTER:
            //Only allow letters.
            if((c < 'A' || c > 'Z') && (c < 'a' || c > 'z')){return;}
            break;
        }
        //char n = '';
        //
        if(cursorIndex == stringObject.length())
        {
            stringObject += c;
            cursorIndex++;
        }
        else
        {
            String s0 = stringObject.substring(0, cursorIndex),
            s1 = stringObject.substring(cursorIndex);
            //
            s0 += c;
            //
            stringObject = s0 + s1;
            cursorIndex++;
            //
            //System.out.println(stringObject);
            //System.out.println((char)codepoint);
        }
    }

    public boolean isTyping(){return typingMode != TYPING_NONE;}
    public int getCursorIndex()
    {
        //if(cursorIndex >= stringObject.length()){cursorIndex = stringObject.length()-1;}
        return cursorIndex;
    }

    public void beginTyping(String stringObject, final byte typingMode, int cursorIndex)
    {
        this.typingMode = typingMode;
        //
        this.stringObject = null;
        this.stringObject = stringObject;
        //
        this.cursorIndex = (cursorIndex > stringObject.length()) ? stringObject.length() : cursorIndex;
    }

    public void beginTyping(String stringObject, final byte typingMode){beginTyping(stringObject, typingMode, stringObject.length());}
    public void beginTyping(String stringObject){beginTyping(stringObject, TYPING_ANY, stringObject.length());}

    public String getStringObject(){return this.stringObject;}

    public String endTyping()
    {
        typingMode = TYPING_NONE;
        return this.stringObject;
    }

    /*
     * Controller Stuff
     */

    /**This is the function GLFW will refer to whenever a Controller is Connected or Disconnected.*/
    public void controllerCallback(int controllerID, int event)
    {
        if(event == GLFW_CONNECTED)
        {controllerPresent[controllerID] = glfwGetJoystickName(controllerID);}
        else if(event == GLFW_DISCONNECTED)
        {controllerPresent[controllerID] = null;}
    }

    /**Returns a button input every frame it is held*/
    public boolean buttonHeld(int playerNum, int button)
    {return (buttonHeld[playerNum].get(button) > 0);}

    /**Returns a button input once the moment it is checked for*/
    public boolean buttonPressed(int playerNum, int button)
    {
        if(buttonPressed[playerNum][button] == 1)
        {
            buttonPressed[playerNum][button] = 2;
            return true;
        }
        return false;
    }

    //Control sstick deadZone.
    private float deadZone = 0.3f;
    public float getDeadZone(){return deadZone;}

     /**
      * Returns -1.0f to 1.0f values of either Control Sticks or Triggers of the given controller.
      *
      * @param playerNum is the Player Number.
      * @param a is the GLFW control stick/trigger axis you want to check for.
      * @return a float from -1.0f to 1.0f of the requested axis.
      */
    public float getAxes(int playerNum, int a)
    {
        float result = 0.0f;

        if(glfwJoystickPresent(playerNum))
        {
            result = axes[playerNum].get(a);

            //Left/Up Side (LiveZone)
            result = (result <= -0.9f) ? -1.0f  

            //Right/Down Side (LiveZone)
            : (result >= 0.9f) ? 1.0f

            //Middle (DeadZone)
            : (result >= -deadZone && result <= deadZone) ? 0.0f
            
            //Everything else
            : result;
        }

        return result;
    }

    /*
     * Input in general
     */

    /**Returns a held input from either Keyboard or Controller.*/
    public boolean inputHeld(int playerNum, int action)
    {
        if(glfwJoystickPresent(playerNum))
        {
            int button = gameButtons[action];

            switch(button)
            {
                case LEFT_TRIGGER: return (getAxes(playerNum, GLFW_GAMEPAD_AXIS_LEFT_TRIGGER) > 0.0f);

                case RIGHT_TRIGGER: return (getAxes(playerNum, GLFW_GAMEPAD_AXIS_RIGHT_TRIGGER) > 0.0f);
                
                default: return buttonHeld(playerNum, button);
            }
        }

        return isKeyHeld(gameKeys[playerNum][action]);
    }

    /**Returns a Single-Frame Bufferable input for either Keyboard or Controller*/
    public boolean inputPressed(int playerNum, int action)
    {
        if(glfwJoystickPresent(playerNum))
        {
            int button = gameButtons[action];

            switch(button)
            {
                case LEFT_TRIGGER: return (getAxes(playerNum, GLFW_GAMEPAD_AXIS_LEFT_TRIGGER) > 0.0f);

                case RIGHT_TRIGGER: return (getAxes(playerNum, GLFW_GAMEPAD_AXIS_RIGHT_TRIGGER) > 0.0f);

                default: return buttonPressed(playerNum, button);
            }
        }
        return isKeyPressed(gameKeys[playerNum][action]);
    }

    /**This is meant for use with Profile.assignKey()*/
    public int anyKey()
    {
        for(int i = 0; i < keyHeld.length; i++)
        {if(keyHeld[i]){return i;}}
        return -1;
    }

    /**
     * Input Held Method for Menus.
     * 
     * @param playerNum is the Player Number.
     * @param action is the action to check. Use menu_X.
     * @return true if an input assciated with the given action is held down.
     */
    public boolean menu_InputHeld(int playerNum, int action)
    {
        if(glfwJoystickPresent(playerNum))
        {
            int button = menu_Buttons[action];

            boolean dPad = false;
            float axes;
            switch(action)
            {
                case menu_UP:
                dPad = buttonHeld(playerNum, button);
                axes = getAxes(playerNum, GLFW_GAMEPAD_AXIS_LEFT_Y);
                if(axes < -deadZone || dPad){return true;}
                break;

                case menu_DOWN:
                dPad = buttonHeld(playerNum, button);
                axes = getAxes(playerNum, GLFW_GAMEPAD_AXIS_LEFT_Y);
                if(axes > deadZone || dPad){return true;}
                break;

                case menu_LEFT:
                dPad = buttonHeld(playerNum, button);
                axes = getAxes(playerNum, GLFW_GAMEPAD_AXIS_LEFT_X);
                if(axes < -deadZone || dPad){return true;}
                break;

                case menu_RIGHT:
                dPad = buttonHeld(playerNum, button);
                axes = getAxes(playerNum, GLFW_GAMEPAD_AXIS_LEFT_X);
                if(axes > deadZone || dPad){return true;}
                break;

                default: return buttonHeld(playerNum, button);
            }
        }
        return isKeyHeld(menu_Keys[playerNum % 4][action]);
    }

    /**
     * Input Pressed Method for Menus.
     * 
     * @param playerNum is the Player Number.
     * @param action is the action to check. Use menu_X.
     * @return true if an input assciated with the given action is initially being pressed.
     */
    public boolean menu_InputPressed(int playerNum, int action)
    {
        if(glfwJoystickPresent(playerNum))
        {return buttonHeld(playerNum, menu_Buttons[action]);}

        return isKeyHeld(menu_Keys[playerNum % 4][action]);
    }

    /**Checks if any player is holding the given input.*/
    public boolean menu_InputHeld_AnyPlayer(int action)
    {
        for(int i = 0; i < MAX_PLAYERS; i++)
        {
            if(menu_InputHeld(i, action))
            {return true;}
        }
        return false;
    }

    /**Checks if any player has pressed the given input.*/
    public boolean menu_InputPressed_AnyPlayer(int action)
    {
        for(int i = 0; i < MAX_PLAYERS; i++)
        {
            if(menu_InputPressed(i, action))
            {return true;}
        }
        return false;
    }
}
