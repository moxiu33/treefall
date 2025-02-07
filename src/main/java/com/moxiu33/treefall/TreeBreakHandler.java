package com.moxiu33.treefall;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.HashSet;
import java.util.Set;

@Mod.EventBusSubscriber(modid = TreeFallMod.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class TreeBreakHandler {

    @SubscribeEvent
    public static void onBlockBreak(BlockEvent.BreakEvent event) {
        Level world = (Level) event.getLevel();
        BlockPos pos = event.getPos();
        BlockState state = world.getBlockState(pos);

        // Check if the broken block is a log
        if (state.getBlock() instanceof RotatedPillarBlock) {
            // Trigger tree falling logic
            breakTree(world, pos, new HashSet<>());
        }
    }

    private static void breakTree(Level world, BlockPos pos, Set<BlockPos> processed) {
        // Avoid processing the same block multiple times
        if (processed.contains(pos)) {
            return;
        }
        processed.add(pos);

        BlockState state = world.getBlockState(pos);

        // Break the current block if it's a log or leaves
        if (state.getBlock() instanceof RotatedPillarBlock || state.getBlock() instanceof LeavesBlock) {
            world.destroyBlock(pos, true); // Drops items

            // Recursively break adjacent blocks
            for (int x = -1; x <= 1; x++) {
                for (int y = -1; y <= 1; y++) {
                    for (int z = -1; z <= 1; z++) {
                        if (x == 0 && y == 0 && z == 0) continue; // Skip the current block
                        BlockPos newPos = pos.offset(x, y, z);
                        breakTree(world, newPos, processed);
                    }
                }
            }
        }
    }
}