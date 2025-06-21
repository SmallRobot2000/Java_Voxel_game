package io.github.myPackage;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.utils.Array;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
public class World_old {
    //private Array<Chunk> WorldLoadedChunks = new Array<Chunk>();
    private HashMap<Vector2i, Chunk> WorldLoadedChunksMap = new HashMap<Vector2i, Chunk>();

    private long seed = 123;
    private int renderSize = 13;
    private Array<ModelInstance> worldModelInstance;
    //private Chunk = pool(Chunk);
    private TextureUV[] globalTextureUVS;
    private int textureBlockFaces = 16;
    private final Texture blocksTexture;
    private BlockSet[] globalBlockSet = new BlockSet[16];
    //private Array<UnChunk> unfinishedChunks;
    private Array<Chunk> updateChunks;
    private Array<Chunk> reGenChunks;
    private Array<Chunk> reModelChunks;
    public World_old(long seedIn)
    {
        seed = seedIn;
        worldModelInstance = new Array<ModelInstance>();
        updateChunks = new Array<Chunk>();
        reGenChunks = new Array<Chunk>();
        reModelChunks = new Array<Chunk>();
        globalTextureUVS = new TextureUV[24*16];
        blocksTexture = new Texture("data/blocks.png");

        //TODO
        //With JSON
        // Initialize globalBlockSet array elements
        for (int i = 0; i < globalBlockSet.length; i++) {
            globalBlockSet[i] = new BlockSet(); // Replace with appropriate constructor
        }
        globalBlockSet[0].setTextureIDs(1,1,0,2,1,1);


        //TODO make better
        //Create global texture UVS
        int texSize = 16;   //One texture size in pixels
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

        Array<Vector2i> activeChunks = new Array<Vector2i>();

        for (int x = 0; x < renderSize; x++)
        {
            for(int z = 0; z < renderSize; z++)
            {
                Vector2i currPos = new Vector2i(x, z);
                activeChunks.add(new Vector2i(x, z));

                if(!WorldLoadedChunksMap.containsKey(currPos))
                {
                    //System.out.println("Generating chunk " + fx + " " + fz);
                    Chunk tmpChunk = new Chunk(x,z,seed,globalTextureUVS,blocksTexture, true);
                    WorldLoadedChunksMap.put(new Vector2i(x,z), tmpChunk);
                    //WorldLoadedChunks.add(tmpChunk);
                    //tmpChunk.createVI();
                }
            }
        }

        for (WorldLoadedChunksMap.values().iterator(); WorldLoadedChunksMap.values().iterator().hasNext();) {
            Chunk currChunk = WorldLoadedChunksMap.values().iterator().next();
            if(!activeChunks.contains(new Vector2i(currChunk.getPosX(), currChunk.getPosZ()), true))
            {
                //System.out.println("Removing chunk " + currChunk.getPosX() + " " + currChunk.getPosZ());
                WorldLoadedChunksMap.remove(new Vector2i(currChunk.getPosX(), currChunk.getPosZ()));   //remove unloaded chunks
            }

        }

        worldModelInstance.clear();
        for (WorldLoadedChunksMap.values().iterator(); WorldLoadedChunksMap.values().iterator().hasNext();)
        {
            Chunk currChunk = WorldLoadedChunksMap.values().iterator().next();
            worldModelInstance.addAll(currChunk.getChnkIstances());
        }


        //System.gc(); //garbage colector
    }

    public Chunk getChunk(int x, int z)
    {

        return WorldLoadedChunksMap.get(new Vector2i(x, z));
    }

    /*
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
  */
    //in worker thread
    public void updateChunksVI(float camX, float camZ)
    {
        System.out.println("Size of world hash : " + WorldLoadedChunksMap.size());
        System.out.println("Size of regen buffer : " + reGenChunks.size);
        System.out.println("Size of remodel buffer : " + reModelChunks.size);
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


                if(!WorldLoadedChunksMap.containsKey(new Vector2i(fx, fz)))
                {
                    Chunk tmpChunk = new Chunk(fx,fz,seed,globalTextureUVS,blocksTexture, true);
                    updateChunks.add(tmpChunk);
                    WorldLoadedChunksMap.put(new Vector2i(fx, fz),tmpChunk);
                    //WorldLoadedChunks.get(WorldLoadedChunks.indexOf(tmpChunk, true)).createVI();  //Redundant? - yes
                }
            }
        }
        //System.out.println("VI middle of updateChunksVI");
        ArrayList<Vector2i> toRemove = new ArrayList<Vector2i>();
        Iterator<Chunk> it = WorldLoadedChunksMap.values().iterator();
        while(it.hasNext())
        {
            Chunk currChunk = it.next();
            if(!(allFx.contains(currChunk.getPosX(), true) && allFz.contains(currChunk.getPosZ(), true)))
            {
                //System.out.println("Removing chunk " + WorldLoadedChunks.get(i).getPosX() + " " + WorldLoadedChunks.get(i).getPosZ());
                toRemove.add(new Vector2i(currChunk.getPosX(), currChunk.getPosZ()));
            }
        }
        for(Vector2i currPos:toRemove)
        {
            WorldLoadedChunksMap.remove(currPos);   //remove unloaded chunks
        }
        System.out.println("VI middle of updateChunksVI 2");
        synchronized (reGenChunks)
        {
            for(Chunk currChk:reGenChunks)
            {
                if(currChk != null)
                {
                    currChk.regenVI();
                    currChk.getChnkIstances().clear();
                    updateChunks.add(currChk);

                }

            }
            reGenChunks.clear();

        }





    }
    //In main thread
    public void updateChunksMain(float camX, float camZ)
    {
        synchronized (updateChunks)
        {
            for(Chunk currChunk:updateChunks)
            {
                currChunk.createInstances(); //create instances for chunks
            }
        }


        updateChunks.clear(); //Reset update chunks
        worldModelInstance.clear(); //Prepare for new instances
        Iterator<Chunk> it = WorldLoadedChunksMap.values().iterator();
        while(it.hasNext())
        {
            Chunk currChunk = it.next();
            worldModelInstance.addAll(currChunk.getChnkIstances());
        }

    }

    public void setBlock(int x, int y, int z, short ID)
    {

        int chXChk,chZChk, inChXChk, inChYChk, inChZChk;

        chXChk = (x >= 0 ? x/16 : (x/16)-1);
        chZChk = (z >= 0 ? z/16 : (z/16)-1);

        inChXChk = (x >= 0 ? x%16 : (16 + x%16) & 0x0F);
        inChZChk = (z >= 0 ? z%16 : (16 + z%16) & 0x0F);

        inChYChk = (int)(y%Chunk.sizeY);

        //System.out.println("X : " + x + " Y : " + y + " z : " + z);
        //System.out.println("CX : " + chXChk + " CZ : " + chZChk + "\nIX : " + inChXChk + " IY : " + inChYChk + " IZ : " + inChZChk);


        Chunk tmpChunk = WorldLoadedChunksMap.get(new Vector2i(chXChk, chZChk));
        boolean foundAllReady = false;
        if(tmpChunk == null)
        {
            //WorldLoadedChunks.removeIndex(i);
            for(int j = 0; j < reGenChunks.size; j++)
            {
                if(reGenChunks.get(j).getPosX() == tmpChunk.getPosX() && reGenChunks.get(j).getPosZ() == tmpChunk.getPosZ())
                {
                    foundAllReady = true;
                }
            }
            if (!foundAllReady)
            {
                reGenChunks.add(tmpChunk);
            }
        }else{
            tmpChunk.setBlock(inChXChk,inChYChk,inChZChk,ID);
        }


    }

    public Array<ModelInstance> getInstances() {
        return worldModelInstance;
    }

    public Block getBlockObject(int chx, int chz, int icx, int icy,int icz)
    {
        Chunk tmpChunk = WorldLoadedChunksMap.get(new Vector2i(chx, chz));
        if(tmpChunk != null) {
            return tmpChunk.getBlockObject(icx, icy, icz);
        }else{
            return null;
        }
    }

    public Block getGlobalBlock(Vector3i pos)
    {
        return getGlobalBlock(pos.x, pos.y, pos.z);
    }
    public Block getGlobalBlock(int x, int y, int z)
    {

        int chXChk,chZChk, inChXChk, inChYChk, inChZChk;

        chXChk = (x >= 0 ? x/16 : ((x+1)/16)-1);
        chZChk = (z >= 0 ? z/16 : ((z+1)/16)-1);

        inChXChk = (x >= 0 ? x%16 : (16 + x%16) & 0x0F);
        inChZChk = (z >= 0 ? z%16 : (16 + z%16) & 0x0F);

        inChYChk = (int)(y%Chunk.sizeY);

        //System.out.print("X : " + x + " Y : " + y + " z : " + z);
        //System.out.println("  ====>   CX : " + chXChk + " CZ : " + chZChk + "\tIX : " + inChXChk + " IY : " + inChYChk + " IZ : " + inChZChk);

        //System.out.println("OK in calc for chunk pos!!: X : " + inChXChk + " Y : " + inChYChk + " Z : " + inChZChk + " CH X : " + chXChk + "CH Z : " + chZChk);

        if(y < 0)
        {
            return null;
        }


        return this.getBlockObject(chXChk, chZChk, inChXChk, inChYChk, inChZChk);
    }
}
