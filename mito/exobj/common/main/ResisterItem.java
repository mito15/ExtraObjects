package com.mito.exobj.common.main;

import com.mito.exobj.common.Main;
import com.mito.exobj.common.MitoShapedRecipe;
import com.mito.exobj.common.MitoShapelessRecipe;
import com.mito.exobj.common.block.BlockObjects;
import com.mito.exobj.common.item.ItemBar;
import com.mito.exobj.common.item.ItemBender;
import com.mito.exobj.common.item.ItemBlockSetter;
import com.mito.exobj.common.item.ItemBrace;
import com.mito.exobj.common.item.ItemFakeBlock;
import com.mito.exobj.common.item.ItemLinearMotor;
import com.mito.exobj.common.item.ItemRedCable;
import com.mito.exobj.common.item.ItemRuler;
import com.mito.exobj.common.item.ItemSelectTool;

import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.RecipeSorter;

public class ResisterItem {

	public static Item ItemBrace;
	public static Item ItemBar;
	public static Item ItemBender;
	public static Item ItemWall;
	public static Item ItemRuler;
	public static Item ItemLinearMotor;
	public static Item ItemBlockSetter;
	public static Item ItemSelectTool;
	public static Item ItemFakeBlock;
	public static Item ItemRedCable;

	public static Block BlockObjects;

	static public void preinit() {
		ItemFakeBlock = new ItemFakeBlock().setUnlocalizedName("ItemFakeBlock").setCreativeTab(Main.tab);
		ItemSelectTool = new ItemSelectTool().setUnlocalizedName("ItemSelectTool").setCreativeTab(Main.tab);
		ItemBrace = new ItemBrace().setUnlocalizedName("ItemBrace").setCreativeTab(Main.tab);
		ItemBender = new ItemBender().setUnlocalizedName("ItemBender").setCreativeTab(Main.tab);
		ItemBar = new ItemBar().setUnlocalizedName("ItemBar");
		ItemBlockSetter = new ItemBlockSetter().setUnlocalizedName("ItemBlockSetter").setCreativeTab(Main.tab);
		//ItemWall = new ItemWall().setUnlocalizedName("ItemWall").setCreativeTab(mitomain.tab);
		ItemRuler = new ItemRuler().setUnlocalizedName("ItemRuler").setCreativeTab(Main.tab);
		ItemLinearMotor = new ItemLinearMotor().setUnlocalizedName("ItemLinearMotor").setCreativeTab(Main.tab);
		ItemRedCable = new ItemRedCable().setUnlocalizedName("ItemRedCable").setCreativeTab(Main.tab);
		
		BlockObjects = new BlockObjects().setBlockName("BlockObjects").setCreativeTab(Main.tab);

		GameRegistry.registerItem(ItemBar, "ItemBar");
		GameRegistry.registerItem(ItemBender, "ItemBender");
		//GameRegistry.registerItem(ItemWall, "ItemWall");
		GameRegistry.registerItem(ItemRuler, "ItemRuler");
		GameRegistry.registerItem(ItemBlockSetter, "ItemBlockSetter");
		GameRegistry.registerItem(ItemSelectTool, "ItemSelectTool");
		GameRegistry.registerItem(ItemBrace, "ItemBrace");
		GameRegistry.registerItem(ItemFakeBlock, "ItemFakeBlock");
		GameRegistry.registerItem(ItemLinearMotor, "ItemLinearMotor");
		GameRegistry.registerItem(ItemRedCable, "ItemRedCable");
		
		GameRegistry.registerBlock(BlockObjects, "BlockObjects");
	}
	
	static public void RegisterRecipe() {

		RecipeSorter.register("exobj;shapeless", MitoShapelessRecipe.class, RecipeSorter.Category.SHAPELESS, "after:minecraft:shapeless");

		GameRegistry.addRecipe(new MitoShapedRecipe());
		GameRegistry.addRecipe(new MitoShapelessRecipe());

		GameRegistry.addRecipe(new ItemStack(ResisterItem.ItemBrace, 4, 0),
				"#  ",
				" # ",
				"  #",
				'#', Blocks.iron_bars);

		GameRegistry.addRecipe(new ItemStack(ResisterItem.ItemBlockSetter),
				"GBG",
				"GCG",
				"GGG",
				'B', ResisterItem.ItemBrace,
				'C', Blocks.chest,
				'G', Items.gold_ingot);

		GameRegistry.addRecipe(new ItemStack(ResisterItem.ItemBender),
				" # ",
				" # ",
				"B B",
				'#', Items.iron_ingot,
				'B', ResisterItem.ItemBar);

		GameRegistry.addRecipe(new ItemStack(ResisterItem.ItemBar),
				"I  ",
				" I ",
				"  B",
				'B', Blocks.iron_bars,
				'I', Items.iron_ingot);
	}

}
