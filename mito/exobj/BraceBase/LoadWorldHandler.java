package com.mito.exobj.BraceBase;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.mito.exobj.common.MyLogger;

import cpw.mods.fml.common.gameevent.TickEvent;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.event.world.ChunkDataEvent;
import net.minecraftforge.event.world.WorldEvent;

public class LoadWorldHandler {

	public static LoadWorldHandler INSTANCE = new LoadWorldHandler();

	public LoadWorldHandler() {
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
		NBTTagCompound nbt = e.getData();
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
		NBTTagList taglistBraces = new NBTTagList();
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
						MyLogger.info("nbt associate(save) " + n + " : " + BB_ResisteredList.classToStringMapping.get(exObj.getClass()));
					}

					for (int n = 0; n < group.list.size(); n++) {
						ExtraObject exObj1 = group.list.get(n);
						NBTTagCompound nbt2 = new NBTTagCompound();
						nbt2.setInteger("exObjNumber", n + 1);
						if (exObj1.writeToNBTOptional(nbt2)) {
							taglistGroup.appendTag(nbt2);
							exObj1.writeNBTAssociate(nbt2, BraceBaseToIntMapping);
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

		nbt.setTag("BB_Groups", taglistGroups);

		if (/*chunkData.isDead*/!e.getChunk().isChunkLoaded) {
			Iterator iterator = chunkData.braceList.iterator();
			while (iterator.hasNext()) {
				ExtraObject base = (ExtraObject) iterator.next();
				base.datachunk = null;
				base.removeFromWorld();
			}
			worldData.removeDataChunk(chunkData);
		}
	}

	public void onChunkDataLoad(ChunkDataEvent.Load e) {
		World world = e.world;
		BB_DataWorld worldData = (BB_DataWorld) this.getMap().get(world);
		if (worldData == null) {
			worldData = new BB_DataWorld(world);
			this.getMap().put(e.world, worldData);
		}
		NBTTagCompound nbt = e.getData();
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
						ExtraObject base = BB_ResisteredList.createBraceBaseFromNBT(nbttagcompound4, world);
						int n1 = nbttagcompound4.getInteger("exObjNumber") - 1;
						if (base != null) {
							IntToBraceBaseMapping.put(new Integer(n1), base);
							BraceBaseToIntMapping.put(base, new Integer(n1));
							base.addToWorld();
							group.add(base);
							MyLogger.info("nbt associate(load) " + n1 + " : " + BB_ResisteredList.classToStringMapping.get(base.getClass()));
						}
					}
					//bugが
					for (int n = 0; n < group.list.size(); ++n) {
						ExtraObject base = group.list.get(n);
						NBTTagCompound nbttagcompound4 = taglistGroup.getCompoundTagAt(BraceBaseToIntMapping.get(base));
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

	public void onWorldTickEvent(TickEvent.WorldTickEvent e) {
		BB_DataWorld data = BB_DataLists.getWorldData(e.world);
		data.onUpDate();
	}

	// 重複については未処理  unload -> save

	/*public void onChunkLoad(ChunkEvent.Load e) {
		//BB_DataWorld data = DataLists.getWorldData(e.world);
		//data.chunkToDataMapping.put(e.getChunk(), new FOChunkData(e.world, e.getChunk()));
	}
	
	public void onChunkUnload(ChunkEvent.Unload e) {
		//BB_DataChunk datachunk = BB_DataLists.getChunkData(e.getChunk());
		//datachunk.setDead();
	}*/

}
