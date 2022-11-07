package UnBlocked;
/**
 * Author: Luke Sullivan
 * Last Edit: 10/19/2022
 */
import java.io.File;

import org.joml.Vector2f;
import org.joml.Vector4f;

import UnBlocked.Graphics.Screen;
import UnBlocked.Graphics.Sprite;
import UnBlocked.Graphics.SpriteSheet;
import UnBlocked.Graphics.Sprites;
import UnBlocked.Graphics.Animations.FrameAnimation;
import UnBlocked.Entities.Entities;
import UnBlocked.Entities.Entity;
import UnBlocked.Entities.Player;

public class Level
{
    //Player.
    private Player player;

    //Constants.
    public static final int TILE_BITS = 4, TILE_SIZE = 1 << TILE_BITS;

    //Dimensions and Tile arrays.
    private int width, height;
    private byte[] tiles, entities;

    //Tile animations.
    private Tile[] background_TileAnims = new Tile[15], playground_TileAnims = new Tile[15];
    private byte num_backgroundTiles, num_playgroundTiles;

    //Camera.
    private LevelCamera camera = null;

    /**Constructor loading from file.*/
    public Level(File file)
    {
        this.camera = new LevelCamera(this);
    }

    //Gawd diamit Java...
    private byte[] intArrayToByteArrayCast(int[] data)
    {
        byte[] result = new byte[data.length];
        for(int i = 0; i < data.length; i++){result[i] = (byte)data[i];}
        return result;
    }

    /**Level Editor constructor.*/
    public Level(int width, int height, Vector2f position)
    {
        //Make camera.
        this.camera = new LevelCamera(this);
        this.camera.setEntityPosition(position);

        //Set dimensions.
        this.width = width;
        this.height = height;

        //Set tiles.
        this.tiles = new byte[width * height];

        //Set entities.
        this.entities = new byte[width * height];
    }

    /**Test Constructor.*/
    public Level()
    {
        //Make camera.
        this.camera = new LevelCamera(this);

        //Set dimensions.
        this.width = 12;
        this.height = 8;

        //Set tiles.
        this.tiles = intArrayToByteArrayCast
        (
            new int[]
            {
                0x10, 0x10, 0x10, 0x10, 0x10, 0x10, 0x10, 0x10, 0x10, 0x10, 0x10, 0x10,
                0x20, 0x01, 0x02, 0x02, 0x01, 0x01, 0x01, 0x01, 0x02, 0x02, 0x01, 0x20,
                0x20, 0x01, 0x02, 0x02, 0x01, 0x01, 0x01, 0x01, 0x02, 0x02, 0x01, 0x20,
                0x20, 0x01, 0x02, 0x02, 0x01, 0x01, 0x01, 0x01, 0x02, 0x02, 0x01, 0x20,
                0x20, 0x01, 0x02, 0x02, 0x01, 0x00, 0x01, 0x01, 0x02, 0x02, 0x01, 0x20,
                0x20, 0x01, 0x02, 0x02, 0x01, 0x10, 0x01, 0x01, 0x02, 0x02, 0x01, 0x20,
                0x20, 0x01, 0x02, 0x02, 0x10, 0x10, 0x10, 0x01, 0x02, 0x02, 0x01, 0x20,
                0x10, 0x10, 0x10, 0x10, 0x10, 0x10, 0x10, 0x10, 0x10, 0x10, 0x10, 0x10,
            }
        );

        //Color vector.
        Vector4f color = new Vector4f(1.0f, 1.0f, 1.0f, 1.0f);

        //Set animations.
        SpriteSheet testBrick = Sprites.loadTileSheet("TestBrick");
        background_TileAnims[0] = new Tile(new Sprite(testBrick, 0, 0, 20, 20), 4, -4, color);
        background_TileAnims[1] = new Tile(new Sprite(testBrick, 20, 0, 20, 20), 4, -4, color);
        playground_TileAnims[0] = new Tile(new Sprite(testBrick, 0, 20, 20, 20), 0, 0, color);
        playground_TileAnims[1] = new Tile(new Sprite(testBrick, 20, 20, 20, 20), 0, 0, color);

        num_backgroundTiles = 2;
        num_playgroundTiles = 2;

        //Set entities.
        this.entities = new byte[]
        {
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 8, 0,
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
        };
        initPlayer();
        camera.setEntityPosition(player.getPosition());
    }

    public int getWidth(){return width;}
    public int getHeight(){return height;}


    public void setDimensions(int w, int h)
    {
        int oldWidth = width, oldHeight = height;
        byte[] resultTiles = new byte[w * h],
        resultEnts = new byte[w * h];

        int xLimit = (w >= oldWidth) ? oldWidth : w,
        yLimit = (h >= oldHeight) ? oldHeight : h;
        for(int y = 0; y < yLimit; y++)
        {
            for(int x = 0; x < xLimit; x++)
            {
                resultTiles[x + y * w] = tiles[x + y * oldWidth];
                resultEnts[x + y * w] = entities[x + y * oldWidth];
            }
        }
    }

    private void initPlayer()
    {
        for(int y = 0; y < height; y++)
        {
            for(int x = 0; x < width; x++)
            {
                byte entityID = entities[x + y * width];

                switch(entityID)
                {
                    case 1:
                    this.player = (Player)Entities.get(1);
                    this.player.setTilePosition(x, y);
                    this.player.init(this);
                    break;

                    default:
                    break;
                }
            }
        }
    }

    //Removes an existing player from the map.
    private void removePlayer()
    {
        for(int y = 0; y < height; y++)
        {
            for(int x = 0; x < width; x++)
            {
                byte entityID = entities[x + y * width];

                if(entityID == 1)
                {
                    entities[x + y * width] = 0;
                    //return;
                }
            }
        }
    }

    
    /**Update function.*/
    public void update()
    {
        //Update Player and any moving Entities.
        player.update(1.0f);

        //Update camera.
        camera.update();

        //Update background tile animations.
        for(int i = 0; i < num_backgroundTiles; i++)
        {background_TileAnims[i].update(1.0f);}

        //Update playground tile animations.
        for(int i = 0; i < num_playgroundTiles; i++)
        {playground_TileAnims[i].update(1.0f);}
    }

    //This just updates the animations.
    public void updateAnimations()
    {
        //Update camera.
        camera.update();

        //Update background tile animations.
        for(int i = 0; i < num_backgroundTiles; i++)
        {background_TileAnims[i].update(1.0f);}

        //Update playground tile animations.
        for(int i = 0; i < num_playgroundTiles; i++)
        {playground_TileAnims[i].update(1.0f);}
    }

    /**Render function.*/
    public void render(Screen screen)
    {
        //
        //Determine what tiles the camera can currently see.
        //

        //Get Camera Position
        int cameraX = (int)camera.getX(), cameraY = (int)camera.getY();

        //Set Screen offset relative to camera position.
        //screen.setCameraOffsets(camera.getScaledX(), camera.getScaledY());
        screen.setCameraOffsets(cameraX, cameraY);

        //Get Camera Zoom.
        float scale = camera.getScale();

        //Calculate what needs to be rendered horizontally.
        int x0 = cameraX >> Level.TILE_BITS;
        int x1 = (int)((cameraX + (screen.getWidth() / scale)) + Level.TILE_SIZE) >> Level.TILE_BITS;
        if(x0 < 0){x0 = 0;} else if(x0 >= width){x0 = width;}
        if(x1 < 0){x1 = 0;} else if(x1 >= width){x1 = width;}

        //Calculate what needs to be rendered vertically.
        int y0 = cameraY >> Level.TILE_BITS;
        int y1 = (int)((cameraY + (screen.getHeight() / scale)) + Level.TILE_SIZE) >> Level.TILE_BITS;
        if(y0 < 0){y0 = 0;} else if(y0 >= height){y0 = height;}
        if(y1 < 0){y1 = 0;} else if(y1 >= height){y1 = height;}


        //
        //Iterate through all visible background tiles and render them.
        //Left to Right, Bottom to Top.
        //
        for(int y = y1-1; y >= y0; y--)
        {
            for(int x = x0; x < x1; x++)
            {
                //Get Tile ID.
                byte tileID = getTileID_Unsafe(x, y);

                //Get background tile ID.
                int b = (tileID & 0x0F) - 1;

                if(b >= 0)
                {
                    //Get and render Tile Animation.
                    Tile bt = background_TileAnims[b];
                    bt.render(screen, x << TILE_BITS, y << TILE_BITS, scale);
                }
            }
        }

        //
        //Iterate through all playground tiles and render them.
        //Left to Right, Bottom to Top.
        //
        for(int y = y1-1; y >= y0; y--)
        {
            for(int x = x0; x < x1; x++)
            {
                //Get Entity ID.
                int entityID = getEntityID_Unsafe(x, y),
                entityBelow = getEntityID(x, y+1);

                //Player's block check.
                if(entityBelow == 1){player.renderBlock(screen, scale);}
                else
                {
                    //Tile render
                    int tx = x << TILE_BITS, ty = y << TILE_BITS;

                    if(entityID != 0)
                    {
                        //Player case.
                        if(entityID == 1){player.render(screen, tx, ty, scale);}
                        else if(entityID == 8){Entities.get(8).render(screen, tx, ty, scale);}
                        continue;
                    }

                    //Get Tile ID.
                    byte tileID = getTileID_Unsafe(x, y);

                    //Get playground tile IDs.
                    int p = (((tileID & 0xF0) >> 4) & 0xFF) - 1;

                    if(p >= 0)
                    {
                        //Get and render Tile Animation.
                        Tile pt = playground_TileAnims[p];
                        pt.render(screen, tx, ty, scale);
                    }
                }
            }
        }
    }

    /**Gets the Tile ID at the given Tile point without doing an out-of-bounds check.*/
    public byte getTileID_Unsafe(int x, int y){return tiles[x + y * width];}

    /**Gets the Tile ID at the given Tile point.*/
    public byte getTileID(int x, int y)
    {
        if(x < 0 || x >= width || y < 0 || y >= height)
        {return 0;}

        return tiles[x + y * width];
    }


    /**Gets the Tile ID at the given Tile point.*/
    public boolean isSolid(int x, int y)
    {
        if(x < 0 || x >= width || y < 0 || y >= height)
        {return true;}

        return (((tiles[x + y * width] & 0xF0) >> 4) & 0xFF) > 0
        || Entities.get(entities[x + y * width]).isSolid();
    }


    /**Gets the Entity ID at the given Tile point without doing an out-of-bounds check.*/
    public byte getEntityID_Unsafe(int x, int y){return entities[x + y * width];}

    /**Gets the Entity ID at the given Tile point.*/
    public byte getEntityID(int x, int y)
    {
        if(x < 0 || x >= width || y < 0 || y >= height)
        {return 0;}

        return entities[x + y * width];
    }

    /**Sets the Entity at the given Tile point.*/
    public void setEntityID(int x, int y, byte ID)
    {
        if(x < 0 || x >= width || y < 0 || y >= height)
        {return;}

        entities[x + y * width] = ID;
    }


    /**Gets the Entity at the given Tile point.*/
    public Entity getEntity(int x, int y)
    {
        if(x < 0 || x >= width || y < 0 || y >= height)
        {return Entities.get(0);}

        return Entities.get(entities[x + y * width]);
    }


    /**Gets the Entity ID at the given Tile point.*/
    public void setPlayerPosition(int oldX, int oldY, int x, int y)
    {
        if(!(oldX < 0 || oldX >= width || oldY < 0 || oldY >= height))
        {entities[oldX + oldY * width] = 0;}

        if(!(x < 0 || x >= width || y < 0 || y >= height))
        {entities[x + y * width] = 1;}
    }
}
