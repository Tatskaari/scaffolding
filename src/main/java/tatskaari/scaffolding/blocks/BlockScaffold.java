package tatskaari.scaffolding.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
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
    public static final AxisAlignedBB boundingBox = new AxisAlignedBB(0.4D, 0.0D, 0.4D, 0.6D, 1.0D, 0.6D);

    public BlockScaffold() {
        super(Material.WOOD);
        setRegistryName(REGISTRY_NAME);
        setCreativeTab(CreativeTabs.BUILDING_BLOCKS);
        setUnlocalizedName("scaffolding");
        setHardness(0.075f);
    }

    @SideOnly(Side.CLIENT)
    public boolean shouldSideBeRendered(IBlockState p_shouldSideBeRendered_1_, IBlockAccess p_shouldSideBeRendered_2_, BlockPos p_shouldSideBeRendered_3_, EnumFacing p_shouldSideBeRendered_4_) {
        return true;
    }

    @Override
    public AxisAlignedBB getBoundingBox(IBlockState p_getBoundingBox_1_, IBlockAccess p_getBoundingBox_2_, BlockPos p_getBoundingBox_3_) {
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
}
