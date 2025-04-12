package io.github.myPackage;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.Shader;
import com.badlogic.gdx.utils.Array;

import javax.swing.*;
import java.util.concurrent.atomic.AtomicReference;


// 1. Create a dedicated thread class
public class ChunkUpdater implements Runnable {
    private volatile boolean isRunning = true;
    private final Camera cam;


    private final AtomicReference<Array<ModelInstance>> updatedInstances = new AtomicReference<>();
    private final World ThisWorld;
    private Array<ModelInstance> tmpInstances = new Array<ModelInstance>();
    public ChunkUpdater(Camera camera, World myWolrd) {

        this.cam = camera;
        this.ThisWorld =  myWolrd;
    }

    public AtomicReference<Array<ModelInstance>> getupdatedInstances() {
        return updatedInstances;
    }

    @Override
    public void run() {

        while (isRunning) {
            // 2. Synchronize camera position access
            float x, z;
            synchronized(cam) {
                x = cam.position.x / 16;
                z = cam.position.z / 16;
            }

            synchronized (ThisWorld) {
                //ThisWorld.getChunk((int)cam.position.x/16,(int)cam.position.z/16).createVI();
                ThisWorld.updateChunksVI(cam.position.x /16,cam.position.z/16);
            }

            // 3. Update chunks and store instances







            //updatedInstances.set(tmpInstances);

            // 4. Add delay to prevent CPU overload
            try {
                Thread.sleep(1000/20); // Adjust based on needs
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    public void stop() {
        isRunning = false;
    }
}
