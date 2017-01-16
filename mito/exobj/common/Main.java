package com.mito.exobj.common;

import java.io.File;

import com.mito.exobj.BraceBase.BB_EventHandler;
import com.mito.exobj.client.gui.GuiHandler;
import com.mito.exobj.client.render.exorender.BB_TypeResister;
import com.mito.exobj.common.entity.EntityWrapperBB;
import com.mito.exobj.common.main.ResisterItem;
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
import cpw.mods.fml.relauncher.FMLInjectionData;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;

@Mod(modid = Main.MODID, version = Main.VERSION/*, guiFactory = "com.mito.ruins.client.config.CaveGuiFactory"*/)
public class Main {

	public static final String MODID = "mito_extra_objects";
	public static final String VERSION = "1.0.0";
	public static boolean debug = false;

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
	public static int RenderType_Objects;
	public BB_EventHandler leh;

	//configuration
	public File modelDir;
	public File shapesDir;
	public File GroupsDir;
	public File ObjsDir;

	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		resisterFiles();
		proxy.preInit();
		ResisterItem.preinit();
		PacketHandler.init();
		setConfig();
	}

	@EventHandler
	public void Init(FMLInitializationEvent e) {
		BB_TypeResister.loadModels();
		resisterEvent();
		proxy.init();
		NetworkRegistry.INSTANCE.registerGuiHandler(this, new GuiHandler());
		EntityRegistry.registerModEntity(EntityWrapperBB.class, "Wrapper", 1, this, 512, 120, false);
		ResisterItem.RegisterRecipe();
	}

	@EventHandler
	public void postInit(FMLPostInitializationEvent e) {
		if (debug) {
			LoadNGTLib.elase();
		}
	}

	public void resisterEvent() {
		this.leh = new BB_EventHandler();
		MinecraftForge.EVENT_BUS.register(leh);
		FMLCommonHandler.instance().bus().register(leh);

		MinecraftForge.EVENT_BUS.register(this);
		FMLCommonHandler.instance().bus().register(this);
	}

	public void resisterFiles() {
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
	}

	public void setConfig() {
		File mcDir = (File) FMLInjectionData.data()[6];
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
}
