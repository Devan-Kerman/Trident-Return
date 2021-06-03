package net.devtech.trident_return;

import java.util.Objects;

import it.unimi.dsi.fastutil.Hash;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenCustomHashMap;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;

public class TridentReturn {
	public static final Object2IntOpenCustomHashMap<ItemStack> ORIGINAL_TRIDENT_SLOTS = new Object2IntOpenCustomHashMap<>(new Hash.Strategy<ItemStack>() {
		@Override
		public int hashCode(ItemStack o) {
			int result = Objects.hashCode(o.getItem());
			CompoundTag t;
			if(o.hasTag()) {
				CompoundTag copied = o.getTag().copy();
				copied.remove("Damage");
				t = copied;
			} else {
				t = o.getTag();
			}
			result = 31 * result + Objects.hashCode(t);
			return result;
		}

		@Override
		public boolean equals(ItemStack a, ItemStack b) {
			if(a == b) {
				return true;
			} else if(a == null ^ b == null) {
				return false;
			} else {
				// fuzzy equals, checks if Damage is roughly equal
				if(ItemStack.areItemsEqual(a, b)) {
					CompoundTag ac = a.getTag(), ab = b.getTag();
					if(ac == null || ab == null) {
						return ac == ab;
					} else {
						CompoundTag acd = ac.copy(), abd = ab.copy();
						acd.remove("Damage");
						abd.remove("Damage");
						if(acd.equals(abd)) {
							return Math.abs(ac.getInt("Damage") - ab.getInt("Damage")) <= 3;
						}
					}
				}
				return false;
			}
		}
	});
	static {
		ORIGINAL_TRIDENT_SLOTS.defaultReturnValue(-1);
	}
}
