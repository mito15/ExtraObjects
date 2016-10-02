package com.mito.exobj.common.item;

import com.mito.exobj.BraceBase.BB_DataLists;
import com.mito.exobj.BraceBase.BB_EnumTexture;
import com.mito.exobj.BraceBase.ExtraObject;
import com.mito.exobj.BraceBase.Brace.Brace;
import com.mito.exobj.BraceBase.Brace.RedSignalCable;
import com.mito.exobj.client.BB_Key;
import com.mito.exobj.client.RenderHighLight;
import com.mito.exobj.common.Main;
import com.mito.exobj.common.entity.EntityWrapperBB;
import com.mito.exobj.utilities.MitoMath;
import com.mito.exobj.utilities.MitoUtil;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

public class ItemRedCable extends ItemSet {


	private double size = 0.1;

	public ItemRedCable() {
		super();
		this.setMaxDamage(0);
	}

	
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
			MitoUtil.snapByShiftKey(mop, set);
		}
	}

	public void activate(World world, EntityPlayer player, ItemStack itemstack, MovingObjectPosition mop, NBTTagCompound nbt) {
		if (mop.typeOfHit == MovingObjectPosition.MovingObjectType.ENTITY && mop.entityHit != null && mop.entityHit instanceof EntityWrapperBB) {
			nbt.setInteger("brace", ((EntityWrapperBB) mop.entityHit).base.BBID);
		}
	}
	@Override
	public void clientProcess(MovingObjectPosition mop, ItemStack itemstack) {
		NBTTagCompound nbt = itemstack.getTagCompound();
		if (nbt != null && nbt.getBoolean("activated")) {
			Vec3 pos = mop.hitVec;
			BB_EnumTexture texture = BB_EnumTexture.REDSTONE;
			Main.proxy.playSound(new ResourceLocation(texture.getBreakSound()), texture.getVolume(), texture.getPitch(), (float) pos.xCoord, (float) pos.yCoord, (float) pos.zCoord);
		}
	}

	@Override
	public boolean drawHighLightBox(ItemStack itemstack, EntityPlayer player, float partialTicks, MovingObjectPosition mop) {
		NBTTagCompound nbt = getTagCompound(itemstack);
		double size = this.size ;
		if (mop == null || !MitoUtil.canClick(player.worldObj, Main.proxy.getKey(), mop))
			return false;
		Vec3 set = mop.hitVec;

		RenderHighLight rh = RenderHighLight.INSTANCE;
		if (nbt.getBoolean("activated")) {
			Vec3 end = Vec3.createVectorHelper(nbt.getDouble("setX"), nbt.getDouble("setY"), nbt.getDouble("setZ"));
			rh.drawFakeBrace(player, set, end, size, partialTicks);
		} else {
			ExtraObject base = null;
			if (mop.typeOfHit == MovingObjectPosition.MovingObjectType.ENTITY && mop.entityHit != null && mop.entityHit instanceof EntityWrapperBB) {
				base = ((EntityWrapperBB) mop.entityHit).base;
			}
			if (base != null && base instanceof Brace && size < ((Brace) base).size) {
				rh.drawCenter(player, set, ((Brace) base).size / 2 + 0.1, partialTicks);
			} else {
				rh.drawBox(player, set, size, partialTicks);
			}
		}

		return true;

	}
	
	public void onActiveClick(World world, EntityPlayer player, ItemStack itemstack, MovingObjectPosition movingOP, Vec3 set, Vec3 end, NBTTagCompound nbt) {
		if (MitoMath.subAbs(set, end) < 100) {
			Brace brace = new RedSignalCable(world, set, end);
			brace.addToWorld();
			//EntityBrace brace = new EntityBrace(world, set, end, this.getSize(itemstack), color, (byte)1);
			//world.spawnEntityInWorld(brace);
			if (MitoUtil.isBrace(movingOP) && MitoUtil.getBrace(movingOP).isStatic) {
				ExtraObject base = MitoUtil.getBrace(movingOP);
				brace.connectBrace(base);
			}
			ExtraObject base = BB_DataLists.getWorldData(world).getBraceBaseByID(nbt.getInteger("brace"));
			if (base != null && base.isStatic) {
				brace.connectBrace(base);
			}
		}
		if (!player.capabilities.isCreativeMode) {
			itemstack.stackSize--;
			if (itemstack.stackSize == 0) {
				player.destroyCurrentEquippedItem();
			}
		}
	}

}
