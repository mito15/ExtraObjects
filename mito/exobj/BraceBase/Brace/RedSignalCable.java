package com.mito.exobj.BraceBase.Brace;

import com.mito.exobj.BraceBase.BB_EnumTexture;
import com.mito.exobj.BraceBase.Brace.Render.BB_TypeResister;
import com.mito.exobj.common.Main;
import com.mito.exobj.common.item.ItemBar;
import com.mito.exobj.common.item.ItemBrace;
import com.mito.exobj.utilities.Line;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

public class RedSignalCable extends Brace {

	boolean light = false;

	public RedSignalCable(World world) {
		super(world);
	}

	public RedSignalCable(World world, Vec3 pos) {
		super(world, pos);
	}

	public RedSignalCable(World world, Vec3 pos, Vec3 end) {
		this(world, pos);
		this.line = new Line(pos, end);
		this.texture = BB_EnumTexture.REDSTONE;
		this.shape = BB_TypeResister.getFigure("square");
		this.size = 0.1;
		this.color = 0;
	}
	
	@SideOnly(Side.CLIENT)
	public int getBrightnessForRender(float partialticks) {
		if(this.light){
			return 15 << 20;
		} else {
			return (int)((double)super.getBrightnessForRender(partialticks) / 2);
		}
	}

	@Override
	public void onUpdate() {
		if (this.line instanceof Line) {
			Line l = (Line) this.line;
			Vec3 v = l.start;
			boolean a = this.worldObj.isBlockIndirectlyGettingPowered(MathHelper.floor_double(v.xCoord), MathHelper.floor_double(v.yCoord), MathHelper.floor_double(v.zCoord));
			if (a && !this.light) {
				this.light = true;
				this.shouldUpdateRender = true;
			} else if (!a && this.light) {
				this.light = false;
				this.shouldUpdateRender = true;
			}
		}
		/*if(!this.equals(this.dataworld.getBraceBaseByID(this.BBID))){
			mitoLogger.info("delete phase mmm");
		}*/
		/*debug++;
		if (debug == 20) {
			int i = MathHelper.floor_double(this.pos.xCoord / 16.0D);
			int j = MathHelper.floor_double(this.pos.zCoord / 16.0D);
			//if (!this.worldObj.isRemote) {
		
			BB_DataChunk ret = (BB_DataChunk) BB_DataLists.getWorldData(worldObj).coordToDataMapping.getValueByKey(ChunkCoordIntPair.chunkXZ2Int(i, j));
			//mitoLogger.info("Im here(ID:" + this.BBID + ")  " + "chunk data: " + this.group.list.size() + "  : " + this.group.datachunk.xPosition + ", " + this.group.datachunk.zPosition + "  "
			//		+ BB_DataLists.getWorldData(worldObj).braceBaseList.size() + "  " + this.isDead);
			//}
			debug = 0;
		}*/
	}

	@Override
	protected void readExtraObjectFromNBT(NBTTagCompound nbt) {
		super.readExtraObjectFromNBT(nbt);
	}

	@Override
	protected void writeExtraObjectToNBT(NBTTagCompound nbt) {
		super.writeExtraObjectToNBT(nbt);
	}

	public void breakBrace(EntityPlayer player) {
		if (!player.worldObj.isRemote) {
			if (!player.capabilities.isCreativeMode) {
				this.dropItem();
			}

			this.setDead();
			/*for (int n = 0; n < this.bindBraces.size(); n++) {
				this.bindBraces.get(n).setDead();
			}*/
		} else {
			Main.proxy.playSound(new ResourceLocation(this.texture.getBreakSound()), this.texture.getVolume(), this.texture.getPitch(), (float) pos.xCoord, (float) pos.yCoord, (float) pos.zCoord);

			//破壊時パーティクル
			//this.setDead();
			/*int b0 = (int) (this.size * 4) + 1;
			Vec3 center = MitoMath.vectorRatio(this.end, this.pos, 0.5);
			int div = (int) (MitoMath.subAbs(this.pos, this.end) * 4) + 1;
			Vec3 vec = MitoMath.vectorSub(this.end, this.pos);
			
			for (int i1 = 0; i1 < b0; ++i1) {
				for (int j1 = 0; j1 < b0; ++j1) {
					for (int k1 = 0; k1 < div; ++k1) {
						double d0 = this.pos.xCoord + vec.xCoord * (double) k1 / (double) div + ((double) j1 * size) / (double) b0 - (size / 2);
						double d1 = this.pos.yCoord + vec.yCoord * (double) k1 / (double) div + ((double) i1 * size) / (double) b0 - (size / 2);
						double d2 = this.pos.zCoord + vec.zCoord * (double) k1 / (double) div + ((double) j1 * size) / (double) b0 - (size / 2);
						//Minecraft.getMinecraft().effectRenderer.addEffect((new EntityDiggingFX(this.worldObj, d0, d1, d2, d0 - center.xCoord, d1 - center.yCoord, d2 - center.zCoord, this.texture.getBlock(), this.color))
						//.applyColourMultiplier((int) center.xCoord, (int) center.yCoord, (int) center.zCoord));
						BAO_main.proxy.addDiggingEffect(worldObj, center, d0, d1, d2, this.texture.getBlock(), color);
					}
				}
			}*/
			if (line != null)
				line.particle();
		}
	}

	public boolean leftClick(EntityPlayer player, ItemStack itemstack) {
		if (player.capabilities.isCreativeMode) {
			this.breakBrace(player);
			return true;
		} else if (itemstack != null && itemstack.getItem() instanceof ItemBar) {
			this.breakBrace(player);
			return true;
		}
		return false;
	}

	public boolean rightClick(EntityPlayer player, Vec3 pos, ItemStack itemstack) {
		if (itemstack != null && itemstack.getItem() instanceof ItemBar) {
			this.breakBrace(player);
			return true;
		}
		return false;
	}

	public void dropItem() {

		float f = this.random.nextFloat() * 0.2F + 0.1F;
		float f1 = this.random.nextFloat() * 0.2F + 0.1F;
		float f2 = this.random.nextFloat() * 0.2F + 0.1F;

		ItemBrace brace = (ItemBrace) Main.ItemBrace;
		ItemStack itemstack1 = new ItemStack(Main.ItemBrace, 1, this.color);
		brace.setSize(itemstack1, (int) (this.size * 20));
		brace.setType(itemstack1, BB_TypeResister.getName(this.shape));

		NBTTagCompound nbt = itemstack1.getTagCompound();
		itemstack1.setTagCompound(nbt);
		nbt.setBoolean("activated", false);
		nbt.setDouble("setX", 0.0D);
		nbt.setDouble("setY", 0.0D);
		nbt.setDouble("setZ", 0.0D);
		nbt.setBoolean("useFlag", false);
		EntityItem entityitem = new EntityItem(worldObj, (double) ((float) this.pos.xCoord + f), (double) ((float) this.pos.yCoord + f1), (double) ((float) this.pos.zCoord + f2), itemstack1);

		float f3 = 0.05F;
		entityitem.motionX = (double) ((float) this.random.nextGaussian() * f3);
		entityitem.motionY = (double) ((float) this.random.nextGaussian() * f3 + 0.2F);
		entityitem.motionZ = (double) ((float) this.random.nextGaussian() * f3);
		worldObj.spawnEntityInWorld(entityitem);
	}

}
