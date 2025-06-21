package io.github.myPackage;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;

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
    private final float velocityHCap = 0.05f;
    private final float velocityVCap = 5f;
    private float Gacc = 0.1f;
    private float horizontalAcc = 0.2f;
    private float horizontalDeAcc = 0.5f;
    private final float velocityCap = 0.5f;
    private float jumpAcc = 0.05f;
    private final float playerRadius = 0.5f;
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

    Vector3 oldPlayerPos = playerPos;
    public void updatePlayer() {
        float playerOff = 0.25f;
        float deltaTime = Gdx.graphics.getDeltaTime();

        camController.update();
        wantedDir = camController.wantedDir;

        Vector3 Hvelocity = new Vector3(new Vector3(wantedDir.x,0,wantedDir.z).nor().scl(horizontalAcc));
        Hvelocity.y = 0; //For horizontal velocity only


        velocity.add(Hvelocity.scl(deltaTime));

        tmp.set(velocity);
        tmp.y = 0;
        tmp.nor().scl(horizontalDeAcc * deltaTime);

        Vector3 tmpVelocity = new Vector3(velocity);
        tmpVelocity.sub(tmp);
        Vector3 prevVelocity = new Vector3(velocity).nor();
        Vector3 tmpVelNor = new Vector3(tmpVelocity).nor();
        if (prevVelocity.dot(tmpVelNor) < 0 && Hvelocity.x ==0 && Hvelocity.z == 0) {
            // If the dot product is negative, it means the velocity is in the opposite direction
            // We can set the velocity to zero or adjust it accordingly
            velocity.set(0, velocity.y, 0);
        } else {
            // Otherwise, we can safely subtract the horizontal deacceleration
            if(Hvelocity.x == 0 && Hvelocity.z == 0) {
                velocity.sub(tmp);
            }
        }

        if(!isOnGround(new Vector3(playerPos).add(velocity)))
        {
            velocity.y -= Gacc*deltaTime; // Apply gravity only if not on ground
        }else{
            velocity.y = 0;
            if(wantedDir.y != 0)
            {
                // If the player is on the ground and wants to jump
                velocity.y = wantedDir.y * jumpAcc; // Jumping velocity
            }
        }

        //velocity.y -= Gacc*deltaTime;



        //if(Math.abs(velocity.x) < 0.5)
        //{
        //    // If the velocity is very small, set it to zero to avoid jittering
        //    velocity.x = 0;
        //}
        //if(Math.abs(velocity.z) < 0.5)
        //{
        //    // If the velocity is very small, set it to zero to avoid jittering
        //    velocity.z = 0;
        //}
        //if(Math.abs(velocity.y) < 0.5)
        //{
        //    // If the velocity is very small, set it to zero to avoid jittering
        //    velocity.y = 0;
        //}
        //System.out.println(tmpWantedDir);


        //velocity.clamp(-velocityHCap, velocityHCap);
        Vector3 velocityHorizontal = new Vector3(velocity.x, 0, velocity.z);
        velocityHorizontal.limit(velocityHCap);
        velocity = new Vector3(velocityHorizontal.x, velocity.y, velocityHorizontal.z);
        velocity.limit(velocityCap);
        tmp.set(velocity);

        Vector3 newPlayerPos = new Vector3(playerPos).add(tmp);

        // Check for collision at new position
        if (checkSolid(newPlayerPos)) {

            // Try moving only along X
            Vector3 tryX = new Vector3(playerPos).add(tmp.x, tmp.y, 0);
            if (!checkSolid(tryX)) {
                playerPos.set(tryX);
                velocity.z = 0; // Reset Z velocity if X movement is successful
            } else {
                // Try moving only along Z
                Vector3 tryZ = new Vector3(playerPos).add(0, tmp.y, tmp.z);
                if (!checkSolid(tryZ)) {
                    playerPos.set(tryZ);
                    velocity.x = 0; // Reset X velocity if Z movement is successful
                }
                // Optionally, try Y if you want vertical sliding
            }
            // If all fail, stay in place (no movement)
        } else {
            // No collision, move freely
            playerPos.add(tmp);
        }
        //playerPos.add(tmp);
        if (isOnGround(playerPos)) {
            playerPos.y = Math.round(playerPos.y);
        }

        cam.position.set(playerPos).add(0, playerFeetOffToCam, 0);






        /*Vector3 looking = lookingAt();
        instanceArray.clear();
        if(looking != null)
        {
            ModelInstance cubeMI = new ModelInstance(cube);


            looking = new Vector3((int)looking.x, (int)looking.y, (int)looking.z);

            cubeMI.transform.set(new Vector3((int)(looking.x+0.75f), (int)(looking.y)+0.5f, (int)(looking.z)+0.5f),new Quaternion(0,0,0,0));

            instanceArray.add(cubeMI);

            if(((world.getGlobalBlock((int)x, (int)y, (int)z) != null) && (((Block)world.getGlobalBlock((int)x, (int)y, (int)z)).canBePlacedOn)) && camController.button == Input.Buttons.LEFT)
            {
                world.setBlock((int)looking.x,(int)looking.y,(int)looking.z,(short)0);
            }


        }*/
        //System.out.println(looking);

        camController.update(); // One more time because FOV and skybox breaks

        //oldPlayerPos = new Vector3(playerPos);
    }
    private boolean isOnGround(Vector3 playerPos) {
        // Check if the player is on the ground by checking the block directly below
        Vector3i blockBelow = new Vector3i((int) playerPos.x, (int) (playerPos.y - 0.02), (int) playerPos.z);
        Block block = world.getGlobalBlock(blockBelow);
        return block != null && block.fullBlock;
    }
    private final int rayIterationsMax = 100;
    private final float rayIncrement = 0.1f;
    boolean checkForCollision(Vector3 playerPos)
    {
        Vector3 newPlayerPos;
        Block touchingBlock = world.getGlobalBlock(new Vector3i((int)playerPos.x,(int)playerPos.y,(int)playerPos.z));
        if(touchingBlock == null)
        {
            return false;

        }else if(touchingBlock.fullBlock){
            return true;
        }else{
            return false;
        }
    }
    private boolean checkSolid(Vector3 pos)
    {
        Vector3 workingVector = new Vector3(pos);

        Block workingBlock = world.getGlobalBlock(transformPosFloatToInt(workingVector));
        if(workingBlock != null && workingBlock.isColliding(workingVector.x, workingVector.y, workingVector.z))
        {
            return true;
        }
        return false;
    }
    public static Vector3 evaluate(Vector3 origin, Vector3 lastPoint, float t) {
        // Calculate direction vector from origin to lastPoint
        Vector3 direction = lastPoint.sub(origin);

        // Return the interpolated point: origin + t * direction
        return origin.add(direction.sub(t));
    }
    private Vector3i transformPosFloatToInt(Vector3 pos)
    {
        Vector3i tmp = new Vector3i();
        tmp.x = pos.x >= 0 ? (int)pos.x : (int)pos.x-1;
        tmp.y = pos.y >= 0 ? (int)pos.y : (int)pos.y-1;
        tmp.z = pos.z >= 0 ? (int)pos.z : (int)pos.z-1;

        //System.out.println(pos + " ===> " + tmp);

        return tmp;
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
            if ((world.getGlobalBlock((int)x, (int)y, (int)z) != null) && (((Block)world.getGlobalBlock((int)x, (int)y, (int)z)).canBePlacedOn)) {
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
