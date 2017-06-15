package com.latmod.warp_pads.block;

import com.feed_the_beast.ftbl.lib.EnumPrivacyLevel;
import com.feed_the_beast.ftbl.lib.block.EnumHorizontalOffset;
import com.feed_the_beast.ftbl.lib.util.StringUtils;
import com.latmod.warp_pads.WarpPadsConfig;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.UUID;

/**
 * @author LatvianModder
 */
public class TileWarpPad extends TileWarpPadBase implements ITickable
{
	private UUID owner;
	public boolean inactive;
	private String name = "";
	private EnumPrivacyLevel privacyLevel = EnumPrivacyLevel.TEAM;

	public TileWarpPad()
	{
	}

	public TileWarpPad(boolean u)
	{
		checkUpdates = u;
	}

	public String getName()
	{
		return name.isEmpty() ? "Unnamed" : name;
	}

	@Override
	public EnumHorizontalOffset getPart()
	{
		return EnumHorizontalOffset.CENTER;
	}

	@Override
	public ITextComponent getDisplayName()
	{
		return StringUtils.text(getName());
	}

	public void setName(String n)
	{
		name = n;
		markDirty();
	}

	@Nullable
	public UUID getOwner()
	{
		return owner;
	}

	public void setOwner(UUID id)
	{
		owner = id;
	}

	public boolean isOwner(UUID id)
	{
		return owner == null || owner.equals(id);
	}

	public EnumPrivacyLevel getPrivacyLevel()
	{
		return privacyLevel;
	}

	public void togglePrivacyLevel()
	{
		privacyLevel = EnumPrivacyLevel.VALUES[privacyLevel.ordinal() % EnumPrivacyLevel.VALUES.length];
		markDirty();
	}

	@Override
	public void onLoad()
	{
		super.onLoad();
		WarpPadsNet.add(this);
	}

	@Override
	public void invalidate()
	{
		WarpPadsNet.remove(this);
		super.invalidate();
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt)
	{

		if (owner != null)
		{
			nbt.setString("Owner", StringUtils.fromUUID(owner));
		}

		nbt.setByte("Privacy", (byte) privacyLevel.ordinal());
		nbt.setString("Name", name);
		nbt.setBoolean("Inactive", inactive);
		return super.writeToNBT(nbt);
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt)
	{
		super.readFromNBT(nbt);

		owner = nbt.hasKey("Owner") ? StringUtils.fromString(nbt.getString("Owner")) : null;
		privacyLevel = EnumPrivacyLevel.VALUES[nbt.getByte("Privacy")];
		name = nbt.getString("Name");
		inactive = nbt.getBoolean("Inactive");
	}

	@Override
	public NBTTagCompound getUpdateTag()
	{
		NBTTagCompound nbt = new NBTTagCompound();

		if (!name.isEmpty())
		{
			nbt.setString("N", name);
		}

		if (inactive)
		{
			nbt.setBoolean("I", true);
		}

		return nbt;
	}

	@Override
	public void onUpdateTag(NBTTagCompound nbt)
	{
		name = nbt.getString("N");
		inactive = nbt.getBoolean("I");
	}

	@Override
	public void update()
	{
		checkIfDirty();
	}

	public int getEnergyRequired(TileWarpPad teleporter)
	{
		if (teleporter.world.provider.getDimension() == world.provider.getDimension())
		{
			return WarpPadsConfig.getEnergyRequired(Math.sqrt(teleporter.getDistanceSq(pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D)));
		}

		return WarpPadsConfig.getEnergyRequired(-1D);
	}

	public boolean consumeEnergy(EntityPlayer ep, int levels, boolean simulate)
	{
		if (levels <= 0 || ep.capabilities.isCreativeMode || ep.experienceLevel >= levels)
		{
			if (!simulate && levels > 0)
			{
				ep.addExperienceLevel(-levels);
			}

			return true;
		}

		return false;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public AxisAlignedBB getRenderBoundingBox()
	{
		return new AxisAlignedBB(pos.getX() - 2D, pos.getY() - 1D, pos.getZ() - 2D, pos.getX() + 3D, pos.getY() + 50D, pos.getZ() + 3D);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public double getMaxRenderDistanceSquared()
	{
		return 64D;
	}
}