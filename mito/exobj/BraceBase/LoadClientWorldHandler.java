package com.mito.exobj.BraceBase;

import java.util.Iterator;

import com.mito.exobj.common.Main;
import com.mito.exobj.network.BB_PacketProcessor;
import com.mito.exobj.network.PacketHandler;
import com.mito.exobj.network.BB_PacketProcessor.Mode;

import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.world.World;
import net.minecraftforge.event.world.ChunkEvent;
import net.minecraftforge.event.world.WorldEvent;

@SideOnly(Side.CLIENT)
public class LoadClientWorldHandler {

	public BB_DataWorld data;
	public double time = 0;
	public static LoadClientWorldHandler INSTANCE = new LoadClientWorldHandler();

	public LoadClientWorldHandler() {
	}

	public BB_DataWorld getData(World world) {
		return data;
	}

	public void onUnloadWorld(WorldEvent.Unload e) {
		this.data = null;
		Main.proxy.sg.init();
	}

	public void onLoadWorld(WorldEvent.Load e) {
		this.data = new BB_DataWorld(e.world);
	}

	public void onUpdate(TickEvent.PlayerTickEvent e) {
		if (this.data != null)
			this.data.onUpDate();
	}

	public void onChunkLoad(ChunkEvent.Load e) {
		//mitoLogger.info("chunk load " + e.getChunk().xPosition + " " + e.getChunk().zPosition);
		PacketHandler.INSTANCE.sendToServer(new BB_PacketProcessor(Mode.REQUEST_CHUNK, e.getChunk().xPosition, e.getChunk().zPosition));
	}

	public void onChunkUnload(ChunkEvent.Unload e) {
		//mitoLogger.info("chunk unload " + e.getChunk().xPosition + " " + e.getChunk().zPosition);
		BB_DataChunk chunkData = BB_DataLists.getChunkData(e.getChunk());
		Iterator iterator = chunkData.braceList.iterator();
		while (iterator.hasNext()) {
			ExtraObject fobj = (ExtraObject) iterator.next();
			fobj.datachunk = null;
			fobj.removeFromWorld();
			//mitoLogger.warn("delete!!!!!!!!!!!!!!!!!chunk");
		}
		data.removeDataChunk(chunkData);
	}

}
