package UnBlocked.GameStates;
/**
 * State for the Main Menu
 * 
 * Author: Luke Sullivan
 * Last Edit: 8/16/2022
 */
//import static org.lwjgl.glfw.GLFW.*;

import org.joml.Vector4f;

import UnBlocked.Controller;
//import UnBlocked.Game;
import UnBlocked.Graphics.*;
import UnBlocked.UI.Dialogue.DropDown_Menu;
import UnBlocked.UI.Menus.MouseMenu;
import UnBlocked.UI.Menus.TextBox;
import UnBlocked.Util.Shapes2D.AAB_Box2D;
import UnBlocked.Audio.*;

//import JettersR.UI.Menus.*;//MainMenu.*;

public class MainMenuState extends GameState
{
    //MainMenu mainMenu;
    SpriteSheet test = null;

    AudioBuffer select0 = null, select1 = null;
    SoundSource source = null;

    MouseMenu menu = new MouseMenu(0, 0, new AAB_Box2D(300, 300));
    Vector4f[] colors = {new Vector4f(0.5f, 0.5f, 0.5f, 1.0f), new Vector4f(0.8f, 0.8f, 0.8f, 1.0f)};

    public void start()
    {
        test = Sprites.global_UISheet("READY_BOMB");
        select0 = Audio.global_UISound("Select0");
        select1 = Audio.global_UISound("Select1");
        source = new SoundSource();
        //
        menu.addComponent(
            new DropDown_Menu(100, 100, 100, 18, colors, "Drop DOWN", 1.0f)
            .addOption("WOAH", () -> {})
            .addOption("uh", () -> {})
            .addOption("huh", () -> {})
            .addOption("wat?", () -> {})
            .addOption("AAAAH", () -> {})
            .addOption("This is an option.", () -> {})
            .addOption("THE END...", () -> {})
        )
        .addComponent(new TextBox(300, 150, 100, 20, Fonts.get("Arial"), Controller.TYPING_LETTER,
        1.0f, new Vector4f(0.2f, 0.2f, 0.2f, 1.0f), Screen.DEFAULT_BLEND));
    }

    public void end(){source.delete();}

    

    int i = 0, j = 0; Vector4f color = new Vector4f(0, 0, 0, 1.0f);
    public void update()
    {
        //i = (i+1) % 255;
        //j=i;
        
        //color.y = (float)j/255f;
        //System.out.println(j);

        //if(Game.mouse.buttonPressed(GLFW_MOUSE_BUTTON_LEFT))
        //{
            //source.setPosition((float)Game.mouse.getX()-320, (float)Game.mouse.getY()-180, 0);
            //source.play(select0.getID(), select1.getID());
        //}

        //if(Game.controller.isKeyPressed(GLFW_KEY_0)){source.play(select1.getID());}


        menu.update(true, true);
    }

    public void render(Screen screen)
    {
        //screen.renderSheet(3, 3, test, color, false);
        menu.render(screen, 0, 0);
    }
}
