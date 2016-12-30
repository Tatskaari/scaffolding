package tatskaari.scaffolding.TileEntities;

import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;

public abstract class TileEntityElevatorPiece extends TileEntity {

    private final IBlockState blockStateToRender;

    public TileEntityElevatorPiece(IBlockState blockStateToRender){
        this.blockStateToRender = blockStateToRender;
    }

    public abstract double getYOffset();
    public abstract boolean isMoving();

    protected abstract double getProgress();

    public abstract void setController(TileEntityElevatorController controller);

    public IBlockState getBlockStateToRender(){
        return blockStateToRender;
    }
}
