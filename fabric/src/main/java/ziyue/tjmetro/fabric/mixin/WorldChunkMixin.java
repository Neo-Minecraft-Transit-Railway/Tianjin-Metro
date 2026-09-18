package ziyue.tjmetro.fabric.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import ziyue.tjmetro.mapping.IntegerGameRule;
import ziyue.tjmetro.mod.BlockList;
import ziyue.tjmetro.mod.block.BlockSmokeAlarm;
import ziyue.tjmetro.mod.block.IBlockExtension;

@Mixin(LevelChunk.class)
public abstract class WorldChunkMixin
{
	@Shadow
	public abstract Level getLevel();

	@Inject(at = @At("TAIL"), method = "setBlockState")
	private void afterSetBlockState(BlockPos pos, BlockState state, int flags, CallbackInfoReturnable<BlockState> cir) {
		if (!state.is(BlockTags.FIRE) && !state.is(BlockTags.CAMPFIRES)) {
			return;
		}
		if (!(getLevel() instanceof ServerLevel serverLevel)) {
			return;
		}
		final int radius = IntegerGameRule.getValue(new org.mtr.mapping.holder.ServerWorld(serverLevel), ziyue.tjmetro.mod.GameRules.SMOKE_ALARM_RANGE);
		for (int x = pos.getX() - radius; x <= pos.getX() + radius; x++) {
			for (int y = pos.getY(); y <= pos.getY() + radius; y++) {
				for (int z = pos.getZ() - radius; z <= pos.getZ() + radius; z++) {
					final BlockPos currentPos = new BlockPos(x, y, z);
					final BlockState blockState = ((LevelChunk) (Object) this).getBlockState(currentPos);
					if (IBlockExtension.isBlock(new org.mtr.mapping.holder.BlockState(blockState), BlockList.SMOKE_ALARM.get())) {
						getLevel().setBlock(currentPos, blockState.setValue(BlockSmokeAlarm.ACTIVATED.data, true), 3);
						getLevel().updateNeighborsAt(currentPos.above(2), blockState.getBlock());
					}
				}
			}
		}
	}
}
