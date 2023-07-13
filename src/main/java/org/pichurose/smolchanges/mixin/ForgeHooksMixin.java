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
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
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

import java.util.List;
import java.util.Optional;


@Mixin(value = ForgeHooks.class, remap = false)
public class ForgeHooksMixin {

    @Inject(at = @At("HEAD"), method = "isLivingOnLadder", cancellable = true)
    private static void OnisLivingOnLadder(BlockState state, Level level, BlockPos pos, LivingEntity entity, CallbackInfoReturnable<Optional<BlockPos>> cir){
        boolean shouldBeLadder = false;
        boolean shapeContainsProbe = false;
        float closeEnough = 1.01f;
        Vec3 xPlusV = entity.getPosition(1).add(entity.getBoundingBox().getXsize()*closeEnough, 0, 0);
        BlockPos xPlusPos = new BlockPos(xPlusV);
        BlockState xPlus =level.getBlockState( xPlusPos);
        Vec3 xMinusV =  entity.getPosition(1).subtract(entity.getBoundingBox().getXsize()*closeEnough, 0, 0);
        BlockPos xMinusPos = new BlockPos(xMinusV);
        BlockState xMinus =level.getBlockState( xMinusPos);
        Vec3 zPlusV = entity.getPosition(1).add(0, 0, entity.getBoundingBox().getZsize()*closeEnough);
        BlockPos zPlusPos =new BlockPos(zPlusV);
        BlockState zPlus =level.getBlockState( zPlusPos);
        Vec3 zMinusV = entity.getPosition(1).subtract(0, 0, entity.getBoundingBox().getZsize()*closeEnough);
        BlockPos zMinusPos = new BlockPos(zMinusV);
        BlockState zMinus =level.getBlockState( zMinusPos);

        VoxelShape xPlusCol = state.getCollisionShape(level, xPlusPos, CollisionContext.of(entity));
        VoxelShape xMinusCol = state.getCollisionShape(level, xMinusPos, CollisionContext.of(entity));
        VoxelShape zPlusCol = state.getCollisionShape(level, zPlusPos, CollisionContext.of(entity));
        VoxelShape zMinusCol = state.getCollisionShape(level, zMinusPos, CollisionContext.of(entity));

        if(xPlus.getBlock().isCollisionShapeFullBlock(xPlus, level, xPlusPos)){
            shapeContainsProbe = true;
        }
        else if(xMinus.getBlock().isCollisionShapeFullBlock(xMinus, level, xPlusPos)){
            shapeContainsProbe = true;
        }
        else if(zPlus.getBlock().isCollisionShapeFullBlock(zPlus, level, xPlusPos)){
            shapeContainsProbe = true;
        }
        else if(zMinus.getBlock().isCollisionShapeFullBlock(zMinus, level, xPlusPos)){
            shapeContainsProbe = true;
        }
        else{
            //xPlus
            if(!shapeContainsProbe){
                for (AABB a : xPlusCol.move(xPlusPos.getX(), xPlusPos.getY(), xPlusPos.getZ()).toAabbs()) {
                    if (a.contains(xPlusV)) {
                        shapeContainsProbe = true;
                    }
                    if (shapeContainsProbe) {
                        break;
                    }
                }
            }
            //xMinus
            if(!shapeContainsProbe){
                for (AABB a : xMinusCol.move(xMinusPos.getX(), xMinusPos.getY(), xMinusPos.getZ()).toAabbs()) {
                    if (a.contains(xMinusV)) {
                        shapeContainsProbe = true;
                    }
                    if (shapeContainsProbe) {
                        break;
                    }
                }
            }
            //zPlus
            if(!shapeContainsProbe){
                for (AABB a : zPlusCol.move(zPlusPos.getX(), zPlusPos.getY(), zPlusPos.getZ()).toAabbs()) {
                    if (a.contains(zPlusV)) {
                        shapeContainsProbe = true;
                    }
                    if (shapeContainsProbe) {
                        break;
                    }
                }
            }
            //zMinus
            if(!shapeContainsProbe){
                for (AABB a : zMinusCol.move(zMinusPos.getX(), zMinusPos.getY(), zMinusPos.getZ()).toAabbs()) {
                    if (a.contains(zMinusV)) {
                        shapeContainsProbe = true;
                    }
                    if (shapeContainsProbe) {
                        break;
                    }
                }
            }






        }


        //shapeContainsProbe = .contains(xPlusV) || collision.toAabbs().contains(xMinus) || collision.toAabbs().contains(zPlus) || collision.toAabbs().contains(zMinus);
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



        if((ResizingUtils.getActualSize(entity) <= .25) && (shouldBeLadder) && (hasSlime || isClimbableWithoutSlime) && shapeContainsProbe){
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
