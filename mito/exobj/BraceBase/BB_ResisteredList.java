package com.mito.exobj.BraceBase;

import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.Level;

import com.mito.exobj.BraceBase.Brace.Brace;
import com.mito.exobj.BraceBase.Brace.FakeBlock;
import com.mito.exobj.BraceBase.Brace.GroupObject;
import com.mito.exobj.BraceBase.Brace.Junction;
import com.mito.exobj.BraceBase.Brace.LinearMotor;
import com.mito.exobj.BraceBase.Brace.Motor;
import com.mito.exobj.BraceBase.Brace.RedSignalCable;
import com.mito.exobj.BraceBase.Brace.Scale;
import com.mito.exobj.BraceBase.Brace.Wall;
import com.mito.exobj.BraceBase.Brace.Render.RenderBrace;
import com.mito.exobj.BraceBase.Brace.Render.RenderFakeBlock;
import com.mito.exobj.BraceBase.Brace.Render.RenderGroupObject;
import com.mito.exobj.BraceBase.Brace.Render.RenderJunction;
import com.mito.exobj.BraceBase.Brace.Render.RenderLinearMotor;
import com.mito.exobj.BraceBase.Brace.Render.RenderMotor;
import com.mito.exobj.BraceBase.Brace.Render.RenderScale;
import com.mito.exobj.BraceBase.Brace.Render.RenderWall;
import com.mito.exobj.common.MyLogger;
import com.mito.exobj.network.BB_PacketProcessor;
import com.mito.exobj.network.BB_PacketProcessor.Mode;
import com.mito.exobj.network.PacketHandler;

import cpw.mods.fml.common.FMLLog;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

public class BB_ResisteredList {

	public static int nextID = 0;

	public static Map<String, Class> stringToClassMapping = new HashMap<String, Class>();
	public static Map<Class, String> classToStringMapping = new HashMap<Class, String>();
	public static Map<Class, BB_Render> classToRenderMapping = new HashMap<Class, BB_Render>();

	/**
	 * adds a mapping between FObj classes and both a string representation and an ID
	 */
	public static void addMapping(Class ioclass, String name, int id, BB_Render render) {
		if (stringToClassMapping.containsKey(name)) {
			throw new IllegalArgumentException("ID is already registered: " + name);
		} else {
			stringToClassMapping.put(name, ioclass);
			classToStringMapping.put(ioclass, name);
			classToRenderMapping.put(ioclass, render);
		}
	}

	/**
	 * Create a new instance of an entity in the world by using the entity name.
	 */
	public static ExtraObject createFixedObjByName(String p_75620_0_, World p_75620_1_) {
		ExtraObject iobj = null;

		try {
			Class oclass = (Class) stringToClassMapping.get(p_75620_0_);

			if (oclass != null) {
				iobj = (ExtraObject) oclass.getConstructor(new Class[] { World.class }).newInstance(new Object[] { p_75620_1_ });
			}
		} catch (Exception exception) {
			exception.printStackTrace();
		}

		return iobj;
	}

	/**
	 * create a new instance of an entity from NBT store
	 */

	public static ExtraObject createBraceBaseFromNBT(NBTTagCompound nbt, World world) {
		return createBraceBaseFromNBT(nbt, world, -1);
	}

	public static ExtraObject createBraceBaseFromNBT(NBTTagCompound nbt, World world, int id) {
		ExtraObject iobj = null;

		Class oclass = null;
		try {
			if (nbt.getString("id") == null) {
				MyLogger.info("id is null");
			} else {
				//mitoLogger.info("id is " + nbt.getString("id"));
			}
			oclass = (Class) stringToClassMapping.get(nbt.getString("id"));

			if (oclass == null) {
				//mitoLogger.info("class is null");
			}
			if (oclass != null) {
				iobj = (ExtraObject) oclass.getConstructor(new Class[] { World.class }).newInstance(new Object[] { world });
				if (id != -1) {
					iobj.BBID = id;
				}
			}
		} catch (Exception exception) {
			exception.printStackTrace();
		}

		if (iobj != null) {
			try {
				iobj.readFromNBT(nbt);
			} catch (Exception e) {
				FMLLog.log(Level.ERROR, e,
						"An Entity %s(%s) has thrown an exception during loading, its state cannot be restored. Report this to the mod author",
						nbt.getString("id"), oclass.getName());
				iobj = null;
			}
		} else {
			MyLogger.warn("Skipping Entity with id " + nbt.getString("id"));
		}

		return iobj;
	}

	/**
	 * Gets the string representation of a specific entity.
	 */
	public static String getBraceBaseString(ExtraObject p_75621_0_) {
		return classToStringMapping.get(p_75621_0_.getClass());
	}

	public static BB_Render getBraceBaseRender(ExtraObject p_75621_0_) {
		return classToRenderMapping.get(p_75621_0_.getClass());
	}

	static {
		addMapping(Brace.class, "Brace", nextID++, new RenderBrace());
		addMapping(LinearMotor.class, "LinearMotor", nextID++, new RenderLinearMotor());
		addMapping(Scale.class, "Scale", nextID++, new RenderScale());
		addMapping(Wall.class, "Wall", nextID++, new RenderWall());
		addMapping(Junction.class, "Junction", nextID++, new RenderJunction());
		addMapping(FakeBlock.class, "FakeBlock", nextID++, new RenderFakeBlock());
		addMapping(Motor.class, "Motor", nextID++, new RenderMotor());
		addMapping(GroupObject.class, "GroupObject", nextID++, new RenderGroupObject());
		addMapping(RedSignalCable.class, "RedSignalCable", nextID++, new RenderBrace());
	}

	public static ExtraObject syncBraceBaseFromNBT(NBTTagCompound nbt, World world, int id) {
		ExtraObject base = BB_DataLists.getWorldData(world).getBraceBaseByID(id);
		if (base != null) {
			MyLogger.info("syncsync33 " + ((Brace)base).texture.ordinal());
			base.readFromNBT(nbt);
			MyLogger.info("syncsync34 " + ((Brace)base).texture.ordinal());
		} else {
			PacketHandler.INSTANCE.sendToAll(new BB_PacketProcessor(Mode.DELETE, base));
			MyLogger.warn("Skipping Entity with id " + nbt.getString("id"));
		}
		return base;
	}

}
