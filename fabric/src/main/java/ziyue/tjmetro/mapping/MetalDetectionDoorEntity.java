package ziyue.tjmetro.mapping;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.minecart.MinecartChest;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.inventory.MenuType;
import org.mtr.mapping.holder.BlockPos;
import org.mtr.mapping.holder.World;
import ziyue.tjmetro.mod.block.BlockMetalDetectionDoor;

/**
 * An entity for GUI of Metal Detection Door. This entity is a minecart-with-chest.
 *
 * @author ZiYueCommentary
 * @see BlockMetalDetectionDoor
 * @since 1.0.0-beta-2
 */
public class MetalDetectionDoorEntity extends MinecartChest
{
	public final BlockMetalDetectionDoor.BlockEntity blockEntity;

	public MetalDetectionDoorEntity(World world, BlockPos blockPos, BlockMetalDetectionDoor.BlockEntity blockEntity) {
		super(EntityType.CHEST_MINECART, world.data);
		setPos(blockPos.getX() + 0.5, -1, blockPos.getZ() + 0.5);
		this.blockEntity = blockEntity;
		for (int i = 0; i < blockEntity.inventory.size(); i++) {
			getItemStacks().set(i, blockEntity.inventory.get(i));
		}
	}

	@Override
	public AbstractContainerMenu createMenu(int syncId, Inventory playerInventory) {
		return new ChestMenu(MenuType.GENERIC_9x1, syncId, playerInventory, this, 1) {
			@Override
			public void removed(Player player) {
				super.removed(player);
				final MetalDetectionDoorEntity entity = (MetalDetectionDoorEntity) getContainer();
				entity.blockEntity.setData(new DefaultedItemStackList(entity.getItemStacks()));
				entity.clearItemStacks();
				entity.discard();
			}
		};
	}

	@Override
	public boolean stillValid(Player player) {
		return true;
	}

	@Override
	public boolean isNoGravity() {
		return true;
	}

	@Override
	public int getMaxStackSize() {
		return 1;
	}

	@Override
	public Component getDisplayName() {
		return Component.translatable("gui.tjmetro.metal_detection_door");
	}
}
