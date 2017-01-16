package com.mito.exobj.client.render;

import com.mito.exobj.common.block.TileObjects;

import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;

public class TileObjectsRenderer extends TileEntitySpecialRenderer {

	@Override
	public void renderTileEntityAt(TileEntity tile, double x, double y, double z, float part) {
		if(tile instanceof TileObjects){
			TileObjects to = (TileObjects) tile;
			if(to.name != null){
				to.name.rendertest(x, y, z);
			}
		}
	}

}
