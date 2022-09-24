package UnBlocked.Graphics.Animations;
/**
 * Author: Luke Sullivan
 * Last Edit: 3/21/2022
 */
import UnBlocked.Graphics.Sprite;

public class WrapAnimation extends Animation
{
    private Sprite sprite;
    private float x = 0, y = 0;
    private float wrapX, wrapY;

    public WrapAnimation(Sprite sprite, float wrapX, float wrapY, float x, float y)
    {
        this.sprite = sprite;
        this.wrapX = wrapX;
        this.wrapY = wrapY;
        this.x = x;
        this.y = y;
    }

    public WrapAnimation(Sprite sprite, float wrapX, float wrapY)
    {this(sprite, wrapX, wrapY, 0, 0);}

    public Sprite update(float timeMod)
    {
        x += (wrapX * timeMod) % sprite.getWidth();
        y += (wrapY * timeMod) % sprite.getHeight();
        return sprite;
    }

    public Sprite getSprite(){return sprite;}
    public Sprite[] getSprites(){return new Sprite[]{sprite};}

    public int getX(){return (int)x;}
    public int getY(){return (int)y;}

    public float getWrapX(){return wrapX;}
    public float getWrapY(){return wrapY;}
}
