package com.spaceproject.screens;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Application.ApplicationType;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;
import com.spaceproject.EntityFactory;
import com.spaceproject.SpaceProject;
import com.spaceproject.systems.BoundsSystem;
import com.spaceproject.systems.CameraSystem;
import com.spaceproject.systems.DebugUISystem;
import com.spaceproject.systems.DesktopInputSystem;
import com.spaceproject.systems.ExpireSystem;
import com.spaceproject.systems.MovementSystem;
import com.spaceproject.systems.OrbitSystem;
import com.spaceproject.systems.PlayerControlSystem;
import com.spaceproject.systems.RenderingSystem;
import com.spaceproject.systems.TouchUISystem;

public class SpaceScreen extends ScreenAdapter {

	SpaceProject game;
	
	public Engine engine;
	
		
	public SpaceScreen(SpaceProject game) {

		this.game = game;
		// engine to handle all entities and component
		engine = new Engine();
		
		
		//temporary test entities--------------------------------------------
		//add entities to engine, should put in spawn system or initializer of sorts...
		//TODO: need refactor and a home			
		
		/*
		//test planets
		engine.addEntity(EntityFactory.createPlanet(300, 300));
		engine.addEntity(EntityFactory.createPlanet(-300, -300));
		engine.addEntity(EntityFactory.createPlanet(600, 0));
		engine.addEntity(EntityFactory.createPlanet(-600, 0));
		engine.addEntity(EntityFactory.createPlanet(1900, 0));
		*/
		
		//add test planetary system (solar system)
		for (Entity entity : EntityFactory.createPlanetarySystem(0, 0)) {
			engine.addEntity(entity);
		}
		
		int size = 1000;
		float x = -100.01f;
		float y = 300;
		int tX = 4;
		int tY = 3;
		
		int newTX = tX + (int)(x / size);
		if (x < 0) {
			newTX--;
		}
		System.out.println(tX + " -> " + newTX);
		
		//System.out.println(x + " -> " + x % size);
		//int newX = x - ((x / size) * size);
		float newX = x % size;
		if (newX < 0) {
			newX = size + newX;
		}
		System.out.println(x + " -> " + newX);
		
		//test ships
		//engine.addEntity(EntityFactory.createShip(100, 300));	
		engine.addEntity(EntityFactory.createShip(0, 0, 500, 500));
		engine.addEntity(EntityFactory.createShip(0, 1, 500, 500));
		engine.addEntity(EntityFactory.createShip(0, -1, 500, 500));
		engine.addEntity(EntityFactory.createShip(1, 0, 500, 500));
		engine.addEntity(EntityFactory.createShip(1, 1, 500, 500));
		engine.addEntity(EntityFactory.createShip(1, -1, 500, 500));
		engine.addEntity(EntityFactory.createShip(-1, 0, 500, 500));
		engine.addEntity(EntityFactory.createShip(-1, 1, 500, 500));
		engine.addEntity(EntityFactory.createShip(-1, -1, 500, 500));
		//engine.addEntity(EntityFactory.createShip3(-100, 400));
		//engine.addEntity(EntityFactory.createShip3(-200, 400));		
		//engine.addEntity(EntityFactory.createShip3(-300, 400));
		//engine.addEntity(EntityFactory.createShip3(-400, 400));
		//engine.addEntity(EntityFactory.createShip3(200, 400));
		//engine.addEntity(EntityFactory.createShip3(300, 400));
		//engine.addEntity(EntityFactory.createShip3(400, 400));

		
		//player------------------------------------------
		//start as player
		//Entity player = EntityFactory.createCharacter(0, 0, false, null);
		//engine.addEntity(player);
		
		//start as ship
		//Entity playerTESTSHIP = EntityFactory.createShip3(0, 0);
		Entity playerTESTSHIP = EntityFactory.createShip(0, 0, 500, 500);
		Entity player = EntityFactory.createCharacter(0, 0, true, playerTESTSHIP);
		engine.addEntity(playerTESTSHIP);
				
		
		// Add systems to engine---------------------------------------------------------
		//engine.addSystem(new PlayerControlSystem(player));//start as player
		engine.addSystem(new PlayerControlSystem(player, playerTESTSHIP));//start as ship
		engine.addSystem(new RenderingSystem(engine));
		engine.addSystem(new MovementSystem());
		engine.addSystem(new OrbitSystem());
		engine.addSystem(new DebugUISystem());
		engine.addSystem(new BoundsSystem());
		engine.addSystem(new CameraSystem(playerTESTSHIP));
		engine.addSystem(new ExpireSystem(1));
		
		//add input system. touch on android and keys on desktop.
		if (Gdx.app.getType() == ApplicationType.Android) {
			engine.addSystem(new TouchUISystem());
		} else {
			engine.addSystem(new DesktopInputSystem());
		}
		
	}
	
	Entity[] testPlanetsDebug; //test removable planetary system
	
	public void render(float delta) {		
		//update engine
		engine.update(delta);
			
		//terminate
		if (Gdx.input.isKeyJustPressed(Keys.ESCAPE)) Gdx.app.exit();
		
		
		
		// [DEBUG]//////////////////////////////
		if (Gdx.input.isKeyJustPressed(Keys.K)) {
			if (testPlanetsDebug != null) {
				// remove-----------------
				for (Entity entity : testPlanetsDebug) {
					engine.removeEntity(entity);
				}
				testPlanetsDebug = null;
				System.out.println("removed test planets");
			} else {
				// add------------------
				Vector3 pos = engine.getSystem(RenderingSystem.class).getCam().position;
				testPlanetsDebug = EntityFactory.createPlanetarySystem(pos.x, pos.y);
				for (Entity entity : testPlanetsDebug) {
					engine.addEntity(entity);
				}
				System.out.println("added test planets");
			}
		}
		// [DEGUB]/////////////////////////////

	}

	//resize game
	public void resize(int width, int height) {
		Gdx.app.log("screen", width + ", " + height);
		engine.getSystem(RenderingSystem.class).resize(width, height);
	}

	
	public void dispose() {
		//TODO: clean up after self
		//dispose of all spritebatches and whatnot
		//create dispose method in all systems and call?
		
	}
	
	public void hide() { }

	public void pause() { }

	public void resume() { }
}
