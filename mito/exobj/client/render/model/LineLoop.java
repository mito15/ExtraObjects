package com.mito.exobj.client.render.model;

import java.util.ArrayList;
import java.util.List;

import com.mito.exobj.utilities.Line;
import com.mito.exobj.utilities.MitoMath;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;

public class LineLoop implements ILineBrace {

	public List<Vec3> line = new ArrayList<Vec3>();
	public boolean isLoop = false;

	public LineLoop(Vec3... list) {
		for (Vec3 v : list) {
			line.add(v);
		}
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
		//MyLogger.info("brace read line loop " + line.size());
		NBTTagList taglistGroups = new NBTTagList();
		for (Vec3 v : line) {
			NBTTagCompound nbt1 = new NBTTagCompound();
			setVec3(nbt1, "vec", v);
			taglistGroups.appendTag(nbt1);
		}
		nbt.setTag("line_list", taglistGroups);
		nbt.setInteger("line", 2);
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
	public List<Line> getSegments() {
		List<Line> ret = new ArrayList<Line>();
		for (int n = 0; n < line.size() - 1; n++) {
			Vec3 v = MitoMath.copyVec3(line.get(n));
			Vec3 v1 = MitoMath.copyVec3(line.get(n + 1));
			ret.add(new Line(v, v1));
		}
		return ret;
	}

	@Override
	public void addCoordinate(double x, double y, double z) {
		for (Vec3 v : line) {
			v.addVector(x, y, z);
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
		// TODO 自動生成されたメソッド・スタブ
		return Vec3.createVectorHelper(0, 0, 0);
	}*/

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
		/*if (this.line.isEmpty()) {
			return Vec3.createVectorHelper(0, 0, 0);
		}*/
		return this.line.get(0);
	}

	@Override
	public Vec3 getEnd() {
		/*if (this.line.isEmpty()) {
			return Vec3.createVectorHelper(0, 0, 0);
		}*/
		return this.line.get(line.size() - 1);
	}

	@Override
	public LineWithDirection[] getDrawLine() {
		int acc = this.getAccuracy();
		List<Line> list = this.getSegments();
		int num = acc * (list.size() - 1) + list.size();
		LineWithDirection[] ret = new LineWithDirection[num];
		int num1 = 0;
		//MyLogger.info("line loop a " + num);
		for (Line l : list) {
			//MyLogger.info("line loop " + num1);
			ret[num1] = l.getODrawLine();
			//Vec3 a = Vec3.createVectorHelper(0, 0, 0);
			//ret[num1] = new LineWithDirection(a, a, a, a, a, a);
			num1 = num1 + acc + 1;
		}
		Vec3 ms = Vec3.createVectorHelper(0, -1, 0);
		for (int n = 0; n < list.size() - 1; n++) {
			Line l1 = list.get(n);
			Line l2 = list.get(n + 1);
			Vec3 s = l1.getEnd();
			Vec3 sn = l1.start.subtract(l1.end).normalize();
			Vec3 en = l2.start.subtract(l2.end).normalize();
			for (int n1 = 0; n1 < acc; n1++) {
				double t = (double) n / (double) acc;
				double t1 = (double) (n + 1) / (double) acc;
				/*Vec3 sn1 = MitoMath.ratio_vector(sn, en, t).normalize();
				Vec3 sn2 = MitoMath.ratio_vector(sn, en, t1).normalize();*/
				//MyLogger.info("line loop " + (n * 6 + n1 + 1));
				ret[n * (acc + 1) + n1 + 1] = new LineWithDirection(s, s, sn, en, ms, ms);
			}
		}
		return ret;
	}

	public int getAccuracy() {
		return 1;
	}

	@Override
	public List<Vec3> getLine() {
		return line;
	}

}
