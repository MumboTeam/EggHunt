package io.github.mumboteam.flagparticipation.registry;

import eu.pb4.polymer.core.api.item.PolymerItemGroupUtils;
import io.github.mumboteam.flagparticipation.FlagParticipation;
import io.github.mumboteam.flagparticipation.item.FlagSpawner;
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

    public static final Item FLAG_SPAWNER = register("flag", (FlagSpawner::new));

    public static Item register(String path, Function<Item.Settings, Item> function) {
        Identifier id = Identifier.of(FlagParticipation.ID, path);
        Item item = function.apply(new Item.Settings().registryKey(RegistryKey.of(RegistryKeys.ITEM, id)));
        return Registry.register(Registries.ITEM, id, item);
    }

    public static void initialize() {
        PolymerItemGroupUtils.registerPolymerItemGroup(Identifier.of(FlagParticipation.ID, "items"), ItemGroup.create(ItemGroup.Row.BOTTOM, -1)
                .icon(() -> new ItemStack(FLAG_SPAWNER))
                .displayName(Text.translatable("itemGroup.flagparticipation"))
                .entries(((context, entries) -> {
                    entries.add(FLAG_SPAWNER);
                })).build()
        );
    }
}
