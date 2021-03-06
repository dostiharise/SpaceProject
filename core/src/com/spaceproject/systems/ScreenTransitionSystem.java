package com.spaceproject.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.spaceproject.SpaceProject;
import com.spaceproject.components.AIComponent;
import com.spaceproject.components.ControllableComponent;
import com.spaceproject.components.ScreenTransitionComponent;
import com.spaceproject.components.ScreenTransitionComponent.LandAnimStage;
import com.spaceproject.components.ScreenTransitionComponent.TakeOffAnimStage;
import com.spaceproject.components.TextureComponent;
import com.spaceproject.components.TransformComponent;
import com.spaceproject.screens.GameScreen;
import com.spaceproject.utility.Mappers;
import com.spaceproject.utility.MyScreenAdapter;


public class ScreenTransitionSystem extends IteratingSystem {
	//Vector3 landPos = null;
	
	public ScreenTransitionSystem() {
		super(Family.all(ScreenTransitionComponent.class, TransformComponent.class).get());
	}

	@Override
	protected void processEntity(Entity entity, float delta) {
		ScreenTransitionComponent screenTrans = Mappers.screenTrans.get(entity);
		
		if (screenTrans.landStage != null) {
			if (screenTrans.curLandStage == null || screenTrans.curLandStage != screenTrans.landStage) {
				System.out.println("Animation Stage: " + screenTrans.landStage);
				screenTrans.curLandStage = screenTrans.landStage;
				//landPos = screenTrans.landCFG.position;//?
			}
			switch (screenTrans.landStage) {
			case shrink:
				shrink(entity, screenTrans, delta);
				break;
			case zoomIn:
				zoomIn(screenTrans);
				break;
			case transition:
				landOnPlanet(screenTrans);
				return;
			case pause:
				pause(screenTrans, delta);
				break;
			case exit:
				exit(entity, screenTrans);
				break;
			case end:
				entity.remove(ScreenTransitionComponent.class);
				System.out.println("Animation complete. Removed ScreenTransitionComponent");
				break;
			default:
				try {
					throw new Exception("Unknown Animation Stage: " + screenTrans.landStage);
				} catch (Exception e) {
					e.printStackTrace();
				}
				break;
			}
		} else if (screenTrans.takeOffStage != null) {
			if (screenTrans.curTakeOffStage == null || screenTrans.curTakeOffStage != screenTrans.takeOffStage) {
				System.out.println("Animation Stage: " + screenTrans.takeOffStage);
				screenTrans.curTakeOffStage = screenTrans.takeOffStage;
				//landPos = screenTrans.landCFG.position;
			}
			switch (screenTrans.takeOffStage) {
			case transition:
				takeOff(screenTrans);
				return;
			case zoomOut:
				zoomOut(screenTrans);
				break;
			case grow:
				grow(entity, screenTrans, delta);
				break;
			case end:
				entity.remove(screenTrans.getClass());
				System.out.println("Animation complete. Removed ScreenTransitionComponent");
				break;
			default:
				try {
					throw new Exception("Unknown Animation Stage: " + screenTrans.takeOffStage);
				} catch (Exception e) {
					e.printStackTrace();
				}
				break;
			}
		}

		// freeze movement during animation
		TransformComponent transform = Mappers.transform.get(entity);
		if (transform != null) {
			transform.velocity.set(0, 0);
			ControllableComponent control = entity.getComponent(ControllableComponent.class);
			if (control != null) {
				control.angleFacing = Mappers.transform.get(entity).rotation;
			}
		}

	}


	private static void shrink(Entity entity, ScreenTransitionComponent screenTrans, float delta) {
		TextureComponent tex = Mappers.texture.get(entity);
		
		//shrink texture
		tex.scale -= 3f * delta; 
		if (tex.scale <= 0.1f) {
			tex.scale = 0;

			if (entity.getComponent(AIComponent.class) != null) {
				screenTrans.landStage = LandAnimStage.end;
			} else {
				screenTrans.landStage = LandAnimStage.zoomIn;
			}
		}
	}
	
	private static void grow(Entity entity, ScreenTransitionComponent screenTrans, float delta) {
		TextureComponent tex = Mappers.texture.get(entity);
		
		//grow texture
		tex.scale += 3f * delta; 
		if (tex.scale >= SpaceProject.scale) {
			tex.scale = SpaceProject.scale;
			
			screenTrans.takeOffStage = ScreenTransitionComponent.TakeOffAnimStage.end;
		}
	}

	private static void zoomIn(ScreenTransitionComponent screenTrans) {
		MyScreenAdapter.setZoomTarget(0);
		if (MyScreenAdapter.cam.zoom <= 0.01f)
			screenTrans.landStage = ScreenTransitionComponent.LandAnimStage.transition;
	}
	
	private static void zoomOut(ScreenTransitionComponent screenTrans) {
		MyScreenAdapter.setZoomTarget(1);
		if (MyScreenAdapter.cam.zoom == 1)
			screenTrans.takeOffStage = ScreenTransitionComponent.TakeOffAnimStage.grow;
	}


	private static void landOnPlanet(ScreenTransitionComponent screenTrans) {
		screenTrans.landStage = LandAnimStage.pause;
		screenTrans.landCFG.ship.add(screenTrans);
		screenTrans.landCFG.ship.getComponent(TextureComponent.class).scale = SpaceProject.scale;//reset size to normal

		System.out.println("Land: " + screenTrans.landCFG.position);
		GameScreen.landCFG = screenTrans.landCFG;
		GameScreen.transition = true;
	}
	
	private void takeOff(ScreenTransitionComponent screenTrans) {
		screenTrans.takeOffStage = TakeOffAnimStage.zoomOut;
		screenTrans.landCFG.ship.add(screenTrans);
		screenTrans.landCFG.ship.getComponent(TextureComponent.class).scale = 0;//set size to 0 so texture can grow
		//screenTrans.landCFG.position = landPos;//landCFG.position;

		System.out.println("Take off:" + screenTrans.landCFG.position);

		GameScreen.transition = true;
		//TODO: do current systems run in the background during change?
		//if so, disable/pause and cleanup/dispose
	}
		
	private static void pause(ScreenTransitionComponent screenTrans, float delta) {				
		int transitionTime = 5000;
		screenTrans.timer += 1000 * delta;		
		if (screenTrans.timer >= transitionTime)		
			screenTrans.landStage = ScreenTransitionComponent.LandAnimStage.exit;
		
	}

	private static void exit(Entity entity, ScreenTransitionComponent screenTrans) {
		if (Mappers.vehicle.get(entity) != null) {
			ControllableComponent control = Mappers.controllable.get(entity);
			if (control != null) {
				control.changeVehicle = true;
				screenTrans.landStage = ScreenTransitionComponent.LandAnimStage.end;
			}
		}
	}
}
