package io.github.myPackage;

import java.util.Arrays;

public class BlockSet {
    private int defaultTextureID = 0;
    private int textureID_front = defaultTextureID;
    private int textureID_back = defaultTextureID;
    private int textureID_top = defaultTextureID;
    private int textureID_bottom = defaultTextureID;
    private int textureID_right = defaultTextureID;
    private int textureID_left = defaultTextureID;

    private int[] textureSides;
    public BlockSet()
    {
        textureSides = new int[6];
        updateTextureSides();
    }
    public BlockSet(int DefaultTextureID)
    {
        this.defaultTextureID = DefaultTextureID;
        textureSides = new int[6];
        updateTextureSides();
    }
    public void setTextureIDs(int textureID_front, int textureID_back, int textureID_top, int textureID_bottom, int textureID_right, int textureID_left) {
        this.textureID_back = textureID_back;
        this.textureID_front = textureID_front;
        this.textureID_top = textureID_top;
        this.textureID_bottom = textureID_bottom;
        this.textureID_right = textureID_right;
        this.textureID_left = textureID_left;

        updateTextureSides();

    }

    private void updateTextureSides()
    {
        textureSides[0] = this.textureID_bottom;
        textureSides[1] = this.textureID_top;
        textureSides[2] = this.textureID_left;
        textureSides[3] = this.textureID_right;
        textureSides[4] = this.textureID_front;
        textureSides[5] = this.textureID_back;
    }

    public int[] getTextureSides() {
        return textureSides;
    }

    public int getTextureID_back() {
        return textureID_back;
    }

    public int getTextureID_bottom() {
        return textureID_bottom;
    }

    public int getTextureID_front() {
        return textureID_front;
    }

    public int getTextureID_left() {
        return textureID_left;
    }

    public int getTextureID_right() {
        return textureID_right;
    }

    public int getTextureID_top() {
        return textureID_top;
    }

    public int getDefaultTextureID() {
        return defaultTextureID;
    }
}
