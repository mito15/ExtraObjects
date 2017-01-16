package com.mito.exobj.BraceBase;

import java.util.List;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;

import com.mito.exobj.common.MyLogger;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.culling.ICamera;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.entity.EntityLivingBase;
import net.minecraftforge.client.MinecraftForgeClient;

public class BB_RenderHandler {

	BB_Render renderer;
	LoadClientWorldHandler lcw;

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

	/*public static void onRenderEntities(EntityLivingBase entity, ICamera camera, float partialticks) {
	
		if (MinecraftForgeClient.getRenderPass() == 0) {
	
			Minecraft.getMinecraft().entityRenderer.enableLightmap((double) partialticks);
			Minecraft.getMinecraft().renderEngine.bindTexture(TextureMap.locationBlocksTexture);
			//GL11.glTexParameterf(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL11.GL_REPEAT);
			//enableClient();
			//GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
			double d0 = entity.prevPosX + (entity.posX - entity.prevPosX) * (double) partialticks;
			double d1 = entity.prevPosY + (entity.posY - entity.prevPosY) * (double) partialticks;
			double d2 = entity.prevPosZ + (entity.posZ - entity.prevPosZ) * (double) partialticks;
			GL11.glTranslated(-d0, -d1, -d2);
			List<ExtraObject> list = LoadClientWorldHandler.INSTANCE.data.braceBaseList;
	
			//Minecraft.getMinecraft().entityRenderer.enableLightmap((double) partialticks);
			//camera.isBoundingBoxInFrustum(null);
	
			for (int n = 0; n < list.size(); n++) {
	
				ExtraObject base = list.get(n);
				AxisAlignedBB aabb = base.getBoundingBox();
				if (aabb != null && !camera.isBoundingBoxInFrustum(aabb)) {
					continue;
				}
				GL11.glMatrixMode(GL11.GL_MODELVIEW);
				GL11.glPushMatrix();
				if (base.isStatic) {
					GL11.glTranslated(base.pos.xCoord, base.pos.yCoord, base.pos.zCoord);
					int i = base.getBrightnessForRender(partialticks);
					int j = i % 65536;
					int k = i / 65536;
					OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, (float) j / 1.0F, (float) k / 1.0F);
				} else {
					double x = base.prevPos.xCoord + (base.pos.xCoord - base.prevPos.xCoord) * (double) partialticks;
					double y = base.prevPos.yCoord + (base.pos.yCoord - base.prevPos.yCoord) * (double) partialticks;
					double z = base.prevPos.zCoord + (base.pos.zCoord - base.prevPos.zCoord) * (double) partialticks;
					GL11.glTranslated(x, y, z);
					int i = base.getBrightnessForRender(partialticks);
					int j = i % 65536;
					int k = i / 65536;
					OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, (float) j / 1.0F, (float) k / 1.0F);
				}
	
				BB_Render render = BB_ResisteredList.getBraceBaseRender(base);
	
				render.doRender(base, partialticks);
	
				GL11.glMatrixMode(GL11.GL_MODELVIEW);
				GL11.glPopMatrix();
			}
			//GL11.glTexParameterf(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL11.GL_CLAMP);
			//disableClient();
			Minecraft.getMinecraft().entityRenderer.disableLightmap((double) partialticks);
		}
	}*/

	public static void onRenderEntities(EntityLivingBase entity, ICamera camera, float partialticks) {

		if (MinecraftForgeClient.getRenderPass() == 0) {

			Minecraft.getMinecraft().renderEngine.bindTexture(TextureMap.locationBlocksTexture);
			//GL11.glTexParameterf(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL11.GL_REPEAT);
			enableClient();
			//GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
			double d0 = entity.prevPosX + (entity.posX - entity.prevPosX) * (double) partialticks;
			double d1 = entity.prevPosY + (entity.posY - entity.prevPosY) * (double) partialticks;
			double d2 = entity.prevPosZ + (entity.posZ - entity.prevPosZ) * (double) partialticks;
			GL11.glTranslated(-d0, -d1, -d2);
			BB_DataWorld data = LoadClientWorldHandler.INSTANCE.data;
			List<ExtraObject> list = data.braceBaseList;

			Minecraft.getMinecraft().entityRenderer.enableLightmap((double) partialticks);
			//camera.isBoundingBoxInFrustum(null);

			/*for (int n = 0; n < list.size(); n++) {
				ExtraObject base = list.get(n);
				BB_Render render = BB_ResisteredList.getBraceBaseRender(base);
				GL11.glPushMatrix();
				GL11.glTranslated(base.pos.xCoord, base.pos.yCoord, base.pos.zCoord);
				int i = base.getBrightnessForRender(partialticks);
				int j = i % 65536;
				int k = i / 65536;
				OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, (float) j / 1.0F, (float) k / 1.0F);
				render.doRender(base, partialticks);
				GL11.glPopMatrix();
			}*/
			GL11.glTexParameterf(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL11.GL_REPEAT);
			data.buffer.draw();

			disableClient();
			if (data.shouldUpdateRender) {
				data.buffer.delete();
				CreateVertexBufferObject c = CreateVertexBufferObject.INSTANCE;
				c.beginRegist(GL15.GL_STATIC_DRAW, GL11.GL_TRIANGLES);
				c.setColor(1.0F, 1.0F, 1.0F, 1.0F);
				for (int n = 0; n < list.size(); n++) {
					ExtraObject base = list.get(n);
					BB_Render render = BB_ResisteredList.getBraceBaseRender(base);
					render.updateRender(c, base, partialticks);
				}
				data.shouldUpdateRender = false;
				VBOHandler vbo = c.end();
				data.buffer.add(vbo);
				MyLogger.info("render update");
			}
			//GL11.glTexParameterf(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL11.GL_CLAMP);
			Minecraft.getMinecraft().entityRenderer.disableLightmap((double) partialticks);
		}
	}

}
