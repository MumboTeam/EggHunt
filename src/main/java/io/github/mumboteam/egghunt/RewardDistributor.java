package io.github.mumboteam.egghunt;

import io.github.mumboteam.egghunt.utils.EggHuntState;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class RewardDistributor {
    public static void spawnFor(PlayerEntity player, ItemConvertible item, int count) {
        World world = player.getWorld();
        double x = player.getX(), y = player.getY() + 1, z = player.getZ();
        ItemStack stack = new ItemStack(item, count);
        ItemEntity drop = new ItemEntity(world, x, y, z, stack);
        world.spawnEntity(drop);
        EggHuntState state = EggHuntState.getServerState(player.getServer());
        state.totalDiamonds += count;
    }
}
