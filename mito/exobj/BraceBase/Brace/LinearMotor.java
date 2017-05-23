package com.mito.exobj.BraceBase.Brace;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.mito.exobj.BraceBase.ExtraObject;
import com.mito.exobj.item.ItemBar;
import com.mito.exobj.item.ItemBraceBase;
import com.mito.exobj.network.BB_PacketProcessor;
import com.mito.exobj.network.BB_PacketProcessor.Mode;
import com.mito.exobj.network.PacketHandler;
import com.mito.exobj.utilities.Line;
import com.mito.exobj.utilities.MitoMath;
import com.mito.exobj.utilities.MyLogger;

import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ReportedException;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

public class LinearMotor extends ExtraObject {

	public Brace railBrace;
	public boolean active = false;
	public double speed = 0.05;
	//trueでstart>end方向
	public boolean direction = true;
	private int debug;

	public LinearMotor(World world) {
		super(world);
	}

	public LinearMotor(World world, Vec3 pos) {
		super(world, pos);
	}

	public boolean connectBrace(ExtraObject base) {

		if (base == null || !(base instanceof Brace)) {
			return false;
		}
		if (!this.worldObj.isRemote) {
			this.bindDataGroup(base);
		}
		this.addBrace(base, 1);
		return base.addBrace(this, 0);
	}

	public boolean addBrace(ExtraObject base, int id) {

		boolean flag = false;
		if (base == null) {
			return false;
		} else if (id == 0) {
			flag = this.bindBraces.add(base);
		} else if (id == 1) {
			this.railBrace = (Brace) base;
			flag = true;
		}
		if (!this.worldObj.isRemote && flag) {
			PacketHandler.INSTANCE.sendToAll(new BB_PacketProcessor(Mode.BIND, this.BBID, base.BBID, id));
		}

		return flag;
	}

	public boolean removeFromWorld() {
		for (int i = 0; i < this.bindBraces.size(); i++) {
			this.bindBraces.get(i).bindBraces.remove(this);
		}
		if (this.railBrace != null && this.railBrace.bindBraces != null) {
			this.railBrace.bindBraces.remove(this);
		}
		this.bindBraces = null;
		return dataworld.removeBrace(this);
	}

	public void moveLinearMotor() {
		/*Vec3 motion = this.railBrace.line.getMotion(this.pos, this.speed, this.direction);

		if (motion == null) {
			direction = !direction;
			return;
		}
		List<ExtraObject> list = new ArrayList<ExtraObject>();
		Vec3 motion1 = this.moveRequest(motion, command++, list);
		this.moveEntity(motion1, list);
		//this.move(motion1, command++);
*/	}

	public void moveEntity(Vec3 m, List<ExtraObject> objs) {
		List<Entity> list = new ArrayList<Entity>();
		List<Entity> list2 = new ArrayList<Entity>();
		for (int n = 0; n < objs.size(); n++) {
			List<Entity> list1 = this.worldObj.getEntitiesWithinAABBExcludingEntity(null, objs.get(n).getBoundingBox().expand(speed, speed, speed));
			for (int n2 = 0; n2 < list1.size(); n2++) {
				if (!list.contains(list1.get(n2))) {
					list.add(list1.get(n2));
				}
			}
		}
		Iterator i = list.iterator();
		while (i.hasNext()) {
			Entity entity = (Entity) i.next();
			if (!(entity instanceof EntityPlayer) || (this.worldObj.isRemote)) {

				AxisAlignedBB copy = entity.boundingBox.copy();
				List<AxisAlignedBB> colli = new ArrayList<AxisAlignedBB>();

				double dx1 = -m.xCoord;
				double dy1 = -m.yCoord;
				double dz1 = -m.zCoord;

				for (int n1 = 0; n1 < objs.size(); n1++) {
					objs.get(n1).addCollisionBoxesToList(worldObj, copy.addCoord(dx1, dy1, dz1), colli, entity);
				}

				List<AxisAlignedBB> neko = new ArrayList<AxisAlignedBB>();

				for (int n1 = 0; n1 < colli.size(); n1++) {
					dy1 = colli.get(n1).calculateYOffset(copy, dy1);
				}
				copy.offset(0, dy1, 0);

				for (int n1 = 0; n1 < colli.size(); n1++) {
					dx1 = colli.get(n1).calculateXOffset(copy, dx1);
				}
				copy.offset(dx1, 0, 0);

				for (int n1 = 0; n1 < colli.size(); n1++) {
					dz1 = colli.get(n1).calculateZOffset(copy, dz1);
				}
				copy.offset(0, 0, dz1);

				colli = new ArrayList<AxisAlignedBB>();

				if (dx1 == -m.xCoord && dz1 == -m.zCoord) {
					for (int n1 = 0; n1 < objs.size(); n1++) {
						objs.get(n1).addCollisionBoxesToList(worldObj, entity.boundingBox.expand(0, 0.1, 0), colli, entity);
					}
				}
				boolean flag = false;
				if (!colli.isEmpty()) {
					if (entity.onGround /*&& this.onBoxes(entity.boundingBox, colli)*/) {
						//boolean temp = entity.onGround;
						if (m.yCoord < 0) {
							list2.add(entity);
						}
						entity.moveEntity(m.xCoord, 0, m.zCoord);
						entity.onGround = true;
					}
				}

				boolean temp = entity.onGround;
				double testd = entity.posY;
				entity.moveEntity(m.xCoord + dx1, m.yCoord + dy1 + 0.000001, m.zCoord + dz1);
				/*if (m.yCoord + dy1 > entity.posY - testd) {
					MyLogger.info("trace 1 : move " + (m.yCoord + dy1 - entity.posY + testd));
					entity.moveEntity(0, (m.yCoord + dy1 - entity.posY + testd) + 0.01, 0);
				}*/
				entity.onGround = temp;

				for (int n1 = 0; n1 < objs.size(); n1++) {
					objs.get(n1).addCollisionBoxesToList(worldObj, entity.boundingBox.copy().offset(-m.xCoord, -m.yCoord, -m.zCoord), neko, entity);
				}
				if(!neko.isEmpty() && m.yCoord > 0){
					MyLogger.info("a2 trace");
					entity.moveEntity(0,  m.yCoord, 0);
					entity.onGround = temp;
				}

				/*for (int n1 = 0; n1 < objs.size(); n1++) {
					objs.get(n1).addCollisionBoxesToList(worldObj, entity.boundingBox.expand(0.01, 0.01, 0.01), colli, entity);
				}
				if (!colli.isEmpty()) {
					if (entity.onGround && this.onBoxes(entity.boundingBox, colli)) {
						//boolean temp = entity.onGround;
						entity.moveEntity(m.xCoord, m.yCoord, m.zCoord);
						entity.onGround = true;
					}
					//entity.posX += m.xCoord;
					//entity.posY += m.yCoord;
					//entity.posZ += m.xCoord;
				}*/
			}
		}
		
		this.move(m, command++);
		
		i = list2.iterator();
		while (i.hasNext()) {
			Entity entity = (Entity) i.next();
			if (entity instanceof EntityPlayer && (this.worldObj.isRemote)) {
				entity.moveEntity(0, m.yCoord, 0);
				entity.onGround = true;

			}
		}
	}

	private boolean onBoxes(AxisAlignedBB b, List<AxisAlignedBB> colli) {
		AxisAlignedBB aabb = AxisAlignedBB.getBoundingBox(b.minX, b.minY + this.speed + 0.01, b.minZ, b.maxX, b.maxY, b.maxZ);
		return func_a(aabb, colli);
	}

	private boolean func_a(AxisAlignedBB aabb, List<AxisAlignedBB> colli) {
		for (int i = 0; i < colli.size(); i++) {
			if (aabb.intersectsWith(colli.get(i))) {
				return false;
			}
		}
		return true;
	}

	@Override
	public void onUpdate() {
		super.onUpdate();
		if (this.active && this.railBrace != null && this.pos != null) {
			this.isStatic = false;
			this.moveLinearMotor();
		} else {
			this.isStatic = true;
		}
		if (!this.worldObj.isRemote && (this.railBrace == null || this.railBrace.isDead)) {
			this.setDead();
		}

		/*
		)
		
		debug++;
		if (debug == 20) {
			int i = MathHelper.floor_double(this.pos.xCoord / 16.0D);
			int j = MathHelper.floor_double(this.pos.zCoord / 16.0D);
			mitoLogger.info("Im here(ID:" + this.BBID + ")  " + "rail data: " + (this.railBrace != null) );//+ "  " + this.group.list.size());
			debug = 0;
		}*/
	}

	@Override
	public double getYaw() {
		if (this.railBrace != null && this.railBrace.line != null) {
			//this.rotationYaw = this.railBrace.line.getYaw(this.pos);
		}
		return this.rotationYaw;
	}

	@Override
	public double getPitch() {
		if (this.railBrace != null) {
			//this.rotationPitch = this.railBrace.line.getPitch(this.pos);
		}
		return this.rotationPitch;
	}

	@Override
	public int getBrightnessForRender(float partialtick) {
		int i = MathHelper.floor_double(this.pos.xCoord - 0.2);
		int j = MathHelper.floor_double(this.pos.zCoord - 0.2);
		int k = MathHelper.floor_double(this.pos.yCoord - 0.2);

		int i1 = MathHelper.floor_double(this.pos.xCoord + 0.2);
		int j1 = MathHelper.floor_double(this.pos.zCoord + 0.2);
		int k1 = MathHelper.floor_double(this.pos.yCoord + 0.2);

		if (this.worldObj.blockExists(i, 0, j)) {
			return Math.max(this.worldObj.getLightBrightnessForSkyBlocks(i, k, j, 0), this.worldObj.getLightBrightnessForSkyBlocks(i1, k1, j1, 0));
		} else {
			return 0;
		}
	}

	@Override
	public Line interactWithRay(Vec3 set, Vec3 end) {
		if (this.railBrace == null || set == null || end == null) {
			return null;
		}
		Line ret = null;
		for (int n = 0; n < 4; n++) {
			Line line = MitoMath.getLineNearPoint(set, end, this.getJunction(n));
			if (line.getLength() < 0.4) {
				if (ret == null || ret.getLength() > line.getLength()) {
					ret = line;
				}
			}
		}
		return ret;
	}

	public Vec3 getJunction(int n) {
		Vec3 ret = null;
		if (this.railBrace != null) {
			Vec3 v1;
			switch (n) {
			case 0:
				v1 = Vec3.createVectorHelper(0, 0.25, 0);
				break;
			case 1:
				v1 = Vec3.createVectorHelper(0, -0.25, 0);
				break;
			case 2:
				v1 = Vec3.createVectorHelper(0, 0, 0.25);
				break;
			case 3:
				v1 = Vec3.createVectorHelper(0, 0, -0.25);
				break;
			default:
				v1 = Vec3.createVectorHelper(0, 0, 0);
				break;
			}
			Vec3 v2 = MitoMath.rot(v1, this.getRoll(), this.getPitch(), this.getYaw());
			ret = MitoMath.vectorSum(v2, this.pos);
		}
		return ret;
	}

	@Override
	public boolean rightClick(EntityPlayer player, Vec3 pos, ItemStack itemstack) {
		if (itemstack != null && itemstack.getItem() instanceof ItemBar) {
			this.setDead();
			return true;
		} else if (itemstack != null && itemstack.getItem() instanceof ItemBraceBase) {
		} else {
			this.active();
			return true;
		}
		return false;
	}

	private void active() {
		if (this.active) {
			this.stop(command++);
		}
		this.active = this.active ? false : true;

	}

	@Override
	public void readExtraObjectFromNBT(NBTTagCompound nbt) {
		this.direction = nbt.getBoolean("direction");
	}

	@Override
	public void writeExtraObjectToNBT(NBTTagCompound nbt) {
		nbt.setBoolean("direction", this.direction);
	}

	@Override
	public void writeNBTAssociate(NBTTagCompound nbt, Map<ExtraObject, Integer> map) {
		try {
			int[] aint = new int[this.bindBraces.size()];
			for (int n = 0; n < this.bindBraces.size(); n++) {
				aint[n] = map.get(this.bindBraces.get(n));
			}
			nbt.setIntArray("Association", aint);
			nbt.setInteger("rail", map.get(this.railBrace));

		} catch (Throwable throwable) {
			CrashReport crashreport = CrashReport.makeCrashReport(throwable, "Saving fixed object NBT");
			CrashReportCategory crashreportcategory = crashreport.makeCategory("Fixed object being saved");
			//this.addEntityCrashInfo(crashreportcategory);
			throw new ReportedException(crashreport);
		}
	}

	@Override
	public void readNBTAssociate(NBTTagCompound nbt, Map<Integer, ExtraObject> map) {
		int[] aint = nbt.getIntArray("Association");
		for (int n = 0; n < aint.length; n++) {
			this.addBrace(map.get(aint[n]), 0);
		}
		ExtraObject bbb = map.get(nbt.getInteger("rail"));
		if (bbb instanceof Brace) {
			this.addBrace(bbb, 1);
		} else {
			this.addBrace(null, 1);
			MyLogger.info("error");
		}
	}

	@Override
	public void sendConnect() {
		super.sendConnect();
		if (!this.worldObj.isRemote) {
			if (this.railBrace != null)
				PacketHandler.INSTANCE.sendToAll(new BB_PacketProcessor(Mode.BIND, this.BBID, railBrace.BBID, 1));

		}
	}

}
