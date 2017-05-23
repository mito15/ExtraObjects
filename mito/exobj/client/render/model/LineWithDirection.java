package com.mito.exobj.client.render.model;

import com.mito.exobj.utilities.MyUtil;

import net.minecraft.util.Vec3;

public class LineWithDirection {

	public final Vec3 start;
	public final Vec3 end;
	public final Mat4 mat1;
	public final Mat4 mat2;
	
	public LineWithDirection(Vec3 s, Vec3 e, Vec3 v1, Vec3 v2, Vec3 v3, Vec3 v4){
		start = s;
		end = e;
		mat1 = MyUtil.getRotationMatrix(v1, v3);
		mat2 = MyUtil.getRotationMatrix(v2, v4);
	}

}
