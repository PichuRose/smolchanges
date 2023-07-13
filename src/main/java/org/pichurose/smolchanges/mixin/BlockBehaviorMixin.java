package org.pichurose.smolchanges.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.SignBlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.EntityCollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.pichurose.smolchanges.utils.CustomShapes;
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
            VoxelShape newShape = null;
            if (!this.hasCollision){
                voxelShape = this.getShape(pState, pLevel, pPos, pContext);
                isUnsolidSelectable = voxelShape != Shapes.empty() &&  !voxelShape.isEmpty();
            }
            else{

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
                    return;
                }

            }

            else if(isUnsolidSelectable && voxelShape!= null){
                if (((Object) this) instanceof TorchBlock){
                    newShape = CustomShapes.SHORT_ROD_SHAPE;
                }

                else if (((Object) this) instanceof SignBlock){
                    if (!(((Object) this) instanceof WallSignBlock)){
                        int rotation = pState.getValue(BlockStateProperties.ROTATION_16);
                        switch(rotation){
                            case 0: case 8:
                                newShape = CustomShapes.SIGN_SHAPE;
                                break;
                            case 4: case 12:
                                newShape = CustomShapes.SIGN_SHAPE_2;
                                break;

                        }
                    }
                }

                else if (((Object) this) instanceof WebBlock){
                    newShape = CustomShapes.EMPTY_SHAPE;
                }

                else if (((Object) this) instanceof SaplingBlock){
                    newShape = CustomShapes.MEDIUM_ROD_SHAPE;
                }

                if (newShape != null) {
                    cir.setReturnValue(newShape);
                    return;
                }
                cir.setReturnValue(voxelShape);
            }
            else{

                if (newShape != null) {
                    cir.setReturnValue(newShape);
                    return;
                }
                if (voxelShape != null) {
                    cir.setReturnValue(voxelShape);
                }
                else{
                    return;
                }
            }

        }
        else{
            return;
        }


    }

}
