package vazkii.quark.content.client.module;

import com.mojang.blaze3d.platform.Window;

import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.OptionInstance;
import net.minecraft.client.player.Input;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.resources.language.I18n;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.client.event.MovementInputUpdateEvent;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.client.event.RenderGuiOverlayEvent;
import net.minecraftforge.client.gui.overlay.VanillaGuiOverlay;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import vazkii.arl.util.ClientTicker;
import vazkii.quark.base.client.handler.ModKeybindHandler;
import vazkii.quark.base.module.LoadModule;
import vazkii.quark.base.module.ModuleCategory;
import vazkii.quark.base.module.QuarkModule;
import vazkii.quark.base.module.config.Config;

@LoadModule(category = ModuleCategory.CLIENT, hasSubscriptions = true, subscribeOn = Dist.CLIENT)
public class AutoWalkKeybindModule extends QuarkModule {

	@Config public static boolean drawHud = true;
	@Config public static int hudHeight = 10;

	@OnlyIn(Dist.CLIENT)
	private KeyMapping keybind;

	private boolean autorunning;
	private boolean hadAutoJump;

	@Override
	@OnlyIn(Dist.CLIENT)
	public void registerKeybinds(RegisterKeyMappingsEvent event) {
		keybind = ModKeybindHandler.init(event, "autorun", null, ModKeybindHandler.MISC_GROUP);
	}

	@SubscribeEvent
	@OnlyIn(Dist.CLIENT)
	public void onMouseInput(InputEvent.MouseButton event) {
		acceptInput();
	}

	@SubscribeEvent
	@OnlyIn(Dist.CLIENT)
	public void onKeyInput(InputEvent.Key event) {
		acceptInput();
	}

	@SubscribeEvent
	@OnlyIn(Dist.CLIENT)
	public void drawHUD(RenderGuiOverlayEvent.Post event) {
		if(drawHud && autorunning && event.getOverlay() == VanillaGuiOverlay.HOTBAR.type()) {
			String message = I18n.get("quark.misc.autowalking");

			Minecraft mc = Minecraft.getInstance();
			int w = mc.font.width("OoO" + message + "oOo");

			Window window = event.getWindow();
			int x = (window.getGuiScaledWidth() - w) / 2;
			int y = hudHeight;

			String displayMessage = message;
			int dots = (ClientTicker.ticksInGame / 10) % 2;
			switch(dots) {
			case 0 -> displayMessage = "OoO " + message + " oOo";
			case 1 -> displayMessage = "oOo " + message + " OoO";
			}

			mc.font.drawShadow(event.getPoseStack(), displayMessage, x, y, 0xFFFFFFFF);
		}
	}

	private void acceptInput() {
		Minecraft mc = Minecraft.getInstance();

		OptionInstance<Boolean> opt = mc.options.autoJump();
		if(mc.options.keyUp.isDown()) {
			if(autorunning)
				opt.set(hadAutoJump);

			autorunning = false;
		}

		else if(keybind.isDown()) {
			autorunning = !autorunning;

			if(autorunning) {
				hadAutoJump = opt.get();
				opt.set(true);
			} else opt.set(hadAutoJump);
		}
	}

	@SubscribeEvent
	@OnlyIn(Dist.CLIENT)
	public void onInput(MovementInputUpdateEvent event) {
		Minecraft mc = Minecraft.getInstance();
		if(mc.player != null && autorunning) {
			Input input = event.getInput();
			input.up = true;
			input.forwardImpulse = ((LocalPlayer) event.getEntity()).isMovingSlowly() ? 0.3F : 1F;
		}
	}

}
