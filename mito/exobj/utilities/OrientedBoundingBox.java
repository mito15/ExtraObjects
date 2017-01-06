package com.mito.exobj.utilities;

import com.mito.exobj.common.MyLogger;

import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.Vec3;

public class OrientedBoundingBox extends AxisAlignedBB {

	double yaw;

	Vec3 center, side1, side2, side3;

	public OrientedBoundingBox(double minX, double minY, double minZ, double maxX, double maxY, double maxZ) {
		super(minX, minY, minZ, maxX, maxY, maxZ);
		this.center = Vec3.createVectorHelper((minX + maxX) / 2, (minY + maxY) / 2, (minZ + maxZ) / 2);
		this.side1 = Vec3.createVectorHelper(maxX - minX, 0, 0);
		this.side2 = Vec3.createVectorHelper(0, maxY - minY, 0);
		this.side3 = Vec3.createVectorHelper(0, 0, maxZ - minZ);
	}

	public OrientedBoundingBox(Vec3 vert, Vec3 side1, Vec3 side2, Vec3 side3) {
		super(0, 0, 0, 0, 0, 0);

		double x1 = vert.xCoord;
		double x2 = vert.xCoord;
		double y1 = vert.yCoord;
		double y2 = vert.yCoord;
		double z1 = vert.zCoord;
		double z2 = vert.zCoord;
		double x3, y3, z3;
		double[] a1 = { -0.5, 0.5, -0.5, -0.5, 0.5, -0.5, 0.5, 0.5 };
		double[] a2 = { -0.5, -0.5, 0.5, -0.5, 0.5, 0.5, -0.5, 0.5 };
		double[] a3 = { -0.5, -0.5, -0.5, 0.5, -0.5, 0.5, 0.5, 0.5 };
		for (int n = 1; n < 8; n++) {
			x3 = vert.xCoord + a1[n] * side1.xCoord + a2[n] * side2.xCoord + a3[n] * side3.xCoord;
			y3 = vert.yCoord + a1[n] * side1.yCoord + a2[n] * side2.yCoord + a3[n] * side3.yCoord;
			z3 = vert.zCoord + a1[n] * side1.zCoord + a2[n] * side2.zCoord + a3[n] * side3.zCoord;
			if (x1 > vert.xCoord) {
				x1 = x3;
			}
			if (y1 > vert.yCoord) {
				y1 = y3;
			}
			if (z1 > vert.zCoord) {
				z1 = z3;
			}
			if (x2 < vert.xCoord) {
				x1 = x3;
			}
			if (y2 < vert.yCoord) {
				y1 = y3;
			}
			if (z2 < vert.zCoord) {
				z1 = z3;
			}
		}

		this.center = vert;
		this.side1 = side1;
		this.side2 = side2;
		this.side3 = side3;
		this.minX = x1;
		this.minY = y1;
		this.minZ = z1;
		this.maxX = x2;
		this.maxY = y2;
		this.maxZ = z2;

	}

	public Vec3[] getSeparatingAxis(AxisAlignedBB aabb) {
		Vec3[] a1 = getParallelAxis(this);
		Vec3[] a2 = getParallelAxis(aabb);
		return new Vec3[] { a1[0], a1[1], a1[2], a2[0], a2[1], a2[2],
				a1[0].crossProduct(a2[0]), a1[0].crossProduct(a2[1]), a1[0].crossProduct(a2[2]), a1[1].crossProduct(a2[0]), a1[1].crossProduct(a2[1]), a1[1].crossProduct(a2[2]), a1[2].crossProduct(a2[0]), a1[2].crossProduct(a2[1]),
				a1[2].crossProduct(a2[2]) };
	}

	public Vec3[] getParallelAxis(AxisAlignedBB aabb) {
		if (aabb instanceof OrientedBoundingBox) {
			OrientedBoundingBox obb = (OrientedBoundingBox) aabb;
			return new Vec3[] { obb.side1.normalize(), obb.side2.normalize(), obb.side3.normalize() };
		} else {
			return new Vec3[] { Vec3.createVectorHelper(1, 0, 0), Vec3.createVectorHelper(0, 1, 0), Vec3.createVectorHelper(0, 0, 1) };
		}
	}

	// return length on sep axis [min, max]
	public double[] getLengthOnAxis(Vec3 v, AxisAlignedBB aabb) {
		Vec3 vert0;
		int i = 0;
		double dotm = v.dotProduct(this.getVertex(0, aabb));
		for (int n = 1; n < 8; n++) {
			vert0 = this.getVertex(n, aabb);
			double dot = v.dotProduct(vert0);
			if (dot < dotm) {
				dotm = dot;
				i = n;
			}
		}
		double[] ret = { dotm, v.dotProduct(this.getVertex(this.getReverse(i), aabb)) };

		return ret;
	}

	public Vec3 getVertex(int n, AxisAlignedBB aabb) {
		if (aabb instanceof OrientedBoundingBox) {
			OrientedBoundingBox obb = (OrientedBoundingBox) aabb;
			double[] a1 = { -0.5, 0.5, -0.5, -0.5, 0.5, -0.5, 0.5, 0.5 };
			double[] a2 = { -0.5, -0.5, 0.5, -0.5, 0.5, 0.5, -0.5, 0.5 };
			double[] a3 = { -0.5, -0.5, -0.5, 0.5, -0.5, 0.5, 0.5, 0.5 };
			double x = obb.center.xCoord + a1[n] * obb.side1.xCoord + a2[n] * obb.side2.xCoord + a3[n] * obb.side3.xCoord;
			double y = obb.center.yCoord + a1[n] * obb.side1.yCoord + a2[n] * obb.side2.yCoord + a3[n] * obb.side3.yCoord;
			double z = obb.center.zCoord + a1[n] * obb.side1.zCoord + a2[n] * obb.side2.zCoord + a3[n] * obb.side3.zCoord;
			return Vec3.createVectorHelper(x, y, z);
		} else {
			boolean[] a1 = { false, true, false, false, true, false, true, true };
			boolean[] a2 = { false, false, true, false, true, true, false, true };
			boolean[] a3 = { false, false, false, true, false, true, true, true };
			double x = a1[n] ? aabb.maxX : aabb.minX;
			double y = a2[n] ? aabb.maxY : aabb.minY;
			double z = a3[n] ? aabb.maxZ : aabb.minZ;
			return Vec3.createVectorHelper(x, y, z);
		}
	}

	public int getReverse(int i) {
		if (i < 0 || i > 7) {
			return -1;
		}
		int[] ref = { 7, 5, 6, 4, 3, 1, 2, 0 };
		return ref[i];
	}

	public AxisAlignedBB copy() {
		return getBoundingBox(this.center, this.side1, this.side2, this.side3);
	}

	public static OrientedBoundingBox getBoundingBox(double minX, double minY, double minZ, double maxX, double maxY, double maxZ) {
		return new OrientedBoundingBox(minX, minY, minZ, maxX, maxY, maxZ);
	}

	public static OrientedBoundingBox getBoundingBox(Vec3 v1, Vec3 v2, Vec3 v3, Vec3 v4) {
		return new OrientedBoundingBox(v1, v2, v3, v4);
	}

	public boolean intersectsWith(AxisAlignedBB aabb) {
		Vec3[] sepa = getSeparatingAxis(aabb);
		for (int n = 0; n < sepa.length; n++) {
			double[] a1 = this.getLengthOnAxis(sepa[n], this);
			double[] a2 = this.getLengthOnAxis(sepa[n], aabb);
			if (a1[0] > a2[1] || a1[1] < a2[0]) {
				return false;
			}
		}
		return true;
	}

	public AxisAlignedBB offset(double x, double y, double z) {
		Vec3 ofs = Vec3.createVectorHelper(x, y, z);
		this.minX += x;
		this.minY += y;
		this.minZ += z;
		this.maxX += x;
		this.maxY += y;
		this.maxZ += z;
		this.center = MitoMath.vectorSum(ofs, center);
		this.side1 = MitoMath.vectorSum(ofs, side1);
		this.side2 = MitoMath.vectorSum(ofs, side2);
		this.side3 = MitoMath.vectorSum(ofs, side3);

		return this;
	}

	public double calculateXOffset(AxisAlignedBB aabb, double moveX) {
		if (intersectsWith(aabb.copy().offset(moveX, 0, 0))) {
			double d1;
			Vec3[] sepa = getSeparatingAxis(aabb);
			for (int n = 0; n < sepa.length; n++) {
				if (Math.abs(moveX * sepa[n].xCoord) < 1.0E-4D) {
					continue;
				}
				double[] a1 = this.getLengthOnAxis(sepa[n], this);
				double[] a2 = this.getLengthOnAxis(sepa[n], aabb);
				if (a1[0] > a2[1] || a1[1] < a2[0]) {
					if (moveX * sepa[n].xCoord > 0.0D && a2[1] <= a1[0]) {
						d1 = a1[0] - a2[1];

						if (d1 < moveX * sepa[n].xCoord) {
							moveX = d1 / sepa[n].xCoord;
						}
					}
					if (moveX * sepa[n].xCoord < 0.0D && a2[0] >= a1[1]) {
						d1 = a1[1] - a2[0];

						if (d1 > moveX * sepa[n].xCoord) {
							moveX = d1 / sepa[n].xCoord;
						}
					}
				}
			}
		}
		return moveX;
	}

	public double calculateYOffset(AxisAlignedBB aabb, double moveY) {
		if (intersectsWith(aabb.copy().offset(moveY, 0, 0))) {
			MyLogger.info("sepa y  ");
			if (!intersectsWith(aabb)) {
				//MyLogger.info("sepa y inter ");
				double d1;
				Vec3[] sepa = getSeparatingAxis(aabb);
				for (int n = 0; n < sepa.length; n++) {
					if (Math.abs(moveY * sepa[n].yCoord) < 1.0E-4D) {
						continue;
					}
					double[] a1 = this.getLengthOnAxis(sepa[n], this);
					double[] a2 = this.getLengthOnAxis(sepa[n], aabb);
					if (a1[0] > a2[1] || a1[1] < a2[0]) {
						if (moveY * sepa[n].yCoord > 0.0D && a2[1] <= a1[0]) {
							d1 = a1[0] - a2[1];

							if (d1 < moveY * sepa[n].yCoord) {
								moveY = d1 / sepa[n].yCoord;
							}
						}
						if (moveY * sepa[n].yCoord < 0.0D && a2[0] >= a1[1]) {
							d1 = a1[1] - a2[0];

							if (d1 > moveY * sepa[n].yCoord) {
								moveY = d1 / sepa[n].yCoord;
							}
						}
						MyLogger.info("sepa y : " + moveY + " : " + sepa[n].yCoord);
					}
				}
			}
		}
		return moveY;
	}

	public double calculateZOffset(AxisAlignedBB aabb, double moveZ) {
		if (intersectsWith(aabb.copy().offset(moveZ, 0, 0))) {
			double d1;
			Vec3[] sepa = getSeparatingAxis(aabb);
			for (int n = 0; n < sepa.length; n++) {
				if (Math.abs(moveZ * sepa[n].zCoord) < 1.0E-4D) {
					continue;
				}
				double[] a1 = this.getLengthOnAxis(sepa[n], this);
				double[] a2 = this.getLengthOnAxis(sepa[n], aabb);
				if (a1[0] > a2[1] || a1[1] < a2[0]) {
					if (moveZ * sepa[n].zCoord > 0.0D && a2[1] <= a1[0]) {
						d1 = a1[0] - a2[1];

						if (d1 < moveZ * sepa[n].zCoord) {
							moveZ = d1 / sepa[n].zCoord;
						}
					}
					if (moveZ * sepa[n].zCoord < 0.0D && a2[0] >= a1[1]) {
						d1 = a1[1] - a2[0];

						if (d1 > moveZ * sepa[n].zCoord) {
							moveZ = d1 / sepa[n].zCoord;
						}
					}
				}
			}
		}
		return moveZ;
	}

}
