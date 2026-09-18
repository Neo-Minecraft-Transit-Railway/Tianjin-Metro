package ziyue.tjmetro.mod;

import ziyue.tjmetro.mapping.BooleanGameRule;
import ziyue.tjmetro.mapping.GameRuleRegistry;
import ziyue.tjmetro.mapping.IntegerGameRule;

public interface GameRules
{
    BooleanGameRule NO_FALLING_BLOCK = GameRuleRegistry.registerBoolean("prevent_block_falling", false);
    IntegerGameRule PLAYER_DETECT_RANGE = GameRuleRegistry.registerInteger("player_detect_range", 3);
    IntegerGameRule SMOKE_ALARM_RANGE = GameRuleRegistry.registerInteger("smoke_alarm_range", 8);

    static void registerGameRules() {
        // Calling this class to initialize constants
        TianjinMetro.LOGGER.info("Registering gamerules");
    }
}
