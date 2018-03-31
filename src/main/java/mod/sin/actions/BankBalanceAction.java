package mod.sin.actions;

import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.gotti.wurmunlimited.modsupport.actions.ActionPerformer;
import org.gotti.wurmunlimited.modsupport.actions.BehaviourProvider;
import org.gotti.wurmunlimited.modsupport.actions.ModAction;
import org.gotti.wurmunlimited.modsupport.actions.ModActions;

import com.wurmonline.server.behaviours.Action;
import com.wurmonline.server.behaviours.ActionEntry;
import com.wurmonline.server.creatures.Creature;
import com.wurmonline.server.economy.Change;
import com.wurmonline.server.economy.Economy;
import com.wurmonline.server.items.Item;
import com.wurmonline.server.items.ItemList;
import com.wurmonline.server.players.Player;

public class BankBalanceAction implements ModAction {
	private static Logger logger = Logger.getLogger(BankBalanceAction.class.getName());

	private final short actionId;
	private final ActionEntry actionEntry;

	public BankBalanceAction() {
		logger.log(Level.WARNING, "BankBalanceAction()");

		actionId = (short) ModActions.getNextActionId();
		actionEntry = ActionEntry.createEntry(
			actionId, 
			"Bank Balance", 
			"getting bank balance", 
			new int[] { 6 /* ACTION_TYPE_NOMOVE */ }	// 6 /* ACTION_TYPE_NOMOVE */, 48 /* ACTION_TYPE_ENEMY_ALWAYS */, 36 /* ACTION_TYPE_ALWAYS_USE_ACTIVE_ITEM */
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
				long money = performer.getMoney();
				Change change = Economy.getEconomy().getChangeFor(money);
				performer.getCommunicator().sendNormalServerMessage("You currently have " + change.getChangeString() + " on your bank account.");
				return true;
			}
			
			@Override
			public boolean action(Action act, Creature performer, Item source, Item target, short action, float counter)
			{
				return this.action(act, performer, target, action, counter);
			}
			
	
		}; // ActionPerformer
	}
}