package org.pichurose.smolchanges.utils;

import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class CustomShapes {

    //public static final VoxelShape _SHAPE = null;
    public static final VoxelShape LONG_ROD_SHAPE = createCustomShape(7, 0, 7, 9, 16, 9);
    public static final VoxelShape MEDIUM_ROD_SHAPE = createCustomShape(7, 0, 7, 9, 16, 9);
    public static final VoxelShape SHORT_ROD_SHAPE = createCustomShape(7, 0, 7, 9, 10, 9);
    public static final VoxelShape EMPTY_SHAPE = Shapes.empty();
    public static final VoxelShape SLAB_SHAPE = createCustomShape(0, 0, 0, 16, 8, 16);
    public static final VoxelShape FULL_SHAPE = Shapes.block();

    public static final VoxelShape SIGN_SHAPE = join(LONG_ROD_SHAPE, createCustomShape(0, 9, 7, 16, 16, 9));
    public static final VoxelShape SIGN_SHAPE_2 = join(LONG_ROD_SHAPE, createCustomShape(7, 9, 0, 9, 16, 16));
    public static final VoxelShape FENCE_SHAPE = null;




    public static VoxelShape createCustomShape(int pX, int pY, int pZ, int pX2, int pY2, int pZ2){
        VoxelShape newShape = Shapes.create(pX/16f, pY/16f, pZ/16f, pX2/16f, pY2/16f, pZ2/16f);
        return newShape;
    }

    public static VoxelShape join(VoxelShape... shapes) {
        if (shapes.length == 0) return null;
        if (shapes.length == 1) return shapes[0];
        VoxelShape shape = shapes[0];
        for (int i = 1; i < shapes.length; i++) shape = Shapes.joinUnoptimized(shapes[i], shape, BooleanOp.OR);
        //joinUnoptimized(VoxelShape pShape1, VoxelShape pShape2, BooleanOp pFunction)
        return shape;
    }
}
