package UnBlocked.Graphics;
/**
 * This is a version of the base Screen class that utilizes OpenGL 3.3 to render the game.
 * 
 * Author: Luke Sullivan
 * Last Edit: 9/24/2022
 */
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

//import static org.lwjgl.system.MemoryUtil.NULL;
import static org.lwjgl.opengl.GL33.*;
import static org.lwjgl.glfw.GLFW.*;

import org.joml.Vector4f;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;

import UnBlocked.Game;
//import JettersR.Entities.Components.Lights.AreaLight;
//import JettersR.Entities.Components.Lights.DirectionalLight;
//import JettersR.Entities.Components.Lights.Light;
//import JettersR.Tiles.Graphics.TileSprite;
//import JettersR.Util.Shapes.Shapes3D.Shape3D;
//import JettersR.Util.Shapes.Shapes3D.Misc.AAB_RoundedBox;

public class OpenGLScreen extends Screen
{
    public static final String shadersPath = "assets/Shaders/OpenGL3_3/";

    /**
     * Shader Program class. Compiles given Vertex and Fragment Shaders into
     * a Shader Program.
     */
    private class ShaderProgram
    {
        //ID
        protected final int ID;

        //Variable location for the view matrix.
        protected int uView = -4;

        /**Constructor.*/
        public ShaderProgram(String vertexPath, String fragmentPath)
        {
            ID = linkShaders
            (
                loadShader(OpenGLScreen.shadersPath + vertexPath + ".glsl", GL_VERTEX_SHADER),
                loadShader(OpenGLScreen.shadersPath + fragmentPath + ".glsl", GL_FRAGMENT_SHADER)
            );

            this.uView = glGetUniformLocation(ID, "uView");
            //System.out.println(uView);
        }

        /**Loads and compiles shaders*/
        private int loadShader(String path, int shaderType)
        {
            String code = "";
            try
            {
                File file = new File(path);
                Scanner scanner = new Scanner(file);

                while(scanner.hasNextLine())
                {code = code + scanner.nextLine() + "\n";}
                scanner.close();

                //System.out.println(code);
            }
            catch(Exception e){e.printStackTrace();}

            //Create a new shader of the given shaderType
            int shader = glCreateShader(shaderType);
            glShaderSource(shader, code);

            //Compile Shader
            glCompileShader(shader);

            //Check if compile succeded
            int[] success = new int[1];
            String infoLog;
            glGetShaderiv(shader, GL_COMPILE_STATUS, success);
            if(success[0] < 1)
            {
                infoLog = glGetShaderInfoLog(shader, 512);
                throw new RuntimeException("ERROR: Shader \"" + path + "\" Compilation failed\n" + infoLog);
            }

            //Return the Shader address
            return shader;
        }

        /**Links given shaders to a shader program*/
        private int linkShaders(int... shaders)
        {
            //Create Shader program
            int shaderProgram = glCreateProgram();

            //Attach shaders to program
            for(int i = 0; i < shaders.length; i++)
            {glAttachShader(shaderProgram, shaders[i]);}

            //Link the program
            glLinkProgram(shaderProgram);

            //Check for Linking Errors
            int success = glGetProgrami(shaderProgram, GL_LINK_STATUS);
            if(success == GL_FALSE)
            {
                int len = glGetProgrami(shaderProgram, GL_INFO_LOG_LENGTH);
                throw new RuntimeException
                (
                    "ERROR: Linking of Shaders failed.\n" +
                    glGetProgramInfoLog(shaderProgram, len)
                );
            }

            //Delete shaders from CPU. We're done with them here.
            for(int i = 0; i < shaders.length; i++)
            {glDeleteShader(shaders[i]);}

            return shaderProgram;
        }

        //ID Getter.
        public int getID(){return ID;}

        //Matrix Location Getters.
        //public int get_uProj(){return uProj;}
        public int get_uView(){return uView;}
    
        public void use(){glUseProgram(this.ID);}
        public void detach(){glUseProgram(0);}
        //public void delete(){glDeleteProgram(this.ID);}
    }

    //ShaderPrograms.
    private ShaderProgram frameBuffer_ShaderProgram,
    basic_ShaderProgram,
    //
    line_ShaderProgram,
    circle_ShaderProgram;

    //Texture Slots.
    private final int[] TEX_SLOTS = {0, 1, 2, 3, 4, 5, 6, 7};

    /**
     * This is a batch that takes a bunch of verticies and renders them all in one draw call.
     * This is much faster than drawing one sprite per draw call as it saves on draw calls.
     * Up to 8 textures can be used per draw call and one shader can be used per draw call.
     */
    private abstract class RenderBatch
    {
        //Vertices Array.
        protected float[] vertices;
        //protected int maxVertexCount;

        //IDs
        protected int vertexArrayObject_ID, vertexBufferObject_ID,
        elementBufferObject_ID;

        //Shader.
        //protected ShaderProgram shaderProgram;

        //Vertex stuff.
        protected int offset = 0; 
        protected boolean hasRoom;

        /**Constructor.*/
        public RenderBatch(ShaderProgram shaderProgram, int maxVertexCount, int vertexSize)
        {   
            //this.shaderProgram = shaderProgram;
            //this.maxVertexCount = maxVertexCount;
            vertices = new float[maxVertexCount * vertexSize];

            this.hasRoom = true;
        }

        public abstract void start();

        public final boolean hasRoom(){return hasRoom;}

        public abstract void render();
    }


    /**
     * RenderBatch specifically for Sprites.
     */
    private abstract class SpriteBatch extends RenderBatch
    {
        protected int numSprites = 0;
        protected final int maxSpriteCount;
        protected final List<SpriteSheet> sheets;

        /**Constructor.*/
        public SpriteBatch(ShaderProgram shaderProgram, int maxSpriteCount, int vertexSize)
        {
            super(shaderProgram, maxSpriteCount * 4, vertexSize);
            this.maxSpriteCount = maxSpriteCount;

            this.numSprites = 0;
            this.hasRoom = true;
            this.sheets = new ArrayList<SpriteSheet>();
        }

        /**Sets up OpenGL objects needed for rendering.*/
        public void start()
        {
            //Generate and bind a Vertex Array Object.
            vertexArrayObject_ID = glGenVertexArrays();
            glBindVertexArray(vertexArrayObject_ID);

            //Generate and bind a Vertex Buffer Object.
            vertexBufferObject_ID = glGenBuffers();
            glBindBuffer(GL_ARRAY_BUFFER, vertexBufferObject_ID);
            glBufferData(GL_ARRAY_BUFFER, vertices.length * Float.BYTES, GL_DYNAMIC_DRAW);

            //Create and upload Index Buffer to GPU.
            elementBufferObject_ID = glGenBuffers();
            int[] indices = new int[6 * maxSpriteCount];
            for(int index = 0; index < maxSpriteCount; index++)
            {
                int offsetArrayIndex = 6 * index;
                int offset = 4 * index;

                //3, 2, 0, 0, 2, 1      7, 6, 4, 4, 6, 5
                //Triangle 1
                indices[offsetArrayIndex]   = offset + 0;
                indices[offsetArrayIndex+1] = offset + 1;
                indices[offsetArrayIndex+2] = offset + 2;

                //Triangle 2
                indices[offsetArrayIndex+3] = offset + 2;
                indices[offsetArrayIndex+4] = offset + 3;
                indices[offsetArrayIndex+5] = offset + 0;
            }
            glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, elementBufferObject_ID);
            glBufferData(GL_ELEMENT_ARRAY_BUFFER, indices, GL_STATIC_DRAW);
        }

        /**Adds a Texture to this RenderBatch.*/
        public void addSheet(SpriteSheet s){sheets.add(s);}
        public boolean hasSheet(SpriteSheet s){return sheets.contains(s);}
        public boolean hasSheetRoom(){return this.sheets.size() < 8;}

        public int getTexSlot(int sheetID)
        {
            for(int i = 0; i < sheets.size(); i++)
            {
                if(sheetID == sheets.get(i).getID())
                {return i + 1;}
            }
            return 0;

            //return sheetID;
        }

        //private void printVertex(int v)
        //{
            //int offset = v * Basic_RenderBatch.VERTEX_SIZE;

            //System.out.println
            //(
                //vertices[offset] + " " + vertices[offset + 1] + " " + vertices[2] + " " +
                //vertices[offset + 3] + " " + vertices[offset + 4] + " " + vertices[offset + 5] + " " + vertices[offset + 6] + " " +
                //vertices[offset + 7] + " " + vertices[offset + 8] + " " +
                //vertices[offset + 9]
            //);
        //}

        /**Sends all stored vertex data in this batch to the GPU for rendering.*/
        public final void render()
        {
            //System.out.println(numSprites);

            //Use Vertex Buffer Object to upload vertices to Shader.
            glBindBuffer(GL_ARRAY_BUFFER, vertexBufferObject_ID);
            glBufferSubData(GL_ARRAY_BUFFER, 0, vertices);

            //Bind Textures.
            for(int i = 0; i < sheets.size(); i++)
            {
                glActiveTexture(GL_TEXTURE0 + i + 1);
                glBindTexture(GL_TEXTURE_2D, sheets.get(i).getID());
            }

            //Bind Vertex Array Object.
            glBindVertexArray(vertexArrayObject_ID);

            //Use the uploaded vertcies to render.
            glDrawElements(GL_TRIANGLES, this.numSprites * 6, GL_UNSIGNED_INT, 0);
            this.numSprites = 0;
            this.offset = 0;
            this.hasRoom = true;

            //Remove SpriteSheets.
            for(int i = sheets.size()-1; i >= 0; i--)
            {sheets.remove(i);}
        }
    }


    /**
     * Sprite Batch for basic sprites.
     */
    private class Basic_SpriteBatch extends SpriteBatch
    {
        private static final int
        POSITION_SIZE = 3,
        COLOR_SIZE = 4,
        TEX_COORDS_SIZE = 2,
        TEX_ID_SIZE = 1,
        //
        POSITION_OFFSET = 0,
        COLOR_OFFSET = POSITION_OFFSET + POSITION_SIZE * Float.BYTES,
        TEX_COORDS_OFFSET = COLOR_OFFSET + COLOR_SIZE * Float.BYTES,
        TEX_ID_OFFSET = TEX_COORDS_OFFSET + TEX_COORDS_SIZE * Float.BYTES,
        //
        VERTEX_SIZE = 10,
        VERTEX_SIZE_BYTES = VERTEX_SIZE * Float.BYTES;

        /**Constructor.*/
        public Basic_SpriteBatch(int maxSpriteCount)
        {
            super(basic_ShaderProgram, maxSpriteCount, VERTEX_SIZE);
        }

        public void start()
        {
            super.start();

            //Create and enable vertex attribute pointers.
            glVertexAttribPointer(0, POSITION_SIZE, GL_FLOAT, false, VERTEX_SIZE_BYTES, POSITION_OFFSET);
            glEnableVertexAttribArray(0);

            glVertexAttribPointer(1, COLOR_SIZE, GL_FLOAT, false, VERTEX_SIZE_BYTES, COLOR_OFFSET);
            glEnableVertexAttribArray(1);

            glVertexAttribPointer(2, TEX_COORDS_SIZE, GL_FLOAT, false, VERTEX_SIZE_BYTES, TEX_COORDS_OFFSET);
            glEnableVertexAttribArray(2);

            glVertexAttribPointer(3, TEX_ID_SIZE, GL_FLOAT, false, VERTEX_SIZE_BYTES, TEX_ID_OFFSET);
            glEnableVertexAttribArray(3);
        }

        public void addVertices(float position_x, float position_y, float position_z, Vector4f color, float texX, float texY, int sheetID)
        {
            //Load Position
            vertices[offset] = position_x;
            vertices[offset+1] = position_y;
            vertices[offset+2] = position_z;

            //Load Color
            vertices[offset+3] = color.x;
            vertices[offset+4] = color.y;
            vertices[offset+5] = color.z;
            vertices[offset+6] = color.w;

            //Load Texture Coordinates
            vertices[offset+7] = texX;
            vertices[offset+8] = texY;

            //Load Texture ID
            vertices[offset+9] = sheetID;

            //Increment offset;
            offset += VERTEX_SIZE;

            //Increment Sprite count if needed.
            if(offset % (VERTEX_SIZE * 4) == 0)
            {
                numSprites++;
                if(numSprites >= this.maxSpriteCount){this.hasRoom = false;}
            }
        }
    }


    /**
     * Render Batch meant for lines.
     */
    private class Line_RenderBatch extends RenderBatch
    {
        private static final int
        POSITION_SIZE = 3,
        COLOR_SIZE = 4,
        //
        POSITION_OFFSET = 0,
        COLOR_OFFSET = POSITION_OFFSET + POSITION_SIZE * Float.BYTES,
        //
        VERTEX_SIZE = 7,
        VERTEX_SIZE_BYTES = VERTEX_SIZE * Float.BYTES;

        protected int numLines = 0;
        protected final int maxLineCount;

        /**Constructor.*/
        public Line_RenderBatch(int maxLineCount)
        {
            super(line_ShaderProgram, maxLineCount * 2, VERTEX_SIZE);
            this.maxLineCount = maxLineCount;
        }

        @Override
        public void start()
        {
            //Generate and bind a Vertex Array Object.
            vertexArrayObject_ID = glGenVertexArrays();
            glBindVertexArray(vertexArrayObject_ID);

            //Generate and bind a Vertex Buffer Object.
            vertexBufferObject_ID = glGenBuffers();
            glBindBuffer(GL_ARRAY_BUFFER, vertexBufferObject_ID);
            glBufferData(GL_ARRAY_BUFFER, vertices.length * Float.BYTES, GL_DYNAMIC_DRAW);


            //Create and enable vertex attribute pointers.
            glVertexAttribPointer(0, POSITION_SIZE, GL_FLOAT, false, VERTEX_SIZE_BYTES, POSITION_OFFSET);
            glEnableVertexAttribArray(0);

            glVertexAttribPointer(1, COLOR_SIZE, GL_FLOAT, false, VERTEX_SIZE_BYTES, COLOR_OFFSET);
            glEnableVertexAttribArray(1);
        }

        public void addVertices(float position_x, float position_y, float position_z, Vector4f color)
        {
            //Load Position.
            vertices[offset] = position_x;
            vertices[offset+1] = position_y;
            vertices[offset+2] = position_z;

            //Load Color.
            vertices[offset+3] = color.x;
            vertices[offset+4] = color.y;
            vertices[offset+5] = color.z;
            vertices[offset+6] = color.w;

            //Increment offset.
            offset += VERTEX_SIZE;

            //Increment Sprite count if needed.
            if(offset % (VERTEX_SIZE * 2) == 0)
            {
                numLines++;
                if(numLines >= this.maxLineCount){this.hasRoom = false;}
            }
        }

        @Override
        /**Renders every line in this batch.*/
        public final void render()
        {
            //System.out.println("Rendering " + numLines);

            //Use Vertex Buffer Object to upload vertices to Shader.
            glBindBuffer(GL_ARRAY_BUFFER, vertexBufferObject_ID);
            glBufferSubData(GL_ARRAY_BUFFER, 0, vertices);

            //Bind Vertex Array Object.
            glBindVertexArray(vertexArrayObject_ID);

            //Use the uploaded vertcies to render.
            glDrawArrays(GL_LINES, 0, this.numLines * 2);
            this.numLines = 0;
            this.offset = 0;
            this.hasRoom = true;
        }
    }


    /**
     * Render Batch meant for circles.
     */
    private class Circle_RenderBatch extends RenderBatch
    {
        private static final int
        POSITION_SIZE = 3,
        COLOR_SIZE = 4,
        QUAD_COORDS_SIZE = 2,
        THICKNESS_SIZE = 3,
        //
        POSITION_OFFSET = 0,
        COLOR_OFFSET = POSITION_OFFSET + POSITION_SIZE * Float.BYTES,
        QUAD_COORDS_OFFSET = COLOR_OFFSET + COLOR_SIZE * Float.BYTES,
        THICKNESS_OFFSET = QUAD_COORDS_OFFSET + QUAD_COORDS_SIZE * Float.BYTES,
        //
        VERTEX_SIZE = 10,
        VERTEX_SIZE_BYTES = VERTEX_SIZE * Float.BYTES;

        protected int numCircles = 0;
        protected final int maxCircleCount;

        public Circle_RenderBatch(int maxCircleCount)
        {
            super(circle_ShaderProgram, maxCircleCount * 4, VERTEX_SIZE);
            this.maxCircleCount = maxCircleCount;
        }

        @Override
        public void start()
        {
            //Generate and bind a Vertex Array Object.
            vertexArrayObject_ID = glGenVertexArrays();
            glBindVertexArray(vertexArrayObject_ID);

            //Generate and bind a Vertex Buffer Object.
            vertexBufferObject_ID = glGenBuffers();
            glBindBuffer(GL_ARRAY_BUFFER, vertexBufferObject_ID);
            glBufferData(GL_ARRAY_BUFFER, vertices.length * Float.BYTES, GL_DYNAMIC_DRAW);

            //Create and upload Index Buffer to GPU.
            elementBufferObject_ID = glGenBuffers();
            int[] indices = new int[6 * maxCircleCount];
            for(int index = 0; index < maxCircleCount; index++)
            {
                int offsetArrayIndex = 6 * index;
                int offset = 4 * index;

                //3, 2, 0, 0, 2, 1      7, 6, 4, 4, 6, 5
                //Triangle 1
                indices[offsetArrayIndex]   = offset + 0;
                indices[offsetArrayIndex+1] = offset + 1;
                indices[offsetArrayIndex+2] = offset + 2;

                //Triangle 2
                indices[offsetArrayIndex+3] = offset + 2;
                indices[offsetArrayIndex+4] = offset + 3;
                indices[offsetArrayIndex+5] = offset + 0;
            }
            glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, elementBufferObject_ID);
            glBufferData(GL_ELEMENT_ARRAY_BUFFER, indices, GL_STATIC_DRAW);


            //Create and enable vertex attribute pointers.
            glVertexAttribPointer(0, POSITION_SIZE, GL_FLOAT, false, VERTEX_SIZE_BYTES, POSITION_OFFSET);
            glEnableVertexAttribArray(0);

            glVertexAttribPointer(1, COLOR_SIZE, GL_FLOAT, false, VERTEX_SIZE_BYTES, COLOR_OFFSET);
            glEnableVertexAttribArray(1);

            glVertexAttribPointer(2, QUAD_COORDS_SIZE, GL_FLOAT, false, VERTEX_SIZE_BYTES, QUAD_COORDS_OFFSET);
            glEnableVertexAttribArray(2);

            glVertexAttribPointer(3, THICKNESS_SIZE, GL_FLOAT, false, VERTEX_SIZE_BYTES, THICKNESS_OFFSET);
            glEnableVertexAttribArray(3);
        }

        public void addVertices(float position_x, float position_y, float position_z, Vector4f color, float quadX, float quadY, float thickness)
        {
            //Load Position.
            vertices[offset] = position_x;
            vertices[offset+1] = position_y;
            vertices[offset+2] = position_z;

            //Load Color.
            vertices[offset+3] = color.x;
            vertices[offset+4] = color.y;
            vertices[offset+5] = color.z;
            vertices[offset+6] = color.w;

            //Load Quad Coordinates.
            vertices[offset+7] = quadX;
            vertices[offset+8] = quadY;

            //Load Quad size and Circle thickness.
            vertices[offset+9] = thickness;

            //Increment offset.
            offset += VERTEX_SIZE;

            //Increment Sprite count if needed.
            if(offset % (VERTEX_SIZE * 4) == 0)
            {
                numCircles++;
                if(numCircles >= this.maxCircleCount){this.hasRoom = false;}
            }
        }

        @Override
        public final void render()
        {
            //Use Vertex Buffer Object to upload vertices to Shader.
            glBindBuffer(GL_ARRAY_BUFFER, vertexBufferObject_ID);
            glBufferSubData(GL_ARRAY_BUFFER, 0, vertices);

            //Bind Vertex Array Object.
            glBindVertexArray(vertexArrayObject_ID);

            //Use the uploaded vertcies to render.
            glDrawElements(GL_TRIANGLES, this.numCircles * 6, GL_UNSIGNED_INT, 0);
            this.numCircles = 0;
            this.offset = 0;
            this.hasRoom = true;
        }
    }
    

    //RenderBatches.
    private final int BATCH_MAX_SPRITE_COUNT = 256;
    private List<Basic_SpriteBatch> basic_Batches, basic2D_Batches;
    //
    private List<Line_RenderBatch> line_Batches, line2D_Batches;
    private List<Circle_RenderBatch> circle_Batches, circle2D_Batches;

    //Light Data.
    //private float[] lightData;
    //private int[][] cellData = new int[2][];
    //private int numLights;

    //Uniform Block Addresses.
    //private int lightsBlock_UBO, cellsBlock0_UBO, cellsBlock1_UBO;

    //FrameBuffer and RenderBuffer Addresses.
    private int output_FrameBuffer_Object = -1,
    output_FrameBuffer_Texture = -1,// shadow_FrameBuffer_Texture = -1,
    output_RenderBuffer_Object = -1;

    //Matricies.
    private Matrix4f projectionMatrix = new Matrix4f(),
    viewMatrix = new Matrix4f();
    protected float[] matrixArray = new float[16];

    /**
     * Constructor.
     */
    public OpenGLScreen(int width, int height)
    {
        //Set Dimensions and Viewport.
        setDimensions(width, height, false);
        setViewportDimensions(0, 0, width, height);
    }

    public void setDimensions(int w, int h, boolean API_Initialized)
    {
        //Width and Height.
        WIDTH = w; HEIGHT = h;

        //Base Projection Matrix.
        projectionMatrix.identity();
        projectionMatrix.ortho(0, w,
        h, 0,
        -256, 256);

        //Custom FrameBuffer, to avoid texture offset problems and improve performance at higher resolutions.
        {
            //If one hasn't already been made, make one. Bind it.
            if(output_FrameBuffer_Object == -1){output_FrameBuffer_Object = glGenFramebuffers();}
            glBindFramebuffer(GL_FRAMEBUFFER, output_FrameBuffer_Object);


            //
            //Same with Output FrameBuffer Texture.
            //
            if(output_FrameBuffer_Texture == -1){output_FrameBuffer_Texture = glGenTextures();}
            glBindTexture(GL_TEXTURE_2D, output_FrameBuffer_Texture);

            //(Re)Create texture.
            glTexImage2D(GL_TEXTURE_2D, 0, GL_RGB, WIDTH, HEIGHT, 0, GL_RGB, GL_UNSIGNED_BYTE, (int[])null);

            //Set texture parameters.
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);

            //Set FrameBuffer Texture to this frameBuffer.
            glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_TEXTURE_2D, output_FrameBuffer_Texture, 0);


            //
            //Same with Shadow FrameBuffer Texture.
            //
            //if(shadow_FrameBuffer_Texture == -1){shadow_FrameBuffer_Texture = glGenTextures();}
            //glBindTexture(GL_TEXTURE_2D, shadow_FrameBuffer_Texture);

            //(Re)Create texture.
            //glTexImage2D(GL_TEXTURE_2D, 0, GL_RG16_SNORM, WIDTH, HEIGHT, 0, GL_RGBA, GL_INT, (int[])null);

            //Set texture parameters.
            //glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
            //glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
            //glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
            //glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);

            //Unbind Texture for now.
            glBindTexture(GL_TEXTURE_2D, 0);


            //TODO: Use stencil buffer to use Crop Regions.

            //RenderBuffer, used as the Depth and Stencil buffers.
            if(output_RenderBuffer_Object == -1){output_RenderBuffer_Object = glGenRenderbuffers();}
            glBindRenderbuffer(GL_RENDERBUFFER, output_RenderBuffer_Object);

            //What is the RenderBuffer used for?
            glRenderbufferStorage(GL_RENDERBUFFER, GL_DEPTH_COMPONENT, w, h);
            glFramebufferRenderbuffer(GL_FRAMEBUFFER, GL_DEPTH_ATTACHMENT, GL_RENDERBUFFER, output_RenderBuffer_Object);
            //glRenderbufferStorage(GL_RENDERBUFFER, GL_DEPTH24_STENCIL8, w, h);
            //glFramebufferRenderbuffer(GL_FRAMEBUFFER, GL_DEPTH_STENCIL_ATTACHMENT, GL_RENDERBUFFER, output_RenderBuffer_Object);
            glBindRenderbuffer(GL_RENDERBUFFER, 0);

            //Completion check for debugging purposes.
            if(glCheckFramebufferStatus(GL_FRAMEBUFFER) != GL_FRAMEBUFFER_COMPLETE)
            {System.err.println("FRAMEBUFFER INCOMPLETE");}
        }

        
        //Go back to default framebuffer.
        glBindFramebuffer(GL_FRAMEBUFFER, 0);
    }

    //VAO and VBO addresses for rendering frameBuffer.
    private int frameBuffer_VAO, frameBuffer_VBO;

    //Array for rendering framebuffer.
    private final float[] SCR_VERTS =
    {
        -1.0f, -1.0f, 0.0f, 0.0f,
        1.0f, -1.0f, 1.0f, 0.0f,
        1.0f, 1.0f, 1.0f, 1.0f,
        //
        1.0f, 1.0f, 1.0f, 1.0f,
        -1.0f, 1.0f, 0.0f, 1.0f,
        -1.0f, -1.0f, 0.0f, 0.0f
    };

    /**Initialization functions.*/
    public void init()
    {
        //
        //Compile FrameBuffer Shader.
        //
        frameBuffer_ShaderProgram = new ShaderProgram("FrameBuffer_Vert", "FrameBuffer_Frag");

        //Create a Vertex Array Object for the custom framebuffer texture.
        frameBuffer_VAO = glGenVertexArrays();
        glBindVertexArray(frameBuffer_VAO);

        //Put SCR_VERTS into a Vertex Buffer Object into the VAO.
        frameBuffer_VBO = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, frameBuffer_VBO);
        glBufferData(GL_ARRAY_BUFFER, SCR_VERTS, GL_STATIC_DRAW);

        //Create and enable attribute pointers.
        glVertexAttribPointer(0, 2, GL_FLOAT, false, 4 * Float.BYTES, 0);
        glEnableVertexAttribArray(0);
        //
        glVertexAttribPointer(1, 2, GL_FLOAT, false, 4 * Float.BYTES, 2 * Float.BYTES);
        glEnableVertexAttribArray(1);

        //Unbind VAO.
        glBindVertexArray(0);


        //
        //Compile Basic Shader.
        //
        basic_ShaderProgram = new ShaderProgram("Basic_Vert", "Basic_Frag");
        
        //Upload Texture Slots to Basic Shader.
        basic_ShaderProgram.use();
        int basic_uTex = glGetUniformLocation(basic_ShaderProgram.getID(), "uTextures");
        glUniform1iv(basic_uTex, TEX_SLOTS);

        //Create Basic_SpriteBatch ArrayLists.
        basic_Batches = new ArrayList<Basic_SpriteBatch>();
        basic2D_Batches = new ArrayList<Basic_SpriteBatch>();



        



        //
        //Compile Line Shader.
        //
        line_ShaderProgram = new ShaderProgram("Line_Vert", "Line_Frag");

        //Create Line_RenderBatch ArrayLists.
        line_Batches = new ArrayList<Line_RenderBatch>();
        line2D_Batches = new ArrayList<Line_RenderBatch>();


        //
        //Compile Circle Shader.
        //
        circle_ShaderProgram = new ShaderProgram("Circle_Vert", "Circle_Frag");

        //Create Circle_RenderBatch ArrayLists.
        circle_Batches = new ArrayList<Circle_RenderBatch>();
        circle2D_Batches = new ArrayList<Circle_RenderBatch>();
    }


    //private float[] offsets_Array = new float[3];

    public void render(long windowAddress)
    {
        //Calculate View Matrix.
        projectionMatrix.translate(-xOffset, -yOffset, 0, viewMatrix);
        viewMatrix.get(matrixArray);

        //Use custom FrameBuffer and set viewport to fixed size.
        //glBindFramebuffer(GL_FRAMEBUFFER, output_FrameBuffer_Object);
        glViewport(0, 0, WIDTH, HEIGHT);

        //glEnable(GL_DEPTH_TEST);


        //
        //Use BasicShader and upload offset matrix and every Basic RenderBatch (relative to camera).
        //
        basic_ShaderProgram.use();
        glUniformMatrix4fv(basic_ShaderProgram.get_uView(), false, matrixArray);
        for(Basic_SpriteBatch batch : basic_Batches){batch.render();}


        //
        //Use LineShader and upload every Line RenderBatch (relative to camera).
        //
        line_ShaderProgram.use();
        glDisable(GL_DEPTH_TEST);
        glUniformMatrix4fv(line_ShaderProgram.get_uView(), false, matrixArray);
        for(Line_RenderBatch batch : line_Batches){batch.render();}


        //
        //Use CircleShader and upload every Circle RenderBatch (relative to camera).
        //
        circle_ShaderProgram.use();
        glUniformMatrix4fv(circle_ShaderProgram.get_uView(), false, matrixArray);
        for(Circle_RenderBatch batch : circle_Batches){batch.render();}


        //
        //Use BasicShader and upload base matrix and every Basic RenderBatch (fixed to screen).
        //
        basic_ShaderProgram.use();
        //glDisable(GL_DEPTH_TEST);
        projectionMatrix.get(matrixArray);
        glUniformMatrix4fv(basic_ShaderProgram.get_uView(), false, matrixArray);
        for(Basic_SpriteBatch batch : basic2D_Batches){batch.render();}


        //
        //Use LineShader and upload every Line RenderBatch (fixed to screen)
        //
        line_ShaderProgram.use();
        glUniformMatrix4fv(line_ShaderProgram.get_uView(), false, matrixArray);
        for(Line_RenderBatch batch : line2D_Batches){batch.render();}


        //
        //Use CircleShader and upload every Circle RenderBatch (fixed to screen).
        //
        circle_ShaderProgram.use();
        glUniformMatrix4fv(circle_ShaderProgram.get_uView(), false, matrixArray);
        for(Circle_RenderBatch batch : circle2D_Batches){batch.render();}


        //
        //Use default FrameBuffer and FrameBuffer Shader.
        //
        glBindFramebuffer(GL_FRAMEBUFFER, 0);
        glViewport(VIEWPORT_X, VIEWPORT_Y, VIEWPORT_WIDTH, VIEWPORT_HEIGHT);
        glClear(GL_COLOR_BUFFER_BIT);
        frameBuffer_ShaderProgram.use();

        //Bind the FrameBuffer's Vertex Array Object and Texture.
        glBindVertexArray(frameBuffer_VAO);
        //glDisable(GL_DEPTH_TEST);
        glActiveTexture(GL_TEXTURE0);
        glBindTexture(GL_TEXTURE_2D, output_FrameBuffer_Texture);

        //Render the quad.
        glDrawArrays(GL_TRIANGLES, 0, 6);

        //Display to the screen.
        glfwSwapBuffers(windowAddress);

        //Switch back to custom FrameBuffer.
        glBindFramebuffer(GL_FRAMEBUFFER, output_FrameBuffer_Object);
    }

    @Override
    public void clear()//{}
    {
        //Only clear the custom FrameBuffer.
        //glBindFramebuffer(GL_FRAMEBUFFER, output_FrameBuffer_Object);
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        //glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT | GL_STENCIL_BUFFER_BIT);
    }

    @Override
    /**Resets renderBatches to clean out any unused SpriteSheets.*/
    public void reset_RenderBatches()
    {
        //Basic.
        basic_Batches.clear();
        basic2D_Batches.clear();

        //Line.
        line_Batches.clear();
        line2D_Batches.clear();
    
        //Circle.
        circle_Batches.clear();
        circle2D_Batches.clear();
    }


    /*
     * EVERYTHING below this threshold will be applying Sprite Vertices to the RenderBatches.
     */

    /**Adds a sprite to a Basic_SpriteBatch.*/
    private void add_basic(float position_x0, float position_y0, float position_z0, 
    float position_x1, float position_y1, float position_z1, 
    float position_x2, float position_y2, float position_z2, 
    float position_x3, float position_y3, float position_z3,
    Vector4f color, Sprite sprite, byte flip, int wrapFlags, int wrapX, int wrapY, boolean fixed)
    {
        //Get SpriteSheet.
        SpriteSheet sheet = sprite.getSheet();

        int size = (fixed) ? basic_Batches.size() : basic2D_Batches.size();
        for(int i = 0; i <= size; i++)
        {
            //Create Batch pointer.
            Basic_SpriteBatch batch = null;

            //Happens if every batch is full.
            if(i >= size)
            {
                //Create a new RenderBatch and add the sheet to it. 
                batch = new Basic_SpriteBatch(BATCH_MAX_SPRITE_COUNT);
                batch.start();
                batch.addSheet(sheet);

                //Add this batch to the correct list.
                if(fixed){basic_Batches.add(batch);}
                else{basic2D_Batches.add(batch);}
            }
            //Otherwise, try to add it to the current batch.
            else
            {
                //Get the current batch.
                if(fixed){batch = basic_Batches.get(i);}
                else{batch = basic2D_Batches.get(i);}

                //Does it have room for a quad?
                if(batch.hasRoom())
                {
                    //Does it have this sheet?
                    if(!batch.hasSheet(sheet))
                    {
                        //Does it have room for this sheet? If so, add it. Otherwise, skip to the next one.
                        if(batch.hasSheetRoom()){batch.addSheet(sheet);}
                        else{continue;}
                    }
                }
                else{continue;}
            }

            //Get Texture Slot.
            int texSlot = batch.getTexSlot(sheet.getID());

            //Calculate Texture coordinates.
            float
            x0 = (sprite.getX() + (((wrapFlags & 0b01) == 0b01) ? 0 : wrapX)) / (float)sheet.getWidth(),
            x1 = (sprite.getX() + (((wrapFlags & 0b01) == 0b01) ? wrapX : sprite.getWidth())) / (float)sheet.getWidth(),
            //
            y0 = (sprite.getY() + (((wrapFlags & 0b10) == 0b10) ? 0 : wrapY)) / (float)sheet.getHeight(),
            y1 = (sprite.getY() + (((wrapFlags & 0b10) == 0b10) ? wrapY : sprite.getHeight())) / (float)sheet.getHeight();

            if((flip & Sprite.FLIP_X) == 1)
            {
                float bagHolder = x1;
                x1 = x0;
                x0 = bagHolder;
            }

            if((flip & Sprite.FLIP_Y) == 1)
            {
                float bagHolder = y1;
                y1 = y0;
                y0 = bagHolder;
            }

            //System.out.println("add vertices");

            //Add the full quad.
            batch.addVertices(position_x0, position_y0, position_z0, color, x0, y0, texSlot);
            batch.addVertices(position_x1, position_y1, position_z1, color, x1, y0, texSlot);
            batch.addVertices(position_x2, position_y2, position_z2, color, x1, y1, texSlot);
            batch.addVertices(position_x3, position_y3, position_z3, color, x0, y1, texSlot);
            
            //We're done here.
            break;
        }
    }
    


    /**Adds a sprite to a Basic_SpriteBatch.*/
    private void add_line(float position_x0, float position_y0, float position_z0, 
    float position_x1, float position_y1, float position_z1, 
    Vector4f color, boolean fixed)
    {
        int size = (fixed) ? line_Batches.size() : line2D_Batches.size();
        for(int i = 0; i <= size; i++)
        {
            //Create Batch pointer.
            Line_RenderBatch batch = null;

            //Happens if every batch is full.
            if(i >= size)
            {
                //System.out.println("New line batch.");

                //Create a new RenderBatch and add the sheet to it. 
                batch = new Line_RenderBatch(BATCH_MAX_SPRITE_COUNT);
                batch.start();

                //Add this batch to the correct list.
                if(fixed){line_Batches.add(batch);}
                else{line2D_Batches.add(batch);}
            }
            //Otherwise, try to add it to the current batch.
            else
            {
                //Get the current batch.
                if(fixed){batch = line_Batches.get(i);}
                else{batch = line2D_Batches.get(i);}

                //Does it have room for a line?
                if(!batch.hasRoom()){continue;}
            }

            //Add the full quad.
            batch.addVertices(position_x0, position_y0, position_z0, color);
            batch.addVertices(position_x1, position_y1, position_z1, color);
            
            //We're done here.
            break;
        }
    }


    /**Adds a circle to a Circle_RenderBatch.*/
    private void add_circle(float position_x, float position_y, float position_z, 
    float radius, Vector4f color, float thickness, boolean fixed)
    {
        int size = (fixed) ? circle_Batches.size() : circle2D_Batches.size();
        for(int i = 0; i <= size; i++)
        {
            //Create Batch pointer.
            Circle_RenderBatch batch = null;

            //Happens if every batch is full.
            if(i >= size)
            {
                //Create a new RenderBatch and add the sheet to it. 
                batch = new Circle_RenderBatch(BATCH_MAX_SPRITE_COUNT);
                batch.start();

                //Add this batch to the correct list.
                if(fixed){circle_Batches.add(batch);}
                else{circle2D_Batches.add(batch);}
            }
            //Otherwise, try to add it to the current batch.
            else
            {
                //Get the current batch.
                if(fixed){batch = circle_Batches.get(i);}
                else{batch = circle2D_Batches.get(i);}

                //Does it have room for a quad?
                if(!batch.hasRoom()){continue;}
            }

            float
            x0 = position_x - radius,
            x1 = position_x + radius,
            y0 = position_y - radius,
            y1 = position_y + radius;

            //float t = (thickness) / radius;
            float t = (radius - (thickness)) / radius;

            //Add the full quad.
            batch.addVertices(x0, y0, position_z, color, 0.0f, 0.0f, t);
            batch.addVertices(x1, y0, position_z, color, 1.0f, 0.0f, t);
            batch.addVertices(x1, y1, position_z, color, 1.0f, 1.0f, t);
            batch.addVertices(x0, y1, position_z, color, 0.0f, 1.0f, t);
            
            //We're done here.
            break;
        }
    }


    @Override
    /**Renders a SpriteSheet.*/
    public void renderSheet(int xPos, int yPos, int zPos, SpriteSheet sheet, boolean fixed){}
    public void renderSheet(int xPos, int yPos, int zPos, SpriteSheet sheet, Vector4f blendingColor, boolean fixed){}
    
    
    @Override
    /**Renders a Sprite.*/
    public void renderSprite(int xPos, int yPos, Sprite sprite, byte flip, int wrapX, int wrapY, int cropX0, int cropY0, int cropX1, int cropY1, boolean fixed)
    {renderSprite(xPos, yPos, 0, sprite, flip, wrapX, wrapY, Screen.DEFAULT_BLEND, cropX0, cropY0, cropX1, cropY1, fixed);}
    //
    public void renderSprite(int xPos, int yPos, Sprite sprite, byte flip, int wrapX, int wrapY, Vector4f blendingColor, int cropX0, int cropY0, int cropX1, int cropY1, boolean fixed)
    {renderSprite(xPos, yPos, 0, sprite, flip, wrapX, wrapY, blendingColor, cropX0, cropY0, cropX1, cropY1, fixed);}
    //
    public void renderSprite(int xPos, int yPos, int zPos, Sprite sprite, byte flip, int wrapX, int wrapY, int cropX0, int cropY0, int cropX1, int cropY1, boolean fixed)
    {renderSprite(xPos, yPos, zPos, sprite, flip, wrapX, wrapY, Screen.DEFAULT_BLEND, cropX0, cropY0, cropX1, cropY1, fixed);}
    //
    public void renderSprite(int xPos, int yPos, int zPos, Sprite sprite, byte flip, int wrapX, int wrapY, Vector4f blendingColor, int cropX0, int cropY0, int cropX1, int cropY1, boolean fixed)
    {
        if(sprite.getWidth() == 0 || sprite.getHeight() == 0){return;}

        //Wrap stuff.
        wrapX %= sprite.getWidth();
        wrapY %= sprite.getHeight();

        int wrapFlags = ((wrapY != 0) ? 0b10 : 0) | ((wrapX != 0) ? 0b01 : 0),
        wrapInc = (wrapFlags == 0b10) ? 2 : 1;

        for(int w = 0b00; w <= wrapFlags; w += wrapInc)
        {
            float
            x0 = xPos + (((0b01 & w) == 0b01) ? sprite.getWidth() - wrapX : 0),
            y0 = yPos + (((0b10 & w) == 0b10) ? sprite.getHeight() - wrapY : 0),
            x1 = xPos + (((0b01 & w) == 0b01) ? sprite.getWidth() : sprite.getWidth() - wrapX),
            y1 = yPos + (((0b10 & w) == 0b10) ? sprite.getHeight() : sprite.getHeight() - wrapY);

            //Pass the data to a RenderBatch.
            add_basic
            (
                x0, y0, 0,
                x1, y0, 0,
                x1, y1, 0,
                x0, y1, 0,
                //
                blendingColor, sprite, flip, wrapFlags & w, wrapX, wrapY, fixed
            );
        }
    }



    @Override
    /**Renders a scaled Sprite.*/
    public void renderSprite_Sc(int xPos, int yPos, Sprite sprite, byte flip, int wrapX, int wrapY, int cropX0, int cropY0, int cropX1, int cropY1, float xScale, float yScale, boolean fixed)
    {renderSprite_Sc(xPos, yPos, 0, sprite, flip, wrapX, wrapY, Screen.DEFAULT_BLEND, cropX0, cropY0, cropX1, cropY1, xScale, yScale, fixed);}
    //
    public void renderSprite_Sc(int xPos, int yPos, Sprite sprite, byte flip, int wrapX, int wrapY, Vector4f blendingColor, int cropX0, int cropY0, int cropX1, int cropY1, float xScale, float yScale, boolean fixed)
    {renderSprite_Sc(xPos, yPos, 0, sprite, flip, wrapX, wrapY, blendingColor, cropX0, cropY0, cropX1, cropY1, xScale, yScale, fixed);}
    //
    public void renderSprite_Sc(int xPos, int yPos, int zPos, Sprite sprite, byte flip, int wrapX, int wrapY, int cropX0, int cropY0, int cropX1, int cropY1, float xScale, float yScale, boolean fixed)
    {renderSprite_Sc(xPos, yPos, zPos, sprite, flip, wrapX, wrapY, Screen.DEFAULT_BLEND, cropX0, cropY0, cropX1, cropY1, xScale, yScale, fixed);}
    //
    public void renderSprite_Sc(int xPos, int yPos, int zPos, Sprite sprite, byte flip, int wrapX, int wrapY, Vector4f blendingColor, int cropX0, int cropY0, int cropX1, int cropY1, float xScale, float yScale, boolean fixed)
    {
        if(sprite.getWidth() == 0 || sprite.getHeight() == 0){return;}

        //Dimensions.
        int resultWidth = (int)((sprite.getWidth() * xScale)//),
         + 0.5f),
        resultHeight = (int)((sprite.getHeight() * yScale)//);
         + 0.5f);

        //Wrap stuff.
        wrapX %= sprite.getWidth();
        wrapY %= sprite.getHeight();
        int swx = (int)(wrapX * xScale);
        int swy = (int)(wrapY * yScale);

        int wrapFlags = ((wrapY != 0) ? 0b10 : 0) | ((wrapX != 0) ? 0b01 : 0),
        wrapInc = (wrapFlags == 0b10) ? 2 : 1;

        for(int w = 0b00; w <= wrapFlags; w += wrapInc)
        {
            float
            x0 = xPos + (((0b01 & w) == 0b01) ? resultWidth - swx : 0),
            y0 = yPos + (((0b10 & w) == 0b10) ? resultHeight - swy : 0),
            x1 = xPos + (((0b01 & w) == 0b01) ? resultWidth : resultWidth - swx),
            y1 = yPos + (((0b10 & w) == 0b10) ? resultHeight : resultHeight - swy);

            //Pass the data to a RenderBatch.
            add_basic
            (
                x0, y0, 0,
                x1, y0, 0,
                x1, y1, 0,
                x0, y1, 0,
                //
                blendingColor, sprite, flip, wrapFlags & w, wrapX, wrapY, fixed
            );
        }
    }
 



    @Override
    /**Renders a stretch Sprite.*/
    public void renderSprite_St(int xPos, int yPos, Sprite sprite, byte flip, int wrapX, int wrapY, int cropX0, int cropY0, int cropX1, int cropY1, int resultWidth, int resultHeight, boolean fixed)
    {renderSprite_St(xPos, yPos, 0, sprite, flip, wrapX, wrapY, Screen.DEFAULT_BLEND, cropX0, cropY0, cropX1,  cropY1, resultWidth, resultHeight, fixed);}
    //
    public void renderSprite_St(int xPos, int yPos, Sprite sprite, byte flip, int wrapX, int wrapY, Vector4f blendingColor, int cropX0, int cropY0, int cropX1, int cropY1, int resultWidth, int resultHeight, boolean fixed)
    {renderSprite_St(xPos, yPos, 0, sprite, flip, wrapX, wrapY, blendingColor, cropX0, cropY0, cropX1,  cropY1, resultWidth, resultHeight, fixed);}
    //
    public void renderSprite_St(int xPos, int yPos, int zPos, Sprite sprite, byte flip, int wrapX, int wrapY, int cropX0, int cropY0, int cropX1, int cropY1, int resultWidth, int resultHeight, boolean fixed)
    {renderSprite_St(xPos, yPos, zPos, sprite, flip, wrapX, wrapY, Screen.DEFAULT_BLEND, cropX0, cropY0, cropX1,  cropY1, resultWidth, resultHeight, fixed);}
    //
    public void renderSprite_St(int xPos, int yPos, int zPos, Sprite sprite, byte flip, int wrapX, int wrapY, Vector4f blendingColor, int cropX0, int cropY0, int cropX1, int cropY1, int resultWidth, int resultHeight, boolean fixed)
    {
        //Wrap stuff.
        wrapX %= sprite.getWidth();
        wrapY %= sprite.getHeight();
        int swx = (int)((wrapX / (float)sprite.getWidth()) * resultWidth);
        int swy = (int)((wrapY / (float)sprite.getHeight()) * resultHeight);

        int wrapFlags = ((wrapY != 0) ? 0b10 : 0) | ((wrapX != 0) ? 0b01 : 0),
        wrapInc = (wrapFlags == 0b10) ? 2 : 1;

        for(int w = 0b00; w <= wrapFlags; w += wrapInc)
        {
            float
            x0 = xPos + (((0b01 & w) == 0b01) ? resultWidth - swx : 0),
            y0 = yPos + (((0b10 & w) == 0b10) ? resultHeight - swy : 0),
            x1 = xPos + (((0b01 & w) == 0b01) ? resultWidth : resultWidth - swx),
            y1 = yPos + (((0b10 & w) == 0b10) ? resultHeight : resultHeight - swy);

            //Pass the data to a RenderBatch.
            add_basic
            (
                x0, y0, 0,
                x1, y0, 0,
                x1, y1, 0,
                x0, y1, 0,
                //
                blendingColor, sprite, flip, wrapFlags & w, wrapX, wrapY, fixed
            );
        }
    }

    @Override
    /**Renders a sheared Sprite.*/
    public void renderSprite_Sh(int xPos, int yPos, Sprite sprite, byte flip, int wrapX, int wrapY, int cropX0, int cropY0, int cropX1, int cropY1, float xShear, float yShear, boolean fixed)
    {renderSprite_Sh(xPos, yPos, 0, sprite, flip, wrapX, wrapY, Screen.DEFAULT_BLEND, cropX0, cropX1, cropY0, cropY1, xShear, yShear, 0, 0, fixed);}
    //
    public void renderSprite_Sh(int xPos, int yPos, Sprite sprite, byte flip, int wrapX, int wrapY, Vector4f blendingColor, int cropX0, int cropY0, int cropX1, int cropY1, float xShear, float yShear, boolean fixed)
    {renderSprite_Sh(xPos, yPos, 0, sprite, flip, wrapX, wrapY, blendingColor, cropX0, cropX1, cropY0, cropY1, xShear, yShear, 0, 0, fixed);}
    //
    public void renderSprite_Sh(int xPos, int yPos, int zPos, Sprite sprite, byte flip, int wrapX, int wrapY, int cropX0, int cropY0, int cropX1, int cropY1, float xShear, float yShear, float zxShear, float zyShear, boolean fixed)
    {renderSprite_Sh(xPos, yPos, zPos, sprite, flip, wrapX, wrapY, Screen.DEFAULT_BLEND, cropX0, cropX1, cropY0, cropY1, xShear, yShear, zxShear, zyShear, fixed);}
    //
    public void renderSprite_Sh(int xPos, int yPos, int zPos, Sprite sprite, byte flip, int wrapX, int wrapY, Vector4f blendingColor, int cropX0, int cropY0, int cropX1, int cropY1, float xShear, float yShear, float zxShear, float zyShear, boolean fixed)
    {
        //add_basic
        //(
            //xPos + (0 + (0 * xShear)),
            //yPos + (0 + (0 * yShear)),
            //zPos,
            //
            //xPos + (sprite.getWidth() + (0 * xShear)),
            //yPos + (0 + (sprite.getWidth() * yShear)),
            //zPos,
            //
            //xPos + (sprite.getWidth() + (sprite.getHeight() * xShear)),
            //yPos + (sprite.getHeight() + (sprite.getWidth() * yShear)),
            //zPos,
            //
            //xPos + (0 + (sprite.getHeight() * xShear)),
            //yPos + (sprite.getHeight() + (0 * yShear)),
            //zPos,
            //
            //blendingColor, sprite, 0, 0, 0, fixed
        //);

        if(sprite.getWidth() == 0 || sprite.getHeight() == 0){return;}

        //Wrap stuff.
        wrapX %= sprite.getWidth();
        wrapY %= sprite.getHeight();

        int wx = (sprite.getWidth() - wrapX), wy = (sprite.getHeight() - wrapY);

        int wrapFlags = ((wrapY != 0) ? 0b10 : 0) | ((wrapX != 0) ? 0b01 : 0),
        wrapInc = (wrapFlags == 0b10) ? 2 : 1;

        for(int w = 0b00; w <= wrapFlags; w += wrapInc)
        {
            float
            x0 = xPos + (((0b01 & w) == 0b01) ? (wx + (wy * xShear)) : (0 + (0 * xShear))),
            y0 = yPos + (((0b10 & w) == 0b10) ? (wy + (wx * yShear)) : (0 + (0 * yShear))),
            //
            x1 = xPos + (((0b01 & w) == 0b01) ? (sprite.getWidth() + (wy * xShear)) : (wx + (0 * xShear))),
            y1 = yPos + (((0b10 & w) == 0b10) ? (wy + (sprite.getWidth() * yShear)) : (0 + (wx * yShear))),
            //
            x2 = xPos + (((0b01 & w) == 0b01) ? (sprite.getWidth() + (sprite.getHeight() * xShear)) : (wx + (wy * xShear))),
            y2 = yPos + (((0b10 & w) == 0b10) ? (sprite.getHeight() + (sprite.getWidth() * yShear)) : (wy + (wx * yShear))),
            //
            x3 = xPos + (((0b01 & w) == 0b01) ? (wx + (sprite.getHeight() * xShear)) : (0 + (wy * xShear))),
            y3 = yPos + (((0b10 & w) == 0b10) ? (sprite.getHeight() + (wx * yShear)) : (wy + (0 * yShear)));

            //Pass the data to a RenderBatch.
            add_basic
            (
                x0, y0, 0,
                x1, y1, 0,
                x2, y2, 0,
                x3, y3, 0,
                //
                blendingColor, sprite, flip, wrapFlags & w, wrapX, wrapY, fixed
            );
        }
    }

    @Override
    /**Renders a rotated Sprite.*/
    public void renderSprite_Ro(int xPos, int yPos, Sprite sprite, byte flip, int wrapX, int wrapY, int cropX0, int cropY0, int cropX1, int cropY1, float rads, int originX, int originY, boolean fixed)
    {renderSprite_Ro(xPos, yPos, 0, sprite, flip, wrapX, wrapY, Screen.DEFAULT_BLEND, cropX0, cropY0, cropX1, cropY1, rads, originX, originY, fixed);}
    //
    public void renderSprite_Ro(int xPos, int yPos, Sprite sprite, byte flip, int wrapX, int wrapY, Vector4f blendingColor, int cropX0, int cropY0, int cropX1, int cropY1, float rads, int originX, int originY, boolean fixed)
    {renderSprite_Ro(xPos, yPos, 0, sprite, flip, wrapX, wrapY, blendingColor, cropX0, cropY0, cropX1, cropY1, rads, originX, originY, fixed);}
    //
    public void renderSprite_Ro(int xPos, int yPos, int zPos, Sprite sprite, byte flip, int wrapX, int wrapY, int cropX0, int cropY0, int cropX1, int cropY1, float rads, int originX, int originY, boolean fixed)
    {renderSprite_Ro(xPos, yPos, zPos, sprite, flip, wrapX, wrapY, Screen.DEFAULT_BLEND, cropX0, cropY0, cropX1, cropY1, rads, originX, originY, fixed);}
    //
    public void renderSprite_Ro(int xPos, int yPos, int zPos, Sprite sprite, byte flip, int wrapX, int wrapY, Vector4f blendingColor, int cropX0, int cropY0, int cropX1, int cropY1, float rads, int originX, int originY, boolean fixed)
    {
        //Sprite Dimensions and zero check.
        int sprite_width = sprite.getWidth(), sprite_height = sprite.getHeight();
        if(sprite_width == 0 || sprite_height == 0){return;}

        //Trig...
        float sin = (float)Math.sin(rads), cos = (float)Math.cos(rads);

        //Multiply the origin by sin and cos for the actual result origin.
        float oX = ((originX * cos) + (originY * -sin));
        float oY = ((originX * sin) + (originY * cos));

        //Wrap stuff.
        wrapX %= sprite_width;
        wrapY %= sprite_height;

        int wx = (sprite_width - wrapX), wy = (sprite_height - wrapY);

        int wrapFlags = ((wrapY != 0) ? 0b10 : 0) | ((wrapX != 0) ? 0b01 : 0),
        wrapInc = (wrapFlags == 0b10) ? 2 : 1;

        for(int w = 0b00; w <= wrapFlags; w += wrapInc)
        {
            float
            x0 = xPos + (((0b01 & w) == 0b01) ? ((wx * cos) + (wy * -sin)) : 0) - oX,//((0 * cos) + (0 * -sin)) - oX,
            y0 = yPos + (((0b10 & w) == 0b10) ? ((wx * sin) + (wy * cos)) : 0) - oY,//((0 * sin) + (0 * cos)) - oY,
            //
            x1 = xPos + (((0b01 & w) == 0b01) ? ((sprite_width * cos) + (wy * -sin)) : (wx * cos)) - oX,//((wx * cos) + (0 * -sin))) - oX,
            y1 = yPos + (((0b10 & w) == 0b10) ? ((sprite_width * sin) + (wy * cos)) : (wx * sin)) - oY,//((wx * sin) + (0 * cos))) - oY,
            //
            x2 = xPos + (((0b01 & w) == 0b01) ? ((sprite_width * cos) + (sprite_height * -sin)) : ((wx * cos) + (wy * -sin))) - oX,
            y2 = yPos + (((0b10 & w) == 0b10) ? ((sprite_width * sin) + (sprite_height * cos)) : ((wx * sin) + (wy * cos))) - oY,
            //
            x3 = xPos + (((0b01 & w) == 0b01) ? ((wx * cos) + (sprite_height * -sin)) : (wy * -sin)) - oX,//((0 * cos) + (wy * -sin))) - oX,
            y3 = yPos + (((0b10 & w) == 0b10) ? ((wx * sin) + (sprite_height * cos)) : (wy * cos)) - oY;//((0 * sin) + (wy * cos))) - oY;

            //Pass the data to a RenderBatch.
            add_basic
            (
                x0, y0, 0,
                x1, y1, 0,
                x2, y2, 0,
                x3, y3, 0,
                //
                blendingColor, sprite, flip, wrapFlags & w, wrapX, wrapY, fixed
            );
        }
    }





    @Override
    /**Renders a scaled and rotated Sprite.*/
    public void renderSprite_ScRo(int xPos, int yPos, Sprite sprite, byte flip,
    int wrapX, int wrapY, int cropX0, int cropY0, int cropX1, int cropY1,
    float xScale, float yScale, float rads, int originX, int originY, boolean fixed)
    {
        renderSprite_ScRo(xPos, yPos, 0, sprite, flip,
        wrapX, wrapY, Screen.DEFAULT_BLEND, cropX0, cropY0, cropX1, cropY1,
        xScale, yScale, rads, originX, originY, fixed);
    }

    @Override
    public void renderSprite_ScRo(int xPos, int yPos, Sprite sprite, byte flip,
    int wrapX, int wrapY, Vector4f blendingColor, int cropX0, int cropY0, int cropX1, int cropY1,
    float xScale, float yScale, float rads, int originX, int originY, boolean fixed)
    {
        renderSprite_ScRo(xPos, yPos, 0, sprite, flip,
        wrapX, wrapY, blendingColor, cropX0, cropY0, cropX1, cropY1,
        xScale, yScale, rads, originX, originY, fixed);
    }

    @Override
    public void renderSprite_ScRo(int xPos, int yPos, int zPos, Sprite sprite, byte flip,
    int wrapX, int wrapY, int cropX0, int cropY0, int cropX1, int cropY1,
    float xScale, float yScale, float rads, int originX, int originY, boolean fixed)
    {
        renderSprite_ScRo(xPos, yPos, zPos, sprite, flip,
        wrapX, wrapY, Screen.DEFAULT_BLEND, cropX0, cropY0, cropX1, cropY1,
        xScale, yScale, rads, originX, originY, fixed);
    }

    @Override
    public void renderSprite_ScRo(int xPos, int yPos, int zPos, Sprite sprite, byte flip,
    int wrapX, int wrapY, Vector4f blendingColor, int cropX0, int cropY0, int cropX1, int cropY1,
    float xScale, float yScale, float rads, int originX, int originY, boolean fixed)
    {
        //Zero check and Result Dimensions.
        if(sprite.getWidth() == 0 || sprite.getHeight() == 0 || xScale == 0.0f || yScale == 0.0f){return;}
        float resultWidth = sprite.getWidth() * xScale, resultHeight = sprite.getHeight() * yScale;

        //Trig...
        float sin = (float)Math.sin(rads), cos = (float)Math.cos(rads);

        float osX = (xScale * originX);
        float osY = (yScale * originY);

        //Multiply the origin by sin and cos for the actual result origin.
        float orX = ((osX * cos) + (osY * -sin));
        float orY = ((osX * sin) + (osY * cos));

        //Wrap stuff.
        wrapX %= sprite.getWidth();
        wrapY %= sprite.getHeight();

        float wx = (sprite.getWidth() - wrapX) * xScale, wy = (sprite.getHeight() - wrapY) * yScale;

        int wrapFlags = ((wrapY != 0) ? 0b10 : 0) | ((wrapX != 0) ? 0b01 : 0),
        wrapInc = (wrapFlags == 0b10) ? 2 : 1;

        for(int w = 0b00; w <= wrapFlags; w += wrapInc)
        {
            float
            x0 = xPos + (((0b01 & w) == 0b01) ? ((wx * cos) + (wy * -sin)) : 0) - orX + osX,//((0 * cos) + (0 * -sin)) - oX,
            y0 = yPos + (((0b10 & w) == 0b10) ? ((wx * sin) + (wy * cos)) : 0) - orY + osY,//((0 * sin) + (0 * cos)) - oY,
            //
            x1 = xPos + (((0b01 & w) == 0b01) ? ((resultWidth * cos) + (wy * -sin)) : (wx * cos)) - orX + osX,//((wx * cos) + (0 * -sin))) - oX,
            y1 = yPos + (((0b10 & w) == 0b10) ? ((resultWidth * sin) + (wy * cos)) : (wx * sin)) - orY + osY,//((wx * sin) + (0 * cos))) - oY,
            //
            x2 = xPos + (((0b01 & w) == 0b01) ? ((resultWidth * cos) + (resultHeight * -sin)) : ((wx * cos) + (wy * -sin))) - orX + osX,
            y2 = yPos + (((0b10 & w) == 0b10) ? ((resultWidth * sin) + (resultHeight * cos)) : ((wx * sin) + (wy * cos))) - orY + osY,
            //
            x3 = xPos + (((0b01 & w) == 0b01) ? ((wx * cos) + (resultHeight * -sin)) : (wy * -sin)) - orX + osX,//((0 * cos) + (wy * -sin))) - oX,
            y3 = yPos + (((0b10 & w) == 0b10) ? ((wx * sin) + (resultHeight * cos)) : (wy * cos)) - orY + osY;//((0 * sin) + (wy * cos))) - oY;

            //Pass the data to a RenderBatch.
            add_basic
            (
                x0, y0, 0,
                x1, y1, 0,
                x2, y2, 0,
                x3, y3, 0,
                //
                blendingColor, sprite, flip, wrapFlags & w, wrapX, wrapY, fixed
            );
        }
    }




    @Override
    public void renderTile(int xPos, int yPos, Sprite sprite, byte flip, int wrapX, int wrapY, Vector4f blendingColor, int cropX0, int cropY0, int cropX1, int cropY1,
    float scale, byte shearType, float shear, boolean fixed)
    {
        renderTile(xPos, yPos, 0, sprite, flip, wrapX, wrapY, blendingColor,
        cropX0, cropY0, cropX1, cropY1, scale, shearType, shear, fixed);
    }

    @Override
    public void renderTile(int xPos, int yPos, int zPos, Sprite sprite, byte flip, int wrapX, int wrapY, Vector4f blendingColor, int cropX0, int cropY0, int cropX1, int cropY1,
    float scale, byte shearType, float shear, boolean fixed)
    {
        /*
        if(sprite.getWidth() == 0 || sprite.getHeight() == 0){return;}

        //Dimensions.
        int scaleWidth = (int)((sprite.getWidth() * scale)//),
         + 0.7f),
        scaleHeight = (int)((sprite.getHeight() * scale)//),
         + 0.7f),
        depth = (int)((sprite.getHeight() * 2) * scale);
        //depth = ((scaleHeight) * 2);

        //Wrap stuff.
        wrapX %= sprite.getWidth();
        wrapY %= sprite.getHeight();
        int swx = (int)(wrapX * scale);
        int swy = (int)(wrapY * scale);
        int swz = (int)((wrapY * 2) * scale);

        int wrapFlags = ((wrapY != 0) ? 0b10 : 0) | ((wrapX != 0) ? 0b01 : 0),
        wrapInc = (wrapFlags == 0b10) ? 2 : 1;

        switch(shearType)
        {
            //Just scale, asssume Z-Normal = 0.
            case TileSprite.SHEARTYPE_WALL:
            {
                for(int w = 0b00; w <= wrapFlags; w += wrapInc)
                {
                    add_basic
                    (
                        //UL
                        xPos + (((0b01 & w) == 0b01) ? scaleWidth - swx : 0),
                        yPos,
                        zPos - (((0b10 & w) == 0b10) ? depth - swz : 0),
                        //
                        //UR
                        xPos + (((0b01 & w) == 0b01) ? scaleWidth : scaleWidth - swx),
                        yPos,
                        zPos - (((0b10 & w) == 0b10) ? depth - swz : 0),
                        //
                        //DR
                        xPos + (((0b01 & w) == 0b01) ? scaleWidth : scaleWidth - swx),
                        yPos,
                        zPos - (((0b10 & w) == 0b10) ? depth : depth - swz),
                        //
                        //DL
                        xPos + (((0b01 & w) == 0b01) ? scaleWidth - swx : 0),
                        yPos,
                        zPos - (((0b10 & w) == 0b10) ? depth : depth - swz),
                        //
                        blendingColor, sprite, wrapFlags & w, wrapX, wrapY, fixed
                    );
                }
            }
            break;


            //Y Shear, assume Z-Normal = 0.
            case TileSprite.SHEARTYPE_Y:
            {
                for(int w = 0b00; w <= wrapFlags; w += wrapInc)
                {
                    add_basic
                    (
                        //UL
                        xPos + (((0b01 & w) == 0b01) ? scaleWidth - swx : 0),
                        yPos + (((0b10 & w) == 0b10) ? (scaleWidth * shear) - swy : 0),
                        zPos - (((0b10 & w) == 0b10) ? depth - swz : 0),
                        //
                        //UR
                        xPos + (((0b01 & w) == 0b01) ? scaleWidth : scaleWidth - swx),
                        yPos + (((0b10 & w) == 0b10) ? (scaleWidth * shear) : (scaleWidth * shear) - swy),
                        zPos - (((0b10 & w) == 0b10) ? depth - swz : 0),
                        //
                        //DR
                        xPos + (((0b01 & w) == 0b01) ? scaleWidth : scaleWidth - swx),
                        yPos + (((0b10 & w) == 0b10) ? (scaleWidth * shear) : (scaleWidth * shear) - swy),
                        zPos - (((0b10 & w) == 0b10) ? depth : depth - swz),
                        //
                        //DL
                        xPos + (((0b01 & w) == 0b01) ? scaleWidth - swx : 0),
                        yPos + (((0b10 & w) == 0b10) ? (scaleWidth * shear) - swy : 0),
                        zPos - (((0b10 & w) == 0b10) ? depth : depth - swz),
                        //
                        blendingColor, sprite, wrapFlags & w, wrapX, wrapY, fixed
                    );
                }
            }
            break;


            //ZX Shear, assume Z-Normal = 1.
            case TileSprite.SHEARTYPE_ZX:
            {
                for(int w = 0b00; w <= wrapFlags; w += wrapInc)
                {
                    //Pass the data to a RenderBatch.
                    add_basic
                    (
                        //UL
                        xPos + (((0b01 & w) == 0b01) ? scaleWidth - swx : 0),
                        yPos + (((0b10 & w) == 0b10) ? scaleHeight - swy : 0),
                        zPos,
                        //
                        //UR
                        xPos + (((0b01 & w) == 0b01) ? scaleWidth : scaleWidth - swx),
                        yPos + (((0b10 & w) == 0b10) ? scaleHeight - swy : 0),
                        zPos + (scaleHeight * shear),
                        //
                        //DR
                        xPos + (((0b01 & w) == 0b01) ? scaleWidth : scaleWidth - swx),
                        yPos + (((0b10 & w) == 0b10) ? scaleHeight : scaleHeight - swy),
                        zPos + (scaleHeight * shear),
                        //
                        //DL
                        xPos + (((0b01 & w) == 0b01) ? scaleWidth - swx : 0),
                        yPos + (((0b10 & w) == 0b10) ? scaleHeight : scaleHeight - swy),
                        zPos,
                        //
                        blendingColor, sprite, wrapFlags & w, wrapX, wrapY, fixed
                    );
                }
            }
            break;


            //ZY Shear, assume Z-Normal = 1.
            case TileSprite.SHEARTYPE_ZY:
            {
                for(int w = 0b00; w <= wrapFlags; w += wrapInc)
                {
                    add_basic
                    (
                        //UL
                        xPos + (((0b01 & w) == 0b01) ? scaleWidth - swx : 0),
                        yPos + (((0b10 & w) == 0b10) ? scaleHeight - swy : 0),
                        zPos + (((0b10 & w) == 0b10) ? ((scaleHeight - swy) * shear) : 0),
                        //
                        //UR
                        xPos + (((0b01 & w) == 0b01) ? scaleWidth : scaleWidth - swx),
                        yPos + (((0b10 & w) == 0b10) ? scaleHeight - swy : 0),
                        zPos + (((0b10 & w) == 0b10) ? ((scaleHeight - swy) * shear) : 0),
                        //
                        //DR
                        xPos + (((0b01 & w) == 0b01) ? scaleWidth : scaleWidth - swx),
                        yPos + (((0b10 & w) == 0b10) ? scaleHeight : scaleHeight - swy),
                        zPos + (((0b10 & w) == 0b10) ? (scaleHeight * shear) : ((scaleHeight - swy) * shear)),
                        //
                        //DL
                        xPos + (((0b01 & w) == 0b01) ? scaleWidth - swx : 0),
                        yPos + (((0b10 & w) == 0b10) ? scaleHeight : scaleHeight - swy),
                        zPos + (((0b10 & w) == 0b10) ? (scaleHeight * shear) : ((scaleHeight - swy) * shear)),
                        //
                        blendingColor, sprite, wrapFlags & w, wrapX, wrapY, fixed
                    );
                }
            }
            break;


            //Just scale, assume Z-Normal = 1.
            default:
            {
                for(int w = 0b00; w <= wrapFlags; w += wrapInc)
                {
                    add_basic
                    (
                        //UL
                        xPos + (((0b01 & w) == 0b01) ? scaleWidth - swx : 0),
                        yPos + (((0b10 & w) == 0b10) ? scaleHeight - swy : 0),
                        zPos,
                        //
                        //UR
                        xPos + (((0b01 & w) == 0b01) ? scaleWidth : scaleWidth - swx),
                        yPos + (((0b10 & w) == 0b10) ? scaleHeight - swy : 0),
                        zPos,
                        //
                        //DR
                        xPos + (((0b01 & w) == 0b01) ? scaleWidth : scaleWidth - swx),
                        yPos + (((0b10 & w) == 0b10) ? scaleHeight : scaleHeight - swy),
                        zPos,
                        //
                        //DL
                        xPos + (((0b01 & w) == 0b01) ? scaleWidth - swx : 0),
                        yPos + (((0b10 & w) == 0b10) ? scaleHeight : scaleHeight - swy),
                        zPos,
                        //
                        blendingColor, sprite, wrapFlags & w, wrapX, wrapY, fixed
                    );
                }
            }
            break;
        }
        */
    }

    @Override
    public void renderTile_Ent(int xPos, int yPos, Sprite sprite, byte flip, int wrapX, int wrapY, Vector4f blendingColor, int cropX0, int cropY0, int cropX1, int cropY1,
    float scale, byte shearType, float shear, boolean fixed)
    {
        renderTile(xPos, yPos, 0, sprite, flip, wrapX, wrapY, blendingColor,
        cropX0, cropY0, cropX1, cropY1, scale, shearType, shear, fixed);
    }

    @Override
    public void renderTile_Ent(int xPos, int yPos, int zPos, Sprite sprite, byte flip, int wrapX, int wrapY, Vector4f blendingColor, int cropX0, int cropY0, int cropX1, int cropY1,
    float scale, byte shearType, float shear, boolean fixed)
    {
        renderTile(xPos, yPos, zPos, sprite, flip, wrapX, wrapY, blendingColor,
        cropX0, cropY0, cropX1, cropY1, scale, shearType, shear, fixed);
    }




    /*
    private final Matrix4f TRANSFORM_MAT = new Matrix4f();
    private final Vector4f TRANSFORM_VEC = new Vector4f();

    @Override
    //Renders a sprite using any combination of affine transformations.
    public void renderSprite_Matrix(int xPos, int yPos, Matrix4f matrix, int originX, int originY, Sprite sprite, byte flip, int wrapX, int wrapY, int cropX0, int cropY0, int cropX1, int cropY1, boolean fixed)
    {renderSprite_Matrix(xPos, yPos, matrix, originX, originY, sprite, flip, wrapX, wrapY, Screen.DEFAULT_BLEND, cropX0, cropY0, cropX1, cropY1, fixed);}
    //
    @Override
    public void renderSprite_Matrix(int xPos, int yPos, Matrix4f matrix, int originX, int originY, Sprite sprite, byte flip, int wrapX, int wrapY, Vector4f blendingColor, int cropX0, int cropY0, int cropX1, int cropY1, boolean fixed)
    {
        if(sprite.getWidth() == 0 || sprite.getHeight() == 0){return;}

        matrix.translate(-originX, -originY, 0, TRANSFORM_MAT);

        //Wrap stuff.
        wrapX %= sprite.getWidth();
        wrapY %= sprite.getHeight();

        int wrapFlags = ((wrapY != 0) ? 0b10 : 0) | ((wrapX != 0) ? 0b01 : 0),
        wrapInc = (wrapFlags == 0b10) ? 2 : 1;

        for(int w = 0b00; w <= wrapFlags; w += wrapInc)
        {
            float
            x0 = xPos + (((0b01 & w) == 0b01) ? sprite.getWidth() - wrapX : 0),
            y0 = yPos + (((0b10 & w) == 0b10) ? sprite.getHeight() - wrapY : 0),
            x1 = xPos + (((0b01 & w) == 0b01) ? sprite.getWidth() : sprite.getWidth() - wrapX),
            y1 = yPos + (((0b10 & w) == 0b10) ? sprite.getHeight() : sprite.getHeight() - wrapY);

            //UL
            TRANSFORM_VEC.set(x0, y0, 1, 1);
            TRANSFORM_VEC.mul(matrix);
            float ulX = TRANSFORM_VEC.x, ulY = TRANSFORM_VEC.y;

            //UR
            TRANSFORM_VEC.set(x1, y0, 1, 1);
            TRANSFORM_VEC.mul(matrix);
            float urX = TRANSFORM_VEC.x, urY = TRANSFORM_VEC.y;

            //DR
            TRANSFORM_VEC.set(x1, y1, 1, 1);
            TRANSFORM_VEC.mul(matrix);
            float drX = TRANSFORM_VEC.x, drY = TRANSFORM_VEC.y;

            //DL
            TRANSFORM_VEC.set(x0, y1, 1, 1);
            TRANSFORM_VEC.mul(matrix);
            float dlX = TRANSFORM_VEC.x, dlY = TRANSFORM_VEC.y;

            //Pass the data to a RenderBatch.
            add_basic
            (
                ulX, ulY, 0,
                urX, urY, 0,
                drX, drY, 0,
                dlX, dlY, 0,
                //
                blendingColor, sprite, wrapFlags & w, wrapX, wrapY, fixed
            );
        }
    }
    */


    
    @Override
    /**Renders a sprite using 4 points.*/
    public void renderSprite_Quad(int x0, int y0, int x1, int y1, int x2, int y2, int x3, int y3,
    Sprite sprite, byte flip, int wrapX, int wrapY, int cropX0, int cropY0, int cropX1, int cropY1, boolean fixed)
    {renderSprite_Quad(x0, y0, 0, x1, y1, 0, x2, y2, 0, x3, y3, 0, sprite, flip, wrapX, wrapY, Screen.DEFAULT_BLEND, cropX0, cropY0, cropX1, cropY1, fixed);}
    //
    public void renderSprite_Quad(int x0, int y0, int x1, int y1, int x2, int y2, int x3, int y3,
    Sprite sprite, byte flip, int wrapX, int wrapY, Vector4f blendingColor, int cropX0, int cropY0, int cropX1, int cropY1, boolean fixed)
    {renderSprite_Quad(x0, y0, 0, x1, y1, 0, x2, y2, 0, x3, y3, 0, sprite, flip, wrapX, wrapY, blendingColor, cropX0, cropY0, cropX1, cropY1, fixed);}
    //
    public void renderSprite_Quad(int x0, int y0, int z0, int x1, int y1, int z1, int x2, int y2, int z2, int x3, int y3, int z3,
    Sprite sprite, byte flip, int wrapX, int wrapY, int cropX0, int cropY0, int cropX1, int cropY1, boolean fixed)
    {renderSprite_Quad(x0, y0, z0, x1, y1, z1, x2, y2, z2, x3, y3, z3, sprite, flip, wrapX, wrapY, Screen.DEFAULT_BLEND, cropX0, cropY0, cropX1, cropY1, fixed);}
    //
    public void renderSprite_Quad(int x0, int y0, int z0, int x1, int y1, int z1, int x2, int y2, int z2, int x3, int y3, int z3,
    Sprite sprite, byte flip, int wrapX, int wrapY, Vector4f blendingColor, int cropX0, int cropY0, int cropX1, int cropY1, boolean fixed)
    {
        add_basic
        (
            x0, y0, z0,
            x1, y1, z1,
            x2, y2, z2,
            x3, y3, z3,
            //
            blendingColor, sprite, flip, 0, 0, 0, fixed
        );
    }


    /*
     * Debug Stuff
     */

    @Override
    /**Renders a Point.*/
    public void drawPoint(int xPos, int yPos, Vector4f pointColor, int cropX0, int cropY0, int cropX1, int cropY1, boolean fixed){}

    /**Renders a Point taking this game's z-coordinate into account.*/
    public void drawPoint(int xPos, int yPos, int zPos, Vector4f pointColor, int cropX0, int cropY0, int cropX1, int cropY1, boolean fixed)
    {drawPoint(xPos, yPos - (zPos/2), pointColor, cropX0, cropY0, cropX1, cropY1, fixed);}
    

    @Override
    /**Renders a Line.*/
    public void drawLine(int x0, int y0, int x1, int y1, Vector4f color, int cropX0, int cropY0, int cropX1, int cropY1, boolean fixed)
    {drawLine(x0, y0, 0, x1, y1, 0, color, cropX0, cropY0, cropX1, cropY1, fixed);}
    //
    public void drawLine(int x0, int y0, int z0, int x1, int y1, int z1, Vector4f color, int cropX0, int cropY0, int cropX1, int cropY1, boolean fixed)
    {
        //System.out.println("Line");
        add_line(x0, y0, z0, x1, y1, z1, color, fixed);
    }

    @Override
    /**Renders a Rect.*/
    public void drawRect(int xPos, int yPos, int w, int h, Vector4f vecColor, int cropX0, int cropY0, int cropX1, int cropY1, boolean fixed)
    {drawRect(xPos, yPos, 0, w, h, vecColor, cropX0, cropY0, cropX1, cropY1, fixed);}
    //
    public void drawRect(int xPos, int yPos, int zPos, int w, int h, Vector4f vecColor, int cropX0, int cropY0, int cropX1, int cropY1, boolean fixed)
    {
        add_line(xPos,   yPos,   zPos, xPos+w, yPos,   zPos, vecColor, fixed);
        add_line(xPos+w, yPos,   zPos, xPos+w, yPos+h, zPos, vecColor, fixed);
        add_line(xPos+w, yPos+h, zPos, xPos,   yPos+h, zPos, vecColor, fixed);
        add_line(xPos,   yPos+h, zPos, xPos,   yPos,   zPos, vecColor, fixed);
    }

    @Override
    public void fillRect(int xPos, int yPos, int w, int h, Vector4f vecColor, int cropX0, int cropY0, int cropX1, int cropY1, boolean fixed)
    {renderSprite_St(xPos, yPos, 0, Sprites.whiteSprite, Sprite.FLIP_NONE, 0, 0, vecColor, cropX0, cropY0, cropX1, cropY1, w, h, fixed);}
    //
    public void fillRect(int xPos, int yPos, int zPos, int w, int h, Vector4f vecColor, int cropX0, int cropY0, int cropX1, int cropY1, boolean fixed)
    {renderSprite_St(xPos, yPos, zPos, Sprites.whiteSprite, Sprite.FLIP_NONE, 0, 0, vecColor, cropX0, cropY0, cropX1, cropY1, w, h, fixed);}


    @Override
    /**Draws a Quad.*/
    public void drawQuad(int x0, int y0, int x1, int y1, int x2, int y2, int x3, int y3,
    Vector4f vecColor, int cropX0, int cropY0, int cropX1, int cropY1, boolean fixed)
    {drawQuad(x0, y0, 0, x1, y1, 0, x2, y2, 0, x3, y3, 0, vecColor, cropX0, cropY0, cropX1, cropY1, fixed);}

    @Override
    /**Draws a Quad.*/
    public void drawQuad(int x0, int y0, int z0, int x1, int y1, int z1, int x2, int y2, int z2, int x3, int y3, int z3,
    Vector4f vecColor, int cropX0, int cropY0, int cropX1, int cropY1, boolean fixed)
    {
        add_line(x0, y0, z0, x1, y1, z1, vecColor, fixed);
        add_line(x1, y1, z1, x2, y2, z2, vecColor, fixed);
        add_line(x2, y2, z2, x3, y3, z3, vecColor, fixed);
        add_line(x3, y3, z3, x0, y0, z0, vecColor, fixed);
    }


    @Override
    /**Renders a filled Quad.*/
    public void fillQuad(int x0, int y0, int x1, int y1, int x2, int y2, int x3, int y3, Vector4f vecColor, int cropX0, int cropY0, int cropX1, int cropY1, boolean fixed)
    {renderSprite_Quad(x0, y0, 0, x1, y1, 0, x2, y2, 0, x3, y3, 0, Sprites.whiteSprite, Sprite.FLIP_NONE, 0, 0, vecColor, cropX0, cropY0, cropX1, cropY1, fixed);}
    //
    public void fillQuad(int x0, int y0, int z0, int x1, int y1, int z1, int x2, int y2, int z2, int x3, int y3, int z3, Vector4f vecColor, int cropX0, int cropY0, int cropX1, int cropY1, boolean fixed)
    {renderSprite_Quad(x0, y0, z0, x1, y1, z1, x2, y2, z2, x3, y3, z3, Sprites.whiteSprite, Sprite.FLIP_NONE, 0, 0, vecColor, cropX0, cropY0, cropX1, cropY1, fixed);}


    @Override
    /**Renders a Circle.*/
    public void drawCircle(int xPos, int yPos, float radius, int thickness, Vector4f vecColor, int cropX0, int cropY0, int cropX1, int cropY1, boolean fixed)
    {drawCircle(xPos, yPos, 0, radius, thickness, vecColor, cropX0, cropY0, cropX1, cropY1, fixed);}

    @Override
    public void drawCircle(int xPos, int yPos, int zPos, float radius, int thickness, Vector4f vecColor, int cropX0, int cropY0, int cropX1, int cropY1, boolean fixed)
    {
        add_circle(xPos, yPos, zPos, radius, vecColor, thickness, fixed);
    }


    
}
