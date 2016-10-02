package com.mito.exobj.common;

import java.io.File;

import com.mito.exobj.BraceBase.BB_EventHandler;
import com.mito.exobj.BraceBase.Brace.Render.BB_TypeResister;
import com.mito.exobj.client.gui.GuiHandler;
import com.mito.exobj.common.entity.EntityWrapperBB;
import com.mito.exobj.common.item.ItemBar;
import com.mito.exobj.common.item.ItemBender;
import com.mito.exobj.common.item.ItemBlockSetter;
import com.mito.exobj.common.item.ItemBrace;
import com.mito.exobj.common.item.ItemFakeBlock;
import com.mito.exobj.common.item.ItemLinearMotor;
import com.mito.exobj.common.item.ItemRedCable;
import com.mito.exobj.common.item.ItemRuler;
import com.mito.exobj.common.item.ItemSelectTool;
import com.mito.exobj.network.PacketHandler;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.registry.EntityRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.FMLInjectionData;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.oredict.RecipeSorter;

@Mod(modid = Main.MODID, version = Main.VERSION/*, guiFactory = "com.mito.ruins.client.config.CaveGuiFactory"*/)
public class Main {

	public static final String MODID = "mito_extra_objects";
	public static final String VERSION = "1.3.5";
	public static boolean debug = false;

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

	public static final CreativeTabs tab = new CreativeTabMito("CreativeTabExObj");

	@Mod.Instance(Main.MODID)
	public static Main INSTANCE;

	@SidedProxy(clientSide = "com.mito.exobj.client.mitoClientProxy", serverSide = "com.mito.exobj.common.mitoCommonProxy")
	public static mitoCommonProxy proxy;
	//public static boolean vampSwitch = false;

	public static final int GUI_ID_FakeBlock = 0;
	public static final int GUI_ID_BBSetter = 1;
	public static final int GUI_ID_BBSelect = 2;

	@SideOnly(Side.CLIENT)
	public static int OscillatorRenderType;
	@SideOnly(Side.CLIENT)
	public static int PipeRenderType;
	public BB_EventHandler leh;

	//configuration
	public boolean vampmode = false;
	public File modelDir;
	public File shapesDir;
	public File GroupsDir;
	public File ObjsDir;

	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {

		File mcDir = (File) FMLInjectionData.data()[6];
		try {
			modelDir = new File(mcDir, "brace-models");
			modelDir.mkdir();
			shapesDir = new File(modelDir, "shapes");
			shapesDir.mkdir();
			GroupsDir = new File(modelDir, "groups");
			GroupsDir.mkdir();
			ObjsDir = new File(modelDir, "objects");
			ObjsDir.mkdir();
		} finally {
		}

		proxy.preInit();

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

		PacketHandler.init();

		File configDir = new File(mcDir, "config");
		File configFile = new File(configDir, "Braces-Oscillators.cfg");
		Configuration cfg = new Configuration(configFile);
		try {
			cfg.load();//コンフィグをロード
			debug = cfg.getBoolean("debug mode", "mode", false, "debug");
		} finally {
			cfg.save();//セーブ
		}
	}

	@EventHandler
	public void Init(FMLInitializationEvent e) {

		BB_TypeResister.loadModels();

		this.leh = new BB_EventHandler();
		MinecraftForge.EVENT_BUS.register(leh);
		FMLCommonHandler.instance().bus().register(leh);

		MinecraftForge.EVENT_BUS.register(this);
		FMLCommonHandler.instance().bus().register(this);

		proxy.init();

		//GUIの登録

		NetworkRegistry.INSTANCE.registerGuiHandler(this, new GuiHandler());

		//entityの登録

		EntityRegistry.registerModEntity(EntityWrapperBB.class, "Wrapper", 1, this, 512, 120, false);

		RegisterRecipe();

	}

	@EventHandler
	public void postInit(FMLPostInitializationEvent e) {
		if (debug) {
			LoadNGTLib.elase();
		}
	}

	public void RegisterRecipe() {

		RecipeSorter.register("exobj;shapeless", MitoShapelessRecipe.class, RecipeSorter.Category.SHAPELESS, "after:minecraft:shapeless");

		GameRegistry.addRecipe(new MitoShapedRecipe());
		GameRegistry.addRecipe(new MitoShapelessRecipe());

		GameRegistry.addRecipe(new ItemStack(this.ItemBrace, 4, 0),
				"#  ",
				" # ",
				"  #",
				'#', Blocks.iron_bars);

		GameRegistry.addRecipe(new ItemStack(this.ItemBlockSetter),
				"GBG",
				"GCG",
				"GGG",
				'B', this.ItemBrace,
				'C', Blocks.chest,
				'G', Items.gold_ingot);

		GameRegistry.addRecipe(new ItemStack(this.ItemBender),
				" # ",
				" # ",
				"B B",
				'#', Items.iron_ingot,
				'B', this.ItemBar);

		GameRegistry.addRecipe(new ItemStack(this.ItemBar),
				"I  ",
				" I ",
				"  B",
				'B', Blocks.iron_bars,
				'I', Items.iron_ingot);
	}
}
