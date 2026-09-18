package ziyue.tjmetro.fabric.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.FallingBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ziyue.tjmetro.mapping.BooleanGameRule;
import ziyue.tjmetro.mod.GameRules;

@Mixin(FallingBlock.class)
public abstract class FallingBlockMixin extends Block
{
	protected FallingBlockMixin(Properties settings) {
		super(settings);
	}

	@Inject(at = @At("HEAD"), method = "tick", cancellable = true)
	private void beforeScheduledTick(BlockState state, ServerLevel world, BlockPos pos, RandomSource random, CallbackInfo ci) {
		if (BooleanGameRule.getValue(new org.mtr.mapping.holder.ServerWorld(world), GameRules.NO_FALLING_BLOCK)) {
			ci.cancel();
		}
	}
}
