package com.mito.exobj.BraceBase;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

import com.mito.exobj.common.MyLogger;
import com.mito.exobj.network.BB_PacketProcessor;
import com.mito.exobj.network.BB_PacketProcessor.Mode;
import com.mito.exobj.network.PacketHandler;
import com.mito.exobj.utilities.Line;
import com.mito.exobj.utilities.MitoMath;
import com.mito.exobj.utilities.MyUtil;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagDouble;
import net.minecraft.nbt.NBTTagFloat;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ReportedException;
import net.minecraft.util.StatCollector;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

public abstract class ExtraObject {

	public static int nextID;

	public boolean isDead = false;
	public int frequency;
	public boolean isStatic = true;
	public Vec3 pos;
	//public Vec3 motion;
	public World worldObj;
	public UUID uuid;
	public int fire;
	public int BBID;
	private NBTTagCompound customFixedObjectData;

	public VBOList buffer = null;

	public double rotationYaw = 0;

	public double rotationPitch = 0;

	public double rotationRoll = 0;

	public Vec3 prevPos;

	static public int command = 0;
	public int currentCommand = -1;

	public double prevRotationYaw = 0;

	public double prevRotationPitch = 0;

	public double prevRotationRoll = 0;

	//private BB_ObjectsBinder braces = null;
	public List<ExtraObject> bindBraces = new ArrayList<ExtraObject>();
	public BB_DataWorld dataworld;
	public BB_DataChunk datachunk;
	public BB_DataGroup group = null;
	public boolean shouldUpdateRender = true;

	protected final Random random = new Random();

	//protected int brightness = -1;

	public ExtraObject(World world, Vec3 pos) {
		this(world);
		this.pos = pos;
		this.prevPos = MitoMath.copyVec3(pos);
	}

	public ExtraObject(World world) {
		this.worldObj = world;
		this.BBID = nextID++;
		this.dataworld = BB_DataLists.getWorldData(worldObj);
		if (dataworld == null) {
			MyLogger.warn("bracebase data world is null");
		}
		this.frequency = 100;
		this.isStatic = true;
		this.uuid = UUID.randomUUID();
	}

	public boolean addToWorld() {
		if (worldObj == null) {
			return false;
		}
		if (worldObj.isRemote) {
			return dataworld.addBraceBase(this, true);
		} else {
			boolean ret;
			if (pos != null) {
				ret = dataworld.addBraceBase(this, true);
			} else {
				ret = dataworld.addBraceBase(this, false);
			}
			if (ret) {
				PacketHandler.INSTANCE.sendToAll(new BB_PacketProcessor(Mode.SUGGEST, this));
			}
			return ret;
		}
	}

	public boolean connectBrace(ExtraObject base) {

		if (base == null) {
			return false;
		}
		if (!this.worldObj.isRemote) {
			this.bindDataGroup(base);
		}
		this.addBrace(base, 0);
		return base.addBrace(this, 0);
	}

	public boolean addBrace(ExtraObject base, int id) {
		boolean flag = false;
		if (base == null) {
			return false;
		} else if (id == 0) {
			flag = this.bindBraces.add(base);
		}
		if (!this.worldObj.isRemote && flag) {
			PacketHandler.INSTANCE.sendToAll(new BB_PacketProcessor(Mode.BIND, this.BBID, base.BBID, id));
		}

		return flag;
	}

	public boolean bindDataGroup(ExtraObject base) {
		if (this.group == null || this.group.single) {
			if (base.group == null || base.group.single) {
				BB_DataGroup group1 = new BB_DataGroup(base.worldObj, base.datachunk);
				group1.addToWorld();
				group1.add(base, this);
			} else {
				base.group.add(this);
			}
		} else {
			if (base.group == null || base.group.single) {
				this.group.add(base);
			} else {
				this.group.integrate(base.group);
			}
		}
		return true;
	}

	public boolean removeFromWorld() {
		if (this.group != null) {
			this.group.remove(this);
		}
		for (int i = 0; i < this.bindBraces.size(); i++) {
			if (this.bindBraces.get(i).bindBraces != null)
				this.bindBraces.get(i).bindBraces.remove(this);
		}
		return dataworld.removeBrace(this);
	}

	public void changeId(int id) {
		if (id != this.BBID) {
			this.dataworld.BBIDMap.removeObject(this.BBID);
			this.BBID = id;
			this.dataworld.BBIDMap.addKey(this.BBID, this);
		} else {
			return;
		}
	}

	public void setDead() {
		this.isDead = true;
	}

	public void move(Vec3 motion, int command) {
		if (this.currentCommand == command) {
			return;
		}
		this.currentCommand = command;

		this.pos = MitoMath.vectorSum(this.pos, motion);
		for (int n = 0; n < this.bindBraces.size(); n++) {
			ExtraObject base = this.bindBraces.get(n);
			base.move(motion, command);
		}
		this.isStatic = false;

	}

	public void stop(int command) {
		if (this.currentCommand == command) {
			return;
		}
		this.currentCommand = command;

		this.prevPos = MitoMath.copyVec3(this.pos);
		for (int n = 0; n < this.bindBraces.size(); n++) {
			ExtraObject base = this.bindBraces.get(n);
			base.stop(command);
		}
		this.isStatic = true;
		if (!this.worldObj.isRemote) {
			PacketHandler.INSTANCE.sendToAll(new BB_PacketProcessor(Mode.ADJUST, this.BBID, this.pos));
		}
	}

	public Vec3 moveRequest(Vec3 motion, int command, List<ExtraObject> list) {
		if (this.currentCommand == command) {
			return motion;
		}
		list.add(this);
		this.currentCommand = command;

		for (int n = 0; n < this.bindBraces.size(); n++) {
			ExtraObject base = this.bindBraces.get(n);
			motion = base.moveRequest(motion, command, list);
		}
		return motion;
	}

	public void onUpdate() {
	}

	public boolean renderOnWorldRender() {
		return false;
	}

	public NBTTagCompound getNBTTagCompound() {
		if (customFixedObjectData == null) {
			customFixedObjectData = new NBTTagCompound();
			this.writeToNBTOptional(customFixedObjectData);
		}
		return customFixedObjectData;
	}

	protected NBTTagList newDoubleNBTList(double... p_70087_1_) {
		NBTTagList nbttaglist = new NBTTagList();
		double[] adouble = p_70087_1_;
		int i = p_70087_1_.length;

		for (int j = 0; j < i; ++j) {
			double d1 = adouble[j];
			nbttaglist.appendTag(new NBTTagDouble(d1));
		}

		return nbttaglist;
	}

	protected NBTTagList newFloatNBTList(float... p_70049_1_) {
		NBTTagList nbttaglist = new NBTTagList();
		float[] afloat = p_70049_1_;
		int i = p_70049_1_.length;

		for (int j = 0; j < i; ++j) {
			float f1 = afloat[j];
			nbttaglist.appendTag(new NBTTagFloat(f1));
		}

		return nbttaglist;
	}

	public boolean writeToNBTOptional(NBTTagCompound p_70039_1_) {
		String s = BB_ResisteredList.getBraceBaseString(this);

		if (!this.isDead && s != null) {
			p_70039_1_.setString("id", s);
			this.writeToNBT(p_70039_1_);
			return true;
		} else {
			return false;
		}
	}

	public void writeToNBT(NBTTagCompound nbt) {
		try {
			nbt.setTag("Pos", this.newDoubleNBTList(new double[] { this.pos.xCoord, this.pos.yCoord, this.pos.zCoord }));
			//nbt.setTag("Motion", this.newDoubleNBTList(new double[] { this.motion.xCoord, this.motion.yCoord, this.motion.zCoord }));
			nbt.setTag("Rotation", this.newDoubleNBTList(new double[] { this.rotationYaw, this.rotationPitch, this.rotationRoll }));
			nbt.setShort("Fire", (short) this.fire);
			if (this.uuid != null) {
				nbt.setLong("UUIDMost", this.getUniqueID().getMostSignificantBits());
				nbt.setLong("UUIDLeast", this.getUniqueID().getLeastSignificantBits());
			}

			this.writeExtraObjectToNBT(nbt);

		} catch (Throwable throwable) {
			CrashReport crashreport = CrashReport.makeCrashReport(throwable, "Saving extra object NBT");
			CrashReportCategory crashreportcategory = crashreport.makeCategory("Extra object being saved");
			//this.addEntityCrashInfo(crashreportcategory);
			throw new ReportedException(crashreport);
		}
	}

	private UUID getUniqueID() {
		return this.uuid;
	}

	public void readFromNBT(NBTTagCompound nbt) {
		//		try {
		NBTTagList nbttaglist = nbt.getTagList("Pos", 6);
		//			NBTTagList nbttaglist1 = nbt.getTagList("Motion", 6);
		NBTTagList nbttaglist2 = nbt.getTagList("Rotation", 6);
		//			this.motion.xCoord = nbttaglist1.func_150309_d(0);
		//			this.motion.yCoord = nbttaglist1.func_150309_d(1);
		//			this.motion.zCoord = nbttaglist1.func_150309_d(2);
		//
		//			if (Math.abs(this.motion.xCoord) > 10.0D) {
		//				this.motion.xCoord = 0.0D;
		//			}
		//
		//			if (Math.abs(this.motion.yCoord) > 10.0D) {
		//				this.motion.yCoord = 0.0D;
		//			}
		//
		//			if (Math.abs(this.motion.zCoord) > 10.0D) {
		//				this.motion.zCoord = 0.0D;
		//			}

		this.prevPos = Vec3.createVectorHelper(0, 0, 0);
		this.pos = Vec3.createVectorHelper(0, 0, 0);
		this.prevPos.xCoord = this.pos.xCoord = nbttaglist.func_150309_d(0);
		this.prevPos.yCoord = this.pos.yCoord = nbttaglist.func_150309_d(1);
		this.prevPos.zCoord = this.pos.zCoord = nbttaglist.func_150309_d(2);
		this.prevRotationYaw = this.rotationYaw = nbttaglist2.func_150309_d(0);
		this.prevRotationPitch = this.rotationPitch = nbttaglist2.func_150309_d(1);
		this.prevRotationRoll = this.rotationRoll = nbttaglist2.func_150309_d(2);

		this.fire = nbt.getShort("Fire");

		if (nbt.hasKey("UUIDMost", 4) && nbt.hasKey("UUIDLeast", 4)) {
			this.uuid = new UUID(nbt.getLong("UUIDMost"), nbt.getLong("UUIDLeast"));
		}

		this.setPosition(this.pos.xCoord, this.pos.yCoord, this.pos.zCoord);
		//this.setRotation(this.rotationYaw, this.rotationPitch, this.rotationRoll);

		if (nbt.hasKey("PersistentIDMSB") && nbt.hasKey("PersistentIDLSB")) {
			this.uuid = new UUID(nbt.getLong("PersistentIDMSB"), nbt.getLong("PersistentIDLSB"));
		}
		this.readExtraObjectFromNBT(nbt);

		if (this.shouldSetPosAfterLoading()) {
			this.setPosition(this.pos.xCoord, this.pos.yCoord, this.pos.zCoord);
		}
		/*} catch (Throwable throwable) {
			CrashReport crashreport = CrashReport.makeCrashReport(throwable, "Loading fixed object NBT");
			CrashReportCategory crashreportcategory = crashreport.makeCategory("Fixed object being loaded");
			//this.addEntityCrashInfo(crashreportcategory);
			throw new ReportedException(crashreport);
		}*/
	}

	private void setPosition(double xCoord, double yCoord, double zCoord) {
		this.pos = Vec3.createVectorHelper(xCoord, yCoord, zCoord);

	}

	private boolean shouldSetPosAfterLoading() {
		return true;
	}

	//	public void addEntityCrashInfo(CrashReportCategory p_85029_1_) {
	//		p_85029_1_.addCrashSectionCallable("Entity Type", new Callable() {
	//			private static final String __OBFID = "CL_00001534";
	//
	//			public String call() {
	//				return FixedObjList.getFObjString(InstObj.this) + " (" + InstObj.this.getClass().getCanonicalName() + ")";
	//			}
	//		});
	//		p_85029_1_.addCrashSection("Entity ID", Integer.valueOf(this.InstObjID));
	//		p_85029_1_.addCrashSectionCallable("Entity Name", new Callable() {
	//			private static final String __OBFID = "CL_00001535";
	//
	//			public String call() {
	//				return InstObj.this.getCommandSenderName();
	//			}
	//		});
	//		p_85029_1_.addCrashSection("Fixed object\'s Exact location", String.format("%.2f, %.2f, %.2f", new Object[] { Double.valueOf(this.pos.xCoord), Double.valueOf(this.pos.yCoord), Double.valueOf(this.pos.zCoord) }));
	//		p_85029_1_.addCrashSection("Fixed object\'s Block location", CrashReportCategory.getLocationInfo(MathHelper.floor_double(this.pos.xCoord), MathHelper.floor_double(this.pos.yCoord), MathHelper.floor_double(this.pos.zCoord)));
	//		p_85029_1_.addCrashSection("Fixed object\'s Momentum", String.format("%.2f, %.2f, %.2f", new Object[] { Double.valueOf(this.motion.xCoord), Double.valueOf(this.motion.yCoord), Double.valueOf(this.motion.zCoord) }));
	//	}

	public String getCommandSenderName() {
		String s = BB_ResisteredList.getBraceBaseString(this);

		if (s == null) {
			s = "generic";
		}

		return StatCollector.translateToLocal("entity." + s + ".name");
	}

	/**
	 * (abstract) Protected helper method to read subclass braceBase data from NBT.
	 */
	protected abstract void readExtraObjectFromNBT(NBTTagCompound nbt);

	/**
	 * (abstract) Protected helper method to write subclass braceBase data to NBT.
	 */
	protected abstract void writeExtraObjectToNBT(NBTTagCompound nbt);

	public void onChunkLoad() {
	}

	@SideOnly(Side.CLIENT)
	public int getBrightnessForRender(float partialtick) {
		int i = MathHelper.floor_double(this.pos.xCoord);
		int j = MathHelper.floor_double(this.pos.zCoord);
		int k = MathHelper.floor_double(this.pos.yCoord);

		if (this.worldObj.blockExists(i, 0, j)) {
			return this.worldObj.getLightBrightnessForSkyBlocks(i, k, j, 0);
		} else {
			return 0;
		}
	}

	public boolean interactWithAABB(AxisAlignedBB boundingBox) {
		boolean ret = false;
		if (boundingBox.isVecInside(pos)) {
			ret = true;
		}
		return ret;
	}

	/*public BB_ObjectsBinder getBinder() {
		if (this.braces == null) {
			this.braces = new BB_ObjectsBinder(this);
		}
		return this.braces;
	}*/

	public double getYaw() {
		return this.rotationYaw;
	}

	public double getPitch() {
		return this.rotationPitch;
	}

	public double getRoll() {
		return this.rotationRoll;
	}

	public Vec3 interactWithLine(Vec3 s, Vec3 e) {
		return null;
	}

	//RayTrace
	public Line interactWithRay(Vec3 set, Vec3 end) {
		return null;
	}

	public boolean rightClick(EntityPlayer player, Vec3 pos, ItemStack itemstack) {
		if (player.capabilities.isCreativeMode) {
			//gui
		}
		return false;
	}

	public boolean leftClick(EntityPlayer player, ItemStack itemStack) {
		if (player.capabilities.isCreativeMode) {
			this.breakBrace(player);
			return true;
		}
		return false;
	}

	public void writeNBTAssociate(NBTTagCompound nbt, Map<ExtraObject, Integer> braceBaseToIntMapping) {
		try {
			int[] aint = new int[this.bindBraces.size()];
			for (int n = 0; n < this.bindBraces.size(); n++) {
				aint[n] = braceBaseToIntMapping.get(this.bindBraces.get(n));
			}
			nbt.setIntArray("Association", aint);

		} catch (Throwable throwable) {
			CrashReport crashreport = CrashReport.makeCrashReport(throwable, "Saving fixed object NBT");
			CrashReportCategory crashreportcategory = crashreport.makeCategory("Fixed object being saved");
			//this.addEntityCrashInfo(crashreportcategory);
			throw new ReportedException(crashreport);
		}
	}

	public void readNBTAssociate(NBTTagCompound nbt, Map<Integer, ExtraObject> intToBraceBaseMapping) {
		int[] aint = nbt.getIntArray("Association");
		for (int n = 0; n < aint.length; n++) {
			this.addBrace(intToBraceBaseMapping.get(aint[n]), 0);
		}
	}

	public AxisAlignedBB getBoundingBox() {
		return MyUtil.createAabbBySize(pos, 1.0);
	}

	public void addCoordinate(Vec3 v) {
		this.addCoordinate(v.xCoord, v.yCoord, v.zCoord);
	}

	public void addCoordinate(double x, double y, double z) {
		this.pos = this.pos.addVector(x, y, z);
		this.prevPos = this.prevPos.addVector(x, y, z);
	}

	public double getMinY() {
		return pos.yCoord;
	}

	public double getMaxY() {
		return pos.yCoord;
	}

	public Vec3 getPos() {
		return pos;
	}

	public void breakBrace(EntityPlayer player) {
		this.setDead();
	}

	public void addCollisionBoxesToList(World world, AxisAlignedBB aabb, List collidingBoundingBoxes, Entity entity) {

	}

	public void rotation(Vec3 cent, double yaw) {
	}

	public void resize(Vec3 c, double d) {
		// TODO 自動生成されたメソッド・スタブ

	}

	public void sendConnect() {
		if (!this.worldObj.isRemote) {
			for (int i = 0; i < this.bindBraces.size(); i++) {
				if (bindBraces.get(i) != null)
					PacketHandler.INSTANCE.sendToAll(new BB_PacketProcessor(Mode.BIND, this.BBID, bindBraces.get(i).BBID, 0));
			}
		}
	}

	public ExtraObject copy() {
		NBTTagCompound nbt = new NBTTagCompound();
		this.writeToNBTOptional(nbt);
		return BB_ResisteredList.createExObjFromNBT(nbt, null);
	}

}
