package com.minelittlepony.unicopia.entity.player;

import net.minecraft.entity.Entity;
import net.minecraft.util.hit.HitResult.Type;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

public interface MeteorlogicalUtil {

    static boolean isLookingIntoSun(World world, Entity entity) {

        // check first whether the world has a sun
        if (!world.getDimension().hasSkyLight()) {
            return false;
        }

        // check if sun is obscured by clouds
        if (world.hasRain(entity.getBlockPos()) || world.isThundering()) {
            return false;
        }

        // we translate sun angle to a scale of 0-1 (0=sunrise, 1=sunset, >1 nighttime)
        final float skyAngle = ((entity.world.getSkyAngle(1) + 0.25F) % 1F) * 2;
        float playerYaw = MathHelper.wrapDegrees(entity.getHeadYaw());
        float playerAngle = (-entity.getPitch(1) / 90F) / 2F;

        // player is facing the other way so flip the yaw to match sun angle
        if (playerYaw > 0) {
            playerAngle = 1 - playerAngle;
        }

        playerYaw = Math.abs(playerYaw);

        // check if day,
        // and player is looking towards the sun, and that there isn't a block obstructing their view
        return skyAngle < 1
            && (playerYaw > 89 && playerYaw < 92 || (playerAngle > 0.45F && playerAngle < 0.55F))
            && playerAngle > (skyAngle - 0.04F) && playerAngle < (skyAngle + 0.04F)
            && entity.raycast(100, 1, true).getType() == Type.MISS;
    }
}