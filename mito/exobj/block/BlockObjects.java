package com.mito.exobj.block;

import java.util.List;

import com.mito.exobj.Main;
import com.mito.exobj.BraceBase.Brace.GroupObject;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.IIcon;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

public class BlockObjects extends BlockContainer {

	private IIcon TopIcon;
	private IIcon SideIcon;

	public BlockObjects() {
		super(Material.cloth);
		this.setStepSound(Block.soundTypeCloth);
		this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 0.0625F, 1.0F);
		this.setBlockTextureName("iron_ore");
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
		this.TopIcon = par1IconRegister.registerIcon("exobj:chestframe");
		this.SideIcon = par1IconRegister.registerIcon("exobj:chestframe");
	}

	@SideOnly(Side.CLIENT)
	public IIcon getIcon(int par1, int par2) {
		if (par1 == 0 || par1 == 1) {
			return TopIcon;
		} else {
			return SideIcon;
		}
	}

	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int face, float hitX, float hitY, float hitZ) {
		TileEntity tile = world.getTileEntity(x, y, z);
		if (tile != null) {
			GroupObject go = ((TileObjects) tile).name;
			if (go != null) {
			} else {
			}
		} else {
		}
		return true;
	}

	public int getRenderType() {
		return Main.RenderType_Objects;
	}

	public void breakBlock(World world, int x, int y, int z, Block block, int dir) {
		TileEntity tile = world.getTileEntity(x, y, z);
		if (tile != null && tile instanceof TileObjects) {
			TileObjects t = (TileObjects) tile;
			t.breakBrace();
		}
		world.removeTileEntity(x, y, z);
	}

	public boolean isOpaqueCube() {
		return false;
	}

	@Override
	public void addCollisionBoxesToList(World world, int x, int y, int z, AxisAlignedBB aabb, List list, Entity entity) {
	}

}
