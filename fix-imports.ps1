$root = "d:\RongMC_Upadte\porting\Tianjin-Metro\fabric\src\main\java"
$importMap = @{
    'BlockPos' = 'import net.minecraft.core.BlockPos;'
    'Direction' = 'import net.minecraft.core.Direction;'
    'ResourceLocation' = 'import net.minecraft.resources.ResourceLocation;'
    'Component' = 'import net.minecraft.network.chat.Component;'
    'CompoundTag' = 'import net.minecraft.nbt.CompoundTag;'
    'InteractionResult' = 'import net.minecraft.world.InteractionResult;'
    'InteractionHand' = 'import net.minecraft.world.InteractionHand;'
    'Player' = 'import net.minecraft.world.entity.player.Player;'
    'LivingEntity' = 'import net.minecraft.world.entity.LivingEntity;'
    'ServerPlayer' = 'import net.minecraft.server.level.ServerPlayer;'
    'ItemStack' = 'import net.minecraft.world.item.ItemStack;'
    'Item\.' = 'import net.minecraft.world.item.Item;'
    'TooltipFlag' = 'import net.minecraft.world.item.TooltipFlag;'
    'BlockPlaceContext' = 'import net.minecraft.world.item.context.BlockPlaceContext;'
    'BlockGetter' = 'import net.minecraft.world.level.BlockGetter;'
    'LevelReader' = 'import net.minecraft.world.level.LevelReader;'
    'LevelAccessor' = 'import net.minecraft.world.level.LevelAccessor;'
    'ScheduledTickAccess' = 'import net.minecraft.world.level.ScheduledTickAccess;'
    'RandomSource' = 'import net.minecraft.util.RandomSource;'
    'Level ' = 'import net.minecraft.world.level.Level;'
    'EntityBlock' = 'import net.minecraft.world.level.block.EntityBlock;'
    'BlockEntity' = 'import net.minecraft.world.level.block.entity.BlockEntity;'
    'BlockEntityType' = 'import net.minecraft.world.level.block.entity.BlockEntityType;'
    'BlockEntityTicker' = 'import net.minecraft.world.level.block.entity.BlockEntityTicker;'
    'BlockBehaviour' = 'import net.minecraft.world.level.block.state.BlockBehaviour;'
    'BlockState' = 'import net.minecraft.world.level.block.state.BlockState;'
    'StateDefinition' = 'import net.minecraft.world.level.block.state.StateDefinition;'
    'BlockStateProperties' = 'import net.minecraft.world.level.block.state.properties.BlockStateProperties;'
    'BooleanProperty' = 'import net.minecraft.world.level.block.state.properties.BooleanProperty;'
    'IntegerProperty' = 'import net.minecraft.world.level.block.state.properties.IntegerProperty;'
    'EnumProperty' = 'import net.minecraft.world.level.block.state.properties.EnumProperty;'
    'BlockHitResult' = 'import net.minecraft.world.phys.BlockHitResult;'
    'CollisionContext' = 'import net.minecraft.world.phys.shapes.CollisionContext;'
    'Shapes\.' = 'import net.minecraft.world.phys.shapes.Shapes;'
    'VoxelShape' = 'import net.minecraft.world.phys.shapes.VoxelShape;'
    'ChatFormatting' = 'import net.minecraft.ChatFormatting;'
    'SoundSource' = 'import net.minecraft.sounds.SoundSource;'
    'List<' = 'import java.util.List;'
    'Identifier' = 'import net.minecraft.resources.ResourceLocation;'
}

Get-ChildItem -Path $root -Recurse -Filter "*.java" | ForEach-Object {
    $content = [IO.File]::ReadAllText($_.FullName)
    $toAdd = New-Object System.Collections.Generic.List[string]
    foreach ($entry in $importMap.GetEnumerator()) {
        $pattern = $entry.Key
        $imp = $entry.Value
        if ($content -match $pattern -and $content -notmatch [regex]::Escape($imp)) {
            if ($toAdd -notcontains $imp) { $toAdd.Add($imp) | Out-Null }
        }
    }
    if ($toAdd.Count -eq 0) { return }
    $insert = ($toAdd | Sort-Object -Unique) -join "`r`n"
    if ($content -match '(?m)^package [^;]+;\r?\n\r?\n') {
        $content = $content -replace '(?m)^(package [^;]+;\r?\n)\r?\n', "`$1`r`n$insert`r`n"
    } else {
        $content = $content -replace '(?m)^(package [^;]+;\r?\n)', "`$1`r`n$insert`r`n"
    }
    [IO.File]::WriteAllText($_.FullName, $content)
    Write-Host "Imports: $($_.Name)"
}

Write-Host "Done imports."
