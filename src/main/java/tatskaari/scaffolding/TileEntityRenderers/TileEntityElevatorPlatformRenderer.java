package tatskaari.scaffolding.TileEntityRenderers;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import tatskaari.scaffolding.TileEntities.TileEntityElevatorPlatform;

public class TileEntityElevatorPlatformRenderer extends TileEntitySpecialRenderer<TileEntityElevatorPlatform>{
    @Override
    @SideOnly(Side.CLIENT)
    public void renderTileEntityAt(TileEntityElevatorPlatform tileEntityElevatorPlatform, double xPos, double yPos, double zPos, float p_renderTileEntityAt_6_, int p_renderTileEntityAt_6_2) {
        IBlockState blockStateToRender = tileEntityElevatorPlatform.getBlockStateToRender();
        World world = tileEntityElevatorPlatform.getWorld();
        Tessellator tessellator = Tessellator.getInstance();
        VertexBuffer vertexBuffer = tessellator.getBuffer();
        BlockPos blockPos = tileEntityElevatorPlatform.getPos();

        double offset = tileEntityElevatorPlatform.getClientSideOffSet();

        this.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);

        if(Minecraft.isAmbientOcclusionEnabled()) {
            GlStateManager.shadeModel(7425);
        } else {
            GlStateManager.shadeModel(7424);
        }

        vertexBuffer.begin(7, DefaultVertexFormats.BLOCK);
        vertexBuffer.setTranslation(
                (xPos - (double)blockPos.getX()),
                (yPos - (double)blockPos.getY() + offset),
                (zPos - (double)blockPos.getZ()));


        BlockRendererDispatcher blockRendererDispatcher = Minecraft.getMinecraft().getBlockRendererDispatcher();
        BlockModelRenderer blockModelRenderer = Minecraft.getMinecraft().getBlockRendererDispatcher().getBlockModelRenderer();
        blockModelRenderer.renderModel(world, blockRendererDispatcher.getModelForState(blockStateToRender),blockStateToRender, blockPos, vertexBuffer, true);

        vertexBuffer.setTranslation(0.0D, 0.0D, 0.0D);
        tessellator.draw();

    }
}
