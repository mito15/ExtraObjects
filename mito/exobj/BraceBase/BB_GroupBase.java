package com.mito.exobj.BraceBase;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;

import com.mito.exobj.Main;
import com.mito.exobj.client.render.CreateVertexBufferObject;
import com.mito.exobj.client.render.VBOHandler;
import com.mito.exobj.client.render.VBOList;

import net.minecraft.entity.player.EntityPlayer;

public class BB_GroupBase {
	
	public List<ExtraObject> list = new ArrayList<ExtraObject>();
	private boolean shouldUpdateHighLight = true;
	private VBOList buffer = new VBOList();
	
	public void updateHighLight(){
		this.shouldUpdateHighLight = true;
	}
	
	public boolean drawHighLightGroup(EntityPlayer player, float partialticks) {
		if (this.list.isEmpty()) {
			return false;
		}
		GL11.glMatrixMode(GL11.GL_MODELVIEW);
		GL11.glPushMatrix();
		GL11.glTranslated(-(player.lastTickPosX + (player.posX - player.lastTickPosX) * partialticks),
				-(player.lastTickPosY + (player.posY - player.lastTickPosY) * partialticks),
				-(player.lastTickPosZ + (player.posZ - player.lastTickPosZ) * partialticks));
		float size = 2.0F;

		if (shouldUpdateHighLight) {
			buffer.delete();
			CreateVertexBufferObject c = CreateVertexBufferObject.INSTANCE;
			c.beginRegist(GL15.GL_STATIC_DRAW, GL11.GL_TRIANGLES);
			c.setColor(1.0F, 1.0F, 1.0F, 1.0F);
			for (ExtraObject base : list) {
				BB_Render render = BB_ResisteredList.getBraceBaseRender(base);
				render.updateRender(c, base);
			}
			VBOHandler vbo = c.end();
			buffer.add(vbo);
			shouldUpdateHighLight = false;
		}

		GL11.glPushMatrix();

		GL11.glLineWidth(size);
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

		GL11.glDisable(GL11.GL_TEXTURE_2D);
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_ONE_MINUS_DST_COLOR, GL11.GL_ZERO);
		BB_RenderHandler.enableClient();
		this.buffer.draw(GL11.GL_LINE_LOOP);
		BB_RenderHandler.disableClient();

		GL11.glDisable(GL11.GL_BLEND);
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		GL11.glPopMatrix();
		
		GL11.glPopMatrix();
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
	
	public void outputObj(){
		File dir = Main.INSTANCE.ObjsDir;
		//BB_OutputHandler(dir, )
	}
	
	

}
