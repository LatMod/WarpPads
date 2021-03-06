package com.latmod.warp_gates.client;

import com.feed_the_beast.ftblib.lib.client.ClientUtils;
import com.feed_the_beast.ftblib.lib.math.MathUtils;
import com.latmod.warp_gates.block.TileWarpGate;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.opengl.GL11;

/**
 * @author LatvianModder
 */
public class RenderWarpGate extends TileEntitySpecialRenderer<TileWarpGate>
{
	@Override
	public void render(TileWarpGate te, double rx, double ry, double rz, float partialTicks, int destroyStage, float alpha)
	{
		if (!te.hasPortal())
		{
			return;
		}

		double distanceSq = te.getDistanceSq(ClientUtils.getPlayerX(), ClientUtils.getPlayerY() + 1D, ClientUtils.getPlayerZ());
		double a = getAlpha(distanceSq);

		if (a <= 0.01D)
		{
			return;
		}

		ClientUtils.pushMaxBrightness();
		GlStateManager.pushMatrix();
		GlStateManager.translate(rx + 0.5D, ry + 4.5D, rz + 0.5D);
		GlStateManager.disableCull();
		GlStateManager.enableBlend();
		GlStateManager.disableLighting();
		//render
		String name = te.getName();

		GlStateManager.pushMatrix();
		OpenGlHelper.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, 1, 0);
		//GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);
		GlStateManager.enableTexture2D();
		float f1 = 0.02F;

		double rot = -MathHelper.atan2((te.getPos().getZ() + 0.5F) - ClientUtils.getPlayerZ(), (te.getPos().getX() + 0.5F) - ClientUtils.getPlayerX()) * MathUtils.DEG + 90D;
		GlStateManager.rotate((float) rot, 0F, 1F, 0F);
		GlStateManager.scale(-f1, -f1, f1);

		GlStateManager.rotate(0F, 0F, 1F, 0F);
		GlStateManager.color(1F, 1F, 1F, 1F);
		GlStateManager.disableDepth();
		GlStateManager.depthMask(false);
		ClientUtils.MC.fontRenderer.drawString(name, -(ClientUtils.MC.fontRenderer.getStringWidth(name) / 2), -8, 0xFFFFFF | ((int) (a * 255D)) << 24);
		GlStateManager.enableDepth();
		GlStateManager.depthMask(true);
		GlStateManager.popMatrix();

		GlStateManager.enableCull();
		GlStateManager.popMatrix();
		GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		GlStateManager.color(1F, 1F, 1F, 1F);
		GlStateManager.disableBlend();
		ClientUtils.popBrightness();
	}

	private double getAlpha(double distSq)
	{
		if (distSq < 6.1D * 6.1D)
		{
			return 1D;
		}

		if (distSq > 7.9D * 7.9D)
		{
			return 0D;
		}

		return MathHelper.clamp(MathUtils.map(MathUtils.sqrt(distSq), 6D, 8D, 1D, 0D), 0D, 1D);
	}
}