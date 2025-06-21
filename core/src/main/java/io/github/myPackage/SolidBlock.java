package io.github.myPackage;

import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;

public class SolidBlock extends Block{

    public boolean canBePlacedOn = true;
    protected boolean fullBlock = true;
    //public Vector2[] textureUVS; //8 points per block
    //private TextureUV[] textureUVS;
    //TODO better mesh gen
    Vector3[] Normals = {
        new Vector3(0,-1,0),
        new Vector3(0,1,0),
        new Vector3(-1,0,0),
        new Vector3(1,0,0),
        new Vector3(0,0,-1),
        new Vector3(0,0,1),
    };

    float[] xAdd = {0,1,1,0,0,1,1,0};
    float[] yAdd = {0,0,0,0,1,1,1,1};
    float[] zAdd = {0,0,1,1,0,0,1,1};

    SolidBlock(boolean invisible, boolean fullBlock, Vector3i position, long blockID, long blockTextureID)
    {
        super.invisible = invisible;
        super.fullBlock = fullBlock;
        super.position = position;
        super.blockID = blockID;
        super.blockTextureID = blockTextureID;
    }
    @Override
    int generateMeshData(boolean xp, boolean xn, boolean yp, boolean yn, boolean zp, boolean zn, int curIndx, TextureUV[] textureUVS)
    {
        //this.textureUVS = textureUVS;
        vertices = new Array<>();
        indices = new Array<>();

        int normal = 0;

        int x = position.x;
        int y = position.y;
        int z = position.z;
        int curBlockIndex = 0;

        short blockIndexTextureOffset = (short) (blockTextureID*24-24); //So it starts from 0 (1 = 0; 0 is air)
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

        }

        return  curIndx;
    }
}
