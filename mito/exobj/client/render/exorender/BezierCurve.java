package com.mito.exobj.client.render.exorender;

import java.util.List;

import com.mito.exobj.client.render.model.ILineBrace;
import com.mito.exobj.utilities.Line;
import com.mito.exobj.utilities.MitoMath;
import com.mito.exobj.utilities.MyUtil;

import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

public class BezierCurve implements ILineBrace {

	public Vec3[] points;

	public BezierCurve(Vec3 p1, Vec3 p2, Vec3 p3, Vec3 p4) {
		this.points = new Vec3[] { p1, p2, p3, p4 };
	}

	public Vec3 getPoint(double t) {
		Vec3 ret = processBezier(points, t);
		return ret;
	}

	public Vec3 getTangent(double t) {
		if (points.length == 3) {
			return MitoMath.normalBezier(points[2], points[1], points[0], t);
		} else if (points.length == 4) {
			return MitoMath.normalBezier(points[3], points[2], points[1], points[0], t);
		} else if (points.length == 2) {
			return MitoMath.sub_vector(points[1], points[0]).normalize();
		}
		return null;
	}

	public Vec3 secondTan(double t) {
		Vec3 ret = MitoMath.vectorSum(MitoMath.vectorMul(points[0], 1 - t), MitoMath.vectorMul(points[1], 3 * t - 2), MitoMath.vectorMul(points[2], 1 - 3 * t), MitoMath.vectorMul(points[3], t));
		return ret.normalize();
	}

	public Vec3 processBezier(Vec3[] points, double t) {
		if (points.length > 1) {
			Vec3[] ps = new Vec3[points.length - 1];
			for (int n = 0; n < points.length - 1; n++) {
				ps[n] = MitoMath.ratio_vector(points[n], points[n + 1], t);
			}
			return processBezier(ps, t);
		} else if (points.length == 1) {
			return points[0];
		}
		return null;
	}

	@Override
	public void move(Vec3 motion, int command) {
		// TODO 自動生成されたメソッド・スタブ

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
	public void readNBT(NBTTagCompound nbt) {
		// TODO 自動生成されたメソッド・スタブ

	}

	@Override
	public void writeNBT(NBTTagCompound nbt) {
		setVec3(nbt, "bezier1", points[0]);
		setVec3(nbt, "bezier2", points[1]);
		setVec3(nbt, "bezier3", points[2]);
		setVec3(nbt, "bezier4", points[3]);
		nbt.setInteger("line", 1);
	}

	@Override
	public Vec3 interactWithLine(Vec3 s, Vec3 e) {
		Line line = MitoMath.getDistanceLine(s, e, this.points[0], this.points[3]);
		return line.end;
	}

	@Override
	public void rotation(Vec3 cent, double yaw) {
		// TODO 自動生成されたメソッド・スタブ

	}

	@Override
	public void resize(Vec3 cent, double i) {
		// TODO 自動生成されたメソッド・スタブ

	}

	@Override
	public AxisAlignedBB getBoundingBox(double size) {
		// TODO 自動生成されたメソッド・スタブ
		return null;
	}

	@Override
	public double getMinY() {
		// TODO 自動生成されたメソッド・スタブ
		return 0;
	}

	@Override
	public double getMaxY() {
		// TODO 自動生成されたメソッド・スタブ
		return 0;
	}

	@Override
	public Vec3 getPos() {
		// TODO 自動生成されたメソッド・スタブ
		return null;
	}

	@Override
	public void addCoordinate(double x, double y, double z) {
		for (int n = 0; n < points.length; n++) {
			points[n].addVector(x, y, z);
		}
	}

	@Override
	public void particle() {
		// TODO 自動生成されたメソッド・スタブ

	}

	@Override
	public boolean interactWithAABB(AxisAlignedBB aabb, double size) {
		boolean ret = false;
		if (aabb.expand(size, size, size).calculateIntercept(points[0], this.points[3]) != null
				|| (aabb.expand(size, size, size).isVecInside(points[0]) && aabb.expand(size, size, size).isVecInside(this.points[3]))) {
			ret = true;
		}
		return ret;
	}

	@Override
	public Line interactWithRay(Vec3 set, Vec3 end, double size) {
		if (this.points[0].distanceTo(this.points[3]) < 0.01) {
			Vec3 ve = MitoMath.getNearPoint(set, end, this.points[0]);
			if (ve.distanceTo(this.points[0]) < size / 1.5) {
				return new Line(ve, this.points[0]);
			}
		}
		Line line = MitoMath.getDistanceLine(set, end, this.points[0], this.points[3]);
		if (line.getAbs() < size / 1.5 && !(MyUtil.isVecEqual(line.end, this.points[0]) || MyUtil.isVecEqual(line.end, this.points[3]))) {
			return line;
		}
		return null;
	}

	@Override
	public void addCollisionBoxesToList(World world, AxisAlignedBB aabb, List collidingBoundingBoxes, Entity entity, double size) {
		// TODO 自動生成されたメソッド・スタブ

	}

	@Override
	public void snap(MovingObjectPosition mop, boolean b) {
		if (points == null || points.length == 0) {
			return;
		}
		Vec3 end = points[points.length - 1];
		if (b) {
			double leng = MitoMath.subAbs(points[0], end);
			if (leng < 1.5) {
				double r = MitoMath.subAbs(points[0], mop.hitVec) / leng;
				//absは絶対値なので厳密ではない
				if (r < 0.3333) {
					mop.hitVec = points[0];
				} else if (r > 0.6666) {
					mop.hitVec = end;
				} else {
					mop.hitVec = MitoMath.ratio_vector(points[0], end, 0.5);
				}
			} else {
				if (MitoMath.subAbs(points[0], mop.hitVec) < 0.5) {
					mop.hitVec = points[0];
				} else if (MitoMath.subAbs(end, mop.hitVec) < 0.5) {
					mop.hitVec = end;
				} else if (MitoMath.subAbs(MitoMath.ratio_vector(points[0], end, 0.5), mop.hitVec) < 0.25) {
					mop.hitVec = MitoMath.ratio_vector(points[0], end, 0.5);
				}
			}
		} else {
			double r = MitoMath.subAbs(points[0], mop.hitVec) / MitoMath.subAbs(points[0], end);
			if (r < 0.5) {
				mop.hitVec = points[0];
			} else if (r > 0.5) {
				mop.hitVec = end;
			}
		}
	}

	@Override
	public double getYaw(Vec3 pos) {
		return 0;
	}

	@Override
	public double getPitch(Vec3 pos) {
		return 0;
	}

	@Override
	public Vec3 getMotion(Vec3 pos, double speed, boolean dir) {
		// TODO 自動生成されたメソッド・スタブ
		return Vec3.createVectorHelper(0, 0, 0);
	}

}
