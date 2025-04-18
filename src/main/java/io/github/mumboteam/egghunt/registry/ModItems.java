package io.github.mumboteam.egghunt.registry;

import eu.pb4.polymer.core.api.item.PolymerBlockItem;
import eu.pb4.polymer.core.api.item.PolymerItemGroupUtils;
import io.github.mumboteam.egghunt.EggHunt;
import net.minecraft.item.*;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.function.Function;

public class ModItems {
    private ModItems() {
    }

    public static final Item EGG_1 = registerEgg("egg1", (settings) -> new PolymerBlockItem(ModBlocks.EGG_1, settings));
    public static final Item EGG_1_1 = registerEgg("egg11", (settings) -> new PolymerBlockItem(ModBlocks.EGG_1_1, settings));
    public static final Item EGG_1_2 = registerEgg("egg12", (settings) -> new PolymerBlockItem(ModBlocks.EGG_1_2, settings));
    public static final Item EGG_1_3 = registerEgg("egg13", (settings) -> new PolymerBlockItem(ModBlocks.EGG_1_3, settings));
    public static final Item EGG_2 = registerEgg("egg2", (settings) -> new PolymerBlockItem(ModBlocks.EGG_2, settings));
    public static final Item EGG_2_1 = registerEgg("egg21", (settings) -> new PolymerBlockItem(ModBlocks.EGG_2_1, settings));
    public static final Item EGG_2_2 = registerEgg("egg22", (settings) -> new PolymerBlockItem(ModBlocks.EGG_2_2, settings));
    public static final Item EGG_2_3 = registerEgg("egg23", (settings) -> new PolymerBlockItem(ModBlocks.EGG_2_3, settings));
    public static final Item EGG_3 = registerEgg("egg3", (settings) -> new PolymerBlockItem(ModBlocks.EGG_3, settings));
    public static final Item EGG_3_1 = registerEgg("egg31", (settings) -> new PolymerBlockItem(ModBlocks.EGG_3_1, settings));
    public static final Item EGG_3_2 = registerEgg("egg32", (settings) -> new PolymerBlockItem(ModBlocks.EGG_3_2, settings));
    public static final Item EGG_3_3 = registerEgg("egg33", (settings) -> new PolymerBlockItem(ModBlocks.EGG_3_3, settings));
    public static final Item EGG_MUMBO = registerEgg("eggmumbo", (settings) -> new PolymerBlockItem(ModBlocks.EGG_MUMBO, settings));

    public static Item register(String path, Function<Item.Settings, Item> function) {
        Identifier id = Identifier.of(EggHunt.ID, path);
        Item item = function.apply(new Item.Settings().registryKey(RegistryKey.of(RegistryKeys.ITEM, id)));
        return Registry.register(Registries.ITEM, id, item);
    }

    public static Item registerEgg(String path, Function<Item.Settings, Item> function) {
        Identifier id = Identifier.of(EggHunt.ID, path);
        Item item = function.apply(new Item.Settings().registryKey(RegistryKey.of(RegistryKeys.ITEM, id)).translationKey("item.egghunt.egg").maxCount(1));
        return Registry.register(Registries.ITEM, id, item);
    }

    public static void initialize() {
        PolymerItemGroupUtils.registerPolymerItemGroup(Identifier.of(EggHunt.ID, "items"), ItemGroup.create(ItemGroup.Row.BOTTOM, -1)
                .icon(() -> new ItemStack(EGG_1))
                .displayName(Text.translatable("itemGroup.egghunt"))
                .entries(((context, entries) -> {
                    entries.add(EGG_1);
                    entries.add(EGG_1_1);
                    entries.add(EGG_1_2);
                    entries.add(EGG_1_3);
                    entries.add(EGG_2);
                    entries.add(EGG_2_1);
                    entries.add(EGG_2_2);
                    entries.add(EGG_2_3);
                    entries.add(EGG_3);
                    entries.add(EGG_3_1);
                    entries.add(EGG_3_2);
                    entries.add(EGG_3_3);
                    entries.add(EGG_MUMBO);
                })).build()
        );
    }
}
