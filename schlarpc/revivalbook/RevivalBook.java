package schlarpc.revivalbook;

import java.util.ArrayList;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.management.BanEntry;
import net.minecraft.server.management.BanList;
import net.minecraftforge.common.Configuration;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.Init;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.Mod.PostInit;
import cpw.mods.fml.common.Mod.PreInit;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkMod;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.LanguageRegistry;

@Mod(modid="RevivalBook", name="RevivalBook", version="1.0.0")
@NetworkMod(clientSideRequired=true, serverSideRequired=false, channels={"RevivalBook"}, packetHandler = PacketHandler.class)
public class RevivalBook {

	// The instance of your mod that Forge uses.
	@Instance("RevivalBook")
	public static RevivalBook instance;
	public static ArrayList<String> targets = new ArrayList<String>();

	private static RevivalBookItem revivalBookItem;

	// Says where the client and server 'proxy' code is loaded.
	@SidedProxy(clientSide="schlarpc.revivalbook.client.ClientProxy", serverSide="schlarpc.revivalbook.CommonProxy")
	public static CommonProxy proxy;

	public static boolean isHardcoreDeath(String playerName) {
		BanList banList = MinecraftServer.getServer().getConfigurationManager().getBannedPlayers();
		if (!banList.isBanned(playerName)) return false;

		BanEntry banEntry = (BanEntry)banList.getBannedList().get(playerName);
		if (!banEntry.getBanReason().equals("Death in Hardcore")) return false;

		return true;
	}

	public static boolean removeHardcoreDeath(String playerName) {
		if (!isHardcoreDeath(playerName)) return false;

		BanList banList = MinecraftServer.getServer().getConfigurationManager().getBannedPlayers();
		banList.remove(playerName);

		return true;
	}


	@PreInit
	public void preInit(FMLPreInitializationEvent event) {
		Configuration config = new Configuration(event.getSuggestedConfigurationFile());

		config.load();
		revivalBookItem = new RevivalBookItem(config.getItem("revivalBook", 29650).getInt());
		config.save();
	}

	@Init
	public void load(FMLInitializationEvent event) {

		LanguageRegistry.addName(revivalBookItem, "Revival Book");
		GameRegistry.addShapelessRecipe(new ItemStack(revivalBookItem), new ItemStack(Item.appleGold, 1, 1), new ItemStack(Item.book));
		NetworkRegistry.instance().registerGuiHandler(this, proxy);

	}

	@PostInit
	public void postInit(FMLPostInitializationEvent event) {
		// Stub Method
	}
}
