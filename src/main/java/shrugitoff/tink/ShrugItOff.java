package shrugitoff.tink;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import org.apache.logging.log4j.Logger;
import shrugitoff.tink.config.ModConfig;
import shrugitoff.tink.proxy.CommonProxy;


@Mod(modid = ShrugItOff.MODID,
        name = ShrugItOff.MODNAME,
        version = ShrugItOff.MODVERSION,
        dependencies = "required-after:forge@[14.23.5.2847,)",
        useMetadata = true)
public class ShrugItOff {

    public static final String MODID = "shrugitoff";
    public static final String MODNAME = "Shrug It Off!";
    public static final String MODVERSION = "0.0.3";

    @Mod.Instance
    public static ShrugItOff instance;

    public static Logger logger;

    @SidedProxy(clientSide = "shrugitoff.tink.proxy.ClientProxy", serverSide = "shrugitoff.tink.proxy.ServerProxy")
    public static CommonProxy proxy;

    @Mod.EventHandler
    public void onPreInit(FMLPreInitializationEvent event) {
        // Get logger
        logger = event.getModLog();

        // Register Event Handler
        LivingAttackEventHandler livingAttackEventHandler = new LivingAttackEventHandler();
        MinecraftForge.EVENT_BUS.register(livingAttackEventHandler);
        MinecraftForge.EVENT_BUS.register(ModConfig.getInstance());
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent e) {
        proxy.init(e);
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent e) {
        proxy.postInit(e);
    }

}
