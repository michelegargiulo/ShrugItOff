package shrugitoff.tink;

import net.minecraftforge.common.config.Config;

@Config(modid = TinkMain.MODID)
public class ShrugConfig {

    @Config.Comment({
            "This is the multiplier for the formula, CHANCE = MULTIPLIER * TOUGHNESS/DAMAGE",
            "Default: 0.1 (AKA 10%)"
    })
    public static double multiplier = 0.1;

    @Config.Comment({
            "This option changes the formula to ignore the value of the incoming damage.",
            "If it is set to true, the new formula will be: CHANCE = MULTIPLIER * TOUGHNESS",
            "Default: false"
    })
    public static boolean useSimpleFormula = false;

    @Config.Comment({
            "This is the maximum chance for damage to be shrugged off.",
            "Default: 1.0 (AKA 100%)"
    })
    public static double maxTinkChance = 1.0;

    @Config.Comment({
            "This is the list of damage types that can be shrugged off.",
            "Default: mob, player, generic, arrow, explosive, explosion.player"
    })
    public static String[] shruggableDamageTypes = {
            "mob",
            "player",
            "generic",
            "arrow",
            "explosive",
            "explosion.player"
    };

    @Config.Comment({
            "This is the list of damage types that can be shrugged off without playing the 'tink' sound.",
            "Default: cactus, thorns, anvil, fallingBlock"
    })
    public static String[] silentShruggableDamageTypes = {
            "cactus",
            "thorns",
            "anvil",
            "fallingBlock"
    };

    @Config.Comment({
            "Entity blacklist. Entiies in this list will not be able to shrug off damage.",
            "If the 'blacklist' option below is set to false, this will instead be treated as a whitelist.",
            "Empty by default."
    })
    public static String[] entityBlacklist = {};

    @Config.Comment({
            "Whether the entity list is a blacklist.",
            "Default: true"
    })
    public static boolean blacklist = true;
}
