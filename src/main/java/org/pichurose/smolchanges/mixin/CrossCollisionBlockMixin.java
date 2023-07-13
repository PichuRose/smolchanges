package org.pichurose.smolchanges.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CrossCollisionBlock;
import net.minecraft.world.level.block.FenceBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.EntityCollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.pichurose.smolchanges.utils.CustomShapes;
import org.pichurose.smolchanges.utils.ResizingUtils;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(CrossCollisionBlock.class)
public abstract class CrossCollisionBlockMixin {

    @Shadow protected abstract int getAABBIndex(BlockState pState);

    @Shadow @Final protected VoxelShape[] collisionShapeByIndex;
    @Unique
    VoxelShape fenceShape[] = null;

    @Inject(at = @At("HEAD"), method = "getCollisionShape", cancellable = true)
    public void onGetCollisionShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext, CallbackInfoReturnable<VoxelShape> cir) {
        if (pContext instanceof EntityCollisionContext && ResizingUtils.getActualSize(((EntityCollisionContext) pContext).getEntity()) <=0.25) {
            VoxelShape voxelShape = ((Block)((Object)this)).getShape(pState, pLevel, pPos, pContext);
            Entity entity = ((EntityCollisionContext) pContext).getEntity();
            if (entity == null) {
                return;
            }
            if(((Block)((Object)this)) instanceof FenceBlock){
                if (fenceShape == null) {
                    //fenceShape = CustomShapes.subtract(voxelShape, CustomShapes.createCustomShape(0, 0, 0, 6, 6, 16), CustomShapes.createCustomShape(0, 0, 0, 16, 6, 6), CustomShapes.createCustomShape(10, 0, 0, 16, 6, 16), CustomShapes.createCustomShape(0, 0, 10, 16, 6, 16));
                    //fenceShape = CustomShapes.LONG_ROD_SHAPE_4;
                    fenceShape = new VoxelShape[collisionShapeByIndex.length];
                    for (int i = 0; i < collisionShapeByIndex.length; i++) {
                        VoxelShape newVoxel = CustomShapes.subtract(collisionShapeByIndex[i], CustomShapes.createCustomShape(0, 0, 0, 6, 6, 16), CustomShapes.createCustomShape(0, 0, 0, 16, 6, 6), CustomShapes.createCustomShape(10, 0, 0, 16, 6, 16), CustomShapes.createCustomShape(0, 0, 10, 16, 6, 16), CustomShapes.createCustomShape(0, 16, 0, 16, 32, 16));
                        fenceShape[i] = newVoxel;
                    }
                }
            }
            cir.setReturnValue(this.fenceShape[this.getAABBIndex(pState)]);
        }
    }






}
