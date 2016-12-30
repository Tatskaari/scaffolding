package tatskaari.scaffolding.TileEntities;

import net.minecraft.block.Block;
import net.minecraft.block.material.EnumPushReaction;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import tatskaari.scaffolding.blocks.BlockElevator;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class TileEntityElevatorController extends TileEntityElevatorPiece implements ITickable {
    public static final int MOVE_UP = 99100;
    public static final int MOVE_DOWN = 99110;

    private static final double SPEED = 0.05;

    private boolean isFirstTick = true;

    private double progress = 0;
    private double lastProgress = 0;
    private boolean moving = false;
    private boolean continueMoving = false;
    private int direction;

    private Map<BlockPos, IBlockState> elevatorBlocks = new TreeMap<BlockPos, IBlockState>();

    public TileEntityElevatorController(IBlockState blockStateToRender) {
        super(blockStateToRender);
    }


    @Override
    public void update() {
        if (isFirstTick){
            updateElevatorBlocks();
            isFirstTick = false;
        }
        if (moving){
            lastProgress = progress;
            progress+=SPEED;

            for (BlockPos elevatorPos : elevatorBlocks.keySet()){
                moveCollidedEntities(lastProgress, progress, elevatorPos);
            }

            if (progress >= 1){
                moveElevatorUp();
            }
        }
    }

    private void moveElevatorUp(){
        if (!worldObj.isRemote){
            for (BlockPos elevatorPos : elevatorBlocks.keySet()){
                worldObj.setBlockToAir(elevatorPos);
                worldObj.removeTileEntity(elevatorPos);
            }
            for (BlockPos elevatorPos : elevatorBlocks.keySet()){
                BlockPos nextPosition = elevatorPos.up();
                IBlockState blockState = elevatorBlocks.get(elevatorPos);
                worldObj.setBlockState(nextPosition, blockState);
            }
            progress = 0;
        }
    }




    @Override
    public double getYOffset(){
        if (direction == MOVE_UP){
            return progress;
        } else if (direction == MOVE_DOWN){
            return -progress;
        } else {
            return 0;
        }
    }

    public void toggleMovingUp(){
        if (!moving){
            Block blockAbove = worldObj.getBlockState(getPos().up()).getBlock();
            if (blockAbove == Blocks.AIR){
                moving = true;
                continueMoving = true;
                direction = MOVE_UP;
            }
        }
        else {
            continueMoving = false;
        }
    }

    @Override
    protected double getProgress() {
        return progress;
    }

    @Override
    public void setController(TileEntityElevatorController controller) {

    }

    private AxisAlignedBB getAABB(World world, BlockPos pos, double lastProgress, double progress){
        AxisAlignedBB restingBoundingBox = getBlockStateToRender().getBoundingBox(world, pos);
        AxisAlignedBB lastBoundingBox = restingBoundingBox.offset(0, lastProgress, 0);
        AxisAlignedBB newBoundingBox = restingBoundingBox.offset(0, progress, 0);

        return lastBoundingBox.union(newBoundingBox);
    }

    private void moveCollidedEntities(double lastProgress, double progress, BlockPos elevatorPos) {
        AxisAlignedBB liftAABB = getAABB(this.worldObj, elevatorPos, lastProgress, progress).offset(elevatorPos);
        List list = this.worldObj.getEntitiesWithinAABBExcludingEntity(null, liftAABB);
        if(!list.isEmpty()) {

            for(int i = 0; i < list.size(); ++i) {
                Entity entity = (Entity)list.get(i);
                if(entity.getPushReaction() != EnumPushReaction.IGNORE) {
                    double impulse = liftAABB.maxY - entity.getEntityBoundingBox().minY;
                    entity.moveEntity(0, impulse, 0);
                    entity.onGround = true;
                }
            }
        }

    }

    public boolean isMoving() {
        return moving;
    }

    public Map<BlockPos, IBlockState> getConnectedElevatorBlocks(World world, BlockPos pos, Map<BlockPos, IBlockState> elevators){
        elevators = addNeighborIfElevator(world, pos.north(), elevators);
        elevators = addNeighborIfElevator(world, pos.south(), elevators);
        elevators = addNeighborIfElevator(world, pos.east(), elevators);
        elevators = addNeighborIfElevator(world, pos.west(), elevators);
        elevators = addNeighborIfElevator(world, pos.up(), elevators);
        elevators = addNeighborIfElevator(world, pos.down(), elevators);
        return elevators;
    }

    public Map<BlockPos, IBlockState> addNeighborIfElevator(World world, BlockPos pos, Map<BlockPos, IBlockState> elevators){
        IBlockState state = world.getBlockState(pos);
        if (!elevators.containsKey(pos) && state.getBlock() instanceof BlockElevator){
            elevators.put(pos, state);
            elevators = getConnectedElevatorBlocks(world, pos, elevators);
        }
        return elevators;
    }

    public void updateElevatorBlocks(){
        elevatorBlocks.clear();
        elevatorBlocks.put(getPos(), worldObj.getBlockState(getPos()));
        elevatorBlocks = getConnectedElevatorBlocks(worldObj, pos, elevatorBlocks);
        for (BlockPos elevatorPos : elevatorBlocks.keySet()){
            TileEntity tileEntity = worldObj.getTileEntity(elevatorPos);
            if (tileEntity instanceof TileEntityElevatorPiece){
                ((TileEntityElevatorPiece) tileEntity).setController(this);
            }
        }
    }


}
