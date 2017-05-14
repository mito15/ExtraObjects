package com.mito.exobj.BraceBase.Brace;

import java.util.List;

import com.mito.exobj.client.render.CreateVertexBufferObject;
import com.mito.exobj.client.render.exobj.IJoint;
import com.mito.exobj.client.render.model.BB_Polygon;
import com.mito.exobj.client.render.model.Triangle;
import com.mito.exobj.client.render.model.Vertex;
import com.mito.exobj.utilities.MyUtil;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class CubeJoint implements IJoint {

	@Override
	@SideOnly(Side.CLIENT)
	public void drawJointTessellator(Brace brace) {
		// TODO Auto-generated method stub

	}

	@Override
	@SideOnly(Side.CLIENT)
	public void drawJointwithVBO(CreateVertexBufferObject c, Brace brace) {
		double size = brace.size * 0.75;
		c.pushMatrix();
		c.translate(brace.line.getStart());
		drawCube(c, brace, size, size, size);
		c.popMatrix();

		c.pushMatrix();
		c.translate(brace.line.getEnd());
		drawCube(c, brace, size, size, size);
		c.popMatrix();
	}

	@SideOnly(Side.CLIENT)
	public void drawCube(CreateVertexBufferObject c, Brace brace, double x, double y, double z) {
		Vertex v1 = new Vertex(x, y, z);
		Vertex v2 = new Vertex(-x, y, z);
		Vertex v3 = new Vertex(-x, -y, z);
		Vertex v4 = new Vertex(x, -y, z);
		BB_Polygon square = new BB_Polygon(v1, v2, v3, v4);
		List<Triangle> arrayTriangle = MyUtil.decomposeTexture(square);
		for (int n = 0; n < 6; n++) {
			rotateCube(c, n);
			for (Triangle triangle : arrayTriangle) {
				triangle.drawIcon(c, brace.texture.getIcon(2, brace.color));
			}
		}
	}

	private void rotateCube(CreateVertexBufferObject c, int n) {
		switch (n) {
		case 0:
			break;
		case 1:
			c.rotate(90, 1, 0, 0);
			break;
		case 2:
			c.rotate(90, 1, 0, 0);
			break;
		case 3:
			c.rotate(90, 1, 0, 0);
			break;
		case 4:
			c.rotate(90, 0, 1, 0);
			break;
		case 5:
			c.rotate(180, 0, 1, 0);
			break;
		default:
			break;
		}
	}

}
