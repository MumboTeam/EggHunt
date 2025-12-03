package io.github.mumboteam.egghunt.entity;

import eu.pb4.factorytools.api.virtualentity.ItemDisplayElementUtil;
import eu.pb4.polymer.core.api.entity.PolymerEntity;
import eu.pb4.polymer.virtualentity.api.ElementHolder;
import eu.pb4.polymer.virtualentity.api.attachment.EntityAttachment;
import eu.pb4.polymer.virtualentity.api.elements.ItemDisplayElement;
import io.github.mumboteam.egghunt.registry.ModItems;
import io.github.mumboteam.egghunt.utils.EggHuntState;
import io.github.mumboteam.egghunt.utils.PlayerData;
import io.github.mumboteam.egghunt.utils.RewardDistributor;
import io.github.mumboteam.egghunt.utils.TickScheduler;
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
import net.minecraft.world.World;
import xyz.nucleoid.packettweaker.PacketContext;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

public class EggEntity extends Entity implements PolymerEntity {
    private final Set<Item> VARIANTS = new HashSet<>() {{
        add(ModItems.EGG_1);
        add(ModItems.EGG_1_1);
        add(ModItems.EGG_1_2);
        add(ModItems.EGG_1_3);
        add(ModItems.EGG_2);
        add(ModItems.EGG_2_1);
        add(ModItems.EGG_2_2);
        add(ModItems.EGG_2_3);
        add(ModItems.EGG_3);
        add(ModItems.EGG_3_1);
        add(ModItems.EGG_3_2);
        add(ModItems.EGG_3_3);
    }};

    private Item egg;
    private ItemDisplayElement eggDisplay;
    private ElementHolder elementHolder;

    public EggEntity(EntityType<? extends EggEntity> type, World world) {
        super(type, world);
        this.egg = randomEgg(VARIANTS);
        this.eggDisplay = ItemDisplayElementUtil.createSimple(this.egg);
        this.eggDisplay.setRotation(-90.0f, 0.0f);
    }

    public EggEntity(EntityType<? extends EggEntity> type, World world, Item variant) {
        super(type, world);
        this.egg = variant;
        this.eggDisplay = ItemDisplayElementUtil.createSimple(this.egg);
        this.eggDisplay.setRotation(-90.0f, 0.0f);
    }

    @Override
    public void tick() {
        super.tick();

        if (elementHolder == null) {
            elementHolder = new ElementHolder();
            EntityAttachment.of(elementHolder, this);

            elementHolder.addElement(this.eggDisplay);
        }

        EggHuntState state = EggHuntState.getServerState(getEntityWorld().getServer());
        if (!state.eggIds.contains(this.uuid)) {
            state.eggIds.add(this.uuid);
            state.markDirty();
        }

        elementHolder.tick();
    }

    @Override
    public ActionResult interact(PlayerEntity player, Hand hand) {
        EggHuntState state = EggHuntState.getServerState(player.getEntityWorld().getServer());
        PlayerData playerData = state.playerData.get(player.getUuid());
        if (playerData == null || !playerData.isAdmin()) {
            player.sendMessage(Text.translatable("text.egghunt.collected").setStyle(Style.EMPTY.withColor(Formatting.AQUA)), true);
            if (playerData != null) {
                playerData.addToFoundEggs(this.uuid);
            } else {
                PlayerData data = new PlayerData();
                data.addToFoundEggs(this.uuid);
                state.playerData.put(player.getUuid(), data);
            }
            state.markDirty();
            hideFromClient((ServerPlayerEntity) player);

            RewardDistributor.spawnFor(player, egg, 1);
        } else {
            state.eggIds.remove(this.uuid);
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
        EggHuntState state = EggHuntState.getServerState(player.getEntityWorld().getServer());
        PlayerData playerData = state.playerData.get(player.getUuid());
        if (playerData != null && playerData.getFoundEggs().contains(this.uuid)) {
            // Wait a few ticks so the client can process that the entity exists
            TickScheduler.schedule(() -> hideFromClient(player), 10);
        }
    }

    private void hideFromClient(ServerPlayerEntity player) {
        EggHuntState state = EggHuntState.getServerState(player.getEntityWorld().getServer());
        if (!state.playerData.get(player.getUuid()).isAdmin()) {
            player.networkHandler.sendPacket(
                    new EntitiesDestroyS2CPacket(this.getId())
            );
            player.networkHandler.sendPacket(
                    new EntitiesDestroyS2CPacket(this.eggDisplay.getEntityId())
            );
        }
    }

    private static Item randomEgg(Set<Item> items) {
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
        String id = view.getString("EggVariant", "");
        if (!id.isEmpty()) {
            Identifier itemId = Identifier.of(id);
            this.egg = Registries.ITEM.get(itemId);
            this.eggDisplay = ItemDisplayElementUtil.createSimple(this.egg);
            this.eggDisplay.setRotation(-90f, 0f);
        }
    }

    @Override
    protected void writeCustomData(WriteView view) {
        view.putString("EggVariant", Registries.ITEM.getId(this.egg).toString());
    }
}