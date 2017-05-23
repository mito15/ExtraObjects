package com.mito.exobj.item;

import java.util.List;

import com.mito.exobj.BraceBase.BB_DataLists;
import com.mito.exobj.BraceBase.BB_DataWorld;
import com.mito.exobj.BraceBase.ExtraObject;
import com.mito.exobj.BraceBase.Brace.GuideBrace;
import com.mito.exobj.client.BB_Key;
import com.mito.exobj.client.render.RenderHighLight;
import com.mito.exobj.utilities.MyUtil;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.IIcon;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.StatCollector;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

public class ItemRuler extends ItemSet {

	public byte key = 0;

	@SideOnly(Side.CLIENT)
	private IIcon icon;

	public ItemRuler() {
		super();
		this.setMaxDamage(0);
		this.maxStackSize = 1;
		this.setHasSubtypes(true);
	}

	public void onCreated(ItemStack itemstack, World world, EntityPlayer player) {

		if (itemstack.getTagCompound() == null) {
			NBTTagCompound nbt = new NBTTagCompound();
			itemstack.setTagCompound(nbt);
		}
		itemstack.getTagCompound().setByte("pressedKey", (byte) 0);
	}

	public boolean activate(World world, EntityPlayer player, ItemStack itemstack, MovingObjectPosition movingOP, NBTTagCompound nbt, BB_Key key) {
		if (key.isShiftPressed()) {
			BB_DataWorld data = BB_DataLists.getWorldData(world);
			List<ExtraObject> list = data.braceBaseList;
			for (int n = 0; n < list.size(); n++) {
				ExtraObject base = list.get(n);
				if (base instanceof GuideBrace) {
					GuideBrace guide = (GuideBrace) base;
					guide.setDead();
					if (guide.name.equals(player.getDisplayName())) {
					}
				}
			}
			return false;
		}
		return true;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIconFromDamage(int par1) {
		return icon;
	}

	@Override
	public String getItemStackDisplayName(ItemStack itemstack) {
		return ("" + StatCollector.translateToLocal(this.getUnlocalizedNameInefficiently(itemstack) + ".name")).trim();
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IIconRegister iiconregister) {
		this.icon = iiconregister.registerIcon("exobj:ruler");
	}

	public int getDiv(ItemStack itemstack) {
		return (itemstack.getItemDamage() & (16 - 1)) + 1;
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

	public void onActiveClick(World world, EntityPlayer player, ItemStack itemstack, MovingObjectPosition movingOP, Vec3 set, Vec3 end, NBTTagCompound nbt) {

		int divine = this.getDiv(itemstack);
		GuideBrace guide = new GuideBrace(world, set, end, 0.2, player);
		guide.addToWorld();

	}

	@Override
	public boolean drawHighLightBox(ItemStack itemstack, EntityPlayer player, float partialTicks, MovingObjectPosition mop) {
		NBTTagCompound nbt = getTagCompound(itemstack);
		if (mop == null)
			return false;
		Vec3 set = mop.hitVec;

		RenderHighLight rh = RenderHighLight.INSTANCE;
		if (itemstack.getTagCompound() != null && itemstack.getTagCompound().getBoolean("activated")) {
			Vec3 end = Vec3.createVectorHelper(nbt.getDouble("setX"), nbt.getDouble("setY"), nbt.getDouble("setZ"));
			rh.drawRuler(player, set, end, this.getDiv(itemstack), partialTicks);
		} else {
			rh.drawCenter(player, set, partialTicks);
		}

		return true;

	}

	public boolean wheelEvent(EntityPlayer player, ItemStack stack, BB_Key key, int dwheel) {
		if (key.isShiftPressed()) {
			int w = dwheel / 120;
			int div = stack.getItemDamage() + w;
			if (div < 0) {
				div = 128;
			} else if (div > 128) {
				div = 0;
			}

			stack.setItemDamage(div);
			return true;
		}
		return false;
	}

}
