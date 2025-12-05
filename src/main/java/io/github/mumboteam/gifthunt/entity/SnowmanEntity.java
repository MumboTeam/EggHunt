package io.github.mumboteam.gifthunt.entity;

import eu.pb4.factorytools.api.virtualentity.ItemDisplayElementUtil;
import eu.pb4.polymer.core.api.entity.PolymerEntity;
import eu.pb4.polymer.virtualentity.api.ElementHolder;
import eu.pb4.polymer.virtualentity.api.attachment.EntityAttachment;
import eu.pb4.polymer.virtualentity.api.elements.ItemDisplayElement;
import eu.pb4.polymer.virtualentity.api.elements.TextDisplayElement;
import io.github.mumboteam.gifthunt.GiftHunt;
import io.github.mumboteam.gifthunt.mixin.SnowGolemEntityAccessor;
import io.github.mumboteam.gifthunt.registry.ModItems;
import io.github.mumboteam.gifthunt.utils.GiftHuntState;
import io.github.mumboteam.gifthunt.utils.PlayerData;
import io.github.mumboteam.gifthunt.utils.RewardDistributor;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.decoration.DisplayEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemDisplayContext;
import net.minecraft.item.Items;
import net.minecraft.network.packet.s2c.play.PlayerRemoveS2CPacket;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.server.PlayerConfigEntry;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.StyleSpriteSource;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.joml.Vector3f;
import xyz.nucleoid.packettweaker.PacketContext;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

public class SnowmanEntity extends Entity implements PolymerEntity {
    private final TextDisplayElement leaderboard = new TextDisplayElement();
    private final TextDisplayElement background = new TextDisplayElement();
    private final TextDisplayElement giftHuntTitle = new TextDisplayElement();
    private final TextDisplayElement leaderboardTitle = new TextDisplayElement();
    private final ItemDisplayElement gift1 = ItemDisplayElementUtil.createSimple(ModItems.GIFT_1);
    private final ItemDisplayElement gift2 = ItemDisplayElementUtil.createSimple(ModItems.GIFT_2);
    private final ItemDisplayElement gift3 = ItemDisplayElementUtil.createSimple(ModItems.GIFT_3);
    private final TextDisplayElement giftReturn = new TextDisplayElement();
    private ElementHolder elementHolder;

    public SnowmanEntity(EntityType<? extends SnowmanEntity> entityType, World world) {
        super(entityType, world);
        this.leaderboard.setBillboardMode(DisplayEntity.BillboardMode.FIXED);
        this.leaderboard.setLineWidth(256);
        this.leaderboard.setBackground(0x00000000);
        this.leaderboard.setOffset(new Vec3d(-3.3, 0.5, 0));
        this.background.setBillboardMode(DisplayEntity.BillboardMode.FIXED);
        this.background.setBackground(0x00000000);
        this.background.setText(Text.literal("1").setStyle(Style.EMPTY.withFont(new StyleSpriteSource.Font(Identifier.of(GiftHunt.ID, "background")))));
        this.background.setOffset(new Vec3d(-3.3, 3.5, -0.05));
        this.giftHuntTitle.setBillboardMode(DisplayEntity.BillboardMode.FIXED);
        this.giftHuntTitle.setText(Text.literal("ɢɪꜰᴛ ʜᴜɴᴛ").setStyle(Style.EMPTY.withColor(Formatting.GOLD).withBold(true)));
        this.giftHuntTitle.setScale(new Vector3f(3f, 3f, 0.01f));
        this.giftHuntTitle.setBackground(0x00000000);
        this.giftHuntTitle.setOffset(new Vec3d(-3.3, 3.9, 0));
        this.leaderboardTitle.setBillboardMode(DisplayEntity.BillboardMode.FIXED);
        this.leaderboardTitle.setText(Text.literal("ʟᴇᴀᴅᴇʀʙᴏᴀʀᴅ").setStyle(Style.EMPTY.withBold(true)));
        this.leaderboardTitle.setScale(new Vector3f(1.5f, 1.5f, 0.01f));
        this.leaderboardTitle.setBackground(0x00000000);
        this.leaderboardTitle.setOffset(new Vec3d(-3.3, 3.4, 0));
        this.gift1.setItemDisplayContext(ItemDisplayContext.NONE);
        this.gift1.setOffset(new Vec3d(-4.3, 5.3, 0));
        this.gift1.setScale(new Vector3f(1.2f));
        this.gift1.setRotation(-10f, 50f);
        this.gift2.setItemDisplayContext(ItemDisplayContext.NONE);
        this.gift2.setOffset(new Vec3d(-3.3, 5.6, 0));
        this.gift2.setScale(new Vector3f(1.5f));
        this.gift2.setRotation(10f, -30f);
        this.gift3.setItemDisplayContext(ItemDisplayContext.NONE);
        this.gift3.setOffset(new Vec3d(-2.3, 5.1, 0));
        this.gift3.setRotation(20f, 70f);
        this.giftReturn.setText(Text.translatable("text.gifthunt.gift_return").setStyle(Style.EMPTY.withBold(true)));
        this.giftReturn.setOffset(new Vec3d(0, 2.1, 0.1));
        updateLeaderboard(GiftHuntState.getServerState(world.getServer()));
    }

    @Override
    public void modifyRawTrackedData(List<net.minecraft.entity.data.DataTracker.SerializedEntry<?>> data, ServerPlayerEntity player, boolean initial) {
        data.add(new net.minecraft.entity.data.DataTracker.SerializedEntry<>(
                SnowGolemEntityAccessor.getSnowGolemFlags().id(),
                TrackedDataHandlerRegistry.BYTE,
                (byte) 0x00 // 0x10 = 16 = pumpkin
        ));
    }

    @Override
    public EntityType<?> getPolymerEntityType(PacketContext context) {
        return EntityType.SNOW_GOLEM;
    }

    @Override
    public void onStoppedTrackingBy(ServerPlayerEntity player) {
        player.networkHandler.sendPacket(new PlayerRemoveS2CPacket(List.of(this.getUuid())));
        super.onStartedTrackingBy(player);
    }

    @Override
    protected void initDataTracker(DataTracker.Builder builder) {
    }

    @Override
    public void tick() {
        super.tick();
        if (elementHolder == null) {
            elementHolder = new ElementHolder();
            EntityAttachment.of(elementHolder, this);
        }

        elementHolder.addElement(this.leaderboard);
        elementHolder.addElement(this.background);
        elementHolder.addElement(this.giftHuntTitle);
        elementHolder.addElement(this.leaderboardTitle);
        elementHolder.addElement(this.gift1);
        elementHolder.addElement(this.gift2);
        elementHolder.addElement(this.gift3);
        elementHolder.addElement(this.giftReturn);
        elementHolder.tick();
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

    public void updateLeaderboard(GiftHuntState state) {
        MutableText text = Text.literal("\n");
        Style style = Style.EMPTY.withFont(new StyleSpriteSource.Font(Identifier.of(GiftHunt.ID, "mono")));
        AtomicInteger count = new AtomicInteger(0);

        state.playerData.entrySet().stream().sorted(Map.Entry.comparingByValue(Comparator.comparingInt(PlayerData::getGiftCount).reversed())).limit(10).forEach(entry -> {
            Optional<PlayerConfigEntry> cacheEntry = this.getEntityWorld().getServer().getApiServices().nameToIdCache().getByUuid(entry.getKey());
            String name = "Unknown player";
            if (cacheEntry.isPresent()) {
                name = cacheEntry.get().name();
            }
            MutableText line = Text.literal(String.format(" %2d. %-16s - %3d \n", count.incrementAndGet(), name, entry.getValue().getGiftCount()));
            if (count.get() == 1) {
                line.setStyle(style.withColor(0xffd700));
            } else if (count.get() == 2) {
                line.setStyle(style.withColor(0xc0c0c0));
            } else if (count.get() == 3) {
                line.setStyle(style.withColor(0xcd7f32));
            } else {
                line.setStyle(style.withColor(Formatting.WHITE));
            }
            text.append(line);
        });
        if (state.playerData.size() < 10) {
            for (int i = 0; i < 10 - state.playerData.size(); i++) {
                text.append(Text.literal(String.format(" %2d. ________________ - ___ \n", count.incrementAndGet())).setStyle(style.withColor(Formatting.GRAY)));
            }
        }
        leaderboard.setText(text);
    }

    @Override
    public ActionResult interact(PlayerEntity player, Hand hand) {
        GiftHuntState state = GiftHuntState.getServerState(player.getEntityWorld().getServer());
        AtomicInteger gifts = new AtomicInteger();
        player.getInventory().forEach(itemStack -> {
            if (itemStack.isIn(TagKey.of(RegistryKeys.ITEM, Identifier.of(GiftHunt.ID, "gifts")))) {
                if (itemStack.isOf(ModItems.GIFT_SHINY)) {
                    player.sendMessage(Text.translatable("text.gifthunt.shiny").setStyle(Style.EMPTY.withColor(Formatting.GOLD).withBold(true)), false);
                    RewardDistributor.spawnFor(player, Items.DIAMOND, 5);
                }
                itemStack.setCount(0);
                gifts.getAndIncrement();
            }
        });

        if (gifts.get() == 0) {
            player.sendMessage(Text.translatable("text.gifthunt.empty"), false);
        } else {
            state.playerData.compute(player.getUuid(), (uuid, data) -> {
                if (data == null) {
                    PlayerData pData = new PlayerData();
                    pData.increaseGiftCount(gifts.get());
                    return pData;
                } else {
                    data.increaseGiftCount(gifts.get());
                    return data;
                }
            });
            if (!GiftHunt.dailyPlayerSubmissions.contains(player.getUuid())) {
                GiftHunt.dailyPlayerSubmissions.add(player.getUuid());
                RewardDistributor.spawnFor(player, Items.DIAMOND, 3);
                player.sendMessage(Text.translatable("text.gifthunt.daily_reward").setStyle(Style.EMPTY.withColor(Formatting.AQUA).withBold(true)), false);
            }
            player.sendMessage(Text.translatable("text.gifthunt.returned", gifts.get(), (gifts.get() == 1) ? "" : "s"), false);
            updateLeaderboard(state);
        }

        return ActionResult.SUCCESS;
    }
}
