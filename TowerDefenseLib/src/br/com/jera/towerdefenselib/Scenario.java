﻿package br.com.jera.towerdefenselib;

import br.com.jera.audio.AudioPlayer;
import br.com.jera.graphic.GraphicDevice.ALPHA_MODE;
import br.com.jera.graphic.Sprite;
import br.com.jera.resources.PropertyReader;
import br.com.jera.resources.ResourceIdRetriever;
import br.com.jera.util.CommonMath;
import br.com.jera.util.CommonMath.Rectangle2D;
import br.com.jera.util.CommonMath.Vector2;
import br.com.jera.util.CommonMath.Vector3;
import br.com.jera.util.DisplayableEntity;
import br.com.jera.util.SceneViewer;
import br.com.jera.util.SpriteResourceManager;

public class Scenario implements DisplayableEntity {

	private Vector2 pos = new Vector2();
	private final Vector2 normalizedOrigin = new Vector2(0.0f, 0.0f);
	private int resourceId;
	private ResourceIdRetriever resRet;

	public Scenario(int resourceId, Vector2 pos, ResourceIdRetriever resRet) {
		this.resourceId = resourceId;
		this.pos = pos;
		this.resRet = resRet;
	}

	public void draw(SceneViewer viewer, SpriteResourceManager res) {
		res.getGraphicDevice().setAlphaMode(ALPHA_MODE.ALPHA_TEST_ONLY);
		Sprite sprite = res.getSprite(resourceId);
		sprite.draw(pos.sub(viewer.getOrthogonalViewerPos()), normalizedOrigin);
		
		Sprite seamSprite = res.getSprite(resRet.getBmpScenarioSeam());
		if (!PropertyReader.getHorizontalSceneWrapSprite()) {
			seamSprite.draw(pos.sub(viewer.getOrthogonalViewerPos()).add(new Vector2(0, getMax(res).y)), normalizedOrigin);
		} else {
			seamSprite.draw(pos.sub(viewer.getOrthogonalViewerPos()).add(new Vector2(getMax(res).x, 0)), normalizedOrigin);
		}
	}

	public Vector2 getMin(SpriteResourceManager res) {
		Sprite sprite = res.getSprite(resourceId);
		return new Vector2(pos.sub(sprite.getBitmapSize().multiply(normalizedOrigin)));
	}
	
	public boolean isPointInScene(Vector2 p, SpriteResourceManager res) {
		return CommonMath.isPointInRect(Sprite.zero, getMax(res), p);
	}

	public Vector2 getMax(SpriteResourceManager res) {
		Sprite sprite = res.getSprite(resourceId);
		return new Vector2(pos.add(sprite.getBitmapSize().multiply(new Vector2(1,1).sub(normalizedOrigin))));
	}

	public void update(long lastFrameDeltaTimeMS, AudioPlayer audioPlayer) {
		// dummy
	}

	public int compareTo(DisplayableEntity another) {
		return 0;
	}

	public boolean isVisible(SceneViewer viewer, Rectangle2D clientRect) {
		return true;
	}

	public Vector3 getPos() {
		return new Vector3(pos, 0);
	}
}
