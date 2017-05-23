package com.mito.exobj.client.render.exobj;

import com.mito.exobj.BraceBase.BB_Render;
import com.mito.exobj.BraceBase.ExtraObject;

public class RenderGroupObject extends BB_Render {

	@Override
	public void drawHighLight(ExtraObject base, float partialticks) {
	}

	public void doRender(ExtraObject base, float x, float y, float z, float partialTickTime) {
		
		/*GroupObject go = (GroupObject)base;
		
		Tessellator t = Tessellator.instance;

		int i = base.getBrightnessForRender(0);
		int j = i % 65536;
		int k = i / 65536;
		t.startDrawing(3);

		for(int n = 0 ; n < go.list.size(); n++){
			Brace eo = (Brace) go.list.get(n);
			Vec3 a = eo.line.getStart();
			Vec3 b = eo.line.getEnd();
			t.addVertex(a.xCoord, a.yCoord, a.zCoord);
			t.addVertex(b.xCoord, b.yCoord, b.zCoord);
		}
		t.draw();*/

	/*BB_RenderHandler.enableClient();
	Brace brace = (Brace) base;
	Minecraft.getMinecraft().renderEngine.bindTexture(brace.texture.getResourceLocation(brace.color));
	GL11.glTranslated(brace.rand.xCoord, brace.rand.yCoord, brace.rand.zCoord);
	GL11.glTexParameterf(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL11.GL_REPEAT);
	if (base.buffer != null) {
		base.buffer.draw();
	}*/
	}

	public void updateRender(ExtraObject base, float partialticks) {

		//base.shouldUpdateRender = false;

		/*int i = base.getBrightnessForRender(partialticks);
		int j = i % 65536;
		int k = i / 65536;
		
		base.shouldUpdateRender = false;
		Brace brace = (Brace) base;
		if (brace.shape == null)
			return;
		base.buffer = new VBOList(new VBOHandler[0]);
		brace.shape.drawBrace(base.buffer, brace);
		CreateVertexBufferObject c = CreateVertexBufferObject.INSTANCE;
		c.beginRegist(35044, 7);
		c.setColor(1.0F, 1.0F, 1.0F, 1.0F);
		c.setBrightness(j, k);
		brace.shape.drawBraceSquare(c, brace);
		VBOHandler vbo1 = c.end();
		c.beginRegist(35044, 4);
		c.setColor(1.0F, 1.0F, 1.0F, 1.0F);
		c.setBrightness(j, k);
		brace.shape.drawBraceTriangle(c, brace);
		VBOHandler vbo2 = c.end();
		base.buffer.add(vbo1);
		base.buffer.add(vbo2);*/
	}
	
	public boolean isVbo() {
		return true;
	}
}
