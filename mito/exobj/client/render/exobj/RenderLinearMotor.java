package com.mito.exobj.client.render.exobj;

import org.lwjgl.opengl.GL11;

import com.mito.exobj.BraceBase.BB_Render;
import com.mito.exobj.BraceBase.ExtraObject;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.ResourceLocation;

public class RenderLinearMotor extends BB_Render {

	@Override
	public void doRender(ExtraObject base, float x, float y, float z, float partialTickTime) {
		Minecraft.getMinecraft().renderEngine.bindTexture(new ResourceLocation("exobj", "textures/blocks/motor.png"));
		double x1;
		double y1;
		double z1;
		if (base.isStatic) {
			x1 = base.pos.xCoord;
			y1 = base.pos.yCoord;
			z1 = base.pos.zCoord;
		} else {
			x1 = base.prevPos.xCoord + (base.pos.xCoord - base.prevPos.xCoord) * (double) partialTickTime;
			y1 = base.prevPos.yCoord + (base.pos.yCoord - base.prevPos.yCoord) * (double) partialTickTime;
			z1 = base.prevPos.zCoord + (base.pos.zCoord - base.prevPos.zCoord) * (double) partialTickTime;
		}
		int i = base.getBrightnessForRender(partialTickTime);
		int j = i % 65536;
		int k = i / 65536;
		OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, (float) j / 1.0F, (float) k / 1.0F);
		float yaw = (float) base.getYaw();
		float pitch = (float) base.getPitch();

		GL11.glTranslated(x1 + x, y1 + y, z1 + z);
		GL11.glRotatef(yaw, 0.0F, 1.0F, 0.0F);
		GL11.glRotatef(pitch, 1.0F, 0.0F, 0.0F);
		renderBox(0.5, 0.5, 0.7);

	}

	public void renderHighLight(ExtraObject base, float partialTickTime) {
		float yaw = (float) base.getYaw();
		float pitch = (float) base.getPitch();

		GL11.glRotatef(pitch, 1.0F, 0.0F, 0.0F);
		GL11.glRotatef(yaw, 0.0F, 1.0F, 0.0F);
		renderBox(0.5, 0.5, 0.7, GL11.GL_LINE_STRIP);
	}

	private void renderBox(double x, double y, double z) {
		renderBox(x, y, z, GL11.GL_QUADS);
	}

	private void renderBox(double x, double y, double z, int mode) {
		Tessellator tessellator = Tessellator.instance;

		tessellator.startDrawing(mode);

		tessellator.setColorRGBA_F(1, 1, 1, 1);
		//if(alpha != 1.0)GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);

		tessellator.setNormal(0, 0, -1);
		tessellator.addVertexWithUV(-x / 2, -y / 2, -z / 2, 0, 0);
		tessellator.addVertexWithUV(-x / 2, y / 2, -z / 2, 0, y);
		tessellator.addVertexWithUV(x / 2, y / 2, -z / 2, x, y);
		tessellator.addVertexWithUV(x / 2, -y / 2, -z / 2, x, 0);

		tessellator.setNormal(0, 0, 1);
		tessellator.addVertexWithUV(-x / 2, -y / 2, z / 2, 0, 0);
		tessellator.addVertexWithUV(x / 2, -y / 2, z / 2, x, 0);
		tessellator.addVertexWithUV(x / 2, y / 2, z / 2, x, y);
		tessellator.addVertexWithUV(-x / 2, y / 2, z / 2, 0, y);

		tessellator.setNormal(-1, 0, 0);
		tessellator.addVertexWithUV(-x / 2, -y / 2, -z / 2, 0.5, 0);
		tessellator.addVertexWithUV(-x / 2, -y / 2, z / 2, 0.5, z);
		tessellator.addVertexWithUV(-x / 2, y / 2, z / 2, y + 0.5, z);
		tessellator.addVertexWithUV(-x / 2, y / 2, -z / 2, y + 0.5, 0);

		tessellator.setNormal(1, 0, 0);
		tessellator.addVertexWithUV(x / 2, -y / 2, -z / 2, 0.5, 0);
		tessellator.addVertexWithUV(x / 2, y / 2, -z / 2, y + 0.5, 0);
		tessellator.addVertexWithUV(x / 2, y / 2, z / 2, y + 0.5, z);
		tessellator.addVertexWithUV(x / 2, -y / 2, z / 2, 0.5, z);

		tessellator.setNormal(0, 1, 0);
		tessellator.addVertexWithUV(-x / 2, y / 2, -z / 2, 0.5, 0);
		tessellator.addVertexWithUV(-x / 2, y / 2, z / 2, 0.5, z);
		tessellator.addVertexWithUV(x / 2, y / 2, z / 2, x + 0.5, z);
		tessellator.addVertexWithUV(x / 2, y / 2, -z / 2, x + 0.5, 0);

		tessellator.setNormal(0, -1, 0);
		tessellator.addVertexWithUV(-x / 2, -y / 2, -z / 2, 0.5, 0);
		tessellator.addVertexWithUV(x / 2, -y / 2, -z / 2, x + 0.5, 0);
		tessellator.addVertexWithUV(x / 2, -y / 2, z / 2, x + 0.5, z);
		tessellator.addVertexWithUV(-x / 2, -y / 2, z / 2, 0.5, z);

		tessellator.draw();

		//GL11.glDepthMask(true);
		//GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

	}

}
