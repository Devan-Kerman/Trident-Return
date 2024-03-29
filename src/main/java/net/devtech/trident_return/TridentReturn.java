package net.devtech.trident_return;

import java.util.Objects;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import it.unimi.dsi.fastutil.Hash;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenCustomHashMap;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.Toml4jConfigSerializer;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;

public class TridentReturn implements ModMenuApi {
	public static final Object2IntMap<ItemStack> ORIGINAL_TRIDENT_SLOTS = new Object2IntOpenCustomHashMap<>(new FuzzyItemStackStrategy());
	public static TridentReturnConfig config;
	
	static {
		ORIGINAL_TRIDENT_SLOTS.defaultReturnValue(-1);
	}
	
	public TridentReturn() {
		AutoConfig.register(TridentReturnConfig.class, Toml4jConfigSerializer::new);
		config = AutoConfig.getConfigHolder(TridentReturnConfig.class).getConfig();
	}
	
	@Override
	public ConfigScreenFactory<?> getModConfigScreenFactory() {
		return parent -> AutoConfig.getConfigScreen(TridentReturnConfig.class, parent).get();
	}
	
	private static class FuzzyItemStackStrategy implements Hash.Strategy<ItemStack> {
		@Override
		public int hashCode(ItemStack o) {
			int result = Objects.hashCode(o.getItem());
			NbtCompound t;
			if(o.hasNbt()) {
				NbtCompound copied = o.getNbt().copy();
				copied.remove("Damage");
				t = copied;
			} else {
				t = o.getNbt();
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
					NbtCompound ac = a.getNbt(), ab = b.getNbt();
					if(ac == null || ab == null) {
						return ac == ab;
					} else {
						NbtCompound acd = ac.copy(), abd = ab.copy();
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
	}
}
