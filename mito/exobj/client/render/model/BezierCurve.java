package com.mito.exobj.client.render.model;

import java.util.LinkedList;
import java.util.List;

import com.mito.exobj.utilities.Line;
import com.mito.exobj.utilities.MitoMath;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;

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
		for (Vec3 v : points) {
			v = MitoMath.vectorSum(v, motion);
		}
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
		setVec3(nbt, "bezier1", points[0]);
		setVec3(nbt, "bezier2", points[1]);
		setVec3(nbt, "bezier3", points[2]);
		setVec3(nbt, "bezier4", points[3]);
		nbt.setInteger("line", 1);
	}

	@Override
	public void rotation(Vec3 cent, double yaw) {
		for (Vec3 v : points) {
			v = MitoMath.vectorSum(MitoMath.rotY(MitoMath.sub_vector(v, cent), yaw), cent);
		}
	}

	@Override
	public void resize(Vec3 cent, double i) {
		for (Vec3 v : points) {
			v = MitoMath.vectorSum(MitoMath.vectorMul(MitoMath.sub_vector(v, cent), i), cent);
		}
	}

	/*@Override
	public AxisAlignedBB getBoundingBox(double size) {
		double maxX = Double.MIN_VALUE;
		double maxY = Double.MIN_VALUE;
		double maxZ = Double.MIN_VALUE;
		double minX = Double.MAX_VALUE;
		double minY = Double.MAX_VALUE;
		double minZ = Double.MAX_VALUE;
		List<Vec3> list = this.getLine();
		for (Vec3 v : list) {
			maxX = maxX > v.xCoord ? maxX : v.xCoord;
			maxY = maxY > v.yCoord ? maxY : v.yCoord;
			maxZ = maxZ > v.zCoord ? maxZ : v.zCoord;
			minX = minX < v.xCoord ? minX : v.xCoord;
			minY = minY < v.yCoord ? minY : v.yCoord;
			minZ = minZ < v.zCoord ? minZ : v.zCoord;
		}
		return AxisAlignedBB.getBoundingBox(minX, minY, minZ, maxX, maxY, maxZ).expand(size, size, size);
	}*/

	public List<Vec3> getLine() {
		List<Vec3> ret = new LinkedList<Vec3>();
		int nm = this.getAccuracy();
		for (int n = 0; n < nm; n++) {
			Vec3 v = this.getPoint((double) n / (double) nm);
			ret.add(v);
		}
		return ret;
	}

	public List<Line> getSegments() {
		List<Line> ret = new LinkedList<Line>();
		int nm = this.getAccuracy();
		for (int n = 0; n < nm - 1; n++) {
			Vec3 v = this.getPoint((double) n / (double) nm);
			Vec3 v1 = this.getPoint((double) (n + 1) / (double) nm);
			ret.add(new Line(v, v1));
		}
		return ret;
	}

	/*@Override
	public double getMinY() {
		double minY = Double.MAX_VALUE;
		List<Vec3> list = this.getLine();
		for (Vec3 v : list) {
			minY = minY < v.yCoord ? minY : v.yCoord;
		}
		return minY;
	}

	@Override
	public double getMaxY() {
		double maxY = Double.MIN_VALUE;
		List<Vec3> list = this.getLine();
		for (Vec3 v : list) {
			maxY = maxY > v.yCoord ? maxY : v.yCoord;
		}
		return maxY;
	}

	@Override
	public Vec3 getPos() {
		return this.getPoint(0.5);
	}*/

	@Override
	public void addCoordinate(double x, double y, double z) {
		for (int n = 0; n < points.length; n++) {
			points[n].addVector(x, y, z);
		}
	}

	/*@Override
	public boolean interactWithAABB(AxisAlignedBB aabb, double size) {
		boolean ret = false;
		List<Line> list = this.getSegments();
		for (Line line : list) {
			if (line.interactWithAABB(aabb, size)) {
				ret = true;
			}
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
		Line line = null;
		List<Line> list = this.getSegments();
		for (Line l : list) {
			Line line2 = MitoMath.getDistanceLine(set, end, l.start, l.end);
			if (line2.getLength() < size / 1.5 && !(MyUtil.isVecEqual(line2.end, this.points[0]) || MyUtil.isVecEqual(line2.end, this.points[3]))) {
				if (line == null || line2.end.distanceTo(set) < line.end.distanceTo(set)) {
					line = line2;
				}
			}
		}
		return line;
	}
	@Override
	public void addCollisionBoxesToList(World world, AxisAlignedBB aabb, List collidingBoundingBoxes, Entity entity, double size) {
		List<Line> list = this.getSegments();
		for (Line line : list) {
			line.addCollisionBoxesToList(world, aabb, collidingBoundingBoxes, entity, size);
		}
		/*Vec3 v3 = MitoMath.sub_vector(this.end, this.start);
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
	}*/


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

	/*@Override
	public double getYaw(Vec3 pos) {
		return 0;
	}

	@Override
	public double getPitch(Vec3 pos) {
		return 0;
	}

	@Override
	public Vec3 getMotion(Vec3 pos, double speed, boolean dir) {
		return Vec3.createVectorHelper(0, 0, 0);
	}*/

	public int getAccuracy() {
		int i = (int) (points[0].distanceTo(points[1]) + points[1].distanceTo(points[2]) + points[2].distanceTo(points[3]) * 5.0);
		return Math.max(i, 20);
	}

	@Override
	public double getLength() {
		double ret = 0;
		List<Line> list = this.getSegments();
		for (Line line : list) {
			ret += line.getLength();
		}
		return ret;
	}

	@Override
	public Vec3 getStart() {
		return this.points[0];
	}

	@Override
	public Vec3 getEnd() {
		return this.points[3];
	}

	@Override
	public LineWithDirection[] getDrawLine() {
		int nmax = this.getAccuracy();
		LineWithDirection[] ret = new LineWithDirection[nmax];
		Vec3 s = this.getPoint(0);
		Vec3 sn = this.getTangent(0);
		Vec3 ms = this.secondTan(0);
		for (int n = 0; n < nmax; n++) {
			double t1 = (double) (n + 1) / (double) nmax;
			Vec3 e = this.getPoint(t1);
			Vec3 en = this.getTangent(t1);
			Vec3 me = this.secondTan(t1);
			ret[n] = new LineWithDirection(s, e, sn, en, ms, me);
			s = MitoMath.copyVec3(e);
			sn = MitoMath.copyVec3(en);
			ms = MitoMath.copyVec3(me);
		}
		return ret;
	}

}
