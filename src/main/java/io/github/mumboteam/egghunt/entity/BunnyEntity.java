package io.github.mumboteam.egghunt.entity;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import eu.pb4.factorytools.api.virtualentity.ItemDisplayElementUtil;
import eu.pb4.polymer.core.api.entity.PolymerEntity;
import eu.pb4.polymer.core.api.entity.PolymerEntityUtils;
import eu.pb4.polymer.virtualentity.api.ElementHolder;
import eu.pb4.polymer.virtualentity.api.attachment.EntityAttachment;
import eu.pb4.polymer.virtualentity.api.elements.ItemDisplayElement;
import eu.pb4.polymer.virtualentity.api.elements.TextDisplayElement;
import io.github.mumboteam.egghunt.EggHunt;
import io.github.mumboteam.egghunt.RewardDistributor;
import io.github.mumboteam.egghunt.registry.ModItems;
import io.github.mumboteam.egghunt.utils.EggHuntState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.decoration.DisplayEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemDisplayContext;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.PlayerListS2CPacket;
import net.minecraft.network.packet.s2c.play.PlayerRemoveS2CPacket;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.*;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.GameMode;
import net.minecraft.world.World;
import org.joml.Vector3f;
import xyz.nucleoid.packettweaker.PacketContext;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

public class BunnyEntity extends Entity implements PolymerEntity {
    private final TextDisplayElement leaderboard = new TextDisplayElement();
    private final TextDisplayElement background = new TextDisplayElement();
    private final TextDisplayElement eggHuntTitle = new TextDisplayElement();
    private final TextDisplayElement leaderboardTitle = new TextDisplayElement();
    private final ItemDisplayElement egg1 = ItemDisplayElementUtil.createSimple(ModItems.EGG_1);
    private final ItemDisplayElement egg2 = ItemDisplayElementUtil.createSimple(ModItems.EGG_2);
    private final ItemDisplayElement egg3 = ItemDisplayElementUtil.createSimple(ModItems.EGG_3);
    private final TextDisplayElement eggReturn = new TextDisplayElement();
    private ElementHolder elementHolder;

    public BunnyEntity(EntityType<? extends BunnyEntity> entityType, World world) {
        super(entityType, world);
        this.leaderboard.setBillboardMode(DisplayEntity.BillboardMode.FIXED);
        this.leaderboard.setLineWidth(256);
        this.leaderboard.setBackground(0x00000000);
        this.leaderboard.setOffset(new Vec3d(-3.3, 0.5, 0));
        this.background.setBillboardMode(DisplayEntity.BillboardMode.FIXED);
        this.background.setBackground(0x00000000);
        this.background.setText(Text.literal("1").setStyle(Style.EMPTY.withFont(Identifier.of(EggHunt.ID, "background"))));
        this.background.setOffset(new Vec3d(-3.3, 3.5, -0.05));
        this.eggHuntTitle.setBillboardMode(DisplayEntity.BillboardMode.FIXED);
        this.eggHuntTitle.setText(Text.literal("ᴇɢɢ ʜᴜɴᴛ").setStyle(Style.EMPTY.withColor(Formatting.GOLD).withBold(true)));
        this.eggHuntTitle.setScale(new Vector3f(3f, 3f, 0.01f));
        this.eggHuntTitle.setBackground(0x00000000);
        this.eggHuntTitle.setOffset(new Vec3d(-3.3, 3.9, 0));
        this.leaderboardTitle.setBillboardMode(DisplayEntity.BillboardMode.FIXED);
        this.leaderboardTitle.setText(Text.literal("ʟᴇᴀᴅᴇʀʙᴏᴀʀᴅ").setStyle(Style.EMPTY.withBold(true)));
        this.leaderboardTitle.setScale(new Vector3f(1.5f, 1.5f, 0.01f));
        this.leaderboardTitle.setBackground(0x00000000);
        this.leaderboardTitle.setOffset(new Vec3d(-3.3, 3.4, 0));
        this.egg1.setItemDisplayContext(ItemDisplayContext.NONE);
        this.egg1.setOffset(new Vec3d(-4.3, 5.3, 0));
        this.egg1.setScale(new Vector3f(1.2f));
        this.egg1.setRotation(-10f, 50f);
        this.egg2.setItemDisplayContext(ItemDisplayContext.NONE);
        this.egg2.setOffset(new Vec3d(-3.3, 5.6, 0));
        this.egg2.setScale(new Vector3f(1.5f));
        this.egg2.setRotation(10f, -30f);
        this.egg3.setItemDisplayContext(ItemDisplayContext.NONE);
        this.egg3.setOffset(new Vec3d(-2.3, 5.1, 0));
        this.egg3.setRotation(20f, 70f);
        this.eggReturn.setText(Text.translatable("text.egghunt.egg_return").setStyle(Style.EMPTY.withBold(true)));
        this.eggReturn.setOffset(new Vec3d(0, 2.1, 0.1));
        updateLeaderboard(EggHuntState.getServerState(world.getServer()));
    }

    @Override
    public EntityType<?> getPolymerEntityType(PacketContext context) {
        return EntityType.PLAYER;
    }

    @Override
    public void onBeforeSpawnPacket(ServerPlayerEntity player, Consumer<Packet<?>> packetConsumer) {
        var packet = PolymerEntityUtils.createMutablePlayerListPacket(EnumSet.of(PlayerListS2CPacket.Action.ADD_PLAYER, PlayerListS2CPacket.Action.UPDATE_LISTED));
        var gameprofile = new GameProfile(this.getUuid(), "");
        gameprofile.getProperties().put("textures", new Property("textures",
                "ewogICJ0aW1lc3RhbXAiIDogMTYxNzE4MTc4Mjc2NSwKICAicHJvZmlsZUlkIiA6ICIzZmM3ZmRmOTM5NjM0YzQxOTExOTliYTNmN2NjM2ZlZCIsCiAgInByb2ZpbGVOYW1lIiA6ICJZZWxlaGEiLAogICJzaWduYXR1cmVSZXF1aXJlZCIgOiB0cnVlLAogICJ0ZXh0dXJlcyIgOiB7CiAgICAiU0tJTiIgOiB7CiAgICAgICJ1cmwiIDogImh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZmE2ODlmNjBlMTVjYmVlM2MxMTQ4OWE2YzhkMTExZjU0MWNkYzZiNjZiNmU4ZDMyN2NmMWUwOGQwODI0ZjMzNyIKICAgIH0KICB9Cn0=",
                "Epxge+NakzuCRjIHbn8IgpQg5QxllqR04Tirrr0bQrtdbMwIXXwX/JUl1A5GVQaJ5p8hTtu0c7dI5B5McNnh8pNku3+ctH7DzxA5nhTHxnodsGZMLhBZYDKL944lyYXb59X78Y+4M8VU5yd9fdw8c1K64MvNg9C7F1dSIyG5yUK2eo5cXmfVVl7F3vj6MUwcTEyDAOOGhf13jJUe9k0dMZIONn3lcqg3MqHGCBHwQ7kFfeLcLdgwofYyh2HsgV6pvo2DMLLaCzO94+Z5F7vY4lsj5gqDR9rfkLc0qUxaweFUvyz6DJWRMkU7EaI7ltzJs4FizApvGRCN6j8a/+kovkgsVJkbPUQOcqYXhlffRW2Z4pf5X5JrcVWuPbzIoMvPCPjVRt36NuGsL+y1bxKiSih1mdTKWZAo3ihbkekMbe1c2ulfA43Mzwa0gXsyp6ligPH+YKt5ZPzuPkDyNDRSoUvEXFiF86Er0NCI64lzGqPd9pp6mmUkWi9hz/SQQRZVkaUwh8HU+kziPororpf/1uuVSEPhKl8rQu6I4TMOOidNISjp6LHd6s/F+PIrpMRqariOxKYvSiqIvoQ7oXD8Pd2CTRFH5YaTKw2qD6xsEMXUERwsNmu+jKNlieen+rtUXOrlq653FbyldSJuZtbN9rO8HLwGpdIw6ND6NQiBXZ0="
        ));
        packet.getEntries().add(new PlayerListS2CPacket.Entry(this.getUuid(), gameprofile, false, 0, GameMode.ADVENTURE, null, true, 0, null));
        packetConsumer.accept(packet);
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
        elementHolder.addElement(this.eggHuntTitle);
        elementHolder.addElement(this.leaderboardTitle);
        elementHolder.addElement(this.egg1);
        elementHolder.addElement(this.egg2);
        elementHolder.addElement(this.egg3);
        elementHolder.addElement(this.eggReturn);
        elementHolder.tick();
    }

    @Override
    public boolean damage(ServerWorld world, DamageSource source, float amount) {
        return false;
    }

    @Override
    protected void readCustomDataFromNbt(NbtCompound nbt) {

    }

    @Override
    protected void writeCustomDataToNbt(NbtCompound nbt) {

    }

    public void updateLeaderboard(EggHuntState state) {
        MutableText text = Text.literal("\n");
        Style style = Style.EMPTY.withFont(Identifier.of(EggHunt.ID, "mono"));
        AtomicInteger count = new AtomicInteger(0);

        state.playerScores.entrySet().stream().sorted(Map.Entry.comparingByValue(Comparator.reverseOrder())).limit(10).forEach(entry -> {
            Optional<GameProfile> profile = this.getServer().getUserCache().getByUuid(entry.getKey());
            profile.ifPresent(gameProfile -> {
                MutableText line = Text.literal(String.format(" %2d. %-16s - %3d \n", count.incrementAndGet(), gameProfile.getName(), entry.getValue()));
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
        });
        if (state.playerScores.size() < 10) {
            for (int i = 0; i<10-state.playerScores.size(); i++) {
                text.append(Text.literal(String.format(" %2d. ________________ - ___ \n", count.incrementAndGet())).setStyle(style.withColor(Formatting.GRAY)));
            }
        }
        leaderboard.setText(text);
    }

    @Override
    public ActionResult interact(PlayerEntity player, Hand hand) {
        EggHuntState state = EggHuntState.getServerState(player.getServer());
        AtomicInteger eggs = new AtomicInteger();
        player.getInventory().forEach(itemStack -> {
            if (itemStack.isIn(TagKey.of(RegistryKeys.ITEM, Identifier.of(EggHunt.ID, "eggs")))) {
                itemStack.setCount(0);
                eggs.getAndIncrement();
            }
        });

        if (eggs.get() == 0) {
            player.sendMessage(Text.translatable("text.egghunt.empty"), false);
        } else {
            state.playerScores.compute(player.getUuid(), (uuid, eggCount) -> {
                if (eggCount == null) {
                    return eggs.get();
                } else {
                    return eggCount + eggs.get();
                }
            });
            if (!EggHunt.dailyPlayerSubmissions.contains(player.getUuid())) {
                EggHunt.dailyPlayerSubmissions.add(player.getUuid());
                RewardDistributor.spawnFor(player, Items.DIAMOND, 3);
                player.sendMessage(Text.translatable("text.egghunt.daily_reward"), false);
            }
            player.sendMessage(Text.translatable("text.egghunt.returned", eggs.get(), (eggs.get() == 1) ? "" : "s"), false);
            updateLeaderboard(state);
        }

        return ActionResult.SUCCESS;
    }
}
