package com.mito.exobj.BraceBase;

import java.util.ArrayList;
import java.util.List;

import com.mito.exobj.client.render.VBOList;
import com.mito.exobj.utilities.MyLogger;

import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;

public class BB_DataChunk {

	public List<ExtraObject> exObjList = new ArrayList<ExtraObject>();
	public List<BB_DataGroup> groupList = new ArrayList<BB_DataGroup>();
	public World world;
	public boolean isDead = false;
	public final int xPosition;
	public final int zPosition;

	private boolean shouldUpdateRender = false;
	public VBOList buffer = new VBOList();

	public BB_DataChunk(World w, Chunk c) {
		this.world = w;
		this.xPosition = c.xPosition;
		this.zPosition = c.zPosition;
	}

	public BB_DataChunk(World w, int i, int j) {
		this.world = w;
		this.xPosition = i;
		this.zPosition = j;
	}

	public void modified() {
		if (world.isRemote) {
			this.updateRenderer();
		}
		if (this.chunkExist()) {
			this.world.getChunkFromChunkCoords(xPosition, zPosition).setChunkModified();
			//MyLogger.info("data chunk#modified");
		}
	}

	public void updateRenderer() {
		setShouldUpdateRender(true);
	}

	public int getLength() {
		return exObjList.size();
	}

	public void removeBrace(ExtraObject base) {
		this.exObjList.remove(base);
		if (!this.world.isRemote) {
			this.getSingleGroup().remove(base);
		}
		this.modified();
	}

	public boolean addBraceBase(ExtraObject base) {
		int i = MathHelper.floor_double(base.pos.xCoord / 16.0D);
		int j = MathHelper.floor_double(base.pos.zCoord / 16.0D);

		if (i != this.xPosition || j != this.zPosition) {
			MyLogger.warn("Wrong location! " + base + " (at " + i + ", " + j + " instead of " + this.xPosition + ", " + this.zPosition + ")");
			return false;
		}
		base.datachunk = this;
		if (!this.world.isRemote) {
			this.getSingleGroup().add(base);
		}
		this.modified();
		return this.exObjList.add(base);
	}

	public BB_DataGroup getSingleGroup() {
		if (this.world.isRemote) {
			return null;
		}
		BB_DataGroup ret;
		if (this.groupList.size() <= 0) {
			ret = new BB_DataGroup(world, this, true);
			groupList.add(ret);
		} else {
			ret = this.groupList.get(0);
		}
		return ret;
	}

	public void setDead() {
		this.isDead = true;
	}

	public void getEntitiesWithinAABBForEntity(AxisAlignedBB boundingBox, ArrayList arraylist) {
		for (ExtraObject base : exObjList) {
			if (base.interactWithAABB(boundingBox)) {
				arraylist.add(base);
			}
		}
	}

	public void addGroup(BB_DataGroup group) {
		if (group.single && !this.groupList.isEmpty()) {
			if (!this.groupList.get(0).isEmpty()) {
				group.integrate(group);
			}
			this.groupList.set(0, group);
		} else {
			this.groupList.add(group);
		}
		this.modified();
	}

	public boolean isShouldUpdateRender() {
		return shouldUpdateRender;
	}

	public void setShouldUpdateRender(boolean shouldUpdateRender) {
		this.shouldUpdateRender = shouldUpdateRender;
	}

	public boolean chunkExist() {
		return this.world.getChunkProvider().chunkExists(this.xPosition, this.zPosition);
	}

}
