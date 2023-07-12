package org.pichurose.smolchanges.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DoubleBlockCombiner;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.event.ForgeEventFactory;
import org.pichurose.smolchanges.utils.ResizingUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import virtuoel.pehkui.Pehkui;
import virtuoel.pehkui.util.PehkuiEntityExtensions;
import virtuoel.pehkui.util.ScaleUtils;

import java.util.Optional;


@Mixin(value = ForgeHooks.class, remap = false)
public class ForgeHooksMixin {

    @Inject(at = @At("HEAD"), method = "isLivingOnLadder", cancellable = true)
    private static void OnisLivingOnLadder(BlockState state, Level level, BlockPos pos, LivingEntity entity, CallbackInfoReturnable<Optional<BlockPos>> cir){
        boolean shouldBeLadder = false;

        BlockState xPlus =level.getBlockState( new BlockPos(entity.getPosition(1).add(entity.getBoundingBox().getXsize(), 0, 0)));
        BlockState xMinus =level.getBlockState( new BlockPos(entity.getPosition(1).subtract(entity.getBoundingBox().getXsize(), 0, 0)));
        BlockState zPlus =level.getBlockState( new BlockPos(entity.getPosition(1).add(0, 0, entity.getBoundingBox().getZsize())));
        BlockState zMinus =level.getBlockState( new BlockPos(entity.getPosition(1).subtract(0, 0, entity.getBoundingBox().getZsize())));

        shouldBeLadder = (! xPlus.isAir()) || (! xMinus.isAir()) || (! zPlus.isAir()) || (! zMinus.isAir());

        boolean isClimbableWithoutSlime = false;
        boolean hasSlime = false;
        if(entity instanceof Player){
            isClimbableWithoutSlime = isClimbableWithoutSlime((Player)entity, xPlus) || isClimbableWithoutSlime((Player)entity, xMinus) || isClimbableWithoutSlime((Player)entity, zPlus) || isClimbableWithoutSlime((Player)entity, zMinus);
            for(ItemStack i:((Player)entity).getHandSlots()){
                if(i.is(Items.SLIME_BALL)||i.is(Items.SLIME_BLOCK)||i.is(Items.STRING)||i.is(Items.HONEY_BLOCK)||i.is(Items.COBWEB)||i.is(Items.FISHING_ROD)||i.is(Items.CARROT_ON_A_STICK)||i.is(Items.WARPED_FUNGUS_ON_A_STICK)||i.is(Items.HONEYCOMB)||i.is(Items.HONEYCOMB_BLOCK)||i.is(Items.LEAD)){
                    hasSlime = true;
                }
            }
        }



        if((ResizingUtils.getActualSize(entity) <= .25) && (shouldBeLadder) && (hasSlime || isClimbableWithoutSlime)){
           cir.setReturnValue(Optional.of(pos));
        }

        /*if(hasSlime){
            cir.setReturnValue(Optional.of(pos));
        }*/

    }

    private static boolean isClimbableWithoutSlime(Player player, BlockState block){
        return ((!(block.isAir()) && (player.hasCorrectToolForDrops(block))));
    }


}
