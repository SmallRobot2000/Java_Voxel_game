/*******************************************************************************
 * Copyright 2011 See AUTHORS file.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/

package io.github.myPackage;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.IntIntMap;

/** Takes a {@link Camera} instance and controls it via w,a,s,d and mouse panning.
 * @author badlogic */
public class FirstPersonCameraController extends InputAdapter {
    protected final Camera camera;
    protected final IntIntMap keys = new IntIntMap();
    public int strafeLeftKey = Keys.A;
    public int strafeRightKey = Keys.D;
    public int forwardKey = Keys.W;
    public int backwardKey = Keys.S;
    public int upKey = Keys.SPACE;
    public int downKey = Keys.E;
    public boolean autoUpdate = true;
    protected float velocity = 5;
    protected float degreesPerPixel = 0.5f;
    protected final Vector3 tmp = new Vector3();
    private boolean cursorChaced;
    private boolean ESCtogle = false;
    private boolean ESC_pressed = false, ESC_pressed_old = false;
    private float accG = 9.81f;
    private float velocityDown;

    public boolean canUp = false, inFall = true, canXp = false, canXn = false, canZp = false, canZn = false, shoudHop = false;
    public FirstPersonCameraController (Camera camera) {
        this.camera = camera;
        cursorChaced = true;
        Gdx.input.setCursorCatched(cursorChaced);
        Gdx.input.setCursorPosition(0, 0);
    }

    @Override
    public boolean keyDown (int keycode) {
        keys.put(keycode, keycode);
        return true;
    }

    @Override
    public boolean keyUp (int keycode) {
        keys.remove(keycode, 0);
        return true;
    }



    /** Sets the velocity in units per second for moving forward, backward and strafing left/right.
     * @param velocity the velocity in units per second */
    public void setVelocity (float velocity) {
        this.velocity = velocity;
    }

    /** Sets how many degrees to rotate per pixel the mouse moved.
     * @param degreesPerPixel */
    public void setDegreesPerPixel (float degreesPerPixel) {
        this.degreesPerPixel = degreesPerPixel;
    }

    @Override
    public boolean mouseMoved (int screenX, int screenY) {
        float deltaX = -Gdx.input.getDeltaX() * degreesPerPixel;
        float deltaY = -Gdx.input.getDeltaY() * degreesPerPixel;

        if(cursorChaced)
        {
            camera.direction.rotate(camera.up, deltaX);
            tmp.set(camera.up).crs(camera.direction).nor();
            camera.direction.rotate(tmp, -deltaY);
        }


        return true;
    }

    public void update () {
        update(Gdx.graphics.getDeltaTime());
    }

    public void update (float deltaTime) {

        if(shoudHop)
        {
            camera.position.y += 0.5f;
        }
        if(inFall)
        {
            velocityDown += accG * deltaTime;
        }else{
            velocityDown = 0;
        }

        boolean canForwardX = false;
        if(camera.direction.x > 0)
        {
            canForwardX = canXp;
        }

        if(camera.direction.x < 0)
        {
            canForwardX = canXn;
        }


        boolean canBackwarsX = false;
        if(camera.direction.x < 0)
        {
            canBackwarsX = canXp;
        }

        if(camera.direction.x > 0)
        {
            canBackwarsX = canXn;
        }

        boolean canForwardZ = false;
        if(camera.direction.z > 0)
        {
            canForwardZ = canZp;
        }


        if(camera.direction.z < 0)
        {
            canForwardZ = canZn;
        }



        boolean canBackwarsZ = false;
        if(camera.direction.z < 0)
        {
            canBackwarsZ = canZp;
        }


        if(camera.direction.z > 0)
        {
            canBackwarsZ = canZn;
        }

        boolean canLeftX = false;

        if(tmp.set(camera.direction).crs(camera.up).nor().x < 0)
        {
            canLeftX = canXp;
        }


        if(tmp.set(camera.direction).crs(camera.up).nor().x > 0)
        {
            canLeftX = canXn;
        }

        boolean canLeftZ = false;

        if(tmp.set(camera.direction).crs(camera.up).nor().z < 0)
        {
            canLeftZ = canZp;
        }


        if(tmp.set(camera.direction).crs(camera.up).nor().z > 0)
        {
            canLeftZ = canZn;
        }


        boolean canRightX = false;

        if(tmp.set(camera.direction).crs(camera.up).nor().x > 0)
        {
            canRightX = canXp;
        }


        if(tmp.set(camera.direction).crs(camera.up).nor().x < 0)
        {
            canRightX = canXn;
        }

        boolean canRightZ = false;

        if(tmp.set(camera.direction).crs(camera.up).nor().z > 0)
        {
            canRightZ = canZp;
        }


        if(tmp.set(camera.direction).crs(camera.up).nor().z < 0)
        {
            canRightZ = canZn;
        }

        if (keys.containsKey(forwardKey)) {
            tmp.set(camera.direction).nor();
            tmp.y = 0;
            tmp.x = tmp.x * (canForwardX ? 1f : 0f);
            tmp.z = tmp.z * (canForwardZ ? 1f : 0f);
            tmp.nor().scl(deltaTime * velocity);
            camera.position.add(tmp);
            //System.out.println(camera.direction);
        }
        if (keys.containsKey(backwardKey)) {
            tmp.set(camera.direction).nor();
            tmp.y = 0;
            tmp.x = tmp.x * (canBackwarsX ? 1f : 0f);
            tmp.z = tmp.z * (canBackwarsZ ? 1f : 0f);
            tmp.nor().scl(deltaTime * -velocity);
            camera.position.add(tmp);
        }
        if (keys.containsKey(strafeLeftKey)) {
            tmp.set(camera.direction).crs(camera.up).nor();
            tmp.y = 0;
            tmp.x = tmp.x * (canLeftX ? 1f : 0f);
            tmp.z = tmp.z * (canLeftZ ? 1f : 0f);
            tmp.nor().scl(deltaTime * -velocity);
            camera.position.add(tmp);
        }
        if (keys.containsKey(strafeRightKey)) {
            tmp.set(camera.direction).crs(camera.up).nor();
            tmp.y = 0;
            tmp.x = tmp.x * (canRightX ? 1f : 0f);
            tmp.z = tmp.z * (canRightZ ? 1f : 0f);
            tmp.nor().scl(deltaTime * velocity);
            camera.position.add(tmp);
        }
        if (keys.containsKey(upKey) && canUp) {
            velocityDown += -5;
        }
        if (keys.containsKey(downKey)) {
            tmp.set(camera.up).nor().scl(-deltaTime * velocity);
            camera.position.add(tmp);
        }

        tmp.set(camera.up).nor().scl(-deltaTime * velocityDown);
        camera.position.add(tmp);
        if (keys.containsKey(Keys.ESCAPE)) {
            ESC_pressed = true;
        }else{
            ESC_pressed = false;
        }

        if(ESC_pressed_old == false && ESC_pressed == true)
        {
            cursorChaced = !cursorChaced;

            Gdx.input.setCursorCatched(cursorChaced);
            if(!cursorChaced)
            {
                Gdx.input.setCursorPosition(Gdx.graphics.getWidth()/2, Gdx.graphics.getHeight()/2);
            }
        }

        ESC_pressed_old = ESC_pressed;
        if (autoUpdate) camera.update(true);


    }
}
