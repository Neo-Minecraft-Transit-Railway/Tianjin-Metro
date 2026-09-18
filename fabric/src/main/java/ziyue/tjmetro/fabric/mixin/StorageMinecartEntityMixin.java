package ziyue.tjmetro.fabric.mixin;

import net.minecraft.core.NonNullList;
import net.minecraft.world.entity.vehicle.minecart.AbstractMinecartContainer;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import ziyue.tjmetro.mapping.ContainerAccessor;

@Mixin(AbstractMinecartContainer.class)
public abstract class StorageMinecartEntityMixin implements ContainerAccessor
{
	@Shadow
	private NonNullList<ItemStack> itemStacks;

	@Override
	public NonNullList<ItemStack> tianjin_Metro$getInventory() {
		return itemStacks;
	}
}
