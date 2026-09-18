package ziyue.tjmetro.mod.block;

import org.mtr.mapping.holder.*;
import org.mtr.mapping.mapper.*;
import ziyue.tjmetro.mod.BlockEntityTypes;
import ziyue.tjmetro.mod.BlockList;
import ziyue.tjmetro.mod.Registry;
import ziyue.tjmetro.mod.block.base.BlockCustomColorBase;
import ziyue.tjmetro.mod.block.base.StairBlock;
import ziyue.tjmetro.mod.packet.PacketOpenBlockEntityScreen;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

/**
 * @author ZiYueCommentary
 * @see BlockCustomColorBase
 * @see BlockEntity
 * @since 1.0.0-beta-1
 */

public class BlockCustomColorConcreteStairs extends StairBlock implements BlockWithEntity
{
    public BlockCustomColorConcreteStairs() {
        super(Blocks.getWhiteConcreteMapped());
    }

    @Override
    public void addTooltips(ItemStack stack, @Nullable BlockView world, List<MutableText> tooltip, TooltipContext options) {
        tooltip.add(TextHelper.translatable("tooltip.tjmetro.custom_color").formatted(TextFormatting.GRAY));
    }

    @Nonnull
    public ActionResult onUse2(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        return IBlockExtension.checkHoldingBrushOrWrench(world, player, () -> Registry.sendPacketToClient(ServerPlayerEntity.cast(player), new PacketOpenBlockEntityScreen(pos)));
    }

    @Override
    protected net.minecraft.world.InteractionResult useWithoutItem(net.minecraft.world.level.block.state.BlockState state, net.minecraft.world.level.Level world, net.minecraft.core.BlockPos pos, net.minecraft.world.entity.player.Player player, net.minecraft.world.phys.BlockHitResult hit) {
        return onUse2(new BlockState(state), new World(world), new BlockPos(pos), new PlayerEntity(player), Hand.MAIN_HAND, new BlockHitResult(hit)).data;
    }

    @Override
    public BlockEntityExtension createBlockEntity(BlockPos blockPos, BlockState blockState) {
        return new BlockEntity(blockPos, blockState);
    }

    /**
     * @author ZiYueCommentary
     * @see ziyue.tjmetro.mod.screen.ColorPickerScreen
     * @since 1.0.0-beta-1
     */
    public static class BlockEntity extends BlockCustomColorBase.BlockEntityBase
    {
        public BlockEntity(BlockPos blockPos, BlockState blockState) {
            super(BlockEntityTypes.CUSTOM_COLOR_CONCRETE_STAIRS.get(), blockPos, blockState);
        }
    }
}
