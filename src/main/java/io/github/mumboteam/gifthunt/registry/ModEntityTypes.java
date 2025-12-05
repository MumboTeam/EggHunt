package io.github.mumboteam.gifthunt.registry;

import eu.pb4.polymer.core.api.entity.PolymerEntityUtils;
import io.github.mumboteam.gifthunt.GiftHunt;
import io.github.mumboteam.gifthunt.entity.SnowmanEntity;
import io.github.mumboteam.gifthunt.entity.GiftEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;

public class ModEntityTypes {
    public static final EntityType<SnowmanEntity> SNOWMAN = register(
            "snowman",
            EntityType.Builder.create(SnowmanEntity::new, SpawnGroup.MISC)
    );

    public static final EntityType<GiftEntity> GIFT = register(
            "gift",
            EntityType.Builder.create(GiftEntity::new, SpawnGroup.MISC)
    );

    public static <T extends Entity> EntityType<T> register(String path, EntityType.Builder<T> builder) {
        Identifier id = Identifier.of(GiftHunt.ID, path);
        EntityType<T> entityType =  Registry.register(Registries.ENTITY_TYPE, id, builder.build(RegistryKey.of(RegistryKeys.ENTITY_TYPE, id)));
        PolymerEntityUtils.registerType(entityType);
        return entityType;
    }

    public static void initialize() {
    }
}
