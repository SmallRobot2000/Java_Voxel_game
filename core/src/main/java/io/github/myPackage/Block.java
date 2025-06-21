package io.github.myPackage;

import com.badlogic.gdx.utils.Array;

public class Block {
    public Vector3i position = new Vector3i();
    public long blockTextureID = 0;
    public long blockID = 0;
    protected boolean fullBlock = false;
    public Array<Float> vertices;
    public Array<Short> indices;

    //these are true if can be visible and false if ALWAYS visible
    public final boolean xpCanVisible = true;
    public final boolean xnCanVisible = true;

    public final boolean ypCanVisible = true;
    public final boolean ynCanVisible = true;

    public final boolean zpCanVisible = true;
    public final boolean znCanVisible = true;
    public boolean canBePlacedOn = false;
    protected boolean invisible = false;
    boolean isColliding(float x, float y, float z)  /// x,y,z values in world coordinates that are in the block
    {

        return fullBlock;

    }

    int generateMeshData(boolean xp, boolean xn, boolean yp, boolean yn, boolean zp, boolean zn, int curIndx, TextureUV[] textureUVS)
    {
        return -1;
    }
}
