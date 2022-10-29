package UnBlocked;
/**
 * 
 */
import org.joml.Vector4f;

import UnBlocked.Graphics.Screen;
import UnBlocked.Graphics.Sprite;
import UnBlocked.Graphics.Animations.FrameAnimation;
import UnBlocked.Graphics.SpriteRenderer.ScaleSpriteRenderer;

public class Tile implements TileRenderable
{
    //Animation.
    private FrameAnimation animation;

    //SpriteRenderer.
    private ScaleSpriteRenderer spriteRenderer = null;

    /**Constructor.*/
    public Tile(FrameAnimation animation, int xOffset, int yOffset, Vector4f color)
    {
        //Set animation.
        this.animation = animation;

        //Set spriteRenderer.
        spriteRenderer = new ScaleSpriteRenderer(animation.getSprite(0), xOffset, yOffset, true,
        (color.x == 1.0f && color.y == 1.0f && color.z == 1.0f && color.w == 1.0f) ? false : true);

        //Set color.
        spriteRenderer.setBlendingColor(color);
    }

    /**Constructor.*/
    public Tile(Sprite sprite, int xOffset, int yOffset, Vector4f color)
    {
        this(new FrameAnimation(0, new Sprite[]{sprite}, new float[]{1.0f}), xOffset, yOffset, color);
    }


    /**Updates animation.*/
    public void update(float timeMod)
    {
        //Update animation and set Sprite.
        //spriteRenderer.setSprite(animation.update(timeMod));
    }

    @Override
    /**Renders this tile.*/
    public void render(Screen screen, int x, int y, float scale)
    {
        //screen.fillRect(x, y, Level.TILE_SIZE, Level.TILE_SIZE, Screen.DEFAULT_BLEND, false);

        //Render the currently set Sprite.
        spriteRenderer.render(screen, x, y-4, 0.0f, scale);
    }
}
