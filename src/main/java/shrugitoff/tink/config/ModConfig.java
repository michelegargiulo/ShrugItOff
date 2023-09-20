package shrugitoff.tink.config;

import com.google.common.annotations.GwtIncompatible;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import shrugitoff.tink.ShrugItOff;

import javax.swing.text.DefaultEditorKit;
import java.util.Arrays;
import java.util.HashSet;

@Config(modid = ShrugItOff.MODID, category="General", name=ShrugItOff.MODID)
@Mod.EventBusSubscriber
public class ModConfig {

    private ModConfig() { }

    @Config.Ignore
    private static ModConfig INSTANCE = null;

    @Config.Ignore
    public static HashSet<String> excludedItems = new HashSet<>();

    public static ModConfig getInstance() {
        if (INSTANCE == null)
            INSTANCE = new ModConfig();
        return INSTANCE;
    }

    @Config.Name("logDamageSources")
    @Config.LangKey(ShrugItOff.MODID + ".config.log_damage_sources")
    @Config.Comment({"If true, each time an entity is hit, the damage source type will be printed in the log"})
    public static boolean logDamageSources = false;

    @Config.Name("useWhitelist")
    @Config.LangKey(ShrugItOff.MODID + ".config.use_whitelist")
    @Config.Comment({"Controls the damage sources that can be avoided with the Armor Toughness.",
            "If True, only the damage sources specified in the whitelist can be avoided"})
    public static boolean useWhitelist = false;

    @Config.Name("damageSourceWhitelist")
    @Config.LangKey(ShrugItOff.MODID + ".config.whitelist")
    @Config.Comment({"The whitelist of damage sources that can be avoided"})
    public static String[] damageSourceWhitelist = new String[]  {
            "anvil",
            "arrow",
            "badRespawnPoint",
            "cactus",
            "dragonBreath",
            "dryout",
            "explosion",
            "explosion.player",
            "infinity",
            "generic",
            "mob",
            "player",
            "sting",
            "thorns",
            "thrown",
            "trident",
    };

    @Config.Name("damageSourceBlacklist")
    @Config.LangKey(ShrugItOff.MODID + ".config.blacklist")
    @Config.Comment({"The blacklist of damage sources that cannot be avoided",
            "Damage sources here will be ignored and armor toughness can't be used to avoid the damage"})
    public static String[] damageSourceBlacklist = new String[] {
            "cramming",
            "drown",
            "fall",
            "fallingBlock",
            "fallingStalactite",
            "fireworks",
            "flyIntoWall",
            "freeze",
            "hotFloor",
            "indirectMagic",
            "inFire",
            "inWall",
            "lava",
            "lightningBolt",
            "magic",
            "onFire",
            "outOfWorld",
            "stalagmite",
            "starve",
            "sweetBerryBush",
            "wither",
            "witherSkull"
    };

    @Config.Name("itemBlacklist")
    @Config.LangKey(ShrugItOff.MODID + ".config.item_blacklist")
    @Config.Comment("For any item specified in this list, ShrugItOff will not apply any modification. Useful to exclude " +
            "items that have special behaviours, like Avaritia Infinity Tools. " +
            "Format is MODID:ITEM:METADATA. Metadata is optional")
    public static String[] itemBlacklist = new String[] {
            "avaritia:infinity_sword",
            "avaritia:infinity_pickaxe",
            "avaritia:infinity_axe",
            "avaritia:infinity_shovel",
            "avaritia:infinity_hoe",
            "avaritia:infinity_bow",
    };

    @Config.Name("smallDamageSources")
    @Config.LangKey(ShrugItOff.MODID + ".config.small_damage_sources")
    @Config.Comment({"Small damage sources for which the sound will not be played. Leave empty to always make a sound.",
            "To always disable sound, use the 'disableSound' config option"})
    public static String[] smallDamageSources = new String[] {
            "anvil",
            "cactus",
            "thorns",
    };

    @Config.Name("disableSound")
    @Config.LangKey(ShrugItOff.MODID + ".config.disable_sound")
    @Config.Comment({"If true, sound will never be played"})
    public static boolean disableSound = false;

    @SubscribeEvent
    public void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent event)
    {
        if (event.getModID().equals(ShrugItOff.MODID)) {
            ConfigManager.sync(ShrugItOff.MODID, Config.Type.INSTANCE);
            reloadItemExclusions();
        }
    }

    private static void reloadItemExclusions() {
        excludedItems.clear();
        excludedItems.addAll(Arrays.asList(itemBlacklist));
    }
}
