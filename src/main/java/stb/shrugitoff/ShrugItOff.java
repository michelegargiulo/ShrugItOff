package stb.shrugitoff;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import org.apache.logging.log4j.Logger;
import stb.shrugitoff.config.ModConfig;


@Mod(modid = ShrugItOff.MODID,
        name = ShrugItOff.MODNAME,
        version = ShrugItOff.MODVERSION,
        dependencies = "required-after:forge@[14.23.5.2847,)",
        useMetadata = true)
public class ShrugItOff {

    public static final String MODID = "shrugitoff";
    public static final String MODNAME = "Shrug It Off!";
    public static final String MODVERSION = "0.1.0";

    @Mod.Instance
    public static ShrugItOff instance;

    public static Logger logger;

    @Mod.EventHandler
    public void onPreInit(FMLPreInitializationEvent event) {
        // Get logger
        logger = event.getModLog();

        // Register Event Handler
        LivingAttackEventHandler livingAttackEventHandler = new LivingAttackEventHandler();
        MinecraftForge.EVENT_BUS.register(livingAttackEventHandler);
        MinecraftForge.EVENT_BUS.register(ModConfig.getInstance());
    }
}
