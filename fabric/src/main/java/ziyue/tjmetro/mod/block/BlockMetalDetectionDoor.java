package ziyue.tjmetro.mod.block;

import org.mtr.mapping.holder.*;
import org.mtr.mapping.mapper.*;
import org.mtr.mapping.tool.HolderBase;
import org.mtr.mod.Blocks;
import org.mtr.mod.SoundEvents;
import org.mtr.mod.block.IBlock;
import ziyue.tjmetro.mapping.DefaultedItemStackList;
import ziyue.tjmetro.mapping.MetalDetectionDoorEntity;
import ziyue.tjmetro.mapping.PlayerInventoryHelper;
import ziyue.tjmetro.mapping.RegistryHelper;
import ziyue.tjmetro.mod.BlockEntityTypes;
import ziyue.tjmetro.mod.BlockList;
import ziyue.tjmetro.mod.data.IGuiExtension;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

/**
 * A device for clearing specify items from players' inventory.
 *
 * @author ZiYueCommentary
 * @see BlockEntity
 * @since 1.0.0-beta-1
 */

public class BlockMetalDetectionDoor extends BlockExtension implements DirectionHelper, BlockWithEntity, IBlock
{
    public static final BooleanProperty OPEN = BooleanProperty.of("open");

    public BlockMetalDetectionDoor() {
        this(Blocks.createDefaultBlockSettings(true));
    }

    public BlockMetalDetectionDoor(BlockSettings blockSettings) {
        super(blockSettings);
    }

    @Nullable
    @Override
    public BlockState getPlacementState2(ItemPlacementContext ctx) {
        final BlockState state = getDefaultState2().with(new Property<>(FACING.data), ctx.getPlayerFacing().data);
        final BlockPos pos = ctx.getBlockPos();
        final World world = ctx.getWorld();
        if (IBlock.isReplaceable(ctx, Direction.UP, 3)) {
            world.setBlockState(pos.up(1), state.with(new Property<>(THIRD.data), EnumThird.MIDDLE));
            world.setBlockState(pos.up(2), state.with(new Property<>(THIRD.data), EnumThird.UPPER));
            return state.with(new Property<>(THIRD.data), EnumThird.LOWER);
        }
        return null;
    }

    @Nonnull
    @Override
    public ActionResult onUse2(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        return IBlockExtension.checkHoldingBrushOrWrench(world, player, () -> {
            final BlockPos blockPos = switch (IBlock.getStatePropertySafe(state, THIRD)) {
                case LOWER -> pos;
                case MIDDLE -> pos.down(1);
                default -> pos.down(2);
            };
            final org.mtr.mapping.holder.BlockEntity lowerHolder = world.getBlockEntity(blockPos);
            final org.mtr.mapping.holder.BlockEntity middleHolder = world.getBlockEntity(blockPos.up(1));
            final org.mtr.mapping.holder.BlockEntity upperHolder = world.getBlockEntity(blockPos.up(2));
            if (lowerHolder == null || middleHolder == null || upperHolder == null) {
                return;
            }
            if (!(lowerHolder.data instanceof BlockEntity lower) || !(middleHolder.data instanceof BlockEntity middle) || !(upperHolder.data instanceof BlockEntity upper)) {
                return;
            }
            MetalDetectionDoorEntity entity = new MetalDetectionDoorEntity(world, blockPos, lower);
            world.addFreshEntity(new Entity(entity));
            entity.interact(player.data, hand.data);
            middle.setData(lower.inventory);
            upper.setData(lower.inventory);
        });
    }

    @Nonnull
    @Override
    public VoxelShape getOutlineShape2(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        final Direction direction = IBlock.getStatePropertySafe(state, FACING);
        if (IBlock.getStatePropertySafe(state, THIRD) == EnumThird.UPPER) {
            return IBlock.getVoxelShapeByDirection(0, 0, 1, 16, 6, 15, direction);
        } else {
            final VoxelShape left = IBlock.getVoxelShapeByDirection(0, 0, 1, 1, 16, 15, direction);
            final VoxelShape right = IBlock.getVoxelShapeByDirection(15, 0, 1, 16, 16, 15, direction);
            return VoxelShapes.union(left, right);
        }
    }

    @Nonnull
    @Override
    public VoxelShape getCollisionShape2(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        final VoxelShape shape = this.getOutlineShape2(state, world, pos, context);
        if ((!IBlock.getStatePropertySafe(state, OPEN)) && (IBlock.getStatePropertySafe(state, THIRD) != EnumThird.UPPER)) {
            final VoxelShape barrier = IBlock.getVoxelShapeByDirection(0, 0, 1, 16, 16, 2, IBlock.getStatePropertySafe(state, FACING));
            return VoxelShapes.union(shape, barrier);
        }
        return shape;
    }

    @Override
    public void onBreak2(World world, BlockPos pos, BlockState state, PlayerEntity player) {
        switch (IBlock.getStatePropertySafe(state, THIRD)) {
            case UPPER:
                IBlockExtension.breakBlock(world, pos.down(1), BlockList.METAL_DETECTION_DOOR.get());
                IBlockExtension.breakBlock(world, pos.down(2), BlockList.METAL_DETECTION_DOOR.get());
                break;
            case MIDDLE:
                IBlockExtension.breakBlock(world, pos.up(1), BlockList.METAL_DETECTION_DOOR.get());
                IBlockExtension.breakBlock(world, pos.down(1), BlockList.METAL_DETECTION_DOOR.get());
                break;
            case LOWER:
                IBlockExtension.breakBlock(world, pos.up(1), BlockList.METAL_DETECTION_DOOR.get());
                IBlockExtension.breakBlock(world, pos.up(2), BlockList.METAL_DETECTION_DOOR.get());
                break;
        }
        super.onBreak2(world, pos, state, player);
    }

    @Override
    public void addTooltips(ItemStack stack, @Nullable BlockView world, List<MutableText> tooltip, TooltipContext options) {
        IGuiExtension.addHoldShiftTooltip(tooltip, TextHelper.translatable("tooltip.tjmetro.metal_detection_door"));
    }

    @Override
    public BlockEntityExtension createBlockEntity(BlockPos blockPos, BlockState blockState) {
        return new BlockEntity(blockPos, blockState);
    }

    @Override
    public void addBlockProperties(List<HolderBase<?>> properties) {
        properties.add(FACING);
        properties.add(THIRD);
        properties.add(OPEN);
    }

    /**
     * @author ZiYueCommentary
     * @since 1.0.0-beta-2
     */
    public static class BlockEntity extends BlockEntityExtension
    {
        public static final String TAG_FILTER_FORMAT = "tjmetro_mdd_filter_v1";
        public final DefaultedItemStackList inventory;
        private boolean needsSaveMigration;

        public BlockEntity(BlockPos blockPos, BlockState blockState) {
            super(BlockEntityTypes.METAL_DETECTION_DOOR.get(), blockPos, blockState);
            this.inventory = DefaultedItemStackList.ofSize(9);
        }

        @Override
        public void blockEntityTick() {
            if (IBlock.getStatePropertySafe(getCachedState2(), THIRD) != EnumThird.LOWER) return;

            // Flush one-shot NBT migration after the world is ready (avoids writing during load).
            if (needsSaveMigration) {
                needsSaveMigration = false;
                markDirty2();
            }

            // MTR mapping wraps a null Minecraft player as a non-null PlayerEntity holder.
            // Old worlds (and any loaded chunk with these doors) tick every tick; without this
            // guard, player.getX() NPEs when nobody is within range and the server crashes.
            final PlayerEntity player = getWorld2().getNearestPlayer(getPos2().getX(), getPos2().getY(), getPos2().getZ(), 1, false);
            if (player == null || player.data == null) {
                if (IBlock.getStatePropertySafe(getCachedState2(), OPEN)) {
                    getWorld2().setBlockState(getPos2(), getCachedState2().with(new Property<>(OPEN.data), false));
                }
                return;
            }

            if (getPos2().getX() == Math.floor(player.getX()) && getPos2().getY() == Math.round(player.getY()) && getPos2().getZ() == Math.floor(player.getZ())) {
                List<?> items = this.inventory.data.stream().map(itemStack -> itemStack.getItem()).toList(); // Do not use method reference.
                PlayerInventoryHelper.clearItems(player, items::contains);
                if (IBlock.getStatePropertySafe(getCachedState2(), OPEN)) return;
                getWorld2().playSound(null, getPos2(), SoundEvents.TICKET_BARRIER.get(), SoundCategory.BLOCKS, 1, 1);
                getWorld2().setBlockState(getPos2(), getCachedState2().with(new Property<>(OPEN.data), true));
            }
        }

        @Override
        public void writeCompoundTag(CompoundTag compoundTag) {
            compoundTag.putBoolean(TAG_FILTER_FORMAT, true);
            for (int i = 0; i < this.inventory.size(); i++) {
                compoundTag.data.putString(Integer.toString(i), RegistryHelper.getIdentifierByItem(this.inventory.get(i).getItem()).data.toString());
            }
            super.writeCompoundTag(compoundTag);
        }

        @Override
        public void readCompoundTag(CompoundTag compoundTag) {
            boolean migrated = false;
            try {
                if (!compoundTag.getBoolean(TAG_FILTER_FORMAT)) {
                    // Pre-1.21.11 / early builds: filters may be missing, empty, or stored as item lists.
                    migrated = migrateLegacyFilterInventory(compoundTag);
                }
                for (int i = 0; i < this.inventory.size(); i++) {
                    final String key = Integer.toString(i);
                    final String id = compoundTag.contains(key) ? compoundTag.getString(key) : "";
                    this.inventory.set(i, RegistryHelper.getItemStackByIdentifierSafe(id));
                }
            } catch (Exception e) {
                // Never let bad NBT take down the server; clear filters and rewrite on next save.
                for (int i = 0; i < this.inventory.size(); i++) {
                    this.inventory.set(i, net.minecraft.world.item.ItemStack.EMPTY);
                }
                migrated = true;
            }
            if (migrated || !compoundTag.getBoolean(TAG_FILTER_FORMAT)) {
                needsSaveMigration = true;
            }
            super.readCompoundTag(compoundTag);
        }

        /**
         * Best-effort conversion of older metal-detection-door inventories into the current
         * string-id slot format. Returns true if anything was rewritten into {@code compoundTag}.
         */
        private boolean migrateLegacyFilterInventory(CompoundTag compoundTag) {
            boolean changed = false;
            // Older saves sometimes used a vanilla-style item list.
            if (compoundTag.contains("Items")) {
                try {
                    final net.minecraft.nbt.ListTag items = compoundTag.getListOrEmpty("Items");
                    for (int i = 0; i < this.inventory.size(); i++) {
                        this.inventory.set(i, net.minecraft.world.item.ItemStack.EMPTY);
                        compoundTag.putString(Integer.toString(i), "minecraft:air");
                    }
                    for (int i = 0; i < items.size(); i++) {
                        final net.minecraft.nbt.CompoundTag itemTag = items.getCompoundOrEmpty(i);
                        final int slot = itemTag.getByteOr("Slot", (byte) i) & 255;
                        if (slot < 0 || slot >= this.inventory.size()) {
                            continue;
                        }
                        final String id = itemTag.getStringOr("id", "");
                        final net.minecraft.world.item.ItemStack stack = RegistryHelper.getItemStackByIdentifierSafe(id);
                        this.inventory.set(slot, stack);
                        compoundTag.putString(Integer.toString(slot), RegistryHelper.getIdentifierByItem(stack.getItem()).data.toString());
                    }
                    compoundTag.data.remove("Items");
                    changed = true;
                } catch (Exception ignored) {
                    // Fall through to per-slot string repair below.
                }
            }
            for (int i = 0; i < this.inventory.size(); i++) {
                final String key = Integer.toString(i);
                if (!compoundTag.contains(key)) {
                    compoundTag.putString(key, "minecraft:air");
                    changed = true;
                    continue;
                }
                final String raw = compoundTag.getString(key);
                if (raw == null || raw.isEmpty() || "null".equalsIgnoreCase(raw)) {
                    compoundTag.putString(key, "minecraft:air");
                    changed = true;
                }
            }
            compoundTag.putBoolean(TAG_FILTER_FORMAT, true);
            return changed;
        }

        public void setData(DefaultedItemStackList list) {
            for (int i = 0; i < this.inventory.size(); i++) {
                this.inventory.set(i, RegistryHelper.cloneSingleItemStack(list.get(i)));
            }
            markDirty2();
        }
    }
}
