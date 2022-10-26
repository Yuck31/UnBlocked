package UnBlocked.Entities;

import UnBlocked.TileRenderable;
import UnBlocked.Graphics.Screen;
import UnBlocked.Graphics.SpriteRenderer.ScaleSpriteRenderer;

/**
 * 
 */

public class Block extends Entity implements TileRenderable
{
    //Sprite Renderer.
    private ScaleSpriteRenderer spriteRenderer;

    //private Vector2f targetPosition = new Vector2f();

    /**Constructor.*/
    public Block(int x, int y)
    {
        super(x, y);
    }
    
    @Override
    /**Update function.*/
    public void update(float timeMod)
    {

    }

    @Override
    /**Render function.*/
    public void render(Screen screen, int x, int y, float scale)
    {
        // TODO Auto-generated method stub
    }
}
