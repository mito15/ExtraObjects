package com.mito.exobj.client.render;

import org.lwjgl.opengl.GL11;

import com.mito.exobj.MyLogger;
import com.mito.exobj.item.ItemBrace;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraftforge.client.IItemRenderer;

public class RenderItemBrace implements IItemRenderer {

	private RenderItem renderer = new RenderItem();

	@Override
	public boolean handleRenderType(ItemStack item, ItemRenderType type) {
		return type == ItemRenderType.INVENTORY;
	}

	@Override
	public boolean shouldUseRenderHelper(ItemRenderType type, ItemStack item, ItemRendererHelper helper) {
		return false;
	}

	@Override
	public void renderItem(ItemRenderType type, ItemStack itemstack, Object... data) {

		ItemBrace item = (ItemBrace) itemstack.getItem();
		int isize = item.getSize(itemstack);
		int color = item.getColor(itemstack);

		Tessellator tess = Tessellator.instance;
		Minecraft.getMinecraft().renderEngine.bindTexture(TextureMap.locationBlocksTexture);

		IIcon iicon = item.getMaterial(itemstack).getIcon(0, item.getColor(itemstack));
		if(iicon == null){
			MyLogger.warn("missing iicon");
			return;
		}

		double size = 0.05 * (double) isize + 0.25;
		size = size >= 5.0 ? 5.0 : size;

		GL11.glPushMatrix();
		GL11.glTranslated(8, 8, 0);
		
		tess.startDrawingQuads();

		tess.setNormal(0, 0, 1);
		tess.addVertexWithUV(2, -2, 0, iicon.getMinU(), iicon.getMinV());
		tess.addVertexWithUV(6, -2, 0, iicon.getMaxU(), iicon.getMinV());
		tess.addVertexWithUV(6, -6, 0, iicon.getMaxU(), iicon.getMaxV());
		tess.addVertexWithUV(2, -6, 0, iicon.getMinU(), iicon.getMaxV());

		tess.setNormal(0, 0, 1);
		tess.addVertexWithUV(-2, 2, 0, iicon.getMinU(), iicon.getMinV());
		tess.addVertexWithUV(-6, 2, 0, iicon.getMaxU(), iicon.getMinV());
		tess.addVertexWithUV(-6, 6, 0, iicon.getMaxU(), iicon.getMaxV());
		tess.addVertexWithUV(-2, 6, 0, iicon.getMinU(), iicon.getMaxV());

		tess.draw();
		
		GL11.glRotated(-45, 0, 0, 1);

		tess.startDrawingQuads();

		tess.setNormal(0, 0, 1);
		tess.addVertexWithUV(6, size, 0, iicon.getMaxU(), iicon.getMaxV());
		tess.addVertexWithUV(6, -size, 0, iicon.getMaxU(), iicon.getMinV());
		tess.addVertexWithUV(-6, -size, 0, iicon.getMinU(), iicon.getMinV());
		tess.addVertexWithUV(-6, size, 0, iicon.getMinU(), iicon.getMaxV());

		tess.draw();

		GL11.glPopMatrix();

	}
}
