package mod.sin.servertweaks;

import org.gotti.wurmunlimited.modloader.classhooks.HookException;
import org.gotti.wurmunlimited.modloader.classhooks.HookManager;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.NotFoundException;
import javassist.bytecode.Descriptor;
import mod.sin.lib.Util;

public class BugfixTweaks {
    public static boolean immortalTraders = true;
    public static boolean immortalBartenders = true;
    public static boolean moonMetalVeinFix = true;
    public static boolean caHelpDefault = true;
    public static boolean disableChaos = true;
    public static boolean logUniques = true;
    public static boolean fixSteamDupeAuthentication = true;
    public static boolean unlockTreasureChests = true;
    public static boolean buildOnHolyGround = true;
    public static boolean disableEpicMapTwitter = true;
    public static boolean removeInfidelError = true;
    public static boolean trueSteamAuthentication = true;
    public static boolean fixZombieEnchantError = true;
    public static boolean fixEpicMissionNaming = true;
    public static boolean fixSpellsWithoutEnterPermission = true;
    public static boolean allowFreedomMyceliumAbsorb = true;

	public static void preInit(){
		try {
        	ClassPool classPool = HookManager.getInstance().getClassPool();
        	Class<ServerTweaks> thisClass = ServerTweaks.class;
        	String replace = "";
        	
        	if(immortalTraders && immortalBartenders){
	        	// Make traders (and merchants for now) immortal.
        		replace = "return invulnerable || trader || bartender;";
	            //CtClass ctCreatureTemplate = classPool.get("com.wurmonline.server.creatures.CreatureTemplate");
	            //ctCreatureTemplate.getDeclaredMethod("isInvulnerable").setBody("return invulnerable || trader || bartender;");
        	} else if(immortalTraders && !immortalBartenders){
	        	// Make bartenders immortal.
        		replace = "return invulnerable || trader;";
	            //CtClass ctCreatureTemplate = classPool.get("com.wurmonline.server.creatures.CreatureTemplate");
	            //ctCreatureTemplate.getDeclaredMethod("isInvulnerable").setBody("return invulnerable || trader;");
        	} else if(!immortalTraders && immortalBartenders){
        		replace = "return invulnerable || bartender;";
        		//CtClass ctCreatureTemplate = classPool.get("com.wurmonline.server.creatures.CreatureTemplate");
        		//ctCreatureTemplate.getDeclaredMethod("isInvulnerable").setBody("return invulnerable || bartender;");
        	}
        	if(replace != ""){
        		CtClass ctCreatureTemplate = classPool.get("com.wurmonline.server.creatures.CreatureTemplate");
        		Util.setReason("Make traders and/or bartenders immortal.");
        		Util.setBodyDeclared(thisClass, ctCreatureTemplate, "isInvulnerable", replace);
        	}
        	
        	if(moonMetalVeinFix){
        		Util.setReason("Fix Glimmersteel & Adamantine veins from being depleted rapidly.");
            	CtClass ctCaveWallBehaviour = classPool.get("com.wurmonline.server.behaviours.CaveWallBehaviour");
            	CtClass ctItem = classPool.get("com.wurmonline.server.items.Item");
            	CtClass ctCreature = classPool.get("com.wurmonline.server.creatures.Creature");
            	CtClass ctAction = classPool.get("com.wurmonline.server.behaviours.Action");
            	CtClass[] params6 = {
            			ctAction,
            			ctCreature,
            			ctItem,
            			CtClass.intType,
            			CtClass.intType,
            			CtClass.booleanType,
            			CtClass.intType,
            			CtClass.intType,
            			CtClass.intType,
            			CtClass.shortType,
            			CtClass.floatType
            	};
            	String desc6 = Descriptor.ofMethod(CtClass.booleanType, params6);
            	replace = "resource = com.wurmonline.server.Server.getCaveResource(tilex, tiley);"
                		+ "if (resource == 65535) {"
                		+ "  resource = com.wurmonline.server.Server.rand.nextInt(10000);"
                		+ "}"
                		+ "if (resource > 1000 && (itemTemplateCreated == 693 || itemTemplateCreated == 697)) {"
                		+ "  resource = com.wurmonline.server.Server.rand.nextInt(1000);"
                		+ "}"
                		+ "$_ = $proceed($$);";
            	Util.instrumentDescribed(thisClass, ctCaveWallBehaviour, "action", desc6, "getDifficultyForTile", replace);
        	}
        	
        	if(caHelpDefault){
	            // Open CA HELP by default for new players.
	            CtClass ctLoginHandler = classPool.get("com.wurmonline.server.LoginHandler");
	            replace = "file.togglePlayerAssistantWindow(true);"
                		+ "$proceed($$);";
	            Util.setReason("Open CA HELP by default for new players.");
	            Util.instrumentDeclared(thisClass, ctLoginHandler, "handleLogin", "setSaveFile", replace);
	            /*ctLoginHandler.getDeclaredMethod("handleLogin").instrument(new ExprEditor(){
	                public void edit(MethodCall m) throws CannotCompileException {
	                    if (m.getClassName().equals("com.wurmonline.server.players.Player") && m.getMethodName().equals("setSaveFile")) {
	                        m.replace("file.togglePlayerAssistantWindow(true);"
	                        		+ (bDebug ? "logger.info(\"Edited CA HELP entry.\");" : "")
	                        		+ "$proceed($$);");
	                        return;
	                    }
	                }
	            });*/
        	}

        	if(disableChaos){
	            // Set "isChaosServer" to false, permanently.
        		CtClass ctServerEntry = classPool.get("com.wurmonline.server.ServerEntry");
        		replace = "{return false;}";
        		Util.setReason("Set isChaosServer to false, permanently.");
        		Util.setBodyDeclared(thisClass, ctServerEntry, "isChaosServer", replace);
	            //ctServerEntry.getDeclaredMethod("isChaosServer").setBody("{return false;}");
        	}

            if(logUniques){
	            // Logging for unique spawn checking.
	            CtClass ctDens = classPool.get("com.wurmonline.server.zones.Dens");
	            replace = "if(whileRunning){"
	            		+ "  logger.info(\"Checking unique spawns. [Running]\");"
	            		+ "}else{"
	            		+ "  logger.info(\"Checking unique spawns. [Not Running]\");"
	            		+ "}";
	            Util.setReason("Send server log message when the server checks to spawn uniques.");
	            Util.insertBeforeDeclared(thisClass, ctDens, "checkDens", replace);
	            /*ctDens.getDeclaredMethod("checkDens").insertAt(0, "if(whileRunning){"
	            		+ "  logger.info(\"Checking unique spawns. [Running]\");"
	            		+ "}else{"
	            		+ "  logger.info(\"Checking unique spawns. [Not Running]\");"
	            		+ "}");*/
            }
            
            if(fixSteamDupeAuthentication){
	            // Fix Steam duplicate authentication bug.
	            CtClass ctSteamHandler = classPool.get("com.wurmonline.server.steam.SteamHandler");
	            replace = "{"
	            		+ "  this.loginHandlerList.put($1, $2);"
	            		+ "  return true;"
	            		+ "}";
	            Util.setReason("Fix Steam duplicate authentication bug.");
	            Util.setBodyDeclared(thisClass, ctSteamHandler, "addLoginHandler", replace);
	            /*ctSteamHandler.getDeclaredMethod("addLoginHandler").setBody("{this.loginHandlerList.put($1, $2);"
	            		+ "return true;}");*/
            }

            if(unlockTreasureChests){
	            // Unlock all treasure chests.
                CtClass ctZone = classPool.get("com.wurmonline.server.zones.Zone");
                replace = "$_ = false;";
                Util.setReason("Unlock all treasure chests.");
                Util.instrumentDeclared(thisClass, ctZone, "createTreasureChest", "nextBoolean", replace);
                /*CtMethod ctCreateTreasureChest = ctZone.getDeclaredMethod("createTreasureChest");
	            ctCreateTreasureChest.instrument(new ExprEditor(){
	                public void edit(MethodCall m) throws CannotCompileException {
	                    if (m.getClassName().equals("java.util.Random") && m.getMethodName().equals("nextBoolean")) {
	                        m.replace("$_ = false;");
	                        return;
	                    }
	                }
	            });*/
            }
            
            if(buildOnHolyGround){
	            // Disable holy grounds from huge altars
	            CtClass ctMethodsStructure = classPool.get("com.wurmonline.server.behaviours.MethodsStructure");
	            replace = "$_ = $proceed(tiley);";
	            Util.setReason("Disable holy grounds from huge altars.");
	            Util.instrumentDeclared(thisClass, ctMethodsStructure, "canPlanStructureAt", "safeTileY", replace);
	            /*ctMethodsStructure.getDeclaredMethod("canPlanStructureAt").instrument(new ExprEditor(){
	                public void edit(MethodCall m) throws CannotCompileException {
	                    if (m.getMethodName().equals("safeTileY")) {
	                        m.replace("$_ = $proceed(tiley);");
	                        return;
	                    }
	                }
	            });*/
	            replace = "$_ = $proceed(tilex);";
	            Util.setReason("Disable holy grounds from huge altars.");
	            Util.instrumentDeclared(thisClass, ctMethodsStructure, "canPlanStructureAt", "safeTileX", replace);
	            /*ctMethodsStructure.getDeclaredMethod("canPlanStructureAt").instrument(new ExprEditor(){
	                public void edit(MethodCall m) throws CannotCompileException {
	                    if (m.getMethodName().equals("safeTileX")) {
	                        m.replace("$_ = $proceed(tilex);");
	                        return;
	                    }
	                }
	            });*/
	            replace = "$_ = null;";
	            Util.setReason("Disable holy grounds from huge altars.");
	            Util.instrumentDeclared(thisClass, ctMethodsStructure, "isFirstFenceTileOk", "getEvilAltar", replace);
	            /*ctMethodsStructure.getDeclaredMethod("isFirstFenceTileOk").instrument(new ExprEditor(){
	                public void edit(MethodCall m) throws CannotCompileException {
	                    if (m.getMethodName().equals("getEvilAltar")) {
	                        m.replace("$_ = null;");
	                        return;
	                    }
	                }
	            });*/
	            Util.setReason("Disable holy grounds from huge altars.");
	            Util.instrumentDeclared(thisClass, ctMethodsStructure, "isFirstFenceTileOk", "getGoodAltar", replace);
	            /*ctMethodsStructure.getDeclaredMethod("isFirstFenceTileOk").instrument(new ExprEditor(){
	                public void edit(MethodCall m) throws CannotCompileException {
	                    if (m.getMethodName().equals("getGoodAltar")) {
	                        m.replace("$_ = null;");
	                        return;
	                    }
	                }
	            });*/
	            
	            // -- This is all not working for some reason --
	            //CtClass ctVillageFoundationQuestion = HookManager.getInstance().getClassPool().get("com.wurmonline.server.questions.VillageFoundationQuestion");
	            //CtClass[] parameters = new CtClass[0];
	            //replace = "$_ = true;";
	            //Util.setReason("Disable holy ground from blocking village foundation.");
	            //Util.instrumentDeclared(thisClass, ctVillageFoundationQuestion, "answersFail", "checkBlockingItems", replace);
	            /*CtMethod method = ctVillageFoundationQuestion.getMethod("answersFail", Descriptor.ofMethod(CtPrimitiveType.booleanType, parameters));
	            method.instrument(new ExprEditor() { 
	                @Override
	                public void edit(MethodCall methodCall) throws CannotCompileException {
	                    String methodName = methodCall.getMethodName();
	                    if (methodName.equals("checkBlockingItems")){
	                        methodCall.replace("$_ = true;");
	                    }
	                }
	            } );*/
            }

            if(disableEpicMapTwitter){
	            // Remove epic hex map twitter messages.
                CtClass ctServer = classPool.get("com.wurmonline.server.Server");
                replace = "{}";
                Util.setReason("Disable twitter messages from the epic map.");
                Util.setBodyDeclared(thisClass, ctServer, "broadCastEpicEvent", replace);
	            //ctServer.getDeclaredMethod("broadCastEpicEvent").setBody("{}");
	            CtClass ctEpicEntity = classPool.get("com.wurmonline.server.epic.EpicEntity");
	            Util.setReason("Disable twitter messages from the epic map.");
	            Util.setBodyDeclared(thisClass, ctEpicEntity, "broadCastWithName", replace);
	            //ctEpicEntity.getDeclaredMethod("broadCastWithName").setBody("{}");
            }
            
            if(removeInfidelError){
	            // Remove infidel error
				Util.setReason("Remove infidel error [Creature Enchantment].");
	            CtClass ctCreatureEnchantment = classPool.get("com.wurmonline.server.spells.CreatureEnchantment");
	            replace = "$_ = true;";
	            Util.instrumentDeclared(thisClass, ctCreatureEnchantment, "precondition", "accepts", replace);

				Util.setReason("Remove infidel error [Karma Enchantment].");
	            CtClass ctKarmaEnchantment = classPool.get("com.wurmonline.server.spells.KarmaEnchantment");
				replace = "$_ = true;";
	            Util.instrumentDeclared(thisClass, ctKarmaEnchantment, "precondition", "accepts", replace);

				Util.setReason("Fix Bless infidel error.");
				CtClass ctBless = classPool.get("com.wurmonline.server.spells.Bless");
				replace = "$_ = true;";
				Util.instrumentDeclared(thisClass, ctBless, "precondition", "accepts", replace);

				Util.setReason("Fix Refresh infidel error.");
				CtClass ctRefresh = classPool.get("com.wurmonline.server.spells.Refresh");
				replace = "$_ = true;";
				Util.instrumentDeclared(thisClass, ctRefresh, "precondition", "accepts", replace);
            }

            if(fixZombieEnchantError){
				Util.setReason("Remove spam from creature enchantments on zombies.");
				CtClass ctCreatureEnchantment = classPool.get("com.wurmonline.server.spells.CreatureEnchantment");
				replace = "$_ = false;";
				Util.instrumentDeclared(thisClass, ctCreatureEnchantment, "precondition", "isReborn", replace);
			}

			if(fixEpicMissionNaming){
                Util.setReason("Fix epic mission naming.");
                CtClass ctEpicServerStatus = classPool.get("com.wurmonline.server.epic.EpicServerStatus");
                replace = "if($2.equals(\"\")){" +
                        "  $2 = com.wurmonline.server.deities.Deities.getDeityName($1);" +
                        "}";
                Util.insertBeforeDeclared(thisClass, ctEpicServerStatus, "generateNewMissionForEpicEntity", replace);
            }

            if(fixSpellsWithoutEnterPermission){
                Util.setReason("Fix permissions in structures so players cannot cast spells unless they have enter permission.");
                CtClass ctStructure = classPool.get("com.wurmonline.server.structures.Structure");
                replace = "if(com.wurmonline.server.behaviours.Actions.isActionDietySpell(action)){"
                        + "  return this.mayPass(performer);"
                        + "}"
                        + "$_ = $proceed($$);";
                Util.instrumentDeclared(thisClass, ctStructure, "isActionAllowed", "isActionImproveOrRepair", replace);
            }

            if(allowFreedomMyceliumAbsorb){
                Util.setReason("Enable Mycelium to be absorbed from Freedom Isles.");
                CtClass ctAction = classPool.get("com.wurmonline.server.behaviours.Action");
                CtClass ctCreature = classPool.get("com.wurmonline.server.creatures.Creature");
                CtClass ctTileBehaviour = classPool.get("com.wurmonline.server.behaviours.TileBehaviour");
                CtClass[] params = {
                        ctAction,
                        ctCreature,
                        CtClass.intType,
                        CtClass.intType,
                        CtClass.booleanType,
                        CtClass.intType,
                        CtClass.shortType,
                        CtClass.floatType
                };
                String desc = Descriptor.ofMethod(CtClass.booleanType, params);
                replace = "$_ = 3;";
                Util.instrumentDescribed(thisClass, ctTileBehaviour, "action", desc, "getKingdomTemplateId", replace);
            }

            if(trueSteamAuthentication){
				Util.setReason("Use SteamID as password for accounts.");
	            CtClass ctLoginHandler = classPool.get("com.wurmonline.server.LoginHandler");
	            replace = "$_ = $proceed($1, $6, $3, $4, $5, $6);";
	            Util.instrumentDeclared(thisClass, ctLoginHandler, "reallyHandle", "login", replace);
        	}

        }
        catch (NotFoundException e) {
            throw new HookException((Throwable)e);
        }
	}
}
