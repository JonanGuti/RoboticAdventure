package com.jonanguti.roboticadventure.Blocks;

import com.jonanguti.roboticadventure.RoboticAdventure;
import com.jonanguti.roboticadventure.creativetab.CreativeTabRA;
import com.jonanguti.roboticadventure.init.ModBlocks;
import com.jonanguti.roboticadventure.reference.Reference;
import com.jonanguti.roboticadventure.tileEntity.TileEntityDuplicator;
import cpw.mods.fml.common.network.internal.FMLNetworkHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

import java.util.Random;

public class BlockDuplicator extends BlockContainer {

    private final boolean isActive;

    @SideOnly(Side.CLIENT)
    private IIcon iconFront;

    @SideOnly(Side.CLIENT)
    private IIcon iconTop;

    private static boolean keepInventory;


    public BlockDuplicator(boolean isActive) {

        super(Material.iron);
        this.isActive = isActive;

        if (isActive){
            this.setBlockName("duplicatorActive");
            this.setLightLevel(15F);
        }
        else {
            this.setBlockName("duplicatorIdle");
            this.setCreativeTab(CreativeTabRA.RA_TAB);
        }
    }

    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister iconRegister){

        this.blockIcon = iconRegister.registerIcon(Reference.MOD_ID + ":" + "duplicatorSide");
        this.iconFront = iconRegister.registerIcon(Reference.MOD_ID + ":" + (this.isActive ? "duplicatorFrontOn" : "duplicatorFrontOff"));
        this.iconTop = iconRegister.registerIcon(Reference.MOD_ID + ":" + "duplicatorTop");

    }

    @SideOnly(Side.CLIENT)
    public IIcon getIcon (int side, int metadata) {

        return metadata == 0 && side == 3 ? this.iconFront : side == 1 ? this.iconTop : (side == 0 ? this.iconTop : (side == metadata ? this.iconFront : this.blockIcon));
    }

    public Item getItemDropped(int i, Random random, int j){

        return Item.getItemFromBlock(ModBlocks.duplicatorIdle);

    }

    public void onBlockAdded(World world, int x, int y, int z) {
        super.onBlockAdded(world, x, y, z);
        this.setDefaultDirection(world, x, y, z);
    }

    private void setDefaultDirection(World world, int x, int y, int z) {
        if(!world.isRemote) {
            Block b1 = world.getBlock(x, y, z - 1);
            Block b2 = world.getBlock(x,  y,  z + 1);
            Block b3 = world.getBlock(x - 1, y, z);
            Block b4  = world.getBlock(x + 1, y, z);

            byte b0 = 3;

            if(b1.func_149730_j() && !b2.func_149730_j()) {
                b0 = 3;
            }

            if(b2.func_149730_j() && !b1.func_149730_j()) {
                b0 = 2;
            }

            if(b3.func_149730_j() && !b4.func_149730_j()) {
                b0 = 5;
            }

            if(b4.func_149730_j() && !b3.func_149730_j()) {
                b0 = 4;
            }

            world.setBlockMetadataWithNotify(x, y, x, b0, 2);
        }

    }

    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float hitX, float hitY, float hitZ) {
        if(!world.isRemote) {
            FMLNetworkHandler.openGui(player, RoboticAdventure.instance, ModBlocks.guiIDDuplicator, world, x, y, z);
        }
        return true;
    }



    @Override
    public TileEntity createNewTileEntity(World world, int i) {
        return new TileEntityDuplicator();
    }


    public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase entityplayer, ItemStack itemstack) {
        int l = MathHelper.floor_double((double)(entityplayer.rotationYaw * 4.0F / 360.F) + 0.5D) & 3;

        if(l == 0) {
            world.setBlockMetadataWithNotify(x, y, z, 2, 2);
        }

        if(l == 1) {
            world.setBlockMetadataWithNotify(x, y, z, 5, 2);
        }

        if(l == 2) {
            world.setBlockMetadataWithNotify(x, y, z, 3, 2);
        }

        if(l == 3) {
            world.setBlockMetadataWithNotify(x, y, z, 4, 2);
        }

        if(itemstack.hasDisplayName()) {
            ((TileEntityDuplicator)world.getTileEntity(x, y, z)).setGuiDisplayName(itemstack.getDisplayName());
        }
    }

    public static void updateDuplicatorBlockState(boolean active, World worldObj, int xCoord, int yCoord, int zCoord) {
        int i = worldObj.getBlockMetadata(xCoord, yCoord, zCoord);

        TileEntity tileentity = worldObj.getTileEntity(xCoord, yCoord, zCoord);
        keepInventory = true;

        if(active) {
            worldObj.setBlock(xCoord, yCoord, zCoord, ModBlocks.duplicatorActive);
        }else{
            worldObj.setBlock(xCoord, yCoord, zCoord, ModBlocks.duplicatorIdle);
        }

        keepInventory = false;

        worldObj.setBlockMetadataWithNotify(xCoord, yCoord, zCoord, i, 2);

        if(tileentity != null) {
            tileentity.validate();
            worldObj.setTileEntity(xCoord, yCoord, zCoord, tileentity);
        }
    }
}




