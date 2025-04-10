package io.github.myPackage;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.Shader;
import com.badlogic.gdx.utils.Array;
import com.sun.org.apache.xpath.internal.operations.Mod;

import java.util.ArrayList;
import java.util.List;

public class World {
    private Array<Chunk> WorldLoadedChunks = new Array<Chunk>();

    private long seed = 123;
    private int renderSize = 13;
    private Array<ModelInstance> worldModelInstance;
    //private Chunk = pool(Chunk);
    private TextureUV[] globalTextureUVS;
    private int textureBlockFaces = 16;
    private final Texture blocksTexture;
    private BlockSet[] globalBlockSet = new BlockSet[16];
    private Array<UnChunk> unfinishedChunks;
    private Array<Integer> updateChunks;
    public World(long seedIn)
    {
        seed = seedIn;
        worldModelInstance = new Array<ModelInstance>();
        unfinishedChunks = new Array<UnChunk>();
        globalTextureUVS = new TextureUV[24*16];
        blocksTexture = new Texture("data/blocks.png");

        // Initialize globalBlockSet array elements
        for (int i = 0; i < globalBlockSet.length; i++) {
            globalBlockSet[i] = new BlockSet(); // Replace with appropriate constructor
        }
        globalBlockSet[0].setTextureIDs(1,1,0,2,1,1);


        int texSize = 16;
        int widthTotal = blocksTexture.getWidth();
        int heightTotal = blocksTexture.getHeight();
        int texCnt = 0;
        float dp = 1f/(float)widthTotal; //delta position
        for(int i = 0; i < globalTextureUVS.length/4; i += 4)
        {
            int curID = i/24;
            int curSubTexID = (i/4)%6;
            //System.out.println("Sub texture: " + curSubTexID + " ID: " + curID);
            int[] subBlockSidesIDs = globalBlockSet[0].getTextureSides();
            int curTexID = subBlockSidesIDs[curSubTexID];

            int x0 = texSize*curTexID % widthTotal;
            int x1 = x0+texSize;

            int y0 = (curTexID / texSize) * texSize;
            int y1 = y0+texSize;

            float xf0 = (float)((float)x0*dp);
            float xf1 = (float)((float)x1*dp);

            float yf0 = (float)((float)y0*dp);
            float yf1 = (float)((float)y1*dp);

            //System.out.println("ID tex: " + curTexID);
            //System.out.println("x0 and x0f : " + x0 + " " + xf0);
            //System.out.println("y0 and y0f : " + y0 + " " + yf0);
            //System.out.println("x1 and x1f : " + x1 + " " + xf1);
            //System.out.println("y1 and y1f : " + y1 + " " + yf1);

            globalTextureUVS[i] = new TextureUV();
            globalTextureUVS[i+1] = new TextureUV();
            globalTextureUVS[i+2] = new TextureUV();
            globalTextureUVS[i+3] = new TextureUV();

            globalTextureUVS[i+3].x = xf0;
            globalTextureUVS[i+3].y = yf0;

            globalTextureUVS[i+2].x = xf1;
            globalTextureUVS[i+2].y = yf0;

            globalTextureUVS[i+1].x = xf1;
            globalTextureUVS[i+1].y = yf1;

            globalTextureUVS[i].x = xf0;
            globalTextureUVS[i].y = yf1;

/*

            globalTextureUVS[i+3].x = 0f;
            globalTextureUVS[i+3].y = 0f;

            globalTextureUVS[i+2].x = 1f;
            globalTextureUVS[i+2].y = 0f;

            globalTextureUVS[i+1].x = 1f;
            globalTextureUVS[i+1].y = 1f;

            globalTextureUVS[i].x = 0f;
            globalTextureUVS[i].y = 1f;
*/

        }
/*
        for(int i = 0; i < 16; i++)
        {
            System.out.println("Tex: " + globalTextureUVS[i].x + " " + globalTextureUVS[i].y);
        }

 */


    }

    public void updateDrawnChunks(float camX, float camZ)
    {

        Array<Integer> allFx = new Array<Integer>();
        Array<Integer> allFz = new Array<Integer>();
        for (int x = 0; x < renderSize; x++)
        {
            for(int z = 0; z < renderSize; z++)
            {
                int fx = ((int)camX-(renderSize -1)/2)+x;
                int fz = ((int)camZ-(renderSize -1)/2)+z;
                allFx.add(fx);
                allFz.add(fz);
                //System.out.println("At " + fx + " " + fz);
                boolean found = false;
                for(int i = 0; i < WorldLoadedChunks.size; i++)
                {
                    if(WorldLoadedChunks.get(i).getPosX() == fx && WorldLoadedChunks.get(i).getPosZ() == fz)
                    {
                        found = true;
                    }
                }

                if(!found)
                {
                    //System.out.println("Generating chunk " + fx + " " + fz);
                    WorldLoadedChunks.add(new Chunk(fx,fz,seed,globalTextureUVS,blocksTexture));


                }
            }
        }

        for(int i = 0; i < WorldLoadedChunks.size; i ++)
        {
            if(!allFx.contains(WorldLoadedChunks.get(i).getPosX(), true) || !allFz.contains(WorldLoadedChunks.get(i).getPosZ(), true))
            {
                //System.out.println("Removing chunk " + WorldLoadedChunks.get(i).getPosX() + " " + WorldLoadedChunks.get(i).getPosZ());
                WorldLoadedChunks.removeIndex(i);   //remove unloaded chunks
            }
        }

        worldModelInstance.clear();
        for (Chunk currChunk:WorldLoadedChunks)
        {
            worldModelInstance.addAll(currChunk.getChnkIstances());
        }
        //System.gc(); //garbage colector
    }

    public Chunk getChunk(int x, int z)
    {
        for(int i = 0; i < WorldLoadedChunks.size; i ++)
        {
            if(WorldLoadedChunks.get(i).getPosZ() == z && WorldLoadedChunks.get(i).getPosX() == x)
            {
                return WorldLoadedChunks.get(i);
            }
        }
         return null;
    }

    public void updateChunksVI(float camX, float camZ)
    {
        Array<Integer> allFx = new Array<Integer>();
        Array<Integer> allFz = new Array<Integer>();
        for (int x = 0; x < renderSize; x++)
        {
            for(int z = 0; z < renderSize; z++)
            {
                int fx = ((int)camX-(renderSize -1)/2)+x;
                int fz = ((int)camZ-(renderSize -1)/2)+z;
                allFx.add(fx);
                allFz.add(fz);
                //System.out.println("At " + fx + " " + fz);
                boolean found = false;
                for(int i = 0; i < WorldLoadedChunks.size; i++)
                {
                    if(WorldLoadedChunks.get(i).getPosX() == fx && WorldLoadedChunks.get(i).getPosZ() == fz)
                    {
                        found = true;
                        break;
                    }
                }

                if(!found)
                {
                    //System.out.println("Generating chunk " + fx + " " + fz);
                    UnChunk tmpUnChunk = new UnChunk();
                    tmpUnChunk.x = fx;
                    tmpUnChunk.z = fz;
                    Chunk tmpChunk = new Chunk(fx,fz,seed,globalTextureUVS,blocksTexture, true);
                    WorldLoadedChunks.add(tmpChunk);
                    WorldLoadedChunks.get(WorldLoadedChunks.indexOf(tmpChunk, true)).createVI();

                    unfinishedChunks.add(tmpUnChunk);

                }
            }
        }

        for(int i = 0; i < WorldLoadedChunks.size; i ++)
        {
            if(!(allFx.contains(WorldLoadedChunks.get(i).getPosX(), true) && allFz.contains(WorldLoadedChunks.get(i).getPosZ(), true)))
            {
                //System.out.println("Removing chunk " + WorldLoadedChunks.get(i).getPosX() + " " + WorldLoadedChunks.get(i).getPosZ());
                WorldLoadedChunks.removeIndex(i);   //remove unloaded chunks
            }
        }

    }

    public void updateChunksModels(float camX, float camZ)
    {

        for (int i = 0; i < unfinishedChunks.size; i++) {
            int x = unfinishedChunks.get(i).x;
            int z = unfinishedChunks.get(i).z;

            for(int j = 0; j < WorldLoadedChunks.size; j ++)
            {
                if(WorldLoadedChunks.get(j).getPosX() == x && WorldLoadedChunks.get(j).getPosZ() == z)
                {

                    WorldLoadedChunks.get(j).createInstances(x,z);


                }
            }

        }
        unfinishedChunks.clear();
        worldModelInstance.clear();
        for (Chunk currChunk:WorldLoadedChunks)
        {
            worldModelInstance.addAll(currChunk.getChnkIstances());
        }

    }

    public void setBlock(int x, int y, int z, short ID)
    {

        int chXChk,chZChk, inChXChk, inChYChk, inChZChk;

        chXChk = (int)((int)x >= 0 ? (int)x/16 : ((int)x/16)-1);
        chZChk = (int)((int)z >= 0 ? (int)z/16 : ((int)z/16)-1);

        inChXChk = (int)(((int)x) >= 0 ? ((int)x)%16 : (16 + ((int)x)%16)-1);
        inChZChk = (int)(((int)z) >= 0 ? ((int)z)%16 : (16 + ((int)z)%16)-1);

        inChYChk = (int)(y%Chunk.sizeY);

        for(int i = 0; i < WorldLoadedChunks.size; i ++)
        {
            if(WorldLoadedChunks.get(i).getPosX() == chXChk && WorldLoadedChunks.get(i).getPosZ() == chZChk)
            {
                WorldLoadedChunks.get(i).setBlock(inChXChk,inChYChk,inChZChk,ID);

                if(!updateChunks.contains(i, true))
                {
                    updateChunks.add(i);
                }
            }
        }


    }

    public Array<ModelInstance> getInstances() {
        return worldModelInstance;
    }

    public short getBlockID(int chx, int chz, int icx, int icy,int icz)
    {
        for(int i = 0; i < WorldLoadedChunks.size; i ++)
        {
            if(WorldLoadedChunks.get(i).getPosX() == chx && WorldLoadedChunks.get(i).getPosZ() == chz)
            {
                return WorldLoadedChunks.get(i).getBlock(icx,icy,icz);
            }
        }
        return 0;
    }

    public short getGlobalBlockID(float chkX, float chkY, float chkZ)
    {

        int chXChk,chZChk, inChXChk, inChYChk, inChZChk;
        chXChk = (int)((int)chkX >= 0 ? (int)chkX/16 : ((int)chkX/16)-1);
        chZChk = (int)((int)chkZ >= 0 ? (int)chkZ/16 : ((int)chkZ/16)-1);

        inChXChk = (int)(((int)chkX) >= 0 ? ((int)chkX)%16 : (16 + ((int)chkX)%16)-1);
        inChZChk = (int)(((int)chkZ) >= 0 ? ((int)chkZ)%16 : (16 + ((int)chkZ)%16)-1);

        inChYChk = (int)(chkY%Chunk.sizeY);
        //System.out.println("OK in calc for chunk pos!!: X : " + inChXChk + " Y : " + inChYChk + " Z : " + inChZChk + " CH X : " + chXChk + "CH Z : " + chZChk);
        if(chkY < 0)
        {
            return 0;
        }
        if(chkY >= Chunk.sizeY)
        {
            return 0;
        }




        if(inChXChk >= 16 || inChZChk >= 16)
        {
            System.out.println("ERROR in calc for chunk pos!!: X : " + inChXChk + " Y : " + inChYChk + " Z : " + inChZChk);
            System.out.println("X : " + chkX + " Y : " + chkY + " Z : " + chkZ);
        }


        return this.getBlockID(chXChk, chZChk, inChXChk, inChYChk, inChZChk);
    }
}
class UnChunk {
    public int x;
    public int z;
}
