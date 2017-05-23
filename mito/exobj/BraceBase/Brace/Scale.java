package com.mito.exobj.BraceBase.Brace;

import com.mito.exobj.BraceBase.ExtraObject;
import com.mito.exobj.utilities.Line;
import com.mito.exobj.utilities.MitoMath;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

public class Scale extends ExtraObject {
	

	public Scale(World world) {
		super(world);
	}

	public Scale(World world, double x, double y, double z) {
		super(world, Vec3.createVectorHelper(x, y, z));
	}
	
	@Override
	public void onUpdate() {
		super.onUpdate();
		if (isDead && !worldObj.isRemote) {
		}
	}

	@Override
	public void readExtraObjectFromNBT(NBTTagCompound nbt) {

	}

	@Override
	public void writeExtraObjectToNBT(NBTTagCompound nbt) {

	}
	
	@Override
	public Line interactWithRay(Vec3 set, Vec3 end) {
		Line line = MitoMath.getLineNearPoint(set, end, this.pos);
		if (line.getLength() < 0.1) {
			return line;
		}
		return null;
	}

}
