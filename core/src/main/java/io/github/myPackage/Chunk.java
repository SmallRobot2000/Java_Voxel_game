package io.github.myPackage;

import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g3d.*;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.DirectionalLightsAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import org.w3c.dom.Attr;

import java.util.Random;
import java.util.Vector;

public class Chunk {
    private Array<ModelInstance> chnkIstances = new Array<ModelInstance>();
    private ModelBuilder modelBuilder;
    private Model chunMmodel;
    private short[][][] blocks;

    private Array<Float> vertices = new Array<Float>();
    private int posX;
    private int posZ;
    private final int size = 16;
    public static final int sizeY = 16;
    private TextureUV[] textureUVS; //8 points per block
    private final Texture blocksTexture;
    private float[] verticesArr;
    private short[] indicesArr;
    /*
    private int width = size+1;
    private int height = size+1;
    private int depth = size+1;
    private int devisions = size;
    */


    public Chunk(int cOffx, int cOffz, long seed, TextureUV[] textureUVS, Texture blocksTexture)
    {
        //modelBuilder = new ModelBuilder();
        //Box = modelBuilder.createBox(1,1,1,
        //    new Material(ColorAttribute.createDiffuse(Color.GREEN)),
        //    VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal);
        posX = cOffx;
        posZ = cOffz;
        this.textureUVS = textureUVS;
        this.blocksTexture = blocksTexture;
        blocks = new short[size][sizeY][size];
        PerlinNoiseGenerator noise = new PerlinNoiseGenerator(22);

        for (int x = 0; x < size; x ++)
        {
            for (int y = 0; y < sizeY; y ++)
            {
                for (int z = 0; z < size; z ++)
                {
                    float cOffxf = (float)cOffx;
                    float cOffzf = (float)cOffz;

                    float xf = (float)(((float)x)/16f)+cOffxf;
                    float zf = (float)(((float)z)/16f)+cOffzf;
                    short yTop = (short)((noise.noise2(xf,zf))*10);

                    //System.out.println("X " + xf + "Z " + zf + "Y" + yTop);

                    //System.out.println(yTop);
                    blocks[x][y][z] = yTop >= y ? (short)1 : (short)0;
                }
            }
        }

        createMy(cOffx,cOffz);


    }

    /*
    OnlyVI - if true only vertecies and indecies
           - if false only model is made
     */
    public Chunk(int cOffx, int cOffz, long seed, TextureUV[] textureUVS, Texture blocksTexture, boolean onlyVI)
    {
        //modelBuilder = new ModelBuilder();
        //Box = modelBuilder.createBox(1,1,1,
        //    new Material(ColorAttribute.createDiffuse(Color.GREEN)),
        //    VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal);
        posX = cOffx;
        posZ = cOffz;
        this.textureUVS = textureUVS;
        this.blocksTexture = blocksTexture;
        blocks = new short[size][sizeY][size];
        PerlinNoiseGenerator noise = new PerlinNoiseGenerator(22);

        for (int x = 0; x < size; x ++)
        {
            for (int y = 0; y < sizeY; y ++)
            {
                for (int z = 0; z < size; z ++)
                {
                    float cOffxf = (float)cOffx;
                    float cOffzf = (float)cOffz;

                    float xf = (float)(((float)x)/16f)+cOffxf;
                    float zf = (float)(((float)z)/16f)+cOffzf;
                    short yTop = (short)((noise.noise2(xf,zf))*10);

                    //System.out.println("X " + xf + "Z " + zf + "Y" + yTop);

                    //System.out.println(yTop);
                    blocks[x][y][z] = yTop >= y ? (short)1 : (short)0;
                }
            }
        }
        if(onlyVI)
        {
            createVI();
        }else if (indicesArr != null && verticesArr != null){

            chunMmodel = createModel();
            chnkIstances.add(new ModelInstance(chunMmodel, cOffx*size, 0, cOffz*size));
        }



    }
    public void createInstances(float cOffx, float cOffz)
    {
        chunMmodel = createModel();
        chnkIstances.add(new ModelInstance(chunMmodel, cOffx*size, 0, cOffz*size));
    }
    public void setBlock(int x, int y, int z, short id) {
        this.blocks[x][y][z] = id;
        createMy(posX,posZ);
    }

    public Array<ModelInstance> getChnkIstances() {
        return chnkIstances;
    }





        public void createMy(int cOffX, int cOffZ)
        {


            chunMmodel = createObjectCubes();
            chnkIstances.add(new ModelInstance(chunMmodel, cOffX*size, 0, cOffZ*size));
            //chunMmodel.dispose();
        }
        private Model createObjectCubes() {

            createVI();
            return createModel();

        }

    public Model createModel() {
        Mesh mesh = new Mesh(true, verticesArr.length, indicesArr.length,
            new VertexAttribute(VertexAttributes.Usage.Position, 3, ShaderProgram.POSITION_ATTRIBUTE),
            new VertexAttribute(VertexAttributes.Usage.Normal, 3, ShaderProgram.NORMAL_ATTRIBUTE),
            new VertexAttribute(VertexAttributes.Usage.TextureCoordinates,2,ShaderProgram.TEXCOORD_ATTRIBUTE + "0"));

        mesh.setVertices(verticesArr);
        mesh.setIndices(indicesArr);


        Material material = new Material();
        material.set(TextureAttribute.createDiffuse(blocksTexture));




        //material.set(attribute);
        modelBuilder = new ModelBuilder();
        modelBuilder.begin();// begin it
        modelBuilder.part("plane", mesh,
            GL20.GL_TRIANGLES ,
            0,
            mesh.getNumIndices(),
            material); // creates your mesh

        // makes the model
        return modelBuilder.end();
    }


    public int getPosX() {return posX;}
    public int getPosZ() {return posZ;}

    /*
    Crates vertices and indices for train
     */
    public void createVI()
    {

        verticesArr = new float[vertices.size];

        Array<Short> indices = new Array<Short>();

        //Array<Float> vertices = new Array<Float>();
        vertices.clear();
        //FloatBuffer vertices;

        int width = size+1;
        int height = sizeY+1;
        int depth = size+1;
        int devisions = size;
        int devisionsY = sizeY;
        int[][][] vertIndex = new int[width][height][depth];



        float dx = (float)width/devisions;
        float dz = (float)height/devisions;
        float dy = (float)depth/devisionsY;
        Noise n = new Noise(null, 1.0f, 128, 128);
        n.initialise();
        float[][] noise = new float[32][32];
        noise = n.toFloats();

        int curIndx = 0;


        long t1 = System.nanoTime();
        for(int block = 0; block < size*sizeY*size; block++){
            int x = block % size;
            int y = (block / size) % sizeY;
            int z = block / (sizeY * size);

            short blockIndex = (short) ((short)blocks[x][y][z]); //Only for textures
            boolean xn = x == 0 ? false : (blocks[Math.max(0,x-1)][y][z] != 0 ? true : false) ;
            boolean xp = x == size-1 ? false : (blocks[Math.min(size-1,x+1)][y][z] != 0 ? true : false);

            boolean yn = y == 0 ? false : (blocks[x][Math.max(0, y-1)][z] != 0 ? true : false);
            boolean yp = y == sizeY-1 ? false : (blocks[x][Math.min(sizeY-1,y+1)][z] != 0 ? true : false);

            boolean zn = z == 0 ? false : (blocks[x][y][Math.max(0,z-1)] != 0 ? true : false);
            boolean zp = z == size-1 ? false : (blocks[x][y][Math.min(size-1,z+1)] != 0 ? true : false);

            if((blocks[x][y][z]&0x8000) != 0)//Render all no mathe what
            {
                xn = false; xp = false; zn = false; zp = false; yn = false; yp = false;
            }
            //System.out.println("X: " + x + " Y: " + y + " Z: " + z);



            float[] xAdd = {0,1,1,0,0,1,1,0};
            float[] yAdd = {0,0,0,0,1,1,1,1};
            float[] zAdd = {0,0,1,1,0,0,1,1};

            Vector3[] Normals = {
                new Vector3(0,-1,0),
                new Vector3(0,1,0),
                new Vector3(-1,0,0),
                new Vector3(1,0,0),
                new Vector3(0,0,-1),
                new Vector3(0,0,1),
            };

            //bottom face
            short c0 = (short)(curIndx);
            short c1 = (short)(curIndx+1);
            short c2 = (short)(curIndx+2);
            short c3 = (short)(curIndx+3);
            //Upper face
            short c4 = (short)(curIndx+4);
            short c5 = (short)(curIndx+5);
            short c6 = (short)(curIndx+6);
            short c7 = (short)(curIndx+7);
            //curIndx += 8;
                /*
                //bottom face
                short c0 = (short) vertIndex[x][y][z];
                short c1 = (short) vertIndex[x+1][y][z];
                short c2 = (short) vertIndex[x+1][y][z+1];
                short c3 = (short) vertIndex[x][y][z+1];
                //Upper face
                short c4 = (short) vertIndex[x][y+1][z];
                short c5 = (short) vertIndex[x+1][y+1][z];
                short c6 = (short) vertIndex[x+1][y+1][z+1];
                short c7 = (short) vertIndex[x][y+1][z+1];
                */

            if(blocks[x][y][z] == 0)
            {

                continue;
            }

            int normal;
            short blockIndexTextureOffset = (short) (blockIndex*24-24); //So it starts from 0 (1 = 0; 0 is air)
            short curBlockIndex;



            int texCnt = 0;
            if(!yn)
            {

                normal= 0;
                curBlockIndex = blockIndexTextureOffset;

                vertices.add(x+xAdd[0], y+yAdd[0], z+zAdd[0]);
                vertices.add(Normals[normal].x,Normals[normal].y,Normals[normal].z);
                vertices.add(textureUVS[curBlockIndex].x,textureUVS[curBlockIndex].y);

                vertices.add(x+xAdd[1], y+yAdd[1], z+zAdd[1]);
                vertices.add(Normals[normal].x,Normals[normal].y,Normals[normal].z);
                vertices.add(textureUVS[curBlockIndex+1].x,textureUVS[curBlockIndex+1].y);

                vertices.add(x+xAdd[3], y+yAdd[3], z+zAdd[3]);
                vertices.add(Normals[normal].x,Normals[normal].y,Normals[normal].z);
                vertices.add(textureUVS[curBlockIndex+3].x,textureUVS[curBlockIndex+3].y);

                vertices.add(x+xAdd[3], y+yAdd[3], z+zAdd[3]);
                vertices.add(Normals[normal].x,Normals[normal].y,Normals[normal].z);
                vertices.add(textureUVS[curBlockIndex+3].x,textureUVS[curBlockIndex+3].y);

                vertices.add(x+xAdd[1], y+yAdd[1], z+zAdd[1]);
                vertices.add(Normals[normal].x,Normals[normal].y,Normals[normal].z);
                vertices.add(textureUVS[curBlockIndex+1].x,textureUVS[curBlockIndex+1].y);

                vertices.add(x+xAdd[2], y+yAdd[2], z+zAdd[2]);
                vertices.add(Normals[normal].x,Normals[normal].y,Normals[normal].z);
                vertices.add(textureUVS[curBlockIndex+2].x,textureUVS[curBlockIndex+2].y);

                //-y
                indices.add((short)curIndx);
                indices.add((short)(curIndx+1));
                indices.add((short)(curIndx+2));


                //-y
                indices.add((short)(curIndx+3));
                indices.add((short)(curIndx+4));
                indices.add((short)(curIndx+5));

                curIndx += 6;

                    /*
                    //-y
                    indices.add(c0);
                    indices.add(c1);
                    indices.add(c3);


                    //-y
                    indices.add(c3);
                    indices.add(c1);
                    indices.add(c2);

                     */
            }

            if(!yp)
            {
                normal= 1;
                curBlockIndex = (short) (blockIndexTextureOffset+4);
                vertices.add(x+xAdd[6], y+yAdd[6], z+zAdd[6]);
                vertices.add(Normals[normal].x,Normals[normal].y,Normals[normal].z);
                vertices.add(textureUVS[curBlockIndex+2].x,textureUVS[curBlockIndex+2].y);

                vertices.add(x+xAdd[5], y+yAdd[5], z+zAdd[5]);
                vertices.add(Normals[normal].x,Normals[normal].y,Normals[normal].z);
                vertices.add(textureUVS[curBlockIndex+1].x,textureUVS[curBlockIndex+1].y);

                vertices.add(x+xAdd[4], y+yAdd[4], z+zAdd[4]);
                vertices.add(Normals[normal].x,Normals[normal].y,Normals[normal].z);
                vertices.add(textureUVS[curBlockIndex].x,textureUVS[curBlockIndex].y);

                vertices.add(x+xAdd[4], y+yAdd[4], z+zAdd[4]);
                vertices.add(Normals[normal].x,Normals[normal].y,Normals[normal].z);
                vertices.add(textureUVS[curBlockIndex].x,textureUVS[curBlockIndex].y);

                vertices.add(x+xAdd[7], y+yAdd[7], z+zAdd[7]);
                vertices.add(Normals[normal].x,Normals[normal].y,Normals[normal].z);
                vertices.add(textureUVS[curBlockIndex+3].x,textureUVS[curBlockIndex+3].y);

                vertices.add(x+xAdd[6], y+yAdd[6], z+zAdd[6]);
                vertices.add(Normals[normal].x,Normals[normal].y,Normals[normal].z);
                vertices.add(textureUVS[curBlockIndex+2].x,textureUVS[curBlockIndex+2].y);

                //-y
                indices.add((short)curIndx);
                indices.add((short)(curIndx+1));
                indices.add((short)(curIndx+2));


                //-y
                indices.add((short)(curIndx+3));
                indices.add((short)(curIndx+4));
                indices.add((short)(curIndx+5));

                curIndx += 6;
                    /*
                    //+y
                    indices.add(c6);
                    indices.add(c5);
                    indices.add(c4);

                    //+y
                    indices.add(c4);
                    indices.add(c7);
                    indices.add(c6);

                     */
            }


            if(!xn)
            {
                normal= 2;
                curBlockIndex = (short) (blockIndexTextureOffset+8);
                vertices.add(x+xAdd[4], y+yAdd[4], z+zAdd[4]);
                vertices.add(Normals[normal].x,Normals[normal].y,Normals[normal].z);
                vertices.add(textureUVS[curBlockIndex+3].x,textureUVS[curBlockIndex+3].y);

                vertices.add(x+xAdd[0], y+yAdd[0], z+zAdd[0]);
                vertices.add(Normals[normal].x,Normals[normal].y,Normals[normal].z);
                vertices.add(textureUVS[curBlockIndex].x,textureUVS[curBlockIndex].y);

                vertices.add(x+xAdd[7], y+yAdd[7], z+zAdd[7]);
                vertices.add(Normals[normal].x,Normals[normal].y,Normals[normal].z);
                vertices.add(textureUVS[curBlockIndex+2].x,textureUVS[curBlockIndex+2].y);

                vertices.add(x+xAdd[0], y+yAdd[0], z+zAdd[0]);
                vertices.add(Normals[normal].x,Normals[normal].y,Normals[normal].z);
                vertices.add(textureUVS[curBlockIndex].x,textureUVS[curBlockIndex].y);

                vertices.add(x+xAdd[3], y+yAdd[3], z+zAdd[3]);
                vertices.add(Normals[normal].x,Normals[normal].y,Normals[normal].z);
                vertices.add(textureUVS[curBlockIndex+1].x,textureUVS[curBlockIndex+1].y);

                vertices.add(x+xAdd[7], y+yAdd[7], z+zAdd[7]);
                vertices.add(Normals[normal].x,Normals[normal].y,Normals[normal].z);
                vertices.add(textureUVS[curBlockIndex+2].x,textureUVS[curBlockIndex+2].y);

                //-y
                indices.add((short)curIndx);
                indices.add((short)(curIndx+1));
                indices.add((short)(curIndx+2));


                //-y
                indices.add((short)(curIndx+3));
                indices.add((short)(curIndx+4));
                indices.add((short)(curIndx+5));

                curIndx += 6;
                    /*
                    //-x
                    indices.add(c4);
                    indices.add(c0);
                    indices.add(c7);

                    //-x
                    indices.add(c0);
                    indices.add(c3);
                    indices.add(c7);

                     */
            }

            if(!xp)
            {
                normal= 3;
                curBlockIndex = (short) (blockIndexTextureOffset+12);
                vertices.add(x+xAdd[2], y+yAdd[2], z+zAdd[2]);
                vertices.add(Normals[normal].x,Normals[normal].y,Normals[normal].z);
                vertices.add(textureUVS[curBlockIndex].x,textureUVS[curBlockIndex].y);

                vertices.add(x+xAdd[1], y+yAdd[1], z+zAdd[1]);
                vertices.add(Normals[normal].x,Normals[normal].y,Normals[normal].z);
                vertices.add(textureUVS[curBlockIndex+1].x,textureUVS[curBlockIndex+1].y);

                vertices.add(x+xAdd[5], y+yAdd[5], z+zAdd[5]);
                vertices.add(Normals[normal].x,Normals[normal].y,Normals[normal].z);
                vertices.add(textureUVS[curBlockIndex+2].x,textureUVS[curBlockIndex+2].y);

                vertices.add(x+xAdd[2], y+yAdd[2], z+zAdd[2]);
                vertices.add(Normals[normal].x,Normals[normal].y,Normals[normal].z);
                vertices.add(textureUVS[curBlockIndex].x,textureUVS[curBlockIndex].y);

                vertices.add(x+xAdd[5], y+yAdd[5], z+zAdd[5]);
                vertices.add(Normals[normal].x,Normals[normal].y,Normals[normal].z);
                vertices.add(textureUVS[curBlockIndex+2].x,textureUVS[curBlockIndex+2].y);

                vertices.add(x+xAdd[6], y+yAdd[6], z+zAdd[6]);
                vertices.add(Normals[normal].x,Normals[normal].y,Normals[normal].z);
                vertices.add(textureUVS[curBlockIndex+3].x,textureUVS[curBlockIndex+3].y);

                //-y
                indices.add((short)curIndx);
                indices.add((short)(curIndx+1));
                indices.add((short)(curIndx+2));


                //-y
                indices.add((short)(curIndx+3));
                indices.add((short)(curIndx+4));
                indices.add((short)(curIndx+5));

                curIndx += 6;
                    /*
                    //+x
                    indices.add(c2);
                    indices.add(c1);
                    indices.add(c5);

                    //+x
                    indices.add(c2);
                    indices.add(c5);
                    indices.add(c6);
                    */
            }


            if(!zn) {
                normal= 4;
                curBlockIndex = (short) (blockIndexTextureOffset+16);
                vertices.add(x+xAdd[5], y+yAdd[5], z+zAdd[5]);
                vertices.add(Normals[normal].x,Normals[normal].y,Normals[normal].z);
                vertices.add(textureUVS[curBlockIndex+3].x,textureUVS[curBlockIndex+3].y);

                vertices.add(x+xAdd[1], y+yAdd[1], z+zAdd[1]);
                vertices.add(Normals[normal].x,Normals[normal].y,Normals[normal].z);
                vertices.add(textureUVS[curBlockIndex].x,textureUVS[curBlockIndex].y);

                vertices.add(x+xAdd[0], y+yAdd[0], z+zAdd[0]);
                vertices.add(Normals[normal].x,Normals[normal].y,Normals[normal].z);
                vertices.add(textureUVS[curBlockIndex+1].x,textureUVS[curBlockIndex+1].y);

                vertices.add(x+xAdd[4], y+yAdd[4], z+zAdd[4]);
                vertices.add(Normals[normal].x,Normals[normal].y,Normals[normal].z);
                vertices.add(textureUVS[curBlockIndex+2].x,textureUVS[curBlockIndex+2].y);

                vertices.add(x+xAdd[5], y+yAdd[5], z+zAdd[5]);
                vertices.add(Normals[normal].x,Normals[normal].y,Normals[normal].z);
                vertices.add(textureUVS[curBlockIndex+3].x,textureUVS[curBlockIndex+3].y);

                vertices.add(x+xAdd[0], y+yAdd[0], z+zAdd[0]);
                vertices.add(Normals[normal].x,Normals[normal].y,Normals[normal].z);
                vertices.add(textureUVS[curBlockIndex+1].x,textureUVS[curBlockIndex+1].y);

                //-y
                indices.add((short)curIndx);
                indices.add((short)(curIndx+1));
                indices.add((short)(curIndx+2));


                //-y
                indices.add((short)(curIndx+3));
                indices.add((short)(curIndx+4));
                indices.add((short)(curIndx+5));

                curIndx += 6;
                    /*
                    //-z
                    indices.add(c5);
                    indices.add(c1);
                    indices.add(c0);

                    //-z
                    indices.add(c4);
                    indices.add(c5);
                    indices.add(c0);
                    */
            }

            if(!zp)
            {
                curBlockIndex = (short) (blockIndexTextureOffset+20);
                normal= 5;
                vertices.add(x+xAdd[7], y+yAdd[7], z+zAdd[7]);
                vertices.add(Normals[normal].x,Normals[normal].y,Normals[normal].z);
                vertices.add(textureUVS[curBlockIndex+3].x,textureUVS[curBlockIndex+3].y);

                vertices.add(x+xAdd[3], y+yAdd[3], z+zAdd[3]);
                vertices.add(Normals[normal].x,Normals[normal].y,Normals[normal].z);
                vertices.add(textureUVS[curBlockIndex].x,textureUVS[curBlockIndex].y);

                vertices.add(x+xAdd[2], y+yAdd[2], z+zAdd[2]);
                vertices.add(Normals[normal].x,Normals[normal].y,Normals[normal].z);
                vertices.add(textureUVS[curBlockIndex+1].x,textureUVS[curBlockIndex+1].y);

                vertices.add(x+xAdd[6], y+yAdd[6], z+zAdd[6]);
                vertices.add(Normals[normal].x,Normals[normal].y,Normals[normal].z);
                vertices.add(textureUVS[curBlockIndex+2].x,textureUVS[curBlockIndex+2].y);

                vertices.add(x+xAdd[7], y+yAdd[7], z+zAdd[7]);
                vertices.add(Normals[normal].x,Normals[normal].y,Normals[normal].z);
                vertices.add(textureUVS[curBlockIndex+3].x,textureUVS[curBlockIndex+3].y);

                vertices.add(x+xAdd[2], y+yAdd[2], z+zAdd[2]);
                vertices.add(Normals[normal].x,Normals[normal].y,Normals[normal].z);
                vertices.add(textureUVS[curBlockIndex+1].x,textureUVS[curBlockIndex+1].y);

                //-y
                indices.add((short)curIndx);
                indices.add((short)(curIndx+1));
                indices.add((short)(curIndx+2));


                //-y
                indices.add((short)(curIndx+3));
                indices.add((short)(curIndx+4));
                indices.add((short)(curIndx+5));

                curIndx += 6;
                    /*
                    //+z
                    indices.add(c7);
                    indices.add(c3);
                    indices.add(c2);



                    //+z
                    indices.add(c6);
                    indices.add(c7);
                    indices.add(c2);
                    */
            }









        }

        short[] indicesArr = new short[indices.size];
        float[] verticesArr = new float[vertices.size];
        for(int i = 0; i < vertices.size; i++)
        {
            verticesArr[i] = vertices.get(i);
        }


        for(int i = 0; i < indices.size; i++)
        {
            indicesArr[i] = indices.get(i);
        }

        this.indicesArr = indicesArr;
        this.verticesArr = verticesArr;
    }

    public short getBlock(int x, int y, int z) {
        return blocks[x][y][z];
    }
}
class TextureUV{
    public float x = 0f;
    public float y = 0f;
}
