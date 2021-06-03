package net.devtech.trident_return.mixin;

import net.devtech.trident_return.TridentReturn;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.TridentItem;
import net.minecraft.world.World;

@Mixin(TridentItem.class)
public class TridentItemMixin_CaptureSlot {
	@Inject(method = "onStoppedUsing", at = @At(value = "FIELD", target = "Lnet/minecraft/world/World;isClient:Z"))
	public void onStoppedUsing(ItemStack stack, World world, LivingEntity user, int remainingUseTicks, CallbackInfo ci) {
		if(world.isClient && user instanceof ClientPlayerEntity) {
			ClientPlayerEntity entity = (ClientPlayerEntity) user;
			TridentReturn.ORIGINAL_TRIDENT_SLOTS.put(stack.copy(), entity.inventory.selectedSlot);
		}
	}
}
