package stb.shrugitoff;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.attributes.AbstractAttributeMap;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.*;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import stb.shrugitoff.config.ModConfig;

import java.util.Arrays;

@Mod.EventBusSubscriber
public class LivingAttackEventHandler {

    // High pitched anvil sound makes a metallic "clink", or "tink" if you will.
    private static final ResourceLocation soundResource = new ResourceLocation("block.anvil.land");

    @SubscribeEvent
    public static void onLivingAttackEvent(LivingAttackEvent event) {

        // Retrieve Entity
        EntityLivingBase livingEntity = event.getEntityLiving();

        if (livingEntity == null) return;

        // Retrieve World
        World world = livingEntity.world;

        // Chance to completely shrug off any blockable, physical damage if the entity has any toughness
        DamageSource source = event.getSource();
        float amount = event.getAmount();

        // Retrieve attacker, to check what's in their hand and compare against the item blacklist
        Entity attacker = source.getTrueSource();

        // If world is remote, return
        if(world.isRemote) {
            return;
        }

        // If it is an excluded item, return
        if (attacker instanceof EntityLivingBase) {
            EntityLivingBase elbAttacker = (EntityLivingBase) attacker;
            ItemStack attackItem = elbAttacker.getHeldItem(EnumHand.MAIN_HAND);

            if (attackItem.getItem().getRegistryName() != null) {

                // Retrieve registry name and metadata
                int metadata = attackItem.getItem().getMetadata(attackItem);
                String regName = attackItem.getItem().getRegistryName().toString();

                // If in blacklist, skip
                if (ModConfig.excludedItems.contains(regName) || ModConfig.excludedItems.contains(regName + ":" + metadata)) {
                    if (ModConfig.logDamageSources) {
                        ShrugItOff.logger.debug(String.format("Blacklist contains item %s: ShrugItOff will ignore this attack.", regName + ":" + metadata));
                    }
                    return;
                }
            }
        }

        // Log damage source
        if (ModConfig.logDamageSources) {
            String msg = String.format("Entity %s has been dealt %s amount of %s damage",
                        event.getEntityLiving().getDisplayName(),
                        event.getAmount(),
                        event.getSource().getDamageType()
                    );

            ShrugItOff.logger.debug(msg);

            if (attacker != null)
                attacker.sendMessage(new TextComponentString(msg));

            livingEntity.sendMessage(new TextComponentString(msg));
        }

        // No damage, no tink.
        if (amount == 0) {
            return;
        }

        // If whitelist is active and source not in whitelist OR blacklist is active and source in blacklist
        if ((ModConfig.useWhitelist && !Arrays.asList(ModConfig.damageSourceWhitelist).contains(source.getDamageType())) ||
                (!ModConfig.useWhitelist && Arrays.asList(ModConfig.damageSourceBlacklist).contains(source.getDamageType())))
            return;

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
        double blockChance = 0;
        if (ModConfig.enableNewFormula) {
            blockChance = Math.min(ModConfig.oldFormulaBase * toughness / amount, ModConfig.oldFormulaCap);
        } else {
            float toughnessFactor = ModConfig.newFormulaToughnessFactor;
            blockChance = (2 / (1 + Math.exp((-0.001 * toughnessFactor * toughness) / (0.05 * amount)))) - 1;
        }

        // If damage is unblockable or absolute, or bad RNG, do nothing
        double rng = livingEntity.getRNG().nextDouble();

        if (ModConfig.logChances) {
            String msg = String.format("Block Chance: %.4f; RNG: %.4f; Damage: %.4f", blockChance, rng, amount);
            if (attacker != null)
                attacker.sendMessage(new TextComponentString(msg));
            livingEntity.sendMessage(new TextComponentString(msg));
        }

        // If the source is unblockable, or the damage is absolute, or back luck, everything goes as normal
        if (source.isUnblockable() || source.isDamageAbsolute() || rng > blockChance) return;

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
