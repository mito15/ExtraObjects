package com.mito.exobj.client;

import java.util.List;

import org.lwjgl.opengl.GL11;

import com.mito.exobj.BraceBase.BB_EnumTexture;
import com.mito.exobj.BraceBase.BB_GroupBase;
import com.mito.exobj.BraceBase.BB_Render;
import com.mito.exobj.BraceBase.BB_ResisteredList;
import com.mito.exobj.BraceBase.ExtraObject;
import com.mito.exobj.BraceBase.Brace.Brace;
import com.mito.exobj.client.render.model.IDrawBrace;
import com.mito.exobj.network.BB_PacketProcessor;
import com.mito.exobj.network.BB_PacketProcessor.Mode;
import com.mito.exobj.network.GroupPacketProcessor;
import com.mito.exobj.network.GroupPacketProcessor.EnumGroupMode;
import com.mito.exobj.network.PacketHandler;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;

public class BB_SelectedGroup extends BB_GroupBase {

	public mitoClientProxy proxy;
	public Vec3 set = Vec3.createVectorHelper(0, 0, 0);
	public boolean activated = false;
	private boolean modecopy = false;
	public int pasteNum = 0;
	public int size = 100;
	public int rot = 0;
	public boolean modeMove = false;
	public boolean modeBlock = false;

	public BB_SelectedGroup(mitoClientProxy px) {
		this.proxy = px;
	}

	public void initNum() {
		size = 100;
		rot = 0;
	}

	public void addShift(ExtraObject... bases) {
		initNum();
		for (int n = 0; n < bases.length; n++) {
			if (this.list.contains(bases[n])) {
				this.list.remove(bases[n]);
			} else {
				this.list.add(bases[n]);
			}
		}
	}

	public void addShift(List<ExtraObject> bases) {
		initNum();
		for (int n = 0; n < bases.size(); n++) {
			if (this.list.contains(bases.get(n))) {
				this.list.remove(bases.get(n));
			} else {
				this.list.add(bases.get(n));
			}
		}
	}

	public void replace(List<ExtraObject> bases) {
		initNum();
		this.list = bases;
	}

	public void replace(ExtraObject base) {
		initNum();
		this.list.clear();
		this.list.add(base);
	}

	public void remove(ExtraObject... bases) {
		initNum();
		for (int n = 0; n < bases.length; n++) {
			this.list.remove(bases[n]);
		}
	}

	public void delete() {
		initNum();
		this.list.clear();
	}

	public List<ExtraObject> getList() {
		return this.list;
	}

	public boolean drawHighLightGroup(EntityPlayer player, float partialticks) {
		if (this.list.isEmpty()) {
			return false;
		}
		for (int n = 0; n < this.list.size(); n++) {
			GL11.glMatrixMode(GL11.GL_MODELVIEW);
			GL11.glPushMatrix();
			GL11.glTranslated(-(player.lastTickPosX + (player.posX - player.lastTickPosX) * partialticks),
					-(player.lastTickPosY + (player.posY - player.lastTickPosY) * partialticks),
					-(player.lastTickPosZ + (player.posZ - player.lastTickPosZ) * partialticks));
			BB_Render render = BB_ResisteredList.getBraceBaseRender(this.list.get(n));
			render.drawHighLight(this.list.get(n), partialticks);//4.0F
			GL11.glPopMatrix();
		}
		return true;
	}

	public boolean drawHighLightCopy(EntityPlayer player, float partialticks, MovingObjectPosition mop) {
		if (this.list.isEmpty()) {
			return false;
		}
		for (int n = 0; n < this.list.size(); n++) {
			GL11.glMatrixMode(GL11.GL_MODELVIEW);
			GL11.glPushMatrix();
			GL11.glTranslated(-(player.lastTickPosX + (player.posX - player.lastTickPosX) * partialticks),
					-(player.lastTickPosY + (player.posY - player.lastTickPosY) * partialticks),
					-(player.lastTickPosZ + (player.posZ - player.lastTickPosZ) * partialticks));
			Vec3 pos = this.getDistance(mop);
			GL11.glTranslated(pos.xCoord, pos.yCoord, pos.zCoord);
			BB_Render render = BB_ResisteredList.getBraceBaseRender(this.list.get(n));
			render.drawHighLight(this.list.get(n), partialticks);//4.0F
			GL11.glPopMatrix();
		}
		return true;
	}

	public double getMaxY() {
		double ret = 0;
		for (int n = 0; n < this.list.size(); n++) {
			double m = this.list.get(n).getMaxY();
			if (ret < m) {
				ret = m;
			}
		}
		return ret;
	}

	public double getMinY() {
		double ret = 128;
		for (int n = 0; n < this.list.size(); n++) {
			double m = this.list.get(n).getMinY();
			if (ret > m) {
				ret = m;
			}
		}
		return ret;
	}

	public boolean modeCopy() {
		return modecopy;
	}

	public void setcopy(boolean modecopy) {
		this.modecopy = modecopy;
	}

	public Vec3 getDistance(MovingObjectPosition mop) {
		if (list.isEmpty()) {
			return null;
		}
		Vec3 c = getCenter();
		return mop.hitVec.addVector(-c.xCoord, -c.yCoord, -c.zCoord);
	}

	public void applyProperty(BB_EnumTexture tex, int color, IDrawBrace shape) {
		for (int n = 0; n < this.list.size(); n++) {
			if (this.list.get(n) instanceof Brace) {
				Brace brace = ((Brace) this.list.get(n));
				if (tex != null) {
					brace.texture = tex;
				}
				if (shape != null) {
					brace.shape = shape;
					brace.shouldUpdateRender = true;
				}
				if (brace.texture.hasColor) {
					if (color >= 0 && color < 16) {
						brace.color = color;
					} else if (color == 16) {
					} else {
						brace.color = 0;
					}
				} else {
					brace.color = 0;
				}
				PacketHandler.INSTANCE.sendToServer(new BB_PacketProcessor(Mode.SYNC, this.list.get(n)));
			}
		}
	}

	public void applyColor(int color) {
		for (int n = 0; n < this.list.size(); n++) {
			if (this.list.get(n) instanceof Brace) {
				Brace brace = ((Brace) this.list.get(n));
				if (brace.texture.hasColor) {
					if (color >= 0 && color < 16) {
						brace.color = color;
					} else if (color == 16) {
					} else {
						brace.color = 0;
					}
				} else {
					brace.color = 0;
				}
				PacketHandler.INSTANCE.sendToServer(new BB_PacketProcessor(Mode.SYNC, this.list.get(n)));
			}
		}
	}

	public void init() {
		this.delete();
		this.activated = false;
		this.modecopy = false;
		this.modeMove = false;
	}

	public void applySize(int isize) {
		for (int n = 0; n < this.list.size(); n++) {
			if (this.list.get(n) instanceof Brace) {
				Brace brace = ((Brace) this.list.get(n));
				brace.size = (double) isize * 0.05;
				brace.shouldUpdateRender = true;
				PacketHandler.INSTANCE.sendToServer(new BB_PacketProcessor(Mode.SYNC, this.list.get(n)));
			}
		}
	}

	public int getSize() {
		if (this.list.isEmpty()) {
			return -1;
		} else if (this.list.size() == 1 && list.get(0) instanceof Brace) {
			return (int) (((Brace) list.get(0)).size * 20);
		} else {
			int is = (int) (((Brace) list.get(0)).size * 20);
			for (int n = 0; n < this.list.size(); n++) {
				if (this.list.get(n) instanceof Brace) {
					Brace brace = ((Brace) this.list.get(n));
					if (is != (int) (brace.size * 20)) {
						return -1;
					}
				}
			}
			return is;
		}
	}

	public void applyRoll(int iroll) {
		for (int n = 0; n < this.list.size(); n++) {
			if (this.list.get(n) instanceof Brace) {
				Brace brace = ((Brace) this.list.get(n));
				brace.setRoll(iroll);
				brace.shouldUpdateRender = true;
				PacketHandler.INSTANCE.sendToServer(new BB_PacketProcessor(Mode.SYNC, this.list.get(n)));
			}
		}
	}

	public void applyGroupSize(int isize) {
		Vec3 c = getCenter();
		for (int n = 0; n < this.list.size(); n++) {
			ExtraObject brace = this.list.get(n);
			brace.resize(c, (double) isize / this.size);
			brace.shouldUpdateRender = true;
			PacketHandler.INSTANCE.sendToServer(new BB_PacketProcessor(Mode.SYNC, this.list.get(n)));

		}
		this.size = isize;
	}

	public void applyGroupRot(int irot) {
		Vec3 c = getCenter();
		for (int n = 0; n < this.list.size(); n++) {
			ExtraObject brace = this.list.get(n);
			brace.rotation(c, -this.rot + irot);
			brace.shouldUpdateRender = true;
			PacketHandler.INSTANCE.sendToServer(new BB_PacketProcessor(Mode.SYNC, this.list.get(n)));

		}
		this.rot = irot;
	}

	public Vec3 getCenter() {
		//return Vec3.createVectorHelper(bases.get(0).pos.xCoord, this.getMinY(), bases.get(0).pos.zCoord);
		return set;
	}

	public void setmove(boolean b) {
		this.modeMove = b;
	}

	public void breakGroup() {
		/*for (int n = 0; n < getList().size(); n++) {
			ExtraObject base = getList().get(n);
			base.breakBrace(Main.proxy.getClientPlayer());
		}*/
		PacketHandler.INSTANCE.sendToServer(new GroupPacketProcessor(EnumGroupMode.DELETE, getList()));
	}

	public void grouping() {
		PacketHandler.INSTANCE.sendToServer(new GroupPacketProcessor(EnumGroupMode.GROUPING, getList()));
		this.delete();
	}

	public void setblock(boolean b) {
		this.modeBlock = b;
	}

}
