package io.github.myPackage;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.ModelLoader;
import com.badlogic.gdx.graphics.PerspectiveCamera;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g3d.*;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalShadowLight;
import com.badlogic.gdx.graphics.g3d.environment.PointLight;
import com.badlogic.gdx.graphics.g3d.loader.G3dModelLoader;
import com.badlogic.gdx.graphics.g3d.model.NodePart;
import com.badlogic.gdx.graphics.g3d.shaders.DefaultShader;
import com.badlogic.gdx.graphics.g3d.utils.*;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.JsonReader;

import javax.swing.*;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;
import java.util.Arrays;

import static com.badlogic.gdx.graphics.GL20.GL_COLOR_BUFFER_BIT;
import static com.badlogic.gdx.graphics.GL20.GL_DEPTH_BUFFER_BIT;


/** {@link ApplicationListener} implementation shared by all platforms. */
public class Main extends ApplicationAdapter {
    public PerspectiveCamera cam;
    //public CameraInputController camController;
    private io.github.myPackage.FirstPersonCameraController camController;
    public Shader shader;
    public RenderContext renderContext;
    public Model model;
    public Environment environment;
    public Renderable renderable;
    public AssetManager assets;
    public ModelBatch modelBatch;
    public Array<ModelInstance> instances = new Array<ModelInstance>();
    public Array<ModelInstance> chunkInstances = new Array<ModelInstance>();
    public Mesh mesh;
    private BitmapFont font;
    private SpriteBatch spriteBatch;

    public ModelBatch shadowBatch;
    private World myWorld;
    private DirectionalShadowLight shadowLight;
    private DirectionalLight sun;
    private Thread updateThread;
    private ChunkUpdater updater;
    private ColorAttribute Ambient;
    private Player player;
    int shadowRes = 8192;
    @Override
    public void create () {


        modelBatch = new ModelBatch();
        font = new BitmapFont();
        spriteBatch = new SpriteBatch();
        environment = new Environment();
        Ambient = new ColorAttribute(ColorAttribute.AmbientLight, 0.1f, 0.1f, 0.1f, 1f);
        //environment.set(Ambient);
        sun = new DirectionalLight().set(1f, 0.8f, 0.3f, -0.5f, -1f, -0.3f);
        environment.add(sun);

        // Create a shadow light with specified resolution and frustum dimensions
        shadowLight = new DirectionalShadowLight(shadowRes*4, shadowRes*4, 600f, 600f, 0.1f, 100f);
        
        // Set the light direction and color
        shadowLight.set(0.0f, 0.0f, 0.0f, -0.5f, -1f, -0.3f);



        // Add the shadow light to your environment
        environment.add(shadowLight);
        // Set the shadow map in your environment
        environment.shadowMap = shadowLight;





        shadowBatch = new ModelBatch(new DepthShaderProvider());

        cam = new PerspectiveCamera(67, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        cam.position.set(2f, 10f, 2f);
        //cam.lookAt(0,0,0);
        cam.near = 0.1f;
        cam.far = 300f;
        cam.update();
        Gdx.gl.glEnable(GL20.GL_DEPTH_TEST);


        ModelLoader modelLoader = new G3dModelLoader(new JsonReader());
        model = modelLoader.loadModel(Gdx.files.internal("data/invader.g3dj"));
        ModelBuilder modelBuilder = new ModelBuilder(); // creates model build

        //instances.add(new ModelInstance(model));
        NodePart blockPart = model.getNode("ship").parts.get(0);

        renderable = new Renderable();
        blockPart.setRenderable(renderable);
        renderable.environment = environment;
        renderable.worldTransform.idt();

        renderContext = new RenderContext(new DefaultTextureBinder(DefaultTextureBinder.LRU, 1));
        shader = new DefaultShader(renderable);
        shader.init();

        System.out.println("Mem usage : " + (float)((Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory())/1024)/1024 + "MB");



        myWorld = new World(12);
        player = new Player(cam, myWorld);






        System.out.println("Mem usage : " + (float)((Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory())/1024)/1024 + "MB");


        //    instances.add(new ModelInstance(model));
        myWorld.updateDrawnChunks(0,0);
        //Thread for updating
        updater = new ChunkUpdater(cam, myWorld);
        updateThread = new Thread(updater);
        updateThread.start();


        //chunkInstances = updater.getupdatedInstances().get();

        //NodePart blockPart = model.getNode("plane").parts.get(0);

    }


    private float dir  = 0;
    @Override
    public void render () {


        x++;
        dir += 0.01f;
         shadowLight.setDirection(-0.5f, -1f, dir);
        //Ambient.color.r = dir;
        if(dir >= 1)
        {
            dir = -1;
        }
        //System.out.println(dir);

        myWorld.updateChunksModels(cam.position.x/16,cam.position.z/16);
        chunkInstances = myWorld.getInstances();
        player.updatePlayer();

        //create shadow texture
        //shadowLight.begin(Vector3.Zero, cam.direction);
        //shadowBatch.begin(shadowLight.getCamera());

        //shadowBatch.render(instances);

        //shadowBatch.end();
        //shadowLight.end();

        // Begin shadow rendering
        shadowLight.begin(Vector3.Zero, cam.direction);
        shadowBatch.begin(shadowLight.getCamera());
        shadowBatch.render(chunkInstances, environment);
        shadowBatch.end();
        shadowLight.end();

        Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        Gdx.gl.glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

        modelBatch.begin(cam);
        //modelBatch.render(instances, environment);
        modelBatch.render(chunkInstances , environment);
        modelBatch.end();

        spriteBatch.begin();
        font.draw(spriteBatch, "FPS: " + Gdx.graphics.getFramesPerSecond(), 10, Gdx.graphics.getHeight() - 10);

        font.draw(spriteBatch, "Camera X: " + cam.position.x, 10, Gdx.graphics.getHeight() - 20);
        font.draw(spriteBatch, "Camera Y: " + cam.position.y, 10, Gdx.graphics.getHeight() - 36);
        font.draw(spriteBatch, "Camera Z: " + cam.position.z, 10, Gdx.graphics.getHeight() - 52);
        font.draw(spriteBatch, "Mem: " + (float)((Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory())/1024)/1024 + "MB", 10, Gdx.graphics.getHeight() - (52+16));
        font.draw(spriteBatch, "Camera Cx: " + (int)cam.position.x/16 + "in ch x" + cam.position.x%16, 10, Gdx.graphics.getHeight() - (52+32));
        spriteBatch.end();
    }

    @Override
    public void dispose () {
        modelBatch.dispose();
        instances.clear();
        model.dispose();
        updater.stop();

    }

    private int x;
    public void resume () {

    }

    public void resize (int width, int height) {
    }

    public void pause () {
    }

}
