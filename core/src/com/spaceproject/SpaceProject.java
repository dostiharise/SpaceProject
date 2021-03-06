package com.spaceproject;

import com.badlogic.gdx.Game;
import com.spaceproject.config.CelestialConfig;
import com.spaceproject.config.KeyConfig;
import com.spaceproject.screens.*;
import com.spaceproject.utility.MyScreenAdapter;

public class SpaceProject extends Game {

	public static long SEED = 4; //test seed

	public static CelestialConfig celestcfg;
	public static KeyConfig keycfg;

	//TODO: put into a config
	public static float scale = 4.0f;
	public static int tileSize = 32;
	public static int chunkSize = 8;

	@Override
	public void create() {	
		MyScreenAdapter.game = this;
		
		//load values for things like key mapping, settings, default values for generation
		loadConfigs();
				
	
		boolean inSpace = true;
		//setScreen(new GameScreen(inSpace));
		//setScreen(new Test3DScreen());
		//setScreen(new TestShipGenerationScreen(this));
		//setScreen(new TestNoiseScreen(this));
		//setScreen(new TestVoronoiScreen());
		setScreen(new MainMenuScreen(this));
	}
	
	private static void loadConfigs() {
		//keycfg = (KeyConfig) new KeyConfig().loadFromJson();
		keycfg = new KeyConfig();
		keycfg.loadDefault();
		

		//celestcfg = (CelestialConfig) new CelestialConfig().loadFromJson();
		celestcfg = new CelestialConfig();
		celestcfg.loadDefault();
		
		
		//WorldConfig worldcfg = (WorldConfig) new WorldConfig().loadFromJson();
		//worldcfg.loadDefault();
		//worldcfg.saveToJson();
	}		
	
}
