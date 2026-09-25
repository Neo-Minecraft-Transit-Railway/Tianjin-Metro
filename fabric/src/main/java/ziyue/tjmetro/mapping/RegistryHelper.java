package ziyue.tjmetro.mapping;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.ItemStack;
import org.mtr.mapping.holder.Identifier;
import org.mtr.mapping.holder.Item;
import org.mtr.mapping.registry.BlockRegistryObject;
import org.mtr.mapping.registry.ItemRegistryObject;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

/**
 * @author ZiYueCommentary
 * @since 1.0.0-beta-2
 */
public interface RegistryHelper
{
	static ItemRegistryObject RegistryObjectBlock2Item(BlockRegistryObject fabric, Identifier forge)
			throws InvocationTargetException, InstantiationException, IllegalAccessException, NoSuchMethodException {
		Class<ItemRegistryObject> clazz = ItemRegistryObject.class;
		Constructor<ItemRegistryObject> constructor = clazz.getDeclaredConstructor(Item.class);
		constructor.setAccessible(true);
		return constructor.newInstance(fabric.get().asItem());
	}

	static ItemStack cloneSingleItemStack(ItemStack itemStack) {
		return new ItemStack(itemStack.getItem());
	}

	static Identifier getIdentifierByItem(net.minecraft.world.item.Item item) {
		return new Identifier(BuiltInRegistries.ITEM.getKey(item));
	}

	static ItemStack getItemStackByIdentifier(Identifier identifier) {
		return new ItemStack(BuiltInRegistries.ITEM.getValue(identifier.data));
	}

	/**
	 * Resolve an item id string into a stack. Invalid / empty / unknown ids become empty
	 * (air) instead of throwing — used when migrating old metal-detection-door NBT.
	 */
	static ItemStack getItemStackByIdentifierSafe(String id) {
		if (id == null || id.isEmpty() || "null".equalsIgnoreCase(id) || "minecraft:air".equals(id)) {
			return ItemStack.EMPTY;
		}
		try {
			return getItemStackByIdentifier(new Identifier(id));
		} catch (Exception e) {
			return ItemStack.EMPTY;
		}
	}
}
