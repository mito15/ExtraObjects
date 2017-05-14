package com.mito.exobj.item;

import com.mito.exobj.Main;
import com.mito.exobj.client.BB_Key;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;

public class ItemGroup extends ItemBraceBase {

	int size = 2;
	public double[] sizeArray = { 0.2, 1.0, 3.0, 0.005 };

	public ItemGroup() {
		super();
		this.setTextureName("exobj:bar");
		this.setCreativeTab(Main.tab);
		this.maxStackSize = 1;
		this.setMaxDamage(3);

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

	public void RightClick(ItemStack itemstack, World world, EntityPlayer player, BB_Key key, boolean p_77663_5_) {

	}

	public void onUpdate(ItemStack itemstack, World world, Entity entity, int meta, boolean p_77663_5_) {
	}

	@Override
	public boolean drawHighLightBox(ItemStack itemstack, EntityPlayer player, float partialticks, MovingObjectPosition mop) {
		return this.drawHighLightBrace(player, partialticks, mop);
	}

	public boolean wheelEvent(EntityPlayer player, ItemStack stack, BB_Key key, int dwheel) {
		return false;
	}

}
