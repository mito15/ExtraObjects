package com.mito.exobj.client.render.model;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.opengl.GL11;

import com.mito.exobj.BraceBase.CreateVertexBufferObject;
import com.mito.exobj.BraceBase.Brace.Brace;
import com.mito.exobj.client.render.exorender.BezierCurve;
import com.mito.exobj.utilities.Line;
import com.mito.exobj.utilities.MitoMath;
import com.mito.exobj.utilities.MyUtil;

import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.IIcon;
import net.minecraft.util.Vec3;

public class BB_Polygon implements IDrawBrace {

	public List<Vertex> line = new ArrayList();

	public BB_Polygon() {
	}

	public BB_Polygon(Vertex... list) {
		for (int n = 0; n < list.length; n++) {
			this.line.add(list[n]);
		}
	}

	public BB_Polygon(double... list) {
		for (int n = 0; n < list.length / 3; n++) {
			Vertex v = new Vertex(list[(3 * n)], list[(3 * n + 1)], list[(3 * n + 2)], 0.0D, 0.0D);
			this.line.add(v);
		}
	}

	public int getSize(double size) {
		return this.line.size();
	}

	public Vec3 getVec3(int n, double size) {
		if ((n < 0) || (n >= getSize(size))) {
			n %= getSize(size);
			if (n < 0) {
				n += getSize(size);
			}
		}
		return MitoMath.vectorMul(((Vertex) this.line.get(n)).pos, size);
	}

	public Vertex getVertex(int n, double size) {
		if ((n < 0) || (n >= getSize(size))) {
			n %= getSize(size);
			if (n < 0) {
				n += getSize(size);
			}
		}
		return ((Vertex) this.line.get(n)).resize(size);
	}

	private Vec3 getNorm1(Vec3 v1, Vec3 v2) {
		return Vec3.createVectorHelper(v2.yCoord - v1.yCoord, v1.xCoord - v2.xCoord, 0.0D).normalize();
	}

	private Vec3 getNorm2(Vec3 v1, Vec3 v2) {
		return Vec3.createVectorHelper(v2.yCoord - v1.yCoord, v1.xCoord - v2.xCoord, 0.0D).normalize();
	}

	/*public void drawBraceSquare(CreateVertexBufferObject c, Brace brace) {
		double uvOffset = (brace.pos.xCoord + brace.pos.yCoord + brace.pos.zCoord) % 1.0D;
		if (brace.line instanceof BezierCurve) {
	
			double size = brace.size;
			int acc = 20;
			BezierCurve bc = (BezierCurve) brace.line;
			double roll = brace.getRoll();
			double v = 0.0D;
			Vec3 setNormal = bc.getNormal(0.0D);
			for (int n = 0; n < acc; n++) {
				double t = (double) n / (double) acc;
				double t1 = (double) (n + 1) / (double) acc;
				Vec3 s = MitoMath.vectorSub(bc.getPoint(t), brace.pos);
				Vec3 e = MitoMath.vectorSub(bc.getPoint(t1), brace.pos);
				Vec3 sn = bc.getNormal(t);
				Vec3 en = bc.getNormal(t1);
				double usum = 0.0D;
				double vOffset = MitoMath.subAbs(s, e);
				double ps = MitoMath.getPitch(sn);
				double pe = MitoMath.getPitch(en);
				double ys = MitoMath.getYaw(sn);
				double ye = MitoMath.getYaw(en);
				double rollOffset1 = 0.0D;
				double rollOffset2 = 0.0D;
				for (int n1 = 0; n1 < getSize(size); n1++) {
					Vec3 v1 = getVec3(n1 - 1, size);
					Vec3 v2 = getVec3(n1, size);
					Vec3 vs1 = MitoMath.rot(v1, roll + rollOffset1, ps, ys);
					Vec3 vs2 = MitoMath.rot(v2, roll + rollOffset1, ps, ys);
					Vec3 ve1 = MitoMath.rot(v1, roll + rollOffset2, pe, ye);
					Vec3 ve2 = MitoMath.rot(v2, roll + rollOffset2, pe, ye);
					Vec3 norm = MitoMath.unitVector(Vec3.createVectorHelper(v2.yCoord - v1.yCoord, v1.xCoord - v2.xCoord, 0.0D));
					Vec3 norm1 = MitoMath.rot(norm, roll + rollOffset1, ps, ys);
					Vec3 norm2 = MitoMath.rot(norm, roll + rollOffset2, pe, ye);
					double uOffset = MitoMath.subAbs(v1, v2);
					double vOffset2 = v1.zCoord - v2.zCoord;
					c.setNormal(norm1);
					c.registVertexWithUV(MitoMath.vectorSum(new Vec3[] { vs1, s }), uvOffset + v, uvOffset + usum);
					c.registVertexWithUV(MitoMath.vectorSum(new Vec3[] { vs2, s }), uvOffset + v + vOffset2, uvOffset + uOffset + usum);
					c.setNormal(norm2);
					c.registVertexWithUV(MitoMath.vectorSum(new Vec3[] { ve2, e }), uvOffset + v + vOffset + vOffset2, uvOffset + uOffset + usum);
					c.registVertexWithUV(MitoMath.vectorSum(new Vec3[] { ve1, e }), uvOffset + v + vOffset, uvOffset + usum);
					usum += uOffset;
				}
				v += vOffset;
			}
		} else if (brace.line instanceof Line) {
			Line line = (Line) brace.line;
			double size = brace.size;
			double l = MitoMath.subAbs(line.start, line.end);
			double roll = brace.getRoll();
			double yaw = MitoMath.getYaw(line.start, line.end);
			double pitch = MitoMath.getPitch(line.start, line.end);
			c.rotate(roll, pitch, yaw);
			double usum = 0.0D;
			for (int n1 = 0; n1 < getSize(size); n1++) {
				Vec3 v1 = getVec3(n1 - 1, size);
				Vec3 v2 = getVec3(n1, size);
				Vec3 norm1 = this.getNorm1(v1, v2);//MitoMath.unitVector(Vec3.createVectorHelper((v2.yCoord - v1.yCoord), (v1.xCoord - v2.xCoord), 0));
				Vec3 norm2 = this.getNorm2(v1, v2);//MitoMath.unitVector(Vec3.createVectorHelper((v2.yCoord - v1.yCoord), (v1.xCoord - v2.xCoord), 0));
				double uOffset = MitoMath.subAbs(v1, v2);
				double vOffset = v1.zCoord - v2.zCoord;
				c.setNormal(norm1);
				c.registVertexWithUV(v1.xCoord, v1.yCoord, v1.zCoord + l, uvOffset, uvOffset + usum);
				c.registVertexWithUV(v1, uvOffset + l, uvOffset + usum);
				c.setNormal(norm2);
				c.registVertexWithUV(v2, uvOffset + vOffset + l, uvOffset + uOffset + usum);
				c.registVertexWithUV(v2.xCoord, v2.yCoord, v2.zCoord + l, uvOffset + vOffset, uvOffset + uOffset + usum);
				usum += uOffset;
			}
		}
	}*/

	public void drawBraceTriangle(CreateVertexBufferObject c, Brace brace) {
		if (brace.line instanceof BezierCurve) {
			double size = brace.size;
			if (getSize(size) < 3) {
				return;
			}
			double part = 0.05D;
			BezierCurve bc = (BezierCurve) brace.line;
			double roll = brace.getRoll();

			Vec3 norm1 = bc.getTangent(0.0D);
			Vec3 norm2 = MitoMath.vectorMul(bc.getTangent(1.0D), -1.0D);
			double yaw1 = MitoMath.getYaw(norm1);
			double pitch1 = MitoMath.getPitch(norm1);
			double yaw2 = MitoMath.getYaw(norm2);
			double pitch2 = MitoMath.getPitch(norm2);

			Vec3 offset = MitoMath.sub_vector(bc.getPoint(1), bc.getPoint(0));
			drawPlane(c, Vec3.createVectorHelper(0.0D, 0.0D, 0.0D), norm1, roll, pitch1, yaw1, size);
			drawPlane(c, offset, norm2, -roll, pitch2, yaw2, size);

			List<Vertex> vers = new ArrayList();
			for (int n1 = 0; n1 < getSize(size); n1++) {
				vers.add(getVertex(n1, size));
			}
			List<Triangle> ts = MyUtil.decomposePolygon(vers);
			for (int n1 = 0; n1 < ts.size(); n1++) {
				ts.get(n1).drawReverse(c, Vec3.createVectorHelper(0.0D, 0.0D, 0.0D), roll, pitch1, yaw1);
				ts.get(n1).draw(c, offset, roll, pitch2, yaw2);
			}
		} else if (brace.line instanceof Line) {
			Line line = (Line) brace.line;
			double size = brace.size;
			if (getSize(size) < 3) {
				return;
			}
			double l = MitoMath.subAbs(line.start, line.end);
			double roll = brace.getRoll();
			double yaw = MitoMath.getYaw(line.start, line.end);
			double pitch = MitoMath.getPitch(line.start, line.end);
			c.rpyRotate(roll, pitch, yaw);
			Vertex v1 = getVertex(0, size);
			List<Vertex> vers = new ArrayList();
			for (int n1 = 0; n1 < getSize(size); n1++) {
				vers.add(getVertex(n1, size));
			}
			List<Triangle> ts = MyUtil.decomposePolygon(vers);
			for (int n1 = 0; n1 < ts.size(); n1++) {
				ts.get(n1).draw(c, l);
				ts.get(n1).drawReverse(c, 0.0D);
			}
		}
	}

	@Override
	public void drawBraceTessellator(Brace brace, float partialTickTime) {
		GL11.glPushMatrix();
		if (brace.line instanceof Line) {
			double uvOffset = (brace.pos.xCoord + brace.pos.yCoord + brace.pos.zCoord) % 1.0D;
			Tessellator t = Tessellator.instance;
			Line line = (Line) brace.line;
			double size = brace.size;
			IIcon iicon = brace.texture.getIcon(0, brace.color);
			double l = MitoMath.subAbs(line.start, line.end);
			double roll = brace.getRoll();
			double yaw = MitoMath.getYaw(line.start, line.end);
			double pitch = MitoMath.getPitch(line.start, line.end);
			GL11.glRotated(roll, 0, 0, 1);
			GL11.glRotated(pitch, 1, 0, 0);
			GL11.glRotated(yaw, 0, 1, 0);
			double usum = 0.0D;
			t.startDrawingQuads();
			for (int n = 0; n < l; n++) {
				for (int n1 = 0; n1 < getSize(size); n1++) {
					double vz = (double) n + 1.0;
					double tv = iicon.getMaxV();
					if (vz > l) {
						tv = iicon.getMinV() + (l - n) * (iicon.getMaxV() - iicon.getMinV());
						vz = l;
					}
					Vec3 v1 = getVec3(n1 - 1, size);
					Vec3 v2 = getVec3(n1, size);
					Vec3 norm1 = this.getNorm1(v1, v2);//MitoMath.unitVector(Vec3.createVectorHelper((v2.yCoord - v1.yCoord), (v1.xCoord - v2.xCoord), 0));
					Vec3 norm2 = this.getNorm2(v1, v2);//MitoMath.unitVector(Vec3.createVectorHelper((v2.yCoord - v1.yCoord), (v1.xCoord - v2.xCoord), 0));
					double uOffset = MitoMath.subAbs(v1, v2);
					double tu = iicon.getMinU() + uOffset * (iicon.getMaxU() - iicon.getMinU());
					double vOffset = v1.zCoord - v2.zCoord;
					t.setNormal((float) norm1.xCoord, (float) norm1.yCoord, (float) norm1.zCoord);
					t.addVertexWithUV(v1.xCoord, v1.yCoord, v1.zCoord + vz, tu, tv);
					t.addVertexWithUV(v1.xCoord, v1.yCoord, v1.zCoord + n, tu, iicon.getMinV());
					t.setNormal((float) norm2.xCoord, (float) norm2.yCoord, (float) norm2.zCoord);
					t.addVertexWithUV(v2.xCoord, v2.yCoord, v2.zCoord + n, iicon.getMinU(), iicon.getMinV());
					t.addVertexWithUV(v2.xCoord, v2.yCoord, v2.zCoord + vz, iicon.getMinU(), tv);
					usum += uOffset;
				}
			}
			t.draw();
		
			if (getSize(size) < 3) {
				return;
			}
			Vertex v1 = getVertex(0, size);
			List<Vertex> vers = new ArrayList();
			for (int n1 = 0; n1 < getSize(size); n1++) {
				vers.add(getVertex(n1, size));
			}
			t.startDrawing(4);
			List<Triangle> ts = MyUtil.decomposePolygon(vers);
			for (int n1 = 0; n1 < ts.size(); n1++) {
				ts.get(n1).draw(t, l);
				ts.get(n1).drawReverse(t, 0.0D);
			}
			t.draw();
		}
		GL11.glPopMatrix();
	}

	public void drawPlane(CreateVertexBufferObject c, Vec3 offset, Vec3 norm, double roll, double pitch, double yaw, double size) {
		Vertex v1 = getVertex(0, size);
		Vertex rv1 = v1.rot(roll, pitch, yaw);
		for (int n1 = 0; n1 < getSize(size) - 2; n1++) {
			Vertex v2 = getVertex(n1 + 1, size);
			Vertex v3 = getVertex(n1 + 2, size);
			Vertex rv2 = v2.rot(roll, pitch, yaw);
			Vertex rv3 = v3.rot(roll, pitch, yaw);
			c.setNormal(MitoMath.vectorMul(norm, -1.0D));
			c.registVertexWithUV(rv3.addVector(offset));
			c.registVertexWithUV(rv2.addVector(offset));
			c.registVertexWithUV(rv1.addVector(offset));
		}
	}

	public void drawPlane(CreateVertexBufferObject c, Vec3 offset, Vec3 norm, double roll, double size) {
		double yaw = MitoMath.getYaw(norm);
		double pitch = MitoMath.getPitch(norm);
		drawPlane(c, offset, norm, roll, pitch, yaw, size);
	}

	public void drawPlane(CreateVertexBufferObject c, Vec3 offset, double roll, double size) {
		Vertex v1 = getVertex(0, size);
		Vertex rv1 = v1.rot(roll, 0.0D, 0.0D);
		for (int n1 = 0; n1 < getSize(size) - 2; n1++) {
			Vertex v2 = getVertex(n1 + 1, size);
			Vertex v3 = getVertex(n1 + 2, size);
			Vertex rv2 = v2.rot(roll, 0.0D, 0.0D);
			Vertex rv3 = v3.rot(roll, 0.0D, 0.0D);
			c.setNormal(MitoMath.getNormal(rv1, rv2, rv3));
			c.registVertexWithUV(rv1.addVector(offset));
			c.registVertexWithUV(rv2.addVector(offset));
			c.registVertexWithUV(rv3.addVector(offset));
		}
	}

	public void drawTri(CreateVertexBufferObject c, Vec3 offset, double roll, double pitch, double yaw, double size) {
		Vertex v1 = getVertex(0, size);
		Vertex rv1 = v1.rot(roll, pitch, yaw);
		for (int n1 = 0; n1 < getSize(size) - 2; n1++) {
			Vertex v2 = getVertex(n1 + 1, size);
			Vertex v3 = getVertex(n1 + 2, size);
			Vertex rv2 = v2.rot(roll, pitch, yaw);
			Vertex rv3 = v3.rot(roll, pitch, yaw);
			c.setNormal(MitoMath.getNormal(rv1, rv2, rv3));
			c.registVertexWithUV(rv1.addVector(offset));
			c.registVertexWithUV(rv2.addVector(offset));
			c.registVertexWithUV(rv3.addVector(offset));
		}
	}

	public void renderAt(Vec3 offset, double roll, double pitch, double yaw, double size, float partialTickTime) {
		Vertex v1 = getVertex(0, size);
		Vertex rv1 = v1.rot(roll, pitch, yaw);
		Tessellator t = Tessellator.instance;
		for (int n1 = 0; n1 < getSize(size) - 2; n1++) {
			Vertex v2 = getVertex(n1 + 1, size);
			Vertex v3 = getVertex(n1 + 2, size);
			Vertex rv2 = v2.rot(roll, pitch, yaw);
			Vertex rv3 = v3.rot(roll, pitch, yaw);
			Vec3 norm = MitoMath.getNormal(rv1, rv2, rv3);
			t.startDrawing(GL11.GL_TRIANGLES);
			t.setNormal((float) norm.xCoord, (float) norm.yCoord, (float) norm.zCoord);
			tessVertex(t, rv3.addVector(offset));
			tessVertex(t, rv2.addVector(offset));
			tessVertex(t, rv1.addVector(offset));
			t.draw();
		}
	}

	public void tessVertex(Tessellator t, Vertex v) {
		t.addVertexWithUV(v.pos.xCoord, v.pos.yCoord, v.pos.zCoord, v.textureU, v.textureV);
	}

	public boolean hasNull() {
		if (this.line.size() == 0) {
			return true;
		}
		for (int n = 0; n < this.line.size(); n++) {
			if (this.line.get(n) == null) {
				return true;
			}
		}
		return false;
	}

	@Override
	public void drawBracewithVBO(CreateVertexBufferObject c, Brace brace) {
		IIcon iicon = brace.getIIcon(0);
		double size = brace.size;
		double roll = brace.getRoll();
		c.rpyRotate(roll, 0, 0);
		List<Triangle> ts0 = getTriangles(size);
		List<Triangle> ts = MyUtil.decomposeTexture(ts0);
		if (ts != null) {
			c.pushMatrix();
			c.translate(brace.line.getPoint(0.0));
			Vec3 tangen = brace.line.getTangent(0.0);
			Mat4 romat = MyUtil.getRotationMatrix(tangen);
			c.transform(romat);
			double mu = iicon.getMinU();
			double mv = iicon.getMinV();
			double du = iicon.getMaxU() - iicon.getMinU();
			double dv = iicon.getMaxV() - iicon.getMinV();
			for (int n1 = 0; n1 < ts.size(); n1++) {
				c.setNormal(MitoMath.vectorMul(ts.get(n1).norm, -1));
				c.registVertexWithUV(ts.get(n1).vertexs[2].pos, mu, mv + dv);
				c.registVertexWithUV(ts.get(n1).vertexs[1].pos, mu, mv);
				c.registVertexWithUV(ts.get(n1).vertexs[0].pos, mu + du, mv);
			}
			c.popMatrix();

			c.pushMatrix();
			c.translate(brace.line.getPoint(1.0));
			tangen = brace.line.getTangent(1.0);
			romat = MyUtil.getRotationMatrix(tangen);
			c.transform(romat);
			for (int n1 = 0; n1 < ts.size(); n1++) {
				c.setNormal(ts.get(n1).norm);
				c.registVertexWithUV(ts.get(n1).vertexs[2].pos, mu, mv + dv);
				c.registVertexWithUV(ts.get(n1).vertexs[0].pos, mu + du, mv);
				c.registVertexWithUV(ts.get(n1).vertexs[1].pos, mu, mv);
			}
			c.popMatrix();
		}

		double uvOffset = (brace.pos.xCoord + brace.pos.yCoord + brace.pos.zCoord) % 1.0D;
		c.translate(brace.pos);
		if (brace.line instanceof BezierCurve) {

			int acc = 20;
			BezierCurve bc = (BezierCurve) brace.line;
			double v = 0.0D;
			Vec3 setNormal = bc.getTangent(0.0D);
			for (int n = 0; n < acc; n++) {
				double t = (double) n / (double) acc;
				double t1 = (double) (n + 1) / (double) acc;
				Vec3 s = MitoMath.sub_vector(bc.getPoint(t), brace.pos);
				Vec3 e = MitoMath.sub_vector(bc.getPoint(t1), brace.pos);
				Vec3 sn = bc.getTangent(t);
				Vec3 en = bc.getTangent(t1);
				double usum = 0.0D;
				double vOffset = MitoMath.subAbs(s, e);
				double ps = MitoMath.getPitch(sn);
				double pe = MitoMath.getPitch(en);
				double ys = MitoMath.getYaw(sn);
				double ye = MitoMath.getYaw(en);
				double rollOffset1 = 0.0D;
				double rollOffset2 = 0.0D;
				for (int n1 = 0; n1 < getSize(size); n1++) {
					Vec3 v1 = getVec3(n1 - 1, size);
					Vec3 v2 = getVec3(n1, size);
					Vec3 vs1 = MitoMath.rot(v1, roll + rollOffset1, ps, ys);
					Vec3 vs2 = MitoMath.rot(v2, roll + rollOffset1, ps, ys);
					Vec3 ve1 = MitoMath.rot(v1, roll + rollOffset2, pe, ye);
					Vec3 ve2 = MitoMath.rot(v2, roll + rollOffset2, pe, ye);
					Vec3 norm = MitoMath.unitVector(Vec3.createVectorHelper(v2.yCoord - v1.yCoord, v1.xCoord - v2.xCoord, 0.0D));
					Vec3 norm1 = MitoMath.rot(norm, roll + rollOffset1, ps, ys);
					Vec3 norm2 = MitoMath.rot(norm, roll + rollOffset2, pe, ye);
					double uOffset = MitoMath.subAbs(v1, v2);
					double vOffset2 = v1.zCoord - v2.zCoord;
					c.setNormal(norm1);
					c.registVertexWithUV(MitoMath.vectorSum(new Vec3[] { vs1, s }), uvOffset + v, uvOffset + usum);
					c.registVertexWithUV(MitoMath.vectorSum(new Vec3[] { vs2, s }), uvOffset + v + vOffset2, uvOffset + uOffset + usum);
					c.setNormal(norm2);
					c.registVertexWithUV(MitoMath.vectorSum(new Vec3[] { ve2, e }), uvOffset + v + vOffset + vOffset2, uvOffset + uOffset + usum);
					c.registVertexWithUV(MitoMath.vectorSum(new Vec3[] { ve1, e }), uvOffset + v + vOffset, uvOffset + usum);
					usum += uOffset;
				}
				v += vOffset;
			}
		} else if (brace.line instanceof Line) {
			iicon = brace.getIIcon(1);
			Line line = (Line) brace.line;
			double l = MitoMath.subAbs(line.start, line.end);
			Vec3 v = line.getTangent(0).normalize();
			Mat4 mat = MyUtil.getRotationMatrix(v);
			c.transform(mat);
			c.rotate(roll, 0, 0, 1);
			double usum;
			for (int n = 0; n < l; n++) {
				int tex = 1;
				usum = 0.0D;
				for (int n1 = 0; n1 < getSize(size); n1++) {
					double vz = (double) n + 1.0;
					double tv = iicon.getMaxV();
					if (vz > l) {
						tv = iicon.getMinV() + (l - n) * (iicon.getMaxV() - iicon.getMinV());
						vz = l;
					}
					Vec3 v1 = getVec3(n1 - 1, size);
					Vec3 v2 = getVec3(n1, size);
					Vec3 norm1 = Vec3.createVectorHelper(v2.yCoord - v1.yCoord, v1.xCoord - v2.xCoord, 0.0D).normalize();
					Vec3 norm2 = getNorm2(v1, v2);
					double uOffset = MitoMath.subAbs(v1, v2);
					double vOffset = v1.zCoord - v2.zCoord;

					//for (int n2 = 0; (double)n2 <= uOffset; n2++) {
					double ou = iicon.getMinU() + usum * (iicon.getMaxU() - iicon.getMinU());
					if (usum + uOffset > 1) {
						Vec3 v3 = MitoMath.ratio_vector(v1, v2, (1 - usum) / uOffset);
						c.setNormal(norm1);
						c.registVertexWithUV(v1.xCoord, v1.yCoord, v1.zCoord + vz, ou, tv);
						c.registVertexWithUV(v1.xCoord, v1.yCoord, v1.zCoord + n, ou, iicon.getMinV());
						c.registVertexWithUV(v3.xCoord, v3.yCoord, v3.zCoord + n, iicon.getMaxU(), iicon.getMinV());
						c.setNormal(norm2);
						c.registVertexWithUV(v3.xCoord, v3.yCoord, v3.zCoord + n, iicon.getMaxU(), iicon.getMinV());
						c.registVertexWithUV(v3.xCoord, v3.yCoord, v3.zCoord + vz, iicon.getMaxU(), tv);
						c.registVertexWithUV(v1.xCoord, v1.yCoord, v1.zCoord + vz, ou, tv);
						ou = iicon.getMinU();
						v1 = v3;
						usum = usum - 1;
						tex++;
						if (tex == 5) {
							tex = 1;
						}
						iicon = brace.getIIcon(tex);
					}
					double tu = iicon.getMinU() + (usum + uOffset) * (iicon.getMaxU() - iicon.getMinU());
					c.setNormal(norm1);
					c.registVertexWithUV(v1.xCoord, v1.yCoord, v1.zCoord + vz, ou, tv);
					c.registVertexWithUV(v1.xCoord, v1.yCoord, v1.zCoord + n, ou, iicon.getMinV());
					c.registVertexWithUV(v2.xCoord, v2.yCoord, v2.zCoord + n, tu, iicon.getMinV());
					c.setNormal(norm2);
					c.registVertexWithUV(v2.xCoord, v2.yCoord, v2.zCoord + n, tu, iicon.getMinV());
					c.registVertexWithUV(v2.xCoord, v2.yCoord, v2.zCoord + vz, tu, tv);
					c.registVertexWithUV(v1.xCoord, v1.yCoord, v1.zCoord + vz, ou, tv);
					usum += uOffset;
					//}
				}
			}
		}
	}

	private List<Triangle> getTriangles(double size) {
		List<Vertex> ret = new ArrayList();
		for (int n1 = 0; n1 < getSize(size); n1++) {
			ret.add(getVertex(n1, size).transTexUV());
		}
		return MyUtil.decomposePolygon(ret);
	}
}
