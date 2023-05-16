package shrugitoff.tink;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import org.apache.logging.log4j.Logger;
import shrugitoff.tink.proxy.CommonProxy;


@Mod(modid = TinkMain.MODID, name = TinkMain.MODNAME, version = TinkMain.MODVERSION, dependencies = "required-after:forge@[14.23.5.2847,)", useMetadata = true)
public class TinkMain {
    public static final String MODID = "modtut";
    public static final String MODNAME = "Mod tutorials";
    public static final String MODVERSION= "0.0.2";

    @SidedProxy(clientSide = "shrugitoff.tink.proxy.ClientProxy", serverSide = "shrugitoff.tink.proxy.ServerProxy")
    public static CommonProxy proxy;

    @Mod.Instance
    public static TinkMain instance;

    public static Logger logger;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        logger = event.getModLog();
        proxy.preInit(event);
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
