package UnBlocked.Entities;
import UnBlocked.Level;
/**
 * 
 */
import UnBlocked.Graphics.Screen;
import UnBlocked.Graphics.Sprite;
import UnBlocked.Graphics.SpriteSheet;
import UnBlocked.Graphics.Sprites;
import UnBlocked.Graphics.SpriteRenderer.ScaleSpriteRenderer;

public class Block extends Entity
{
    //Sprite Renderer.
    private ScaleSpriteRenderer spriteRenderer;

    public static int targetY = -1;

    /**Constructor.*/
    public Block(int x, int y)
    {
        super(x, y);
        //
        SpriteSheet blockSheet = Sprites.global_EntitySheet("Block");
        Sprite blockSprite = new Sprite(blockSheet);
        spriteRenderer = new ScaleSpriteRenderer(blockSprite, 0, -4, true, false);
    }
    
    @Override
    public boolean isSolid(){return true;}
    
    @Override
    /**Update function.*/
    public void update(float timeMod)
    {

    }    

    public boolean fall(float timeMod, float yVelocity)
    {
        if(position.y + yVelocity >= Block.targetY << Level.TILE_BITS){return true;}
        position.y += yVelocity;
        return false;
    }

    @Override
    /**Tile Render function.*/
    public void render(Screen screen, int x, int y, float scale)
    {spriteRenderer.render(screen, x, y, 0.0f, scale);}

    public void render(Screen screen, float scale)
    {spriteRenderer.render(screen, position.x, position.y, 0.0f, scale);}
}
