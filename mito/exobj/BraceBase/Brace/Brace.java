package com.mito.exobj.BraceBase.Brace;

import java.util.List;

import com.mito.exobj.Main;
import com.mito.exobj.MyLogger;
import com.mito.exobj.BraceBase.ExtraObject;
import com.mito.exobj.client.render.model.BezierCurve;
import com.mito.exobj.client.render.model.ILineBrace;
import com.mito.exobj.client.render.model.LineLoop;
import com.mito.exobj.item.ItemBar;
import com.mito.exobj.item.ItemBrace;
import com.mito.exobj.main.ResisterItem;
import com.mito.exobj.network.BB_PacketProcessor;
import com.mito.exobj.network.BB_PacketProcessor.Mode;
import com.mito.exobj.network.PacketHandler;
import com.mito.exobj.utilities.Line;
import com.mito.exobj.utilities.MitoMath;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.Facing;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

public class Brace extends ExtraObject {

	//pos > cpoint > end
	public Vec3 rand = Vec3.createVectorHelper(Math.random() * 0.002 - 0.001, Math.random() * 0.002 - 0.001, Math.random() * 0.002 - 0.001);
	public ILineBrace line = null;
	public int color = 0;
	public Block texture = Blocks.stone;
	public String shape;
	public String joint;
	public double size;
	private int debug = 0;
	public Vec3 unitVector = null;

	public Brace(World world) {
		super(world);
	}

	public Brace(World world, Vec3 pos) {
		super(world, pos);
	}

	public Brace(World world, Vec3 pos, Vec3 end, String shape, String joint, Block material, int tex, double size) {
		this(world, pos);
		this.line = new Line(pos, end);
		this.shape = shape;
		this.size = size;
		this.texture = material;
		this.color = tex;
		this.joint = joint;
	}

	@Override
	public void onUpdate() {
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
	public void move(Vec3 motion, int command) {
		if (this.currentCommand == command) {
			return;
		}
		this.currentCommand = command;
		this.pos = MitoMath.vectorSum(this.pos, motion);
		this.line.move(motion, command);
		for (int n = 0; n < this.bindBraces.size(); n++) {
			ExtraObject base = this.bindBraces.get(n);
			base.move(motion, command);
		}
		this.isStatic = false;
	}

	@Override
	public void readExtraObjectFromNBT(NBTTagCompound nbt) {
		//this.line.readNBT(nbt);
		if (nbt.hasKey("line")) {
			switch (nbt.getInteger("line")) {
			case 0:
				Vec3 start = getVec3(nbt, "start");
				Vec3 end = getVec3(nbt, "end");
				line = new Line(start, end);
				break;
			case 1:
				Vec3 v1 = getVec3(nbt, "bezier1");
				Vec3 v2 = getVec3(nbt, "bezier2");
				Vec3 v3 = getVec3(nbt, "bezier3");
				Vec3 v4 = getVec3(nbt, "bezier4");
				line = new BezierCurve(v1, v2, v3, v4);
				break;
			case 2:
				NBTTagList nbtList = nbt.getTagList("line_list", 10);
				MyLogger.info("brace read line loop " + nbtList.tagCount());
				if (nbtList.tagCount() > 1) {
					Vec3[] vs = new Vec3[nbtList.tagCount()];
					for (int l = 0; l < nbtList.tagCount(); ++l) {
						NBTTagCompound nbt1 = nbtList.getCompoundTagAt(l);
						vs[l] = getVec3(nbt1, "vec");
					}
					line = new LineLoop(vs);
				} else {
					this.setDead();
					line = new Line(Vec3.createVectorHelper(0, 0, 0), Vec3.createVectorHelper(0, 0, 0));
				}
				break;
			default:
				this.setDead();
				line = new Line(Vec3.createVectorHelper(0, 0, 0), Vec3.createVectorHelper(0, 0, 0));
				break;
			}
		} else {
			if (nbt.getBoolean("hasCP")) {
				Vec3 v1 = getVec3(nbt, "cp1");
				Vec3 v2 = getVec3(nbt, "cp2");
				Vec3 end = getVec3(nbt, "end");
				line = new BezierCurve(pos, v1, v2, end);
			} else {
				Vec3 end = getVec3(nbt, "end");
				line = new Line(pos, end);
			}
		}
		this.shape = nbt.getString("shape");
		this.joint = nbt.getString("joint");
		this.size = nbt.getDouble("size");
		if (nbt.hasKey("texture")) {
			this.texture = BB_EnumTexture.values()[nbt.getInteger("texture")].getBlock();
			this.color = 15 - nbt.getInteger("color");
		} else {
			this.texture = Block.getBlockById(nbt.getInteger("block"));
			this.color = nbt.getInteger("color");
		}
	}

	void setVec3(NBTTagCompound nbt, String name, Vec3 vec) {
		nbt.setDouble(name + "X", vec.xCoord);
		nbt.setDouble(name + "Y", vec.yCoord);
		nbt.setDouble(name + "Z", vec.zCoord);
	}

	Vec3 getVec3(NBTTagCompound nbt, String name) {
		return Vec3.createVectorHelper(nbt.getDouble(name + "X"), nbt.getDouble(name + "Y"), nbt.getDouble(name + "Z"));
	}

	@Override
	public void writeExtraObjectToNBT(NBTTagCompound nbt) {
		//MyLogger.info("save brace id " + this.BBID);
		if (line != null && shape != null) {
			line.writeNBT(nbt);
			nbt.setString("shape", this.shape);
			nbt.setString("joint", this.joint);
			nbt.setDouble("size", this.size);
			nbt.setInteger("block", Block.getIdFromBlock(texture));
			nbt.setInteger("color", this.color);
		}
	}

	@Override
	public boolean interactWithAABB(AxisAlignedBB boundingBox) {
		return line == null ? false : line.interactWithAABB(boundingBox, size);
	}

	@Override
	public Vec3 interactWithLine(Vec3 s, Vec3 e) {
		return line == null ? null : line.interactWithLine(s, e);
	}

	@Override
	public Line interactWithRay(Vec3 set, Vec3 end) {
		return line == null ? null : line.interactWithRay(set, end, size);
	}

	public void breakBrace(EntityPlayer player) {
		if (!player.worldObj.isRemote) {
			if (!player.capabilities.isCreativeMode) {
				this.dropItem();
			}
			this.setDead();
		} else {
			Main.proxy.playSound(new ResourceLocation(this.texture.stepSound.getBreakSound()), this.texture.stepSound.volume, this.texture.stepSound.getPitch(), (float) pos.xCoord, (float) pos.yCoord, (float) pos.zCoord);
			Main.proxy.particle(this);
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void particle() {
		int b0 = (int) (this.size * 3) + 1;
		Vec3 center = this.line.getPoint(0.5);
		int div = (int) (this.line.getLength() * 3) + 1;
		//MyLogger.info(this.line.getLength());

		for (int i1 = 0; i1 < b0; ++i1) {
			for (int j1 = 0; j1 < b0; ++j1) {
				for (int k1 = 0; k1 < div; ++k1) {
					Vec3 vec = this.line.getPoint((double) k1 / (double) div);
					double d0 = vec.xCoord + ((double) j1 * size) / (double) b0 - (size / 2);
					double d1 = vec.yCoord + ((double) i1 * size) / (double) b0 - (size / 2);
					double d2 = vec.zCoord + ((double) j1 * size) / (double) b0 - (size / 2);
					//Minecraft.getMinecraft().effectRenderer.addEffect((new EntityDiggingFX(this.worldObj, d0, d1, d2, d0 - center.xCoord, d1 - center.yCoord, d2 - center.zCoord, this.texture.getBlock(), this.color))
					//.applyColourMultiplier((int) center.xCoord, (int) center.yCoord, (int) center.zCoord));
					Main.proxy.addDiggingEffect(worldObj, center, d0, d1, d2, this.texture, color);
				}
			}
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
		} else if (player.isSneaking() && itemstack != null && Block.getBlockFromItem(itemstack.getItem()) != Blocks.air) {
			//MyLogger.info("brace " + texture.getLocalizedName());
			this.texture = Block.getBlockFromItem(itemstack.getItem());
			this.color = itemstack.getItemDamage() % 16;
			updateRenderer();
			PacketHandler.INSTANCE.sendToServer(new BB_PacketProcessor(Mode.SYNC, this));

		}
		return false;
	}

	public void dropItem() {

		float f = this.random.nextFloat() * 0.2F + 0.1F;
		float f1 = this.random.nextFloat() * 0.2F + 0.1F;
		float f2 = this.random.nextFloat() * 0.2F + 0.1F;

		ItemBrace brace = (ItemBrace) ResisterItem.ItemBrace;
		ItemStack itemstack1 = new ItemStack(ResisterItem.ItemBrace, 1, this.color);
		brace.setSize(itemstack1, (int) (this.size * 20));
		brace.setType(itemstack1, this.shape);
		brace.setDamage(itemstack1, color);
		brace.setMaterial(itemstack1, texture);

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

	/*public int getBrightnessForRender(float partialtick) {
		int i = MathHelper.floor_double((this.pos.xCoord + this.end.xCoord) / 2);
		int k = MathHelper.floor_double((this.pos.yCoord + this.end.yCoord) / 2);
		int j = MathHelper.floor_double((this.pos.zCoord + this.end.zCoord) / 2);
	
		if (this.worldObj.blockExists(i, 0, j)) {
			return this.worldObj.getLightBrightnessForSkyBlocks(i, k, j, 0);
		} else {
			return 0;
		}
	}*/

	@SideOnly(Side.CLIENT)
	@Override
	public int getBrightnessForRender(float partialticks, double x, double y, double z) {
		if (this.worldObj != null) {
			Vec3 v = this.getPos();
			int i = MathHelper.floor_double(v.xCoord + x);
			int j = MathHelper.floor_double(v.yCoord + y);
			int k = MathHelper.floor_double(v.zCoord + z);

			for (int n1 = 0; n1 < 6; n1++) {
				if (!this.worldObj.getBlock(i + Facing.offsetsXForSide[n1], j + Facing.offsetsYForSide[n1], k + Facing.offsetsZForSide[n1]).isOpaqueCube()) {
					return this.worldObj.getLightBrightnessForSkyBlocks(i + Facing.offsetsXForSide[n1], j + Facing.offsetsYForSide[n1], k + Facing.offsetsZForSide[n1], 0);
				}
			}
			if (this.worldObj.blockExists(i, 0, k)) {
				return this.worldObj.getLightBrightnessForSkyBlocks(i, j, k, 0);
			}
		}

		return 0;

	}

	public AxisAlignedBB getBoundingBox() {
		if (line == null) {
			return null;
		}
		return line.getBoundingBox(size);
	}

	public void setRoll(double roll) {
		this.rotationRoll = roll;
		this.prevRotationRoll = roll;
	}

	/*public BezierCurve getBezierCurve() {
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
	}*/

	public void addCoordinate(double x, double y, double z) {
		this.pos = this.pos.addVector(x, y, z);
		this.prevPos = this.prevPos.addVector(x, y, z);
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
		Vec3 p = MitoMath.vectorSum(MitoMath.rotY(MitoMath.sub_vector(this.pos, cent), yaw), cent);
		/*end = MitoMath.vectorPul(MitoMath.rotY(MitoMath.vectorSub(this.end, cent), yaw), cent);
		offCurvePoints1 = MitoMath.rotY(offCurvePoints1, yaw);
		offCurvePoints2 = MitoMath.rotY(offCurvePoints2, yaw);*/
		if (line != null)
			line.rotation(cent, yaw);
		this.prevPos.xCoord = this.pos.xCoord = p.xCoord;
		this.prevPos.yCoord = this.pos.yCoord = p.yCoord;
		this.prevPos.zCoord = this.pos.zCoord = p.zCoord;
	}

	public void resize(Vec3 cent, double i) {
		Vec3 p = MitoMath.vectorSum(MitoMath.vectorMul(MitoMath.sub_vector(this.pos, cent), i), cent);
		//end = MitoMath.vectorPul(MitoMath.vectorMul(MitoMath.vectorSub(this.end, cent), i), cent);
		if (line != null)
			line.resize(cent, i);
		this.prevPos.xCoord = this.pos.xCoord = p.xCoord;
		this.prevPos.yCoord = this.pos.yCoord = p.yCoord;
		this.prevPos.zCoord = this.pos.zCoord = p.zCoord;
	}

	public void snap(MovingObjectPosition mop, boolean b) {
		this.line.snap(mop, b);
	}

	public IIcon getIIcon(int i) {
		return texture.getIcon(i, color);
	}

}
