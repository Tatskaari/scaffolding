package tatskaari.scaffolding.TileEntities;

import net.minecraft.block.state.IBlockState;

public class TileEntityElevatorPlatform extends TileEntityBasicElevatorPart {
    private TileEntityElevatorController controller;

    public TileEntityElevatorPlatform(IBlockState blockStateToRender) {
        super(blockStateToRender);
    }


}
