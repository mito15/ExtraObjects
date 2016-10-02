package com.mito.exobj.client;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import com.mito.exobj.BraceBase.BB_RenderHandler;
import com.mito.exobj.client.render.RenderEntityBrace;
import com.mito.exobj.client.render.RenderItemBrace;
import com.mito.exobj.client.render.RenderWall;
import com.mito.exobj.common.Main;
import com.mito.exobj.common.mitoCommonProxy;
import com.mito.exobj.common.entity.EntityBrace;
import com.mito.exobj.common.entity.EntityFake;
import com.mito.exobj.common.entity.EntityWall;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.FMLCommonHandler;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.client.particle.EntityDiggingFX;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.common.MinecraftForge;

public class mitoClientProxy extends mitoCommonProxy {

	BraceHighLightHandler bh;
	ScrollWheelHandler wh;
	BB_RenderHandler rh;
	private BB_KeyBinding key_ctrl;
	private BB_KeyBinding key_alt;
	private BB_KeyBinding key_shift;

	public mitoClientProxy() {
		MinecraftForge.EVENT_BUS.register(this);
		FMLCommonHandler.instance().bus().register(this);
	}

	public BB_Key getKey() {
		return new BB_Key(this.isControlKeyDown(), this.isShiftKeyDown(), this.isAltKeyDown());
	}

	public boolean isControlKeyDown() {
		return Keyboard.isKeyDown(key_ctrl.overrideKeyCode);
	}

	public boolean haswheel() {
		return Mouse.hasWheel();
	}

	public int getwheel() {
		return Mouse.getDWheel();
	}

	public boolean isShiftKeyDown() {
		return Keyboard.isKeyDown(key_shift.overrideKeyCode);
	}

	public boolean isAltKeyDown() {
		return Keyboard.isKeyDown(key_alt.overrideKeyCode);
	}

	@Override
	public World getClientWorld() {
		return FMLClientHandler.instance().getClient().theWorld;
	}

	@Override
	public void preInit() {
		super.preInit();
		//mitoLogger.info("on pre initializing");

		this.sg = new BB_SelectedGroup(this);

		this.bh = new BraceHighLightHandler(this);
		MinecraftForge.EVENT_BUS.register(bh);
		FMLCommonHandler.instance().bus().register(bh);

		this.wh = new ScrollWheelHandler(this);
		MinecraftForge.EVENT_BUS.register(wh);
		FMLCommonHandler.instance().bus().register(wh);

		this.rh = new BB_RenderHandler();
		MinecraftForge.EVENT_BUS.register(rh);
		FMLCommonHandler.instance().bus().register(rh);

		Main.OscillatorRenderType = RenderingRegistry.getNextAvailableRenderId();
		Main.PipeRenderType = RenderingRegistry.getNextAvailableRenderId();
	}

	@Override
	public void init() {
		super.init();

		//entity render resist

		RenderingRegistry.registerEntityRenderingHandler(EntityBrace.class, new RenderEntityBrace());
		RenderingRegistry.registerEntityRenderingHandler(EntityFake.class, new RenderEntityBrace());
		RenderingRegistry.registerEntityRenderingHandler(EntityWall.class, new RenderWall());

		//item renderer

		MinecraftForgeClient.registerItemRenderer(Main.ItemBrace, new RenderItemBrace());

		//key

		this.key_shift = new BB_KeyBinding("Snap Parallel Key", Keyboard.KEY_LSHIFT, "Braces&Oscillators");
		this.key_alt = new BB_KeyBinding("Air Key", Keyboard.KEY_LMENU, "Braces&Oscillators");
		this.key_ctrl = new BB_KeyBinding("Off Snap Key", Keyboard.KEY_LCONTROL, "Braces&Oscillators");
	}

	public void upkey(int count) {
		Minecraft mc = Minecraft.getMinecraft();
		EntityClientPlayerMP player = mc.thePlayer;
		if (player != null && !mc.isGamePaused() && mc.inGameHasFocus && mc.currentScreen == null) {
			//処理とか
		}
	}

	@Override
	public EntityPlayer getClientPlayer() {
		return Minecraft.getMinecraft().thePlayer;
	}

	@Override
	public void playSound(ResourceLocation rl, float vol, float pitch, float x, float y, float z) {
		Minecraft.getMinecraft().getSoundHandler().playSound(new PositionedSoundRecord(rl, vol, pitch, x, y, z));
	}

	@Override
	public void addDiggingEffect(World world, Vec3 center, double d0, double d1, double d2, Block block, int color) {
		Minecraft.getMinecraft().effectRenderer.addEffect((new EntityDiggingFX(world, d0, d1, d2, d0 - center.xCoord, d1 - center.yCoord, d2 - center.zCoord, block, color))
				.applyColourMultiplier((int) center.xCoord, (int) center.yCoord, (int) center.zCoord));
	}
}
