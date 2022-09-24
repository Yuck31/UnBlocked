package UnBlocked.GameStates;
/**
 * Author: Luke Sullivan
 * Last Edit: 9/23/2022
 */
import org.joml.Vector3f;
import org.joml.Vector4f;

import static org.lwjgl.glfw.GLFW.*;

import java.io.IOException;

import UnBlocked.Controller;
import UnBlocked.Game;
//import UnBlocked.Level;
//import UnBlocked.Audio.MusicSource;
//import UnBlocked.Data.Profile;
//import UnBlocked.Entities.Player;
//import UnBlocked.Entities.Components.Lights.AreaLight;
import UnBlocked.Graphics.*;
//import JettersRDevTools.LevelEditor.JettersR_LevelEditor.LevelEditor;

public class TestState extends GameState
{
    //Level level;
    //Player player = null;
    //AreaLight light = null;
    private transient Controller controller = Game.controller;

    //private MusicSource musicSource;

    public void start()
    {
        //player = new Player(32, 128, 32, new Profile(), 0, Player.PLAYER_TYPE_HUMAN);

        //level = new Level();
        //level.add(new Player(32, 0, 32, new Profile(), 1, Player.PLAYER_TYPE_HUMAN));
        //level.add(player);
        //level.add(new Player(-64,64, 32, new Profile(), 2, Player.PLAYER_TYPE_HUMAN));
        //level.add(player);

        //light = new AreaLight(64, 0, 60, new Vector3f(1.0f, 0.5f, 0.0f), new Vector3f(0.5f, 0.25f, 0.0f),
        //128, 128, 128, 32.0f, (byte)0, (byte)0, (byte)0);

        //level.add(light);

        //level.add
        //(
            //new AreaLight(-96, 0, 32, new Vector3f(0.5f, 1.0f, 1.0f), new Vector3f(0.25f, 0.5f, 0.5f),
            //128, 128, 128, 64.0f, (byte)0, (byte)0, (byte)0)
        //);

        //Load Music File
        //musicSource = new MusicSource("Battle/Sup3Battle");
        //musicSource.play();
    }

    public void end()
    {
        
    }

    boolean paused = false;

    float j = 0;
    public void update()
    {
        //musicSource.loop.update();
        //
        if(!paused || controller.isKeyPressed(GLFW_KEY_LEFT_BRACKET))
        {
            //if(controller.isKeyHeld(GLFW_KEY_KP_ADD)){level.addScale(0.1f, Game.NORMAL_WIDTH, Game.NORMAL_HEIGHT);}
            //if(controller.isKeyHeld(GLFW_KEY_KP_SUBTRACT)){level.addScale(-0.1f, Game.NORMAL_WIDTH, Game.NORMAL_HEIGHT);}
            //if(controller.isKeyHeld(GLFW_KEY_KP_ENTER)){level.setScale(1.0f, Game.NORMAL_WIDTH, Game.NORMAL_HEIGHT);}
            //
            //level.update();
            //light.setRadius((64 * (float)Math.abs(Math.sin(j += 0.01))));
            //light.setZ(20 + (64 * (float)(Math.sin(j += 0.01))));
        }

        if(controller.isKeyPressed(GLFW_KEY_ENTER))
        {
            //Pause.
            paused = !paused;
        }
        if(controller.isKeyPressed(GLFW_KEY_1))
        {
            //Save Entities.
            //try
            //{
                //LevelEditor.saveEntities(level, "Test");
            //}
            //catch(IOException e){e.printStackTrace();}
        }
    }

    Vector4f color = new Vector4f(1.0f, 0.0f, 0.0f, 1.0f);

    Font arial = Fonts.get("Arial");
    Vector4f c = new Vector4f(1.0f, 1.0f, 1.0f, 1.0f);
    float i = 0;

    public void render(Screen screen)
    {
        c.y = Math.abs((float)Math.sin(i += 0.05));
        c.z = c.y;

        //level.render(screen);
        //screen.drawLine(100, 10, 32, (int)player.getX(), (int)player.getY(), (int)player.getZ(), color, true);

        arial.render(screen, 100, 280, "gorsh! Does this work?`Only time will tell...", c, false);
    }
}
