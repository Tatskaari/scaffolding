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
import tatskaari.scaffolding.TileEntities.TileEntityElevatorBreak;
import tatskaari.scaffolding.TileEntities.TileEntityElevatorController;

import javax.annotation.Nullable;

public class BlockElevatorBreak extends BlockElevator {
    public BlockElevatorBreak(Material material) {
        super(material);
        setRegistryName("elevator_break");
        setUnlocalizedName("elevator_break");
        setHardness(0.5f);
    }

    @Override
    public TileEntity createNewTileEntity(World world, int i) {
        return new TileEntityElevatorBreak(Blocks.BRICK_BLOCK.getDefaultState());
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, @Nullable ItemStack stack, EnumFacing facing, float x, float y, float z) {
        if (!world.isRemote){
            TileEntity tileEntity = world.getTileEntity(pos);
            if(tileEntity instanceof TileEntityElevatorBreak){
                TileEntityElevatorBreak breakTileEntity = (TileEntityElevatorBreak)tileEntity;
                BlockPos controllerPos = breakTileEntity.getController().getPos();
                world.addBlockEvent(controllerPos, breakTileEntity.getController().getBlockType(), TileEntityElevatorController.MOVE_DOWN, 0);
            }
        }
        return true;
    }
}
