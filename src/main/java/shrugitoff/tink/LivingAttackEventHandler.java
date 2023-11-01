package shrugitoff.tink;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.attributes.AbstractAttributeMap;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.world.World;

import java.util.Arrays;


@Mod.EventBusSubscriber
public class LivingAttackEventHandler {

    // High pitched anvil sound makes a metallic "clink", or "tink" if you will.
    private static final ResourceLocation soundResource = new ResourceLocation("block.anvil.land");

    @SubscribeEvent
    public static void onLivingAttackEvent (LivingAttackEvent event) {
        EntityLivingBase livingEntity = event.getEntityLiving();
        boolean entityIsInList = Arrays.asList(ShrugConfig.entityBlacklist).contains(livingEntity.getEntityId());
        if(ShrugConfig.blacklist && entityIsInList){
            return; // If we are using an entity blacklist, and the event entity is on it, do nothing and return.
        }
        if(!ShrugConfig.blacklist && !entityIsInList){
            return; // If we are using an entity whitelist, and the event entity is NOT on it, do nothing and return.
        }
        World world = livingEntity.world;
        if(!world.isRemote) {
            // Chance to completely shrug off any blockable, physical damage if the entity has any toughness
            DamageSource source = event.getSource();
            float amount = event.getAmount();
            if (amount == 0) { return; } // No damage, no tink.
            AbstractAttributeMap attrs = livingEntity.getAttributeMap();
            IAttributeInstance toughnessAttr = attrs.getAttributeInstanceByName("generic.armorToughness");
            if(toughnessAttr == null) {
                return;
            }
            double toughness = toughnessAttr.getAttributeValue();
            // Roll RNG based on incoming damage and current toughness: toughness 10x damage =100%, toughness 0x =0%
            double blockChance = 0;
            if(ShrugConfig.useSimpleFormula) {
                blockChance = (ShrugConfig.multiplier*toughness);
            }
            else {
                blockChance = (ShrugConfig.multiplier * toughness / amount);
            }
            // Cap block chance based on config:
            if (blockChance > ShrugConfig.maxTinkChance) { blockChance = ShrugConfig.maxTinkChance; }
            if (blockChance == 0) { return; }
            if(!source.isUnblockable() && !source.isDamageAbsolute() && livingEntity.getRNG().nextDouble() < blockChance) {
                if(Arrays.asList(ShrugConfig.silentShruggableDamageTypes).contains(source.getDamageType())){
                    // Make no noise for tiny damage sources, or for sources with their own sound effect
                    event.setCanceled(true);
                }
                else if (Arrays.asList(ShrugConfig.shruggableDamageTypes).contains(source.getDamageType())) {
                    event.setCanceled(true);
                    int x = livingEntity.getPosition().getX();
                    int y = livingEntity.getPosition().getY();
                    int z = livingEntity.getPosition().getZ();
                    // make anvil noise. Higher damage number results in louder noise and lower pitch
                    SoundEvent soundEvent = new SoundEvent(soundResource);
                    float volume = Math.min(amount/5.0F, 1.0F);
                    float pitch = Math.max(Math.min(5.0F/amount, 2.0F),0.5F);
                    world.playSound(null, x, y, z, soundEvent, SoundCategory.BLOCKS, volume, pitch);
                }
            }
        }
    }
}
