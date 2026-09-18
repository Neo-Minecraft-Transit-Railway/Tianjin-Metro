$root = "d:\RongMC_Upadte\porting\Tianjin-Metro\fabric\src\main\java"
$files = Get-ChildItem -Path $root -Recurse -Filter "*.java"

$standardImports = @(
    "import net.minecraft.core.BlockPos;",
    "import net.minecraft.core.Direction;",
    "import net.minecraft.world.level.BlockGetter;",
    "import net.minecraft.world.level.Level;",
    "import net.minecraft.world.level.block.Block;",
    "import net.minecraft.world.level.block.state.BlockBehaviour;",
    "import net.minecraft.world.level.block.state.BlockState;",
    "import net.minecraft.world.level.block.state.StateDefinition;",
    "import net.minecraft.world.level.block.state.properties.BlockStateProperties;",
    "import net.minecraft.world.phys.shapes.CollisionContext;",
    "import net.minecraft.world.phys.shapes.Shapes;",
    "import net.minecraft.world.phys.shapes.VoxelShape;"
)

foreach ($file in $files) {
    $content = [System.IO.File]::ReadAllText($file.FullName)
    $original = $content

    $content = $content -replace 'createDefaultBlockBehaviour', 'createDefaultBlockSettings'
    $content = $content -replace 'BooleanProperty\.of\(', 'BooleanProperty.create('
    $content = $content -replace 'IntegerProperty\.of\(', 'IntegerProperty.create('
    $content = $content -replace 'EnumProperty\.of\(', 'EnumProperty.create('
    $content = $content -replace 'VoxelShapes\.union', 'Shapes.or'
    $content = $content -replace '\.setBlockState\(', '.setBlock('
    $content = $content -replace '\.getBlockState\(', '.getBlockState('
    $content = $content -replace 'getDefaultState\(\)', 'defaultBlockState()'
    $content = $content -replace 'getDefaultState2\(\)', 'defaultBlockState()'
    $content = $content -replace 'ctx\.getWorld\(\)', 'ctx.getLevel()'
    $content = $content -replace 'ctx\.getBlockPos\(\)', 'ctx.getClickedPos()'
    $content = $content -replace 'ctx\.getPlayerFacing\(\)', 'ctx.getHorizontalDirection()'
    $content = $content -replace 'ItemPlacementContext', 'BlockPlaceContext'
    $content = $content -replace 'TooltipContext', 'Item.TooltipContext'
    $content = $content -replace 'TextFormatting', 'ChatFormatting'
    $content = $content -replace 'import net\.minecraft\.util\.Formatting;', 'import net.minecraft.ChatFormatting;'
    $content = $content -replace 'import static org\.mtr\.mapping\.mapper\.DirectionHelper\.FACING;', 'import net.minecraft.world.level.block.state.properties.BlockStateProperties;'
    $content = $content -replace 'import org\.mtr\.mapping\.mapper\.DirectionHelper;', 'import net.minecraft.world.level.block.state.properties.BlockStateProperties;'
    $content = $content -replace 'DirectionHelper\.FACING', 'BlockStateProperties.HORIZONTAL_FACING'
    $content = $content -replace 'IBlock\.getStatePropertySafe\(([^,]+),\s*FACING\)', 'IBlock.getStatePropertySafe($1, BlockStateProperties.HORIZONTAL_FACING)'
    $content = $content -replace 'new Property<>\(FACING\.data\),\s*([^)]+)\.data\)', 'BlockStateProperties.HORIZONTAL_FACING, $1)'
    $content = $content -replace 'new Property<>\(HALF\.data\),\s*([^)]+)\)', 'IBlock.HALF, $1)'
    $content = $content -replace 'new Property<>\(THIRD\.data\),\s*([^)]+)\)', 'IBlock.THIRD, $1)'
    $content = $content -replace 'new Property<>\(OPEN\.data\),\s*([^)]+)\)', 'OPEN, $1)'
    $content = $content -replace 'new Property<>\(SIDE\.data\),\s*([^)]+)\)', 'IBlock.SIDE, $1)'
    $content = $content -replace 'new Property<>\(SIDE_EXTENDED\.data\),\s*([^)]+)\)', 'IBlock.SIDE_EXTENDED, $1)'
    $content = $content -replace 'new Property<>\(ARROW_LEFT\.data\)', 'ARROW_LEFT'
    $content = $content -replace 'new Property<>\(ARROW_DIRECTION\.data\)', 'ARROW_DIRECTION'
    $content = $content -replace 'new Property<>\(EOS\.data\)', 'EOS'
    $content = $content -replace 'new Property<>\(STYLE\.data\)', 'STYLE'
    $content = $content -replace 'new Property<>\(SCALE\.data\)', 'SCALE'
    $content = $content -replace 'new Property<>\(COLOR\.data\)', 'COLOR'
    $content = $content -replace 'new Property<>\(POWERED\.data\)', 'POWERED'
    $content = $content -replace 'new Property<>\(LOCKED\.data\)', 'LOCKED'
    $content = $content -replace 'new Property<>\(LIGHT\.data\)', 'LIGHT'
    $content = $content -replace 'new Property<>\(SHOULD_RENDER\.data\)', 'SHOULD_RENDER'
    $content = $content -replace 'new Property<>\(TYPE\.data\)', 'TYPE'
    $content = $content -replace 'new Property<>\(BOTTOM\.data\)', 'BOTTOM'
    $content = $content -replace 'new Property<>\(CHANGED\.data\)', 'CHANGED'
    $content = $content -replace 'new Property<>\(FACING_DOUBLE\.data\)', 'FACING_DOUBLE'
    $content = $content -replace 'properties\.add\(FACING\)', 'builder.add(BlockStateProperties.HORIZONTAL_FACING)'
    $content = $content -replace 'facing\.rotateYClockwise\(\)', 'facing.getClockWise()'
    $content = $content -replace 'facing\.rotateYCounterclockwise\(\)', 'facing.getCounterClockWise()'
    $content = $content -replace 'direction\.rotateYClockwise\(\)', 'direction.getClockWise()'
    $content = $content -replace 'direction\.rotateYCounterclockwise\(\)', 'direction.getCounterClockWise()'
    $content = $content -replace 'pos\.offset\(', 'pos.relative('
    $content = $content -replace 'pos\.up\(\)', 'pos.above()'
    $content = $content -replace 'pos\.down\(\)', 'pos.below()'
    $content = $content -replace 'pos\.up\((\d+)\)', 'pos.above($1)'
    $content = $content -replace 'pos\.down\((\d+)\)', 'pos.below($1)'
    $content = $content -replace 'getCollisionShape2\(', 'getCollisionShape('
    $content = $content -replace 'markRemoved2\(\)', 'setRemoved()'
    $content = $content -replace 'blockEntityTick\(\)', 'serverTick()'
    $content = $content -replace 'org\.mtr\.mapping\.holder\.Blocks\.getAirMapped\(\)', 'net.minecraft.world.level.block.Blocks.AIR'
    $content = $content -replace 'Blocks\.getAirMapped\(\)', 'net.minecraft.world.level.block.Blocks.AIR'
    $content = $content -replace 'new Entity\(([^)]+)\)', '$1'
    $content = $content -replace 'world\.spawnEntity\(', 'world.addFreshEntity('
    $content = $content -replace 'entity\.interact\(player\.data, hand\.data\)', 'entity.interact(player, InteractionHand.MAIN_HAND)'
    $content = $content -replace 'BlockList\.([A-Z_0-9]+)\.get\(\)\.data', 'BlockList.$1.get()'
    $content = $content -replace 'ItemList\.([A-Z_0-9]+)\.get\(\)\.data', 'ItemList.$1.get()'
    $content = $content -replace 'Items\.([A-Z_0-9]+)\.get\(\)\.data', 'Items.$1.get()'
    $content = $content -replace 'world\.getBlockEntity\(([^)]+)\)\.data', 'world.getBlockEntity($1)'
    $content = $content -replace 'item\.data == ([^;]+)\.get\(\)\.data', 'item == $1.get()'
    $content = $content -replace 'new LevelAccessor\(world\.data\)', 'world'
    $content = $content -replace 'new Identifier\(', 'ResourceLocation.fromNamespaceAndPath('
    $content = $content -replace 'newBlockEntity\(BlockPos blockPos, BlockState blockState\)', 'newBlockEntity(BlockPos blockPos, BlockState blockState)'
    $content = $content -replace 'public void addBlockProperties\(List<HolderBase<\?>> properties\) \{', '@Override`r`n    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {'
    $content = $content -replace 'properties\.add\(', 'builder.add('

    if ($content -match 'extends Block' -or $content -match 'implements IBlock') {
        foreach ($imp in $standardImports) {
            if ($content -notmatch [regex]::Escape($imp)) {
                if ($content -match 'import org\.mtr\.') {
                    $content = $content -replace '(import org\.mtr\.[^\r\n]+;\r?\n)', "`$1$imp`r`n"
                    break
                }
            }
        }
    }

    if ($content -ne $original) {
        [System.IO.File]::WriteAllText($file.FullName, $content)
        Write-Host "Updated: $($file.Name)"
    }
}

Write-Host "Done."
