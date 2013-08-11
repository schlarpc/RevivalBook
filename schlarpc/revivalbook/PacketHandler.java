package schlarpc.revivalbook;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.ArrayList;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemInWorldManager;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagDouble;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet250CustomPayload;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.WorldServer;
import net.minecraft.world.storage.SaveHandler;
import net.minecraftforge.common.DimensionManager;
import cpw.mods.fml.common.network.IPacketHandler;
import cpw.mods.fml.common.network.Player;


public class PacketHandler implements IPacketHandler  {
	@Override
	public void onPacketData(INetworkManager manager, Packet250CustomPayload packet, Player playerEntity) {
		if (packet.channel.equals("RevivalBook")) {
			DataInputStream inputStream = new DataInputStream(new ByteArrayInputStream(packet.data));

			int packetType;

			try {
				packetType = inputStream.readInt();
			} catch (IOException e) {
				e.printStackTrace();
				return;
			}

			switch(packetType) {
			case 1: {
				ArrayList<String> targets = new ArrayList<String>();

				try {
					while (inputStream.available() > 0) {
						targets.add(inputStream.readUTF());
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
				RevivalBook.targets = targets;

				EntityClientPlayerMP player = (EntityClientPlayerMP) playerEntity;

				if (targets.size() > 0) {
					player.openGui(RevivalBook.instance, 0, player.worldObj, 0, 0, 0);
				} else {
					player.sendChatToPlayer("No players are dead");
				}

				break;
			}
			case 2: {
				String target = "";

				try {
					target = inputStream.readUTF();
				} catch (IOException e) {
					e.printStackTrace();
				}

				EntityPlayerMP player = (EntityPlayerMP) playerEntity;

				if (RevivalBook.removeHardcoreDeath(target)) {
					MinecraftServer server = MinecraftServer.getServer();
					WorldServer worldServer = server.worldServerForDimension(player.dimension);
					SaveHandler saveHandler = (SaveHandler)worldServer.getSaveHandler();
					
					NBTTagCompound targetData = saveHandler.getPlayerData(target);

					targetData.setShort("Health", (short) 6);

					NBTTagList pos = new NBTTagList();
					pos.appendTag(new NBTTagDouble("Pos", player.posX));
					pos.appendTag(new NBTTagDouble("Pos", player.posY));
					pos.appendTag(new NBTTagDouble("Pos", player.posZ));
					targetData.setTag("Pos", pos);

					targetData.setTag("Inventory", new NBTTagList());

					targetData.setInteger("Dimension", player.dimension);
					
					EntityPlayerMP ent = new EntityPlayerMP(server, worldServer, target, new ItemInWorldManager(worldServer));
					ent.readFromNBT(targetData);
					saveHandler.writePlayerData(ent);
					
					player.inventory.setInventorySlotContents(player.inventory.currentItem, (ItemStack)null);
					MinecraftServer.getServer().getConfigurationManager().sendChatMsg(target + " was resurrected by " + player.username);
				} else {
					player.sendChatToPlayer(target + " is not dead");
				}

				break;
			}
			default:

			}
		}
	}
}
