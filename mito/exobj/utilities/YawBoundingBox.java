package com.mito.exobj.utilities;

import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.Vec3;

public class YawBoundingBox extends AxisAlignedBB {

	double yaw;

	public YawBoundingBox(double minX, double minY, double minZ, double maxX, double maxY, double maxZ) {
		super(minX, minY, minZ, maxX, maxY, maxZ);
		this.yaw = 0;
	}

	public YawBoundingBox(double minX, double minY, double minZ, double maxX, double maxY, double maxZ, double roll, double pitch, double yaw) {
		super(minX, minY, minZ, maxX, maxY, maxZ);
		this.yaw = yaw;
	}

	public boolean intersectsWith(AxisAlignedBB aabb1) {
		Vec3 center = this.getCenter();
		Vec3 center1 = getCenter(aabb1);
		Vec3 deff = MitoMath.sub_vector(MitoMath.rot(center, center1, 0, 0, yaw), center1);
		AxisAlignedBB aabb = aabb1.copy().offset(deff.xCoord, deff.yCoord, deff.zCoord);
		return aabb.maxX > this.minX && aabb.minX < this.maxX ? (aabb.maxY > this.minY && aabb.minY < this.maxY ? aabb.maxZ > this.minZ && aabb.minZ < this.maxZ : false) : false;
	}
	
	public boolean intersectsXZ(AxisAlignedBB aabb){
		if(aabb == null){
			return false;
		}
		Vec2 axis1 = new Vec2(Math.cos(yaw), Math.sin(yaw));
		Vec2 axis2 = new Vec2(-Math.sin(yaw), Math.cos(yaw));
		Vec2 axis3, axis4;
		if(aabb instanceof YawBoundingBox){
			YawBoundingBox obb = (YawBoundingBox)aabb;
			axis1 = new Vec2(Math.cos(obb.yaw), Math.sin(obb.yaw));
			axis2 = new Vec2(-Math.sin(obb.yaw), Math.cos(obb.yaw));
		} else {
			axis3 = new Vec2(1, 0);
			axis4 = new Vec2(0, 1);
		}
		//double tpromax = axis1.dotProduct(v)
		
		
		
		
		return true;
	}

	public Vec3 getCenter() {
		return Vec3.createVectorHelper((maxX + minX) / 2, (maxY + minY) / 2, (maxZ + minZ) / 2);
	}

	public Vec3 getCenter(AxisAlignedBB aabb) {
		return Vec3.createVectorHelper((aabb.maxX + aabb.minX) / 2, (aabb.maxY + aabb.minY) / 2, (aabb.maxZ + aabb.minZ) / 2);
	}

	public double calculateXOffset(AxisAlignedBB p_72316_1_, double p_72316_2_) {
		if (p_72316_1_.maxY > this.minY && p_72316_1_.minY < this.maxY) {
			if (p_72316_1_.maxZ > this.minZ && p_72316_1_.minZ < this.maxZ) {
				double d1;

				if (p_72316_2_ > 0.0D && p_72316_1_.maxX <= this.minX) {
					d1 = this.minX - p_72316_1_.maxX;

					if (d1 < p_72316_2_) {
						p_72316_2_ = d1;
					}
				}

				if (p_72316_2_ < 0.0D && p_72316_1_.minX >= this.maxX) {
					d1 = this.maxX - p_72316_1_.minX;

					if (d1 > p_72316_2_) {
						p_72316_2_ = d1;
					}
				}

				return p_72316_2_;
			} else {
				return p_72316_2_;
			}
		} else {
			return p_72316_2_;
		}
	}

	public double calculateYOffset(AxisAlignedBB p_72323_1_, double p_72323_2_) {
		if (p_72323_1_.maxX > this.minX && p_72323_1_.minX < this.maxX) {
			if (p_72323_1_.maxZ > this.minZ && p_72323_1_.minZ < this.maxZ) {
				double d1;

				if (p_72323_2_ > 0.0D && p_72323_1_.maxY <= this.minY) {
					d1 = this.minY - p_72323_1_.maxY;

					if (d1 < p_72323_2_) {
						p_72323_2_ = d1;
					}
				}

				if (p_72323_2_ < 0.0D && p_72323_1_.minY >= this.maxY) {
					d1 = this.maxY - p_72323_1_.minY;

					if (d1 > p_72323_2_) {
						p_72323_2_ = d1;
					}
				}

				return p_72323_2_;
			} else {
				return p_72323_2_;
			}
		} else {
			return p_72323_2_;
		}
	}

	public double calculateZOffset(AxisAlignedBB p_72322_1_, double p_72322_2_) {
		if (p_72322_1_.maxX > this.minX && p_72322_1_.minX < this.maxX) {
			if (p_72322_1_.maxY > this.minY && p_72322_1_.minY < this.maxY) {
				double d1;

				if (p_72322_2_ > 0.0D && p_72322_1_.maxZ <= this.minZ) {
					d1 = this.minZ - p_72322_1_.maxZ;

					if (d1 < p_72322_2_) {
						p_72322_2_ = d1;
					}
				}

				if (p_72322_2_ < 0.0D && p_72322_1_.minZ >= this.maxZ) {
					d1 = this.maxZ - p_72322_1_.minZ;

					if (d1 > p_72322_2_) {
						p_72322_2_ = d1;
					}
				}

				return p_72322_2_;
			} else {
				return p_72322_2_;
			}
		} else {
			return p_72322_2_;
		}
	}

}
