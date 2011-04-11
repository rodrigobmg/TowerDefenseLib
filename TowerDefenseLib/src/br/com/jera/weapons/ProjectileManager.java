﻿package br.com.jera.weapons;

import java.util.LinkedList;
import java.util.ListIterator;

import br.com.jera.audio.AudioPlayer;
import br.com.jera.effects.EffectManager;
import br.com.jera.enemies.Enemy;
import br.com.jera.game.SortedDisplayableEntityList;
import br.com.jera.graphic.Sprite;
import br.com.jera.towerdefenselib.GameCharacter;
import br.com.jera.util.Classic2DViewer;
import br.com.jera.util.CommonMath.Rectangle2D;
import br.com.jera.util.CommonMath.Vector2;
import br.com.jera.util.CommonMath.Vector3;
import br.com.jera.util.CommonMath;
import br.com.jera.util.DisplayableEntity;
import br.com.jera.util.SceneViewer;
import br.com.jera.util.SpriteResourceManager;

public class ProjectileManager {

	static class Projectile implements DisplayableEntity {
		Projectile(final int resourceId, Vector2 pos, Enemy targetZombie, final float speed, final float rotationSpeed,
				WeaponProfile parentWeapon, GameCharacter actor) {
			this.actor = actor;
			this.pos = pos;
			this.targetZombie = targetZombie;
			this.speed = speed;
			this.rotationSpeed = rotationSpeed;
			this.resourceId = resourceId;
			this.parentWeapon = parentWeapon;
			final Vector2 dir = targetZombie.get2DPos().sub(pos).normalize();
			this.angle = -(CommonMath.getAngle(dir) / CommonMath.PI) * 180.0f;
			this.pos = this.pos.add(dir.multiply(FORWARD_OFFSET_LENGTH));
		}

		public void update(final long lastFrameDeltaTimeMS, AudioPlayer audioPlayer) {
			final float bias = (float) ((double) lastFrameDeltaTimeMS / 1000.0);
			final Vector2 dir = targetZombie.get2DPos().sub(pos).normalize();
			pos = pos.add(dir.multiply(speed * bias));

			if (rotationSpeed != 0.0f) {
				angle += rotationSpeed * bias;
			} else {
				angle = -(CommonMath.getAngle(dir) / CommonMath.PI) * 180.0f;
			}
		}

		public GameCharacter getActor() {
			return actor;
		}

		public boolean hasHit(SpriteResourceManager res) {
			float x = res.getSprite(resourceId).getFrameSize().x;
			if (actor.get2DPos().squaredDistance(pos) >= actor.get2DPos().squaredDistance(targetZombie.get2DPos())
					- x * x) {
				dead = true;
				return true;
			} else {
				return false;
			}
		}

		public Vector2 get2DPos() {
			return new Vector2(pos);
		}

		@Override
		public Vector3 getPos() {
			return new Vector3(pos, 0);
		}

		@Override
		public void draw(SceneViewer viewer, SpriteResourceManager res) {
			Sprite sprite = res.getSprite(resourceId);
			sprite.draw(pos.sub(viewer.getOrthogonalViewerPos()).add(HEIGHT_OFFSET), angle, Sprite.centerOrigin);
		}

		@Override
		public int compareTo(DisplayableEntity another) {
			return new Float(Math.signum(pos.y - another.getPos().y)).intValue();
		}

		@Override
		public Vector2 getMin(SpriteResourceManager res) {
			Sprite sprite = res.getSprite(resourceId);
			return pos.sub(sprite.getFrameSize());
		}

		@Override
		public Vector2 getMax(SpriteResourceManager res) {
			Sprite sprite = res.getSprite(resourceId);
			return pos.add(sprite.getFrameSize());
		}

		@Override
		public boolean isVisible(SceneViewer viewer, Rectangle2D clientRect) {
			// TODO verificar visibilidade
			return true;
		}

		public boolean isDead() {
			return dead;
		}

		public Enemy getTargetZombie() {
			return targetZombie;
		}

		public WeaponProfile getParentWeapon() {
			return parentWeapon;
		}

		private WeaponProfile parentWeapon;
		private boolean dead = false;
		private Vector2 pos;
		private Enemy targetZombie;
		private float rotationSpeed;
		private float speed;
		private float angle;
		private int resourceId;
		private final GameCharacter actor;
	}

	public void addProjectile(Projectile projectile) {
		projectiles.add(projectile);
	}

	public void update(final long lastFrameDeltaTimeMS, EffectManager effectManager, AudioPlayer audioPlayer, SpriteResourceManager res) {
		ListIterator<Projectile> iter = projectiles.listIterator();
		while (iter.hasNext()) {
			Projectile projectile = iter.next();
			boolean remove = false;
			if (projectile.isDead()) {
				remove = true;
			}
			if (projectile.hasHit(res)) {
				projectile.getTargetZombie().addHarmEffect(
						projectile.getParentWeapon().getHarmEffect(effectManager, projectile.getActor()), audioPlayer);
				remove = true;
			}
			if (remove) {
				iter.remove();
			} else {
				projectile.update(lastFrameDeltaTimeMS, audioPlayer);
			}
		}
	}

	public void draw(Classic2DViewer viewer, SortedDisplayableEntityList displayList, Rectangle2D clientRect) {
		ListIterator<Projectile> iter = projectiles.listIterator();
		while (iter.hasNext()) {
			displayList.sortAdd(viewer, iter.next(), clientRect);
		}
	}

	private LinkedList<Projectile> projectiles = new LinkedList<Projectile>();
	private static final float FORWARD_OFFSET_LENGTH = 8.0f;
	public static final Vector2 HEIGHT_OFFSET = new Vector2(0, -24);
}
