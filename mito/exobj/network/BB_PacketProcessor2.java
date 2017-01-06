package com.mito.exobj.network;

import com.mito.exobj.BraceBase.BB_DataWorld;
import com.mito.exobj.BraceBase.LoadClientWorldHandler;
import com.mito.exobj.common.Main;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.relauncher.Side;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;

public class BB_PacketProcessor2 implements IMessage, IMessageHandler<BB_PacketProcessor2, IMessage> {
	
	public BB_Packet packet;

	public BB_PacketProcessor2(BB_Packet packet) {
		this.packet = packet;
	}
	
	@Override
	public IMessage onMessage(BB_PacketProcessor2 message, MessageContext ctx) {
		if (ctx.side == Side.CLIENT) {
			World world = Main.proxy.getClientWorld();
			BB_DataWorld dataworld = LoadClientWorldHandler.INSTANCE.data;
		} else {
			EntityPlayerMP player = ctx.getServerHandler().playerEntity;
			World world = DimensionManager.getWorld(player.dimension);
		}
		return null;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		
	}

	@Override
	public void toBytes(ByteBuf buf) {
		
	}

}
