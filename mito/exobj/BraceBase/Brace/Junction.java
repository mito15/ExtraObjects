package com.mito.exobj.BraceBase.Brace;

import com.mito.exobj.BraceBase.ExtraObject;

import net.minecraft.block.Block;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

public class Junction extends ExtraObject {
	
	public Block contain = null;

	public Junction(World world) {
		super(world);
	}
	
	public Junction(World world, Vec3 pos) {
		super(world, pos);
	}

	@Override
	protected void readExtraObjectFromNBT(NBTTagCompound nbt) {

	}

	@Override
	protected void writeExtraObjectToNBT(NBTTagCompound nbt) {

	}

}
