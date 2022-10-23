package UnBlocked.Graphics.Animations;
/**
 * Animates a series of Sprites
 * 
 * Author: Luke Sullivan
 * Last Edit: 10/18/2022
 */
import UnBlocked.Graphics.Sprite;

public abstract class Animation
{
    //Animations Path (so that way if I have to move the animaions folder, I just need to change this).
    public static final String animationsPath = "assets/Animations/";

    public abstract Sprite update(float timeMod);

    public abstract Sprite getSprite();
    public abstract Sprite[] getSprites();
}
