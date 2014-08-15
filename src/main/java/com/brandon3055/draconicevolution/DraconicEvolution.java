package com.brandon3055.draconicevolution;

import com.brandon3055.draconicevolution.client.creativetab.DETab;
import com.brandon3055.draconicevolution.common.core.network.ChannelHandler;
import com.brandon3055.draconicevolution.common.core.proxy.CommonProxy;
import com.brandon3055.draconicevolution.common.core.utills.LogHelper;
import com.brandon3055.draconicevolution.common.lib.References;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import net.minecraft.creativetab.CreativeTabs;

import java.util.Arrays;

@Mod(modid = References.MODID, name = References.MODNAME, version = References.VERSION, canBeDeactivated = false, guiFactory = References.GUIFACTORY)
public class DraconicEvolution {

	@Mod.Instance(References.MODID)
	public static DraconicEvolution instance;

	@SidedProxy(clientSide = References.CLIENTPROXYLOCATION, serverSide = References.COMMONPROXYLOCATION)
	public static CommonProxy proxy;

	public static CreativeTabs tolkienTabToolsWeapons = new DETab(CreativeTabs.getNextID(), References.MODID, "toolsAndWeapons", 0);
	public static CreativeTabs tolkienTabBlocksItems = new DETab(CreativeTabs.getNextID(), References.MODID, "blocksAndItems", 1);
	public static final String networkChannelName = "DraconicEvolution";
	public static ChannelHandler channelHandler = new ChannelHandler(References.MODID, networkChannelName);
	public static boolean debug = false;
	
	public DraconicEvolution()
	{
		LogHelper.info("Hello Minecraft!!!");
	}
	
	@Mod.EventHandler
	public static void preInit(final FMLPreInitializationEvent event)
	{if(debug)
		LogHelper.info("Initialization");

		event.getModMetadata().autogenerated = false;
		event.getModMetadata().credits = "";
		event.getModMetadata().description = "This is a mod originally made for the Tolkiencraft mod pack";
		event.getModMetadata().authorList = Arrays.asList("brandon3055");
		event.getModMetadata().logoFile = "banner.png";
		event.getModMetadata().url = "http://dragontalk.net/draconic_evolution";
		event.getModMetadata().version = References.VERSION + "-MC1.7.10";

		proxy.preInit(event);

		/*
		public static Achievement ultimatePower;
		ultimatePower = new Achievement("achievment.ultimatePower", "Ultimate Power!!!", 1, -2, ModItems.draconicDistructionStaff, null).registerStat();
		AchievementPage draconicEvolution = new AchievementPage("Draconic Evolution", ultimatePower);
		AchievementPage.registerAchievementPage(draconicEvolution);
		*/

	}

	@Mod.EventHandler
	public void init(final FMLInitializationEvent event)
	{if(debug)
		System.out.println("init()");
		
		proxy.init(event);
	}

	@Mod.EventHandler
	public void postInit(final FMLPostInitializationEvent event)
	{if(debug)
		System.out.println("postInit()");
	
		proxy.postInit(event);
		
	}
}