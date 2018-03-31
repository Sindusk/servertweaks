package mod.sin.actions;

import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.gotti.wurmunlimited.modsupport.actions.ActionPerformer;
import org.gotti.wurmunlimited.modsupport.actions.BehaviourProvider;
import org.gotti.wurmunlimited.modsupport.actions.ModAction;
import org.gotti.wurmunlimited.modsupport.actions.ModActions;

import com.wurmonline.server.Servers;
import com.wurmonline.server.behaviours.Action;
import com.wurmonline.server.behaviours.ActionEntry;
import com.wurmonline.server.behaviours.Actions;
import com.wurmonline.server.creatures.Creature;
import com.wurmonline.server.items.Item;
import com.wurmonline.server.items.ItemList;
import com.wurmonline.server.players.Player;

import mod.sin.servertweaks.ServerTweaks;

public class SpawnTeleportAction implements ModAction {
	private static Logger logger = Logger.getLogger(SpawnTeleportAction.class.getName());

	private final short actionId;
	private final ActionEntry actionEntry;

	public SpawnTeleportAction(String name) {
		logger.log(Level.WARNING, "BankBalanceAction()");

		actionId = (short) ModActions.getNextActionId();
		actionEntry = ActionEntry.createEntry(
			actionId, 
			name, 
			"teleporting", 
			new int[] { Actions.ACTION_TYPE_NOMOVE, Actions.ACTION_TYPE_VULNERABLE }
		);
		ModActions.registerAction(actionEntry);
	}


	@Override
	public BehaviourProvider getBehaviourProvider()
	{
		return new BehaviourProvider() {
			// Menu with activated object
			@Override
			public List<ActionEntry> getBehavioursFor(Creature performer, Item source, Item object)
			{
				return this.getBehavioursFor(performer, object);
			}

			// Menu without activated object
			@Override
			public List<ActionEntry> getBehavioursFor(Creature performer, Item object)
			{
				if(performer instanceof Player && object != null && (object.getTemplateId() == ItemList.bodyBody || object.getTemplateId() == ItemList.bodyHand)) {
					return Arrays.asList(actionEntry);
				}
				
				return null;
			}
		};
	}

	@Override
	public ActionPerformer getActionPerformer()
	{
		return new ActionPerformer() {

			@Override
			public short getActionId() {
				return actionId;
			}

			// Without activated object
			@Override
			public boolean action(Action act, Creature performer, Item target, short action, float counter)
			{
				try{
					boolean done = false;
					if(counter == 1.0f){
						performer.getCommunicator().sendNormalServerMessage("You sit down, calm yourself, and attempt to focus on a specific location.");
						performer.getCommunicator().sendNormalServerMessage("Moving, entering combat, or beginning any other action will cancel this teleport.");
						
						//final int time = (int) (4000-((performer.getMindSpeed().getKnowledge()*18) + (performer.getSoulDepth().getKnowledge()*18)));
						final int time = Math.max(ServerTweaks.spawnTeleportTimer, 1);
						performer.getCurrentAction().setTimeLeft(time);
						performer.sendActionControl("Teleporting", true, time);
					}else{
						if(performer.isFighting()){
							act.stop(false);
							performer.getCommunicator().sendAlertServerMessage("Your teleport was interrupted by entering combat.");
							done = true;
						}
						int time = performer.getCurrentAction().getTimeLeft();
						if(counter * 10.0f > time){
							if(performer instanceof Player){
								Player player = (Player) performer;
								player.setTeleportPoints((Servers.localServer.SPAWNPOINTJENNX*4)+2, (Servers.localServer.SPAWNPOINTJENNY*4)+2, 0, 0);
								if(player.startTeleporting()){
									player.getCommunicator().sendNormalServerMessage("You feel a slight tingle in your spine.");
									player.getCommunicator().sendTeleport(false);
								}
							}
							done = true;
						}
					}
					return done;
				}catch(Exception e){
					e.printStackTrace();
					return true;
				}
			}
			
			@Override
			public boolean action(Action act, Creature performer, Item source, Item target, short action, float counter)
			{
				return this.action(act, performer, target, action, counter);
			}
			
	
		}; // ActionPerformer
	}
}