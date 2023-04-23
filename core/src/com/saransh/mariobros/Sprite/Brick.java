package com.saransh.mariobros.Sprite;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.physics.box2d.World;
import com.saransh.mariobros.MarioBros;
import com.saransh.mariobros.Scenes.Hud;
import com.saransh.mariobros.Screens.PlayScreen;

public class Brick extends InteractiveTileObject{
    public Brick(PlayScreen screen, MapObject object) {


        super(screen, object);
        fixture.setUserData(this);
        setCategoryFilter(MarioBros.BRICK_BIT);
    }

    @Override
    public void onHeadHit(Mario mario) {
        Gdx.app.log("Brick","Collision");
        if(mario.isBig())
        {
            setCategoryFilter(MarioBros.DESTROYED_BIT);
            getcell().setTile(null);
            Hud.addscore(200);
            MarioBros.manager.get("Audio/Sound/android_assets_audio_sounds_breakblock.wav", Sound.class).play();
        }
        else
        {
            MarioBros.manager.get("Audio/Sound/android_assets_audio_sounds_bump.wav", Sound.class).play();

        }

    }
}
