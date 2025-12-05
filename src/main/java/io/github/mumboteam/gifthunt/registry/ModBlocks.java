package io.github.mumboteam.gifthunt.registry;

import io.github.mumboteam.gifthunt.GiftHunt;
import io.github.mumboteam.gifthunt.block.GiftBlock;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;

import java.util.function.Function;

public class ModBlocks {
    public static final Block GIFT_1 = register("gift1", GiftBlock::new);
    public static final Block GIFT_2 = register("gift2", GiftBlock::new);
    public static final Block GIFT_3 = register("gift3", GiftBlock::new);
    public static final Block GIFT_4 = register("gift4", GiftBlock::new);
    public static final Block GIFT_5 = register("gift5", GiftBlock::new);
    public static final Block GIFT_6 = register("gift6", GiftBlock::new);
    public static final Block GIFT_7 = register("gift7", GiftBlock::new);
    public static final Block GIFT_SHINY = register("giftshiny", GiftBlock::new);

    public static Block register(String path, Function<AbstractBlock.Settings, Block> function) {
        Identifier id = Identifier.of(GiftHunt.ID, path);
        Block block = function.apply(AbstractBlock.Settings.create().registryKey(RegistryKey.of(RegistryKeys.BLOCK, id)));

        return Registry.register(Registries.BLOCK, id, block);
    }

    public static void initialize() {}
}