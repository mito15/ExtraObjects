package com.mito.exobj.utilities;

import java.util.ArrayList;
import java.util.List;

import com.mito.exobj.client.render.model.ILineBrace;

import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

public class Line implements ILineBrace {

	public Vec3 start;
	public Vec3 end;

	public Line(Vec3 s, Vec3 e) {
		this.start = s;
		this.end = e;
	}

	public double getAbs() {
		return MitoMath.subAbs(this.start, this.end);
	}

	@Override
	public void move(Vec3 motion, int command) {
		this.start = MitoMath.vectorSum(this.start, motion);
		this.end = MitoMath.vectorSum(this.end, motion);
	}

	@Override
	public void readNBT(NBTTagCompound nbt) {
		this.start = getVec3(nbt, "start");
		this.end = getVec3(nbt, "end");
	}

	private void setVec3(NBTTagCompound nbt, String name, Vec3 vec) {
		nbt.setDouble(name + "X", vec.xCoord);
		nbt.setDouble(name + "Y", vec.yCoord);
		nbt.setDouble(name + "Z", vec.zCoord);
	}

	private Vec3 getVec3(NBTTagCompound nbt, String name) {
		return Vec3.createVectorHelper(nbt.getDouble(name + "X"), nbt.getDouble(name + "Y"), nbt.getDouble(name + "Z"));
	}

	@Override
	public void writeNBT(NBTTagCompound nbt) {
		setVec3(nbt, "start", start);
		setVec3(nbt, "end", end);
	}

	@Override
	public boolean interactWithAABB(AxisAlignedBB aabb, double size) {
		boolean ret = false;
		if (aabb.expand(size, size, size).calculateIntercept(start, this.end) != null
				|| (aabb.expand(size, size, size).isVecInside(start) && aabb.expand(size, size, size).isVecInside(this.end))) {
			ret = true;
		}
		return ret;
		/*Vec3 v1 = start.subtract(end);
		Vec3 v2 = MitoMath.vectorMul(v1.crossProduct(Vec3.createVectorHelper(0, 1, 0)).normalize(), size);
		Vec3 v3 = MitoMath.vectorMul(v1.crossProduct(v2).normalize(), size);
		AxisAlignedBB aabb1 = OrientedBoundingBox.getBoundingBox(MitoMath.vectorMul(MitoMath.vectorSum(end, start), 0.5), v1, v2, v3);
		if (aabb1 != null && aabb1.intersectsWith(aabb)) {
			return true;
		}
		return false;*/
	}

	@Override
	public Vec3 interactWithLine(Vec3 s, Vec3 e) {
		Line line = MitoMath.getDistanceLine(s, e, this.start, this.end);
		return line.end;
	}

	@Override
	public void rotation(Vec3 cent, double yaw) {
		start = MitoMath.vectorSum(MitoMath.rotY(MitoMath.vectorSub(start, cent), yaw), cent);
		end = MitoMath.vectorSum(MitoMath.rotY(MitoMath.vectorSub(end, cent), yaw), cent);

	}

	@Override
	public void resize(Vec3 cent, double i) {
		start = MitoMath.vectorSum(MitoMath.vectorMul(MitoMath.vectorSub(start, cent), i), cent);
		end = MitoMath.vectorSum(MitoMath.vectorMul(MitoMath.vectorSub(end, cent), i), cent);

	}

	@Override
	public Line interactWithRay(Vec3 set, Vec3 end, double size) {
		if (this.start.distanceTo(this.end) < 0.01) {
			Vec3 ve = MitoMath.getNearPoint(set, end, this.start);
			if (ve.distanceTo(this.start) < size / 1.5) {
				return new Line(ve, this.start);
			}
		}
		Line line = MitoMath.getDistanceLine(set, end, this.start, this.end);
		if (line.getAbs() < size / 1.5 && !(MyUtil.isVecEqual(line.end, this.start) || MyUtil.isVecEqual(line.end, this.end))) {
			return line;
		}
		return null;
	}

	@Override
	public AxisAlignedBB getBoundingBox(double size) {
		double maxX = Math.max(this.start.xCoord, this.end.xCoord);
		double maxY = Math.max(this.start.yCoord, this.end.yCoord);
		double maxZ = Math.max(this.start.zCoord, this.end.zCoord);
		double minX = Math.min(this.start.xCoord, this.end.xCoord);
		double minY = Math.min(this.start.yCoord, this.end.yCoord);
		double minZ = Math.min(this.start.zCoord, this.end.zCoord);
		return AxisAlignedBB.getBoundingBox(minX, minY, minZ, maxX, maxY, maxZ).expand(size, size, size);
	}

	@Override
	public double getMinY() {
		return Math.min(start.yCoord, end.yCoord);
	}

	@Override
	public double getMaxY() {
		return Math.max(start.yCoord, end.yCoord);
	}

	@Override
	public Vec3 getPos() {
		return MitoMath.vectorRatio(start, end, 0.5);
	}

	@Override
	public void addCoordinate(double x, double y, double z) {
		this.start = this.start.addVector(x, y, z);
		this.end = this.end.addVector(x, y, z);

	}

	@Override
	public void particle() {
		// TODO 自動生成されたメソッド・スタブ

	}

	@Override
	public void addCollisionBoxesToList(World world, AxisAlignedBB aabb, List collidingBoundingBoxes, Entity entity, double size) {
		Vec3 v3 = MitoMath.vectorSub(this.end, this.start);
		int div = size > 0 ? (int) Math.floor(MitoMath.abs(v3) / size) + 1 : 1;
		Vec3 part = MitoMath.vectorDiv(MitoMath.vectorSub(v3, MitoMath.vectorMul(v3.normalize(), size)), div);
		Vec3 offset = MitoMath.vectorMul(v3.normalize(), size / 2);
		List<AxisAlignedBB> list = new ArrayList<AxisAlignedBB>();
		for (int n = 0; n <= div; n++) {
			Vec3 v = MitoMath.vectorSum(this.start, offset, MitoMath.vectorMul(part, (double) n));
			AxisAlignedBB aabb1 = MyUtil.createAabbBySize(v, size);
			if (aabb1 != null && aabb1.intersectsWith(aabb)) {
				//list.add(aabb1);
				collidingBoundingBoxes.add(aabb1);
			}
		}
		/*Vec3 v1 = start.subtract(end);
		Vec3 v2 = MitoMath.vectorMul(v1.crossProduct(Vec3.createVectorHelper(0, 1, 0)).normalize(), size);
		Vec3 v3 = MitoMath.vectorMul(v1.crossProduct(v2).normalize(), size);
		AxisAlignedBB aabb1 = OrientedBoundingBox.getBoundingBox(MitoMath.vectorMul(MitoMath.vectorSum(end, start), 0.5), v1, v2, v3);
		if (aabb1 != null && aabb1.intersectsWith(aabb)) {
			collidingBoundingBoxes.add(aabb1);
		}*/
	}

	@Override
	public void snap(MovingObjectPosition mop, boolean b) {
		if (b) {
			double leng = MitoMath.subAbs(start, end);
			if (leng < 1.5) {
				double r = MitoMath.subAbs(start, mop.hitVec) / leng;
				//absは絶対値なので厳密ではない
				if (r < 0.3333) {
					mop.hitVec = start;
				} else if (r > 0.6666) {
					mop.hitVec = end;
				} else {
					mop.hitVec = MitoMath.vectorRatio(start, end, 0.5);
				}
			} else {
				if (MitoMath.subAbs(start, mop.hitVec) < 0.5) {
					mop.hitVec = start;
				} else if (MitoMath.subAbs(end, mop.hitVec) < 0.5) {
					mop.hitVec = end;
				} else if (MitoMath.subAbs(MitoMath.vectorRatio(start, end, 0.5), mop.hitVec) < 0.25) {
					mop.hitVec = MitoMath.vectorRatio(start, end, 0.5);
				}
			}
		} else {
			double r = MitoMath.subAbs(start, mop.hitVec) / MitoMath.subAbs(start, end);
			if (r < 0.5) {
				mop.hitVec = start;
			} else if (r > 0.5) {
				mop.hitVec = end;
			}
		}
	}

	@Override
	public double getYaw(Vec3 pos) {
		return MitoMath.getYaw(this.start, this.end);
	}

	@Override
	public double getPitch(Vec3 pos) {
		return MitoMath.getPitch(this.start, this.end);
	}

	@Override
	public Vec3 getMotion(Vec3 pos, double speed, boolean dir) {
		Vec3 v1, v2, v3;
		if (dir) {
			v2 = end;
			v3 = start;
			if(pos.distanceTo(this.end) < 0.0001){
				return null;
			}
		} else {
			v2 = start;
			v3 = end;
			if(pos.distanceTo(this.start) < 0.0001){
				return null;
			}
		}
		v1 = MitoMath.getNearPoint(start, end, pos);
		Vec3 unit = MitoMath.vectorSub(v2, v3).normalize();
		Vec3 moved = MitoMath.vectorSum(v1, MitoMath.vectorMul(unit, speed));
		if(MitoMath.subAbs2(v1, moved) > MitoMath.subAbs2(v1, v2)){
			moved = v2;
		}
		return MitoMath.vectorSub(moved, pos);
	}

	@Override
	public Vec3 getPoint(double d) {
		Vec3 ret = MitoMath.vectorRatio(start, end, d);
		return ret;
	}

	@Override
	public Vec3 getTangent(double t) {
		return start.subtract(end).normalize();
	}

}
