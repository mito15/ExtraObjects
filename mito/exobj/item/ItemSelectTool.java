package com.mito.exobj.item;

import java.util.List;

import com.mito.exobj.Main;
import com.mito.exobj.BraceBase.BB_DataLists;
import com.mito.exobj.BraceBase.ExtraObject;
import com.mito.exobj.client.BB_Key;
import com.mito.exobj.client.BB_SelectedGroup;
import com.mito.exobj.client.render.RenderHighLight;
import com.mito.exobj.entity.EntityWrapperBB;
import com.mito.exobj.network.GroupPacketProcessor;
import com.mito.exobj.network.GroupPacketProcessor.EnumGroupMode;
import com.mito.exobj.network.PacketHandler;
import com.mito.exobj.utilities.MitoMath;
import com.mito.exobj.utilities.MyUtil;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.Facing;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

public class ItemSelectTool extends ItemBraceBase {

	public ItemSelectTool() {
		super();
		this.setTextureName("exobj:select");
		this.setMaxDamage(0);
		this.setHasSubtypes(true);
	}

	@Override
	public ItemStack onItemRightClick(ItemStack itemstack, World world, EntityPlayer player) {
		if (world.isRemote) {
			NBTTagCompound nbt = getTagCompound(itemstack);
			MovingObjectPosition movingOP = Minecraft.getMinecraft().objectMouseOver;
			boolean flag = MyUtil.isBrace(movingOP);
			BB_SelectedGroup sel = Main.proxy.sg;
			MovingObjectPosition mop = this.getMovingOPWithKey(itemstack, world, player, Main.proxy.getKey(), Minecraft.getMinecraft().objectMouseOver, 1.0);
			BB_Key key = Main.proxy.getKey();

			//移動モードのとき
			if (sel.modeMove) {
				//シフトクリックでモード解除
				if (key.isShiftPressed() || key.isControlPressed()) {
					sel.delete();
					//移動処理
				} else {
					if (!sel.getList().isEmpty()) {
						Vec3 pos = sel.getDistance(mop);
						double yaw = 0;
						PacketHandler.INSTANCE.sendToServer(new GroupPacketProcessor(EnumGroupMode.COPY, sel.getList(), pos, yaw));
						sel.breakGroup();
						sel.delete();
					}
				}
				sel.setmove(false);
				sel.activated = false;
				//コピーモードのとき
			} else if (sel.modeCopy()) {
				//シフトクリックでモード解除
				if (key.isShiftPressed() || key.isControlPressed()) {
					sel.delete();
					//コピー処理
				} else {
					if (!sel.getList().isEmpty()) {
						Vec3 pos = sel.getDistance(mop);
						double yaw = 0;
						PacketHandler.INSTANCE.sendToServer(new GroupPacketProcessor(EnumGroupMode.COPY, sel.getList(), pos, yaw));
					}
				}
				if (!key.isAltPressed()) {
					sel.setcopy(false);
					sel.activated = false;
				}
				//ブロック化モードのとき
			} else if (sel.modeBlock) {
				//解除
				if (key.isShiftPressed() || key.isControlPressed()) {
					sel.delete();
					//ブロック化処理
				} else {
					int x = Facing.offsetsXForSide[mop.sideHit];
					int y = Facing.offsetsYForSide[mop.sideHit];
					int z = Facing.offsetsZForSide[mop.sideHit];
					Vec3 v = Vec3.createVectorHelper(mop.blockX + x, mop.blockY + y, mop.blockZ + z);
					PacketHandler.INSTANCE.sendToServer(new GroupPacketProcessor(EnumGroupMode.SETBLOCK, sel.getList(), v));
					sel.breakGroup();
					sel.delete();
				}
				sel.setblock(false);
				sel.activated = false;

				//通常セレクト処理
			} else {
				//矩形セレクト
				if (sel.activated) {
					Vec3 set = mop.hitVec;
					if (MitoMath.subAbs(sel.set, set) < 500000) {
						AxisAlignedBB aabb = MyUtil.createAABBByVec3(sel.set, set);
						List<ExtraObject> list = BB_DataLists.getWorldData(world).getExtraObjectWithAABB(aabb);
						//シフト同時押しでセレクト追加
						if (key.isShiftPressed() || key.isControlPressed()) {
							sel.addShift(list);
						} else {
							sel.replace(list);
						}
					}
					sel.activated = false;
					//直接セレクト
				} else {
					if (flag) {
						ExtraObject base = ((EntityWrapperBB) movingOP.entityHit).base;
						//シフト同時押しでセレクト追加
						if (key.isShiftPressed() || key.isControlPressed()) {
							sel.addShift(base);
							//GUIを開く
						} else {
							if (sel.getList().contains(base)) {
								//GUI
								if (key.isAltPressed()) {
									sel.set = mop.hitVec;
									sel.setcopy(true);
									//PacketHandler.INSTANCE.sendToServer(new GroupPacketProcessor(EnumGroupMode.COPY, sel.getList()));
								} else {
									sel.set = mop.hitVec;
									PacketHandler.INSTANCE.sendToServer(new GroupPacketProcessor(EnumGroupMode.GUI));
								}
							} else {
								sel.replace(base);
							}
						}
						sel.activated = false;
						//ブレース以外をクリック
					} else {
						if (key.isShiftPressed() || key.isControlPressed()) {
							sel.delete();
						} else {
							Vec3 set = mop.hitVec;
							sel.set = set;
							sel.activated = true;
						}
					}
				}
			}
		}
		return itemstack;
	}

	public double getRayDistance(BB_Key key) {
		return key.isAltPressed() ? 3.0 : 5.0;
	}

	@Override
	public void onPlayerStoppedUsing(ItemStack itemstack, World world, EntityPlayer player, int i) {
	}

	@Override
	public boolean drawHighLightBox(ItemStack itemStack, EntityPlayer player, float partialticks, MovingObjectPosition mop) {
		if (Minecraft.getMinecraft().currentScreen == null) {
			BB_SelectedGroup sel = Main.proxy.sg;
			if (mop == null)
				return false;
			Vec3 set = mop.hitVec;
			RenderHighLight rh = RenderHighLight.INSTANCE;
			if (sel.modeCopy() || sel.modeMove) {
				sel.drawHighLightCopy(player, partialticks, mop);
			} else if (sel.modeBlock) {
				int x = Facing.offsetsXForSide[mop.sideHit];
				int y = Facing.offsetsYForSide[mop.sideHit];
				int z = Facing.offsetsZForSide[mop.sideHit];
				Vec3 v = Vec3.createVectorHelper(0.5 + mop.blockX + x, 0.5 + mop.blockY + y, 0.5 + mop.blockZ + z);
				rh.drawBox(player, v, 0.95, partialticks);
			}
			sel.drawHighLightGroup(player, partialticks);
			if (sel.activated && MyUtil.canClick(player.worldObj, Main.proxy.getKey(), mop)) {
				Vec3 end = sel.set;
				rh.drawBox(player, set, end, partialticks);
				return true;
			} else {
				return this.drawHighLightBrace(player, partialticks, mop);
			}
		}
		return false;
	}

	@Override
	public boolean wheelEvent(EntityPlayer player, ItemStack stack, BB_Key key, int dwheel) {
		BB_SelectedGroup sel = Main.proxy.sg;
		if (sel.modeCopy() && key.isShiftPressed()) {
			int w = dwheel / 120;
			double div = sel.pasteNum + w;
			if (sel.pasteNum < 0) {
				sel.pasteNum = 50000;
			}
			return true;
		}
		return false;
	}
}
