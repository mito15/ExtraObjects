package com.mito.exobj.network;

import com.mito.exobj.BraceBase.BB_DataLists;
import com.mito.exobj.BraceBase.ExtraObject;
import com.mito.exobj.BraceBase.Brace.Brace;
import com.mito.exobj.client.render.exorender.BezierCurve;
import com.mito.exobj.common.Main;
import com.mito.exobj.common.MyLogger;
import com.mito.exobj.utilities.Line;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import net.minecraft.util.Vec3;

public class BendPacketProcessor implements IMessage, IMessageHandler<BendPacketProcessor, IMessage> {

	public int id;
	public double x;
	public double y;
	public double z;
	public boolean isSetCP;

	public BendPacketProcessor() {
	}

	public BendPacketProcessor(Brace brace, Vec3 v, boolean isSet) {

		this.id = brace.BBID;
		this.x = v.xCoord;
		this.y = v.yCoord;
		this.z = v.zCoord;
		this.isSetCP = isSet;
	}

	@Override
	public IMessage onMessage(BendPacketProcessor message, MessageContext ctx) {
		Vec3 end = Vec3.createVectorHelper(message.x, message.y, message.z);
		ExtraObject base = BB_DataLists.getWorldData(Main.proxy.getClientWorld()).getBraceBaseByID(message.id);
		if (base != null && base.isStatic && base instanceof Brace) {
			Brace brace = (Brace) base;
			if (message.isSetCP) {
				if (brace.line instanceof Line) {
					brace.line = new BezierCurve(brace.line.getPoint(0.0), end, brace.line.getPoint(1.0), brace.line.getPoint(1.0));
				} else if (brace.line instanceof BezierCurve) {
					BezierCurve b = (BezierCurve) brace.line;
					b.points[1] = end;
				}
			} else {
				if (brace.line instanceof Line) {
					brace.line = new BezierCurve(brace.line.getPoint(0.0), brace.line.getPoint(0.0), end, brace.line.getPoint(1.0));
				} else if (brace.line instanceof BezierCurve) {
					BezierCurve b = (BezierCurve) brace.line;
					b.points[2] = end;
				}
			}
			BB_DataLists.getWorldData(Main.proxy.getClientWorld()).shouldUpdateRender = true;
			MyLogger.info("bend5");
		}
		return null;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		this.id = buf.readInt();
		this.x = buf.readDouble();
		this.y = buf.readDouble();
		this.z = buf.readDouble();
		this.isSetCP = buf.readBoolean();
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeInt(id);
		buf.writeDouble(x);
		buf.writeDouble(y);
		buf.writeDouble(z);
		buf.writeBoolean(this.isSetCP);
	}

}
