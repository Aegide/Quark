package vazkii.quark.content.mobs.item;

import javax.annotation.Nonnull;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.feature.StructureFeature;
import net.minecraft.world.phys.Vec3;
import vazkii.quark.base.handler.QuarkSounds;
import vazkii.quark.base.item.QuarkItem;
import vazkii.quark.base.module.QuarkModule;
import vazkii.quark.content.mobs.entity.SoulBead;
import vazkii.quark.content.mobs.module.WraithModule;

public class SoulBeadItem extends QuarkItem {

	public SoulBeadItem(QuarkModule module) {
		super("soul_bead", module, new Item.Properties().tab(CreativeModeTab.TAB_MISC));
	}
	
	@Nonnull
	@Override
	public InteractionResultHolder<ItemStack> use(Level worldIn, Player playerIn, InteractionHand handIn) {
		ItemStack itemstack = playerIn.getItemInHand(handIn);

		if(!worldIn.isClientSide) {
			StructureFeature<?> target = StructureFeature.FORTRESS;
            BlockPos blockpos = ((ServerLevel)worldIn).getChunkSource().getGenerator().findNearestMapFeature((ServerLevel)worldIn, target, playerIn.blockPosition(), 100, false);

			if(blockpos != null) {
				itemstack.shrink(1);
				SoulBead entity = new SoulBead(WraithModule.soulBeadType, worldIn);
				entity.setTarget(blockpos.getX(), blockpos.getZ());
				
				Vec3 look = playerIn.getLookAngle();
				entity.setPos(playerIn.getX() + look.x * 2, playerIn.getY() + 0.25, playerIn.getZ() + look.z * 2);
				worldIn.addFreshEntity(entity);

				worldIn.playSound(null, playerIn.getX(), playerIn.getY(), playerIn.getZ(), QuarkSounds.ITEM_SOUL_POWDER_SPAWN, SoundSource.PLAYERS, 1F, 1F);
			}
		} else playerIn.swing(handIn);

		
        playerIn.awardStat(Stats.ITEM_USED.get(this));
		return new InteractionResultHolder<>(InteractionResult.SUCCESS, itemstack);
	}
	
}
