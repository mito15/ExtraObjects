package com.mito.exobj;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarInputStream;

import com.mito.exobj.BraceBase.BB_EventHandler;
import com.mito.exobj.client.gui.GuiHandler;
import com.mito.exobj.entity.EntityWrapperBB;
import com.mito.exobj.main.ResisterItem;
import com.mito.exobj.main.mitoCommonProxy;
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
	public static final String MODNAME = "Extra Objects";
	public static final String VERSION = "1.0.1";
	public static boolean debug = false;

	public static final CreativeTabs tab = new CreativeTabMito("CreativeTabExObj");

	@Mod.Instance(Main.MODID)
	public static Main INSTANCE;

	@SidedProxy(clientSide = "com.mito.exobj.main.mitoClientProxy", serverSide = "com.mito.exobj.main.mitoCommonProxy")
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
	//public File GroupsDir;
	//public File ObjsDir;
	public File source;

	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		if (event.getSide() == Side.CLIENT) {
			resisterFiles(event);
		}
		proxy.preInit();
		ResisterItem.preinit();
		PacketHandler.init();
		setConfig();
	}

	@EventHandler
	public void Init(FMLInitializationEvent e) {
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

	public static String getSuffix(String name) {
		if (name == null)
			return null;
		int point = name.lastIndexOf(".");
		if (point != -1) {
			return name.substring(point + 1);
		}
		return name;
	}

	public static String getFileName(String name) {
		if (name == null)
			return null;
		int point = name.lastIndexOf("/");
		if (point != -1) {
			return name.substring(point + 1);
		}
		return name;
	}

	public void resisterFiles(FMLPreInitializationEvent event) {
		File mcDir = (File) FMLInjectionData.data()[6];
		source = event.getSourceFile();
		try {
			modelDir = new File(mcDir, "brace-models");
			modelDir.mkdir();
			shapesDir = new File(modelDir, "shapes");
			if (shapesDir.listFiles() == null || shapesDir.listFiles().length < 2) {
				shapesDir.mkdir();
				//File sd2 = new File(sourceDir, "/assets/exobj/jsons");
				if (!debug) {
					try {
						@SuppressWarnings("resource")
						JarFile jar = new JarFile(source.getAbsolutePath());
						@SuppressWarnings("resource")
						JarInputStream jarInStream = new JarInputStream(new BufferedInputStream(new FileInputStream(source)));
						while (true) {
							JarEntry entry = jarInStream.getNextJarEntry();
							if (entry == null)
								break;
							if (entry.isDirectory()) {
							} else {
								String name = entry.getName();
								if (getSuffix(name).equals("json")) {

									final ByteArrayOutputStream outStream = new ByteArrayOutputStream();
									while (true) {
										int iRead = jarInStream.read();
										if (iRead < 0)
											break;
										outStream.write(iRead);
									}
									outStream.flush();
									outStream.close();
									String s = new String(outStream.toByteArray());
									//System.out.println("file " + name + " " + s);

									File file = new File(shapesDir, getFileName(name));
									file.createNewFile();
									FileWriter filewriter = new FileWriter(file);
									filewriter.write(s);
									filewriter.close();
								}
							}
							jarInStream.closeEntry();

							/*
							if (file != null) {
								String name = file.getName();
								File file2 = new File(shapesDir, name);
								try {
									file.renameTo(file2);
								} catch (SecurityException e) {
									System.out.println(e);
								} catch (NullPointerException e) {
									System.out.println(e);
								}
							}
							*/
						}
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
			/*GroupsDir = new File(modelDir, "groups");
			GroupsDir.mkdir();
			ObjsDir = new File(modelDir, "objects");
			ObjsDir.mkdir();*/
		} finally

		{
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
