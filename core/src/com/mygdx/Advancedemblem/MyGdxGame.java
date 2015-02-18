package com.mygdx.Advancedemblem;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.input.GestureDetector.GestureListener;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapRenderer;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer.Cell;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.mygdx.Advancedemblem.Pathfinder.Node;
import java.util.ArrayList;
import java.util.Random;

public class MyGdxGame extends ApplicationAdapter implements InputProcessor, GestureListener, ApplicationListener {

    Texture img;
    TiledMap tiledMap;
    OrthographicCamera camera;
    TiledMapRenderer tiledMapRenderer;
    int mapWidth;
    int mapHeight;
    BitmapFont font;
    SpriteBatch batch;
    String tileType;
    Pathfinder pathfinder;
    // UI
    Sprite blueSquare;
    Sprite redSquare;
    Sprite cursor;
    Sprite panel;
    BitmapFont panelFont;

    Stage currentStage;
    Unit selectedActor;
    ArrayList possibleMoves;

    // DEBUG
    String nbOfMOves;

    public void initCamera() {
        float w = Gdx.graphics.getWidth();
        float h = Gdx.graphics.getHeight();
        camera = new OrthographicCamera();
        camera.setToOrtho(false, w, h);
        camera.zoom = 0.5f; // 0.4
        camera.position.x = Gdx.graphics.getWidth() / 2 * camera.zoom;
        camera.position.y = Gdx.graphics.getHeight() / 2 * camera.zoom;
        camera.update();
    }

    @Override
    public void create() {
        batch = new SpriteBatch();
        initCamera();

        tiledMap = new TmxMapLoader().load("MyCrappyMap.tmx");
        tiledMapRenderer = new OrthogonalTiledMapRenderer(tiledMap);
        mapWidth = (tiledMap.getProperties().get("width", Integer.class))
                * (tiledMap.getProperties().get("tilewidth", Integer.class));
        mapHeight = (tiledMap.getProperties().get("height", Integer.class))
                * (tiledMap.getProperties().get("tileheight", Integer.class));

        // Setup test sprite
        Texture texture = new Texture(Gdx.files.internal("pik32.png"));
        blueSquare = new Sprite(new Texture(Gdx.files.internal("blueSquare.png")));
        blueSquare.setColor(1, 1, 1, 0.5f);
        redSquare = new Sprite(new Texture(Gdx.files.internal("redSquare.png")));
        redSquare.setColor(1, 1, 1, 0.5f);

        // Setup UI
        cursor = new Sprite(new Texture(Gdx.files.internal("cursor.png")));
        panel = new Sprite(new Texture(Gdx.files.internal("panel3.png")));

        // Setup Pathfinding
        pathfinder = new Pathfinder(tiledMap);

        // Setup font
        font = new BitmapFont();
        font.setColor(Color.RED);

        panelFont = new BitmapFont();
        panelFont.setColor(Color.WHITE);

        // Setup stage
        currentStage = new Stage(new ExtendViewport(Gdx.graphics.getWidth(), Gdx.graphics.getHeight()));
        Soldier myActor = new Soldier(0, 0, texture, "Terry");
        Soldier actor = new Soldier(128, 128, texture, "George");
        myActor.setTouchable(Touchable.enabled);
        actor.setTouchable(Touchable.enabled);
        actor.setIsAlly(false);
        myActor.setBaseHealth(10);
        myActor.setBaseAttack(7);
        actor.setBaseHealth(15);
        actor.setBaseAttack(4);
        currentStage.addActor(actor);
        currentStage.addActor(myActor);
        currentStage.setKeyboardFocus(myActor);
        selectedActor = null;

        // Setup input
        Gdx.input.setCatchMenuKey(true);
        InputMultiplexer im = new InputMultiplexer();
        GestureDetector gd = new GestureDetector(this);
        im.addProcessor(gd);
        im.addProcessor(this);
        im.addProcessor(currentStage);
        Gdx.input.setInputProcessor(im);

        // DEBUG
        tileType = "none";

        nbOfMOves = "0";
    }

    @Override
    public void dispose() {
        batch.dispose();
        font.dispose();
        panelFont.dispose();
        // Dispose all Unit sounds
        for (int i = 0; i < currentStage.getActors().size; i++) {
            for (int x = 0; x < ((Unit) currentStage.getActors().get(i)).FX.length; x++) {
                ((Unit) currentStage.getActors().get(i)).FX[x].dispose();
            }
        }
        currentStage.dispose();
        tiledMap.dispose();
    }

    @Override
    public void render() {
        Gdx.gl.glClearColor(1, 0, 0, 1);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        camera.update();
        tiledMapRenderer.setView(camera);
        tiledMapRenderer.render();
        batch.setProjectionMatrix(camera.combined);
        currentStage.act(Gdx.graphics.getDeltaTime());
        currentStage.draw();
        batch.begin();
        drawCursor();
        drawDebugInfo();
        if (selectedActor != null) {
            drawMovement();
            drawInfoPane();
        }
        batch.end();

    }

    public void drawInfoPane() {
        float offsetX = camera.viewportWidth * camera.zoom;
        float offsetY = camera.viewportHeight * camera.zoom;
        batch.draw(panel,
                camera.position.x - panel.getWidth() / 2 + offsetX / 2 - panel.getWidth() / 2,
                camera.position.y - offsetY / 2 + 30);
        batch.draw(selectedActor.portrait,
                camera.position.x - selectedActor.portrait.getWidth() / 2 + offsetX / 2 - selectedActor.portrait.getWidth() / 2 - panel.getWidth(),
                camera.position.y - offsetY / 2 + 30);
        // draw Attack
        panelFont.draw(batch, selectedActor.getRealAttack() + "", camera.position.x + offsetX / 2 - (3 * panel.getWidth() / 4) - 2,
                camera.position.y - offsetY / 2 + 33 + (3 * panel.getHeight() / 4));
        // Draw Health
        panelFont.draw(batch, selectedActor.getBaseHealth() + "", camera.position.x + offsetX / 2 - (3 * panel.getWidth() / 4),
                camera.position.y - offsetY / 2 + 33 + (panel.getHeight() / 3));
        // Draw Movement
        panelFont.draw(batch, selectedActor.maxMoves + "", camera.position.x + offsetX / 2 - (panel.getWidth() / 4),
                camera.position.y - offsetY / 2 + 33 + (3 * panel.getHeight() / 4) - 2);
    }

    public void drawMovement() {
        nbOfMOves = "" + possibleMoves.size();
        Sprite square = new Sprite();
        if (selectedActor.getIsAlly()) {
            square = blueSquare;
        } else {
            square = redSquare;
        }
        for (int i = 0; i < possibleMoves.size(); i++) {
            Node temp = (Node) possibleMoves.get(i);
            square.setPosition(temp.x, temp.y);
            square.draw(batch);
        }
    }

    public void drawDebugInfo() {
        float offsetX = camera.viewportWidth * camera.zoom;
        float offsetY = camera.viewportHeight * camera.zoom;
        /*font.draw(batch, "TileType : " + tileType, camera.position.x - offsetX/2,
         camera.position.y + offsetY/2);*/
        font.draw(batch, "Moves : " + nbOfMOves, camera.position.x - offsetX / 2,
                camera.position.y + offsetY / 2);
        if (selectedActor != null) {
            font.draw(batch, "current Unit: " + selectedActor.name, camera.position.x - offsetX / 2,
                    camera.position.y + offsetY / 2 - 15);
        } else {
            font.draw(batch, "current Unit: None", camera.position.x - offsetX / 2,
                    camera.position.y + offsetY / 2 - 15);
        }

    }

    public void drawCursor() {
        Vector3 clickCoordinates = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
        Vector3 position = camera.unproject(clickCoordinates);
        position.x = position.x - (position.x % 32);
        position.y = position.y - (position.y % 32);
        cursor.setPosition(position.x, position.y);
        cursor.draw(batch);
    }

    @Override
    public boolean keyDown(int keycode) {
        if (keycode == Input.Keys.ESCAPE) {
            Gdx.app.exit();
        }
        if (keycode == Input.Keys.ENTER || keycode == Input.Keys.MENU) {
            Array<Actor> allActors = currentStage.getActors();
            for (int i = 0; i < allActors.size; i++) {
                ((Unit) (allActors.get(i))).hasPassedTurn = false;
            }
        }

        return false;
    }

    @Override
    public boolean keyUp(int keycode) {

        return false;
    }

    @Override
    public boolean keyTyped(char character) {

        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        // Make sure we are left clicking
        if (button == 0) {
            Vector3 clickCoordinates = new Vector3(screenX, screenY, 0);
            Vector3 position = camera.unproject(clickCoordinates);
            Unit potentialHit = (Unit) currentStage.hit(position.x, position.y, true);
            if (potentialHit == null && selectedActor != null && !selectedActor.hasPassedTurn && selectedActor.getIsAlly()) {
                position.x = (position.x) - (position.x % 32);
                position.y = (position.y) - (position.y % 32);
                if (canMoveHere(selectedActor, (int) position.x, (int) position.y)) {
                    selectedActor.setX(position.x);
                    selectedActor.setY(position.y);
                    selectedActor.hasPassedTurn = true;
                    selectedActor = null;
                    tileType = "none";
                    nbOfMOves = "0";
                }
            } else if (potentialHit != null) {
                selectedActor = potentialHit;
                tileType = getCurrentTileType();
                possibleMoves = pathfinder.getPossibleMoves(potentialHit);
                selectedActor.playFX();
                /*camera.position.x = position.x;
                 camera.position.y = position.y;*/

            }
        }
        if (button == 1) {
            selectedActor = null;
            tileType = "none";
            nbOfMOves = "0";
        }
        return true;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {

        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean scrolled(int amount) {
        return false;
    }

    @Override
    public boolean touchDown(float f, float f1, int i, int i1) {
        return false;
    }

    @Override
    public boolean tap(float f, float f1, int i, int i1) {
        return false;
    }

    @Override
    public boolean longPress(float f, float f1) {
        return false;
    }

    @Override
    public boolean fling(float f, float f1, int i) {
        return false;
    }

    @Override
    public boolean pan(float f, float f1, float deltaX, float deltaY) {
        if ((2 * ((camera.position.x - deltaX) / camera.zoom) > Gdx.graphics.getWidth()) &&
                ((camera.position.x - deltaX) )/camera.zoom + (Gdx.graphics.getWidth()/2) < mapWidth/camera.zoom) {
            camera.translate(-deltaX, 0);
        }
        if (2 * ((camera.position.y + deltaY) / camera.zoom) > Gdx.graphics.getHeight() && 
               ((camera.position.y + deltaY) )/camera.zoom + (Gdx.graphics.getHeight()/2) < mapHeight/camera.zoom ) {
            camera.translate(0, deltaY);
        }
        return true;
    }

    @Override
    public boolean panStop(float f, float f1, int i, int i1) {
        return false;
    }

    @Override
    public boolean zoom(float initialDistance, float currentDistance) {
        if (initialDistance < currentDistance) {
            camera.zoom -= 0.01;
        } else {
            camera.zoom += 0.01;
        }
        return false;
    }

    @Override
    public boolean pinch(Vector2 initialPointer1, Vector2 initialPointer2, Vector2 pointer1, Vector2 pointer2) {

        return false;
    }

    public String getCurrentTileType() {
        TiledMapTileLayer layer = (TiledMapTileLayer) tiledMap.getLayers().get(0);
        Cell cell = layer.getCell((int) (selectedActor.getX() / 32), (int) (selectedActor.getY() / 32));
        if (cell != null) {
            return (String) cell.getTile().getProperties().get("type");

        }
        return "none";
    }

    public boolean canMoveHere(Unit unit, int x, int y) {
        boolean canMove = false;
        for (int i = 0; i < possibleMoves.size(); i++) {
            Node temp = (Node) possibleMoves.get(i);
            if (temp.x == x && temp.y == y) {
                canMove = true;
            }
        }
        return canMove;
    }

    public abstract class Unit extends Actor {

        int maxMoves = 2;
        Sprite texture;
        float actorX = 0, actorY = 0;
        float newX = 0;
        String name = "Default";
        boolean hasPassedTurn;
        Sound[] FX;
        Texture portrait;

        // private
        private boolean isAlly = true;
        private int baseHealth = 1;
        private int baseAttack = 1;

        public Unit(float x, float y, Texture texture, String name) {
            this.setX(x);
            this.setY(y);
            this.texture = new Sprite(texture);
            this.name = name;
            this.hasPassedTurn = false;
            setBounds(x, y, texture.getWidth(), texture.getHeight());
            addListener(new InputListener() {

                @Override
                public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                    Gdx.app.exit();
                    selectedActor = ((Unit) event.getTarget());
                    return true;
                }

                @Override
                public boolean keyDown(InputEvent event, int keycode) {
                    float x = ((Unit) event.getTarget()).getX();
                    float y = ((Unit) event.getTarget()).getY();
                    if (keycode == Input.Keys.LEFT) {
                        ((Unit) event.getTarget()).setX(x -= 32);
                    }
                    if (keycode == Input.Keys.RIGHT) {
                        ((Unit) event.getTarget()).setX(x += 32);
                    }
                    if (keycode == Input.Keys.UP) {
                        ((Unit) event.getTarget()).setY(y += 32);
                    }
                    if (keycode == Input.Keys.DOWN) {
                        ((Unit) event.getTarget()).setY(y -= 32);
                    }
                    return true;
                }

                @Override
                public boolean keyUp(InputEvent event,
                        int keycode) {
                    return false;
                }

                @Override
                public boolean keyTyped(InputEvent event,
                        char character) {
                    return false;
                }

            });

        }

        @Override
        public void draw(Batch batch, float alpha) {
            batch.setProjectionMatrix(camera.combined);
            if (this.hasPassedTurn) {
                batch.setColor(0.5F, 0.5F, 0.5F, 1F);
                batch.draw(texture, this.getX(), this.getY());
                batch.setColor(1F, 1F, 1F, 1F);
            } else {
                batch.draw(texture, this.getX(), this.getY());
            }
        }

        @Override
        public void act(float delta) {
            setBounds(this.getX(), this.getY(), this.texture.getWidth(), this.texture.getHeight());
        }

        public abstract int getMovementCost(Cell tile);

        public void playFX() {
            if (!this.hasPassedTurn) {
                Random rand = new Random();
                int randomNum = rand.nextInt((this.FX.length - 1) + 1) + 0;
                FX[randomNum].play();
            }
        }

        public void setIsAlly(boolean ally) {
            // Set correct direction
            this.texture.flip(this.isAlly != ally, false);
            this.isAlly = ally;
        }

        public boolean getIsAlly() {
            return this.isAlly;
        }

        public int getBaseAttack() {
            return this.baseAttack;
        }

        public void setBaseAttack(int attack) {
            this.baseAttack = attack;
        }
        
        public int getRealAttack() {
            return this.baseAttack;
        }
        
        public int getBaseHealth() {
            return this.baseHealth;
        }

        public void setBaseHealth(int health) {
            this.baseHealth = health;
        }
        
        public int getRealHealth() {
            return this.baseHealth;
        }
    }

    public class Soldier extends Unit {

        public Soldier(float x, float y, Texture texture, String name) {
            super(x, y, texture, name);
            this.maxMoves = 2;
            this.FX = new Sound[2];
            this.FX[0] = Sounds.pikachu;
            this.FX[1] = Sounds.pikachu2;
            this.portrait = new Texture(Gdx.files.internal("soldier/portrait.png"));
        }

        @Override
        public int getMovementCost(Cell tile) {
            if (tile != null) {
                String type = (String) tile.getTile().getProperties().get("type");
                if ("grass".equals(type)) {
                    return 1;
                } else {
                    return 2;
                }
            }
            return 1;
        }

    }
}
