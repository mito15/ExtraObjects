package com.mito.exobj.common;

import buildcraft.api.transport.IPipeTile;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.util.ForgeDirection;

public class LoadBCAPI {

	public static boolean isPipe(Object object) {
		return object instanceof IPipeTile;
	}


	public static int insertItemToPipe(Object into, ItemStack item, ForgeDirection side) {
		if (into instanceof IPipeTile) {
			return ((IPipeTile) into).injectItem(item, true, side, null);
		}
		return 0;
	}

	private static IPipeTile getSidePipe() {
		return null;
	}

}
