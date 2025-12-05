package io.github.mumboteam.gifthunt.item;

import eu.pb4.polymer.core.api.item.PolymerItem;
import io.github.mumboteam.gifthunt.GiftHunt;
import io.github.mumboteam.gifthunt.entity.GiftEntity;
import io.github.mumboteam.gifthunt.registry.ModEntityTypes;
import io.github.mumboteam.gifthunt.registry.ModItems;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import xyz.nucleoid.packettweaker.PacketContext;

public class GiftSpawner extends Item implements PolymerItem {

    public GiftSpawner(Settings settings) {
        super(settings);
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        PlayerEntity user = context.getPlayer();
        ItemStack itemStack = context.getStack();
        World world = context.getWorld();
        GiftEntity gift;

        if (user != null) {
            Vec3d pos = context.getHitPos();
            double x = Math.round(pos.x - 0.5) + 0.5;
            double y = Math.floor(pos.y);
            double z = Math.round(pos.z - 0.5) + 0.5;

            if (itemStack.isOf(ModItems.SHINY_GIFT_SPAWNER)) {
                gift = new GiftEntity(ModEntityTypes.GIFT, world, ModItems.GIFT_SHINY);
            } else {
                gift = new GiftEntity(ModEntityTypes.GIFT, world);
            }
            gift.refreshPositionAndAngles(x, y, z, 0.0f, 0.0f);
            if (world.spawnEntity(gift)) {
                return ActionResult.SUCCESS;
            } else {
                GiftHunt.LOGGER.warn("Failed to spawn gift at " + pos);
                return ActionResult.FAIL;
            }
        }
        return ActionResult.FAIL;
    }

    @Override
    public Item getPolymerItem(ItemStack itemStack, PacketContext context) {
        return ModItems.GIFT_1;
    }
}
