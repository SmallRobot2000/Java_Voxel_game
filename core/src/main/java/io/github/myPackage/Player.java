package io.github.myPackage;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;

import java.awt.*;
//TODO better physics for player
public class Player {
    float x,y,z;
    Camera cam;
    World world;
    private float playerPosUnderCam = 1.75f;
    private final FirstPersonCameraController camController;
    private Array<ModelInstance> instanceArray;
    private Model cube;
    public Vector3 playerPos = new Vector3(0, 10, 0);
    public Vector3 playerPosBlock = playerPos;
    private Vector3 wantedDir;
    private float playerFeetOffToCam = 1.75f;
    private float velocityH = 0;
    private float velocityV = 0;
    private final float velocityHCap = 20f;
    private final float velocityVCap = 30f;
    private float Gacc = 9.81f;
    private float horizontalAcc = 5*Gacc;
    private float horizontalDeAcc = 3*Gacc;

    private Vector3 tmp = new Vector3();
    private Vector3 tmpWantedDir = new Vector3();
    private Vector3 velocity = new Vector3();
    Player(Camera cm, World wrld, Array<ModelInstance> instances)
    {
        this.cam = cm;
        if (wrld == null) {
            throw new IllegalArgumentException("World cannot be null!");
        }
        this.world = wrld;
        camController = new FirstPersonCameraController(cam);
        Gdx.input.setInputProcessor(camController);
        this.instanceArray = instances;

        ModelBuilder mb = new ModelBuilder();
        cube = mb.createBox(1.2f, 1.2f, 1.2f, new Material(ColorAttribute.createDiffuse(Color.BLUE)),
            VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal);

        this.cam.position.x = playerPos.x;
        this.cam.position.y = playerPos.y-playerFeetOffToCam;
        this.cam.position.z = playerPos.z;
    }

    public void updatePlayer() {
        float playerOff = 0.25f;
        float deltaTime = Gdx.graphics.getDeltaTime();

        camController.update();
        wantedDir = camController.wantedDir;

        if(wantedDir.x != 0 || wantedDir.y != 0 || wantedDir.z != 0)
        {
            velocity.add(wantedDir.nor().scl(horizontalAcc*deltaTime));

        }else{

            tmp.set(velocity);
            tmp.nor().scl(horizontalDeAcc*deltaTime);
            velocity.sub(tmp);
            if(Math.abs(velocity.x) < 0.5 && Math.abs(velocity.y) < 0.5 && Math.abs(velocity.z) < 0.5)
            {
                velocity.setZero();
            }
            tmp.set(velocity);


        }
        //System.out.println(tmpWantedDir);


        //velocity.clamp(-velocityHCap, velocityHCap);
        velocity.limit(velocityHCap);
        tmp.set(velocity);
        playerPos.add(tmp.scl(deltaTime));
        cam.position.set(playerPos).add(0, playerFeetOffToCam, 0);






        Vector3 looking = lookingAt();
        instanceArray.clear();
        if(looking != null)
        {
            ModelInstance cubeMI = new ModelInstance(cube);


            looking = new Vector3((int)looking.x, (int)looking.y, (int)looking.z);

            cubeMI.transform.set(new Vector3((int)(looking.x+0.75f), (int)(looking.y)+0.5f, (int)(looking.z)+0.5f),new Quaternion(0,0,0,0));

            instanceArray.add(cubeMI);

            if(world.getGlobalBlockID((int)looking.x,(int)looking.y,(int)looking.z) != 0 && camController.button == Input.Buttons.LEFT)
            {
                world.setBlock((int)looking.x,(int)looking.y,(int)looking.z,(short)0);
            }


        }
        //System.out.println(looking);

        camController.update(); // One more time because FOV and skybox breaks
    }
    private final float max_distance = 5;
    private Vector3 lookingAt()
    {

        //Put cam position to cam_pos
        Vector3 cam_pos = cam.position;
        // Initialize grid position
        float x = (float)Math.floor(cam.position.x);
        float y = (float)Math.floor(cam.position.y);
        float z = (float)Math.floor(cam.position.z);


        float step_x = cam.direction.x > 0 ? 1 : -1;
        float step_y = cam.direction.y > 0 ? 1 : -1;
        float step_z = cam.direction.z > 0 ? 1 : -1;

        //Compute t_max and t_delta
        float t_max_x = (x + step_x - cam_pos.x) / cam.direction.x;
        float t_max_y = (y + step_y - cam_pos.y) / cam.direction.y;
        float t_max_z = (z + step_z - cam_pos.z) / cam.direction.z;

        float t_delta_x = step_x / cam.direction.x;
        float t_delta_y = step_y / cam.direction.y;
        float t_delta_z = step_z / cam.direction.z;

        float current_t = 0;
        while (current_t < max_distance) {
            if (world.getGlobalBlockID((int)x, (int)y, (int)z) != 0) {
                return new Vector3((float) cam.direction.x * current_t + cam_pos.x, (float) cam.direction.y * current_t + cam_pos.y, (float) cam.direction.z * current_t + cam_pos.z);
            }

            //Step to next block
            if (t_max_x < t_max_y && t_max_x < t_max_z) {
                x += step_x;
                current_t = t_max_x;
                t_max_x += t_delta_x;
            } else if (t_max_y < t_max_z) {
                y += step_y;
                current_t = t_max_y;
                t_max_y += t_delta_y;
            } else {
                z += step_z;
                current_t = t_max_z;
                t_max_z += t_delta_z;
            }
        }
        return null;
    }
}
