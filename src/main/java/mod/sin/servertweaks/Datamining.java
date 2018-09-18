package mod.sin.servertweaks;

import java.io.FileOutputStream;
import java.util.logging.Logger;

import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.gotti.wurmunlimited.modloader.ReflectionUtil;

import com.wurmonline.server.creatures.CreatureTemplate;
import com.wurmonline.server.creatures.CreatureTemplateFactory;
import com.wurmonline.server.skills.SkillList;
import com.wurmonline.server.skills.Skills;

public class Datamining {
	private static final Logger logger = Logger.getLogger(Datamining.class.getName());
	public static void createCreatureSheet(){
		logger.info("Beginning to create creature data sheet.");
		try {
			String filename = "C:/Users/Sindusk/Documents/Wurm/CreatureData.xls";
			HSSFWorkbook workbook = new HSSFWorkbook();
			HSSFSheet sheet = workbook.createSheet("Creature Data");
			
			HSSFRow header = sheet.createRow(0);
			header.createCell(0).setCellValue("ID");
			header.createCell(1).setCellValue("Name");
			header.createCell(2).setCellValue("Size");
			header.createCell(3).setCellValue("Natural Armour");
			header.createCell(4).setCellValue("Speed");
			header.createCell(5).setCellValue("Move Rate");
			header.createCell(6).setCellValue("Hand Damage");
			header.createCell(7).setCellValue("Kick Damage");
			header.createCell(8).setCellValue("Bite Damage");
			header.createCell(9).setCellValue("Headbutt Damage");
			header.createCell(10).setCellValue("Breath Damage");
			header.createCell(11).setCellValue("Total Combat Rating");
			header.createCell(12).setCellValue("Armour Type");
	
			header.createCell(13).setCellValue("Weaponless Fighting");
			header.createCell(14).setCellValue("Fighting");
			header.createCell(15).setCellValue("Body Strength");
			header.createCell(16).setCellValue("Body Stamina");
			header.createCell(17).setCellValue("Body Control");
			header.createCell(18).setCellValue("Mind Logic");
			header.createCell(19).setCellValue("Mind Speed");
			header.createCell(20).setCellValue("Soul Strength");
			header.createCell(21).setCellValue("Soul Depth");
	
			header.createCell(22).setCellValue("Alignment");
	
			header.createCell(23).setCellValue("Base Combat Rating");
			header.createCell(24).setCellValue("Bonus Combat Rating");
			header.createCell(25).setCellValue("Combat Damage Type");
	
			header.createCell(26).setCellValue("Max Creature Percent");
			header.createCell(27).setCellValue("Max Population");
			header.createCell(28).setCellValue("Max Age");
			
			header.createCell(29).setCellValue("Types");
			
			int i = 0;
			for(CreatureTemplate ct : CreatureTemplateFactory.getInstance().getTemplates()){
				HSSFRow row = sheet.createRow(i+1);
				row.createCell(0).setCellValue(ct.getTemplateId());
				row.createCell(1).setCellValue(ct.getName());
				row.createCell(2).setCellValue(ct.getSize());
				row.createCell(3).setCellValue(ct.getNaturalArmour());
				float speed = ReflectionUtil.getPrivateField(ct, ReflectionUtil.getField(ct.getClass(), "speed"));
				row.createCell(4).setCellValue(speed);
				int moveRate = ReflectionUtil.getPrivateField(ct, ReflectionUtil.getField(ct.getClass(), "moveRate"));
				row.createCell(5).setCellValue(moveRate);
				row.createCell(6).setCellValue(ct.getHandDamage());
				row.createCell(7).setCellValue(ct.getKickDamage());
				row.createCell(8).setCellValue(ct.getBiteDamage());
				row.createCell(9).setCellValue(ct.getHeadButtDamage());
				row.createCell(10).setCellValue(ct.getBreathDamage());
				/*if(!baseCombatRatings.containsKey(names.get(i))){
					baseCombatRatings.put(names.get(i), 0F);
				}
				if(!bonusCombatRating.containsKey(names.get(i))){
					bonusCombatRating.put(names.get(i), 0F);
				}*/
				row.createCell(11).setCellValue(ct.getBaseCombatRating()+ct.getBonusCombatRating());
				row.createCell(12).setCellValue(ct.getArmourType().getName());
				
				Skills skills = ct.getSkills();
				row.createCell(13).setCellValue(skills.getSkillOrLearn(SkillList.WEAPONLESS_FIGHTING).getKnowledge());
				row.createCell(14).setCellValue(skills.getSkillOrLearn(SkillList.GROUP_FIGHTING).getKnowledge());
				row.createCell(15).setCellValue(skills.getSkillOrLearn(SkillList.BODY_STRENGTH).getKnowledge());
				row.createCell(16).setCellValue(skills.getSkillOrLearn(SkillList.BODY_STAMINA).getKnowledge());
				row.createCell(17).setCellValue(skills.getSkillOrLearn(SkillList.BODY_CONTROL).getKnowledge());
				row.createCell(18).setCellValue(skills.getSkillOrLearn(SkillList.MIND_LOGICAL).getKnowledge());
				row.createCell(19).setCellValue(skills.getSkillOrLearn(SkillList.MIND_SPEED).getKnowledge());
				row.createCell(20).setCellValue(skills.getSkillOrLearn(SkillList.SOUL_STRENGTH).getKnowledge());
				row.createCell(21).setCellValue(skills.getSkillOrLearn(SkillList.SOUL_DEPTH).getKnowledge());
	
				row.createCell(22).setCellValue(ct.getAlignment());
	
				row.createCell(23).setCellValue(ct.getBaseCombatRating());
				row.createCell(24).setCellValue(ct.getBonusCombatRating());
				row.createCell(25).setCellValue(ct.getCombatDamageType());
				
				row.createCell(26).setCellValue(ct.getMaxPercentOfCreatures());
				row.createCell(27).setCellValue(ct.getMaxPopulationOfCreatures());
				row.createCell(28).setCellValue(ct.getMaxAge());
				
				i++;
				/*strTypes = "";
				for(int x = 0; x < types.get(names.get(i)).size(); x++){
					if(strTypes.length() > 1){
						strTypes += ", ";
					}
					strTypes += types.get(names.get(i)).get(x);
				}
				i++;
				row.createCell(29).setCellValue(strTypes);*/
			}
			
			FileOutputStream fileOut = new FileOutputStream(filename);
			workbook.write(fileOut);
			//workbook.close();
			fileOut.close();
			System.out.println("Spreadsheet complete.");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
