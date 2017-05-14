package com.mito.exobj.BraceBase;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.mito.exobj.MyLogger;

import cpw.mods.fml.common.gameevent.TickEvent;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.storage.RegionFileCache;
import net.minecraftforge.event.world.ChunkDataEvent;
import net.minecraftforge.event.world.ChunkEvent;
import net.minecraftforge.event.world.WorldEvent;

public class BB_LoadWorld {

	public static BB_LoadWorld INSTANCE = new BB_LoadWorld();

	public BB_LoadWorld() {
	}

	public Map getMap() {
		return BB_DataLists.worldDataMap;
	}

	public void onLoadWorld(WorldEvent.Load e) {
		this.getMap().put(e.world, new BB_DataWorld(e.world));
	}

	public void onUnloadWorld(WorldEvent.Unload e) {
		this.getMap().remove(e.world);
	}

	public void onUpdate(TickEvent.ServerTickEvent e) {
	}

	public void onChunkDataSave(ChunkDataEvent.Save e) {
		World world = e.world;
		Chunk chunk = e.getChunk();

		if (!BB_DataLists.existChunkData(chunk)) {
			return;
		}
		BB_DataWorld worldData = (BB_DataWorld) this.getMap().get(world);
		if (worldData == null) {
			worldData = new BB_DataWorld(world);
			this.getMap().put(e.world, worldData);
		}
		BB_DataChunk chunkData = BB_DataLists.getChunkData(chunk);

		if (chunkData.groupList.size() == 0 || (chunkData.groupList.size() == 1 && chunkData.groupList.get(0).isEmpty())) {
			return;
		}

		NBTTagCompound nbt1;
		Iterator iterator1;
		Iterator iterator2;

		NBTTagList taglistGroups = new NBTTagList();

		iterator1 = chunkData.groupList.iterator();

		while (iterator1.hasNext()) {
			BB_DataGroup group = (BB_DataGroup) iterator1.next();

			nbt1 = new NBTTagCompound();

			try {
				if (group.writeToNBTOptional(nbt1)) {
					NBTTagList taglistGroup = new NBTTagList();
					Map<ExtraObject, Integer> BraceBaseToIntMapping = new HashMap<ExtraObject, Integer>();

					for (int n = 0; n < group.list.size(); n++) {
						ExtraObject exObj = group.list.get(n);
						BraceBaseToIntMapping.put(exObj, new Integer(n));
						//MyLogger.info("nbt associate(save) " + n + " : " + BB_ResisteredList.classToStringMapping.get(exObj.getClass()));
					}

					for (int n = 0; n < group.list.size(); n++) {
						ExtraObject exObj1 = group.list.get(n);
						NBTTagCompound nbt2 = new NBTTagCompound();
						nbt2.setInteger("exObjNumber", n + 1);
						if (exObj1.writeToNBTOptional(nbt2)) {
							taglistGroup.appendTag(nbt2);
							exObj1.writeNBTAssociate(nbt2, BraceBaseToIntMapping);
						}
						if (chunkData.isDead) {
							exObj1.removeFromWorld();
						}
					}
					nbt1.setTag("BB_Group", taglistGroup);

					taglistGroups.appendTag(nbt1);
				}
			} catch (Exception ex) {
				MyLogger.warn("chunk save error on Braces&Oscillators\n");
				ex.printStackTrace();
			}
		}

		NBTTagCompound nbt = new NBTTagCompound();
		nbt.setTag("BB_Groups", taglistGroups);

		if (/*chunkData.isDead*/!e.getChunk().isChunkLoaded) {
			Iterator iterator = chunkData.exObjList.iterator();
			while (iterator.hasNext()) {
				ExtraObject base = (ExtraObject) iterator.next();
				base.datachunk = null;
				base.removeFromWorld();
			}
			worldData.removeDataChunk(chunkData);
		}

		try {
			nbtSave(e.world, e.getChunk(), nbt);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}

	private File getSaveDir(World world) {
		File ret = new File(world.getSaveHandler().getWorldDirectory(), "ExtraObjects");
		if (!ret.exists()) {
			ret.mkdirs();
		}
		return ret;
	}

	private NBTTagCompound nbtLoad(World world, Chunk chunk) {
		NBTTagCompound nbttagcompound = null;

		DataInputStream datainputstream = RegionFileCache.getChunkInputStream(getSaveDir(world), chunk.xPosition, chunk.zPosition);
		if (datainputstream == null) {
			return null;
		}
		try {
			nbttagcompound = CompressedStreamTools.read(datainputstream);
			datainputstream.close();
		} catch (IOException e1) {
			e1.printStackTrace();
		}

		return nbttagcompound;
	}

	private void nbtSave(World world, Chunk chunk, NBTTagCompound nbt) throws IOException {

		DataOutputStream s = RegionFileCache.getChunkOutputStream(getSaveDir(world), chunk.xPosition, chunk.zPosition);
		CompressedStreamTools.write(nbt, s);
		s.close();
	}

	public void onChunkDataLoad(ChunkDataEvent.Load e) {
		World world = e.world;
		BB_DataWorld worldData = (BB_DataWorld) this.getMap().get(world);
		if (worldData == null) {
			worldData = new BB_DataWorld(world);
			this.getMap().put(e.world, worldData);
		}
		NBTTagCompound[] nbts = new NBTTagCompound[] { this.nbtLoad(world, e.getChunk()), e.getData() };
		for (NBTTagCompound nbt : nbts) {
			NBTTagList taglistGroups = nbt == null ? null : nbt.getTagList("BB_Groups", 10);

			if (taglistGroups != null) {
				for (int l = 0; l < taglistGroups.tagCount(); ++l) {
					NBTTagCompound nbttagcompound3 = taglistGroups.getCompoundTagAt(l);
					BB_DataGroup group = new BB_DataGroup(world, e.getChunk(), nbttagcompound3.getBoolean("single"));
					boolean flag = false;
					if (group.single) {
						group.addToWorld();
						flag = true;
					}

					NBTTagList taglistGroup = nbttagcompound3 == null ? null : nbttagcompound3.getTagList("BB_Group", 10);
					if (taglistGroup != null) {
						Map<Integer, ExtraObject> IntToBraceBaseMapping = new HashMap<Integer, ExtraObject>();
						Map<ExtraObject, Integer> BraceBaseToIntMapping = new HashMap<ExtraObject, Integer>();

						for (int n = 0; n < taglistGroup.tagCount(); ++n) {
							NBTTagCompound nbttagcompound4 = taglistGroup.getCompoundTagAt(n);
							ExtraObject base = BB_ResisteredList.createExObjFromNBT(nbttagcompound4, world);
							int n1 = nbttagcompound4.getInteger("exObjNumber") - 1;
							if (base != null) {
								IntToBraceBaseMapping.put(new Integer(n1), base);
								BraceBaseToIntMapping.put(base, new Integer(n1));
								base.addToWorld();
								group.add(base);
								//MyLogger.info("nbt associate(load) " + n1 + " : " + BB_ResisteredList.classToStringMapping.get(base.getClass()));
							}
						}
						//bugが
						for (int n = 0; n < group.list.size(); ++n) {
							ExtraObject base = group.list.get(n);
							NBTTagCompound nbttagcompound4 = taglistGroup.getCompoundTagAt(n);
							if (base != null) {
								base.readNBTAssociate(nbttagcompound4, IntToBraceBaseMapping);
							}
						}
					}
					if (group.list.size() != 0 && !flag) {
						group.addToWorld();
					}
				}
			}
		}
	}

	public void onWorldTickEvent(TickEvent.WorldTickEvent e) {
		BB_DataWorld data = BB_DataLists.getWorldData(e.world);
		data.onUpDate();
		/*for(BB_DataChunk chunk : data.coordToDataMapping.values()){
			if(!e.world.getChunkProvider().chunkExists(chunk.xPosition, chunk.zPosition)){
				chunk.setDead();
			}
		}*/
	}

	// 重複については未処理  unload -> save

	public void onChunkLoad(ChunkEvent.Load e) {
		//MyLogger.info("on load");
	}

	public void onChunkUnload(ChunkEvent.Unload e) {
		if (!BB_DataLists.existChunkData(e.getChunk())) {
			return;
		}
		BB_DataChunk datachunk = BB_DataLists.getChunkData(e.getChunk());
		datachunk.setDead();
	}

}
