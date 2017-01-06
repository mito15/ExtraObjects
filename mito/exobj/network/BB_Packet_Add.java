package com.mito.exobj.network;

import java.util.Iterator;

import com.mito.exobj.BraceBase.BB_DataChunk;
import com.mito.exobj.BraceBase.BB_DataLists;
import com.mito.exobj.BraceBase.BB_DataWorld;
import com.mito.exobj.BraceBase.ExtraObject;
import com.mito.exobj.common.MyLogger;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class BB_Packet_Add extends BB_Packet{
	
	public ExtraObject base;
	public int id;
	
	public void onServerProcess(World world, EntityPlayerMP player) {

	}
	
	public void onClientProcess(World world, BB_DataWorld dataworld) {
		ExtraObject base1 = base;
		if (base1 != null) {
			int i = MathHelper.floor_double(base1.pos.xCoord / 16.0D);
			int j = MathHelper.floor_double(base1.pos.zCoord / 16.0D);
			BB_DataChunk chunkData = BB_DataLists.getChunkData(world, i, j);
			Iterator iterator = chunkData.braceList.iterator();
			boolean flag = true;
			while (iterator.hasNext()) {
				ExtraObject fobj = (ExtraObject) iterator.next();
				if (fobj.BBID == id) {
					MyLogger.info("on sync chohuku");
					flag = false;
					break;
				}
			}
			if (flag) {
				base1.addToWorld();
				dataworld.bindhelper.call(base1);
			}
		}
	}

	public void fromBytes(ByteBuf buf) {
		
	}

	public void toBytes(ByteBuf buf) {
		
	}

}
