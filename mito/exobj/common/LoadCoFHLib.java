package com.mito.exobj.common;

import cofh.api.transport.IItemDuct;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.util.ForgeDirection;

public class LoadCoFHLib {

	public static boolean isDuct(Object object) {
		return object instanceof IItemDuct;
	}


	public static ItemStack insertItemToDuct(IItemDuct into, ItemStack item, ForgeDirection side) {
		if (into instanceof IItemDuct) {
			return into.insertItem(side, item);
		}
		return item;
	}

	private static IItemDuct getSidePipe() {
		return null;
	}

}
