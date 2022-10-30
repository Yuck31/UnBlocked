package UnBlocked;
import java.io.BufferedInputStream;
/**
 * 
 */
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Random;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;

import org.lwjgl.glfw.GLFWImage;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL33;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.system.MemoryUtil.*;
import static org.lwjgl.BufferUtils.*;

import UnBlocked.Audio.AudioManager;
import UnBlocked.GameStates.GameState;
import UnBlocked.GameStates.GameStateManager;
import UnBlocked.GameStates.MainMenuState;
import UnBlocked.Graphics.OpenGLScreen;
import UnBlocked.Graphics.Screen;
import UnBlocked.Graphics.Sprites;

public class Game
{
    //Resize Stuff
    @FunctionalInterface//Interfaces with only a single abstract method can be passed as a lambda function
    private interface ResizeFunction{public abstract void invoke(int resizeWidth, int resizeHeight);}
    private ResizeFunction resizeFunc; private int newWidth = -1, newHeight = -1;

    //Target FPS and Refresh Rate Stuff.
    public static final int TARGET_FPS = 60, TARGET_DELTA = 1000/TARGET_FPS;
    public static int TARGET_REFRESH_RATE = 60, TARGET_REFRESH_DELTA = 1000/TARGET_REFRESH_RATE;

    //Threads the Game will run on.
    private Thread updateThread, renderThread;

    //Screen Size stuff.
    public static final int ASPECT_WIDTH = 16, ASPECT_HEIGHT = 10,
    SCREEN_WIDTH = 320, SCREEN_HEIGHT = (SCREEN_WIDTH / ASPECT_WIDTH) * ASPECT_HEIGHT;

    //Game Title to put on the Window.
    public static final String TITLE = "Project UnBlocked";
    private String rendererString;

    //Path of Window Icon.
    private String imageIconPath = "ImageIcon.png";

    //Renders the Game.
    private static Screen screen;

    //Manages GameStates.
    private final GameStateManager gameStateManager = new GameStateManager();

    //These manage Player Input.
    public static final Controller controller = new Controller();
    public static final Mouse mouse = new Mouse();

    //This manages Game Audio.
    private static AudioManager audioManager = null;

    //RNG.
    public static final Random RANDOM = new Random();

    /**Constructor for the Game.*/
    private Game(int window_width, int window_height)
    {

        //Set window dimensions.
        this.window_width = window_width;
        this.window_height = window_height;
    }

    private static boolean hasBeenInstantiated = false;

    /**
     * This is meant for initial startup, just so you can't instantiate another Game object and absolutly LAG the CPU or somethin' I dunno...
     * 
     * @param graphicsAPI is which Graphics API to configure the Game to.
     * @param window_width is the Screen Width.
     * @param window_height is the Screen Height.
     * @return a new Game object if one hasn't already been instantiated.
     */
    public static Game instantiate(int window_width, int window_height)
    {
        //Don't do anything if one already exists.
        if(hasBeenInstantiated){return null;}

        //Instantiate the game.
        hasBeenInstantiated = true;
        return new Game(window_width, window_height);
    }

    private int window_width = 0, window_height = 0;

    /**Start the game from the given GameState and with the given screen size.*/
    public void start(int screen_width, int screen_height, GameState state)
    {
        //Start the threads
        run(screen_width, screen_height, state);
    }

    /**Start the game from the given GameState.*/
    public void start(GameState state){start(SCREEN_WIDTH, SCREEN_HEIGHT, state);}

    /**Default start() function.*/
    public void start(){start(SCREEN_WIDTH, SCREEN_HEIGHT, new MainMenuState());}

    /*
     * Stuff for when the game is running
     */

    //Memory Address of the GLFW Window(s), Update Window is visable, Render Window is not and only serves as a 2nd API Context.
    private long updateWindowAddress = NULL, renderWindowAddress = NULL;

    //Boolean the Update Thread checks for to stop.
    private boolean running = true;

    //Synchronization objects used on stratup.
    private Object renderLock = new Object(), updateLock = new Object();

    //Frames-Per-Second, Refreshs-Per-Second.
    private int fps = 0, rps = 0;

    /**Thread Begin Function(s)*/
    public void run(int screen_width, int screen_height, GameState state)
    {
        //Against my wishes, we make the render thread first to set everything up for the rest of runtime. Mostly SpriteSheet loading.
        renderThread = new Thread(() ->
        {
            //Initialize GLFW, Graphics API Context, and Render Window Address.
            //switch(graphicsAPI)
            //{
                //case Main.OPENGL:
                renderWindowAddress = openGL_InitGLFW(true, NULL);
                openGL_InitAPI(true, screen_width, screen_height);
                resizeFunc = this::opengl_resize;
                //break;

                //case Main.VULKAN:
                //renderWindowAddress = vulkan_InitGLFW(true, NULL);
                //vulkan_InitAPI(true, screen_width, screen_height);
                //resizeFunc = this::vulkan_resize;
                //break;

                //default:
                //renderWindowAddress = software_InitGLFW(true, NULL);
                //software_InitAPI(true, screen_width, screen_height);
                //resizeFunc = this::software_resize;
                //break;
            //}

            //Initialize screen stuff.
            //screen.init();

            //Make context not current so we can initialize the update window.
            glfwMakeContextCurrent(NULL);

            //The Render Context is made, so unlock the update thread
            //and wait for the Update Context and GameStateManager to be made.
            synchronized(updateLock){updateLock.notify();}
            synchronized(renderLock)
            {
                try{renderLock.wait();}
                catch(InterruptedException e)
                {e.printStackTrace();}
            }

            //Make context current to update window.
            glfwMakeContextCurrent(updateWindowAddress);

            //fps counting
            long startTime = System.currentTimeMillis();
            int refreshCount = 0;

            //Begin Game Render Loop
            while(!glfwWindowShouldClose(updateWindowAddress))
            {
                //Get start frame time
                long before = System.currentTimeMillis();

                //TODO Use glfw timer?
                
                //Call render()
                render(updateWindowAddress);
                refreshCount++;

                //Get end frame time
                long after = System.currentTimeMillis();

                //The total time it took to render the frame
                int deltaTime = (int)(after-before); 

                //Record Refresh-Rate
                if(after - startTime >= 1000)
                {
                    startTime += 1000;
                    rps = refreshCount;
                    refreshCount = 0;
                }
                
                //If render took less time than the delta we want for 60fps (around 16ms), temporarily pause the thread
                if(TARGET_REFRESH_DELTA > deltaTime)
                {
                    try
                    {
                        //This creates the delay between each Frame(each loop)
                        Thread.sleep(TARGET_REFRESH_DELTA - deltaTime);
                    }
                    catch(InterruptedException e){e.printStackTrace();}
                }
            }
            running = false;
        }, "renderThread");



        updateThread = new Thread(() ->
        {
            //Lock and wait for the Render Window to be made.
            synchronized(updateLock)
            {
                try{updateLock.wait();}
                catch(InterruptedException e)
                {e.printStackTrace();}
            }
            if(renderWindowAddress == NULL){return;}

            //Initialize Graphics API Context for this thread, and Update Window Address while sharing assets with the render window.
            //switch(graphicsAPI)
            //{
                //case Main.OPENGL:
                updateWindowAddress = openGL_InitGLFW(false, renderWindowAddress);
                openGL_InitAPI(false, screen_width, screen_height);
                //break;

                //case Main.VULKAN:
                //updateWindowAddress = vulkan_InitGLFW(false, renderWindowAddress);
                //vulkan_InitAPI(false, screen_width, screen_height);
                //break;

                //default:
                //updateWindowAddress = software_InitGLFW(false, renderWindowAddress);
                //software_InitAPI(false, screen_width, screen_height);
                //break;
            //}

            //Initialize screen stuff.
            screen.init();

            //Create AudioManager and initialize it if that hasn't already happened.
            audioManager = new AudioManager(updateWindowAddress);
            AudioManager.init();

            //Set Graphics API load function and load the Global SpriteSheets if that hasn't already happened.
            Sprites.setSheetFunctions();
            Sprites.loadGlobalSheets();            

            //Start the GameStateManager.
            gameStateManager.start(this, state);

            //Context will be made current to render window for... reasons.
            glfwMakeContextCurrent(renderWindowAddress);

            //GameStateManager is started, so unlock the render thread.
            synchronized(renderLock){renderLock.notify();}

            //fps counting.
            long startTime = System.currentTimeMillis();
            int frameCount = 0;

            //Begin Update Loop.
            while(running)
            {
                //Get start frame time
                long before = System.currentTimeMillis();
 
                //Call update()
                update();
                frameCount++;

                //Get end frame time
                long after = System.currentTimeMillis();

                //The total time it took to render the frame
                int deltaTime = (int)(after-before);

                //Record Frame Rate
                if(after - startTime >= 1000)
                {
                    //Updates the title of the Window as needed.
                    glfwSetWindowTitle(updateWindowAddress, Game.TITLE + " | Renderer: " + rendererString + " | delta: " + deltaTime + " | fps: " + fps + " | rps: " + rps);
                    //System.out.println(getUsedMemory());

                    startTime += 1000;
                    fps = frameCount;
                    frameCount = 0;
                }
                
                //If update took less time than the delta we want for 60fps (around 16ms), temporarily pause the thread.
                if(TARGET_DELTA > deltaTime)
                {
                    try
                    {
                        //This creates the delay between each Frame(each loop).
                        Thread.sleep(TARGET_DELTA - deltaTime);
                    }
                    catch(InterruptedException e){e.printStackTrace();}
                }
            }
            cleanUp();
        }, "updateThread");

        renderThread.start();
        updateThread.start();
    }

    //
    public int getFPS(){return fps;}
    public int getRPS(){return rps;}
    //

    /**Can be called to check how much memory is being used.*/
    public static long getUsedMemory()
    {
        return Runtime.getRuntime().totalMemory()
        - Runtime.getRuntime().freeMemory();
    }




    /*
     * GLFW Initialization
     */

    /**OpenGL 3.3 GLFW Initialization.*/
    private long openGL_InitGLFW(boolean isRenderThread, long windowToShare)
    {
        //Initialize GLFW and give it a window hint.
        initGLFW(isRenderThread);

        //Set OpenGL Version to 3.3 core.
        glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 3);
        glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);

        //Some other settings.
        //glfwWindowHint(GLFW_DOUBLEBUFFER, GLFW_TRUE);
        //glfwWindowHint(GLFW_SRGB_CAPABLE, GLFW_TRUE);

        //Make the window.
        long windowAddress = createWindow(isRenderThread, windowToShare);
        //if(windowAddress == NULL){return windowAddress;}

        //Make the OpenGL Context current to current thread.
        glfwMakeContextCurrent(windowAddress);

        //glfwSwapInterval(1);

        //Set Callbacks and whatnot for the window being displayed
        if(!isRenderThread){setCallbacks(windowAddress);}

        //Return the newly created window's address.
        return windowAddress;
    }




    /*
     * Initialization functions for each.
     */

    private void initGLFW(boolean isRenderThread)
    {
        //If this is the Render Thread, we don't want to show this window.
        //We only want to use it as an OpenGL context.
        if(!isRenderThread)
        {
            glfwWindowHint(GLFW_VISIBLE, GLFW_TRUE);
        }
        else
        {
            //Because a GLFW Context can only exist on a single thread, this needs to be called from the Render Thread to work.
            glfwInit();
            glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
        }
    }

    private long createWindow(boolean isRenderThread, long windowToShare)
    {
        //Create the window and get its address.
        long windowAddress = glfwCreateWindow(window_width, window_height, Game.TITLE, NULL, windowToShare);

        //If a window couldn't be created.
        if(windowAddress == NULL)
        {
            //Terminate GLFW.
            System.err.println("Failed to create GLFW window in the " + ((isRenderThread) ? "Render Thread" : "Update Thread."));
            glfwTerminate();

            //Unlock the Update/Render thread so this program can close.
            if(!isRenderThread){synchronized(renderLock){renderLock.notify();}}
            else{synchronized(updateLock){updateLock.notify();}}

            //Get out of here.
            return NULL;
        }

        //Return the address.
        return windowAddress;
    }

    private void setCallbacks(long windowAddress)
    {
        //Set Window Icon
        setWindowIcon(windowAddress);

        //Focus computer to the Game
        glfwFocusWindow(windowAddress);

        //Set Window Resize Callback to a lamba expression that sets newWidth and newHeight to what the callback returns
        glfwSetFramebufferSizeCallback(windowAddress, (window, w, h) -> {this.newWidth = w; this.newHeight = h;});

        //Set Keyboard Callbacks
        glfwSetKeyCallback(windowAddress, controller::keyCallback);
        glfwSetCharCallback(windowAddress, controller::charCallback);

        //Set Mouse Callbacks
        glfwSetMouseButtonCallback(windowAddress, mouse::mouseButtonCallback);
        glfwSetCursorPosCallback(windowAddress, mouse::mouseCursorCallback);
        glfwSetScrollCallback(windowAddress, mouse::mouseScrollCallback);

        //Update Controller bindings.
        byte[] mappingsBytes = null;
        try
        {
            //Load file.
            File mappingsFile = new File("assets/extraControllerBindings.txt");
            //Set up the input stream.
            FileInputStream fis = new FileInputStream(mappingsFile);
            BufferedInputStream bis = new BufferedInputStream(fis);

            //Read the file's bytes and null-terminate it, gawd diamet GLFW.
            mappingsBytes = new byte[(int)mappingsFile.length() + 1];
            bis.read(mappingsBytes);
            mappingsBytes[mappingsBytes.length-1] = 0;
 
            //Close the input streams.
            bis.close();
            fis.close();
        }
        catch(FileNotFoundException e){e.printStackTrace();}
        catch(IOException e){e.printStackTrace();}
 
        ByteBuffer mappingsByte_Buffer = ByteBuffer.allocateDirect(mappingsBytes.length);
        mappingsByte_Buffer.put(mappingsBytes);
        mappingsByte_Buffer.rewind();
        glfwUpdateGamepadMappings(mappingsByte_Buffer);
         
        //Set Controller Callback
        glfwSetJoystickCallback(controller::controllerCallback);
        controller.checkForControllers();
    }



    /*
     * API Initialization.
     */

    /**API Initialization for OpenGL Renderer.*/
    private void openGL_InitAPI(boolean isRenderThread, int screen_width, int screen_height)
    {
        //Establish OpenGL commands for use
        GL.createCapabilities();

        //Enable Textures.
        GL33.glEnable(GL33.GL_TEXTURE_2D);

        //Enables Alpha-Channel Blending (Translucency/Transperancy)
        //GL33.glEnable(GL33.GL_ALPHA_TEST);
        GL33.glEnable(GL33.GL_BLEND);
        //GL33.glBlendFunc(GL33.GL_ONE, GL33.GL_ONE_MINUS_SRC_ALPHA);
        GL33.glBlendFunc(GL33.GL_SRC_ALPHA, GL33.GL_ONE_MINUS_SRC_ALPHA);

        //Set Depth Testsing Function.
        //GL33.glDepthFunc(GL33.GL_LESS);
        //Depth Testing will be switched on and off in the render() function.

        //GL33.glEnable(GL33.GL_LINE_SMOOTH);
        //GL33.glLineWidth(5);

        //Set viewport and clear color.
        if(!isRenderThread)
        {            
            //Set screen Size.
            GL33.glViewport(0, 0, window_width, window_height);

            //Set clear color.
            GL33.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);

            //Create screen object and let the mouse use it.
            screen = new OpenGLScreen(screen_width, screen_height);
            mouse.setScreen(screen);
            rendererString = "OpenGL";
        }
    }

    /**API Initialization for Vulkan Renderer.*/
    //private void vulkan_InitAPI(boolean isRenderThread, int screen_width, int screen_height)
    //{

    //}


    /*
     * GLFW-Related Stuff
     */

    /**Sets the Icon of the Window.*/
    public void setWindowIcon(long windowAddress)
    {
        try
        {
            //Get Image Pixels
            BufferedImage image = ImageIO.read(new File(imageIconPath));
            int[] pixels = new int[image.getWidth() * image.getHeight()];
            image.getRGB(0, 0, image.getWidth(), image.getHeight(), pixels, 0, image.getWidth());

            //Convert to Bytes
            ByteBuffer data = createByteBuffer(pixels.length * 4);
            for(int i = 0; i < pixels.length; i++)
            {
                data.put((byte)((pixels[i] & 0x00FF0000) >> 16));
                data.put((byte)((pixels[i] & 0x0000FF00) >> 8));
                data.put((byte)(pixels[i] & 0x000000FF));
                data.put((byte)((pixels[i] & 0xFF000000) >> 24));
            }
            data.rewind();

            //Create GLFWImage object
            GLFWImage.Buffer glfwImage = GLFWImage.calloc(1).pixels(data).width(image.getWidth()).height(image.getHeight());

            //And set
            glfwSetWindowIcon(windowAddress, glfwImage);
            glfwImage.close();
        }
        catch(IOException e){e.printStackTrace();}
    }

    /**Sets the Path of the Icon Image.*/
    public void setImageIconPath(String imageIconPath){this.imageIconPath = imageIconPath;}





    /*
     * Actual Game-Logic Stuff
     */

    /**Updates everything Game-Logic related.*/
    private void update()
    {
        //Update Player Input, Resize Callback, and whatnot.
        glfwPollEvents();
        controller.update();

        //Update GameStateManager, which updates EVERYTHING game-logic related
        gameStateManager.update();
    }

    public AudioManager getAudioManager(){return audioManager;}

    /**Renders the game.*/
    private void render(long window)
    {
        //Call Resize Function
        if(newWidth > -1 || newHeight > -1){resizeFunc.invoke(newWidth, newHeight);}

        //Clear Screen of Pixels from previous frame
        screen.clear();

        //Call GameStateManager render function.
        //Through this, stuff like Light Culling and Sprite Rendering is done.
        gameStateManager.render(screen);

        //Call Screen's render function
        screen.render(window);
    }


    /*
     * Resize function(s)
     */

    private void opengl_resize(int resizeWidth, int resizeHeight)
    {
        int vpX = 0, vpY = 0, vpWidth = resizeWidth, vpHeight = resizeHeight;
        //
        if(screen.maintainAspectRatio())
        {
            if(screen.fitToScreen())
            {
                int aspectWidth = resizeWidth,
                aspectHeight = (int)(((float)aspectWidth / ASPECT_WIDTH) * ASPECT_HEIGHT);

                if(aspectHeight > resizeHeight)
                {
                    aspectHeight = resizeHeight;
                    aspectWidth = (int)(((float)aspectHeight / ASPECT_HEIGHT) * ASPECT_WIDTH);
                }

                //Set Viewport Position
                vpX = (int)(((float)resizeWidth / 2f) - ((float)aspectWidth / 2f));
                vpY = (int)(((float)resizeHeight / 2f) - ((float)aspectHeight / 2f));

                //Put Dimensions into the screen class for the Mouse
                vpWidth = aspectWidth; vpHeight = aspectHeight;
                screen.setViewportDimensions(vpX, vpY, vpWidth, vpHeight);
            }
            else
            {
                //Set screen size
                int screenWidth = screen.getWidth(),// * scale,
                screenHeight = screen.getHeight();// * scale;

                //Set Viewport Position
                vpX = (resizeWidth - screenWidth) / 2;
                vpY = (resizeHeight - screenHeight) / 2;

                //Put Dimensions into the screen class for the Mouse
                vpWidth = screenWidth; vpHeight = screenHeight;
                screen.setViewportDimensions(vpX, vpY, vpWidth, vpHeight);
            }
        }
        newWidth = -1; newHeight = -1;
    }


    private void cleanUp()
    {
        //Delete AudioManager
        audioManager.cleanUp();

        //Unload OpenGL.
        //GL.destroy();

        //Terminate GLFW.
        glfwTerminate();
    }

    /*
     * Miscellaneous
     */

    /**Converts 4 Big-Endian bytes to an int.*/
    public static int bytesToInt(byte b0, byte b1, byte b2, byte b3)
    {
        return (b0 << 24)  | ((b1 & 0xff) << 16)
        | ((b2 & 0xff) << 8)  | (b3 & 0xff);
    }

    /**Converts 4 Big-Endian bytes to a float.*/
    public static float bytesToFloat(byte b0, byte b1, byte b2, byte b3)
    {
        return Float.intBitsToFloat
        (
            b0 << 24 | (b1 & 0xFF) << 16 |
            (b2 & 0xFF) << 8 | (b3 & 0xFF)
        );
    }

    /**Converts 2 Big-Endian bytes to a short.*/
    public static short bytesToShort(byte b0, byte b1)
    {return (short)((b0 << 8)  | (b1 & 0xff));}

    /**Converts 2 Big-Endian bytes to a char.*/
    public static char bytesToChar(byte b0, byte b1)
    {return (char)((b0 << 8)  | (b1 & 0xff));}
}
