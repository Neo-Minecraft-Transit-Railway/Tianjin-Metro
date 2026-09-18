package ziyue.tjmetro.mapping;

import net.fabricmc.fabric.api.gamerule.v1.GameRuleBuilder;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.gamerules.GameRuleCategory;
import ziyue.tjmetro.mod.Reference;

/**
 * @since 1.0.0-beta-2
 */
public class GameRuleRegistry
{
	public static BooleanGameRule registerBoolean(String name, boolean defaultValue) {
		return new BooleanGameRule(GameRuleBuilder.forBoolean(defaultValue)
				.category(GameRuleCategory.MISC)
				.buildAndRegister(Identifier.fromNamespaceAndPath(Reference.MOD_ID, name)));
	}

	public static IntegerGameRule registerInteger(String name, int defaultValue) {
		return new IntegerGameRule(GameRuleBuilder.forInteger(defaultValue)
				.category(GameRuleCategory.MISC)
				.buildAndRegister(Identifier.fromNamespaceAndPath(Reference.MOD_ID, name)));
	}
}
