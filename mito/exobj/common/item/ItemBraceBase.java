package com.mito.exobj.common.item;

import org.lwjgl.opengl.GL11;

import com.mito.exobj.BraceBase.BB_Render;
import com.mito.exobj.BraceBase.BB_ResisteredList;
import com.mito.exobj.BraceBase.ExtraObject;
import com.mito.exobj.BraceBase.Brace.Brace;
import com.mito.exobj.client.BB_Key;
import com.mito.exobj.common.Main;
import com.mito.exobj.common.entity.EntityWrapperBB;
import com.mito.exobj.network.ItemUsePacketProcessor;
import com.mito.exobj.network.PacketHandler;
import com.mito.exobj.utilities.MitoUtil;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;

public class ItemBraceBase extends Item {

	public ItemBraceBase() {
		super();

	}

	public boolean isDamageable() {
		return false;
	}

	public boolean showDurabilityBar(ItemStack stack) {
		return false;
	}

	@Override
	public int getMaxItemUseDuration(ItemStack p_77626_1_) {
		return 72000;
	}

	@Override
	public ItemStack onItemRightClick(ItemStack itemstack, World world, EntityPlayer player) {

		player.setItemInUse(itemstack, 71999);
		return itemstack;
	}

	@Override
	public void onPlayerStoppedUsing(ItemStack itemstack, World world, EntityPlayer player, int i) {

		NBTTagCompound nbt = itemstack.getTagCompound();
		if (world.isRemote) {
			PacketHandler.INSTANCE.sendToServer(new ItemUsePacketProcessor(Main.proxy.getKey(), player.inventory.currentItem));
		}
		player.clearItemInUse();

	}

	public void RightClick(ItemStack itemstack, World world, EntityPlayer player, MovingObjectPosition mop, BB_Key key, boolean p_77663_5_) {

	}

	public void onUpdate(ItemStack itemstack, World world, Entity entity, int meta, boolean p_77663_5_) {

	}

	public MovingObjectPosition getMovingOPWithKey(ItemStack itemstack, World world, EntityPlayer player, BB_Key key, MovingObjectPosition mop, double partialticks) {
		NBTTagCompound nbt = this.getNBT(itemstack);

		if (mop != null && MitoUtil.canClick(world, key, mop)) {
			if (!key.isControlPressed()) {
				mop = this.snap(mop, itemstack, world, player, key, nbt);
			}
			if (key.isShiftPressed()) {
				this.snapDegree(mop, itemstack, world, player, key, nbt);
			}
		}

		return mop;
	}

	public double getRayDistance(BB_Key key) {
		return 5.0;
	}

	public MovingObjectPosition snap(MovingObjectPosition mop, ItemStack itemstack, World world, EntityPlayer player, BB_Key key, NBTTagCompound nbt) {
		if (mop.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK) {
			this.snapBlock(mop, itemstack, world, player, key);
		} else if (mop.typeOfHit == MovingObjectPosition.MovingObjectType.ENTITY && mop.entityHit != null && mop.entityHit instanceof EntityWrapperBB) {
			this.snapBraceBase(mop, itemstack, world, player, key);
		}
		return mop;
	}

	public void snapBlock(MovingObjectPosition mop, ItemStack itemstack, World world, EntityPlayer player, BB_Key key) {
		MitoUtil.snapBlock(mop);
	}

	public void snapBraceBase(MovingObjectPosition mop, ItemStack itemstack, World world, EntityPlayer player, BB_Key key) {
		//各BraceBaseに振り分け用関数を用意
		if (((EntityWrapperBB)mop.entityHit).base instanceof Brace) {
			Brace brace = (Brace) ((EntityWrapperBB)mop.entityHit).base;
			brace.snap(mop, this.snapCenter());
		}
	}

	public void snapDegree(MovingObjectPosition mop, ItemStack itemstack, World world, EntityPlayer player, BB_Key key, NBTTagCompound nbt) {
	}

	public boolean snapCenter() {
		return true;
	}

	public boolean drawHighLightBox(ItemStack itemstack, EntityPlayer player, float partialTick, MovingObjectPosition mop) {
		return false;
	}

	public NBTTagCompound getNBT(ItemStack itemstack) {
		NBTTagCompound nbt = itemstack.getTagCompound();

		if (nbt == null) {
			nbt = new NBTTagCompound();
			itemstack.setTagCompound(nbt);
			this.nbtInit(nbt, itemstack);
		}

		return nbt;
	}

	public void nbtInit(NBTTagCompound nbt, ItemStack itemstack) {
	}

	public boolean drawHighLightBrace(EntityPlayer player, float partialticks, MovingObjectPosition mop) {
		if (mop != null) {
			if (MitoUtil.isBrace(mop)) {
				ExtraObject base = ((EntityWrapperBB)mop.entityHit).base;
				GL11.glMatrixMode(GL11.GL_MODELVIEW);
				GL11.glPushMatrix();
				GL11.glTranslated(-(player.lastTickPosX + (player.posX - player.lastTickPosX) * partialticks),
						-(player.lastTickPosY + (player.posY - player.lastTickPosY) * partialticks),
						-(player.lastTickPosZ + (player.posZ - player.lastTickPosZ) * partialticks));
				//BB_Render render = BB_ResisteredList.getBraceBaseRender(brace);
				BB_Render render = BB_ResisteredList.getBraceBaseRender(base);
				render.drawHighLight(base, partialticks);
				GL11.glPopMatrix();
				return true;
			}
		}
		return false;
	}

	public boolean wheelEvent(EntityPlayer player, ItemStack stack, BB_Key key, int dwheel) {
		return false;
	}

	public NBTTagCompound getTagCompound(ItemStack stack){
		NBTTagCompound nbt = stack.getTagCompound();
		if (nbt == null) {
			nbt = new NBTTagCompound();
		}
		return nbt;
	}

}
