package com.minelittlepony.unicopia.client.render;

import java.util.Optional;

import com.minelittlepony.unicopia.USounds;
import com.minelittlepony.unicopia.client.minelittlepony.MineLPConnector;
import com.minelittlepony.unicopia.entity.player.Pony;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Arm;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3f;

public class PlayerPoser {
    public static final PlayerPoser INSTANCE = new PlayerPoser();

    public void applyPosing(MatrixStack matrices, PlayerEntity player, BipedEntityModel<?> model) {
        Pony pony = Pony.of(player);
        float progress = pony.getAnimationProgress(MinecraftClient.getInstance().getTickDelta());
        Animation animation = pony.getAnimation();
        boolean isPony = !MineLPConnector.getPlayerPonyRace(player).isDefault();

        switch (animation) {
            case WOLOLO: {
                float roll = MathHelper.sin(player.age / 10F);
                float yaw = MathHelper.cos(player.age / 10F);

                model.leftArm.pitch += -1;
                model.rightArm.pitch += -1;

                model.leftArm.roll = -roll;
                model.rightArm.roll = roll;

                model.leftArm.yaw = yaw;
                model.rightArm.yaw = yaw;
                break;
            }
            case ARMS_FORWARD: {
                float roll = (progress + 1) / 2F;

                float pitch = 1.5F * roll;
                float yaw = 0.5F * roll;

                model.leftArm.pitch -= pitch;
                model.rightArm.pitch -= pitch;

                model.leftArm.roll = 0;
                model.rightArm.roll = 0;

                model.leftArm.yaw = yaw;
                model.rightArm.yaw = -yaw;
                break;
            }
            case ARMS_UP: {
                float roll = (progress + 1) / 2F;

                float pitch = 3F * roll;
                float yaw = 0.5F * roll;

                model.leftArm.pitch -= pitch;
                model.rightArm.pitch -= pitch;

                model.leftArm.roll = 0;
                model.rightArm.roll = 0;

                model.leftArm.yaw = yaw;
                model.rightArm.yaw = -yaw;
                break;
            }
            case WAVE_ONE:
            case WAVE_TWO: {
                progress = seesaw(progress);

                if (animation == Animation.WAVE_TWO && isPony) {
                    rearUp(matrices, model, progress);
                    model.head.pitch += progress;
                    model.hat.pitch += progress;
                }

                float wave = 2.5F + progress * MathHelper.sin(player.age / 3F);

                if (animation == Animation.WAVE_TWO || player.getMainArm() == Arm.LEFT) {
                    model.leftArm.roll = -wave;
                }
                if (animation == Animation.WAVE_TWO || player.getMainArm() == Arm.RIGHT) {
                    model.rightArm.roll = wave;
                }
                break;
            }
            case KICK: {
                if (isPony) {
                    float roll = (progress + 1) / 2F;

                    model.rightLeg.pitch = roll * 1.5F;
                    model.rightLeg.yaw = -roll / 7F;

                    model.leftLeg.pitch = roll * 1.5F;
                    model.leftLeg.yaw = roll / 7F;

                    model.leftArm.pitch = 0;
                    model.leftArm.roll = -roll / 7F;
                    model.rightArm.pitch = 0;
                    model.rightArm.roll = roll / 7F;
                    break;
                }

                float roll = (progress + 1) / 2F;

                model.rightArm.pitch += roll / 5F;
                model.leftArm.roll -= roll / 5F;
                model.rightArm.roll += roll / 5F;

                if (player.getMainArm() == Arm.LEFT) {
                    model.rightLeg.pitch = -roll * 1.5F;
                    model.rightLeg.roll = roll / 10F;
                } else {
                    model.leftLeg.pitch = -roll * 1.5F;
                    model.leftLeg.roll = -roll / 10F;
                }
                break;
            }
            case STOMP: {
                progress = seesaw(progress);

                if (!isPony) {
                    if (player.getMainArm() == Arm.LEFT) {
                        model.rightLeg.roll = -progress / 9F;
                        model.rightLeg.pivotY -= progress * 5;
                    } else {
                        model.leftLeg.roll = -progress / 9F;
                        model.leftLeg.pivotY -= progress * 5;
                    }
                    break;
                }

                rearUp(matrices, model, progress);
                break;
            }
            case WIGGLE_NOSE: {
                if (!isPony) {
                    break;
                }

                progress = seesaw(progress) * MathHelper.sin(player.age / 4F);

                model.getHead().getChild("mare").pivotY += progress;
                model.getHead().getChild("stallion").pivotY += progress;
                break;
            }
            default:
        }
    }

    private void rearUp(MatrixStack matrices, BipedEntityModel<?> model, float progress) {
        matrices.translate(0, 0, 0.5);
        matrices.multiply(Vec3f.POSITIVE_X.getDegreesQuaternion(-45 * progress));
        matrices.translate(0, 0, -0.5);

        float roll = progress / 2F;

        model.rightLeg.pitch = roll * 1.5F;
        model.rightLeg.yaw = -roll / 7F;

        model.leftLeg.pitch = roll * 1.5F;
        model.leftLeg.yaw = roll / 7F;
    }

    static float seesaw(float progress) {
        return Math.max(0, MathHelper.cos((progress - 0.5F) * (float)Math.PI));
    }

    public enum Animation {
        NONE(0),
        WOLOLO(USounds.ENTITY_PLAYER_WOLOLO, 40),
        ARMS_FORWARD(5),
        ARMS_UP(5),
        WAVE_ONE(USounds.ENTITY_PLAYER_WHISTLE, 20),
        WAVE_TWO(USounds.ENTITY_PLAYER_WHISTLE, 20),
        KICK(USounds.ENTITY_PLAYER_KICK, 5),
        STOMP(5),
        WIGGLE_NOSE(6);

        private final int duration;
        private final Optional<SoundEvent> sound;

        Animation(int duration) {
            this.duration = duration;
            this.sound = Optional.empty();
        }

        Animation(SoundEvent sound, int duration) {
            this.duration = duration;
            this.sound = Optional.of(sound);
        }

        public int getDuration() {
            return duration;
        }

        public Optional<SoundEvent> getSound() {
            return sound;
        }
    }
}
