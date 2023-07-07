package shrugitoff.tink;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.attributes.AbstractAttributeMap;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.terraingen.SaplingGrowTreeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import shrugitoff.tink.config.ModConfig;

import java.util.Arrays;

@Mod.EventBusSubscriber
public class LivingAttackEventHandler {

    // High pitched anvil sound makes a metallic "clink", or "tink" if you will.
    private static final ResourceLocation soundResource = new ResourceLocation("block.anvil.land");

    @SubscribeEvent
    public static void onLivingAttackEvent(LivingAttackEvent event) {

        // Get entity and world
        EntityLivingBase livingEntity = event.getEntityLiving();
        World world = livingEntity.world;

        // If world is remote, return
        if(world.isRemote) {
            return;
        }

        // Log damage source
        if (ModConfig.logDamageSources) {
            ShrugItOff.logger.debug(String.format("Entity %s has been dealt %s amount of %s damage",
                    event.getEntityLiving().getDisplayName(),
                    event.getAmount(),
                    event.getSource().getDamageType()));

            livingEntity.sendMessage(new TextComponentString(String.format("Entity %s has been dealt %s amount of %s damage",
                    event.getEntityLiving().getName(),
                    event.getAmount(),
                    event.getSource().getDamageType())));
        }

        // Chance to completely shrug off any blockable, physical damage if the entity has any toughness
        DamageSource source = event.getSource();
        float amount = event.getAmount();

        // No damage, no tink.
        if (amount == 0) { return; }

        // Retrieve toughness attribute from entity
        AbstractAttributeMap attrs = livingEntity.getAttributeMap();
        IAttributeInstance toughnessAttr = attrs.getAttributeInstanceByName("generic.armorToughness");

        // If no toughness, return
        if(toughnessAttr == null) {
            return;
        }

        // Get toughness value
        double toughness = toughnessAttr.getAttributeValue();

        // Roll RNG based on incoming damage and current toughness: toughness 10x damage =100%, toughness 0x =0%
        double blockChance = (0.1F * toughness/amount);

        // If damage is unblockable or absolute, or bad RNG, do nothing
        double rng = livingEntity.getRNG().nextDouble();
        if (source.isUnblockable() || source.isDamageAbsolute() || rng > blockChance) return;

        // If whitelist is active and source not in whitelist OR blacklist is active and source in blacklist
        if ((ModConfig.useWhitelist && !Arrays.asList(ModConfig.damageSourceWhitelist).contains(source.getDamageType())) ||
                (!ModConfig.useWhitelist && Arrays.asList(ModConfig.damageSourceBlacklist).contains(source.getDamageType())))
            return;

        // Damage blocked! Set event as canceled
        if (event.isCancelable()) event.setCanceled(true);

        // If damageSource is smallDamageSource or sound is disabled, do not play sound
        if (ModConfig.disableSound || Arrays.asList(ModConfig.smallDamageSources).contains(source.getDamageType())) return;

        // Else, play the sound
        int x = livingEntity.getPosition().getX();
        int y = livingEntity.getPosition().getY();
        int z = livingEntity.getPosition().getZ();
        SoundEvent soundEvent = new SoundEvent(soundResource);
        float volume = Math.min(amount/5.0F, 1.0F);
        float pitch = Math.max(Math.min(5.0F/amount, 2.0F),0.5F);
        world.playSound(null, x, y, z, soundEvent, SoundCategory.BLOCKS, volume, pitch);

    }
}
