package schlarpc.revivalbook;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.util.ArrayList;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.common.network.PacketDispatcher;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.packet.Packet250CustomPayload;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.management.BanEntry;
import net.minecraft.server.management.BanList;
import net.minecraft.util.StatCollector;

public class RevivalBookGui extends GuiScreen {
	public final int xSize = 192;
	public final int ySize = 192;

	private GuiTextField targetField;
	private int selectedTarget = 0;

	public RevivalBookGui(EntityPlayer player)
	{
		super();
	}

	@Override
	public void drawScreen(int x, int y, float f)
	{
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		this.mc.renderEngine.bindTexture("/mods/RevivalBook/textures/gui/book.png");

		int posX = (this.width - xSize) / 2;
		drawTexturedModalRect(posX, 2, 0, 0, xSize, ySize);
		this.targetField.drawTextBox();
		
		super.drawScreen(x, y, f);
	}

	@Override
	public boolean doesGuiPauseGame()
	{
		return false;
	}

	public void actionPerformed(GuiButton button)
	{
		switch(button.id)
		{
		case 0: {
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			DataOutputStream outputStream = new DataOutputStream(bos);
			try {
				outputStream.writeInt(2);
				outputStream.writeUTF(this.targetField.getText());
			} catch (Exception ex) {
				ex.printStackTrace();
			}

			Packet250CustomPayload packet = new Packet250CustomPayload();
			packet.channel = "RevivalBook";
			packet.data = bos.toByteArray();
			packet.length = bos.size();
			
			PacketDispatcher.sendPacketToServer(packet);
			this.mc.thePlayer.closeScreen();
			break;
		}
		case 1: {
			selectedTarget--;
			if (selectedTarget < 0) {
				selectedTarget = RevivalBook.targets.size() - 1;
			}
			
			this.targetField.setText(RevivalBook.targets.get(selectedTarget));
			break;
		}
		case 2: {
			selectedTarget++;
			if (selectedTarget >= RevivalBook.targets.size()) {
				selectedTarget = 0;
			}
			
			this.targetField.setText(RevivalBook.targets.get(selectedTarget));
			break;
		}
		default:
		}
	}

	public void initGui()
	{
		this.targetField = new GuiTextField(mc.fontRenderer, this.width / 2 - 55, ySize / 2 - 20, 105, 20);
		this.targetField.setText(RevivalBook.targets.get(selectedTarget));
		
		this.buttonList.clear();
		this.buttonList.add(new GuiButton(0, this.width / 2 - 100, ySize + 4, 200, 20, "Revive"));
		this.buttonList.add(new GuiButton(1, this.width / 2 - 55, ySize / 2 + 10, 45, 20, "Prev"));
		this.buttonList.add(new GuiButton(2, this.width / 2 + 5, ySize / 2 + 10, 45, 20, "Next"));
	}

}
