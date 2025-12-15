package io.github.mumboteam.gifthunt.entity;

import eu.pb4.factorytools.api.virtualentity.ItemDisplayElementUtil;
import eu.pb4.polymer.core.api.entity.PolymerEntity;
import eu.pb4.polymer.virtualentity.api.ElementHolder;
import eu.pb4.polymer.virtualentity.api.attachment.EntityAttachment;
import eu.pb4.polymer.virtualentity.api.elements.ItemDisplayElement;
import eu.pb4.polymer.virtualentity.mixin.accessors.InteractionEntityAccessor;
import io.github.mumboteam.gifthunt.GiftHunt;
import io.github.mumboteam.gifthunt.registry.ModItems;
import io.github.mumboteam.gifthunt.utils.GiftHuntState;
import io.github.mumboteam.gifthunt.utils.PlayerData;
import io.github.mumboteam.gifthunt.utils.RewardDistributor;
import io.github.mumboteam.gifthunt.utils.TickScheduler;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.network.packet.s2c.play.EntitiesDestroyS2CPacket;
import net.minecraft.registry.Registries;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import xyz.nucleoid.packettweaker.PacketContext;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class GiftEntity extends Entity implements PolymerEntity {
    private final Set<Item> VARIANTS = new HashSet<>() {{
        add(ModItems.GIFT_1);
        add(ModItems.GIFT_2);
        add(ModItems.GIFT_3);
        add(ModItems.GIFT_4);
        add(ModItems.GIFT_5);
        add(ModItems.GIFT_6);
        add(ModItems.GIFT_7);
    }};

    private Item gift;
    private ItemDisplayElement giftDisplay;
    private ElementHolder elementHolder;

    public GiftEntity(EntityType<? extends GiftEntity> type, World world) {
        super(type, world);
        GiftHunt.LOGGER.info("Gift created without variant");
        this.gift = randomGift(VARIANTS);
        this.giftDisplay = ItemDisplayElementUtil.createSimple(this.gift);
        this.giftDisplay.setOffset(new Vec3d(0, 0.5, 0));
    }

    public GiftEntity(EntityType<? extends GiftEntity> type, World world, Item variant) {
        super(type, world);
        GiftHunt.LOGGER.info("Gift created with variant");
        this.gift = variant;
        this.giftDisplay = ItemDisplayElementUtil.createSimple(this.gift);
    }

    @Override
    public void tick() {
        super.tick();

        if (elementHolder == null) {
            elementHolder = new ElementHolder();
            EntityAttachment.of(elementHolder, this);

            elementHolder.addElement(this.giftDisplay);
        }

        GiftHuntState state = GiftHuntState.getServerState(getEntityWorld().getServer());
        if (!state.giftIds.contains(this.uuid)) {
            state.giftIds.add(this.uuid);
            state.markDirty();
        }

        elementHolder.tick();
    }

    @Override
    public ActionResult interact(PlayerEntity player, Hand hand) {
        GiftHuntState state = GiftHuntState.getServerState(player.getEntityWorld().getServer());
        PlayerData playerData = state.playerData.get(player.getUuid());
        if (playerData != null && playerData.getFoundGifts().contains(this.uuid)) return ActionResult.FAIL;
        if (playerData == null || !playerData.isAdmin()) {
            player.sendMessage(Text.translatable("text.gifthunt.collected").setStyle(Style.EMPTY.withColor(Formatting.AQUA)), true);
            if (playerData != null) {
                playerData.addToFoundGifts(this.uuid);
            } else {
                PlayerData data = new PlayerData();
                data.addToFoundGifts(this.uuid);
                state.playerData.put(player.getUuid(), data);
            }
            state.markDirty();
            hideFromClient((ServerPlayerEntity) player);

            RewardDistributor.spawnFor(player, gift, 1);
        } else {
            state.giftIds.remove(this.uuid);
            this.discard();
        }
        return ActionResult.SUCCESS;
    }

    @Override
    public EntityType<?> getPolymerEntityType(PacketContext ctx) {
        return EntityType.INTERACTION;
    }

    @Override
    public void onStartedTrackingBy(ServerPlayerEntity player) {
        GiftHuntState state = GiftHuntState.getServerState(player.getEntityWorld().getServer());
        PlayerData playerData = state.playerData.get(player.getUuid());
        if (playerData != null && playerData.getFoundGifts().contains(this.uuid)) {
            // Wait a few ticks so the client can process that the entity exists
            TickScheduler.schedule(() -> hideFromClient(player), 5);
        }
    }

    public void modifyRawTrackedData(List<DataTracker.SerializedEntry<?>> data, ServerPlayerEntity player, boolean initial) {
        data.add(DataTracker.SerializedEntry.of(InteractionEntityAccessor.getWIDTH(), 0.5f));
        data.add(DataTracker.SerializedEntry.of(InteractionEntityAccessor.getHEIGHT(), 0.5f));
    }

    private void hideFromClient(ServerPlayerEntity player) {
        GiftHuntState state = GiftHuntState.getServerState(player.getEntityWorld().getServer());
        if (!state.playerData.get(player.getUuid()).isAdmin()) {
            player.networkHandler.sendPacket(
                    new EntitiesDestroyS2CPacket(this.getId())
            );
            player.networkHandler.sendPacket(
                    new EntitiesDestroyS2CPacket(this.giftDisplay.getEntityId())
            );
        }
    }

    private static Item randomGift(Set<Item> items) {
        Random rnd = ThreadLocalRandom.current();
        int target = rnd.nextInt(items.size());
        Iterator<Item> it = items.iterator();
        for (int i = 0; i < target; i++) {
            it.next();
        }
        return it.next();
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
        String id = view.getString("GiftVariant", "");
        if (!id.isEmpty()) {
            Identifier itemId = Identifier.of(id);
            this.gift = Registries.ITEM.get(itemId);
            this.giftDisplay = ItemDisplayElementUtil.createSimple(this.gift);
            this.giftDisplay.setOffset(new Vec3d(0, 0.5, 0));
        }
    }

    @Override
    protected void writeCustomData(WriteView view) {
        view.putString("GiftVariant", Registries.ITEM.getId(this.gift).toString());
    }
}