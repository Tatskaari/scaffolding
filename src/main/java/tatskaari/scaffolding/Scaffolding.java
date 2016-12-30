package tatskaari.scaffolding;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import tatskaari.scaffolding.TileEntities.TileEntityElevatorController;
import tatskaari.scaffolding.TileEntities.TileEntityElevatorPlatform;
import tatskaari.scaffolding.TileEntityRenderers.TileEntityElevatorPlatformRenderer;
import tatskaari.scaffolding.blocks.BlockElevatorController;
import tatskaari.scaffolding.blocks.BlockElevatorPlatform;
import tatskaari.scaffolding.blocks.BlockScaffold;

@Mod(modid = Scaffolding.MODID, version = Scaffolding.VERSION)
public class Scaffolding {
    public static final String MODID = "scaffolding";
    public static final String VERSION = "0.1-SNAPSHOT";

    public static final Block BLOCK_SCAFFOLD = new BlockScaffold();
    public static final Block BLOCK_ELEVATOR_PLATFORM = new BlockElevatorPlatform(Material.WOOD);
    public static final Block BLOCK_ELEVATOR_CONTROLLER = new BlockElevatorController(Material.WOOD);

    public static final Item ITEM_SCAFFOLD = new ItemBlock(BLOCK_SCAFFOLD);
    public static final Item ITEM_ELEVATOR_PLATFORM = new ItemBlock(BLOCK_ELEVATOR_PLATFORM);
    public static final Item ITEM_ELEVATOR_CONTROLLER = new ItemBlock(BLOCK_ELEVATOR_CONTROLLER);


    @EventHandler
    public void init(FMLInitializationEvent event){
        ITEM_SCAFFOLD.setRegistryName(BLOCK_SCAFFOLD.getRegistryName());

        GameRegistry.register(BLOCK_SCAFFOLD);
        GameRegistry.register(ITEM_SCAFFOLD);

        ModelResourceLocation scaffoldingItemModelResLoc = new ModelResourceLocation(MODID+":"+"scaffold", "inventory");
        Minecraft.getMinecraft().getRenderItem().getItemModelMesher()
                .register(ITEM_SCAFFOLD, 0, scaffoldingItemModelResLoc);

        GameRegistry.addSmelting(Items.REEDS, new ItemStack(ITEM_SCAFFOLD, 4), 0.35f);

        ITEM_ELEVATOR_PLATFORM.setRegistryName(BLOCK_ELEVATOR_PLATFORM.getRegistryName());
        ITEM_ELEVATOR_CONTROLLER.setRegistryName(BLOCK_ELEVATOR_CONTROLLER.getRegistryName());
        GameRegistry.register(BLOCK_ELEVATOR_PLATFORM);
        GameRegistry.register(ITEM_ELEVATOR_PLATFORM);
        GameRegistry.register(BLOCK_ELEVATOR_CONTROLLER);
        GameRegistry.register(ITEM_ELEVATOR_CONTROLLER);
        GameRegistry.registerTileEntity(TileEntityElevatorController.class, "elevator_controller");
        GameRegistry.registerTileEntity(TileEntityElevatorPlatform.class, "elevator_platform");
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityElevatorController.class, new TileEntityElevatorPlatformRenderer());
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityElevatorPlatform.class, new TileEntityElevatorPlatformRenderer());

    }
}
