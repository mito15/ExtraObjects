package com.mito.exobj.BraceBase;

import java.util.ArrayList;
import java.util.List;

import com.mito.exobj.common.MyLogger;
import com.mito.exobj.common.entity.EntityWrapperBB;
import com.mito.exobj.network.BB_PacketProcessor;
import com.mito.exobj.network.BB_PacketProcessor.Mode;
import com.mito.exobj.network.PacketHandler;
import com.mito.exobj.utilities.MitoMath;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.IntHashMap;
import net.minecraft.util.LongHashMap;
import net.minecraft.util.MathHelper;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.World;

public class BB_DataWorld {

	public EntityWrapperBB wrapper;
	public IntHashMap BBIDMap = new IntHashMap();
	private int cooltime = 0;
	public List<ExtraObject> braceBaseList = new ArrayList<ExtraObject>();
	public LongHashMap coordToDataMapping = new LongHashMap();
	public World world;
	private double MAX_ENTITY_RADIUS = 100;
	public BB_BindHelper bindhelper = new BB_BindHelper(this);
	private int debug;
	public boolean shouldUpdateRender = false;
	@SideOnly(Side.CLIENT)
	public VBOList buffer = new VBOList();

	/*public BB_DataWorld() {
	}*/

	public BB_DataWorld(World world) {
		this.world = world;
		this.wrapper = new EntityWrapperBB(world, null);
	}

	//boolean b はチャンクに追加するかどうか

	public boolean addBraceBase(ExtraObject base, boolean b) {
		this.BBIDMap.addKey(base.BBID, base);
		if (b) {
			int i = MathHelper.floor_double(base.pos.xCoord / 16.0D);
			int j = MathHelper.floor_double(base.pos.zCoord / 16.0D);
			
			BB_DataChunk datachunk = BB_DataLists.getChunkData(world, i, j);

			if (!this.braceBaseList.add(base)) {
				MyLogger.info("can not add worldlist");
				return false;
			}
			if (!datachunk.addBraceBase(base)) {
				this.braceBaseList.remove(base);
				return false;
			}
			if(world.isRemote)
			this.shouldUpdateRender = true;
			return true;
		} else {
			return this.braceBaseList.add(base);
		}
	}

	public boolean removeBrace(ExtraObject base) {
		this.BBIDMap.removeObject(base.BBID);
		if (base.datachunk != null) {
			int i = MathHelper.floor_double(base.pos.xCoord / 16.0D);
			int j = MathHelper.floor_double(base.pos.zCoord / 16.0D);
			BB_DataChunk chunkdata = base.datachunk;
			if (chunkdata != null) {
				chunkdata.removeBrace(base);
			}
		}
		boolean ret = braceBaseList.remove(base);
		if (!this.world.isRemote) {
			PacketHandler.INSTANCE.sendToAll(new BB_PacketProcessor(Mode.DELETE, base));
		}
		if(world.isRemote)
			this.shouldUpdateRender = true;

		return ret;
	}

	public ExtraObject getBraceBaseByID(int id) {
		return (ExtraObject) this.BBIDMap.lookup(id);
	}

	public void onUpDate() {
		/*debug++;
		if (debug == 200 && this.world.provider.dimensionId == 0) {
			debug = 0;
			MyLogger.info("number of object is " + this.braceBaseList.size());
		}*/
		
		for (int n = 0; n < this.braceBaseList.size(); n++) {
			ExtraObject base = this.braceBaseList.get(n);
			base.prevPos = MitoMath.copyVec3(base.pos);
		}
		
		for (int n = 0; n < this.braceBaseList.size(); n++) {
			ExtraObject base = this.braceBaseList.get(n);
			if (base.isDead) {
				base.removeFromWorld();
				continue;
			}
			base.onUpdate();
		}
	}

	public void removeDataChunk(BB_DataChunk d) {
		this.coordToDataMapping.remove(ChunkCoordIntPair.chunkXZ2Int(d.xPosition, d.zPosition));
	}

	public List<ExtraObject> getExtraObjectWithAABB(AxisAlignedBB boundingBox) {
		ArrayList<ExtraObject> arraylist = new ArrayList<ExtraObject>();
		int i = MathHelper.floor_double((boundingBox.minX - MAX_ENTITY_RADIUS) / 16.0D);
		int j = MathHelper.floor_double((boundingBox.maxX + MAX_ENTITY_RADIUS) / 16.0D);
		int k = MathHelper.floor_double((boundingBox.minZ - MAX_ENTITY_RADIUS) / 16.0D);
		int l = MathHelper.floor_double((boundingBox.maxZ + MAX_ENTITY_RADIUS) / 16.0D);

		for (int i1 = i; i1 <= j; ++i1) {
			for (int j1 = k; j1 <= l; ++j1) {
				if (BB_DataLists.isChunkExist(world, i1, j1)) {
					BB_DataLists.getChunkData(world, i1, j1).getEntitiesWithinAABBForEntity(boundingBox, arraylist);
				}
			}
		}

		return arraylist;
	}

}
