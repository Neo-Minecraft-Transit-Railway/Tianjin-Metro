package ziyue.tjmetro.mapping;

import net.minecraft.world.level.gamerules.GameRule;
import org.mtr.mapping.holder.ServerWorld;
import org.mtr.mapping.tool.HolderBase;

/**
 * @author ZiYueCommentary
 * @since 1.1.0
 */
public class IntegerGameRule extends HolderBase<GameRule<Integer>>
{
	public IntegerGameRule(GameRule<Integer> data) {
		super(data);
	}

	public static int getValue(ServerWorld world, IntegerGameRule rule) {
		return ((net.minecraft.server.level.ServerLevel) world.data).getGameRules().get(rule.data);
	}
}
