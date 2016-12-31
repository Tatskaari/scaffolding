package tatskaari.scaffolding.TileEntities;

import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;

public abstract class TileEntityBasicElevatorPart extends TileEntity {

    private final IBlockState blockStateToRender;
    private TileEntityElevatorController controller;


    public TileEntityBasicElevatorPart(IBlockState blockStateToRender){
        this.blockStateToRender = blockStateToRender;
    }

    public double getYOffset() {
        if (controller == null){
            return 0;
        }
        return controller.getYOffset();
    }

    public double getYOffset(double partialTicks){
        if (controller == null){
            return 0;
        }
        return controller.getYOffset(partialTicks);
    }

    public IBlockState getBlockStateToRender(){
        return blockStateToRender;
    }

    public void setController(TileEntityElevatorController elevatorController) {
        controller = elevatorController;
    }

    public TileEntityElevatorController getController() {
        return controller;
    }

    public void onElevatorBlocksChanged(){
        if (controller !=null){
            controller.onElevatorBlocksChanged();
        }
    }
}
