package com.mito.exobj.BraceBase.Brace;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import com.mito.exobj.BraceBase.BB_ResisteredList;
import com.mito.exobj.BraceBase.ExtraObject;
import com.mito.exobj.utilities.Line;
import com.mito.exobj.utilities.MitoMath;

import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

public class GroupObject extends ExtraObject {

	//pos > cpoint > end
	public List<ExtraObject> list = new ArrayList<ExtraObject>();

	public GroupObject(World world) {
		super(world);
	}

	public GroupObject(World world, Vec3 pos) {
		super(world, pos);
	}

	public GroupObject(World world, Vec3 pos, List<ExtraObject> list) {
		super(world, pos);
		this.list = list;
	}

	@Override
	public void onUpdate() {
		/*for (int n = 0; n < list.length; n++) {
			list[n].onUpdate();
		}*/
	}

	@Override
	protected void readExtraObjectFromNBT(NBTTagCompound nbt) {
		NBTTagList taglist = nbt.getTagList("exobjs", 10);
		NBTTagCompound nbt1 = taglist.getCompoundTagAt(0);
		list = new ArrayList<ExtraObject>();

		Map<Integer, ExtraObject> IntToBraceBaseMapping = new HashMap<Integer, ExtraObject>();
		//Map<ExtraObject, Integer> BraceBaseToIntMapping = new HashMap<ExtraObject, Integer>();

		for (int n = 0; n < taglist.tagCount(); ++n) {
			NBTTagCompound nbttagcompound4 = taglist.getCompoundTagAt(n);
			ExtraObject exobj = BB_ResisteredList.createExObjFromNBT(nbttagcompound4, this.worldObj);
			int n1 = nbttagcompound4.getInteger("exObjNumber") - 1;
			list.add(exobj);
			if (exobj != null) {
				IntToBraceBaseMapping.put(new Integer(n1), exobj);
				//BraceBaseToIntMapping.put(exobj, new Integer(n1));
			}
		}

		for (int n = 0; n < taglist.tagCount(); ++n) {
			ExtraObject base = list.get(n);
			NBTTagCompound nbttagcompound4 = taglist.getCompoundTagAt(n);
			if (base != null) {
				base.readNBTAssociate(nbttagcompound4, IntToBraceBaseMapping);
			}
		}
	}

	@Override
	protected void writeExtraObjectToNBT(NBTTagCompound nbt) {
		NBTTagList taglistGroup = new NBTTagList();
		Map<ExtraObject, Integer> BraceBaseToIntMapping = new HashMap<ExtraObject, Integer>();

		for (int n = 0; n < list.size(); n++) {
			ExtraObject exObj = list.get(n);
			BraceBaseToIntMapping.put(exObj, new Integer(n));
			//MyLogger.info("nbt associate(save) " + n + " : " + BB_ResisteredList.classToStringMapping.get(exObj.getClass()));
		}

		for (int n = 0; n < list.size(); n++) {
			ExtraObject exObj1 = list.get(n);
			NBTTagCompound nbt2 = new NBTTagCompound();
			nbt2.setInteger("exObjNumber", n + 1);
			if (exObj1.writeToNBTOptional(nbt2)) {
				taglistGroup.appendTag(nbt2);
				exObj1.writeNBTAssociate(nbt2, BraceBaseToIntMapping);
			}
		}
		nbt.setTag("exobjs", taglistGroup);
	}

	@Override
	public Line interactWithRay(Vec3 set, Vec3 end) {
		Line ret = null;
		for (int n = 0; n < list.size(); n++) {
			Line tem = list.get(n).interactWithRay(set, end);
			if (ret != null && MitoMath.subAbs2(set, tem.start) < MitoMath.subAbs2(set, tem.start)) {
				ret = tem;
			}
		}
		return ret;
	}

	public void addCollisionBoxesToList(World world, AxisAlignedBB aabb, List collidingBoundingBoxes, Entity entity) {
		for (int n = 0; n < list.size(); n++) {
			list.get(n).addCollisionBoxesToList(world, aabb, collidingBoundingBoxes, entity);
		}
	}

	public void rendertest(double x, double y, double z) {
		if (list != null) {
			Tessellator t = Tessellator.instance;
			GL11.glPushMatrix();
			//GL11.glEnable(GL12.GL_RESCALE_NORMAL);
			GL11.glTranslated(x, y, z);
			GL11.glLineWidth(5.0F);
			for (int n = 0; n < this.list.size(); n++) {
				if (list.get(n) != null && list.get(n) instanceof Brace) {
					Brace brace = (Brace) list.get(n);
					int i = worldObj.getBlockLightValue((int)x, (int)y, (int)z);
					int j = i % 65536;
					int k = i / 65536;
					GL11.glPushMatrix();
					GL11.glTranslated(brace.pos.xCoord, brace.pos.yCoord, brace.pos.zCoord);
					OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, (float) j / 1.0F, (float) k / 1.0F);
					//brace.shape.renderBraceAt(brace, 0);
					GL11.glPopMatrix();
				}
			}
			GL11.glDisable(GL12.GL_RESCALE_NORMAL);
			GL11.glPopMatrix();
		}
	}

	/*
	@Override
	public Vec3 interactWithLine(Vec3 s, Vec3 e) {
		return line == null ? null : line.interactWithLine(s, e);
	}
	
	public Vec3 getUnit() {
		if (this.unitVector == null) {
			this.unitVector = MitoMath.vectorSub(this.end, this.pos).normalize();
		}
		return this.unitVector;
	}
	
	
	public void breakBrace(EntityPlayer player) {
		if (!player.worldObj.isRemote) {
			if (!player.capabilities.isCreativeMode) {
				this.dropItem();
			}
	
			this.setDead();
			for (int n = 0; n < this.bindBraces.size(); n++) {
				this.bindBraces.get(n).setDead();
			}
		} else {
			Main.proxy.playSound(new ResourceLocation(this.texture.getBreakSound()), this.texture.getVolume(), this.texture.getPitch(), (float) pos.xCoord, (float) pos.yCoord, (float) pos.zCoord);
	
			//破壊時パーティクル
			//this.setDead();
			int b0 = (int) (this.size * 4) + 1;
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
			}
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
	
	public int getBrightnessForRender(float partialtick) {
		int i = MathHelper.floor_double((this.pos.xCoord + this.end.xCoord) / 2);
		int k = MathHelper.floor_double((this.pos.yCoord + this.end.yCoord) / 2);
		int j = MathHelper.floor_double((this.pos.zCoord + this.end.zCoord) / 2);
	
		if (this.worldObj.blockExists(i, 0, j)) {
			return this.worldObj.getLightBrightnessForSkyBlocks(i, k, j, 0);
		} else {
			return 0;
		}
	}
	
	@SideOnly(Side.CLIENT)
	public int getBrightnessForRender(float partialticks) {
		Vec3 v = this.getPos();
		int i = MathHelper.floor_double(v.xCoord);
		int j = MathHelper.floor_double(v.yCoord);
		int k = MathHelper.floor_double(v.zCoord);
		for (int n1 = 0; n1 < 6; n1++) {
			if (!this.worldObj.getBlock(i + Facing.offsetsXForSide[n1], j + Facing.offsetsYForSide[n1], k + Facing.offsetsZForSide[n1]).isOpaqueCube()) {
				return this.worldObj.getLightBrightnessForSkyBlocks(i + Facing.offsetsXForSide[n1], j + Facing.offsetsYForSide[n1], k + Facing.offsetsZForSide[n1], 0);
			}
		}
		if (this.worldObj.blockExists(i, 0, k)) {
			return this.worldObj.getLightBrightnessForSkyBlocks(i, j, k, 0);
		} else {
			return 0;
		}
	}
	
	public AxisAlignedBB getBoundingBox() {
		if(line == null){
			return null;
		}
		return line.getBoundingBox(size);
	}
	
	public void setRoll(double roll) {
		this.rotationRoll = roll;
		this.prevRotationRoll = roll;
		this.shouldUpdateRender = true;
	}
	
	public BezierCurve getBezierCurve() {
		BezierCurve bc;
		Vec3 va = this.end;
		Vec3 vb = MitoMath.vectorPul(va, this.offCurvePoints2);
		Vec3 vc = MitoMath.vectorPul(this.pos, this.offCurvePoints1);
		Vec3 vd = this.pos;
		if (this.offCurvePoints1.lengthVector() < 0.001) {
			bc = new BezierCurve(vd, vb, va);
		} else if (this.offCurvePoints2.lengthVector() < 0.001) {
			bc = new BezierCurve(vd, vc, va);
		} else {
			bc = new BezierCurve(vd, vc, vb, va);
		}
		return bc;
	}
	
	public void addCoordinate(double x, double y, double z) {
		this.pos = this.pos.addVector(x, y, z);
		if (line != null)
			line.addCoordinate(x, y, z);//this.end = this.end.addVector(x, y, z);
	}
	
	public double getMinY() {
		return line == null ? null : line.getMinY();//Math.min(pos.yCoord, end.yCoord);
	}
	
	public double getMaxY() {
		return line == null ? null : line.getMaxY();//Math.max(pos.yCoord, end.yCoord);
	}
	
	public Vec3 getPos() {
		return this.pos;
		//return line == null ? null : line.getPos();//MitoMath.vectorRatio(pos, end, 0.5);
	}
	
	public void addCollisionBoxesToList(World world, AxisAlignedBB aabb, List collidingBoundingBoxes, Entity entity) {
		line.addCollisionBoxesToList(world, aabb, collidingBoundingBoxes, entity, size);
	}
	
	public void rotation(Vec3 cent, double yaw) {
		Vec3 p = MitoMath.vectorPul(MitoMath.rotY(MitoMath.vectorSub(this.pos, cent), yaw), cent);
		end = MitoMath.vectorPul(MitoMath.rotY(MitoMath.vectorSub(this.end, cent), yaw), cent);
		offCurvePoints1 = MitoMath.rotY(offCurvePoints1, yaw);
		offCurvePoints2 = MitoMath.rotY(offCurvePoints2, yaw);
		if (line != null)
			line.rotation(cent, yaw);
		this.prevPos.xCoord = this.pos.xCoord = p.xCoord;
		this.prevPos.yCoord = this.pos.yCoord = p.yCoord;
		this.prevPos.zCoord = this.pos.zCoord = p.zCoord;
	}
	
	public void resize(Vec3 cent, double i) {
		Vec3 p = MitoMath.vectorPul(MitoMath.vectorMul(MitoMath.vectorSub(this.pos, cent), i), cent);
		//end = MitoMath.vectorPul(MitoMath.vectorMul(MitoMath.vectorSub(this.end, cent), i), cent);
		if (line != null)
			line.resize(cent, i);
		this.prevPos.xCoord = this.pos.xCoord = p.xCoord;
		this.prevPos.yCoord = this.pos.yCoord = p.yCoord;
		this.prevPos.zCoord = this.pos.zCoord = p.zCoord;
	}
	
	public void snap(MovingObjectPosition mop, boolean b) {
		this.line.snap(mop, b);
	}*/

}
