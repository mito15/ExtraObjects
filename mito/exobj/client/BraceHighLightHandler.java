package com.mito.exobj.client;

import org.lwjgl.opengl.GL11;

import com.mito.exobj.BraceBase.BB_Render;
import com.mito.exobj.BraceBase.BB_ResisteredList;
import com.mito.exobj.BraceBase.ExtraObject;
import com.mito.exobj.common.item.ItemBraceBase;
import com.mito.exobj.utilities.MyUtil;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.MovingObjectPosition;
import net.minecraftforge.client.event.DrawBlockHighlightEvent;

public class BraceHighLightHandler {

	mitoClientProxy proxy;

	public BraceHighLightHandler(mitoClientProxy p) {

		this.proxy = p;
	}

	@SubscribeEvent
	public void onDrawBlockHighlight(DrawBlockHighlightEvent e) {
		if (e.currentItem != null && (e.currentItem.getItem() instanceof ItemBraceBase)) {
			ItemBraceBase itembrace = (ItemBraceBase) e.currentItem.getItem();
			MovingObjectPosition mop = itembrace.getMovingOPWithKey(e.currentItem, e.player.worldObj, e.player, proxy.getKey(), e.target, e.partialTicks);
			boolean flag = itembrace.drawHighLightBox(e.currentItem, e.player, e.partialTicks, mop);
			if (flag) {
				if (e.isCancelable()) {
					e.setCanceled(true);
				}
			}
		} else if (e.player.capabilities.isCreativeMode) {
			if (MyUtil.isBrace(e.target)) {
				GL11.glPushMatrix();
				drawHighLightBrace(e.player, MyUtil.getBrace(e.target), e.partialTicks);
				GL11.glPopMatrix();
				if (e.isCancelable()) {
					e.setCanceled(true);
				}
			}
		}
	}

	public void drawHighLightBrace(EntityPlayer player, ExtraObject base, float partialticks) {
		GL11.glMatrixMode(GL11.GL_MODELVIEW);
		GL11.glPushMatrix();
		GL11.glTranslated(-(player.lastTickPosX + (player.posX - player.lastTickPosX) * partialticks),
				-(player.lastTickPosY + (player.posY - player.lastTickPosY) * partialticks),
				-(player.lastTickPosZ + (player.posZ - player.lastTickPosZ) * partialticks));
		BB_Render render = BB_ResisteredList.getBraceBaseRender(base);
		render.drawHighLight(base, partialticks);
		GL11.glPopMatrix();
	}

}
