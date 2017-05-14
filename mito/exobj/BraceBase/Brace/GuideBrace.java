package com.mito.exobj.BraceBase.Brace;

import com.mito.exobj.BraceBase.ExtraObject;
import com.mito.exobj.utilities.Line;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

public class GuideBrace extends Brace {

	public String name;

	public GuideBrace(World world) {
		super(world);
	}

	public GuideBrace(World world, Vec3 pos) {
		super(world, pos);
	}

	public GuideBrace(World world, Vec3 pos, Vec3 end, double size, EntityPlayer player) {
		this(world, pos);
		this.line = new Line(pos, end);
		this.size = size;
		name = player.getDisplayName();
	}

	public void readExtraObjectFromNBT(NBTTagCompound nbt) {
		//this.line.readNBT(nbt);

		Vec3 start = getVec3(nbt, "start");
		Vec3 end = getVec3(nbt, "end");
		line = new Line(start, end);
		this.size = nbt.getDouble("size");

		name = nbt.getString("player");
	}

	@Override
	public void writeExtraObjectToNBT(NBTTagCompound nbt) {
		if (line != null) {
			line.writeNBT(nbt);
			nbt.setDouble("size", this.size);
		}
		nbt.setString("player", name);
	}

	@Override
	public boolean isBind(ExtraObject eo) {
		return false;
	}

}
