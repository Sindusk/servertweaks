package mod.sin.servertweaks;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.gotti.wurmunlimited.modloader.classhooks.HookManager;

import com.wurmonline.server.Message;
import com.wurmonline.server.Players;
import com.wurmonline.server.Server;
import com.wurmonline.server.Servers;
import com.wurmonline.server.items.Item;
import com.wurmonline.server.items.ItemFactory;
import com.wurmonline.server.kingdom.King;
import com.wurmonline.server.kingdom.Kingdoms;
import com.wurmonline.server.players.Player;
import com.wurmonline.server.zones.Dens;

import javassist.ClassPool;
import javassist.CtClass;
import mod.sin.lib.Util;

public class DeveloperCommands {
    public static boolean addDevCommands = true;
    public static byte cmdEventPower = 0;
    public static byte cmdUniqueSpawnPower = 5;
    public static byte cmdKingMePower = 5;
	public static boolean customCommandHandler(ByteBuffer byteBuffer, Player player) throws UnsupportedEncodingException{
    	Logger inLog = Logger.getLogger("org.gotti.wurmunlimited.mods.servertweaks");
    	byte[] tempStringArr = new byte[byteBuffer.get() & 255];
        byteBuffer.get(tempStringArr);
        String message = new String(tempStringArr, "UTF-8");
        tempStringArr = new byte[byteBuffer.get() & 255];
        byteBuffer.get(tempStringArr);
        //String title = new String(tempStringArr, "UTF-8");
        if(player.mayMute() && message.startsWith("##")){
    		inLog.info("Player "+player.getName()+" used custom developer command "+message);
    		if(message.startsWith("##event") && player.getPower() >= cmdEventPower){
    			StringTokenizer tokens = new StringTokenizer(message);
                tokens.nextToken();
                String toSend = "";
                if (tokens.hasMoreTokens()) {
                    toSend = tokens.nextToken();
                }
                while (tokens.hasMoreTokens()) {
                    toSend = toSend + ' ' + tokens.nextToken();
                }
    			Player[] playarr = Players.getInstance().getPlayers();
    			Message mess = new Message(null, (byte)16, Servers.localServer.getAbbreviation()+"-Event", "<"+player.getName()+"> "+toSend, 255, 200, 0);
                for (int x = 0; x < playarr.length; ++x) {
                    if (playarr[x].getCommunicator().isInvulnerable() || !playarr[x].isKingdomChat() || playarr[x].isIgnored(player.getWurmId())) continue;
                    playarr[x].getCommunicator().sendMessage(mess);
                }
                player.chatted();
    		}else if(message.startsWith("##unique") && player.getPower() >= cmdUniqueSpawnPower){
    			Dens.checkDens(true);
    			player.getCommunicator().sendSafeServerMessage("Attempted to spawn a unique...");
    		}else if(message.startsWith("##kingme") && player.getPower() >= cmdKingMePower){
    			byte kingdom = player.getKingdomId();
    			if(King.getKing(kingdom) == null){
	    			King.createKing(player.getCurrentKingdom(), player.getName(), player.getWurmId(), player.getSex());
	    			//Methods.rewardRegalia(player); //Broken because player class is frozen.
	    			Item inventory = player.getInventory();
	    			byte template = Kingdoms.getKingdom(kingdom).getTemplate();
	    			if(template == 4){
	    				template = 1;
	    			}
	    			try{
		    			if (template == 1) {
		                    Item sceptre = ItemFactory.createItem(529, Server.rand.nextFloat() * 30.0f + 70.0f, player.getName());
		                    sceptre.setAuxData(kingdom);
		                    inventory.insertItem(sceptre, true);
		                    Item crown = ItemFactory.createItem(530, Server.rand.nextFloat() * 30.0f + 70.0f, player.getName());
		                    crown.setAuxData(kingdom);
		                    inventory.insertItem(crown, true);
		                    Item robes = ItemFactory.createItem(531, Server.rand.nextFloat() * 30.0f + 70.0f, player.getName());
		                    robes.setAuxData(kingdom);
		                    inventory.insertItem(robes, true);
		                } else if (template == 3) {
		                    Item sceptre = ItemFactory.createItem(535, Server.rand.nextFloat() * 30.0f + 70.0f, player.getName());
		                    sceptre.setAuxData(kingdom);
		                    inventory.insertItem(sceptre, true);
		                    Item crown = ItemFactory.createItem(536, Server.rand.nextFloat() * 30.0f + 70.0f, player.getName());
		                    crown.setAuxData(kingdom);
		                    inventory.insertItem(crown, true);
		                    Item robes = ItemFactory.createItem(537, Server.rand.nextFloat() * 30.0f + 70.0f, player.getName());
		                    robes.setAuxData(kingdom);
		                    inventory.insertItem(robes, true);
		                } else if (template == 2) {
		                    Item sceptre = ItemFactory.createItem(532, Server.rand.nextFloat() * 30.0f + 70.0f, player.getName());
		                    sceptre.setAuxData(kingdom);
		                    inventory.insertItem(sceptre, true);
		                    Item crown = ItemFactory.createItem(533, Server.rand.nextFloat() * 30.0f + 70.0f, player.getName());
		                    crown.setAuxData(kingdom);
		                    inventory.insertItem(crown, true);
		                    Item robes = ItemFactory.createItem(534, Server.rand.nextFloat() * 30.0f + 70.0f, player.getName());
		                    robes.setAuxData(kingdom);
		                    inventory.insertItem(robes, true);
		                }
    				} catch (Exception ex) {
	                    inLog.log(Level.WARNING, player.getName() + " " + ex.getMessage(), ex);
	                }
	    			player.getCommunicator().sendSafeServerMessage("You are now the king of "+Kingdoms.getNameFor(kingdom)+"!");
    			}else{
    				player.getCommunicator().sendSafeServerMessage(Kingdoms.getKingdom(kingdom).getName()+" already has a king: "+King.getKing(kingdom).getRulerTitle());
    			}
    		}else{
    			player.getCommunicator().sendSafeServerMessage("Custom command not found: "+message);
    		}
    		return true;
        }
        return false;
    }
	
	public static void init(){
		try{
	        if(addDevCommands){
		    	ClassPool classPool = HookManager.getInstance().getClassPool();
		    	Class<ServerTweaks> thisClass = ServerTweaks.class;
		    	classPool.appendClassPath("./mods/servertweaks/servertweaks.jar");
		    	
		    	CtClass ctCommunicator = classPool.get("com.wurmonline.server.creatures.Communicator");
		    	String replace = "java.nio.ByteBuffer tempBuffer = $1.duplicate();"
                		+ "if(!"+DeveloperCommands.class.getName()+".customCommandHandler($1, this.player)){"
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
	        }
    	}catch(Exception e){
    		e.printStackTrace();
    	}
	}
}
