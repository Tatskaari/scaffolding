package tatskaari.scaffolding.TileEntities;

import net.minecraft.block.material.EnumPushReaction;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import tatskaari.scaffolding.ModElevators;
import tatskaari.scaffolding.blocks.BlockElevator;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class TileEntityElevatorController extends TileEntityBasicElevatorPart implements ITickable {
    public static final int MOVE_UP = 99100;
    public static final int MOVE_DOWN = 99110;

    public static final double SPEED = 0.05;

    private boolean isFirstTick = true;

    private double progress = 0;
    public double partialTick = 0;
    private boolean moving = false;
    private boolean continueMoving = false;
    private boolean neighborsUpToDate = true;
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
            double lastProgress = progress;
            progress+=SPEED;

            for (BlockPos elevatorPos : elevatorBlocks.keySet()){
                moveCollidedEntities(lastProgress, progress, elevatorPos);
            }

            if (progress >= 1){
                moveElevator(direction);
            }
        }
        if (!neighborsUpToDate){
            updateElevatorBlocks();
        }
    }

    private void moveElevator(int direction) {
        if (!worldObj.isRemote) {
            for (Iterator<BlockPos> it = elevatorBlocks.keySet().iterator(); it.hasNext();) {
                BlockPos elevatorPos = it.next();
                if (worldObj.isAirBlock(elevatorPos)){
                    it.remove();
                } else {
                    worldObj.setBlockToAir(elevatorPos);
                    worldObj.removeTileEntity(elevatorPos);
                }
            }
            for (BlockPos elevatorPos : elevatorBlocks.keySet()) {
                BlockPos nextPosition;
                if (direction == MOVE_DOWN){
                    nextPosition = elevatorPos.down();
                } else {
                    nextPosition = elevatorPos.up();
                }
                IBlockState blockState = elevatorBlocks.get(elevatorPos);
                worldObj.setBlockState(nextPosition, blockState);
                if (continueMoving && blockState.getBlock() == ModElevators.BLOCK_ELEVATOR_CRANK) {
                    worldObj.addBlockEvent(nextPosition, ModElevators.BLOCK_ELEVATOR_CRANK, direction, 0);
                }
            }
            progress = 0;
            TileEntity entity = worldObj.getTileEntity(pos.up());
            if (entity instanceof TileEntityElevatorController){
                ((TileEntityElevatorController) entity).updateElevatorBlocks();
            }
        }
    }

    @Override
    public double getYOffset(){
        return getOffsetForProgress(progress);
    }

    public double getOffsetForProgress(double progress){
        if (direction == MOVE_UP){
            return progress;
        } else if (direction == MOVE_DOWN){
            return -progress;
        } else {
            return 0;
        }
    }

    @Override
    public double getYOffset(double partialTicks) {
        double progress = this.progress + SPEED*partialTicks;
        return getOffsetForProgress(progress);
    }

    private boolean canMove(int direction){
        updateElevatorBlocks();

        for (BlockPos pos : elevatorBlocks.keySet()){
            BlockPos nextBlockPos;

            if (direction == MOVE_DOWN){
                nextBlockPos = pos.down();
            } else {
                nextBlockPos = pos.up();
            }

            IBlockState nextBlockState = worldObj.getBlockState(nextBlockPos);
            if (!(elevatorBlocks.containsKey(nextBlockPos) || nextBlockState == Blocks.AIR.getDefaultState())){
                return false;
            }
        }

        return true;
    }

    public void toggleMoving(int direction){
        if (!moving){
            if (canMove(direction)){
                moving = true;
                continueMoving = true;
                this.direction = direction;
            }
        }
        else {
            continueMoving = false;
        }
    }

    @Override
    public void setController(TileEntityElevatorController controller) {

    }

    @Override
    public TileEntityElevatorController getController() {
        return this;
    }

    private AxisAlignedBB getAABB(World world, BlockPos pos, double lastProgress, double progress){
        double directionMultiple = MOVE_DOWN == direction ? -1 : 1;
        AxisAlignedBB restingBoundingBox = getBlockStateToRender().getBoundingBox(world, pos);
        AxisAlignedBB lastBoundingBox = restingBoundingBox.offset(0, lastProgress*directionMultiple, 0);
        AxisAlignedBB newBoundingBox = restingBoundingBox.offset(0, progress*directionMultiple, 0);

        return lastBoundingBox.union(newBoundingBox);
    }

    private void moveCollidedEntities(double lastProgress, double progress, BlockPos elevatorPos) {
        AxisAlignedBB liftAABB = getAABB(worldObj, elevatorPos, lastProgress, progress).offset(elevatorPos);
        List list = this.worldObj.getEntitiesWithinAABBExcludingEntity(null, liftAABB);
        if(!list.isEmpty()) {

            for(int i = 0; i < list.size(); ++i) {
                Entity entity = (Entity)list.get(i);
                if(entity.getPushReaction() != EnumPushReaction.IGNORE) {
                    double impulse;
                    if (direction == MOVE_DOWN){
                        impulse = entity.getEntityBoundingBox().minY - liftAABB.maxY;
                    } else {
                        impulse = liftAABB.maxY - entity.getEntityBoundingBox().minY;
                    }
                    entity.motionY = impulse;
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

    private void updateElevatorBlocks(){
        elevatorBlocks.clear();
        elevatorBlocks.put(getPos(), worldObj.getBlockState(getPos()));
        elevatorBlocks = getConnectedElevatorBlocks(worldObj, pos, elevatorBlocks);
        for (BlockPos elevatorPos : elevatorBlocks.keySet()){
            TileEntity tileEntity = worldObj.getTileEntity(elevatorPos);
            if (tileEntity instanceof TileEntityBasicElevatorPart){
                ((TileEntityBasicElevatorPart) tileEntity).setController(this);
            }
        }
        neighborsUpToDate = true;
    }


    public boolean processMessage(int message) {
        if (message == MOVE_DOWN || message == MOVE_UP){
            toggleMoving(message);
            return true;
        }
        return false;
    }

    @Override
    public void onElevatorBlocksChanged() {
        neighborsUpToDate = false;
    }
}
