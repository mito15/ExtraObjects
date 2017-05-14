package com.mito.exobj.asm;

import java.util.List;

import com.mito.exobj.BraceBase.BB_DataLists;
import com.mito.exobj.BraceBase.ExtraObject;
import com.mito.exobj.utilities.Line;
import com.mito.exobj.utilities.MitoMath;
import com.mito.exobj.utilities.MyUtil;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

public class BraceCoreHooks {

	public static void getCollisionHook(World world, AxisAlignedBB aabb, List collidingBoundingBoxes, Entity entity) {
		double d0 = 0.25D;
		List<ExtraObject> list = BB_DataLists.getWorldData(world).getExtraObjectWithAABB(aabb);
		for (ExtraObject base : list) {
			base.addCollisionBoxesToList(world, aabb, collidingBoundingBoxes, entity);
		}
	}

	public static void rayTrace(float partialticks) {
		Minecraft mc = Minecraft.getMinecraft();
		if (mc.renderViewEntity != null) {
			if (mc.theWorld != null) {
				EntityLivingBase player = mc.renderViewEntity;
				double d0 = (double) mc.playerController.getBlockReachDistance();
				MovingObjectPosition pre = mc.objectMouseOver;
				Vec3 start = player.getPosition(partialticks);
				Vec3 vec31 = player.getLook((float) partialticks);
				Vec3 end = start.addVector(vec31.xCoord * d0, vec31.yCoord * d0, vec31.zCoord * d0);
				MovingObjectPosition m2 = rayTraceBrace(player, start, end, (float) partialticks);
				if (m2 != null && pre != null) {
					if (!(MitoMath.subAbs(start, pre.hitVec) + 0.05 < MitoMath.subAbs(start, m2.hitVec) && !(player.worldObj.isAirBlock(pre.blockX, pre.blockY, pre.blockZ)))) {
						mc.objectMouseOver = m2;
					}
				}
			}
		}
	}

	public static MovingObjectPosition rayTraceBrace(EntityLivingBase player, Vec3 set, Vec3 end, double partialticks) {
		World world = player.worldObj;
		MovingObjectPosition m = null;
		List list = BB_DataLists.getWorldData(world).getExtraObjectWithAABB(MyUtil.createAABBByVec3(set, end));
		double l = 999.0D;
		for (int n = 0; n < list.size(); n++) {
			if (list.get(n) instanceof ExtraObject) {
				ExtraObject base = (ExtraObject) list.get(n);
				Line line = base.interactWithRay(set, end);
				if (line != null) {
					double l2 = MitoMath.subAbs(line.start, set);
					if (l2 < l) {
						l = l2;
						m = new MovingObjectPosition(BB_DataLists.getWorldData(world).wrapper.wrap(base), MitoMath.copyVec3(line.end));
					}
				}
			}
		}
		return m;
	}
}
