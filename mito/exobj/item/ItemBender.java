package com.mito.exobj.item;

import com.mito.exobj.Main;
import com.mito.exobj.BraceBase.BB_DataLists;
import com.mito.exobj.BraceBase.ExtraObject;
import com.mito.exobj.BraceBase.Brace.Brace;
import com.mito.exobj.client.BB_Key;
import com.mito.exobj.client.render.RenderHighLight;
import com.mito.exobj.client.render.model.BezierCurve;
import com.mito.exobj.entity.EntityWrapperBB;
import com.mito.exobj.network.BendPacketProcessor;
import com.mito.exobj.network.PacketHandler;
import com.mito.exobj.utilities.Line;
import com.mito.exobj.utilities.MitoMath;
import com.mito.exobj.utilities.MyUtil;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

public class ItemBender extends ItemSet {

	public byte key = 0;

	public ItemBender() {
		super();
		this.setTextureName("exobj:bender");
		this.setMaxDamage(0);
		this.maxStackSize = 1;
		this.setHasSubtypes(true);
	}

	@Override
	public void nbtInit(NBTTagCompound nbt, ItemStack itemstack) {
		super.nbtInit(nbt, itemstack);
		nbt.setInteger("brace", -1);
	}

	public double getRayDistance(BB_Key key) {
		return key.isAltPressed() ? 3.0 : 5.0;
	}

	public void snapDegree(MovingObjectPosition mop, ItemStack itemstack, World world, EntityPlayer player, BB_Key key, NBTTagCompound nbt) {
		if (nbt.getBoolean("activated")) {
			Vec3 set = Vec3.createVectorHelper(nbt.getDouble("setX"), nbt.getDouble("setY"), nbt.getDouble("setZ"));
			MyUtil.snapByShiftKey(mop, set);
		}
	}

	public boolean snapCenter() {
		return false;
	}

	public boolean activate(World world, EntityPlayer player, ItemStack itemstack, MovingObjectPosition mop, NBTTagCompound nbt, BB_Key key) {

		if (mop.entityHit != null && mop.entityHit instanceof EntityWrapperBB && ((EntityWrapperBB) mop.entityHit).base instanceof Brace && ((EntityWrapperBB) mop.entityHit).base.isStatic) {
			Vec3 set = mop.hitVec;
			Brace brace = (Brace) ((EntityWrapperBB) mop.entityHit).base;
			if (key.isShiftPressed()) {
				if (brace.line instanceof BezierCurve) {
					BezierCurve b = (BezierCurve) brace.line;
					brace.line = new Line(b.points[0], b.points[3]);
					PacketHandler.INSTANCE.sendToAll(new BendPacketProcessor(brace));
				}
				return false;
			}
			if (set.distanceTo(brace.line.getStart()) < 0.01) {
				nbt.setBoolean("isPos", true);
			} else {
				nbt.setBoolean("isPos", false);
			}
			nbt.setInteger("brace", brace.BBID);
			return true;
		}
		return false;
	}

	public void onActiveClick(World world, EntityPlayer player, ItemStack itemstack, MovingObjectPosition movingOP, Vec3 set, Vec3 end, NBTTagCompound nbt) {
		ExtraObject base = BB_DataLists.getWorldData(world).getBraceBaseByID(nbt.getInteger("brace"));
		if (base != null && base.isStatic && base instanceof Brace) {
			Brace brace = (Brace) base;
			/*if (brace.line instanceof Line) {
				brace.line = new LineLoop(brace.line.getStart(), end, brace.line.getEnd());
			} else if (brace.line instanceof LineLoop) {
				((LineLoop) brace.line).line.add(end);
			}
			PacketHandler.INSTANCE.sendToAll(new BB_PacketProcessor(Mode.SYNC, brace));*/
			if (nbt.getBoolean("isPos")) {
				if (brace.line instanceof Line) {
					brace.line = new BezierCurve(brace.line.getStart(), end, MitoMath.ratio_vector(brace.line.getEnd(), end, 0.8), brace.line.getEnd());
				} else if (brace.line instanceof BezierCurve) {
					BezierCurve b = (BezierCurve) brace.line;
					b.points[1] = end;
				}
				PacketHandler.INSTANCE.sendToAll(new BendPacketProcessor(brace, end, true));
			} else {
				if (brace.line instanceof Line) {
					brace.line = new BezierCurve(brace.line.getStart(), MitoMath.ratio_vector(brace.line.getStart(), end, 0.8), end, brace.line.getEnd());
				} else if (brace.line instanceof BezierCurve) {
					BezierCurve b = (BezierCurve) brace.line;
					b.points[2] = end;
				}
				PacketHandler.INSTANCE.sendToAll(new BendPacketProcessor(brace, end, false));
			}
		}
	}

	@Override
	public boolean drawHighLightBox(ItemStack itemstack, EntityPlayer player, float partialTicks, MovingObjectPosition mop) {
		NBTTagCompound nbt = getTagCompound(itemstack);
		if (mop == null || !MyUtil.canClick(player.worldObj, Main.proxy.getKey(), mop))
			return false;
		Vec3 set = mop.hitVec;

		RenderHighLight rh = RenderHighLight.INSTANCE;
		if (nbt.getBoolean("activated")) {
			Vec3 end = Vec3.createVectorHelper(nbt.getDouble("setX"), nbt.getDouble("setY"), nbt.getDouble("setZ"));
			rh.drawFakeBraceBend(player, set, nbt, partialTicks);
		} else {
			if (mop.typeOfHit == MovingObjectPosition.MovingObjectType.ENTITY && mop.entityHit != null && mop.entityHit instanceof EntityWrapperBB && ((EntityWrapperBB) mop.entityHit).base instanceof Brace) {
				rh.drawCenter(player, set, ((Brace) ((EntityWrapperBB) mop.entityHit).base).size / 2 + 0.1, partialTicks);
				this.drawHighLightBrace(player, partialTicks, mop);
			}
		}
		return true;
	}

}
