package com.mito.exobj.BraceBase;

import java.util.List;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;

import com.mito.exobj.MyLogger;
import com.mito.exobj.client.render.CreateVertexBufferObject;
import com.mito.exobj.client.render.VBOHandler;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.culling.ICamera;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.Vec3;
import net.minecraftforge.client.MinecraftForgeClient;

public class BB_RenderHandler {

	public BB_RenderHandler() {
	}

	public static void enableClient() {
		GL11.glEnableClientState(GL11.GL_VERTEX_ARRAY);
		GL11.glEnableClientState(GL11.GL_COLOR_ARRAY);
		GL11.glEnableClientState(GL11.GL_TEXTURE_COORD_ARRAY);
		GL11.glEnableClientState(GL11.GL_NORMAL_ARRAY);
	}

	public static void disableClient() {
		GL11.glDisableClientState(GL11.GL_VERTEX_ARRAY);
		GL11.glDisableClientState(GL11.GL_COLOR_ARRAY);
		GL11.glDisableClientState(GL11.GL_TEXTURE_COORD_ARRAY);
		GL11.glDisableClientState(GL11.GL_NORMAL_ARRAY);
	}

	public static void onRenderEntities(EntityLivingBase entity, ICamera camera, float partialticks) {

		if (MinecraftForgeClient.getRenderPass() == 0) {

			//GL11.glTranslatef(-(float)RenderManager.renderPosX, -(float)RenderManager.renderPosY, -(float)RenderManager.renderPosZ);

			//GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
			/*double d0 = entity.prevPosX + (entity.posX - entity.prevPosX) * (double) partialticks;
			double d1 = entity.prevPosY + (entity.posY - entity.prevPosY) * (double) partialticks;
			double d2 = entity.prevPosZ + (entity.posZ - entity.prevPosZ) * (double) partialticks;
			GL11.glTranslated(-d0, -d1, -d2);*/
			/*
			 EntityLivingBase entitylivingbase1 = this.mc.renderViewEntity;
			    double d3 = entitylivingbase1.lastTickPosX + (entitylivingbase1.posX - entitylivingbase1.lastTickPosX) * (double)partialticks;
			    double d4 = entitylivingbase1.lastTickPosY + (entitylivingbase1.posY - entitylivingbase1.lastTickPosY) * (double)partialticks;
			    double d5 = entitylivingbase1.lastTickPosZ + (entitylivingbase1.posZ - entitylivingbase1.lastTickPosZ) * (double)partialticks;
			RenderManager.renderPosX = d3;
			RenderManager.renderPosY = d4;
			RenderManager.renderPosZ = d5;*/

			BB_DataWorld data = LoadClientWorldHandler.INSTANCE.data;

			for (BB_DataChunk chunk : data.coordToDataMapping.values()) {
				List<ExtraObject> list = chunk.exObjList;

				if (!list.isEmpty()) {
					GL11.glPushMatrix();
					Minecraft.getMinecraft().renderEngine.bindTexture(TextureMap.locationBlocksTexture);
					enableClient();

					Minecraft.getMinecraft().entityRenderer.enableLightmap((double) partialticks);
					GL11.glTexParameterf(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL11.GL_REPEAT);
					GL11.glShadeModel(GL11.GL_SMOOTH);
					GL11.glPushMatrix();
					GL11.glTranslatef((float) (-RenderManager.renderPosX - chunk.buffer.v.xCoord), (float) (-RenderManager.renderPosY - chunk.buffer.v.yCoord), (float) (-RenderManager.renderPosZ - chunk.buffer.v.zCoord));
					chunk.buffer.draw();
					GL11.glPopMatrix();
					GL11.glShadeModel(GL11.GL_FLAT);

					GL11.glPushMatrix();
					for (ExtraObject base : list) {
						BB_Render render = BB_ResisteredList.getBraceBaseRender(base);
						if (!render.isVbo(base)) {
							render.doRender(base, -(float) RenderManager.renderPosX, -(float) RenderManager.renderPosY, -(float) RenderManager.renderPosZ, partialticks);
						}
					}
					GL11.glPopMatrix();

					disableClient();
					if (chunk.isShouldUpdateRender()) {
						chunk.buffer.delete();
						CreateVertexBufferObject c = CreateVertexBufferObject.INSTANCE;
						ExtraObject base1 = list.get(0);
						Vec3 v = Vec3.createVectorHelper(-chunk.xPosition * 16.0, -base1.pos.yCoord, -chunk.zPosition * 16.0);
						MyLogger.info(base1.pos.xCoord, RenderManager.renderPosX);
						c.beginRegist(GL15.GL_STATIC_DRAW, GL11.GL_TRIANGLES);
						c.translate(v);
						c.setColor(1.0F, 1.0F, 1.0F, 1.0F);
						for (ExtraObject base : list) {
							BB_Render render = BB_ResisteredList.getBraceBaseRender(base);
							render.updateRender(c, base);
						}
						chunk.setShouldUpdateRender(false);
						VBOHandler vbo = c.end();
						chunk.buffer.v = v;
						chunk.buffer.add(vbo);
						//MyLogger.info("render update");
					}
					Minecraft.getMinecraft().entityRenderer.disableLightmap((double) partialticks);
					GL11.glPopMatrix();
				}
			}
		}
	}

}
