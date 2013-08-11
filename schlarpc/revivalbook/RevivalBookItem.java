package schlarpc.revivalbook;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.network.PacketDispatcher;
import cpw.mods.fml.common.network.Player;
import cpw.mods.fml.relauncher.Side;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.Packet250CustomPayload;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.management.BanEntry;
import net.minecraft.server.management.BanList;
import net.minecraft.world.World;

public class RevivalBookItem extends Item {

	public static ArrayList<String> targets = new ArrayList<String>();

	public RevivalBookItem(int id) {
		super(id);

		maxStackSize = 1;
		setCreativeTab(CreativeTabs.tabMisc);
		setUnlocalizedName("Revival Book");
	}

	

	@Override
	public ItemStack onItemRightClick(ItemStack par1ItemStack, World par2World, EntityPlayer par3EntityPlayer)
	{
		Side side = FMLCommonHandler.instance().getEffectiveSide();

		if (side  == Side.SERVER) {
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			DataOutputStream outputStream = new DataOutputStream(bos);
			try {
				outputStream.writeInt(1);

				BanList banList = MinecraftServer.getServer().getConfigurationManager().getBannedPlayers();

				for (Object entry : banList.getBannedList().values()) {
					BanEntry banEntry = (BanEntry) entry;
					if (RevivalBook.isHardcoreDeath(banEntry.getBannedUsername())) {
						outputStream.writeUTF(banEntry.getBannedUsername());
					}
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}

			Packet250CustomPayload packet = new Packet250CustomPayload();
			packet.channel = "RevivalBook";
			packet.data = bos.toByteArray();
			packet.length = bos.size();

			PacketDispatcher.sendPacketToPlayer((Packet)packet, (Player)par3EntityPlayer);
		}

		return par1ItemStack;
	}

	public void registerIcons(IconRegister iconRegister)
	{
		this.itemIcon = iconRegister.registerIcon("RevivalBook:RevivalBook");
	}
}
