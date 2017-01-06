package com.mito.exobj.client;

import org.lwjgl.opengl.GL11;

import com.mito.exobj.common.Main;

import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.init.Blocks;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;

public class RenderBlockObjects implements ISimpleBlockRenderingHandler {
	
	private IIcon boxIIcon;

	@Override
	public void renderInventoryBlock(Block block, int metadata, int modelID,
			RenderBlocks renderer) {
 
		//アイコンはバニラの樫の木材
		this.boxIIcon = Blocks.planks.getBlockTextureFromSide(1);
 
		if (modelID == this.getRenderId())
		{
			//底
			renderInvCuboid(renderer, block,  0.0F/16.0F, 0.0F/16.0F, 0.0F/16.0F, 16.0F/16.0F, 2.0F/16.0F, 16.0F/16.0F,  this.boxIIcon);
 
			//壁面
			renderInvCuboid(renderer, block,  0.0F/16.0F, 2.0F/16.0F, 0.0F/16.0F, 16.0F/16.0F, 16.0F/16.0F, 1.0F/16.0F,  this.boxIIcon);
			renderInvCuboid(renderer, block,  0.0F/16.0F, 2.0F/16.0F, 15.0F/16.0F, 16.0F/16.0F, 16.0F/16.0F, 16.0F/16.0F,  this.boxIIcon);
			renderInvCuboid(renderer, block,  0.0F/16.0F, 2.0F/16.0F, 1.0F/16.0F, 1.0F/16.0F, 16.0F/16.0F, 15.0F/16.0F,  this.boxIIcon);
			renderInvCuboid(renderer, block,  15.0F/16.0F, 2.0F/16.0F, 1.0F/16.0F, 16.0F/16.0F, 16.0F/16.0F, 15.0F/16.0F,  this.boxIIcon);
		}
 
	}

	@Override
	public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Block block, int modelId, RenderBlocks renderer) {
		this.boxIIcon = Blocks.planks.getBlockTextureFromSide(1);
		 
		if (modelId == this.getRenderId())
		{
 
			renderer.setOverrideBlockTexture(this.boxIIcon);
			block.setBlockBounds(0.0F/16.0F, 0.0F/16.0F, 0.0F/16.0F, 16.0F/16.0F, 2.0F/16.0F, 16.0F/16.0F);
			renderer.setRenderBoundsFromBlock(block);
			renderer.renderStandardBlock(block, x, y, z);
 
			renderer.setOverrideBlockTexture(this.boxIIcon);
			block.setBlockBounds(0.0F/16.0F, 2.0F/16.0F, 0.0F/16.0F, 16.0F/16.0F, 16.0F/16.0F, 1.0F/16.0F);
			renderer.setRenderBoundsFromBlock(block);
			renderer.renderStandardBlock(block, x, y, z);
			renderer.setOverrideBlockTexture(this.boxIIcon);
			block.setBlockBounds(0.0F/16.0F, 2.0F/16.0F, 15.0F/16.0F, 16.0F/16.0F, 16.0F/16.0F, 16.0F/16.0F);
			renderer.setRenderBoundsFromBlock(block);
			renderer.renderStandardBlock(block, x, y, z);
			renderer.setOverrideBlockTexture(this.boxIIcon);
			block.setBlockBounds(0.0F/16.0F, 2.0F/16.0F, 1.0F/16.0F, 1.0F/16.0F, 16.0F/16.0F, 15.0F/16.0F);
			renderer.setRenderBoundsFromBlock(block);
			renderer.renderStandardBlock(block, x, y, z);
			renderer.setOverrideBlockTexture(this.boxIIcon);
			block.setBlockBounds(15.0F/16.0F, 2.0F/16.0F, 1.0F/16.0F, 16.0F/16.0F, 16.0F/16.0F, 15.0F/16.0F);
			renderer.setRenderBoundsFromBlock(block);
			renderer.renderStandardBlock(block, x, y, z);
 
 
			renderer.clearOverrideBlockTexture();
			block.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
			renderer.setRenderBoundsFromBlock(block);
			return true;
		}
		return false;
	}

	@Override
	public boolean shouldRender3DInInventory(int modelId) {
		// TODO 自動生成されたメソッド・スタブ
		return false;
	}

	@Override
	public int getRenderId() {
		return Main.RenderType_Objects;
	}
	
	private void renderInvCuboid(RenderBlocks renderer, Block block, float minX, float minY, float minZ, float maxX, float maxY, float maxZ, IIcon icon)
	{
		Tessellator tessellator = Tessellator.instance;
		block.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
		renderer.setRenderBoundsFromBlock(block);
		GL11.glTranslatef(-0.5F, -0.5F, -0.5F);
		block.setBlockBounds(minX, minY, minZ, maxX, maxY, maxZ);
		renderer.setRenderBoundsFromBlock(block);
		tessellator.startDrawingQuads();
		tessellator.setNormal(0.0F, -1F, 0.0F);
		renderer.renderFaceYNeg(block, 0.0D, 0.0D, 0.0D, icon);
		tessellator.draw();
		tessellator.startDrawingQuads();
		tessellator.setNormal(0.0F, 1.0F, 0.0F);
		renderer.renderFaceYPos(block, 0.0D, 0.0D, 0.0D, icon);
		tessellator.draw();
		tessellator.startDrawingQuads();
		tessellator.setNormal(0.0F, 0.0F, -1F);
		renderer.renderFaceXPos(block, 0.0D, 0.0D, 0.0D, icon);
		tessellator.draw();
		tessellator.startDrawingQuads();
		tessellator.setNormal(0.0F, 0.0F, 1.0F);
		renderer.renderFaceXNeg(block, 0.0D, 0.0D, 0.0D, icon);
		tessellator.draw();
		tessellator.startDrawingQuads();
		tessellator.setNormal(-1F, 0.0F, 0.0F);
		renderer.renderFaceZNeg(block, 0.0D, 0.0D, 0.0D, icon);
		tessellator.draw();
		tessellator.startDrawingQuads();
		tessellator.setNormal(1.0F, 0.0F, 0.0F);
		renderer.renderFaceZPos(block, 0.0D, 0.0D, 0.0D, icon);
		tessellator.draw();
		GL11.glTranslatef(0.5F, 0.5F, 0.5F);
		block.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
		renderer.setRenderBoundsFromBlock(block);
	}

}
