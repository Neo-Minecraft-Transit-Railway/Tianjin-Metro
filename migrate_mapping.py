#!/usr/bin/env python3
"""Bulk migrate org.mtr.mapping.* to MTR 4.1 native API in Tianjin-Metro fabric sources."""
import re
from pathlib import Path

ROOT = Path(__file__).parent / "fabric" / "src" / "main" / "java"

IMPORT_REPLACEMENTS = [
    (r"import org\.mtr\.mapping\.registry\.BlockRegistryObject;\n", ""),
    (r"import org\.mtr\.mapping\.registry\.BlockEntityTypeRegistryObject;\n", ""),
    (r"import org\.mtr\.mapping\.registry\.ItemRegistryObject;\n", ""),
    (r"import org\.mtr\.mapping\.registry\.EntityTypeRegistryObject;\n", ""),
    (r"import org\.mtr\.mapping\.registry\.PacketHandler;\n", "import org.mtr.packet.PacketHandler;\n"),
    (r"import org\.mtr\.mapping\.tool\.PacketBufferReceiver;\n", "import org.mtr.packet.PacketBufferReceiver;\n"),
    (r"import org\.mtr\.mapping\.tool\.PacketBufferSender;\n", "import org.mtr.packet.PacketBufferSender;\n"),
    (r"import org\.mtr\.mapping\.mapper\.BlockEntityExtension;\n", "import org.mtr.block.BlockEntityExtension;\n"),
    (r"import org\.mtr\.mod\.block\.IBlock;\n", "import org.mtr.block.IBlock;\n"),
    (r"import org\.mtr\.mod\.block\.", "import org.mtr.block."),
    (r"import org\.mtr\.mod\.item\.ItemBlockEnchanted;\n", "import org.mtr.item.ItemBlockEnchanted;\n"),
    (r"import org\.mtr\.mod\.render\.", "import org.mtr.render."),
    (r"import org\.mtr\.mod\.client\.", "import org.mtr.client."),
    (r"import org\.mtr\.mod\.data\.IGui;\n", "import org.mtr.data.IGui;\n"),
    (r"import org\.mtr\.mapping\.mapper\.GraphicsHolder;\n", "import org.mtr.client.GraphicsHolder;\n"),
    (r"import org\.mtr\.mapping\.mapper\.BlockEntityRenderer;\n", "import org.mtr.render.BlockEntityRendererExtension;\n"),
    (r"import org\.mtr\.mapping\.holder\.RenderLayer;\n", "import net.minecraft.client.renderer.RenderType;\n"),
    (r"import org\.mtr\.mapping\.holder\.ServerWorld;\n", "import net.minecraft.server.level.ServerLevel;\n"),
    (r"import org\.mtr\.mapping\.holder\.ServerPlayerEntity;\n", "import net.minecraft.server.level.ServerPlayer;\n"),
    (r"import org\.mtr\.mapping\.holder\.MinecraftServer;\n", "import net.minecraft.server.MinecraftServer;\n"),
    (r"import org\.mtr\.mapping\.holder\.BlockEntity;\n", "import net.minecraft.world.level.block.entity.BlockEntity;\n"),
    (r"import org\.mtr\.mapping\.holder\.BlockPos;\n", "import net.minecraft.core.BlockPos;\n"),
    (r"import org\.mtr\.mapping\.holder\.BlockState;\n", "import net.minecraft.world.level.block.state.BlockState;\n"),
    (r"import org\.mtr\.mapping\.holder\.World;\n", "import net.minecraft.world.level.Level;\n"),
    (r"import org\.mtr\.mapping\.holder\.Screen;\n", "import net.minecraft.client.gui.screens.Screen;\n"),
    (r"import org\.mtr\.mapping\.holder\.MutableText;\n", "import net.minecraft.network.chat.Component;\n"),
    (r"import org\.mtr\.mapping\.holder\.ItemStack;\n", "import net.minecraft.world.item.ItemStack;\n"),
    (r"import org\.mtr\.mapping\.holder\.Item;\n", "import net.minecraft.world.item.Item;\n"),
    (r"import org\.mtr\.mapping\.holder\.Identifier;\n", "import net.minecraft.resources.ResourceLocation;\n"),
    (r"import org\.mtr\.mapping\.holder\.Direction;\n", "import net.minecraft.core.Direction;\n"),
    (r"import org\.mtr\.mapping\.holder\.PlayerEntity;\n", "import net.minecraft.world.entity.player.Player;\n"),
    (r"import org\.mtr\.mapping\.holder\.ActionResult;\n", "import net.minecraft.world.InteractionResult;\n"),
    (r"import org\.mtr\.mapping\.holder\.Hand;\n", "import net.minecraft.world.InteractionHand;\n"),
    (r"import org\.mtr\.mapping\.holder\.BlockHitResult;\n", "import net.minecraft.world.phys.BlockHitResult;\n"),
    (r"import org\.mtr\.mapping\.holder\.CompoundTag;\n", "import net.minecraft.nbt.CompoundTag;\n"),
    (r"import org\.mtr\.mapping\.holder\.BlockEntityType;\n", "import net.minecraft.world.level.block.entity.BlockEntityType;\n"),
    (r"import org\.mtr\.mapping\.holder\.BlockView;\n", "import net.minecraft.world.level.BlockGetter;\n"),
    (r"import org\.mtr\.mapping\.holder\.BlockSettings;\n", "import net.minecraft.world.level.block.state.BlockBehaviour;\n"),
    (r"import org\.mtr\.mapping\.holder\.VoxelShape;\n", "import net.minecraft.world.phys.shapes.VoxelShape;\n"),
    (r"import org\.mtr\.mapping\.holder\.ShapeContext;\n", "import net.minecraft.world.phys.shapes.CollisionContext;\n"),
    (r"import org\.mtr\.mapping\.holder\.ItemPlacementContext;\n", "import net.minecraft.world.item.context.BlockPlaceContext;\n"),
    (r"import org\.mtr\.mapping\.holder\.TextFormatting;\n", "import net.minecraft.ChatFormatting;\n"),
    (r"import org\.mtr\.mapping\.holder\.LivingEntity;\n", "import net.minecraft.world.entity.LivingEntity;\n"),
    (r"import org\.mtr\.mapping\.holder\.WorldAccess;\n", "import net.minecraft.world.level.LevelAccessor;\n"),
    (r"import org\.mtr\.mapping\.holder\.TooltipContext;\n", "import net.minecraft.world.item.TooltipFlag;\n"),
    (r"import org\.mtr\.mapping\.holder\.PressAction;\n", "import net.minecraft.world.item.Item;\n"),
]

CONTENT_REPLACEMENTS = [
    ("BlockRegistryObject", "ObjectHolder<Block>"),
    ("BlockEntityTypeRegistryObject<", "ObjectHolder<BlockEntityType<"),
    ("ItemRegistryObject", "ObjectHolder<Item>"),
    ("EntityTypeRegistryObject<", "ObjectHolder<EntityType<"),
    ("RenderLayer.getCutout()", "RenderType.cutout()"),
    ("RenderLayer.getTranslucent()", "RenderType.translucent()"),
    ("RegistryClient.setupPackets(\"packet\");", ""),
    ("RegistryClient.REGISTRY_CLIENT.init();", ""),
    ("BlockPos.fromLong(", "BlockPos.of("),
    ("ServerPlayerEntity.cast(", "(ServerPlayer) "),
    ("ServerWorld.cast(", "("),
    ("markDirty2()", "setChanged()"),
    ("readCompoundTag(", "readNbt("),
    ("writeCompoundTag(", "writeNbt("),
    ("getWorld2()", "getLevel()"),
    ("getPos2()", "getBlockPos()"),
    ("getCachedState2()", "getBlockState()"),
    ("entity.data instanceof", "entity instanceof"),
    ("((BlockCustomColorBase.BlockEntityBase) entity.data)", "((BlockCustomColorBase.BlockEntityBase) entity)"),
    ("org.mtr.mod.block.", "org.mtr.block."),
    ("org.mtr.mod.render.", "org.mtr.render."),
    ("org.mtr.mod.client.", "org.mtr.client."),
    ("org.mtr.mod.data.IGui", "org.mtr.data.IGui"),
    ("BlockItemExtension::new", "BlockItem::new"),
]

BLOCKLIST_REPLACEMENTS = [
    (r'\(\) -> new Block\(new BlockCeiling\(Blocks\.createDefaultBlockSettings\(false\)\)\)',
     r'settings -> new BlockCeiling(settings)'),
    (r'\(\) -> new Block\(new BlockPlatform\(Blocks\.createDefaultBlockSettings\(false\), (true|false)\)\)',
     r'settings -> new BlockPlatform(settings, \1)'),
    (r'\(\) -> new Block\(new BlockPlatformSlab\(Blocks\.createDefaultBlockSettings\(false\)\)\)',
     r'settings -> new BlockPlatformSlab(settings)'),
    (r'\(\) -> new Block\(Blocks\.createDefaultBlockSettings\(false\)\)',
     r'settings -> new Block(settings)'),
    (r'\(\) -> new Block\(new SlabBlockExtension\(Blocks\.createDefaultBlockSettings\(false\)\)\)',
     r'settings -> new SlabBlock(settings)'),
    (r'\(\) -> new Block\(new StairBlock\(org\.mtr\.mapping\.holder\.Blocks\.getBricksMapped\(\)\)\)',
     r'settings -> new ziyue.tjmetro.mod.block.base.StairBlock(net.minecraft.world.level.block.Blocks.BRICKS.defaultBlockState(), settings)'),
    (r'\(\) -> new Block\(new (\w+)\(\)\)', r'\1::new'),
    (r'\(\) -> new Block\(new (\w+)\(([^)]+)\)\)', r'settings -> new \1(settings, \2)'),
]

def migrate_file(path: Path) -> bool:
    text = path.read_text(encoding="utf-8")
    if "org.mtr.mapping" not in text and path.name != "BlockList.java":
        return False
    original = text

    if path.name == "BlockList.java":
        for pat, repl in BLOCKLIST_REPLACEMENTS:
            text = re.sub(pat, repl, text)
        text = text.replace(
            "import org.mtr.mapping.holder.Block;\n", "")
        text = text.replace(
            "import org.mtr.mapping.mapper.BlockItemExtension;\n",
            "import net.minecraft.world.item.BlockItem;\nimport net.minecraft.world.level.block.Block;\nimport net.minecraft.world.level.block.SlabBlock;\n")
        text = text.replace(
            "import org.mtr.mapping.mapper.SlabBlockExtension;\n", "")
        text = text.replace(
            "import org.mtr.mapping.registry.BlockRegistryObject;\n",
            "import org.mtr.registry.ObjectHolder;\n")
        text = text.replace(
            "import org.mtr.mod.block.BlockCeiling",
            "import org.mtr.block.BlockCeiling")
        text = text.replace(
            "import org.mtr.mod.block.BlockPlatform",
            "import org.mtr.block.BlockPlatform")
        text = text.replace(
            "import org.mtr.mod.block.BlockPlatformSlab",
            "import org.mtr.block.BlockPlatformSlab")
        text = text.replace(
            "import org.mtr.mod.block.BlockTicketBarrier",
            "import org.mtr.block.BlockTicketBarrier")
        text = text.replace(
            "import org.mtr.mod.item.ItemBlockEnchanted",
            "import org.mtr.item.ItemBlockEnchanted")

    for old, new in IMPORT_REPLACEMENTS:
        text = re.sub(old, new, text)

    for old, new in CONTENT_REPLACEMENTS:
        text = text.replace(old, new)

    # Renderer registration lambdas
    text = re.sub(
        r'RegistryClient\.registerBlockEntityRenderer\(([^,]+), (\w+)::new\)',
        r'RegistryClient.registerBlockEntityRenderer(\1, context -> new \2<>())',
        text)
    text = re.sub(
        r'RegistryClient\.registerEntityRenderer\(([^,]+), (\w+)::new\)',
        r'RegistryClient.registerEntityRenderer(\1, context -> new \2(context))',
        text)

    if text != original:
        path.write_text(text, encoding="utf-8")
        return True
    return False


def main():
    changed = []
    for path in ROOT.rglob("*.java"):
        if migrate_file(path):
            changed.append(str(path.relative_to(ROOT)))
    print(f"Migrated {len(changed)} files")
    for f in sorted(changed):
        print(f"  {f}")


if __name__ == "__main__":
    main()
