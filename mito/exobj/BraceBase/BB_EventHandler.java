package com.mito.exobj.BraceBase;

import com.mito.exobj.BraceBase.DammyWorld.BB_GUIHandler;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.common.gameevent.TickEvent.Phase;
import cpw.mods.fml.relauncher.Side;
import net.minecraftforge.event.entity.player.PlayerOpenContainerEvent;
import net.minecraftforge.event.world.ChunkDataEvent;
import net.minecraftforge.event.world.ChunkEvent;
import net.minecraftforge.event.world.WorldEvent;

public class BB_EventHandler {

	@SubscribeEvent
	public void onLoadWorld(WorldEvent.Load e) {
		if (e.world.isRemote) {
			LoadClientWorldHandler.INSTANCE.onLoadWorld(e);
		} else {
			BB_LoadWorld.INSTANCE.onLoadWorld(e);
		}
	}

	@SubscribeEvent
	public void onUnloadWorld(WorldEvent.Unload e) {
		if (e.world.isRemote) {
			LoadClientWorldHandler.INSTANCE.onUnloadWorld(e);
		} else {
			BB_LoadWorld.INSTANCE.onUnloadWorld(e);
		}
	}

	@SubscribeEvent
	public void onChunkDataSave(ChunkDataEvent.Save e) {
		if (e.world.isRemote) {
		} else {
			BB_LoadWorld.INSTANCE.onChunkDataSave(e);
		}
	}

	@SubscribeEvent
	public void onChunkDataLoad(ChunkDataEvent.Load e) {
		if (e.world.isRemote) {
		} else {
			BB_LoadWorld.INSTANCE.onChunkDataLoad(e);
		}

	}

	@SubscribeEvent
	public void onUpdate(TickEvent.ServerTickEvent e) {
		if (e.phase == Phase.END) {
			BB_LoadWorld.INSTANCE.onUpdate(e);
		}
	}

	@SubscribeEvent
	public void onUpdate(TickEvent.PlayerTickEvent e) {
		if (e.phase == Phase.END) {
			if (e.player.worldObj.isRemote) {
				LoadClientWorldHandler.INSTANCE.onUpdate(e);
			}
		}
	}

	@SubscribeEvent
	public void onWorldTickEvent(TickEvent.WorldTickEvent e) {
		if (e.phase == Phase.END) {
			BB_LoadWorld.INSTANCE.onWorldTickEvent(e);
		}
		if (e.side == Side.CLIENT) {
			//MyLogger.info("" + e.getPhase());
		}
	}

	// 重複については未処理  unload -> save

	@SubscribeEvent
	public void onChunkLoad(ChunkEvent.Load e) {
		if (e.world.isRemote) {
			LoadClientWorldHandler.INSTANCE.onChunkLoad(e);
		} else {
			BB_LoadWorld.INSTANCE.onChunkLoad(e);
		}
	}

	@SubscribeEvent
	public void onChunkUnload(ChunkEvent.Unload e) {
		if (e.world.isRemote) {
			LoadClientWorldHandler.INSTANCE.onChunkUnload(e);
		} else {
			BB_LoadWorld.INSTANCE.onChunkUnload(e);
		}
	}

	/*@SubscribeEvent
	//@SideOnly(Side.CLIENT)
	public void GuiOpenEvent(GuiOpenEvent e) {
		if (e.gui != null) {
			mitoLogger.info("go" + e.gui.getClass().getName() + "  " + e.getPhase().toString());
			if (e.gui.getClass().getName().equals("mfw.gui.GUIFerrisConstructor")) {
			}
		} else {
			mitoLogger.info("ig" + "  " + e.getPhase().toString());
		}
	}*/

	@SubscribeEvent
	public void ContainerOpenEvent(PlayerOpenContainerEvent e) {
		BB_GUIHandler.openEvent(e);
	}

	/*@SubscribeEvent
	public void GuiOpenEvent(InitGuiEvent e) {
		if (e.gui != null) {
			mitoLogger.info("ig" + e.gui.getClass().getName() + "  " + e.getPhase().toString());
		} else {
			mitoLogger.info("ig" + "  " + e.getPhase().toString());
		}
	}*/

}
