package com.mito.exobj.BraceBase.Brace;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;

public enum BB_EnumTexture {
	IRON("exobj:textures/blocks/brace_", ".png", "iron", Blocks.wool),
	STONE("textures/blocks/stone.png", "stone", Blocks.stone),
	COBBLESTONE("textures/blocks/cobblestone.png", "cobblestone", Blocks.cobblestone),
	WOOD_OAK("textures/blocks/planks_oak.png", "oak", Blocks.planks),
	WOOD_SPRUCE("textures/blocks/planks_spruce.png", "spruce", Blocks.planks),
	WOOD_BIRCH("textures/blocks/planks_birch.png", "birch", Blocks.planks),
	WOOD_JUNGLE("textures/blocks/planks_jungle.png", "jungle", Blocks.planks),
	WOOD_ACACIA("textures/blocks/planks_acacia.png", "acacia", Blocks.planks),
	WOOD_DARKOAK("textures/blocks/planks_big_oak.png", "darkoak", Blocks.planks),
	WOOL("textures/blocks/wool_colored_", ".png", "wool", Blocks.wool),
	HARDENED_CLAY("textures/blocks/hardened_clay_stained_", ".png", "hardened clay", Blocks.stained_hardened_clay),
	REDSTONE("textures/blocks/redstone_block.png", "redstone_block", Blocks.redstone_block),
	;

	private Block block;

	BB_EnumTexture(String location, String name, Block sound) {
		this.setBlock(sound);
	}

	BB_EnumTexture(String location, String extension, String name, Block sound) {
		this.setBlock(sound);
	}

	public Block getBlock() {
		return block;
	}

	public void setBlock(Block block) {
		this.block = block;
	}

}