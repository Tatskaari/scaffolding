package tatskaari.scaffolding.blocks;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import tatskaari.scaffolding.TileEntities.TileEntityElevatorController;

import javax.annotation.Nullable;

public class BlockElevatorCrank extends BlockElevator {
    public BlockElevatorCrank(Material material) {
        super(material);
        setRegistryName("elevator_crank");
        setUnlocalizedName("elevator_crank");
        setHardness(0.5f);
    }

    @Override
    public TileEntity createNewTileEntity(World world, int i) {
        return new TileEntityElevatorController(Blocks.WOOL.getDefaultState());
    }

    @Override
    public boolean eventReceived(IBlockState state, World world, BlockPos pos, int message1, int message2) {
        TileEntity tileEntity = world.getTileEntity(pos);
        if (tileEntity instanceof TileEntityElevatorController){
            return ((TileEntityElevatorController)tileEntity).processMessage(message1);
        }

        return false;
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand enumHand, @Nullable ItemStack handItem, EnumFacing facing, float x, float y, float z) {
        if (!world.isRemote){
            world.addBlockEvent(pos, this, TileEntityElevatorController.MOVE_UP, 0);
        }
        return true;
    }
}
