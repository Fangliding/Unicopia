package com.minelittlepony.unicopia.entity.player.dummy;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.minelittlepony.unicopia.InteractionManager;
import com.minelittlepony.unicopia.Owned;
import com.mojang.authlib.GameProfile;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.s2c.play.PlayerListS2CPacket;
import net.minecraft.world.GameMode;

public class DummyClientPlayerEntity extends AbstractClientPlayerEntity implements Owned<PlayerEntity>, Owned.Mutable<PlayerEntity> {

    private PlayerListEntry playerInfo;

    private PlayerEntity owner;

    public DummyClientPlayerEntity(ClientWorld world, GameProfile profile) {
        super(world, profile, null);
    }

    @Override
    public boolean isSpectator() {
        return getPlayerListEntry().getGameMode() == GameMode.SPECTATOR;
    }

    @Override
    public boolean isCreative() {
        return getPlayerListEntry().getGameMode() == GameMode.CREATIVE;
    }

    @Override
    @NotNull
    protected PlayerListEntry getPlayerListEntry() {
        if (playerInfo == null) {
            ClientPlayNetworkHandler connection = MinecraftClient.getInstance().getNetworkHandler();

            playerInfo = connection.getPlayerListEntry(getGameProfile().getId());

            if (playerInfo == null) {
                playerInfo = new PlayerListEntry(new PlayerListS2CPacket.Entry(getGameProfile(), 0, null, null, null), null, false);
            }
        }

        return playerInfo;
    }

    @Override
    protected void playEquipSound(ItemStack stack) {
        /*noop*/
    }

    @Override
    public boolean shouldRenderName() {
        return !InteractionManager.instance().isClientPlayer(getMaster());
    }

    @Override
    @Nullable
    public PlayerEntity getMaster() {
        return owner;
    }

    @Override
    public void setMaster(PlayerEntity owner) {
        this.owner = owner;
    }
}
