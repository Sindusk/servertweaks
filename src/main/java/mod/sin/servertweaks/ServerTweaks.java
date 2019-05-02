package mod.sin.servertweaks;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Properties;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import mod.sin.actions.KingMeAction;
import mod.sin.lib.Prop;
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
    private Logger logger = Logger.getLogger(ServerTweaks.class.getName());

    public void configure(Properties properties) {
        Prop.properties = properties;

        DeveloperCommands.addDevCommands = Boolean.parseBoolean(properties.getProperty("addDevCommands", Boolean.toString(DeveloperCommands.addDevCommands)));
        DeveloperCommands.cmdEventPower = Byte.parseByte(properties.getProperty("cmdEventPower", String.valueOf(DeveloperCommands.cmdEventPower)));
        DeveloperCommands.cmdUniqueSpawnPower = Byte.parseByte(properties.getProperty("cmdUniqueSpawnPower", String.valueOf(DeveloperCommands.cmdUniqueSpawnPower)));
        DeveloperCommands.cmdKingMePower = Byte.parseByte(properties.getProperty("cmdKingMePower", String.valueOf(DeveloperCommands.cmdKingMePower)));
        BugfixTweaks.immortalTraders = Boolean.parseBoolean(properties.getProperty("immortalTraders", Boolean.toString(BugfixTweaks.immortalTraders)));
        BugfixTweaks.immortalBartenders = Boolean.parseBoolean(properties.getProperty("immortalBartenders", Boolean.toString(BugfixTweaks.immortalBartenders)));
        BugfixTweaks.moonMetalVeinFix = Boolean.parseBoolean(properties.getProperty("moonMetalVeinFix", Boolean.toString(BugfixTweaks.moonMetalVeinFix)));
        BugfixTweaks.moonMetalMinimum = Integer.parseInt(properties.getProperty("moonMetalMinimum", Integer.toString(BugfixTweaks.moonMetalMinimum)));
        BugfixTweaks.moonMetalMaximum = Integer.parseInt(properties.getProperty("moonMetalMaximum", Integer.toString(BugfixTweaks.moonMetalMaximum)));
        BugfixTweaks.caHelpDefault = Boolean.parseBoolean(properties.getProperty("caHelpDefault", Boolean.toString(BugfixTweaks.caHelpDefault)));
        BugfixTweaks.disableChaos = Boolean.parseBoolean(properties.getProperty("disableChaos", Boolean.toString(BugfixTweaks.disableChaos)));
        GameplayTweaks.reduceUniqueTimer = Boolean.parseBoolean(properties.getProperty("reduceUniqueTimer", Boolean.toString(GameplayTweaks.reduceUniqueTimer)));
        GameplayTweaks.uniqueTimerReduction = Long.parseLong(properties.getProperty("uniqueTimerReduction", String.valueOf(GameplayTweaks.reduceUniqueTimer)));
        BugfixTweaks.logUniques = Boolean.parseBoolean(properties.getProperty("logUniques", Boolean.toString(BugfixTweaks.logUniques)));
        BugfixTweaks.fixSteamDupeAuthentication = Boolean.parseBoolean(properties.getProperty("fixSteamDupeAuthentication", Boolean.toString(BugfixTweaks.fixSteamDupeAuthentication)));
        BugfixTweaks.unlockTreasureChests = Boolean.parseBoolean(properties.getProperty("unlockTreasureChests", Boolean.toString(BugfixTweaks.unlockTreasureChests)));
        BugfixTweaks.buildOnHolyGround = Boolean.parseBoolean(properties.getProperty("buildOnHolyGround", Boolean.toString(BugfixTweaks.buildOnHolyGround)));
        BugfixTweaks.disableEpicMapTwitter = Boolean.parseBoolean(properties.getProperty("disableEpicMapTwitter", Boolean.toString(BugfixTweaks.disableEpicMapTwitter)));
        GameplayTweaks.editEpicCurve = Prop.getBooleanProperty("editEpicCurve", GameplayTweaks.editEpicCurve);
        GameplayTweaks.epicCurveMultiplier = Prop.getFloatProperty("epicCurveMultiplier", GameplayTweaks.epicCurveMultiplier);
        GameplayTweaks.noDragonLeeching = Boolean.parseBoolean(properties.getProperty("noDragonLeeching", Boolean.toString(GameplayTweaks.noDragonLeeching)));
        GameplayTweaks.disableSermons = Boolean.parseBoolean(properties.getProperty("disableSermons", Boolean.toString(GameplayTweaks.disableSermons)));
        GameplayTweaks.disableFatigue = Boolean.parseBoolean(properties.getProperty("disableFatigue", Boolean.toString(GameplayTweaks.disableFatigue)));
        GameplayTweaks.gmUncapEnchants = Boolean.parseBoolean(properties.getProperty("gmUncapEnchants", Boolean.toString(GameplayTweaks.gmUncapEnchants)));
        GameplayTweaks.gmEnchantAnyItem = Boolean.parseBoolean(properties.getProperty("gmEnchantAnyItem", Boolean.toString(GameplayTweaks.gmEnchantAnyItem)));
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
        BugfixTweaks.fixZombieEnchantError = Boolean.parseBoolean(properties.getProperty("fixZombieEnchantError", Boolean.toString(BugfixTweaks.fixZombieEnchantError)));
        BugfixTweaks.fixEpicMissionNaming = Boolean.parseBoolean(properties.getProperty("fixEpicMissionNaming", Boolean.toString(BugfixTweaks.fixEpicMissionNaming)));
        BugfixTweaks.fixSpellsWithoutEnterPermission = Boolean.parseBoolean(properties.getProperty("fixSpellsWithoutEnterPermission", Boolean.toString(BugfixTweaks.fixSpellsWithoutEnterPermission)));
        GameplayTweaks.removeLoadUnloadStrengthRequirement = Boolean.parseBoolean(properties.getProperty("removeLoadUnloadStrengthRequirement", Boolean.toString(GameplayTweaks.removeLoadUnloadStrengthRequirement)));
        GameplayTweaks.adjustDragonLoot = Boolean.parseBoolean(properties.getProperty("adjustDragonLoot", Boolean.toString(GameplayTweaks.adjustDragonLoot)));
        GameplayTweaks.dragonLootMultiplier = Float.parseFloat(properties.getProperty("dragonLootMultiplier", String.valueOf(GameplayTweaks.dragonLootMultiplier)));
        GameplayTweaks.removeConversionTimer = Boolean.parseBoolean(properties.getProperty("removeConversionTimer", Boolean.toString(GameplayTweaks.removeConversionTimer)));
        BugfixTweaks.allowFreedomMyceliumAbsorb = Boolean.parseBoolean(properties.getProperty("allowFreedomMyceliumAbsorb", Boolean.toString(BugfixTweaks.allowFreedomMyceliumAbsorb)));
        GameplayTweaks.showAllCreaturesMissionRuler = Boolean.parseBoolean(properties.getProperty("showAllCreaturesMissionRuler", Boolean.toString(GameplayTweaks.showAllCreaturesMissionRuler)));
        GameplayTweaks.enableFatigueSkillGainMultiplier = Prop.getBooleanProperty("enableFatigueSkillGainMultiplier", GameplayTweaks.enableFatigueSkillGainMultiplier);
        GameplayTweaks.fatigueMaximumMultiplier = Prop.getFloatProperty("fatigueMaximumMultiplier", GameplayTweaks.fatigueMaximumMultiplier);
        GameplayTweaks.fatigueMinimumMultiplier = Prop.getFloatProperty("fatigueMinimumMultiplier", GameplayTweaks.fatigueMinimumMultiplier);
        GameplayTweaks.fatigueMaximumThreshold = Prop.getIntegerProperty("fatigueMaximumThreshold", GameplayTweaks.fatigueMaximumThreshold);
        GameplayTweaks.fatigueMinimumThreshold = Prop.getIntegerProperty("fatigueMinimumThreshold", GameplayTweaks.fatigueMinimumThreshold);

        // Developer Commands
        this.logger.info("Developer Commands: " + DeveloperCommands.addDevCommands);
        if(DeveloperCommands.addDevCommands){
        	this.logger.info("(Command) Event Power: " + DeveloperCommands.cmdEventPower);
        	this.logger.info("(Command) Unique Spawn Power: " + DeveloperCommands.cmdUniqueSpawnPower);
        	this.logger.info("(Command) King Me Power: " + DeveloperCommands.cmdKingMePower);
        }
        // Bugfix Tweaks
        this.logger.info("Immortal Traders: " + BugfixTweaks.immortalTraders);
        this.logger.info("Immortal Bartenders: " + BugfixTweaks.immortalBartenders);
        this.logger.info("Moon Metal Vein Fix: " + BugfixTweaks.moonMetalVeinFix);
        this.logger.info("Moon Metal Minimum: " + BugfixTweaks.moonMetalMinimum);
        this.logger.info("Moon Metal Maximum: " + BugfixTweaks.moonMetalMaximum);
        this.logger.info("CA Help Default: " + BugfixTweaks.caHelpDefault);
        this.logger.info("Disable Chaos: " + BugfixTweaks.disableChaos);
        this.logger.info("Reduce Unique Timer: " + GameplayTweaks.reduceUniqueTimer);
        this.logger.info("Unique Timer Reduction: " + GameplayTweaks.uniqueTimerReduction);
        this.logger.info("Logging Uniques: " + BugfixTweaks.logUniques);
        this.logger.info("Steam Duplicate Authentication Fix: " + BugfixTweaks.fixSteamDupeAuthentication);
        this.logger.info("Unlock Treasure Chests: " + BugfixTweaks.unlockTreasureChests);
        this.logger.info("Build On Holy Ground: " + BugfixTweaks.buildOnHolyGround);
        this.logger.info("Disable Epic Hexmap Twitter: " + BugfixTweaks.disableEpicMapTwitter);
        this.logger.info("True Steam Authentication: " + BugfixTweaks.trueSteamAuthentication);
        this.logger.info("Fix Zombie Enchant Errors: " + BugfixTweaks.fixZombieEnchantError);
        this.logger.info("Fix Epic Mission Naming: " + BugfixTweaks.fixEpicMissionNaming);
        this.logger.info("Fix Spells Without Enter Permission: " + BugfixTweaks.fixSpellsWithoutEnterPermission);
        this.logger.info("Allow Freedom Mycelium Absorb: " + BugfixTweaks.allowFreedomMyceliumAbsorb);
        // Gameplay Tweaks
        this.logger.info("Edit Epic Curve: " + GameplayTweaks.editEpicCurve);
        this.logger.info("Epic Curve Multiplier: " + GameplayTweaks.epicCurveMultiplier);
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
        this.logger.info("GM - Enchant Any Item: " + GameplayTweaks.gmEnchantAnyItem);
        logger.info("Fix Freedom Mycelium: " + GameplayTweaks.fixFreedomMycelium);
        logger.info("Enable Freedom Libila: " + GameplayTweaks.enableFreedomLibila);
        logger.info("Enable Freedom Dark Messenger: " + GameplayTweaks.enableFreedomDarkMessenger);
        logger.info("Remove Load/Unload Strength Requirement: " + GameplayTweaks.removeLoadUnloadStrengthRequirement);
        logger.info("Adjust Dragon Loot: " + GameplayTweaks.adjustDragonLoot);
        logger.info("Dragon Loot Multiplier: " + GameplayTweaks.dragonLootMultiplier);
        logger.info("Remove Conversion Timer: " + GameplayTweaks.removeConversionTimer);
        logger.info("Show All Creatures in Mission Ruler: " + GameplayTweaks.showAllCreaturesMissionRuler);
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
        logger.info("Enable Fatigue Skill Gain Multiplier: "+GameplayTweaks.enableFatigueSkillGainMultiplier);
        if(GameplayTweaks.enableFatigueSkillGainMultiplier){
            logger.info("Fatigue Maximum Skill Gain Multiplier: "+GameplayTweaks.fatigueMaximumMultiplier);
            logger.info("Fatigue Minimum Skill Gain Multiplier: "+GameplayTweaks.fatigueMinimumMultiplier);
            logger.info("Fatigue Maximum Threshold: "+GameplayTweaks.fatigueMaximumThreshold);
            logger.info("Fatigue Minimum Threshold: "+GameplayTweaks.fatigueMinimumThreshold);
        }
    }
    
    public void init(){
    	DeveloperCommands.init();
    }

    public void preInit() {
    	BugfixTweaks.preInit();
    	GameplayTweaks.preInit();
    	ModActions.init();
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
		if(DeveloperCommands.addDevCommands) {
            ModActions.registerAction(new KingMeAction());
        }
		//Datamining.createCreatureSheet();
	}

}