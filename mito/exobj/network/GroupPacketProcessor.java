package com.mito.exobj.network;

import java.util.ArrayList;
import java.util.List;

import com.mito.exobj.Main;
import com.mito.exobj.BraceBase.BB_DataLists;
import com.mito.exobj.BraceBase.BB_DataWorld;
import com.mito.exobj.BraceBase.BB_ResisteredList;
import com.mito.exobj.BraceBase.ExtraObject;
import com.mito.exobj.BraceBase.Brace.GroupObject;
import com.mito.exobj.block.TileObjects;
import com.mito.exobj.main.ResisterItem;
import com.mito.exobj.utilities.MyUtil;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;

public class GroupPacketProcessor implements IMessage, IMessageHandler<GroupPacketProcessor, IMessage> {

	public int[] ids;
	public EnumGroupMode mode;
	public Vec3 pos = null;
	public double yaw;

	public GroupPacketProcessor() {
	}

	public GroupPacketProcessor(EnumGroupMode mode, List<ExtraObject> brace) {
		this.ids = new int[brace.size()];
		for (int n = 0; n < brace.size(); n++) {
			ids[n] = brace.get(n).BBID;
		}
		this.mode = mode;
	}

	public GroupPacketProcessor(EnumGroupMode mode, ExtraObject... brace) {
		this.ids = new int[brace.length];
		for (int n = 0; n < brace.length; n++) {
			ids[n] = brace[n].BBID;
		}
		this.mode = mode;
	}

	public GroupPacketProcessor(EnumGroupMode mode, List<ExtraObject> list, Vec3 pos, double yaw) {
		this(mode, list);
		this.pos = pos;
		this.yaw = yaw;
	}

	public GroupPacketProcessor(EnumGroupMode mode, List<ExtraObject> list, Vec3 pos) {
		this(mode, list);
		this.pos = pos;
	}

	@Override
	public IMessage onMessage(GroupPacketProcessor message, MessageContext ctx) {
		EntityPlayerMP player = ctx.getServerHandler().playerEntity;
		World world = DimensionManager.getWorld(player.dimension);
		BB_DataWorld data = BB_DataLists.getWorldData(world);
		List<ExtraObject> list = new ArrayList<ExtraObject>();
		for (int n : message.ids) {
			ExtraObject base = data.getBraceBaseByID(n);
			if (base != null) {
				list.add(base);
			}
		}
		switch (message.mode) {
		case COPY:
			for (ExtraObject base : list) {
				NBTTagCompound nbt = new NBTTagCompound();
				base.writeToNBTOptional(nbt);
				ExtraObject base1 = BB_ResisteredList.createExObjFromNBT(nbt, world);
				base1.addCoordinate(message.pos);
				base1.addToWorld();
			}
			break;
		case GUI:
			player.openGui(Main.INSTANCE, Main.GUI_ID_BBSelect, world, (int) player.posX, (int) player.posY, (int) player.posZ);
			break;
		case DELETE:
			for (int n = 0; n < list.size(); n++) {
				ExtraObject base = list.get(n);
				base.breakBrace(player);
			}
			break;
		case GROUPING:
			/*GroupObject group = new GroupObject(world, list.get(0).pos, list);
			group.addToWorld();
			for (int n = 0; n < list.size(); n++) {
				ExtraObject base = list.get(n);
				base.breakBrace(player);
			}*/
			break;
		case SETBLOCK:
			GroupObject group1 = new GroupObject(world, message.pos, MyUtil.copyList(list, message.pos));
			int ix = MathHelper.ceiling_double_int(message.pos.xCoord);
			int iy = MathHelper.ceiling_double_int(message.pos.yCoord);
			int iz = MathHelper.ceiling_double_int(message.pos.zCoord);
			world.setBlock(ix, iy, iz, ResisterItem.BlockObjects);
			TileEntity tile = world.getTileEntity(ix, iy, iz);
			if (tile != null && tile instanceof TileObjects) {
				TileObjects to = (TileObjects) tile;
				to.name = group1;
				to.markDirty();
			}
		default:
			break;
		}
		return null;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		mode = EnumGroupMode.values()[buf.readInt()];
		if (mode == EnumGroupMode.COPY) {
			double x = buf.readDouble();
			double y = buf.readDouble();
			double z = buf.readDouble();
			pos = Vec3.createVectorHelper(x, y, z);
			yaw = buf.readDouble();
		} else if (mode == EnumGroupMode.SETBLOCK) {
			double x = buf.readDouble();
			double y = buf.readDouble();
			double z = buf.readDouble();
			pos = Vec3.createVectorHelper(x, y, z);
		}
		this.ids = new int[buf.readInt()];
		for (int n = 0; n < ids.length; n++) {
			ids[n] = buf.readInt();
		}
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeInt(mode.ordinal());
		if (mode == EnumGroupMode.COPY) {
			buf.writeDouble(pos.xCoord);
			buf.writeDouble(pos.yCoord);
			buf.writeDouble(pos.zCoord);
			buf.writeDouble(yaw);
		} else if (mode == EnumGroupMode.SETBLOCK) {
			buf.writeDouble(pos.xCoord);
			buf.writeDouble(pos.yCoord);
			buf.writeDouble(pos.zCoord);
		}
		buf.writeInt(ids.length);
		for (int n = 0; n < ids.length; n++) {
			buf.writeInt(ids[n]);
		}
	}

	public enum EnumGroupMode {
		GUI, COPY, DELETE, GROUPING, SETBLOCK, MOVE;
	}

}
