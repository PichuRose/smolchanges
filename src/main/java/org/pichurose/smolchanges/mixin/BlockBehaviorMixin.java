package org.pichurose.smolchanges.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.TorchBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.EntityCollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.pichurose.smolchanges.utils.ResizingUtils;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import virtuoel.pehkui.util.ScaleUtils;

import java.util.Optional;

@Mixin(BlockBehaviour.class)
public abstract class BlockBehaviorMixin {

    @Shadow @Deprecated public abstract VoxelShape getCollisionShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext);

    @Shadow @Deprecated public abstract VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext);

    @Shadow @Final protected boolean hasCollision;

    @Shadow protected abstract boolean isAir(BlockState state);

    @Shadow protected abstract Block asBlock();

    @Inject(at = @At("HEAD"), method = "getCollisionShape", cancellable = true)
    public void onGetCollisionShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext, CallbackInfoReturnable<VoxelShape> cir) {
        //return this.hasCollision ? pState.getShape(pLevel, pPos) : Shapes.empty();

        if (pContext instanceof EntityCollisionContext && ResizingUtils.getActualSize(((EntityCollisionContext) pContext).getEntity()) <=0.25) {
            @SuppressWarnings("ConstantValue") boolean isLeaves = ((Object) this) instanceof LeavesBlock;
            boolean isUnsolidSelectable = false;
            VoxelShape voxelShape = null;

            if (!this.hasCollision){
                voxelShape = this.getShape(pState, pLevel, pPos, pContext);
                isUnsolidSelectable = voxelShape != Shapes.empty() &&  !voxelShape.isEmpty();
            }

            Entity entity = ((EntityCollisionContext) pContext).getEntity();
            if (entity == null) {
                return;
            }
            if(isLeaves){
                boolean topNotEmpty = false;

                topNotEmpty = (!(entity.getBlockY()>(pPos.getY()+0.5f)&&!(entity.isCrouching())&& ResizingUtils.getActualSize(entity) <=0.25));

                if(topNotEmpty && ResizingUtils.getActualSize(entity) <=0.25){

                    cir.setReturnValue(Shapes.empty());
                }

            }
            else if(isUnsolidSelectable && voxelShape!= null){
                int pX = 0, pY = 0, pZ = 0, pX2 = 0, pY2 = 0, pZ2 = 0;
                //noinspection ConstantValue
                if (((Object) this) instanceof TorchBlock){
                    pX = 7;
                    pY = 0;
                    pZ = 7;
                    pX2 = 9;
                    pY2 = 10;
                    pZ2 = 9;
                }
                VoxelShape newShape = Shapes.create(pX/16f, pY/16f, pZ/16f, pX2/16f, pY2/16f, pZ2/16f);
                if (newShape != Shapes.create(0,0,0,0,0,0)) {
                    cir.setReturnValue(newShape);
                    return;
                }
                cir.setReturnValue(voxelShape);
            }


        }
        else{
            return;
        }


    }

}
