package com.mito.exobj.client.render.model;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import com.mito.exobj.utilities.Line;
import com.mito.exobj.utilities.MitoMath;

import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

public class LineLoop implements ILineBrace {

	public List<Vec3> line = new ArrayList<Vec3>();
	public boolean isLoop = false;

	public LineLoop(Vec3... list) {
		for (Vec3 v : list) {
			line.add(v);
		}
	}

	public Vec3 getPoint(double t) {
		double len = this.getLength();
		double sep = t * len;
		double cl = 0;
		List<Line> lines = this.getSegments();
		for (Line l : lines) {
			cl = +l.getLength();
			if (cl > sep) {
				double l1 = l.getLength();
				double l2 = cl - sep;
				double d = (l1 - l2) / l1;
				return l.getPoint(d);
			}
		}
		return null;
	}

	public int getSegNum(double t) {
		double len = this.getLength();
		double sep = t * len;
		double cl = 0;
		List<Line> lines = this.getSegments();
		for (Line l : lines) {
			cl = +l.getLength();
			if (cl > sep) {
				return lines.indexOf(l);
			}
		}
		return -1;
	}

	public Vec3 getTangent(double t) {
		int i = getSegNum(t);
		Line line = this.getSegments().get(i);
		return line.getTangent(0.5);
	}

	@Override
	public void move(Vec3 motion, int command) {
		for (Vec3 v : line) {
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
		NBTTagList taglistGroups = new NBTTagList();
		for (Vec3 v : line) {
			NBTTagCompound nbt1 = new NBTTagCompound();
			setVec3(nbt1, "vec", v);
			taglistGroups.appendTag(nbt1);
		}
		nbt.setTag("line_ list", taglistGroups);
		nbt.setInteger("line", 2);
	}

	@Override
	public Vec3 interactWithLine(Vec3 s, Vec3 e) {
		List<Line> list = this.getSegments();
		Line l1 = list.get(0);
		for (Line l : list) {
			if (l1.getLength() > l.getLength()) {
				l1 = l;
			}
		}
		return l1.end;
	}

	@Override
	public void rotation(Vec3 cent, double yaw) {
		for (Vec3 v : line) {
			v = MitoMath.vectorSum(MitoMath.rotY(MitoMath.sub_vector(v, cent), yaw), cent);
		}
	}

	@Override
	public void resize(Vec3 cent, double i) {
		for (Vec3 v : line) {
			v = MitoMath.vectorSum(MitoMath.vectorMul(MitoMath.sub_vector(v, cent), i), cent);
		}
	}

	@Override
	public AxisAlignedBB getBoundingBox(double size) {
		double maxX = Double.MIN_VALUE;
		double maxY = Double.MIN_VALUE;
		double maxZ = Double.MIN_VALUE;
		double minX = Double.MAX_VALUE;
		double minY = Double.MAX_VALUE;
		double minZ = Double.MAX_VALUE;
		for (Vec3 v : line) {
			maxX = maxX > v.xCoord ? maxX : v.xCoord;
			maxY = maxY > v.yCoord ? maxY : v.yCoord;
			maxZ = maxZ > v.zCoord ? maxZ : v.zCoord;
			minX = minX < v.xCoord ? minX : v.xCoord;
			minY = minY < v.yCoord ? minY : v.yCoord;
			minZ = minZ < v.zCoord ? minZ : v.zCoord;
		}
		return AxisAlignedBB.getBoundingBox(minX, minY, minZ, maxX, maxY, maxZ).expand(size, size, size);
	}

	public List<Line> getSegments() {
		List<Line> ret = new LinkedList<Line>();
		for (int n = 0; n < line.size() - 1; n++) {
			Vec3 v = MitoMath.copyVec3(line.get(n));
			Vec3 v1 = MitoMath.copyVec3(line.get(n + 1));
			ret.add(new Line(v, v1));
		}
		return ret;
	}

	@Override
	public double getMinY() {
		double minY = Double.MAX_VALUE;
		for (Vec3 v : line) {
			minY = minY < v.yCoord ? minY : v.yCoord;
		}
		return minY;
	}

	@Override
	public double getMaxY() {
		double maxY = Double.MIN_VALUE;
		for (Vec3 v : line) {
			maxY = maxY > v.yCoord ? maxY : v.yCoord;
		}
		return maxY;
	}

	@Override
	public Vec3 getPos() {
		return this.getPoint(0.5);
	}

	@Override
	public void addCoordinate(double x, double y, double z) {
		for (Vec3 v : line) {
			v.addVector(x, y, z);
		}
	}

	@Override
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
		Line line = null;
		List<Line> list = this.getSegments();
		for (Line l : list) {
			Line line2 = l.interactWithRay(set, end, size);
			if (line == null || line2.end.distanceTo(set) < line.end.distanceTo(set)) {
				line = line2;
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
	}

	@Override
	public void snap(MovingObjectPosition mop, boolean b) {
		List<Line> list = this.getSegments();
		for (Line l : list) {
			if (MitoMath.getLineNearPoint(l.start, l.end, mop.hitVec).getLength() < 0.01) {
				l.snap(mop, b);
				return;
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
		return this.line.get(0);
	}

	@Override
	public Vec3 getEnd() {
		return this.line.get(line.size() - 1);
	}

}
