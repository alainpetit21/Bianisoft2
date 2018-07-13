/* This file is part of the Bianisoft game library.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 *----------------------------------------------------------------------
 * Copyright (C) Alain Petit - alainpetit21@hotmail.com
 *
 * 18/12/10			0.1.0 First beta initial Version.
 * 12/09/11			0.1.2 Moved everything to a com.bianisoft
 *
 *-----------------------------------------------------------------------
 */
package com.bianisoft.tests;


//LWJGL library imports
import org.lwjgl.util.Rectangle;

//Bianisoft imports
import com.bianisoft.engine.Context;
import com.bianisoft.engine._3d.ObjMD2;
import com.bianisoft.engine.backgrounds.Background;
import com.bianisoft.engine.labels.Label;
import com.bianisoft.engine.labels.LabelTextField;
import com.bianisoft.engine.sprites.Sprite;
import com.bianisoft.engine.sprites.Sprite.State;
import com.bianisoft.engine.sprites.Button;


public class DesignCtxTest {
	public static void load(Context p_ctxUnder){
		/*DATA_BACKGROUND_TEMPLATE:#|CLASS_ID|NAME|RESSOURCE_NAME|POS_X|POS_Y|POS_Z*/
		/*DATA:0|Background|Back_1|/restest/back/bk1.png|0|0|100|1|*/
		Background backBack_1= new Background("/restest/backs/bk1.png");
		backBack_1.setTextID("Back_1");
		backBack_1.setPos(0, 0, 100);
		backBack_1.load();
		p_ctxUnder.addChild(backBack_1, false, false);

		/*DATA_BACKGROUND_TEMPLATE:#|CLASS_ID|NAME|RESSOURCE_NAME|POS_X|POS_Y|POS_Z*/
		/*DATA:1|Background|Back_2|/restest/back/bk2.png|0|0|50|1|*/
		Background backBack_2= new Background("/restest/backs/bk2.png");
		backBack_2.setTextID("Back_2");
		backBack_2.setPos(0, 0, 50);
		backBack_2.load();
		p_ctxUnder.addChild(backBack_2, false, false);

		/*DATA_SPRITE_TEMPLATE:#|CLASS_ID|NAME|RESSOURCE_NAME|POS_X|POS_Y|POS_Z|DEFAULT_STATE|DEFAULT_FRAME|NB_STATES|STATE_NAME|STATE_NB_FRAMES|STATE_SPEED*/
		/*DATA:2|Sprite|Spr_Cursor|/restest/sprite/Cursor.png|0|0|0|1|0|0|1|Idle|1|0.0|*/
		Sprite sprSpr_Cursor= new Sprite("/restest/sprites/cursor.png");
		sprSpr_Cursor.setTextID("Spr_Cursor");
		sprSpr_Cursor.setPos(0, 0, 0);
		sprSpr_Cursor.addState(sprSpr_Cursor.new State("Idle", 1, 0.0f));
		sprSpr_Cursor.load();
		sprSpr_Cursor.setCurState(0);
		sprSpr_Cursor.setCurFrame(0);
		p_ctxUnder.addChild(sprSpr_Cursor, true, false);

		/*DATA_BUTTON_TEMPLATE:#|CLASS_ID|NAME|RESSOURCE_NAME|POS_X|POS_Y|POS_Z|STATEIDLE_NB_FRAMES|IDLE_SPEED|STATEOVER_NB_FRAMES|STATEOVER_SPEED|STATEDOWN_NB_FRAMES|STATEDOWN_SPEED|STATESELECTED_NB_FRAMES|STATESELECTED_SPEED*/
		/*DATA:3|Button|Bt_TitlePlay|/restest/sprites/Bt_100.png|0|130|9|1|1|0.0|1|0.0|1|0.0|1|0.0|*/
		Button btBt_TitlePlay= new Button("/restest/sprites/bt100.png", 1, 0.0f, 1, 0.0f, 1, 0.0f, 1, 0.0f);
		btBt_TitlePlay.setTextID("Bt_Quit");
		btBt_TitlePlay.setPos(0, 0, 9);
		btBt_TitlePlay.load();
		p_ctxUnder.addChild(btBt_TitlePlay, false, false);

		/*DATA_LABEL_TEMPLATE:#|CLASS_ID|NAME|RESSOURCE_NAME|POS_X|POS_Y|POS_Z|FONT_SIZE|TEXT|ALIGNMENT|MULTILINE|REC_LEFT|REC_TOP|REC_WIDTH|RECT_HEIGHT*/
		/*DATA:4|Label|Lbl_Credits1|/restest/fonts/DidactGothic.ttf|0|300|9|1|20|-=-=-= KOZZ =-=-=-\n\n\n-Producer-\nDorion Kozma\n\n-Assistant Producer-\nAlain Petit\n\n-Technical Director-\nAlain Petit\n\n-Lead Programmer-\nAlain Petit\n\n-Programmer-\nSteven Swab\n\n-Art Director-\nAlain Petit\n\n-Concept Arts-\nAndrea Pixley\n\n-3D Modeller-\nMartin Tremblay\n\n-Localisation Director-\nAraceli Orozco Morales\n\n-Quality Assurance Director-\nAlain Petit\n\n-Quality Assurance Testers-\nGuy Petit\nAndrea Pixley\nAraceli Orozco Morales\nDorion Kozma\nSteve Swab\n\n\n-Publishing-\nWaterbend Investment\n\n|1|true|-480|-50|960|100|*/
		Label lblLbl_Credits1= new Label("/restest/fonts/DidactGothic.ttf", 20, "-=-=-= KOZZ =-=-=-\n\n\n-Producer-\nDorion Kozma\n\n-Assistant Producer-\nAlain Petit\n\n-Technical Director-\nAlain Petit\n\n-Lead Programmer-\nAlain Petit\n\n-Programmer-\nSteven Swab\n\n-Art Director-\nAlain Petit\n\n-Concept Arts-\nAndrea Pixley\n\n-3D Modeller-\nMartin Tremblay\n\n-Localisation Director-\nAraceli Orozco Morales\n\n-Quality Assurance Director-\nAlain Petit\n\n-Quality Assurance Testers-\nGuy Petit\nAndrea Pixley\nAraceli Orozco Morales\nDorion Kozma\nSteve Swab\n\n\n-Publishing-\nWaterbend Investment\n\n", 1, true, new Rectangle(-480, -50, 960, 100));
		lblLbl_Credits1.setTextID("Lbl_Credits1");
		lblLbl_Credits1.setPos(0, 300, 9);
		lblLbl_Credits1.load();
		p_ctxUnder.addChild(lblLbl_Credits1, false, false);

		/*DATA_LABEL_TEMPLATE:#|CLASS_ID|NAME|RESSOURCE_NAME|POS_X|POS_Y|POS_Z|FONT_SIZE|TEXT|ALIGNMENT|MULTILINE|REC_LEFT|REC_TOP|REC_WIDTH|RECT_HEIGHT*/
		/*DATA:5|LabelTextField|Lbl_TextFieldTest|/restest/fonts/DidactGothic.ttf|-255|-230|1|1|32|Nothing|0|false|0|-20|80|40|*/
		Label lblLbl_TextFieldTest= new LabelTextField("/restest/fonts/DidactGothic.ttf", 32, "Nothing", 0, false, new Rectangle(0, -20, 800, 40));
		lblLbl_TextFieldTest.setTextID("Lbl_TextFieldTest");
		lblLbl_TextFieldTest.setPos(-255, -230, 1);
		lblLbl_TextFieldTest.load();
		p_ctxUnder.addChild(lblLbl_TextFieldTest, false, false);

		/*DATA_SPRITE_TEMPLATE:#|CLASS_ID|NAME|RESSOURCE_NAME|POS_X|POS_Y|POS_Z|DEFAULT_STATE|DEFAULT_FRAME|NB_STATES|STATE_NAME|STATE_NB_FRAMES|STATE_SPEED*/
		/*DATA:6|Sprite|Spr_Symbols|/restest/sprites/symbols.png|-541|-224|8|1|0|0|10|Symbol1|1|0.0|Symbol2|1|0.0|Symbol3|1|0.0|Symbol4|1|0.0|Symbol5|1|0.0|Symbol6|1|0.0|Symbol7|1|0.0|Symbol8|1|0.0|Symbol9|1|0.0|Symbol10|1|0.0|*/
		Sprite sprSpr_Symbols=  new Sprite("/restest/sprites/symbols.png");
		sprSpr_Symbols.setTextID("Spr_Symbols");
		sprSpr_Symbols.setPos(-241, -124, 8);
		sprSpr_Symbols.addState(sprSpr_Symbols.new State("Symbol1", 1, 0.0f));
		sprSpr_Symbols.addState(sprSpr_Symbols.new State("Symbol2", 1, 0.0f));
		sprSpr_Symbols.addState(sprSpr_Symbols.new State("Symbol3", 1, 0.0f));
		sprSpr_Symbols.addState(sprSpr_Symbols.new State("Symbol4", 1, 0.0f));
		sprSpr_Symbols.addState(sprSpr_Symbols.new State("Symbol5", 1, 0.0f));
		sprSpr_Symbols.addState(sprSpr_Symbols.new State("Symbol6", 1, 0.0f));
		sprSpr_Symbols.addState(sprSpr_Symbols.new State("Symbol7", 1, 0.0f));
		sprSpr_Symbols.addState(sprSpr_Symbols.new State("Symbol8", 1, 0.0f));
		sprSpr_Symbols.addState(sprSpr_Symbols.new State("Symbol9", 1, 0.0f));
		sprSpr_Symbols.addState(sprSpr_Symbols.new State("Symbol10", 1, 0.0f));
		sprSpr_Symbols.load();
		sprSpr_Symbols.setCurState(0);
		sprSpr_Symbols.setCurFrame(0);
		p_ctxUnder.addChild(sprSpr_Symbols, false, false);

		/*DATA_SPRITE_TEMPLATE:#|CLASS_ID|NAME|RESSOURCE_NAME|POS_X|POS_Y|POS_Z|DEFAULT_STATE|DEFAULT_FRAME|NB_STATES|STATE_NAME|STATE_NB_FRAMES|STATE_SPEED*/
		/*DATA:7|Sprite|Spr_Symbols|/restest/sprites/symbols.png|-541|-224|8|1|0|0|10|Symbol1|1|0.0|Symbol2|1|0.0|Symbol3|1|0.0|Symbol4|1|0.0|Symbol5|1|0.0|Symbol6|1|0.0|Symbol7|1|0.0|Symbol8|1|0.0|Symbol9|1|0.0|Symbol10|1|0.0|*/
		Sprite sprSpr_Symbols2=  new Sprite("/restest/sprites/symbols.png");
		sprSpr_Symbols2.setTextID("Spr_Symbols2");
		sprSpr_Symbols2.setPos(-41, -124, 8);
		sprSpr_Symbols2.addState(sprSpr_Symbols2.new State("Symbol1", 1, 0.0f));
		sprSpr_Symbols2.addState(sprSpr_Symbols2.new State("Symbol2", 1, 0.0f));
		sprSpr_Symbols2.addState(sprSpr_Symbols2.new State("Symbol3", 1, 0.0f));
		sprSpr_Symbols2.addState(sprSpr_Symbols2.new State("Symbol4", 1, 0.0f));
		sprSpr_Symbols2.addState(sprSpr_Symbols2.new State("Symbol5", 1, 0.0f));
		sprSpr_Symbols2.addState(sprSpr_Symbols2.new State("Symbol6", 1, 0.0f));
		sprSpr_Symbols2.addState(sprSpr_Symbols2.new State("Symbol7", 1, 0.0f));
		sprSpr_Symbols2.addState(sprSpr_Symbols2.new State("Symbol8", 1, 0.0f));
		sprSpr_Symbols2.addState(sprSpr_Symbols2.new State("Symbol9", 1, 0.0f));
		sprSpr_Symbols2.addState(sprSpr_Symbols2.new State("Symbol10", 1, 0.0f));
		sprSpr_Symbols2.load();
		sprSpr_Symbols2.setCurState(0);
		sprSpr_Symbols2.setCurFrame(0);
		p_ctxUnder.addChild(sprSpr_Symbols2, false, false);

		/*DATA_MD2_TEMPLATE:#|CLASS_ID|NAME|MODEL_NAME|TEXTURE_NAME|POS_X|POS_Y|POS_Z|SCALE|DEFAULT_STATE|DEFAULT_FRAME|NB_STATES|STATE_NAME|STATE_NB_FRAMES|STATE_SPEED*/
		/*DATA:8|MD2|3D_OfficerPearl|/restest/3d/Officer.md2|/restest/3d/OfficerPearl.png|5|0|5|1|0.60|0|0|11|Idle|1|0.0|*/
		ObjMD2 md23D_OfficerPearl= new ObjMD2("/restest/3d/Officer.md2", "/restest/3d/OfficerPearl2.png");
		md23D_OfficerPearl.setScalingFactor(0.60f);
		md23D_OfficerPearl.setTextID("3D_OfficerPearl");
		md23D_OfficerPearl.setPos(5, 0, 5);
		md23D_OfficerPearl.load();
		p_ctxUnder.addChild(md23D_OfficerPearl, false, false);
	}
}
