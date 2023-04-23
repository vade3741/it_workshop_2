package com.saransh.mariobros.Sprite;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.tiled.TiledMapTileSet;
import com.badlogic.gdx.math.Vector2;
import com.saransh.mariobros.MarioBros;
import com.saransh.mariobros.Scenes.Hud;
import com.saransh.mariobros.Screens.PlayScreen;
import com.saransh.mariobros.Sprite.Items.ItemDef;
import com.saransh.mariobros.Sprite.Items.Mushroom;

public class Coin extends InteractiveTileObject{
private TiledMapTileSet tileSet;

    public Coin(PlayScreen screen, MapObject object) {
        super(screen, object);
              tileSet=map.getTileSets().getTileSet("tileset_gutter");
        fixture.setUserData(this);
        setCategoryFilter(MarioBros.COIN_BIT);

    }

    @Override
    public void onHeadHit(Mario mario) {
        Gdx.app.log("Coin","Collision");

        int BLANK_COIN = 28;
        if(getcell().getTile().getId()!= BLANK_COIN){
                if(object.getProperties().containsKey("mushroom"))
                {
                    screen.spawnItem(new ItemDef(new Vector2(body.getPosition().x, body.getPosition().y + 16 / MarioBros.PPM),Mushroom.class));
                    MarioBros.manager.get("Audio/Sound/android_assets_audio_sounds_powerup_spawn.wav", Sound.class).play();
                }
                else {
                    MarioBros.manager.get("Audio/Sound/android_assets_audio_sounds_coin.wav", Sound.class).play();
                    }
                getcell().setTile((tileSet.getTile(BLANK_COIN)));
                Hud.addscore(200);

            }
            else
            {
                MarioBros.manager.get("Audio/Sound/android_assets_audio_sounds_bump.wav", Sound.class).play();

            }
    }
}
