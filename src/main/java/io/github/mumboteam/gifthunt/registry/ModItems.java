package io.github.mumboteam.gifthunt.registry;

import eu.pb4.polymer.core.api.item.PolymerBlockItem;
import eu.pb4.polymer.core.api.item.PolymerItemGroupUtils;
import io.github.mumboteam.gifthunt.GiftHunt;
import io.github.mumboteam.gifthunt.item.GiftSpawner;
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

    public static final Item GIFT_1 = registerGift("gift1", (settings) -> new PolymerBlockItem(ModBlocks.GIFT_1, settings));
    public static final Item GIFT_2 = registerGift("gift2", (settings) -> new PolymerBlockItem(ModBlocks.GIFT_2, settings));
    public static final Item GIFT_3 = registerGift("gift3", (settings) -> new PolymerBlockItem(ModBlocks.GIFT_3, settings));
    public static final Item GIFT_4 = registerGift("gift4", (settings) -> new PolymerBlockItem(ModBlocks.GIFT_4, settings));
    public static final Item GIFT_5 = registerGift("gift5", (settings) -> new PolymerBlockItem(ModBlocks.GIFT_5, settings));
    public static final Item GIFT_6 = registerGift("gift6", (settings) -> new PolymerBlockItem(ModBlocks.GIFT_6, settings));
    public static final Item GIFT_7 = registerGift("gift7", (settings) -> new PolymerBlockItem(ModBlocks.GIFT_7, settings));
    public static final Item GIFT_SHINY = registerGift("giftshiny", (settings) -> new PolymerBlockItem(ModBlocks.GIFT_SHINY, settings));

    public static final Item GIFT_SPAWNER = register("gift_spawner", (GiftSpawner::new));
    public static final Item SHINY_GIFT_SPAWNER = register("shiny_gift_spawner", (GiftSpawner::new));

    public static Item register(String path, Function<Item.Settings, Item> function) {
        Identifier id = Identifier.of(GiftHunt.ID, path);
        Item item = function.apply(new Item.Settings().registryKey(RegistryKey.of(RegistryKeys.ITEM, id)));
        return Registry.register(Registries.ITEM, id, item);
    }

    public static Item registerGift(String path, Function<Item.Settings, Item> function) {
        Identifier id = Identifier.of(GiftHunt.ID, path);
        Item item = function.apply(new Item.Settings().registryKey(RegistryKey.of(RegistryKeys.ITEM, id)).translationKey("item.gifthunt.gift").maxCount(1));
        return Registry.register(Registries.ITEM, id, item);
    }

    public static void initialize() {
        PolymerItemGroupUtils.registerPolymerItemGroup(Identifier.of(GiftHunt.ID, "items"), ItemGroup.create(ItemGroup.Row.BOTTOM, -1)
                .icon(() -> new ItemStack(GIFT_1))
                .displayName(Text.translatable("itemGroup.gifthunt"))
                .entries(((context, entries) -> {
                    entries.add(GIFT_1);
                    entries.add(GIFT_2);
                    entries.add(GIFT_3);
                    entries.add(GIFT_4);
                    entries.add(GIFT_5);
                    entries.add(GIFT_6);
                    entries.add(GIFT_7);
                    entries.add(GIFT_SHINY);
                    entries.add(GIFT_SPAWNER);
                    entries.add(SHINY_GIFT_SPAWNER);
                })).build()
        );
    }
}
