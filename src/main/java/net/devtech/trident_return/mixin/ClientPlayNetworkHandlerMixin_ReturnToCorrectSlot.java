package net.devtech.trident_return.mixin;

import java.util.concurrent.CompletableFuture;

import it.unimi.dsi.fastutil.ints.Int2ObjectMaps;
import net.devtech.trident_return.TridentReturn;
import net.devtech.trident_return.TridentReturnConfig;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.item.ItemStack;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.Packet;
import net.minecraft.network.packet.c2s.play.ClickSlotC2SPacket;
import net.minecraft.network.packet.s2c.play.ScreenHandlerSlotUpdateS2CPacket;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;

@Mixin(ClientPlayNetworkHandler.class)
public abstract class ClientPlayNetworkHandlerMixin_ReturnToCorrectSlot {
	@Shadow @Final private MinecraftClient client;
	@Shadow @Final private ClientConnection connection;
	@Shadow public abstract void sendPacket(Packet<?> packet);

	@Inject(method = "onScreenHandlerSlotUpdate",
			at = @At(value = "INVOKE", target = "Lnet/minecraft/screen/PlayerScreenHandler;setStackInSlot(ILnet/minecraft/item/ItemStack;)V"))
	public void onSetStack1(ScreenHandlerSlotUpdateS2CPacket packet, CallbackInfo ci) throws InterruptedException {
		this.trident_return_send(packet);
	}

	@Inject(method = "onScreenHandlerSlotUpdate",
			at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerInventory;setStack(ILnet/minecraft/item/ItemStack;)V"))
	public void onSetStack0(ScreenHandlerSlotUpdateS2CPacket packet, CallbackInfo ci) throws InterruptedException {
		this.trident_return_send(packet);
	}

	@Unique
	private void trident_return_send(ScreenHandlerSlotUpdateS2CPacket packet) throws InterruptedException {
		if(TridentReturn.config.enabled) {
			int destSlot = TridentReturn.ORIGINAL_TRIDENT_SLOTS.removeInt(packet.getItemStack());
			if(destSlot != -1) {
				TridentReturnConfig.AntiCheatBypass bypass = TridentReturn.config.anticheatBypass;
				if(bypass.enabled()) {
					ClickSlotC2SPacket c2SPacket = new ClickSlotC2SPacket(packet.getSyncId(),
					                                                      packet.getSlot(),
					                                                      0,
					                                                      SlotActionType.PICKUP,
					                                                      ItemStack.EMPTY,
					                                                      Int2ObjectMaps.emptyMap());
					int slotId = 9;
					for(Slot slot : this.client.player.playerScreenHandler.slots) {
						if(slot.getIndex() == destSlot) {
							slotId = slot.id;
						}
					}

					ClickSlotC2SPacket newPacket = new ClickSlotC2SPacket(packet.getSyncId(),
					                                                      slotId,
					                                                      0,
					                                                      SlotActionType.PICKUP,
					                                                      packet.getItemStack(),
					                                                      Int2ObjectMaps.emptyMap());

					if(bypass == TridentReturnConfig.AntiCheatBypass.ADVANCED) {
						CompletableFuture.runAsync(() -> {
							this.trident_return_sendPacket(c2SPacket, newPacket, true);
						});
					} else {
						this.trident_return_sendPacket(c2SPacket, newPacket, false);
					}

				} else {
					ClickSlotC2SPacket c2SPacket = new ClickSlotC2SPacket(packet.getSyncId(),
					                                                      packet.getSlot(),
					                                                      destSlot,
					                                                      SlotActionType.SWAP,
					                                                      packet.getItemStack(),
					                                                      Int2ObjectMaps.emptyMap());
					this.sendPacket(c2SPacket);
				}
			}
		}
	}

	@Unique
	public void trident_return_sendPacket(ClickSlotC2SPacket mainPacket, ClickSlotC2SPacket secondary, boolean advancedBypass) {
		if(advancedBypass) {
			try {
				Thread.sleep((long) (300 + Math.random() * 300));
			} catch(InterruptedException e) {
				throw new RuntimeException(e);
			}
		}
		this.connection.send(mainPacket, future -> {
			Thread.sleep((long) ((advancedBypass ? 500 : 100) + Math.random() * 300));
			this.sendPacket(secondary);
		});
	}
}
