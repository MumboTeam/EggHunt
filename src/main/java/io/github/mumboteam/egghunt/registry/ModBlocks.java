package io.github.mumboteam.egghunt.registry;

import io.github.mumboteam.egghunt.EggHunt;
import io.github.mumboteam.egghunt.block.EggBlock;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;

import java.util.function.Function;

public class ModBlocks {
    public static final Block EGG_1 = register("egg1", EggBlock::new);
    public static final Block EGG_1_1 = register("egg11", EggBlock::new);
    public static final Block EGG_1_2 = register("egg12", EggBlock::new);
    public static final Block EGG_1_3 = register("egg13", EggBlock::new);
    public static final Block EGG_2 = register("egg2", EggBlock::new);
    public static final Block EGG_2_1 = register("egg21", EggBlock::new);
    public static final Block EGG_2_2 = register("egg22", EggBlock::new);
    public static final Block EGG_2_3 = register("egg23", EggBlock::new);
    public static final Block EGG_3 = register("egg3", EggBlock::new);
    public static final Block EGG_3_1 = register("egg31", EggBlock::new);
    public static final Block EGG_3_2 = register("egg32", EggBlock::new);
    public static final Block EGG_3_3 = register("egg33", EggBlock::new);
    public static final Block EGG_MUMBO = register("eggmumbo", EggBlock::new);

    public static Block register(String path, Function<AbstractBlock.Settings, Block> function) {
        Identifier id = Identifier.of(EggHunt.ID, path);
        Block block = function.apply(AbstractBlock.Settings.create().registryKey(RegistryKey.of(RegistryKeys.BLOCK, id)));

        return Registry.register(Registries.BLOCK, id, block);
    }

    public static void initialize() {}
}