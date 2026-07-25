/*
 * Make My Crystals Faster
 * Copyright (C) 2026 femboyPig
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation, version 3 of the License.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package net.femboypig.mmcf.mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.boss.enderdragon.EndCrystal;
import net.minecraft.world.item.Items;
import net.minecraft.world.phys.EntityHitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Minecraft.class)
public abstract class MinecraftMixin {
    @Unique
    private static final int MMCF_ATTACK_BUFFER_TICKS = 4;

    @Unique
    private int mmcf$bufferedCrystalAttackTicks;

    @Shadow
    public LocalPlayer player;

    @Shadow
    protected int missTime;

    @Shadow
    protected abstract boolean startAttack();

    @Inject(method = "startAttack", at = @At("HEAD"))
    private void mmcf$bufferEarlyCrystalClick(CallbackInfoReturnable<Boolean> cir) {
        Minecraft minecraft = (Minecraft) (Object) this;
        if (mmcf$isHoldingCrystal() && !(minecraft.hitResult instanceof EntityHitResult)) {
            mmcf$bufferedCrystalAttackTicks = MMCF_ATTACK_BUFFER_TICKS;
            missTime = 0;
        }
    }

    @Inject(method = "tick", at = @At("HEAD"))
    private void mmcf$removeCrystalMissDelay(CallbackInfo ci) {
        Minecraft minecraft = (Minecraft) (Object) this;
        if (mmcf$isHoldingCrystal()) {
            missTime = 0;

            if (minecraft.options.keyUse.isDown()
                    && minecraft.gameMode != null
                    && minecraft.gameMode.isDestroying()) {
                minecraft.gameMode.stopDestroyBlock();
            }
        }
    }

    @Inject(method = "continueAttack", at = @At("HEAD"), cancellable = true)
    private void mmcf$doNotMineThroughCrystalPlacement(boolean attacking, CallbackInfo ci) {
        Minecraft minecraft = (Minecraft) (Object) this;
        if (minecraft.options.keyUse.isDown() && mmcf$isHoldingCrystal()) {
            if (minecraft.gameMode != null && minecraft.gameMode.isDestroying()) {
                minecraft.gameMode.stopDestroyBlock();
            }
            ci.cancel();
        }
    }

    @Inject(method = "tick", at = @At("TAIL"))
    private void mmcf$repeatCrystalAttackWhileHeld(CallbackInfo ci) {
        Minecraft minecraft = (Minecraft) (Object) this;
        if (!mmcf$isHoldingCrystal()) {
            mmcf$bufferedCrystalAttackTicks = 0;
            return;
        }

        if (mmcf$isAimingAtLiveCrystal(minecraft)
                && (minecraft.options.keyAttack.isDown() || mmcf$bufferedCrystalAttackTicks > 0)) {
            mmcf$bufferedCrystalAttackTicks = 0;
            missTime = 0;
            startAttack();
        } else if (mmcf$bufferedCrystalAttackTicks > 0) {
            mmcf$bufferedCrystalAttackTicks--;
        }
    }

    @Unique
    private boolean mmcf$isHoldingCrystal() {
        return player != null
                && (player.getMainHandItem().getItem() == Items.END_CRYSTAL
                || player.getOffhandItem().getItem() == Items.END_CRYSTAL);
    }

    @Unique
    private boolean mmcf$isAimingAtLiveCrystal(Minecraft minecraft) {
        return minecraft.hitResult instanceof EntityHitResult entityHit
                && entityHit.getEntity() instanceof EndCrystal crystal
                && !crystal.isRemoved();
    }
}
