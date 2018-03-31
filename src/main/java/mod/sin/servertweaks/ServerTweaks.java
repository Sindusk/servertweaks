package mod.sin.servertweaks;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Properties;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import org.gotti.wurmunlimited.modloader.interfaces.Configurable;
import org.gotti.wurmunlimited.modloader.interfaces.Initable;
import org.gotti.wurmunlimited.modloader.interfaces.PreInitable;
import org.gotti.wurmunlimited.modloader.interfaces.ServerStartedListener;
import org.gotti.wurmunlimited.modloader.interfaces.WurmServerMod;
import org.gotti.wurmunlimited.modsupport.actions.ModActions;

import mod.sin.actions.BankBalanceAction;
import mod.sin.actions.SpawnTeleportAction;

public class ServerTweaks
implements WurmServerMod, Configurable, PreInitable, Initable, ServerStartedListener {
    boolean bDebug = false;
    public static boolean enableCheckBankAction = true;
    public static boolean enableSpawnTeleportAction = true;
    public static String spawnTeleportName = "Spawn Teleport";
    public static int spawnTeleportTimer = 1200;
    private Logger logger;

    public ServerTweaks() {
        this.logger = Logger.getLogger(this.getClass().getName());
    }

    public void configure(Properties properties) {
        this.bDebug = Boolean.parseBoolean(properties.getProperty("debug", Boolean.toString(this.bDebug)));
        DeveloperCommands.addDevCommands = Boolean.parseBoolean(properties.getProperty("addDevCommands", Boolean.toString(DeveloperCommands.addDevCommands)));
        DeveloperCommands.cmdEventPower = Byte.parseByte(properties.getProperty("cmdEventPower", String.valueOf(DeveloperCommands.cmdEventPower)));
        DeveloperCommands.cmdUniqueSpawnPower = Byte.parseByte(properties.getProperty("cmdUniqueSpawnPower", String.valueOf(DeveloperCommands.cmdUniqueSpawnPower)));
        DeveloperCommands.cmdKingMePower = Byte.parseByte(properties.getProperty("cmdKingMePower", String.valueOf(DeveloperCommands.cmdKingMePower)));
        BugfixTweaks.immortalTraders = Boolean.parseBoolean(properties.getProperty("immortalTraders", Boolean.toString(BugfixTweaks.immortalTraders)));
        BugfixTweaks.immortalBartenders = Boolean.parseBoolean(properties.getProperty("immortalBartenders", Boolean.toString(BugfixTweaks.immortalBartenders)));
        BugfixTweaks.moonMetalVeinFix = Boolean.parseBoolean(properties.getProperty("moonMetalVeinFix", Boolean.toString(BugfixTweaks.moonMetalVeinFix)));
        BugfixTweaks.caHelpDefault = Boolean.parseBoolean(properties.getProperty("caHelpDefault", Boolean.toString(BugfixTweaks.caHelpDefault)));
        BugfixTweaks.disableChaos = Boolean.parseBoolean(properties.getProperty("disableChaos", Boolean.toString(BugfixTweaks.disableChaos)));
        GameplayTweaks.reduceUniqueTimer = Boolean.parseBoolean(properties.getProperty("reduceUniqueTimer", Boolean.toString(GameplayTweaks.reduceUniqueTimer)));
        GameplayTweaks.uniqueTimerReduction = Long.parseLong(properties.getProperty("uniqueTimerReduction", String.valueOf(GameplayTweaks.reduceUniqueTimer)));
        BugfixTweaks.logUniques = Boolean.parseBoolean(properties.getProperty("logUniques", Boolean.toString(BugfixTweaks.logUniques)));
        BugfixTweaks.fixSteamDupeAuthentication = Boolean.parseBoolean(properties.getProperty("fixSteamDupeAuthentication", Boolean.toString(BugfixTweaks.fixSteamDupeAuthentication)));
        BugfixTweaks.unlockTreasureChests = Boolean.parseBoolean(properties.getProperty("unlockTreasureChests", Boolean.toString(BugfixTweaks.unlockTreasureChests)));
        BugfixTweaks.buildOnHolyGround = Boolean.parseBoolean(properties.getProperty("buildOnHolyGround", Boolean.toString(BugfixTweaks.buildOnHolyGround)));
        BugfixTweaks.disableEpicMapTwitter = Boolean.parseBoolean(properties.getProperty("disableEpicMapTwitter", Boolean.toString(BugfixTweaks.disableEpicMapTwitter)));
        BugfixTweaks.removeInfidelError = Boolean.parseBoolean(properties.getProperty("removeInfidelError", Boolean.toString(BugfixTweaks.removeInfidelError)));
        GameplayTweaks.noDragonLeeching = Boolean.parseBoolean(properties.getProperty("noDragonLeeching", Boolean.toString(GameplayTweaks.noDragonLeeching)));
        GameplayTweaks.disableSermons = Boolean.parseBoolean(properties.getProperty("disableSermons", Boolean.toString(GameplayTweaks.disableSermons)));
        GameplayTweaks.disableFatigue = Boolean.parseBoolean(properties.getProperty("disableFatigue", Boolean.toString(GameplayTweaks.disableFatigue)));
        GameplayTweaks.gmUncapEnchants = Boolean.parseBoolean(properties.getProperty("gmUncapEnchants", Boolean.toString(GameplayTweaks.gmUncapEnchants)));
        GameplayTweaks.gmRemoveConditionChecks = Boolean.parseBoolean(properties.getProperty("gmRemoveConditionChecks", Boolean.toString(GameplayTweaks.gmRemoveConditionChecks)));
        BugfixTweaks.trueSteamAuthentication = Boolean.parseBoolean(properties.getProperty("trueSteamAuthentication", Boolean.toString(BugfixTweaks.trueSteamAuthentication)));
        GameplayTweaks.customRarityRates = Boolean.parseBoolean(properties.getProperty("customRarityRates", Boolean.toString(GameplayTweaks.customRarityRates)));
        GameplayTweaks.rarityFantasticChance = Float.parseFloat(properties.getProperty("rarityFantasticChance", String.valueOf(GameplayTweaks.rarityFantasticChance)));
        GameplayTweaks.raritySupremeChance = Float.parseFloat(properties.getProperty("raritySupremeChance", String.valueOf(GameplayTweaks.raritySupremeChance)));
        GameplayTweaks.rarityRareChance = Float.parseFloat(properties.getProperty("rarityRareChance", String.valueOf(GameplayTweaks.rarityRareChance)));
        GameplayTweaks.itemBasedFoodAffinities = Boolean.parseBoolean(properties.getProperty("itemBasedFoodAffinities", Boolean.toString(GameplayTweaks.itemBasedFoodAffinities)));
        GameplayTweaks.fixFreedomMycelium = Boolean.parseBoolean(properties.getProperty("fixFreedomMycelium", Boolean.toString(GameplayTweaks.fixFreedomMycelium)));
        GameplayTweaks.enableFreedomLibila = Boolean.parseBoolean(properties.getProperty("enableFreedomLibila", Boolean.toString(GameplayTweaks.enableFreedomLibila)));
        GameplayTweaks.enableFreedomDarkMessenger = Boolean.parseBoolean(properties.getProperty("enableFreedomDarkMessenger", Boolean.toString(GameplayTweaks.enableFreedomDarkMessenger)));
        GameplayTweaks.characteristicDivisor = Float.parseFloat(properties.getProperty("characteristicDivisor", String.valueOf(GameplayTweaks.characteristicDivisor)));
        enableCheckBankAction = Boolean.parseBoolean(properties.getProperty("enableCheckBankAction", Boolean.toString(enableCheckBankAction)));
        enableSpawnTeleportAction = Boolean.parseBoolean(properties.getProperty("enableSpawnTeleportAction", Boolean.toString(enableSpawnTeleportAction)));
        spawnTeleportName = properties.getProperty("spawnTeleportName", spawnTeleportName);
        spawnTeleportTimer = Integer.parseInt(properties.getProperty("spawnTeleportTimer", String.valueOf(spawnTeleportTimer)));
        GameplayTweaks.enableTaxConfig = Boolean.parseBoolean(properties.getProperty("enableTaxConfig", Boolean.toString(GameplayTweaks.enableTaxConfig)));
        GameplayTweaks.taxGracePeriodDays = Integer.parseInt(properties.getProperty("taxGracePeriodDays", String.valueOf(GameplayTweaks.taxGracePeriodDays)));
        GameplayTweaks.taxPercentIncreasePerDayGone = Double.parseDouble(properties.getProperty("taxPercentIncreasePerDayGone", String.valueOf(GameplayTweaks.taxPercentIncreasePerDayGone)));
        try {
            String logsPath = Paths.get("mods", new String[0]) + "/logs/";
            File newDirectory = new File(logsPath);
            if (!newDirectory.exists()) {
                newDirectory.mkdirs();
            }
            FileHandler fh = new FileHandler(String.valueOf(String.valueOf(logsPath)) + this.getClass().getSimpleName() + ".log", 10240000, 200, true);
            if (this.bDebug) {
                fh.setLevel(Level.INFO);
            } else {
                fh.setLevel(Level.WARNING);
            }
            fh.setFormatter(new SimpleFormatter());
            this.logger.addHandler(fh);
        }
        catch (IOException ie) {
            System.err.println(String.valueOf(this.getClass().getName()) + ": Unable to add file handler to logger");
        }
        this.logger.info("Developer Commands: " + DeveloperCommands.addDevCommands);
        if(DeveloperCommands.addDevCommands){
        	this.logger.info("(Command) Event Power: " + DeveloperCommands.cmdEventPower);
        	this.logger.info("(Command) Unique Spawn Power: " + DeveloperCommands.cmdUniqueSpawnPower);
        	this.logger.info("(Command) King Me Power: " + DeveloperCommands.cmdKingMePower);
        }
        this.logger.info("Immortal Traders: " + BugfixTweaks.immortalTraders);
        this.logger.info("Immortal Bartenders: " + BugfixTweaks.immortalBartenders);
        this.logger.info("Moon Metal Vein Fix: " + BugfixTweaks.moonMetalVeinFix);
        this.logger.info("CA Help Default: " + BugfixTweaks.caHelpDefault);
        this.logger.info("Disable Chaos: " + BugfixTweaks.disableChaos);
        this.logger.info("Reduce Unique Timer: " + GameplayTweaks.reduceUniqueTimer);
        this.logger.info("Unique Timer Reduction: " + GameplayTweaks.uniqueTimerReduction);
        this.logger.info("Logging Uniques: " + BugfixTweaks.logUniques);
        this.logger.info("Steam Duplicate Authentication Fix: " + BugfixTweaks.fixSteamDupeAuthentication);
        this.logger.info("Unlock Treasure Chests: " + BugfixTweaks.unlockTreasureChests);
        this.logger.info("Build On Holy Ground: " + BugfixTweaks.buildOnHolyGround);
        this.logger.info("Disable Epic Hexmap Twitter: " + BugfixTweaks.disableEpicMapTwitter);
        this.logger.info("Remove Infidel Error: " + BugfixTweaks.removeInfidelError);
        this.logger.info("True Steam Authentication: " + BugfixTweaks.trueSteamAuthentication);
        this.logger.info("Custom Rarity Rates: " + GameplayTweaks.customRarityRates);
        if(GameplayTweaks.customRarityRates){
        	this.logger.info("Fantastic Chance: " + GameplayTweaks.rarityFantasticChance+"%");
        	this.logger.info("Supreme Chance: " + GameplayTweaks.raritySupremeChance+"%");
        	this.logger.info("Rare Chance: " + GameplayTweaks.rarityRareChance+"%");
        }
        this.logger.info("Item Based Food Affinities: " + GameplayTweaks.itemBasedFoodAffinities);
        this.logger.info("No Dragon Leeching: " + GameplayTweaks.noDragonLeeching);
        this.logger.info("Disable Sermons: " + GameplayTweaks.disableSermons);
        this.logger.info("Disable Fatigue: " + GameplayTweaks.disableFatigue);
        this.logger.info("GM - Uncap Enchants: " + GameplayTweaks.gmUncapEnchants);
        this.logger.info("GM - Remove Condition Checks: " + GameplayTweaks.gmRemoveConditionChecks);
        logger.info("Fix Freedom Mycelium: " + GameplayTweaks.fixFreedomMycelium);
        logger.info("Enable Freedom Libila: " + GameplayTweaks.enableFreedomLibila);
        logger.info("Enable Freedom Dark Messenger: " + GameplayTweaks.enableFreedomDarkMessenger);
        logger.info("Characteristic Divisor: " + GameplayTweaks.characteristicDivisor);
        logger.info("Bank Balance Action: " + enableCheckBankAction);
        logger.info("Spawn Teleport Action: " + enableSpawnTeleportAction);
        if(enableSpawnTeleportAction){
        	logger.info("Spawn Teleport Name: " + spawnTeleportName);
        	logger.info("Spawn Teleport Timer: " + spawnTeleportTimer);
        }
        logger.info("Tax Config: " + GameplayTweaks.enableTaxConfig);
        if(GameplayTweaks.enableTaxConfig){
        	logger.info("Tax Grace Period (Days): " + GameplayTweaks.taxGracePeriodDays);
        	logger.info("Tax Percent Increase Per Day Gone: " + GameplayTweaks.taxPercentIncreasePerDayGone);
        }
        this.Debug("Debugging messages are enabled.");
    }

    private void Debug(String x) {
        if (this.bDebug) {
            System.out.println(String.valueOf(this.getClass().getSimpleName()) + ": " + x);
            System.out.flush();
            this.logger.log(Level.INFO, x);
        }
    }
    
    public void init(){
    	DeveloperCommands.init();
    	/*try{
	        if(addDevCommands){
		    	ClassPool classPool = HookManager.getInstance().getClassPool();
		    	Class<ServerTweaks> thisClass = ServerTweaks.class;
		    	classPool.appendClassPath("./mods/servertweaks/servertweaks.jar");
		    	
		    	CtClass ctCommunicator = classPool.get("com.wurmonline.server.creatures.Communicator");
		    	String replace = "java.nio.ByteBuffer tempBuffer = $1.duplicate();"
                		+ "if(!"+ServerTweaks.class.getName()+".customCommandHandler($1, this.player)){"
                		+ "  $_ = $proceed(tempBuffer);"
                		+ "}";
		    	Util.setReason("Add hook for custom dev commands.");
		    	Util.instrumentDeclared(thisClass, ctCommunicator, "reallyHandle", "reallyHandle_CMD_MESSAGE", replace);
	        	/*ctCommunicator.getDeclaredMethod("reallyHandle").instrument(new ExprEditor(){
	                public void edit(MethodCall m) throws CannotCompileException {
	                    if (m.getMethodName().equals("reallyHandle_CMD_MESSAGE")) {
	                        m.replace("java.nio.ByteBuffer tempBuffer = $1.duplicate();"
	                        		+ "if(!"+ServerTweaks.class.getName()+".customCommandHandler($1, this.player)){"
	                        		+ "  $_ = $proceed(tempBuffer);"
	                        		+ "}");
	                        return;
	                    }
	                }
	            });*/
	        /*}
    	}catch(Exception e){
    		e.printStackTrace();
    	}*/
    }

    public void preInit() {
    	BugfixTweaks.preInit();
    	GameplayTweaks.preInit();
    	ModActions.init();
    	// -- Old Attempts --
        /*try {
        	ClassPool classPool = HookManager.getInstance().getClassPool();
        	Class<ServerTweaks> thisClass = ServerTweaks.class;
        	String replace = "";
        	
            CtClass ctServer = classPool.get("com.wurmonline.server.Server");
            ctServer.getDeclaredMethod("startRunning").instrument(new ExprEditor(){
                public void edit(MethodCall m) throws CannotCompileException {
                    if (m.getMethodName().equals("createCreatureTemplates")) {
                        m.replace("$_ = $proceed($$);"
                        		+ "org.gotti.wurmunlimited.mods.servertweaks.Datamining.createCreatureSheet();");
                        return;
                    }
                }
            });
            
            // GM - Uncap the skill limit for all skills.
            ctQuestionParser.getDeclaredMethod("parseLearnSkillQuestion").instrument(new ExprEditor(){
                public void edit(MethodCall m) throws CannotCompileException {
                    if (m.getMethodName().equals("min")) {
                        m.replace("if($1 == 100.0){"
                        		+ "  $_ = $proceed(999.0D, (double)$2);"
                        		+ "}else{"
                        		+ "  $_ = $proceed($$);"
                        		+ "}");
                        return;
                    }
                }
            });
        }
        catch (NotFoundException e) {
            throw new HookException((Throwable)e);
        }*/
        //Datamining.createCreatureSheet();
    }

	@Override
	public void onServerStarted() {
		if(enableCheckBankAction){
			ModActions.registerAction(new BankBalanceAction());
		}
		if(enableSpawnTeleportAction){
			ModActions.registerAction(new SpawnTeleportAction(spawnTeleportName));
		}
		//Datamining.createCreatureSheet();
	}

}