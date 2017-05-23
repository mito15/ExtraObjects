package com.mito.exobj.utilities;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import com.mito.exobj.client.render.model.ILineBrace;
import com.mito.exobj.client.render.model.LineWithDirection;

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

	public double getLength() {
		return MitoMath.subAbs(this.start, this.end);
	}

	@Override
	public void move(Vec3 motion, int command) {
		this.start = MitoMath.vectorSum(this.start, motion);
		this.end = MitoMath.vectorSum(this.end, motion);
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
		nbt.setInteger("line", 0);
	}

	public boolean interactWithAABB(AxisAlignedBB aabb, double size) {
		boolean ret = false;
		if (aabb.expand(size, size, size).calculateIntercept(start, this.end) != null
				|| (aabb.expand(size, size, size).isVecInside(start) && aabb.expand(size, size, size).isVecInside(this.end))) {
			ret = true;
		}
		return ret;
	}

	@Override
	public void rotation(Vec3 cent, double yaw) {
		start = MitoMath.vectorSum(MitoMath.rotY(MitoMath.sub_vector(start, cent), yaw), cent);
		end = MitoMath.vectorSum(MitoMath.rotY(MitoMath.sub_vector(end, cent), yaw), cent);

	}

	@Override
	public void resize(Vec3 cent, double i) {
		start = MitoMath.vectorSum(MitoMath.vectorMul(MitoMath.sub_vector(start, cent), i), cent);
		end = MitoMath.vectorSum(MitoMath.vectorMul(MitoMath.sub_vector(end, cent), i), cent);

	}

	/*@Override
	public Line interactWithRay(Vec3 set, Vec3 end, double size) {
		if (this.start.distanceTo(this.end) < 0.01) {
			Vec3 ve = MitoMath.getNearPoint(set, end, this.start);
			if (ve.distanceTo(this.start) < size / 1.5) {
				return new Line(ve, this.start);
			}
		}
		Line line = MitoMath.getDistanceLine(set, end, this.start, this.end);
		if (line.getLength() < size / 1.5 && !(MyUtil.isVecEqual(line.end, this.start) || MyUtil.isVecEqual(line.end, this.end))) {
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
	}*/

	/*@Override
	public double getMinY() {
		return Math.min(start.yCoord, end.yCoord);
	}
	
	@Override
	public double getMaxY() {
		return Math.max(start.yCoord, end.yCoord);
	}
	
	@Override
	public Vec3 getPos() {
		return MitoMath.ratio_vector(start, end, 0.5);
	}*/

	@Override
	public void addCoordinate(double x, double y, double z) {
		this.start = this.start.addVector(x, y, z);
		this.end = this.end.addVector(x, y, z);

	}

	public void addCollisionBoxesToList(World world, AxisAlignedBB aabb, List collidingBoundingBoxes, Entity entity, double size) {
		Vec3 v3 = MitoMath.sub_vector(this.end, this.start);
		int div = size > 0 ? (int) Math.floor(MitoMath.abs(v3) / size) + 1 : 1;
		Vec3 part = MitoMath.vectorDiv(MitoMath.sub_vector(v3, MitoMath.vectorMul(v3.normalize(), size)), div);
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
					mop.hitVec = MitoMath.ratio_vector(start, end, 0.5);
				}
			} else {
				if (MitoMath.subAbs(start, mop.hitVec) < 0.5) {
					mop.hitVec = start;
				} else if (MitoMath.subAbs(end, mop.hitVec) < 0.5) {
					mop.hitVec = end;
				} else if (MitoMath.subAbs(MitoMath.ratio_vector(start, end, 0.5), mop.hitVec) < 0.25) {
					mop.hitVec = MitoMath.ratio_vector(start, end, 0.5);
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

	/*@Override
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
			if (pos.distanceTo(this.end) < 0.0001) {
				return null;
			}
		} else {
			v2 = start;
			v3 = end;
			if (pos.distanceTo(this.start) < 0.0001) {
				return null;
			}
		}
		v1 = MitoMath.getNearPoint(start, end, pos);
		Vec3 unit = MitoMath.sub_vector(v2, v3).normalize();
		Vec3 moved = MitoMath.vectorSum(v1, MitoMath.vectorMul(unit, speed));
		if (MitoMath.subAbs2(v1, moved) > MitoMath.subAbs2(v1, v2)) {
			moved = v2;
		}
		return MitoMath.sub_vector(moved, pos);
	}*/

	/*@Override
	public Vec3 getPoint(double d) {
		Vec3 ret = MitoMath.ratio_vector(start, end, d);
		return ret;
	}
	
	@Override
	public Vec3 getTangent(double t) {
		return start.subtract(end).normalize();
	}*/

	@Override
	public Vec3 getStart() {
		return MitoMath.copyVec3(this.start);
	}

	@Override
	public Vec3 getEnd() {
		return MitoMath.copyVec3(this.end);
	}

	/*@Override
	public Vec3 secondTan(double d) {
		return Vec3.createVectorHelper(0, -1, 0);
	}*/

	public LineWithDirection getODrawLine() {
		Vec3 s = this.start;
		Vec3 e = this.end;
		Vec3 sn = start.subtract(end).normalize();
		Vec3 ms = Vec3.createVectorHelper(0, -1, 0);
		return new LineWithDirection(s, e, sn, sn, ms, ms);
	}

	@Override
	public LineWithDirection[] getDrawLine() {
		return new LineWithDirection[] { getODrawLine() };
	}

	@Override
	public List<Line> getSegments() {
		List<Line> ret = new ArrayList<Line>();
		ret.add(this);
		return ret;
	}

	@Override
	public List<Vec3> getLine() {
		List<Vec3> ret = new LinkedList<Vec3>();
		ret.add(start);
		ret.add(end);
		return ret;
	}

}