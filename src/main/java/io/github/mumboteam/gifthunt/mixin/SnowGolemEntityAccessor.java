package io.github.mumboteam.gifthunt.mixin;

import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.passive.SnowGolemEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(SnowGolemEntity.class)
public interface SnowGolemEntityAccessor {
    @Accessor("SNOW_GOLEM_FLAGS")
    static TrackedData<Byte> getSnowGolemFlags() {
        throw new AssertionError();
    }
}
