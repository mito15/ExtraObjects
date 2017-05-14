package com.mito.exobj.item;

import com.mito.exobj.Main;
import com.mito.exobj.BraceBase.Brace.Tofu;
import com.mito.exobj.client.BB_Key;
import com.mito.exobj.client.render.RenderHighLight;
import com.mito.exobj.entity.EntityWrapperBB;
import com.mito.exobj.utilities.MyUtil;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

public class ItemTofu extends ItemSet {

	public byte key = 0;


	/*public static final String[] colorName = new String[] { "black", "red", "green", "brown", "blue", "purple", "cyan", "silver", "gray", "pink", "lime", "yellow", "lightBlue", "magenta", "orange", "white" };
	public static final String[] color_name = new String[] { "black", "red", "green", "brown", "blue", "purple", "cyan", "silver", "gray", "pink", "lime", "yellow", "light_blue", "magenta", "orange", "white" };
	*/
	public ItemTofu() {
		super();
		this.setMaxDamage(0);
		this.setTextureName("exobj:tofu");
	}

	@Override
	public String getItemStackDisplayName(ItemStack itemstack) {
		NBTTagCompound nbt = getTagCompound(itemstack);
		return ("" + StatCollector.translateToLocal(this.getUnlocalizedNameInefficiently(itemstack) + ".name")).trim();
	}

	public double convToDoubleSize(int isize) {

		return (double) isize * 0.05;
	}

	public Block getMaterial(ItemStack itemstack) {
		if (itemstack.getTagCompound() != null) {
			if (itemstack.getTagCompound().hasKey("material")) {
				Block b = Block.getBlockById(itemstack.getTagCompound().getInteger("material"));
				if (b.getIcon(0, 0) != null) {
					return b;
				}
			}
		}
		return Blocks.stone;

	}

	public ItemStack setMaterial(ItemStack itemstack, Block e) {
		NBTTagCompound nbt = getTagCompound(itemstack);
		nbt.setInteger("material", Block.getIdFromBlock(e));
		return itemstack;
	}

	public int getColor(ItemStack itemstack) {
		return itemstack.getItemDamage() & (16 - 1);
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
			MyUtil.snapByShiftKey(mop, set);
		}
	}

	public boolean activate(World world, EntityPlayer player, ItemStack itemstack, MovingObjectPosition mop, NBTTagCompound nbt, BB_Key key) {
		if (mop.typeOfHit == MovingObjectPosition.MovingObjectType.ENTITY && mop.entityHit != null && mop.entityHit instanceof EntityWrapperBB) {
			nbt.setInteger("brace", ((EntityWrapperBB) mop.entityHit).base.BBID);
		}
		return true;
	}

	public void onActiveClick(World world, EntityPlayer player, ItemStack itemstack, MovingObjectPosition movingOP, Vec3 set, Vec3 end, NBTTagCompound nbt) {
		int color = this.getColor(itemstack);
		Tofu tofu = new Tofu(world, set, end, this.getMaterial(itemstack), color);
		tofu.addToWorld();

		if (!player.capabilities.isCreativeMode) {
			itemstack.stackSize--;
			if (itemstack.stackSize == 0) {
				player.destroyCurrentEquippedItem();
			}
		}
	}

	@Override
	public void clientProcess(MovingObjectPosition mop, ItemStack itemstack) {
		NBTTagCompound nbt = itemstack.getTagCompound();
		if (nbt != null && nbt.getBoolean("activated")) {
			Vec3 pos = mop.hitVec;
			Block texture = getMaterial(itemstack);
			Main.proxy.playSound(new ResourceLocation(texture.stepSound.getBreakSound()), texture.stepSound.volume, texture.stepSound.getPitch(), (float) pos.xCoord, (float) pos.yCoord, (float) pos.zCoord);
		}
	}

	@Override
	public boolean drawHighLightBox(ItemStack itemStack, EntityPlayer player, float partialticks, MovingObjectPosition mop) {
		if (Minecraft.getMinecraft().currentScreen == null) {
			if (mop == null)
				return false;
			Vec3 set = mop.hitVec;
			RenderHighLight rh = RenderHighLight.INSTANCE;
			NBTTagCompound nbt = getTagCompound(itemStack);
			if (nbt.getBoolean("activated") && MyUtil.canClick(player.worldObj, Main.proxy.getKey(), mop)) {
				Vec3 end = Vec3.createVectorHelper(nbt.getDouble("setX"), nbt.getDouble("setY"), nbt.getDouble("setZ"));
				rh.drawBox(player, set, end, partialticks);
				return true;
			} else {
				return this.drawHighLightBrace(player, partialticks, mop);
			}
		}
		return false;
	}

}
