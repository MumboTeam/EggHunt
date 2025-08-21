package io.github.mumboteam.flagparticipation.entity;

import eu.pb4.factorytools.api.virtualentity.ItemDisplayElementUtil;
import eu.pb4.polymer.core.api.entity.PolymerEntity;
import eu.pb4.polymer.virtualentity.api.ElementHolder;
import eu.pb4.polymer.virtualentity.api.attachment.EntityAttachment;
import eu.pb4.polymer.virtualentity.api.elements.ItemDisplayElement;
import eu.pb4.polymer.virtualentity.mixin.accessors.InteractionEntityAccessor;
import io.github.mumboteam.flagparticipation.registry.ModItems;
import io.github.mumboteam.flagparticipation.utils.FlagState;
import io.github.mumboteam.flagparticipation.utils.PlayerData;
import io.github.mumboteam.flagparticipation.utils.TickScheduler;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.packet.s2c.play.EntitiesDestroyS2CPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import xyz.nucleoid.packettweaker.PacketContext;

import java.util.List;

public class FlagEntity extends Entity implements PolymerEntity {
    private final ItemDisplayElement flagDisplay;
    private ElementHolder elementHolder;

    public FlagEntity(EntityType<? extends FlagEntity> type, World world) {
        super(type, world);
        this.flagDisplay = ItemDisplayElementUtil.createSimple(ModItems.FLAG_SPAWNER);
        flagDisplay.setOffset(new Vec3d(0, 0.5, 0));
    }

    @Override
    public void tick() {
        super.tick();

        if (elementHolder == null) {
            elementHolder = new ElementHolder();
            EntityAttachment.of(elementHolder, this);

            elementHolder.addElement(this.flagDisplay);
        }

        FlagState state = FlagState.getServerState(getWorld().getServer());
        if (!state.flagsIds.contains(this.uuid)) {
            state.flagsIds.add(this.uuid);
            state.markDirty();
        }

        elementHolder.tick();
    }

    @Override
    public ActionResult interact(PlayerEntity player, Hand hand) {
        FlagState state = FlagState.getServerState(player.getServer());
        PlayerData playerData = state.playerData.get(player.getUuid());
        if (playerData == null || !playerData.isAdmin()) { // If the player is not in the system, they cannot be an admin
            player.sendMessage(Text.translatable("text.flagparticipation.collected").setStyle(Style.EMPTY.withColor(Formatting.AQUA)), true);
            if (playerData != null) {
                playerData.addToFoundFlags(this.uuid);
                playerData.increaseFlagCount(1);
            } else {
                PlayerData data = new PlayerData();
                data.addToFoundFlags(this.uuid);
                data.increaseFlagCount(1);
                state.playerData.put(player.getUuid(), data);
            }
            hideFromClient((ServerPlayerEntity) player);
        } else {
            state.flagsIds.remove(this.uuid);
            this.discard();
            player.sendMessage(Text.translatable("text.flagparticipation.admin_removed"), true);
        }
        state.markDirty();
        return ActionResult.SUCCESS;
    }

    @Override
    public EntityType<?> getPolymerEntityType(PacketContext ctx) {
        return EntityType.INTERACTION;
    }

    // Fix the hitbox size (client-side)
    public void modifyRawTrackedData(List<DataTracker.SerializedEntry<?>> data, ServerPlayerEntity player, boolean initial) {
        data.add(DataTracker.SerializedEntry.of(InteractionEntityAccessor.getWIDTH(), 0.5f));
        data.add(DataTracker.SerializedEntry.of(InteractionEntityAccessor.getHEIGHT(), 2f));
    }

    @Override
    public void onStartedTrackingBy(ServerPlayerEntity player) {
        FlagState state = FlagState.getServerState(player.getServer());
        PlayerData playerData = state.playerData.get(player.getUuid());
        if (playerData != null && playerData.getFoundFlags().contains(this.uuid)) {
            // Wait a few ticks so the client can process that the entity exists - Very Hacky
            TickScheduler.schedule(() -> hideFromClient(player), 10);
        }
    }

    private void hideFromClient(ServerPlayerEntity player) {
        FlagState state = FlagState.getServerState(this.getServer());
        if (!state.playerData.get(player.getUuid()).isAdmin()) {
            player.networkHandler.sendPacket(
                    new EntitiesDestroyS2CPacket(this.getId())
            );
            // Apparently the element display has its own entity ID, and isn't removed automatically when the parent is removed
            player.networkHandler.sendPacket(
                    new EntitiesDestroyS2CPacket(this.flagDisplay.getEntityId())
            );
        }
    }

    @Override
    protected void initDataTracker(DataTracker.Builder builder) {
    }

    @Override
    public boolean damage(ServerWorld world, DamageSource source, float amount) {
        return false;
    }

    @Override
    protected void readCustomData(ReadView view) {
    }

    @Override
    protected void writeCustomData(WriteView view) {
    }
}