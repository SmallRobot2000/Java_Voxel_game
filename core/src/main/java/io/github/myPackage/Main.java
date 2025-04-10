package io.github.myPackage;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.ModelLoader;
import com.badlogic.gdx.graphics.PerspectiveCamera;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Gdx.*;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g3d.*;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.CubemapAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalShadowLight;
import com.badlogic.gdx.graphics.g3d.environment.PointLight;
import com.badlogic.gdx.graphics.g3d.loader.G3dModelLoader;
import com.badlogic.gdx.graphics.g3d.model.NodePart;
import com.badlogic.gdx.graphics.g3d.shaders.DefaultShader;
import com.badlogic.gdx.graphics.g3d.utils.*;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.JsonReader;

import javax.swing.*;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;
import java.util.Arrays;

import static com.badlogic.gdx.graphics.GL20.*;


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
        environmentInit();
        worldInit();
        playerInit();
        updaterInit();
    }
    private float dir  = 0;
    @Override
    public void render () {


        x++;
        dir += 0.01f;
        //shadowLight.setDirection(-0.5f, -1f, dir);
        //Ambient.color.r = dir;
        if(dir >= 1)
        {
            dir = -1;
        }
        //System.out.println(dir);
        worldUpdate();
        player.updatePlayer();



        render3D();

        renderScreenText2D();

    }
    private void renderScreenText2D()
    {
        spriteBatch.begin();
        font.draw(spriteBatch, "FPS: " + Gdx.graphics.getFramesPerSecond(), 10, Gdx.graphics.getHeight() - 10);

        font.draw(spriteBatch, "Camera X: " + cam.position.x, 10, Gdx.graphics.getHeight() - 20);
        font.draw(spriteBatch, "Camera Y: " + cam.position.y, 10, Gdx.graphics.getHeight() - 36);
        font.draw(spriteBatch, "Camera Z: " + cam.position.z, 10, Gdx.graphics.getHeight() - 52);
        font.draw(spriteBatch, "Mem: " + (float)((Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory())/1024)/1024 + "MB", 10, Gdx.graphics.getHeight() - (52+16));
        font.draw(spriteBatch, "Camera Cx: " + (int)cam.position.x/16 + "in ch x" + cam.position.x%16, 10, Gdx.graphics.getHeight() - (52+32));
        spriteBatch.end();
    }
    private void render3D()
    {
        //finalBuffer.begin();
        sceneBuffer.begin();

        // Disable depth testing for the skybox






        Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        Gdx.gl.glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        Gdx.gl.glEnable(GL_DEPTH_TEST);
        modelBatch.begin(cam);
        modelBatch.render(chunkInstances , environment);
        skyBox.render(cam);
        modelBatch.end();

        sceneBuffer.end();

        //finalBuffer.end();

/*
        finalBuffer.begin();
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);


        // Bind both scene color and SSAO textures
        sceneBuffer.getColorBufferTexture().bind(0);
        ssaoBuffer.getColorBufferTexture().bind(1);

        compositeShader.bind();
        compositeShader.setUniformi("u_sceneTexture", 0);
        compositeShader.setUniformi("u_ssaoTexture", 1);

        // Render full-screen quad with composite shader
        ScreenQuad.render(compositeShader, GL20.GL_TRIANGLES);

        finalBuffer.end();
*/
        // Render finalBuffer to screen

        frameBufferShader.bind();
        frameBufferShader.setUniformi("u_texture", 0);
        sceneBuffer.getColorBufferTexture().bind(0);

        ScreenQuad.render(frameBufferShader, GL20.GL_TRIANGLES);


    }
    private void worldUpdate()
    {
        myWorld.updateChunksModels(cam.position.x/16,cam.position.z/16);
        chunkInstances = myWorld.getInstances();
    }
    private void updaterInit() {
        updater = new ChunkUpdater(cam, myWorld);
        updateThread = new Thread(updater);
        updateThread.start();
    }

    private void worldInit() {
        myWorld = new World(12);
        myWorld.updateDrawnChunks(0,0);
    }

    private void playerInit() {
        player = new Player(cam, myWorld);
    }

    private void environmentInit() {

        modelBatch = new ModelBatch();
        font = new BitmapFont();
        spriteBatch = new SpriteBatch();
        environment = new Environment();
        Ambient = new ColorAttribute(ColorAttribute.AmbientLight, 0.08f, 0.05f, 0.05f, 1f);
        environment.set(Ambient);
        sun = new DirectionalLight().set(1f, 0.8f, 0.6f, -0.5f, -1f, -0.3f);
        environment.add(sun);


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




        initShaders();

        skyBox = new SkyBox(new Pixmap(Gdx.files.internal("textures/skybox-texture.png")));




        ScreenQuad = createFullscreenQuad();

        // Render a full-screen quad with the SSAO effect

        ScreenQuad.render(ssaoShader, GL20.GL_TRIANGLES);

        System.out.println("Mem usage : " + (float)((Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory())/1024)/1024 + "MB");


    }
    private SkyBox skyBox;
    private FrameBuffer sceneBuffer;
    private FrameBuffer ssaoBuffer;
    private FrameBuffer finalBuffer;
    private Mesh ScreenQuad; // Create a simple full-screen quad mesh
    private ShaderProgram ssaoShader;
    private ShaderProgram compositeShader;
    private ShaderProgram frameBufferShader;
    private ShaderProgram skyboxShader;
    private void initShaders()
    {
        renderContext = new RenderContext(new DefaultTextureBinder(DefaultTextureBinder.LRU, 1));
        shader = new DefaultShader(renderable);
        shader.init();



        ssaoShader = new ShaderProgram(Gdx.files.internal("shaders/ssao.vert"), Gdx.files.internal("shaders/ssao.frag"));


        if (!ssaoShader.isCompiled()) {
            throw new RuntimeException("SSAO Shader Compilation Failed: " + ssaoShader.getLog());
        }

        // Load shader files
        String compositeVert = Gdx.files.internal("shaders/composite.vert").readString();
        String compositeFrag = Gdx.files.internal("shaders/composite.frag").readString();

        compositeShader = new ShaderProgram(compositeVert, compositeFrag);

        // Check for errors
        if (!compositeShader.isCompiled()) {
            throw new RuntimeException("Composite shader failed: " + compositeShader.getLog());
        }


        String finalVert = Gdx.files.internal("shaders/post.vert").readString();
        String finalFrag = Gdx.files.internal("shaders/post.frag").readString();

        frameBufferShader = new ShaderProgram(finalVert, finalFrag);

        if (!frameBufferShader.isCompiled()) {
            throw new RuntimeException("Frame shader failed: " + frameBufferShader.getLog());
        }

        sceneBuffer = new FrameBuffer(Pixmap.Format.RGBA8888, Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true);

        ssaoBuffer = new FrameBuffer(Pixmap.Format.RGBA8888, Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), false);

        finalBuffer = new FrameBuffer(Pixmap.Format.RGBA8888, Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), false);
/*

        skyboxShader = new ShaderProgram(
            Gdx.files.internal("shaders/skybox.vert"),
            Gdx.files.internal("shaders/skybox.frag")
        );

        if (!skyboxShader.isCompiled()) {
            throw new RuntimeException("Skybox shader compilation failed: " + skyboxShader.getLog());
        }

*/
        // Bind the depth texture and set uniforms
        ssaoBuffer.getColorBufferTexture().bind(0);
        ssaoShader.bind();
        //ssaoShader.setUniformi("u_depthTexture", 0);
        //ssaoShader.setUniformf("u_resolution", Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        //ssaoShader.setUniformf("u_radius", 5.0f);



        // Create shader program



    }
    private Mesh createFullscreenQuad() {
        float[] vertices = {
            -1f, -1f, 0f, 0f, 0f,
            1f, -1f, 0f, 1f, 0f,
            1f,  1f, 0f, 1f, 1f,
            -1f,  1f, 0f, 0f, 1f,
        };

        short[] indices = {0, 1, 2, 2, 3, 0};

        Mesh mesh = new Mesh(true, vertices.length / 5, indices.length,
            new VertexAttribute(VertexAttributes.Usage.Position, 3, "a_position"),
            new VertexAttribute(VertexAttributes.Usage.TextureCoordinates, 2, "a_texCoord0"));

        mesh.setVertices(vertices);
        mesh.setIndices(indices);

        return mesh;
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
