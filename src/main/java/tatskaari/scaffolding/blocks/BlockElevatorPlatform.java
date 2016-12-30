package tatskaari.scaffolding.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import tatskaari.scaffolding.TileEntities.TileEntityElevatorPlatform;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class BlockElevatorPlatform extends BlockContainer {

    public static final int MOVE_UP = 99100;
    public static final int MOVE_DOWN = 99110;

    public BlockElevatorPlatform(Material material) {
        super(material);
        setRegistryName("animation_test");
        setUnlocalizedName("animation_test");
        setHardness(0.2f);

    }

    @Override
    public TileEntity createNewTileEntity(World world, int i) {
        return new TileEntityElevatorPlatform().setBlockStateToRender(Blocks.PLANKS.getDefaultState());
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, @Nullable ItemStack handItem, EnumFacing facing, float xHit, float yHit, float zHit) {
        if (!world.isRemote){
            toggleMovingPlatformUp(world, pos);
        }
        return true;
    }

    private void toggleMovingPlatformUp(World world, BlockPos pos){
        List<BlockPos> elevators = new ArrayList<BlockPos>();
        elevators.add(pos);
        elevators = getConnectedElevators(world, pos, elevators);
        for (BlockPos elevator : elevators) {
            world.addBlockEvent(elevator, this, MOVE_UP, 0);
        }
    }

    public List<BlockPos> getConnectedElevators(World world, BlockPos pos, List<BlockPos> elevators){
        elevators = addNeighborIfElevator(world, pos.north(), elevators);
        elevators = addNeighborIfElevator(world, pos.south(), elevators);
        elevators = addNeighborIfElevator(world, pos.east(), elevators);
        elevators = addNeighborIfElevator(world, pos.west(), elevators);
        //elevators = addNeighborIfElevator(world, pos.up(), elevators);
        //elevators = addNeighborIfElevator(world, pos.down(), elevators);
        return elevators;
    }

    public List<BlockPos> addNeighborIfElevator(World world, BlockPos pos, List<BlockPos> elevators){
        if (!elevators.contains(pos) && world.getBlockState(pos).getBlock() == this){
            elevators.add(pos);
            elevators = getConnectedElevators(world, pos, elevators);
        }
        return elevators;
    }

    @Override
    public boolean eventReceived(IBlockState state, World world, BlockPos pos, int message1, int message2) {
        if (message1 == MOVE_UP){
            TileEntity tileEntity = world.getTileEntity(pos);
            if (tileEntity instanceof TileEntityElevatorPlatform){
                ((TileEntityElevatorPlatform)tileEntity).toggleMovingUp();
                return true;
            }
        }
        return false;
    }

    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess access, BlockPos pos) {
        TileEntity tileEntity = access.getTileEntity(pos);

        if (tileEntity instanceof TileEntityElevatorPlatform){
            TileEntityElevatorPlatform tileEntityElevatorPlatform = (TileEntityElevatorPlatform)tileEntity;
            IBlockState renderState = tileEntityElevatorPlatform.getBlockStateToRender();
            if(tileEntityElevatorPlatform.isMoving()){
                return renderState.getBoundingBox(access, pos).offset(0, tileEntityElevatorPlatform.getOffset(), 0);
            }
        }

        return FULL_BLOCK_AABB;
    }

    @Override
    public void neighborChanged(IBlockState state, World world, BlockPos pos, Block block) {
        if(!world.isRemote && world.isBlockPowered(pos)){
            toggleMovingPlatformUp(world, pos);
        }
    }
}
