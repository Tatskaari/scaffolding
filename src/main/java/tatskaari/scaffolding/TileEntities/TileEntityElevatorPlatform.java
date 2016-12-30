package tatskaari.scaffolding.TileEntities;

import net.minecraft.block.state.IBlockState;

public class TileEntityElevatorPlatform extends TileEntityElevatorPiece {
    private TileEntityElevatorController controller;

    public TileEntityElevatorPlatform(IBlockState blockStateToRender) {
        super(blockStateToRender);
    }

    @Override
    public double getYOffset() {
        if (controller == null){
            return 0;
        }
        return controller.getYOffset();
    }

    @Override
    public boolean isMoving(){
        if (controller == null){
            return false;
        }
        return controller.isMoving();
    }

    @Override
    protected double getProgress() {
        if (controller == null){
            return 0;
        }
        return controller.getProgress();
    }

    @Override
    public void setController(TileEntityElevatorController elevatorController) {
        controller = elevatorController;
    }


}
