package com.saransh.mariobros;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;
import com.saransh.mariobros.Screens.PlayScreen;

public class MarioBros extends Game {
	public static final int V_WIDTH=400;
	public static final int V_Height=208;
	public static final float PPM=100;

	//Box2D Collision Bits
	public static final short NOTHING_BIT = 0;
	public static final short GROUND_BIT = 1;
	public static final short MARIO_BIT = 2;
	public static final short BRICK_BIT = 4;
	public static final short COIN_BIT = 8;
	public static final short DESTROYED_BIT = 16;
	public static final short OBJECT_BIT = 32;
	public static final short ENEMY_BIT = 64;
	public static final short ENEMY_HEAD_BIT = 128;
	public static final short ITEM_BIT = 256;
	public static final short MARIO_HEAD_BIT = 512;
	public static final short FIREBALL_BIT = 1024;
	public static SpriteBatch batch;

	public static AssetManager manager;
	@Override
	public void create () {
		batch = new SpriteBatch();
		manager=new AssetManager();
		manager.load("Audio/Music/android_assets_audio_music_mario_music.ogg", Music.class);
		manager.load("Audio/Sound/android_assets_audio_sounds_breakblock.wav", Sound.class);
		manager.load("Audio/Sound/android_assets_audio_sounds_bump.wav", Sound.class);
		manager.load("Audio/Sound/android_assets_audio_sounds_coin.wav", Sound.class);
		manager.load("Audio/Sound/android_assets_audio_sounds_mariodie.wav", Sound.class);
		manager.load("Audio/Sound/android_assets_audio_sounds_powerdown.wav", Sound.class);
		manager.load("Audio/Sound/android_assets_audio_sounds_powerup.wav", Sound.class);
		manager.load("Audio/Sound/android_assets_audio_sounds_powerup_spawn.wav", Sound.class);
		manager.load("Audio/Sound/android_assets_audio_sounds_stomp.wav", Sound.class);
		manager.finishLoading();
		setScreen(new PlayScreen(this));
	}


	
	@Override
	public void dispose () {
		super.dispose();
		manager.dispose();
		batch.dispose();

	}
	@Override
	public void render () {

		super.render();

	}
}
