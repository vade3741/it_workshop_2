package com.saransh.mariobros.Screens;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.saransh.mariobros.MarioBros;
import com.saransh.mariobros.Scenes.Hud;

import com.saransh.mariobros.Sprite.Enemy;
import com.saransh.mariobros.Sprite.Items.Item;
import com.saransh.mariobros.Sprite.Items.ItemDef;
import com.saransh.mariobros.Sprite.Items.Mushroom;
import com.saransh.mariobros.Sprite.Mario;
import com.saransh.mariobros.Tools.B2WorldCreator;
import com.saransh.mariobros.Tools.WorldContactListener;

import java.util.concurrent.LinkedBlockingQueue;

public class PlayScreen implements Screen {

    private MarioBros game;
    private TextureAtlas atlas;
    public static boolean alreadyDestroyed = false;
    private OrthographicCamera gamecam;
    private Viewport gameport;
    private Hud hud;
    private TmxMapLoader mapLoader;
    private TiledMap map;
    private OrthogonalTiledMapRenderer renderer;
    //Box2d Variables
    private World world;
    private Box2DDebugRenderer b2dr;


    private B2WorldCreator creator;
    private Mario player;
    private Controller controller;
    private Music music;
    private Array<Item> items;
    private LinkedBlockingQueue<ItemDef> itemsToSpawn;

    public PlayScreen( MarioBros game) {atlas = new TextureAtlas("Mario_And_Enemies.pack");
        this.game = game;
        gamecam = new OrthographicCamera();

        gameport = new FitViewport(MarioBros.V_WIDTH/MarioBros.PPM, MarioBros.V_Height/MarioBros.PPM, gamecam);
        hud = new Hud(game.batch);
        mapLoader = new TmxMapLoader();
        map = mapLoader.load("level12.tmx");
        renderer = new OrthogonalTiledMapRenderer(map,1/MarioBros.PPM);
        gamecam.position.set(gameport.getWorldWidth() / 2, gameport.getWorldHeight() / 2, 0);
        world = new World(new Vector2(0, -12), true);
        b2dr = new Box2DDebugRenderer();


    creator=  new B2WorldCreator(this);
        player=new Mario(this);
       world.setContactListener(new WorldContactListener());
      controller = new Controller();
      music=MarioBros.manager.get("Audio/Music/android_assets_audio_music_mario_music.ogg",Music.class);
      music.setLooping(true);
      music.play();
//           goomba=new Goomba(this,5.32f,.32f);
        items = new Array<Item>();
        itemsToSpawn = new LinkedBlockingQueue<ItemDef>();

    }
    public void spawnItem(ItemDef idef){

        itemsToSpawn.add(idef);
    }
    public void handleSpawningItems(){
        if(!itemsToSpawn.isEmpty()){
            ItemDef idef = itemsToSpawn.poll();
            if(idef.type == Mushroom.class){
                items.add(new Mushroom(this, idef.position.x, idef.position.y));
            }
        }
    }
    public TextureAtlas getAtlas(){
        return atlas;
    }
    @Override
    public void show() {

    }
    public void handleInput(float dt){
        float leftlimit ,rightlimit,uplimit,downlimit;
        leftlimit=-player.getBoundingRectangle().getX();
        rightlimit=-player.getBoundingRectangle().getY();
        if(player.currentState!=Mario.State.DEAD)
        {
            if(Controller.isRightPressed())
                player.b2body.setLinearVelocity(new Vector2(1, player.b2body.getLinearVelocity().y));
            if (Controller.isLeftPressed())
                player.b2body.setLinearVelocity(new Vector2(-1, player.b2body.getLinearVelocity().y));
            if(controller.isDownPressed())
                player.b2body.setLinearVelocity(new Vector2(0, player.b2body.getLinearVelocity().y));
            if (Controller.isUpPressed() && player.b2body.getLinearVelocity().y == 0)
                player.b2body.applyLinearImpulse(new Vector2(0, 5f), player.b2body.getWorldCenter(), true);
        }


//        if(Gdx.input.isKeyPressed(Input.Keys.UP))
//             player.b2body.applyLinearImpulse(new Vector2(0,0.4f),player.b2body.getWorldCenter(), true);
//        if(Gdx.input.isKeyPressed(Input.Keys.RIGHT)&&player.b2body.getLinearVelocity().x<=2)
//           player.b2body.applyLinearImpulse(new Vector2(0.05f,0),player.b2body.getWorldCenter(),true);
//        if(Gdx.input.isKeyPressed(Input.Keys.RIGHT)&&player.b2body.getLinearVelocity().x>2)
//           player.b2body.applyLinearImpulse(new Vector2(-0.04f,0),player.b2body.getWorldCenter(),true);
//        if(Gdx.input.isKeyPressed(Input.Keys.LEFT)&&player.b2body.getLinearVelocity().x>=-2)
//            player.b2body.applyLinearImpulse(new Vector2(-0.1f,0),player.b2body.getWorldCenter(),true);
    }
public  void update(float dt){

         handleInput(dt);
         handleSpawningItems();
         world.step(1/60f,6,2);
   player.update(dt);
   for(Enemy enemy: creator.getEnemies()){
       enemy.update(dt);
       if(enemy.getX()<player.getX()+224/MarioBros.PPM)
           enemy.b2body.setActive(true);

   }
    for(Item item : items)
        item.update(dt);
//   goomba.update(dt);
   hud.update(dt);
    if(player.currentState!=Mario.State.DEAD)
    {
        gamecam.position.x=player.b2body.getPosition().x;
    }

         gamecam.update();
         renderer.setView(gamecam);
}
    @Override
    public void render(float delta) {
        update(delta);
        Gdx.gl.glClearColor(0,0,0,1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        renderer.render();
        b2dr.render(world,gamecam.combined);
        game.batch.setProjectionMatrix(gamecam.combined);

        game.batch.begin();
        player.draw(game.batch);
        for(Enemy enemy: creator.getEnemies())
            enemy.draw(game.batch);
        for (Item item : items)
            item.draw(MarioBros.batch);

        game.batch.end();
        game.batch.setProjectionMatrix(hud.stage.getCamera().combined);
          hud.stage.draw();
        if(Gdx.app.getType() == Application.ApplicationType.Android)
            controller.draw();
        if(gameOver())
        {
           game.setScreen(new GameOverScreen(game));
            dispose();
        }
    }


    public boolean gameOver()
    {
        if(player.currentState == Mario.State.DEAD && player.getStateTimer() > 3){
            return true;
        }
        return false;
    }
    @Override
    public void resize(int width, int height) {
       gameport.update(width,height);
    }


    public TiledMap getMap(){

        return map;
    }
    public World getWorld()
    {
        return world;

    }



    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {
map.dispose();
renderer.dispose();
world.dispose();
b2dr.dispose();
hud.dispose();
    }
    public Hud getHud(){ return hud; }
}
