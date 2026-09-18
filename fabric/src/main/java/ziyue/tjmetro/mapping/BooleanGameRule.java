package ziyue.tjmetro.mapping;

import net.minecraft.world.level.gamerules.GameRule;
import org.mtr.mapping.holder.ServerWorld;
import org.mtr.mapping.tool.HolderBase;

/**
 * @author ZiYueCommentary
 * @since 1.0.0-beta-2
 */
public class BooleanGameRule extends HolderBase<GameRule<Boolean>>
{
	public BooleanGameRule(GameRule<Boolean> data) {
		super(data);
	}

	public static boolean getValue(ServerWorld world, BooleanGameRule rule) {
		return ((net.minecraft.server.level.ServerLevel) world.data).getGameRules().get(rule.data);
	}
}
