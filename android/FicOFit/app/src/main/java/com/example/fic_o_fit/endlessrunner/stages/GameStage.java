package com.example.fic_o_fit.endlessrunner.stages;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Scaling;
import com.badlogic.gdx.utils.viewport.ScalingViewport;
import com.example.fic_o_fit.endlessrunner.EndlessRunner;
import com.example.fic_o_fit.endlessrunner.actors.Background;
import com.example.fic_o_fit.endlessrunner.actors.Ground;
import com.example.fic_o_fit.endlessrunner.actors.Runner;
import com.example.fic_o_fit.endlessrunner.utils.BodyUtils;
import com.example.fic_o_fit.endlessrunner.utils.WorldUtils;
import com.example.fic_o_fit.endlessrunner.actors.Enemy;


public class GameStage extends Stage implements ContactListener{

    // This will be our viewport measurements while working with the debug renderer
    private static final int VIEWPORT_WIDTH = 20;
    private static final int VIEWPORT_HEIGHT = 13;

    private World world;
    private Ground ground;
    private Runner runner;
    private boolean gameOver;

    private EndlessRunner endlessRunner;
//    private int userPoints;
    private SpriteBatch batch;
    private BitmapFont font;

    private final float TIME_STEP = 1 / 300f;
    private float accumulator = 0f;

    private OrthographicCamera camera;
    private Box2DDebugRenderer renderer;

    public GameStage(EndlessRunner endlessRunner) {
        super(new ScalingViewport(Scaling.stretch, VIEWPORT_WIDTH, VIEWPORT_HEIGHT,
                new OrthographicCamera(VIEWPORT_WIDTH, VIEWPORT_HEIGHT)));
        this.endlessRunner = endlessRunner;
        batch = new SpriteBatch();
        font = new BitmapFont();
        gameOver = false;
        setUpWorld();
        setupCamera();
        renderer = new Box2DDebugRenderer();
    }

    private void setUpWorld() {
        if(!gameOver){
            endlessRunner.setCurrentPoints(-1);
            world = WorldUtils.createWorld();
            world.setContactListener(this);
//            setUpBackground();
            setUpGround();
            setUpRunner();
            createEnemy();
        }
    }

    private void setUpBackground() {
        addActor(new Background());
    }

    private void setUpGround() {
        ground = new Ground(WorldUtils.createGround(world));
        addActor(ground);
    }

    private void setUpRunner() {
        runner = new Runner(WorldUtils.createRunner(world));
        addActor(runner);
    }

    private void setupCamera() {
        camera = new OrthographicCamera(VIEWPORT_WIDTH, VIEWPORT_HEIGHT);
        camera.position.set(camera.viewportWidth / 2, camera.viewportHeight / 2, 0f);
        camera.update();
    }

    @Override
    public void act(float delta) {
        super.act(delta);

        Array<Body> bodies = new Array<Body>(world.getBodyCount());
        world.getBodies(bodies);

        for (Body body : bodies) {
            update(body);
        }

        // Fixed timestep
        accumulator += delta;

        while (accumulator >= delta) {
            world.step(TIME_STEP, 6, 2);
            accumulator -= TIME_STEP;
        }

        //TODO: Implement interpolation
        if(runner.isHit()){
            gameOver = true;
            batch.begin();
            font.draw(batch, "Game Over", 200, 200);
            batch.end();
        }
    }

    private void update(Body body) {
        if (!BodyUtils.bodyInBounds(body)) {
            if (BodyUtils.bodyIsEnemy(body) && !runner.isHit()) {
                createEnemy();
            }
            world.destroyBody(body);
        }
    }

    private void createEnemy() {
        Enemy enemy = new Enemy(WorldUtils.createEnemy(world));
//        addActor(enemy);
    }

    @Override
    public void draw() {
        super.draw();
        renderer.render(world, camera.combined);
    }

    public void triggerJump() {
        if (gameOver){
            Array<Body> bodies = new Array<Body>(world.getBodyCount());
            world.getBodies(bodies);

            for (Body body : bodies) {
                world.destroyBody(body);
            }

            endlessRunner.updateHighScore();

            gameOver = false;
            setUpWorld();
        }else{
            runner.jump();
        }
    }


    @Override
    public void beginContact(Contact contact) {

        Body a = contact.getFixtureA().getBody();
        Body b = contact.getFixtureB().getBody();

        if ((BodyUtils.bodyIsRunner(a) && BodyUtils.bodyIsEnemy(b)) ||
                (BodyUtils.bodyIsEnemy(a) && BodyUtils.bodyIsRunner(b))) {
            runner.hit();

        } else if ((BodyUtils.bodyIsRunner(a) && BodyUtils.bodyIsGround(b)) ||
                (BodyUtils.bodyIsGround(a) && BodyUtils.bodyIsRunner(b))) {
            if(endlessRunner.getCurrentPoints() != -1){
                endlessRunner.setTotalPoints(endlessRunner.getTotalPoints()+1);
            }
            endlessRunner.setCurrentPoints(endlessRunner.getCurrentPoints()+1);
            endlessRunner.updateHighScore();

            runner.landed();
        }

    }

    @Override
    public void endContact(Contact contact) {

    }

    @Override
    public void preSolve(Contact contact, Manifold oldManifold) {

    }

    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {

    }

}
