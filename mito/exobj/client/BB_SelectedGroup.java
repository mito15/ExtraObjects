package com.mito.exobj.client;

import java.util.List;

import org.lwjgl.opengl.GL11;

import com.mito.exobj.BraceBase.BB_GroupBase;
import com.mito.exobj.BraceBase.ExtraObject;
import com.mito.exobj.BraceBase.Brace.Brace;
import com.mito.exobj.main.mitoClientProxy;
import com.mito.exobj.network.BB_PacketProcessor;
import com.mito.exobj.network.BB_PacketProcessor.Mode;
import com.mito.exobj.network.GroupPacketProcessor;
import com.mito.exobj.network.GroupPacketProcessor.EnumGroupMode;
import com.mito.exobj.network.PacketHandler;

import net.minecraft.block.Block;
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
		this.updateHighLight();
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

	public boolean drawHighLightCopy(EntityPlayer player, float partialticks, MovingObjectPosition mop) {
		GL11.glPushMatrix();
		Vec3 pos = this.getDistance(mop);
		GL11.glTranslated(pos.xCoord, pos.yCoord, pos.zCoord);
		this.drawHighLightGroup(player, partialticks);
		GL11.glPopMatrix();
		return true;
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
	
	private void update(ExtraObject exo){
		exo.updateRenderer();
		this.updateHighLight();
		PacketHandler.INSTANCE.sendToServer(new BB_PacketProcessor(Mode.SYNC, exo));
	}

	public void applyProperty(Block tex, int color, String shape) {
		for (int n = 0; n < this.list.size(); n++) {
			if (this.list.get(n) instanceof Brace) {
				Brace brace = ((Brace) this.list.get(n));
				if (tex != null) {
					brace.texture = tex;
				}
				if (shape != null) {
					brace.shape = shape;
					//brace.shouldUpdateRender = true;
				}
				if (color >= 0 && color < 16) {
					brace.color = color;
				} else if (color == 16) {
				} else {
					brace.color = 0;
				}
				update(brace);
			}
		}
	}

	public void applyColor(int color) {
		for (int n = 0; n < this.list.size(); n++) {
			if (this.list.get(n) instanceof Brace) {
				Brace brace = ((Brace) this.list.get(n));

				if (color >= 0 && color < 16) {
					brace.color = color;
				} else if (color == 16) {
				} else {
					brace.color = 0;
				}
				update(brace);
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
				update(brace);
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
				update(brace);
			}
		}
	}

	public void applyGroupSize(int isize) {
		Vec3 c = getCenter();
		for (int n = 0; n < this.list.size(); n++) {
			ExtraObject brace = this.list.get(n);
			brace.resize(c, (double) isize / this.size);
			update(brace);

		}
		this.size = isize;
	}

	public void applyGroupRot(int irot) {
		Vec3 c = getCenter();
		for (int n = 0; n < this.list.size(); n++) {
			ExtraObject brace = this.list.get(n);
			brace.rotation(c, -this.rot + irot);
			update(brace);

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
		this.updateHighLight();
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
