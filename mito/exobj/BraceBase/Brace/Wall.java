package com.mito.exobj.BraceBase.Brace;

import com.mito.exobj.BraceBase.ExtraObject;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

public class Wall extends ExtraObject {

	public Wall(World world) {
		super(world);
	}
	
	public Wall(World world, Vec3 pos) {
		super(world, pos);
	}

	@Override
	protected void readExtraObjectFromNBT(NBTTagCompound nbt) {
		// TODO 自動生成されたメソッド・スタブ

	}

	@Override
	protected void writeExtraObjectToNBT(NBTTagCompound nbt) {
		// TODO 自動生成されたメソッド・スタブ

	}

}
