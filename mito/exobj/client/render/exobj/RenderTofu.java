package com.mito.exobj.client.render.exobj;

import java.util.List;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;

import com.mito.exobj.Main;
import com.mito.exobj.BraceBase.BB_Render;
import com.mito.exobj.BraceBase.BB_RenderHandler;
import com.mito.exobj.BraceBase.BB_ResisteredList;
import com.mito.exobj.BraceBase.ExtraObject;
import com.mito.exobj.BraceBase.Brace.Tofu;
import com.mito.exobj.client.BB_HighLightHandler;
import com.mito.exobj.client.render.CreateVertexBufferObject;
import com.mito.exobj.client.render.VBOHandler;
import com.mito.exobj.client.render.model.BB_Polygon;
import com.mito.exobj.client.render.model.Triangle;
import com.mito.exobj.client.render.model.Triangle.EnumFace;
import com.mito.exobj.client.render.model.Vertex;
import com.mito.exobj.main.mitoClientProxy;
import com.mito.exobj.utilities.MyUtil;

import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.IIcon;
import net.minecraft.util.Vec3;

public class RenderTofu extends BB_Render {

	@Override
	public void drawHighLight(ExtraObject base, float partialticks) {
		float size = 2.0F;
		Tofu brace = (Tofu) base;

		BB_HighLightHandler data = ((mitoClientProxy) Main.proxy).bh;
		if (data.key == null || !data.key.equals(base)) {
			data.buffer.delete();
			CreateVertexBufferObject c = CreateVertexBufferObject.INSTANCE;
			c.beginRegist(GL15.GL_STATIC_DRAW, GL11.GL_TRIANGLES);
			c.setColor(1.0F, 1.0F, 1.0F, 1.0F);
			BB_Render render = BB_ResisteredList.getBraceBaseRender(brace);
			render.updateRender(c, brace);
			data.key = base;
			VBOHandler vbo = c.end();
			data.buffer.add(vbo);
		}
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		BB_RenderHandler.enableClient();
		data.buffer.draw(GL11.GL_LINE_LOOP);
		BB_RenderHandler.disableClient();
	}

	public void doRender(ExtraObject base, float x, float y, float z, float partialTickTime) {
	}

	public void updateRender(CreateVertexBufferObject c, ExtraObject base) {
		updateRender(c, base, 0, 0, 0);
	}

	public void updateRender(CreateVertexBufferObject c, ExtraObject base, double x, double y, double z) {
		int i = base.getBrightnessForRender(0, x, y, z);

		Tofu brace = (Tofu) base;
		c.setBrightness(i);
		c.pushMatrix();
		this.drawCube(c, brace);
		c.popMatrix();

	}

	public void drawCube(CreateVertexBufferObject c, Tofu tofu) {
		AxisAlignedBB aabb = tofu.aabb;
		Vec3 norm = Vec3.createVectorHelper(0, 0, 1);
		Vertex v1 = new Vertex(aabb.maxX, aabb.maxY, aabb.maxZ, aabb.maxX, aabb.maxY, norm);
		Vertex v2 = new Vertex(aabb.minX, aabb.maxY, aabb.maxZ, aabb.minX, aabb.maxY, norm);
		Vertex v3 = new Vertex(aabb.minX, aabb.minY, aabb.maxZ, aabb.minX, aabb.minY, norm);
		Vertex v4 = new Vertex(aabb.maxX, aabb.minY, aabb.maxZ, aabb.maxX, aabb.minY, norm);
		this.drawWall(c, tofu.texture.getIcon(3, tofu.color), v1, v2, v3, v4);
		
		norm = Vec3.createVectorHelper(0, 0, -1);
		v1 = new Vertex(aabb.maxX, aabb.maxY, aabb.minZ, aabb.maxX, aabb.maxY, norm);
		v2 = new Vertex(aabb.maxX, aabb.minY, aabb.minZ, aabb.maxX, aabb.minY, norm);
		v3 = new Vertex(aabb.minX, aabb.minY, aabb.minZ, aabb.minX, aabb.minY, norm);
		v4 = new Vertex(aabb.minX, aabb.maxY, aabb.minZ, aabb.minX, aabb.maxY, norm);
		this.drawWall(c, tofu.texture.getIcon(2, tofu.color), v1, v2, v3, v4);
		
		norm = Vec3.createVectorHelper(0, 1, 0);
		v1 = new Vertex(aabb.maxX, aabb.maxY, aabb.maxZ, aabb.maxZ, aabb.maxX, norm);
		v2 = new Vertex(aabb.maxX, aabb.maxY, aabb.minZ, aabb.minZ, aabb.maxX, norm);
		v3 = new Vertex(aabb.minX, aabb.maxY, aabb.minZ, aabb.minZ, aabb.minX, norm);
		v4 = new Vertex(aabb.minX, aabb.maxY, aabb.maxZ, aabb.maxZ, aabb.minX, norm);
		this.drawWall(c, tofu.texture.getIcon(1, tofu.color), v1, v2, v3, v4);

		norm = Vec3.createVectorHelper(0, -1, 0);
		v1 = new Vertex(aabb.maxX, aabb.minY, aabb.maxZ, aabb.maxZ, aabb.maxX, norm);
		v2 = new Vertex(aabb.minX, aabb.minY, aabb.maxZ, aabb.maxZ, aabb.minX, norm);
		v3 = new Vertex(aabb.minX, aabb.minY, aabb.minZ, aabb.minZ, aabb.minX, norm);
		v4 = new Vertex(aabb.maxX, aabb.minY, aabb.minZ, aabb.minZ, aabb.maxX, norm);
		this.drawWall(c, tofu.texture.getIcon(0, tofu.color), v1, v2, v3, v4);

		norm = Vec3.createVectorHelper(1, 0, 0);
		v1 = new Vertex(aabb.maxX, aabb.maxY, aabb.maxZ, aabb.maxY, aabb.maxZ, norm);
		v2 = new Vertex(aabb.maxX, aabb.minY, aabb.maxZ, aabb.minY, aabb.maxZ, norm);
		v3 = new Vertex(aabb.maxX, aabb.minY, aabb.minZ, aabb.minY, aabb.minZ, norm);
		v4 = new Vertex(aabb.maxX, aabb.maxY, aabb.minZ, aabb.maxY, aabb.minZ, norm);
		this.drawWall(c, tofu.texture.getIcon(5, tofu.color), v1, v2, v3, v4);

		norm = Vec3.createVectorHelper(-1, 0, 0);
		v1 = new Vertex(aabb.minX, aabb.maxY, aabb.maxZ, aabb.maxY, aabb.maxZ, norm);
		v2 = new Vertex(aabb.minX, aabb.maxY, aabb.minZ, aabb.maxY, aabb.minZ, norm);
		v3 = new Vertex(aabb.minX, aabb.minY, aabb.minZ, aabb.minY, aabb.minZ, norm);
		v4 = new Vertex(aabb.minX, aabb.minY, aabb.maxZ, aabb.minY, aabb.maxZ, norm);
		this.drawWall(c, tofu.texture.getIcon(4, tofu.color), v1, v2, v3, v4);
	}
	
	public void drawWall(CreateVertexBufferObject c, IIcon iicon, Vertex v1, Vertex v2, Vertex v3, Vertex v4){
		BB_Polygon square = new BB_Polygon(v1, v2, v3, v4);
		List<Triangle> arrayTriangle = MyUtil.decomposeTexture(square);
		for (Triangle triangle : arrayTriangle) {
			triangle.drawIcon(c, iicon, EnumFace.OBVERSE);
		}
	}
	
	public boolean isVbo(ExtraObject base) {
		return base.isStatic;
	}
}
