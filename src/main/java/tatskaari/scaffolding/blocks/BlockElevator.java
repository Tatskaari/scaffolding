package tatskaari.scaffolding.blocks;

import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import tatskaari.scaffolding.TileEntities.TileEntityBasicElevatorPart;
import tatskaari.scaffolding.TileEntities.TileEntityElevatorController;

public abstract class BlockElevator extends BlockContainer {
    protected BlockElevator(Material material) {
        super(material);
    }

    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess access, BlockPos pos) {
        TileEntity tileEntity = access.getTileEntity(pos);

        if (tileEntity instanceof TileEntityBasicElevatorPart){
            TileEntityBasicElevatorPart elevatorPiece = (TileEntityBasicElevatorPart)tileEntity;
            if (elevatorPiece.getController() != null){
                TileEntityElevatorController controller = elevatorPiece.getController();
//                if (controller.isMoving()){
//                    controller.moveCollidedEntities(controller.getProgress(), controller.getPatialTickProgress(), pos);
//                }
                return FULL_BLOCK_AABB.offset(0, controller.getYOffset(controller.partialTick), 0);
            } else {
                return FULL_BLOCK_AABB;
            }
        } else {
            return FULL_BLOCK_AABB;
        }
    }

    @Override
    public void onNeighborChange(IBlockAccess access, BlockPos pos, BlockPos neighborPos) {
        IBlockState neighborState = access.getBlockState(neighborPos);
        if (neighborState.getBlock() instanceof BlockElevator){
            TileEntity tileEntity = access.getTileEntity(pos);
            if (tileEntity instanceof TileEntityBasicElevatorPart){
                ((TileEntityBasicElevatorPart)tileEntity).onElevatorBlocksChanged();
            }
        }
    }
}
