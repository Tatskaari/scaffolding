package tatskaari.scaffolding.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import tatskaari.scaffolding.Scaffolding;

import javax.annotation.Nullable;


public class BlockScaffold extends Block {
    public static final String REGISTRY_NAME = "scaffold";
    public static final AxisAlignedBB CENTER_AABB = new AxisAlignedBB(0.42D, 0.0D, 0.42D, 0.58D, 1.0D, 0.58D);
    public static final AxisAlignedBB SOUTH_AABB = new AxisAlignedBB(0.42D, 0.0D, 0.58D, 0.58D, 1.0D, 1.0D);
    public static final AxisAlignedBB WEST_AABB = new AxisAlignedBB(0.0D, 0.0D, 0.42D, 0.42D, 1.0D, 0.58D);
    public static final AxisAlignedBB NORTH_AABB = new AxisAlignedBB(0.42D, 0.0D, 0.0D, 0.58D, 1.0D, 0.42D);
    public static final AxisAlignedBB EAST_AABB = new AxisAlignedBB(0.58D, 0.0D, 0.42D, 1.0D, 1.0D, 0.58D);

    public static final PropertyBool northConnection = PropertyBool.create("north");
    public static final PropertyBool southConnection = PropertyBool.create("south");
    public static final PropertyBool eastConnection = PropertyBool.create("east");
    public static final PropertyBool westConnection = PropertyBool.create("west");

    public BlockScaffold() {
        super(Material.WOOD);
        setRegistryName(REGISTRY_NAME);
        setCreativeTab(CreativeTabs.BUILDING_BLOCKS);
        setUnlocalizedName("scaffolding");
        setHardness(0.075f);
        IBlockState defaultState = blockState.getBaseState()
                .withProperty(northConnection, false)
                .withProperty(southConnection, false)
                .withProperty(eastConnection, false)
                .withProperty(westConnection, false);

        setDefaultState(defaultState);
    }

    @SideOnly(Side.CLIENT)
    public boolean shouldSideBeRendered(IBlockState p_shouldSideBeRendered_1_, IBlockAccess p_shouldSideBeRendered_2_, BlockPos p_shouldSideBeRendered_3_, EnumFacing p_shouldSideBeRendered_4_) {
        return true;
    }

    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess access, BlockPos pos) {
        AxisAlignedBB boundingBox = CENTER_AABB;
        IBlockState actualState = getActualState(state, access, pos);

        if (actualState.getValue(northConnection)){
            boundingBox = boundingBox.union(NORTH_AABB);
        }
        if (actualState.getValue(eastConnection)){
            boundingBox = boundingBox.union(EAST_AABB);
        }
        if (actualState.getValue(southConnection)){
            boundingBox = boundingBox.union(SOUTH_AABB);
        }
        if (actualState.getValue(westConnection)){
            boundingBox = boundingBox.union(WEST_AABB);
        }

        return boundingBox;
    }

    @Override
    public boolean isFullBlock(IBlockState p_isFullBlock_1_) {
        return false;
    }

    @Override
    public boolean isFullCube(IBlockState p_isFullCube_1_) {
        return false;
    }

    @Override
    public boolean isFullyOpaque(IBlockState p_isFullyOpaque_1_) {
        return false;
    }

    @Override
    public boolean isVisuallyOpaque() {
        return false;
    }

    @Override
    public boolean isOpaqueCube(IBlockState p_isOpaqueCube_1_) {
        return false;
    }


    @SideOnly(Side.CLIENT)
    public BlockRenderLayer getBlockLayer() {
        return BlockRenderLayer.CUTOUT;
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, @Nullable ItemStack heldItem, EnumFacing side, float hitX, float hitY, float hitZ){

        if (heldItem != null && heldItem.getItem() == Scaffolding.ITEM_SCAFFOLD){
            if (!world.isRemote){
                placeScaffoldingAbove(world, pos, heldItem, player);
            }
            return true;
        }
        return false;
    }

    @Override
    public void onBlockDestroyedByPlayer(World world, BlockPos pos, IBlockState state) {
        if (!world.isRemote){
            world.setBlockState(pos, getDefaultState());
            harvestScaffolding(world, pos);
        }
    }

    private void harvestScaffolding(World world, BlockPos pos){
        BlockPos above = new BlockPos(pos.getX(), pos.getY()+1, pos.getZ());
        Block aboveNeighbouringBlock = Minecraft.getMinecraft().theWorld.getBlockState(above).getBlock();

        if (aboveNeighbouringBlock == Scaffolding.BLOCK_SCAFFOLD){
            harvestScaffolding(world, above);
        } else {
            world.setBlockState(pos, Blocks.AIR.getDefaultState());
        }

    }

    private void placeScaffoldingAbove(World world, BlockPos pos, ItemStack scaffoldStack, EntityPlayer player){
        BlockPos above = new BlockPos(pos.getX(), pos.getY()+1, pos.getZ());
        Block aboveNeighbouringBlock = Minecraft.getMinecraft().theWorld.getBlockState(above).getBlock();

        if (aboveNeighbouringBlock == Blocks.AIR && canPlaceBlockAt(world, above)){
            world.setBlockState(above, Scaffolding.BLOCK_SCAFFOLD.getDefaultState());
            if (!player.isCreative()){
                scaffoldStack.stackSize--;
            }
        } else if (aboveNeighbouringBlock == Scaffolding.BLOCK_SCAFFOLD){
            placeScaffoldingAbove(world, above, scaffoldStack, player);
        }
    }

    @Override
    public boolean isLadder(IBlockState p_isLadder_1_, IBlockAccess p_isLadder_2_, BlockPos p_isLadder_3_, EntityLivingBase p_isLadder_4_) {
        return true;
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, northConnection, eastConnection, southConnection, westConnection);
    }

    @Override
    public int getMetaFromState(IBlockState p_getMetaFromState_1_) {
        return 0;
    }

    @Override
    public IBlockState getStateFromMeta(int p_getStateFromMeta_1_) {
        return getDefaultState();
    }

    @Override
    public IBlockState getActualState(IBlockState state, IBlockAccess blockAccess, BlockPos pos) {

        boolean hasScaffoldNorth = blockAccess.getBlockState(pos.north()).getBlock() == state.getBlock();
        boolean hasScaffoldEast = blockAccess.getBlockState(pos.east()).getBlock() == state.getBlock();
        boolean hasScaffoldSouth = blockAccess.getBlockState(pos.south()).getBlock() == state.getBlock();
        boolean hasScaffoldWest = blockAccess.getBlockState(pos.west()).getBlock() == state.getBlock();

        return state
                .withProperty(northConnection, hasScaffoldNorth)
                .withProperty(eastConnection, hasScaffoldEast)
                .withProperty(southConnection, hasScaffoldSouth)
                .withProperty(westConnection, hasScaffoldWest);

    }

}
