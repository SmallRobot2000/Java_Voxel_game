package io.github.myPackage;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;

public class Player {
    float x,y,z;
    Camera cam;
    World world;
    private float playerPosUnderCam = 1.75f;
    private final FirstPersonCameraController camController;
    Player(Camera cm, World wrld)
    {
        this.cam = cm;
        if (wrld == null) {
            throw new IllegalArgumentException("World cannot be null!");
        }
        this.world = wrld;
        camController = new FirstPersonCameraController(cam);
        Gdx.input.setInputProcessor(camController);
    }

    public void updatePlayer() {
        float playerOff = 0.25f;
        float playerFeet = -2f;
        float playerBottom = playerFeet+1;
        float chkX, chkZ, chkY;



        chkX = cam.position.x;
        chkZ = cam.position.z;
        chkY = cam.position.y-1;

        camController.canXp = world.getGlobalBlockID(chkX+playerOff,chkY,chkZ) == 0;

        camController.canXn = world.getGlobalBlockID(chkX-playerOff,chkY,chkZ) == 0;

        camController.canZp = world.getGlobalBlockID(chkX,chkY,chkZ+playerOff) == 0;

        camController.canZn = world.getGlobalBlockID(chkX,chkY,chkZ-playerOff) == 0;


        short ID = world.getGlobalBlockID(cam.position.x,cam.position.y+playerFeet,cam.position.z);
        //System.out.println("CHx " + chX + " CHz " + chZ + " Inx " + inChX +  " Iny " + inChY +  " CHz " + inChZ + "ID" + ID);

        camController.shoudHop = false;
        if(ID != 0)
        {
            //System.out.println("YES");
            camController.canUp = true;
            camController.inFall = false;
        }else{
            camController.canUp = false;
            camController.inFall = true;
        }
        if(world.getGlobalBlockID(cam.position.x,cam.position.y+playerBottom,cam.position.z) != 0)
        {
            camController.shoudHop = true;
        }

        camController.update();

    }
}
