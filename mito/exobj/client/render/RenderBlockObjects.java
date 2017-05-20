package com.mito.exobj.client.render;

import com.mito.exobj.Main;

import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;

public class RenderBlockObjects implements ISimpleBlockRenderingHandler {

	private IIcon boxIIcon;

	@Override
	public void renderInventoryBlock(Block block, int metadata, int modelID,
			RenderBlocks renderer) {

	}

	@Override
	public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Block block, int modelId, RenderBlocks renderer) {
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

}
