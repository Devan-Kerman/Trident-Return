package net.devtech.trident_return.mixin;

import net.devtech.trident_return.TridentReturn;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.Packet;
import net.minecraft.network.packet.c2s.play.ClickSlotC2SPacket;
import net.minecraft.network.packet.s2c.play.ScreenHandlerSlotUpdateS2CPacket;
import net.minecraft.screen.slot.SlotActionType;

@Mixin (ClientPlayNetworkHandler.class)
public abstract class ClientPlayNetworkHandlerMixin_ReturnToCorrectSlot {
	@Shadow private MinecraftClient client;

	@Inject (method = "onScreenHandlerSlotUpdate",
			at = @At (value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerInventory;setStack(ILnet/minecraft/item/ItemStack;)V"))
	public void onSetStack0(ScreenHandlerSlotUpdateS2CPacket packet, CallbackInfo ci) {
		this.send(packet);
	}

	@Inject(method = "onScreenHandlerSlotUpdate", at = @At(value = "INVOKE", target = "Lnet/minecraft/screen/PlayerScreenHandler;setStackInSlot(ILnet/minecraft/item/ItemStack;)V"))
	public void onSetStack1(ScreenHandlerSlotUpdateS2CPacket packet, CallbackInfo ci) {
		this.send(packet);
	}

	@Unique
	private void send(ScreenHandlerSlotUpdateS2CPacket packet) {
		int destSlot = TridentReturn.ORIGINAL_TRIDENT_SLOTS.removeInt(packet.getItemStack());
		if (destSlot != -1) {
			ClickSlotC2SPacket c2SPacket = new ClickSlotC2SPacket(
					packet.getSyncId(),
					packet.getSlot(),
					destSlot,
					SlotActionType.SWAP,
					packet.getItemStack(),
					client.player.currentScreenHandler.getNextActionId(client.player.inventory));
			this.sendPacket(c2SPacket);
		}
	}

	@Shadow
	public abstract void sendPacket(Packet<?> packet);
}
