package io.github.mumboteam.egghunt.registry;

import eu.pb4.polymer.core.api.entity.PolymerEntityUtils;
import io.github.mumboteam.egghunt.EggHunt;
import io.github.mumboteam.egghunt.entity.BunnyEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;

public class ModEntityTypes {
    public static final EntityType<BunnyEntity> BUNNY = register(
            "bunny",
            EntityType.Builder.create(BunnyEntity::new, SpawnGroup.MISC).dimensions(0.6F, 1.8F)
    );

    public static <T extends Entity> EntityType<T> register(String path, EntityType.Builder<T> builder) {
        Identifier id = Identifier.of(EggHunt.ID, path);
        EntityType<T> entityType =  Registry.register(Registries.ENTITY_TYPE, id, builder.build(RegistryKey.of(RegistryKeys.ENTITY_TYPE, id)));
        PolymerEntityUtils.registerType(entityType);
        return entityType;
    }

    public static void initialize() {
    }
}
