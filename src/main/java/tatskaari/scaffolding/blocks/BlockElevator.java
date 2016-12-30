package tatskaari.scaffolding.blocks;

import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import tatskaari.scaffolding.TileEntities.TileEntityElevatorPiece;

public abstract class BlockElevator extends BlockContainer {
    protected BlockElevator(Material p_i45386_1_) {
        super(p_i45386_1_);
    }

    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess access, BlockPos pos) {
        TileEntity tileEntity = access.getTileEntity(pos);

        if (tileEntity instanceof TileEntityElevatorPiece){
            TileEntityElevatorPiece elevatorPiece = (TileEntityElevatorPiece)tileEntity;
            return FULL_BLOCK_AABB.offset(0, elevatorPiece.getYOffset(), 0);
        } else {
            return FULL_BLOCK_AABB;
        }
    }
}
