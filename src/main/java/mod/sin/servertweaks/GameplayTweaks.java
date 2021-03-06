package mod.sin.servertweaks;

import com.wurmonline.server.*;
import com.wurmonline.server.items.Item;
import com.wurmonline.server.players.Player;
import com.wurmonline.server.players.PlayerInfo;
import com.wurmonline.server.players.PlayerInfoFactory;
import com.wurmonline.server.skills.Skill;
import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.NotFoundException;
import javassist.bytecode.Descriptor;
import javassist.expr.ExprEditor;
import javassist.expr.FieldAccess;
import mod.sin.lib.Util;
import org.gotti.wurmunlimited.modloader.ReflectionUtil;
import org.gotti.wurmunlimited.modloader.classhooks.HookException;
import org.gotti.wurmunlimited.modloader.classhooks.HookManager;

import java.util.Objects;
import java.util.logging.Logger;

public class GameplayTweaks {
    private static Logger logger = Logger.getLogger(GameplayTweaks.class.getName());

	public static boolean editEpicCurve = false;
	public static float epicCurveMultiplier = 1.0f;
    public static boolean customRarityRates = true;
    public static float rarityFantasticChance = 0.1f;
    public static float raritySupremeChance = 8.0f;
    public static float rarityRareChance = 100.0f;
    public static boolean itemBasedFoodAffinities = true;
    public static boolean reduceUniqueTimer = true;
    public static long uniqueTimerReduction = 950400000;
    public static boolean noDragonLeeching = true;
    public static boolean disableSermons = true;
    public static boolean disableFatigue = true;
    public static boolean gmUncapEnchants = true;
    public static boolean gmRemoveConditionChecks = true;
    public static boolean gmEnchantAnyItem = true;
    public static boolean fixFreedomMycelium = true;
    public static boolean enableFreedomLibila = true;
    public static boolean enableFreedomDarkMessenger = true;
    public static float characteristicDivisor = -1f;
    public static boolean enableTaxConfig = true;
    public static int taxGracePeriodDays = 30;
    public static double taxPercentIncreasePerDayGone = 0.03D;
    public static boolean removeLoadUnloadStrengthRequirement = true;
    public static boolean adjustDragonLoot = true;
    public static float dragonLootMultiplier = 0.2f;
    public static boolean removeConversionTimer = true;
    public static boolean showAllCreaturesMissionRuler = true;
    public static boolean enableFatigueSkillGainMultiplier = true;
    public static float fatigueMaximumMultiplier = 1.0f;
    public static float fatigueMinimumMultiplier = 0.2f;
    public static int fatigueMaximumThreshold = 39600; // Default is 39600 seconds, or 11 hours.
    public static int fatigueMinimumThreshold = 21600; // Default is 21600 seconds, or 6 hours.

	public static byte newGetPlayerRarity(Player p){
		int rarity = 0;
		try{
			byte nextActionRarity = ReflectionUtil.getPrivateField(p, ReflectionUtil.getField(p.getClass(), "nextActionRarity"));
			int windowOfCreation = ReflectionUtil.getPrivateField(p, ReflectionUtil.getField(p.getClass(), "windowOfCreation"));
	        if (Servers.isThisATestServer() && nextActionRarity != 0) {
	            rarity = nextActionRarity;
	            //nextActionRarity = 0;
	            ReflectionUtil.setPrivateField(p, ReflectionUtil.getField(p.getClass(), "nextActionRarity"), 0);
	        } else if (windowOfCreation > 0) {
	            //windowOfCreation = 0;
	            ReflectionUtil.setPrivateField(p, ReflectionUtil.getField(p.getClass(), "windowOfCreation"), 0);
	            /*float faintChance = 1.0f;
	            int supPremModifier = 0;
	            if (p.isPaying()) {
	                faintChance = 1.03f;
	                supPremModifier = 3;
	            }*/
	            if (Server.rand.nextFloat() * 100.0f <= rarityFantasticChance) {
	                rarity = 3;
	            } else if (Server.rand.nextFloat() * 100.0f <= raritySupremeChance) {
	                rarity = 2;
	            } else if(Server.rand.nextFloat() * 100.0f <= rarityRareChance) {
	                rarity = 1;
	            }
	        }
	    } catch (IllegalArgumentException | IllegalAccessException | ClassCastException | NoSuchFieldException e) {
			e.printStackTrace();
		}
        return (byte)rarity;
	}

	public static long getTimedAffinitySeed(Item item){
		PlayerInfo pinf = PlayerInfoFactory.getPlayerInfoWithName(item.getCreatorName());
		if(pinf != null){
			return pinf.wurmId;
		}
		return 0;
	}

    /**
     * Grants a multiplier to skill gain based on Fatigue.
     * Fatigue maximum: 43200 (seconds, equates to 12 hours).
     * Fatigue minimum: 0
     * @param player the player having skill gain calculated
     * @return a multiplier for skill gain
     */
	public static double getFatigueSkillMultiplier(Player player){
	    double mult;
        int fatigueLeft = player.getFatigueLeft();
        if (fatigueLeft <= fatigueMinimumThreshold){
            // The player has lower than the minimum threshold, so they have minimum skill gain.
            return (double) fatigueMinimumMultiplier;
        }else if (fatigueLeft >= fatigueMaximumThreshold){
            // The player has more than the maximum threshold, so they have maximum skill gain.
            return (double) fatigueMaximumMultiplier;
        }else{
            // They have some amount between, so do some math.
            double thresholdDifference = fatigueMaximumThreshold - fatigueMinimumThreshold; // Difference between the thresholds
            double difference = fatigueLeft - fatigueMinimumThreshold; // Amount between the two.
            mult = difference / Math.max(1, thresholdDifference); // Percentage between the threshholds
        }
        return mult;
    }

	public static void preInit(){
		try {
        	ClassPool classPool = HookManager.getInstance().getClassPool();
        	Class<ServerTweaks> thisClass = ServerTweaks.class;
        	String replace;

        	if(editEpicCurve){
        		// - Edit the effects of the epic curve from a server. - //
				CtClass ctSkill = classPool.get("com.wurmonline.server.skills.Skill");
				// Ensure the value is within the proper bounds
				epicCurveMultiplier = Math.min(1f, Math.max(0f, epicCurveMultiplier));
				replace = "$_ = $1 + (($proceed($$) - $1) * "+String.valueOf(epicCurveMultiplier)+");";
				Util.setReason("Edit the effect of the Epic Curve");
				CtClass[] params = {
						CtClass.doubleType
				};
				String desc = Descriptor.ofMethod(CtClass.doubleType, params);
				Util.instrumentDescribed(thisClass, ctSkill, "getKnowledge", desc, "getModifiedPercentageEffect", replace);
			}
        	
        	if(customRarityRates){
        		// - Change rarity odds when a player obtains a rarity window - //
                CtClass ctPlayer = classPool.get("com.wurmonline.server.players.Player");
                replace = "{ return "+GameplayTweaks.class.getName()+".newGetPlayerRarity(this); }";
                Util.setReason("Hook new rarity window method for custom rarity values.");
                Util.setBodyDeclared(thisClass, ctPlayer, "getRarity", replace);
        	}
        	
        	if(characteristicDivisor >= 0){
        		CtClass ctSkill = classPool.get("com.wurmonline.server.skills.Skill");
        		replace = "if(skillType == 0){ $5 = "+characteristicDivisor+";}$proceed($$);";
        		Util.setReason("Set new characteristic divisor.");
        		Util.instrumentDeclared(thisClass, ctSkill, "checkAdvance", "doSkillGainNew", replace);
        	}
        	
        	if(itemBasedFoodAffinities){
        		// - Make food/drink affinities based on Item ID instead of creature ID - //
                CtClass ctItem = classPool.get("com.wurmonline.server.items.Item");
                CtClass ctAffinitiesTimed = classPool.get("com.wurmonline.server.skills.AffinitiesTimed");
                replace = "if(item.getCreatorName() != null){"
                		+ "  $_ = $proceed("+GameplayTweaks.class.getName()+".getTimedAffinitySeed(item));"
                		+ "}else{"
                		+ "  $_ = $proceed($$);"
                		+ "}";
                Util.instrumentDeclared(thisClass, ctAffinitiesTimed, "getTimedAffinitySkill", "setSeed", replace);
                CtClass ctItemBehaviour = classPool.get("com.wurmonline.server.behaviours.ItemBehaviour");
                replace = "$_ = $proceed($1, $2, $3, $4, performer.getName());";
                Util.instrumentDeclared(thisClass, ctItemBehaviour, "handleRecipe", "createItem", replace);
                replace = "$_ = $proceed($1, $2, $3, $4, com.wurmonline.server.players.PlayerInfoFactory.getPlayerName(lastowner));";
                Util.instrumentDeclared(thisClass, ctItem, "pollFermenting", "createItem", replace);
                Util.instrumentDeclared(thisClass, ctItem, "pollDistilling", "createItem", replace);
                CtClass ctTempStates = classPool.get("com.wurmonline.server.items.TempStates");
                Util.instrumentDeclared(thisClass, ctTempStates, "checkForChange", "createItem", replace);
        	}
        	
            if(reduceUniqueTimer){
	            // Reduce last spawned unique timer (subtract millis to make work)
            	CtClass ctServerEntry = classPool.get("com.wurmonline.server.ServerEntry");
            	uniqueTimerReduction *= TimeConstants.HOUR_MILLIS;
            	replace = "{"
	            		+ "  return this.lastSpawnedUnique-"+String.valueOf(uniqueTimerReduction)+";"
	            		+ "}";
            	Util.setReason("Adjust unique spawn timer.");
            	Util.setBodyDeclared(thisClass, ctServerEntry, "getLastSpawnedUnique", replace);
	            /*ctServerEntry.getDeclaredMethod("getLastSpawnedUnique").setBody("{"
	            		+ "  return this.lastSpawnedUnique-"+String.valueOf(uniqueTimerReduction)+";"
	            		+ "}");*/
            }

            if(noDragonLeeching){
	            // Disable scale and hide to leechers
	            CtClass ctCreature = classPool.get("com.wurmonline.server.creatures.Creature");
	            replace = "leecher = new java.util.HashSet();";
	            Util.setReason("Disable scale and hide to leechers in a dragon slaying.");
	            Util.insertBeforeDeclared(thisClass, ctCreature, "distributeDragonScaleOrHide", replace);
	            //ctCreature.getDeclaredMethod("distributeDragonScaleOrHide").insertAt(0, "leecher = new java.util.HashSet();");
            }

            if(disableSermons){
	            // Disable sermons altogether
	            CtClass ctMethodsReligion = classPool.get("com.wurmonline.server.behaviours.MethodsReligion");
	            replace = "{"
	            		+ "  $1.getCommunicator().sendNormalServerMessage(\"You may not hold a sermon in this world.\");"
	            		+ "  return true;"
	            		+ "}";
	            Util.setReason("Disable sermons for the server.");
	            Util.setBodyDeclared(thisClass, ctMethodsReligion, "holdSermon", replace);
            }

            if(disableFatigue){
	            // Disable fatigue system altogether
	            CtClass ctPlayer = classPool.get("com.wurmonline.server.players.Player");
	            replace = "{"
	            		+ "  return 20000;"
	            		+ "}";
	            Util.setReason("Disable the fatigue system entirely.");
	            Util.setBodyDeclared(thisClass, ctPlayer, "getFatigueLeft", replace);
	            /*ctPlayer.getDeclaredMethod("getFatigueLeft").setBody("{"
	            		+ "  return 20000;"
	            		+ "}");*/
            }
            
            // Enable Mycelium to spread.
            if(fixFreedomMycelium){
	            CtClass ctTilePoller = classPool.get("com.wurmonline.server.zones.TilePoller");
	            replace = "$_ = true;";
	            Util.setReason("Fix mycelium spread on Freedom servers.");
	            Util.instrumentDeclared(thisClass, ctTilePoller, "checkEffects", "isThisAPvpServer", replace);
				ctTilePoller.getDeclaredMethod("checkForGrassSpread").instrument(new ExprEditor() {
					@Override
					public void edit(FieldAccess f) throws CannotCompileException {
						if ("checkMycel".equals(f.getFieldName())) {
							f.replace("$_ = true;");
						}
					}
				});
				ctTilePoller.getDeclaredMethod("checkForMycelGrowth").instrument(new ExprEditor() {
					@Override
					public void edit(FieldAccess f) throws CannotCompileException {
						if ("checkMycel".equals(f.getFieldName())) {
							f.replace("$_ = true;");
						}
					}
				});
            }
			
			if(enableFreedomLibila){
				// Disable conversion to HotS
	            CtClass ctQuestionParser = classPool.get("com.wurmonline.server.questions.QuestionParser");
	            replace = "$_ = $proceed((byte)4);";
	            // [3/29/18] Disabled - Not Working, but doesn't appear to be having any effect anyway.
	            /*Util.setReason("Disable conversion to HotS when becoming Libila.");
	            Util.instrumentDeclared(thisClass, ctQuestionParser, "parseAltarConvertQuestion", "getKingdomTemplateId", replace);*/

	            Util.setReason("Disable conversion to HotS when becoming Libila.");
	            Util.instrumentDeclared(thisClass, ctQuestionParser, "parseSetDeityQuestion", "setKingdomId", replace);
	            
	            // Allow use of the Bone Altar as a Freedom member
	            CtClass ctAltar = classPool.get("com.wurmonline.server.questions.AltarConversionQuestion");
	            replace = "$_ = true;";
	            Util.setReason("Allow use of Bone Altar conversion to Libila for Freedom.");
	            Util.instrumentDeclared(thisClass, ctAltar, "sendQuestion", "doesKingdomTemplateAcceptDeity", replace);

	            Util.setReason("Allow use of Bone Altar conversion to Libila for Freedom.");
	            Util.instrumentDeclared(thisClass, ctQuestionParser, "parseAltarConvertQuestion", "doesKingdomTemplateAcceptDeity", replace);

	            Util.setReason("Allow use of Bone Altar conversion to Libila for Freedom.");
	            Util.instrumentDeclared(thisClass, ctQuestionParser, "parseConvertQuestion", "doesKingdomTemplateAcceptDeity", replace);
			}
			
            
			if(enableFreedomDarkMessenger){
	            // Make Dark Messenger work for everyone.
	            CtClass ctWurmMailSender = classPool.get("com.wurmonline.server.behaviours.WurmMailSender");
	            replace = "$_ = true;";
	            Util.setReason("Enable Dark Messenger for non-Libila players.");
	            Util.instrumentDeclared(thisClass, ctWurmMailSender, "checkForWurmMail", "hasCourier", replace);
	            Util.setReason("Enable Dark Messenger for non-Libila players.");
	            Util.instrumentDeclared(thisClass, ctWurmMailSender, "sendWurmMail", "hasCourier", replace);
			}

			if(removeLoadUnloadStrengthRequirement){
                Util.setReason("Disable strength requirement checks for load/unload.");
                CtClass ctCargoTransportationMethods = classPool.get("com.wurmonline.server.behaviours.CargoTransportationMethods");
                replace = "{ return true; }";
                Util.setBodyDeclared(thisClass, ctCargoTransportationMethods, "strengthCheck", replace);
            }

            if(adjustDragonLoot){
                Util.setReason("Adjust the amount of scale/hide to distribute after a slaying (1/5).");
                CtClass ctCreature = classPool.get("com.wurmonline.server.creatures.Creature");
                replace = "{ return (1.0f + (float)$1.getWeightGrams() * $2)*"+String.valueOf(dragonLootMultiplier)+"f; }";
                Util.setBodyDeclared(thisClass, ctCreature, "calculateDragonLootTotalWeight", replace);
            }

            if(removeConversionTimer){
                Util.setReason("Remove waiting time between converting deity.");
                CtClass ctPlayerInfo = classPool.get("com.wurmonline.server.players.PlayerInfo");
                replace = "{ return true; }";
                Util.setBodyDeclared(thisClass, ctPlayerInfo, "mayChangeDeity", replace);
            }

            if(showAllCreaturesMissionRuler){
                CtClass ctMissionManager = classPool.get("com.wurmonline.server.questions.MissionManager");
                ctMissionManager.getDeclaredMethod("dropdownCreatureTemplates").instrument(new ExprEditor() {
                    @Override
                    public void edit(FieldAccess fieldAccess) throws CannotCompileException {
                        if (Objects.equals("baseCombatRating", fieldAccess.getFieldName()))
                            fieldAccess.replace("$_ = 1.0f;");
                    }
                });
            }

			if(enableTaxConfig){
				CtClass ctGuardPlan = classPool.get("com.wurmonline.server.villages.GuardPlan");
				replace = "if(vill.getMayor().isPlayer()){"
                		+ "  long lastLogout = com.wurmonline.server.Players.getInstance().getLastLogoutForPlayer(vill.getMayor().getId());"
                		+ "  long delta = System.currentTimeMillis() - lastLogout;"
                		+ "  long gracePeriod = "+String.valueOf(TimeConstants.DAY_MILLIS*taxGracePeriodDays)+";"
                		+ "  if(delta > gracePeriod){"
                		+ "    long daysGone = delta / "+TimeConstants.DAY_MILLIS+";"
                		+ "    cost = (long)((double)cost * (1+((double)daysGone * (double)"+String.valueOf(taxPercentIncreasePerDayGone)+")));"
                		+ "  }"
                		+ "}"
                		+ "$_ = java.lang.Math.max(cost, com.wurmonline.server.villages.Villages.MINIMUM_UPKEEP);"
                		+ "$proceed($$);";
				Util.setReason("Tax deeds based on duration the mayor logged out.");
				Util.instrumentDeclared(thisClass, ctGuardPlan, "getMonthlyCost", "max", replace);
			}
            
            if(gmUncapEnchants){
	            // GM - Uncap the possible enchant value.
	            CtClass ctGmSetEnchants = classPool.get("com.wurmonline.server.questions.GmSetEnchants");
	            replace = "$_ = $proceed($1, 99999);";
	            Util.setReason("Allow GM's to apply any enchant power.");
	            Util.instrumentDeclared(thisClass, ctGmSetEnchants, "answer", "min", replace);
            }
            
            if(gmRemoveConditionChecks){
	            // GM - Remove inability to spawn conditioned animals for types without a den.
	            CtClass ctQuestionParser = classPool.get("com.wurmonline.server.questions.QuestionParser");
	            replace = "$_ = true;";
	            Util.setReason("Allow GM's to spawn conditioned animals even if the animal has no den template.");
	            Util.instrumentDeclared(thisClass, ctQuestionParser, "parseCreatureCreationQuestion", "hasDen", replace);
            }

            if(gmEnchantAnyItem){
                Util.setReason("Allow GM's to enchant items regardless of whether the game thinks they can be enchanted.");
                CtClass ctItemBehaviour = classPool.get("com.wurmonline.server.behaviours.ItemBehaviour");
                CtClass ctCreature = classPool.get("com.wurmonline.server.creatures.Creature");
                CtClass ctItem = classPool.get("com.wurmonline.server.items.Item");
                CtClass ctList = classPool.get("java.util.List");
                CtClass[] params1 = {
                        ctCreature,
                        ctItem,
                        ctItem
                };
                String desc1 = Descriptor.ofMethod(ctList, params1);
                replace = "$_ = true;";
                Util.instrumentDescribed(thisClass, ctItemBehaviour, "getBehavioursFor", desc1, "mayBeEnchanted", replace);

                Util.setReason("Allow GM's to enchant items regardless of whether the game thinks they can be enchanted.");
                CtClass ctAction = classPool.get("com.wurmonline.server.behaviours.Action");
                CtClass[] params2 = {
                        ctAction,
                        ctCreature,
                        ctItem,
                        ctItem,
                        CtClass.shortType,
                        CtClass.floatType
                };
                String desc2 = Descriptor.ofMethod(CtClass.booleanType, params2);
                replace = "$_ = true;";
                Util.instrumentDescribed(thisClass, ctItemBehaviour, "action", desc2, "mayBeEnchanted", replace);
			}

			if(enableFatigueSkillGainMultiplier){
                CtClass ctSkill = classPool.get("com.wurmonline.server.skills.Skill");
                CtClass[] params3 = {
                        CtClass.doubleType,
                        CtClass.booleanType,
                        CtClass.floatType,
                        CtClass.booleanType,
                        CtClass.doubleType
                };
                String desc3 = Descriptor.ofMethod(CtClass.voidType, params3);
                Util.setReason("Adjust skill gain for fatigue.");
                replace = "advanceMultiplicator *= " + GameplayTweaks.class.getName() + ".getFatigueSkillMultiplier($0);"
                        + "$_ = $proceed($$);";
                Util.instrumentDescribed(thisClass, ctSkill, "alterSkill", desc3, "hasSleepBonus", replace);
            }
        }
        catch (NotFoundException | CannotCompileException e) {
            throw new HookException(e);
        }
	}
}
