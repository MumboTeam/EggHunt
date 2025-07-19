package io.github.mumboteam.egghunt.item;

import eu.pb4.polymer.core.api.item.PolymerItem;
import io.github.mumboteam.egghunt.EggHunt;
import io.github.mumboteam.egghunt.entity.EggEntity;
import io.github.mumboteam.egghunt.registry.ModEntityTypes;
import io.github.mumboteam.egghunt.registry.ModItems;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.FireworkExplosionComponent;
import net.minecraft.component.type.FireworksComponent;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.FireworkRocketEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.item.Items;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import xyz.nucleoid.packettweaker.PacketContext;

import java.util.List;

public class EggSpawner extends Item implements PolymerItem {

    public EggSpawner(Settings settings) {
        super(settings);
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        PlayerEntity user = context.getPlayer();
        ItemStack itemStack = context.getStack();
        World world = context.getWorld();
        EggEntity egg;

        if (user != null) {
            Vec3d pos = context.getHitPos();
            double x = Math.round(pos.x - 0.5) + 0.5;
            double y = Math.floor(pos.y);
            double z = Math.round(pos.z - 0.5) + 0.5;

            if (itemStack.isOf(ModItems.MUMBO_EGG_SPAWNER)) {
                egg = new EggEntity(ModEntityTypes.EGG, world, ModItems.EGG_MUMBO);
            } else {
                egg = new EggEntity(ModEntityTypes.EGG, world);
            }
            egg.refreshPositionAndAngles(x, y, z, 0.0f, 0.0f);
            if (world.spawnEntity(egg)) {
                return ActionResult.SUCCESS;
            } else {
                EggHunt.LOGGER.warn("Failed to spawn egg at " + pos);
                return ActionResult.FAIL;
            }
        }
        return ActionResult.FAIL;
    }

    @Override
    public Item getPolymerItem(ItemStack itemStack, PacketContext context) {
        return ModItems.EGG_1;
    }
}
