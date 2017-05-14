package com.mito.exobj.BraceBase.DammyWorld;

import net.minecraft.world.WorldProvider;

public class BB_WorldProvider extends WorldProvider {

	@Override
	public String getDimensionName() {
		return "dummy";
	}

	public int getAverageGroundLevel() {
		return this.terrainType.getMinimumSpawnHeight(this.worldObj);
	}

}
