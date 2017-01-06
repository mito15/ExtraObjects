package com.mito.exobj.common.block;

import com.mito.exobj.BraceBase.BB_ResisteredList;
import com.mito.exobj.BraceBase.Brace.GroupObject;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;

public class TileObjects extends TileEntity {
	public GroupObject name = null;

	public TileObjects() {

	}

	public void updateEntity() {
	}

	public void readFromNBT(NBTTagCompound nbt) {
		super.readFromNBT(nbt);
		NBTTagList taglist = nbt.getTagList("BB_Groups", 10);
		/*for (int i1 = 0; i1 < taglist.tagCount(); ++i1) {
			
		}*/
		NBTTagCompound nbt1 = taglist == null ? null : taglist.getCompoundTagAt(0);
		name = (GroupObject) BB_ResisteredList.createExObjFromNBT(nbt1, worldObj);
	}

	public void writeToNBT(NBTTagCompound nbt) {
		super.writeToNBT(nbt);
		NBTTagList taglist = new NBTTagList();
		NBTTagCompound nbt1 = new NBTTagCompound();
		if (name != null) {
			name.writeToNBTOptional(nbt1);
			taglist.appendTag(nbt1);
			nbt.setTag("BB_Groups", taglist);
		}
	}

	@Override
	public Packet getDescriptionPacket() {
		NBTTagCompound nbtTagCompound = new NBTTagCompound();
		this.writeToNBT(nbtTagCompound);
		return new S35PacketUpdateTileEntity(this.xCoord, this.yCoord, this.zCoord, 1, nbtTagCompound);
	}

	@Override
	public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity pkt) {
		this.readFromNBT(pkt.func_148857_g());
	}

	public int getMetadata() {
		return this.worldObj.getBlockMetadata(xCoord, yCoord, zCoord);
	}
}
