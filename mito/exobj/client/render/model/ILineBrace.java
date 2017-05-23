package com.mito.exobj.client.render.model;

import java.util.List;

import com.mito.exobj.utilities.Line;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;

public interface ILineBrace {
	
	public void move(Vec3 motion, int command);

	public void writeNBT(NBTTagCompound nbt);

	public void rotation(Vec3 cent, double yaw);

	public void resize(Vec3 cent, double i);

	public void addCoordinate(double x, double y, double z);

	public void snap(MovingObjectPosition mop, boolean b);

	public double getLength();

	public Vec3 getStart();

	public Vec3 getEnd();
	
	public LineWithDirection[] getDrawLine();
	
	public List<Line> getSegments();

	public List<Vec3> getLine();

}
