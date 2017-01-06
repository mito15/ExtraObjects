package com.mito.exobj.common.block;

import com.mito.exobj.BraceBase.Brace.GroupObject;
import com.mito.exobj.common.Main;
import com.mito.exobj.common.MyLogger;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

public class BlockObjects extends BlockContainer {

	public BlockObjects() {
		super(Material.cloth);
		this.setStepSound(Block.soundTypeCloth);
	}

	@Override
	public TileEntity createNewTileEntity(World world, int a) {
		TileObjects tile = new TileObjects();
		tile.name = new GroupObject(world, Vec3.createVectorHelper(this.maxX, this.maxY, this.maxZ));
		return tile;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister par1IconRegister) {
		this.blockIcon = Blocks.wool.getIcon(1, 2);
	}
	
	 public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int face, float hitX, float hitY, float hitZ){
		TileEntity tile = world.getTileEntity(x, y, z);
		if(tile != null){
			GroupObject go = ((TileObjects)tile).name;
			if(go != null){
				MyLogger.info("i have tile and group object " + go.list.size());
			} else {
				MyLogger.info("i have tile but ");
			}
		} else {
			MyLogger.info("i dont");
		}
		return true;
	}

	@Override
	public int getRenderType() {
		return Main.RenderType_Objects;
	}

}
