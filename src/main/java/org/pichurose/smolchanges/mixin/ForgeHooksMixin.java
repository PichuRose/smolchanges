package org.pichurose.smolchanges.mixin;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.FishingRodItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.pichurose.smolchanges.utils.ResizingUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;


@Mixin(FishingRodItem.class)
public class FishingRodItemMixin {

    @Inject(at = @At("HEAD"), method = "use")
    public void preUse(Level leve, Player player, InteractionHand hand, CallbackInfoReturnable<InteractionResultHolder<ItemStack>> ci){
        if(ResizingUtils.getActualSize(player) <= .25) {

            Vec3 lookAngle = player.getLookAngle();
            Vec3 moveDelta = player.getDeltaMovement();
            float scaleMultiplier = 2.5f;

            Vec3 newVelo = new Vec3((lookAngle.x * scaleMultiplier), (lookAngle.y * scaleMultiplier), (lookAngle.z * scaleMultiplier));

            //player.move(MoverType.SELF, newVelo);
            player.push(newVelo.x, newVelo.y, newVelo.z);

        }
    }


}
