package io.github.mumboteam.flagparticipation.item;

import eu.pb4.polymer.core.api.item.PolymerItem;
import io.github.mumboteam.flagparticipation.FlagParticipation;
import io.github.mumboteam.flagparticipation.entity.FlagEntity;
import io.github.mumboteam.flagparticipation.registry.ModEntityTypes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.item.Items;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import xyz.nucleoid.packettweaker.PacketContext;

public class FlagSpawner extends Item implements PolymerItem {

    public FlagSpawner(Settings settings) {
        super(settings);
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        PlayerEntity user = context.getPlayer();
        World world = context.getWorld();
        FlagEntity egg;

        if (user != null) {
            Vec3d pos = context.getHitPos();
            double x = Math.round(pos.x - 0.5) + 0.5;
            double y = Math.floor(pos.y);
            double z = Math.round(pos.z - 0.5) + 0.5;

            egg = new FlagEntity(ModEntityTypes.FLAG, world);
            egg.refreshPositionAndAngles(x, y, z, 0.0f, 0.0f);
            if (world.spawnEntity(egg)) {
                return ActionResult.SUCCESS;
            } else {
                FlagParticipation.LOGGER.warn("Failed to spawn flag at " + pos);
                return ActionResult.FAIL;
            }
        }
        return ActionResult.FAIL;
    }

    @Override
    public Item getPolymerItem(ItemStack itemStack, PacketContext context) {
        return Items.PAPER;
    }
}
