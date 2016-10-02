package com.mito.exobj.BraceBase.Brace;

import com.mito.exobj.utilities.MitoMath;

import net.minecraft.util.Vec3;
import net.minecraft.world.World;

public class Motor extends LinearMotor {


	public Motor(World world) {
		super(world);
	}

	public Motor(World world, Vec3 pos) {
		super(world, pos);
	}

	public void moveLinearMotor() {
		Vec3 motion = this.railBrace.line.getMotion(this.pos, this.speed, this.direction);

		if(motion == null){
			direction = !direction;
			return;
		}
		//this.moveRequest(motion, command++, );
		this.move(motion, command++);
	}

	@Override
	public void onUpdate() {
		super.onUpdate();
		if (this.active && this.railBrace != null && this.pos != null) {
			this.isStatic = false;
			this.prevPos = MitoMath.copyVec3(this.pos);
			this.moveLinearMotor();
		} else {
			this.isStatic = true;
		}
		if(this.railBrace == null || this.railBrace.isDead){
			this.setDead();
		}
	}

}
