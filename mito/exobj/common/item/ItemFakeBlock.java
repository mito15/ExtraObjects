package com.mito.exobj.common.item;

import com.mito.exobj.BraceBase.ExtraObject;
import com.mito.exobj.BraceBase.Brace.Brace;
import com.mito.exobj.BraceBase.Brace.FakeBlock;
import com.mito.exobj.client.BB_Key;
import com.mito.exobj.client.RenderHighLight;
import com.mito.exobj.common.Main;
import com.mito.exobj.common.MyLogger;
import com.mito.exobj.common.entity.EntityWrapperBB;
import com.mito.exobj.utilities.MitoMath;
import com.mito.exobj.utilities.MyUtil;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

public class ItemFakeBlock extends ItemBraceBase {

	public ItemFakeBlock() {
		super();
		this.setMaxDamage(0);
		this.setHasSubtypes(true);
	}

	public void onCreated(ItemStack itemstack, World world, EntityPlayer player) {
	}

	@Override
	public ItemStack onItemRightClick(ItemStack itemStack, World world, EntityPlayer player) {
		NBTTagCompound nbt = getTagCompound(itemStack);
		if (player.isSneaking()) {
			if (!world.isRemote)
				player.openGui(Main.INSTANCE, Main.INSTANCE.GUI_ID_FakeBlock, world, (int) player.posX, (int) player.posY, (int) player.posZ);
		} else {
			player.setItemInUse(itemStack, 71999);
		}
		return itemStack;
	}

	@Override
	public boolean getShareTag() {
		return true;
	}

	@Override
	public int getMaxItemUseDuration(ItemStack itemstack) {
		return 72000;
	}

	@Override
	public EnumAction getItemUseAction(ItemStack itemstack) {
		return EnumAction.none;
	}

	public void RightClick(ItemStack itemstack, World world, EntityPlayer player, MovingObjectPosition mop, BB_Key key, boolean p_77663_5_) {

		MovingObjectPosition mop1 = this.getMovingOPWithKey(itemstack, world, player, key, mop, 1.0);
		NBTTagCompound nbt = itemstack.getTagCompound();
		if (!world.isRemote) {
			Vec3 pos = MitoMath.copyVec3(mop.hitVec);
			MyLogger.info("a");
			Block block;

			if (key.isControlPressed()) {
				if (nbt != null && nbt.getBoolean("activated")) {
					nbt.setBoolean("activated", false);
					Vec3 set = Vec3.createVectorHelper(nbt.getDouble("setX"), nbt.getDouble("setY"), nbt.getDouble("setZ"));
				int x1 = Math.min((int) set.xCoord, (int) mop.hitVec.xCoord);
				int y1 = Math.min((int) set.yCoord, (int) mop.hitVec.yCoord);
				int z1 = Math.min((int) set.zCoord, (int) mop.hitVec.zCoord);
				int x2 = Math.max((int) set.xCoord, (int) mop.hitVec.xCoord);
				int y2 = Math.max((int) set.yCoord, (int) mop.hitVec.yCoord);
				int z2 = Math.max((int) set.zCoord, (int) mop.hitVec.zCoord);
				for (int ix = x1; ix <= x2; ix++) {
					for (int iy = y1; iy <= y2; iy++) {
						for (int iz = z1; iz <= z2; iz++) {
							block = world.getBlock(ix, iy, iz);
							if (block != null && block != Blocks.air) {
								world.setBlock(ix, iy, iz, Blocks.air);
								FakeBlock fake = new FakeBlock(world, Vec3.createVectorHelper(ix + 0.5, iy + 0.5, iz + 0.5), block, world.getBlockMetadata(ix, iy, iz));
								fake.addToWorld();
							}
						}
					}
				}
				} else {
					nbt.setBoolean("activated", true);
					nbt.setDouble("setX", mop.hitVec.xCoord);
					nbt.setDouble("setY", mop.hitVec.yCoord);
					nbt.setDouble("setZ", mop.hitVec.zCoord);
				}
				return;
			}

			if (!itemstack.hasTagCompound()) {
				itemstack.setTagCompound(new NBTTagCompound());
				itemstack.getTagCompound().setTag("Items", new NBTTagList());
			}
			NBTTagList tags = (NBTTagList) itemstack.getTagCompound().getTag("Items");
			if (tags == null) {
				tags = new NBTTagList();
			}

			NBTTagCompound tagCompound = tags.getCompoundTagAt(0);
			int slot = tagCompound.getByte("Slot");
			ItemStack i = ItemStack.loadItemStackFromNBT(tagCompound);

			if (i != null) {
				block = Block.getBlockFromItem(i.getItem());
				if (block != null && block != Blocks.air) {
					FakeBlock fake = new FakeBlock(world, pos, block, i.getItemDamage() & 15);
					fake.addToWorld();
				}
			}

		}
	}

	public void snapBlock(MovingObjectPosition mop, ItemStack itemstack, World world, EntityPlayer player, BB_Key key) {
		MyUtil.snapBlock(mop);
		MyUtil.snapBlockOffset(mop);
	}

	public void snapBraceBase(MovingObjectPosition mop, ItemStack itemstack, World world, EntityPlayer player, BB_Key key) {
		//各BraceBaseに振り分け用関数を用意
		ExtraObject base = ((EntityWrapperBB) mop.entityHit).base;
		if (base instanceof Brace) {
			Brace brace = (Brace) ((EntityWrapperBB) mop.entityHit).base;
			brace.snap(mop, snapCenter());
		} else if (base instanceof FakeBlock) {
			//muzui
		}
	}

	@Override
	public boolean drawHighLightBox(ItemStack itemstack, EntityPlayer player, float partialTicks, MovingObjectPosition mop) {
		NBTTagCompound nbt = getTagCompound(itemstack);
		if (mop == null || !MyUtil.canClick(player.worldObj, Main.proxy.getKey(), mop))
			return false;
		Vec3 set = mop.hitVec;
		RenderHighLight rh = RenderHighLight.INSTANCE;
		rh.drawBox(player, set, 0.95, partialTicks);
		return true;
	}

	public void onUpdate(ItemStack itemstack, World world, Entity entity, int meta, boolean p_77663_5_) {
	}

	@Override
	public boolean onDroppedByPlayer(ItemStack itemstack, EntityPlayer player) {
		return true;
	}

}
