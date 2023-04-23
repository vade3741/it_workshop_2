package com.saransh.mariobros.Sprite;

import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.EdgeShape;
import com.badlogic.gdx.physics.box2d.Filter;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.saransh.mariobros.MarioBros;
import com.saransh.mariobros.Screens.PlayScreen;

public class Mario extends Sprite {
    public enum State { FALLING, JUMPING, STANDING, RUNNING, GROWING, DEAD }
    public State currentState;
    public State previousState;
    public World world;
    public Body b2body;
    private final TextureRegion marioStand;
    private final Animation marioRun;
    private final TextureRegion marioJump;
    private final TextureRegion marioDead;
    private final TextureRegion bigMarioStand;
    private final TextureRegion bigMarioJump;
    private final Animation<TextureRegion> bigMarioRun;
    private final Animation<TextureRegion> growMario;
    private float stateTimer;
    private boolean runningRight;
    private boolean marioIsBig;
    private boolean runGrowAnimation;
    private boolean timeToDefineBigMario;
    private boolean timeToRedefineMario;
    private boolean marioIsDead;
    private PlayScreen screen;

    public Mario(PlayScreen screen)
    {
        this.screen = screen;
        this.world = screen.getWorld();
        currentState = State.STANDING;
        previousState = State.STANDING;
        stateTimer = 0;
        runningRight = true;
        Array<TextureRegion> frames = new Array<>();
        //get run animation frames and add them to marioRun Animation
        for(int i = 1; i < 4; i++)
            frames.add(new TextureRegion(screen.getAtlas().findRegion("little_mario"), i * 16, 0, 16, 16));
        marioRun = new Animation(0.4f,frames);

        frames.clear();



        for(int i = 1; i < 4; i++)
            frames.add(new TextureRegion(screen.getAtlas().findRegion("big_mario"), i * 16, 0, 16, 32 ));
        bigMarioRun = new Animation<>(0.1f, frames);

        frames.clear();

        frames.add(new TextureRegion(screen.getAtlas().findRegion("big_mario"), 240, 0, 16, 32));
        frames.add(new TextureRegion(screen.getAtlas().findRegion("big_mario"), 0, 0, 16, 32));
        frames.add(new TextureRegion(screen.getAtlas().findRegion("big_mario"), 240, 0, 16, 32));
        frames.add(new TextureRegion(screen.getAtlas().findRegion("big_mario"), 0, 0, 16, 32));
        growMario = new Animation<>(0.2f, frames);


        marioJump = new TextureRegion(screen.getAtlas().findRegion("little_mario"), 80, 0, 16, 16);
        bigMarioJump = new TextureRegion(screen.getAtlas().findRegion("big_mario"), 80, 0, 16, 32);
//




        //create texture region for mario standing
        marioStand = new TextureRegion(screen.getAtlas().findRegion("little_mario"), 0, 0, 16, 16);
        bigMarioStand = new TextureRegion(screen.getAtlas().findRegion("big_mario"), 0, 0, 16, 32);
//
        //create dead mario texture region
        marioDead = new TextureRegion(screen.getAtlas().findRegion("little_mario"), 96, 0, 16, 16);
        defineMario();

        setBounds(0,0,16/MarioBros.PPM,16/MarioBros.PPM);
         setRegion(marioStand);
    }
    public void update(float dt){

        // time is up : too late mario dies T_T
        // the !isDead() method is used to prevent multiple invocation
        // of "die music" and jumping
        // there is probably better ways to do that but it works for now.




        if (screen.getHud().isTimeUp() && !isDead()) {
            die();
        }
        if(marioIsBig)
            setPosition(b2body.getPosition().x - getWidth() / 2, b2body.getPosition().y - getHeight() / 2 - 6 / MarioBros.PPM);
        else
            setPosition(b2body.getPosition().x - getWidth() / 2, b2body.getPosition().y - getHeight() / 2);

         setRegion(getFrame(dt));
          if(timeToDefineBigMario)
             defineBigMario();
          if(timeToRedefineMario)
              redefineMario();
        //update sprite with the correct frame depending on marios current action


    }
    public TextureRegion getFrame(float dt)
    {
        currentState=getState();
        TextureRegion region;
        switch (currentState)
        {
            case DEAD:
                region=marioDead;
                break;
            case GROWING:
                region = growMario.getKeyFrame(stateTimer);
                if(growMario.isAnimationFinished(stateTimer)) {
                    runGrowAnimation = false;
                }
                break;
            case JUMPING:
                region =  marioIsBig? bigMarioJump:marioJump;
                break;
            case RUNNING:
                region= marioIsBig? bigMarioRun.getKeyFrame(stateTimer,true) : (TextureRegion) marioRun.getKeyFrame(stateTimer, true);
                break;
            case FALLING:
            case STANDING:
            default:
                region=marioIsBig?bigMarioStand:marioStand;
                break;
        }

        if(((b2body.getLinearVelocity().x<0)||!runningRight)&&!region.isFlipX())
    {
        region.flip(true,false);
        runningRight=false;
    } else if ((b2body.getLinearVelocity().x>0||runningRight)&&region.isFlipX()) {
        region.flip(true,false);
        runningRight=true;
    }
    stateTimer=currentState==previousState?stateTimer+dt:0;
        previousState=currentState;
        return region;
    }
    public State getState()
    {
        if(marioIsDead)
            return State.DEAD;
         else if(runGrowAnimation)
            return State.GROWING;
        else if(b2body.getLinearVelocity().y>0||(b2body.getLinearVelocity().y<0&&previousState==State.JUMPING))
            return State.JUMPING;
        else if(b2body.getLinearVelocity().y<0)
            return State.FALLING;
        else if (b2body.getLinearVelocity().x!=0) {
            return State.RUNNING;
        }
        else
            return State.STANDING;
    }
    public void grow(){

        if( !isBig() ) {
            runGrowAnimation = true;
            marioIsBig = true;
            timeToDefineBigMario = true;
            setBounds(getX(), getY(), getWidth(), getHeight() * 2);
            MarioBros.manager.get("Audio/Sound/android_assets_audio_sounds_powerup.wav", Sound.class).play();
        }

    }
    public void die() {

        if (!isDead()) {

            MarioBros.manager.get("Audio/Music/android_assets_audio_music_mario_music.ogg", Music.class).stop();
            MarioBros.manager.get("Audio/Sound/android_assets_audio_sounds_mariodie.wav", Sound.class).play();
            marioIsDead = true;
            Filter filter = new Filter();
            filter.maskBits = MarioBros.NOTHING_BIT;

            for (Fixture fixture : b2body.getFixtureList()) {
                fixture.setFilterData(filter);
            }

            b2body.applyLinearImpulse(new Vector2(0, 4f), b2body.getWorldCenter(), true);
        }
    }
    public boolean isDead()
    {
        return marioIsDead;
    }
    public float getStateTimer()
    {
        return stateTimer;
    }
    public boolean isBig(){
        return marioIsBig;
    }

    public void hit(Enemy enemy){
           if(enemy instanceof Turtle &&((Turtle)enemy).getCurrentState() ==Turtle.State.STANDING_SHELL){
               ((Turtle)enemy).kick(this.getX()<=enemy.getX()?Turtle.KICK_RIGHT:Turtle.KICK_LEFT);
           }
           else{
            if (marioIsBig) {
                marioIsBig = false;
                timeToRedefineMario = true;
                setBounds(getX(), getY(), getWidth(), getHeight() / 2);
                 MarioBros.manager.get("Audio/Sound/android_assets_audio_sounds_powerdown.wav", Sound.class).play();
            }
            else {

                MarioBros.manager.get("Audio/Sound/android_assets_audio_sounds_mariodie.wav", Sound.class).play();
                MarioBros.manager.get("Audio/Music/android_assets_audio_music_mario_music.ogg", Music.class).stop();
                marioIsDead = true;
                Filter filter = new Filter();
                filter.maskBits = MarioBros.NOTHING_BIT;
                for (Fixture fixture : b2body.getFixtureList())
                    fixture.setFilterData(filter);
                b2body.applyLinearImpulse(new Vector2(0, 4f), b2body.getWorldCenter(), true);
            }
            }
    }
    public void redefineMario(){
        Vector2 position = b2body.getPosition();
        world.destroyBody(b2body);

        BodyDef bdef = new BodyDef();
        bdef.position.set(position);
        bdef.type = BodyDef.BodyType.DynamicBody;
        b2body = world.createBody(bdef);

        FixtureDef fdef = new FixtureDef();
        CircleShape shape = new CircleShape();
        shape.setRadius(6 / MarioBros.PPM);
        fdef.filter.categoryBits = MarioBros.MARIO_BIT;
        fdef.filter.maskBits = MarioBros.GROUND_BIT |
                MarioBros.COIN_BIT |
                MarioBros.BRICK_BIT |
                MarioBros.ENEMY_BIT |
                MarioBros.OBJECT_BIT |
                MarioBros.ENEMY_HEAD_BIT |
                MarioBros.ITEM_BIT;

        fdef.shape = shape;
        b2body.createFixture(fdef).setUserData(this);

        EdgeShape head = new EdgeShape();
        head.set(new Vector2(-2 / MarioBros.PPM, 6 / MarioBros.PPM), new Vector2(2 / MarioBros.PPM, 6 / MarioBros.PPM));
        fdef.filter.categoryBits = MarioBros.MARIO_HEAD_BIT;
        fdef.shape = head;
        fdef.isSensor = true;

        b2body.createFixture(fdef).setUserData(this);

        timeToRedefineMario = false;

    }
    public void defineBigMario(){
        Vector2 currentPosition = b2body.getPosition();
        world.destroyBody(b2body);

        BodyDef bdef = new BodyDef();
        bdef.position.set(currentPosition.add(0, 10 / MarioBros.PPM));
        bdef.type = BodyDef.BodyType.DynamicBody;
        b2body = world.createBody(bdef);

        FixtureDef fdef = new FixtureDef();
        CircleShape shape = new CircleShape();
        shape.setRadius(6 / MarioBros.PPM);
        fdef.filter.categoryBits = MarioBros.MARIO_BIT;
        fdef.filter.maskBits = MarioBros.GROUND_BIT |
                MarioBros.COIN_BIT |
                MarioBros.BRICK_BIT |
                MarioBros.ENEMY_BIT |
                MarioBros.OBJECT_BIT |
                MarioBros.ENEMY_HEAD_BIT |
                MarioBros.ITEM_BIT;

        fdef.shape = shape;
        b2body.createFixture(fdef).setUserData(this);
        shape.setPosition(new Vector2(0, -14 / MarioBros.PPM));
        b2body.createFixture(fdef).setUserData(this);

        EdgeShape head = new EdgeShape();
        head.set(new Vector2(-2 / MarioBros.PPM, 6 / MarioBros.PPM), new Vector2(2 / MarioBros.PPM, 6 / MarioBros.PPM));
        fdef.filter.categoryBits = MarioBros.MARIO_HEAD_BIT;
        fdef.shape = head;
        fdef.isSensor = true;

        b2body.createFixture(fdef).setUserData(this);
        timeToDefineBigMario = false;
    }

    public void defineMario()
    {
        BodyDef bdef=new BodyDef();
        bdef.position.set(48/ MarioBros.PPM,48/MarioBros.PPM);
        bdef.type=BodyDef.BodyType.DynamicBody;
        b2body=world.createBody(bdef);
        FixtureDef fdef=new FixtureDef();
        CircleShape shape=new CircleShape();
        shape.setRadius(6/MarioBros.PPM);
        fdef.filter.categoryBits=MarioBros.MARIO_BIT;
        fdef.filter.maskBits=MarioBros.GROUND_BIT|MarioBros.BRICK_BIT|MarioBros.COIN_BIT|MarioBros.OBJECT_BIT|MarioBros.ENEMY_BIT|MarioBros.ENEMY_HEAD_BIT;

        fdef.shape = shape;
        b2body.createFixture(fdef).setUserData(this);

        EdgeShape head = new EdgeShape();
        head.set(new Vector2(-2 / MarioBros.PPM, 6 / MarioBros.PPM), new Vector2(2 / MarioBros.PPM, 6 / MarioBros.PPM));
        fdef.filter.categoryBits = MarioBros.MARIO_HEAD_BIT;
        fdef.shape = head;
        fdef.isSensor = true;

        b2body.createFixture(fdef).setUserData(this);
    }  public void draw(Batch batch){
        super.draw(batch);

    }
}
