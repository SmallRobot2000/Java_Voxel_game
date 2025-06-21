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

import java.util.Objects;
import java.util.Random;
import java.util.Vector;

public class Chunk {
    private Array<ModelInstance> chnkIstances = new Array<ModelInstance>();
    private ModelBuilder modelBuilder;
    private Model chunMmodel;
    //public short[][][] blocks;
    public Block[][][] blockObjects;

    private Array<Float> vertices = new Array<Float>();
    private int posX;
    private int posZ;
    private final int size = 16;
    public static final int sizeY = 16;
    private TextureUV[] textureUVS; //8 points per block
    private final Texture blocksTexture;
    private float[] verticesArr;
    private short[] indicesArr;
    //private SolidBlock testBlock = new SolidBlock();
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
        //blocks = new short[size][sizeY][size];
        blockObjects = new Block[size][sizeY][size];
        PerlinNoiseGenerator noise = new PerlinNoiseGenerator(22);




        //blockObjects
        genBlocks(cOffx, cOffz);


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
        this.textureUVS = textureUVS;
        this.blocksTexture = blocksTexture;
        blockObjects = new Block[size][sizeY][size];

        genBlocks(cOffx, cOffz);
        if(onlyVI)
        {
            createVI();
        }else if (indicesArr != null && verticesArr != null){

            chunMmodel = createModel();
            chnkIstances.add(new ModelInstance(chunMmodel, cOffx*size, 0, cOffz*size));
        }



    }
    private void genBlocks(int cOffx, int cOffz)
    {
        posX = cOffx;
        posZ = cOffz;

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

                    int res = yTop >= y ? (short)1 : (short)0;
                    if(res == 1)
                    {
                        blockObjects[x][y][z] = new SolidBlock(false, true, new Vector3i(x,y,z), 1, 1);

                    }else{
                        blockObjects[x][y][z] = new SolidBlock(true, false, new Vector3i(x,y,z), 0, 0);

                    }

                }
            }
        }
    }

    public void createInstances(float cOffx, float cOffz)
    {
        chunMmodel.dispose();
        chunMmodel = createModel();
        chnkIstances.clear();
        chnkIstances.add(new ModelInstance(chunMmodel, cOffx*size, 0, cOffz*size));
    }

    public void createInstances()
    {
        chunMmodel = createModel();
        chnkIstances.clear();
        chnkIstances.add(new ModelInstance(chunMmodel, posX*size, 0, posZ*size));
    }
    public void setBlock(int x, int y, int z, short id) {
        //this.blocks[x][y][z] = id;
        //createMy(posX,posZ);
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
    private Mesh chunkMesh;
    public Model createModel() {
        if(chunkMesh != null){chunkMesh.dispose();}
        chunkMesh = new Mesh(true, verticesArr.length, indicesArr.length,
            new VertexAttribute(VertexAttributes.Usage.Position, 3, ShaderProgram.POSITION_ATTRIBUTE),
            new VertexAttribute(VertexAttributes.Usage.Normal, 3, ShaderProgram.NORMAL_ATTRIBUTE),
            new VertexAttribute(VertexAttributes.Usage.TextureCoordinates,2,ShaderProgram.TEXCOORD_ATTRIBUTE + "0"));


        chunkMesh.setVertices(verticesArr);
        //System.out.println(indicesArr.length);
        chunkMesh.setIndices(indicesArr);


        Material material = new Material();
        material.set(TextureAttribute.createDiffuse(blocksTexture));




        //material.set(attribute);
        modelBuilder = new ModelBuilder();
        modelBuilder.begin();// begin it
        modelBuilder.part("plane", chunkMesh,
            GL20.GL_TRIANGLES ,
            0,
            chunkMesh.getNumIndices(),
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

        vertices.clear();


        indices.clear();




        int curIndx = 0;


        long t1 = System.nanoTime();
        for(int block = 0; block < size*sizeY*size; block++){
            int x = block % size;
            int y = (block / size) % sizeY;
            int z = block / (sizeY * size);

            Block testBlock = (Block)blockObjects[x][y][z];
            //(Block)((Block) blockObjects[x][y][z]).blockID;
            if(testBlock.invisible == true)
            {

                continue;
            }

            //these are 1 if hidden

            boolean xn = x == 0 ? false : !(((Block) blockObjects[Math.max(0, x - 1)][y][z]).invisible) ;
            boolean xp = x == size-1 ? false : !(((Block) blockObjects[Math.min(size - 1, x + 1)][y][z]).invisible);

            boolean yn = y == 0 ? false : !(((Block) blockObjects[x][Math.max(0, y - 1)][z]).invisible);
            boolean yp = y == sizeY-1 ? false : !(((Block) blockObjects[x][Math.min(sizeY - 1, y + 1)][z]).invisible);

            boolean zn = z == 0 ? false : !(((Block) blockObjects[x][y][Math.max(0, z - 1)]).invisible);
            boolean zp = z == size-1 ? false : !(((Block) blockObjects[x][y][Math.min(size - 1, z + 1)]).invisible);

            xn &= testBlock.xnCanVisible;
            xp &= testBlock.xpCanVisible;

            yn &= testBlock.ynCanVisible;
            yp &= testBlock.ypCanVisible;

            zn &= testBlock.znCanVisible;
            zp &= testBlock.zpCanVisible;



            short blockIndex = (short) ((short)((Block) blockObjects[x][y][z]).blockID); //Only for textures
            testBlock.position.x = x;
            testBlock.position.y = y;
            testBlock.position.z = z;
            testBlock.blockTextureID = blockIndex;
            curIndx = testBlock.generateMeshData(xp, xn, yp, yn, zp, zn, curIndx, textureUVS);
            vertices.addAll(testBlock.vertices);
            indices.addAll(testBlock.indices);



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

    public Block getBlockObject(int x, int y, int z) {
        return blockObjects[x][y][z];
    }

    public void regenVI() {
        //chunMmodel.dispose();
        vertices.clear();
        vertices = new Array<Float>();

        createVI();
    }
}
class TextureUV{
    public float x = 0f;
    public float y = 0f;
}
