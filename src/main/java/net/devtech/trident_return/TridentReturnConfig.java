package net.devtech.trident_return;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.cloth.clothconfig.shadowed.blue.endless.jankson.Comment;

@Config(name = "trident_return")
public class TridentReturnConfig implements ConfigData {
	public boolean enabled = true;
	@Comment("Anticheat Bypass (warning may delay item moving)")
	public AntiCheatBypass anticheatBypass = AntiCheatBypass.NONE;
	
	public enum AntiCheatBypass {
		NONE,
		BASIC,
		ADVANCED;
		
		public boolean enabled() {
			return this != NONE;
		}
	}
}
