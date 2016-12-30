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
import tatskaari.scaffolding.blocks.BlockElevatorPlatform;

import java.util.List;

public class TileEntityElevatorPlatform extends TileEntity implements ITickable {
    private static final double SPEED = 0.05;

    private double progress = 0;
    private double lastProgress = 0;
    private long lastTickTime = System.currentTimeMillis();
    private long lastTickDuration = 0;
    private boolean moving = false;
    private boolean continueMoving = false;
    private int direction;
    private IBlockState blockStateToRender;

    public TileEntityElevatorPlatform setBlockStateToRender(IBlockState state){
        blockStateToRender = state;
        return this;
    }

    @Override
    public void update() {
        lastTickDuration = System.currentTimeMillis() - lastTickTime;
        lastTickTime = System.currentTimeMillis();

        if (moving){
            lastProgress = progress;
            progress+=SPEED;
            moveCollidedEntities(lastProgress, progress);

            if (progress >= 1){
                progress = 0;
                BlockPos nextPosition = getNextPosition();

                worldObj.setBlockState(nextPosition, getBlockType().getDefaultState());

                if (continueMoving){
                    worldObj.addBlockEvent(nextPosition, getBlockType(), BlockElevatorPlatform.MOVE_UP, 0);
                }

                worldObj.setBlockToAir(pos);
                worldObj.removeTileEntity(pos);
                invalidate();
            }
        }
    }


    private BlockPos getNextPosition(){
        if (direction == BlockElevatorPlatform.MOVE_UP){
            return pos.up();
        } else if (direction == BlockElevatorPlatform.MOVE_DOWN){
            return pos.down();
        } else {
            return null;
        }
    }

    public double getClientSideProgress(){
        if (lastTickDuration > 0){
            long timeSinceLastTick = System.currentTimeMillis() - lastTickTime;
            double partialTickFraction = timeSinceLastTick/lastTickDuration;
            return progress + partialTickFraction*SPEED;
        }
        return 0;

    }

    public double getOffset(){
        if (direction == BlockElevatorPlatform.MOVE_UP){
            return progress;
        } else if (direction == BlockElevatorPlatform.MOVE_DOWN){
            return -progress;
        } else {
            return 0;
        }
    }

    public double getClientSideOffSet(){
        if (direction == BlockElevatorPlatform.MOVE_UP){
            return getClientSideProgress();
        } else if (direction == BlockElevatorPlatform.MOVE_DOWN){
            return -getClientSideProgress();
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
                direction = BlockElevatorPlatform.MOVE_UP;
            }
        }
        else {
            continueMoving = false;
        }
    }

    public IBlockState getBlockStateToRender(){
        return blockStateToRender;
    }

    private AxisAlignedBB getAABB(World world, BlockPos pos, double lastProgress, double progress){
        AxisAlignedBB restingBoundingBox = blockStateToRender.getBoundingBox(world, pos);
        AxisAlignedBB lastBoundingBox = restingBoundingBox.offset(0, lastProgress, 0);
        AxisAlignedBB newBoundingBox = restingBoundingBox.offset(0, progress, 0);

        return lastBoundingBox.union(newBoundingBox);
    }

    private void moveCollidedEntities(double lastProgress, double progress) {
        AxisAlignedBB liftAABB = getAABB(this.worldObj, this.pos, lastProgress, progress).offset(this.pos);
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
}
