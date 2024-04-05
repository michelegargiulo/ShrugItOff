package stb.shrugitoff;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.attributes.AbstractAttributeMap;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.*;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
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

        // If the entity being attacked is in the blacklist, or not in the whitelist, return
        String entityId;
        if(livingEntity instanceof EntityPlayer) {
            entityId = "minecraft:player";
        }
        else {
            try {
                // This line of code is copied from Ancient Warfare 3, probably like 5 years old now...
                // May not be the best way to do this, feel free to replace
                ResourceLocation victimResourceLocation = EntityRegistry.getEntry(livingEntity.getClass()).getRegistryName();
                entityId = victimResourceLocation.toString();
            }
            catch (NullPointerException npe) {
                logToChat(ModConfig.logLogic,
                        "[ERROR] Failed to retrieve entity resource location; cannot perform entity white/blacklist check!",
                        attacker, livingEntity);
                return;
            }
        }
        boolean entityIsInList = Arrays.asList(ModConfig.entityBlacklist).contains(entityId);
        if(ModConfig.useEntityWhitelist && !entityIsInList){
            logToChat(ModConfig.logLogic,
                    "[LOGIC] Damage won't be shrugged: entity is not in whitelist.",
                    attacker, livingEntity);
            return; // If we are using an entity whitelist, and the event entity is NOT on it, do nothing and return.
        }
        if(!ModConfig.useEntityWhitelist && entityIsInList){
            logToChat(ModConfig.logLogic,
                    "[LOGIC] Damage won't be shrugged: entity is blacklisted.",
                    attacker, livingEntity);
            return; // If we are using an entity blacklist, and the event entity is on it, do nothing and return.
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

                    // Log damage sources
                    logToChat(ModConfig.logLogic,
                            String.format("[LOGIC] Damage won't be shrugged: damage dealt with blacklisted item (", regName + ":" + metadata + ")"),
                            attacker, livingEntity);
                    return;
                }
            }
        }

        // Log damage source
        logToChat(ModConfig.logDamageSources, String.format("[DAMAGE SOURCES] Entity %s has been dealt %s amount of %s damage",
                event.getEntityLiving().getDisplayName(),
                event.getAmount(),
                event.getSource().getDamageType()), attacker, livingEntity);

        // No damage, no tink.
        if (amount == 0) {
            logToChat(ModConfig.logLogic, "[LOGIC] Damage not shrugged: damage amount is 0", attacker, livingEntity);
            return;
        }

        // If whitelist is active and source not in whitelist OR blacklist is active and source in blacklist
        if ((ModConfig.useWhitelist && !Arrays.asList(ModConfig.damageSourceWhitelist).contains(source.getDamageType())) ||
                (!ModConfig.useWhitelist && Arrays.asList(ModConfig.damageSourceBlacklist).contains(source.getDamageType()))) {
            return;
        }

        // Retrieve toughness attribute from entity
        AbstractAttributeMap attrs = livingEntity.getAttributeMap();
        IAttributeInstance toughnessAttr = attrs.getAttributeInstanceByName("generic.armorToughness");
        // If no toughness, return
        if(toughnessAttr == null) {
            logToChat(ModConfig.logLogic, "[LOGIC] Damage won't be shrugged: Toughness attribute could not be found", attacker, livingEntity);
            return;
        }

        // Get toughness value
        double toughness = toughnessAttr.getAttributeValue();

        // Roll RNG based on incoming damage and current toughness: toughness 10x damage =100%, toughness 0x =0%
        double blockChance = 0;
        if (ModConfig.enableNewFormula) {
            float toughnessFactor = ModConfig.newFormulaToughnessFactor;
            blockChance = (2 / (1 + Math.exp((-0.001 * toughnessFactor * toughness) / (0.05 * amount)))) - 1;
        } else {
            blockChance = Math.min(ModConfig.oldFormulaBase * toughness / amount, ModConfig.oldFormulaCap);
        }
        double rng = livingEntity.getRNG().nextDouble();

        // Log chances
        logToChat(ModConfig.logChances, String.format("[CHANCES] Block Chance: %.4f; RNG: %.4f; Damage: %.4f", blockChance, rng, amount), attacker, livingEntity);

        // If the source is unblockable (and config does not allow to ignore),
        // or the damage is absolute (and config does not allow to ignore), return
        if ((!ModConfig.ignoreUnblockableDamageAttribute && source.isUnblockable()) ||
        (!ModConfig.ignoreAbsoluteDamageAttribute && source.isDamageAbsolute())) {
            logToChat(ModConfig.logLogic, "[LOGIC] Damage won't be shrugged: The damage is either Unblockable or Absolute", attacker, livingEntity);
            return;
        }

        // If back luck, everything goes as normal
        if (rng > blockChance) {
            logToChat(ModConfig.logLogic, "[LOGIC] Damage won't be shrugged: bad luck", attacker, livingEntity);
            return;
        }

        // Damage blocked! Set event as canceled
        if (event.isCancelable()) {
            event.setCanceled(true);
            logToChat(ModConfig.logLogic, "[LOGIC] Damage has been shrugged off", attacker, livingEntity);
        }

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

    private static void logToChat(boolean shoudLog, String msg, Entity attacker, EntityLivingBase entity) {
        if (!shoudLog) return;
        if (attacker instanceof EntityLivingBase) attacker.sendMessage(new TextComponentString(msg));
        entity.sendMessage(new TextComponentString(msg));
    }
}
