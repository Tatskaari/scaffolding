package tatskaari.scaffolding.blocks;

import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import tatskaari.scaffolding.TileEntities.TileEntityElevatorPlatform;

public class BlockElevatorPlatform extends BlockElevator {

    public BlockElevatorPlatform(Material material) {
        super(material);
        setRegistryName("elevator_platform");
        setUnlocalizedName("elevator_platform");
        setHardness(0.2f);

    }

    @Override
    public TileEntity createNewTileEntity(World world, int i) {
        return new TileEntityElevatorPlatform(Blocks.PLANKS.getDefaultState());
    }
}
