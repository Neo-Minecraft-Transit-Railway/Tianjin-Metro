package ziyue.tjmetro.mapping;

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.mtr.mapping.holder.PlayerEntity;

import java.util.function.Function;

/**
 * @author ZiYueCommentary
 * @since 1.0.0-beta-2
 */
public interface PlayerInventoryHelper
{
	static void clearItems(PlayerEntity player, Function<Item, Boolean> filter) {
		final Inventory inventory = player.data.getInventory();
		for (int i = 0; i < inventory.getContainerSize(); i++) {
			if (filter.apply(inventory.getItem(i).getItem())) {
				inventory.setItem(i, ItemStack.EMPTY);
			}
		}
	}
}
