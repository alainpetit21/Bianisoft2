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
 * 18/12/10			0.1 First beta initial Version.
 * 12/09/11			0.1.2 Moved everything to a com.bianisoft
 *
 *-----------------------------------------------------------------------
 */
package com.bianisoft.engine.manager;


//Standard Java library imports
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.lang.reflect.Constructor;

//XML library imports
import org.xml.sax.*;
import org.xml.sax.helpers.XMLReaderFactory;

//Bianisoft libray imports
import com.bianisoft.engine.App;
import com.bianisoft.engine.Context;
import com.bianisoft.engine.Drawable;
import com.bianisoft.engine.backgrounds.Background;
import com.bianisoft.engine.backgrounds.BackgroundTiled;
import com.bianisoft.engine.helper.FixResFilename;
import com.bianisoft.engine.labels.Label;
import com.bianisoft.engine.sprites.Button;
import com.bianisoft.engine.sprites.Sprite;


public class MngHandlerTMX implements ContentHandler{
	public static final int MODE_LAYERS	= 1;
	public static final int MODE_OBJECTS= 2;

	public ArrayList<BackgroundTiled>	m_arLayers= new ArrayList<BackgroundTiled>();
	public ArrayList<Background>		m_arTileBank= new ArrayList<Background>();
	public ArrayList<Integer>			m_arNbTilesInBank= new ArrayList<Integer>();
	public ArrayList<Drawable>			m_arPhysObjToAdd= new ArrayList<Drawable>();
	
	public String	m_stResFilename;
	public String	m_stResPathReference;
	public Drawable	m_objLastCreated;
	public String	m_stTypeLastCreated;

	public int		m_nMapWidth;
	public int		m_nMapHeight;
	public int		m_nTileSize;
	public int		m_nCptX;
	public int		m_nCptY;
	public int		m_nCptZ= 100;

	public int[][]	m_arMap;
	
	public boolean	m_isReadingData;
	public int		m_nTypeObjectReading;
	
	public String	m_stCustomName;
	public int		m_nCustomX;
	public int		m_nCustomY;
	public int		m_nCustomZ;
	
	
	public static void loadTMX(String p_stResFilename, Context p_ctxUnder){
		MngHandlerTMX objHandler= new MngHandlerTMX();
				
		objHandler.loadTMX(p_stResFilename);
		
		for(Drawable objDrawable : objHandler.m_arPhysObjToAdd){
			objDrawable.load();
			p_ctxUnder.addChild(objDrawable, objDrawable.m_isCameraBound, objDrawable.m_isInInfinity);
		}
	}
	
	
    public void loadTMX(String p_stResFilename){
		m_stResFilename= p_stResFilename;
		
		try{
			XMLReader parser= XMLReaderFactory.createXMLReader();
			parser.setContentHandler(this);
			
			p_stResFilename= FixResFilename.fixResFilename(p_stResFilename);
			URL objURL= Thread.currentThread().getContextClassLoader().getResource(p_stResFilename);
			m_stResPathReference= objURL.getFile();
					
			int nLastSlashPos= m_stResPathReference.lastIndexOf('/');
			int nLastResPathPos= m_stResPathReference.lastIndexOf("/res");
			m_stResPathReference= m_stResPathReference.substring(nLastResPathPos, nLastSlashPos+1);
			parser.parse(objURL.getFile());
		}catch(IOException ex){
			ex.printStackTrace();
		}catch(SAXException ex){
			ex.printStackTrace();
		}
    }

	public MngHandlerTMX()	{	}
	
	public void characters(char[] p_stText, int p_nStart, int p_nLength)	throws SAXException{
		if(m_isReadingData){
			String stData= (new String(p_stText)).substring(p_nStart, p_nStart+p_nLength);
			
			while(!stData.isEmpty()){
				if(stData.charAt(0) == '\n'){
					stData= stData.substring(1);
					continue;
				}
				
				if(m_nCptY >= m_nMapHeight)
					return;
				
				int		nPosNextComma= stData.indexOf(',');
				String	stSubString;
				
				if(nPosNextComma != -1)
					stSubString= stData.substring(0, nPosNextComma);
				else{
					nPosNextComma= stData.indexOf('\n');
					if(nPosNextComma != -1)
						stSubString= stData.substring(0, nPosNextComma);
					else
						stSubString= stData;
				}
				
				m_arMap[m_nCptX][m_nCptY]= Integer.parseInt(stSubString)-1;

				if((++m_nCptX) >= m_nMapWidth){
					m_nCptX= 0;
					++m_nCptY;
				}

				stData= stData.substring(nPosNextComma+1);
			}

			BackgroundTiled.g_arTileBank		= m_arTileBank;
			BackgroundTiled.g_arNbTilesInBank	= m_arNbTilesInBank;
			m_arLayers.get(m_arLayers.size()-1).setMap(m_arMap);
		}
	}
	public void setDocumentLocator(Locator locator)	{	}
	public void startDocument()	{	}
	public void endDocument()	{	}
	public void startPrefixMapping(String prefix, String uri)	{	}
	public void endPrefixMapping(String prefix)	{	}
	public void startElement(String p_stNamespaceURI, String p_stLocalName, String p_stQualifiedName, Attributes p_atts){
		m_isReadingData= false;

		if(p_stLocalName.equals("map")){

			m_nMapWidth= Integer.parseInt(p_atts.getValue("width"));
			m_nMapHeight= Integer.parseInt(p_atts.getValue("height"));
			m_nTileSize= Integer.parseInt(p_atts.getValue("tilewidth"));
			
		}else if(p_stLocalName.equals("tileset")){
			m_stCustomName= p_atts.getValue("name");
		}else if(p_stLocalName.equals("image")){
			int	width= Integer.parseInt(p_atts.getValue("width"));
			int	height= Integer.parseInt(p_atts.getValue("height"));

			String stTileSetFilename= p_atts.getValue("source");
			Background bkTileBank= new Background(m_stResPathReference+stTileSetFilename);
			bkTileBank.setTextID(m_stCustomName);
			bkTileBank.hide();
			bkTileBank.load();

			m_arTileBank.add(bkTileBank);
			m_arNbTilesInBank.add(new Integer((width / m_nTileSize) * (height / m_nTileSize)));
	
		}else if(p_stLocalName.equals("objectgroup")){
			m_nTypeObjectReading= 0;
			m_nCptZ-= 1;
		
		}else if(p_stLocalName.equals("object")){
			String stObjectName= p_atts.getValue("name");
			int nWidth= Integer.parseInt(p_atts.getValue("width"));
			int nHeight= Integer.parseInt(p_atts.getValue("height"));
			int nPosX= (Integer.parseInt(p_atts.getValue("x")) + nWidth/2) - App.g_nWidth / 2;
			int nPosY= (Integer.parseInt(p_atts.getValue("y")) + nHeight/2) - App.g_nHeight / 2;

			m_nTypeObjectReading= MODE_OBJECTS;
			m_stTypeLastCreated= p_atts.getValue("type");
			
			if(m_stTypeLastCreated.equals("Background")){
				Background objBack= new Background();
				objBack.setTextID(stObjectName);
				objBack.setPos(nPosX, nPosY, m_nCptZ);
				
				m_objLastCreated= objBack;
				m_arPhysObjToAdd.add(objBack);
			}else if(m_stTypeLastCreated.equals("Button")){
				Button objButton= new Button();
				objButton.setTextID(stObjectName);
				objButton.setPos(nPosX, nPosY, m_nCptZ);
				
				m_objLastCreated= objButton;
				m_arPhysObjToAdd.add(objButton);
			}else if(m_stTypeLastCreated.equals("Sprite")){
				Sprite objSprite= new Sprite();
				objSprite.setTextID(stObjectName);
				objSprite.setPos(nPosX, nPosY, m_nCptZ);
				
				m_objLastCreated= objSprite;
				m_arPhysObjToAdd.add(objSprite);
			}else if(m_stTypeLastCreated.equals("Label")){
				Label objLabel= new Label();
				objLabel.setTextID(stObjectName);
				objLabel.setPos(nPosX, nPosY, m_nCptZ);
				
				objLabel.m_recLimit.setBounds(-nWidth/2, -nHeight/2, nWidth, nHeight);
				
				m_objLastCreated= objLabel;
				m_arPhysObjToAdd.add(objLabel);
			}else if(m_stTypeLastCreated.equals("LabelGradual")){
			}else if(m_stTypeLastCreated.equals("LabelTextField")){
			}else if(m_stTypeLastCreated.equals("Custom")){
				m_stCustomName= stObjectName;
				m_nCustomX= nPosX;
				m_nCustomY= nPosY;
				m_nCustomZ= m_nCptZ;
				m_objLastCreated= null;
			}
	
		}else if(p_stLocalName.equals("layer")){
			m_nTypeObjectReading= MODE_LAYERS;
			m_nCptZ-= 1;
			m_nCptX= m_nCptY= 0;
			m_nMapWidth= Integer.parseInt(p_atts.getValue("width"));
			m_nMapHeight= Integer.parseInt(p_atts.getValue("height"));
			m_stCustomName= p_atts.getValue("name");
			
			m_arMap= new int[m_nMapWidth][m_nMapHeight];
	
		}else if(p_stLocalName.equals("property")){
			String stName= p_atts.getValue("name");
			String stValue= p_atts.getValue("value");
			
			if(m_nTypeObjectReading == MODE_OBJECTS){
				//Standard Properties for all Drawables
				if(stName.equals("isCameraBound"))
					m_objLastCreated.m_isCameraBound= Boolean.parseBoolean(stValue);
				else if(stName.equals("isInInfinity"))
					m_objLastCreated.m_isInInfinity= Boolean.parseBoolean(stValue);
				else if(stName.equals("nPosZ"))
					m_objLastCreated.setPosZ(Integer.parseInt(stValue));
				
				if(m_stTypeLastCreated.equals("Background")){
					Background objBack= (Background)m_objLastCreated;
					
					if(stName.equals("stImageFilename"))
						objBack.setImageFilename(stValue);
				}else if(m_stTypeLastCreated.equals("Button")){
					Button objButton= (Button)m_objLastCreated;
					
					if(stName.equals("stImageFilename"))
						objButton.setImageFilename(stValue);
					else if(stName.equals("state_1-Idle")){
						Sprite.State objState= objButton.m_vecStates.get(0);
						String stMaxFrame= stValue.substring(0, stValue.indexOf(','));
						
						stValue= stValue.substring(stValue.indexOf(',')+1);
						
						objState.setMaxFrames(Integer.parseInt(stMaxFrame));
						objState.setAnimationSpeed(Float.parseFloat(stValue));
					}else if(stName.equals("state_2-Over")){
						Sprite.State objState= objButton.m_vecStates.get(1);
						String stMaxFrame= stValue.substring(0, stValue.indexOf(','));
						
						stValue= stValue.substring(stValue.indexOf(',')+1);
						
						objState.setMaxFrames(Integer.parseInt(stMaxFrame));
						objState.setAnimationSpeed(Float.parseFloat(stValue));
					}else if(stName.equals("state_3-Clicked")){
						Sprite.State objState= objButton.m_vecStates.get(2);
						String stMaxFrame= stValue.substring(0, stValue.indexOf(','));
						
						stValue= stValue.substring(stValue.indexOf(',')+1);
						
						objState.setMaxFrames(Integer.parseInt(stMaxFrame));
						objState.setAnimationSpeed(Float.parseFloat(stValue));
					}else if(stName.equals("state_4-Checked")){
						Sprite.State objState= objButton.m_vecStates.get(3);
						String stMaxFrame= stValue.substring(0, stValue.indexOf(','));
						
						stValue= stValue.substring(stValue.indexOf(',')+1);
						
						objState.setMaxFrames(Integer.parseInt(stMaxFrame));
						objState.setAnimationSpeed(Float.parseFloat(stValue));
					}
				}else if(m_stTypeLastCreated.equals("Sprite")){
					Sprite objSprite= (Sprite)m_objLastCreated;
					String stPartName= stName.substring(0, 6);
					
					if(stName.equals("stImageFilename")){
						objSprite.setImageFilename(stValue);
					}else if(stName.equals("nDefaultState")){
						objSprite.m_nChangeToState= Integer.parseInt(stValue);
					}else if(stName.equals("nDefaultFrame")){
						objSprite.m_nChangeToFrame= Integer.parseInt(stValue);
					}else if(stPartName.equals("state_")){
						String stStateName= stName.substring(6);
						String stMaxFrame= stValue.substring(0, stValue.indexOf(','));
						
						stValue= stValue.substring(stValue.indexOf(',')+1);
						
						objSprite.addState(stStateName, Integer.parseInt(stMaxFrame), Float.parseFloat(stValue));
					}
				}else if(m_stTypeLastCreated.equals("Label")){
					Label objLabel= (Label)m_objLastCreated;
					
					if(stName.equals("clrText")){
						int nPosComma= stValue.indexOf(',');
						String stColorComponent= stValue.substring(0, nPosComma);
						int nValueComponent= Integer.parseInt(stColorComponent);
						objLabel.setFilterRed(nValueComponent);
						
						stValue= stValue.substring(nPosComma+1);
						nPosComma= stValue.indexOf(',');
						stColorComponent= stValue.substring(0, nPosComma);
						nValueComponent= Integer.parseInt(stColorComponent);
						objLabel.setFilterGreen(nValueComponent);

						stValue= stValue.substring(nPosComma+1);
						nPosComma= stValue.indexOf(',');
						stColorComponent= stValue.substring(0, nPosComma);
						nValueComponent= Integer.parseInt(stColorComponent);
						objLabel.setFilterBlue(nValueComponent);
						
						stValue= stValue.substring(nPosComma+1);
						nValueComponent= Integer.parseInt(stValue);
						objLabel.setFilterAlpha(nValueComponent);
					}else if(stName.equals("isMultiline")){
						objLabel.m_isMultiline= Boolean.parseBoolean(stValue);
					}else if(stName.equals("nFontSize")){
						objLabel.m_nFontSize= Integer.parseInt(stValue);
					}else if(stName.equals("stMode")){
						if(stValue.equalsIgnoreCase("left"))		objLabel.m_nMode= Label.MODE_LEFT;
						else if(stValue.equalsIgnoreCase("center"))	objLabel.m_nMode= Label.MODE_CENTER;
						else if(stValue.equalsIgnoreCase("right"))	objLabel.m_nMode= Label.MODE_RIGHT;
					}else if(stName.equals("stFontFilename")){
						objLabel.m_stFontName= stValue;
					}else if(stName.equals("stText")){
						objLabel.set(stValue);
					}
				}else if(m_stTypeLastCreated.equals("LabelGradual")){
				}else if(m_stTypeLastCreated.equals("LabelTextField")){
				}else if(m_stTypeLastCreated.equals("Custom")){
					Sprite objSprite= (Sprite)m_objLastCreated;
					String stPartName= stName.substring(0, 6);
					
					if(stName.equals("_stClassName")){
						try{
							Class c= Class.forName(stValue);
							Constructor ct= c.getConstructor();
							m_objLastCreated= (Drawable)ct.newInstance();
						}catch(Throwable e){
							System.err.println(e);
						}
						
						objSprite= (Sprite)m_objLastCreated;
						objSprite.setTextID(m_stCustomName);
						objSprite.setPos(m_nCustomX, m_nCustomY, m_nCustomZ);
						m_arPhysObjToAdd.add(objSprite);
					}else if(stName.equals("stImageFilename")){
						objSprite.setImageFilename(stValue);
					}else if(stName.equals("nDefaultState")){
						objSprite.m_nChangeToState= Integer.parseInt(stValue);
					}else if(stName.equals("nDefaultFrame")){
						objSprite.m_nChangeToFrame= Integer.parseInt(stValue);
					}else if(stPartName.equals("state_")){
						String stStateName= stName.substring(6);
						String stMaxFrame= stValue.substring(0, stValue.indexOf(','));
						
						stValue= stValue.substring(stValue.indexOf(',')+1);
						
						objSprite.addState(stStateName, Integer.parseInt(stMaxFrame), Float.parseFloat(stValue));
					}
				}
			}else{
				if(stName.equals("nPosZ")){
					m_nCptZ= Integer.parseInt(stValue);
				}
			}

		}else if(p_stLocalName.equals("data")){
			BackgroundTiled newLayer= new BackgroundTiled();
			newLayer.setTextID(m_stCustomName);
			newLayer.setPos(m_nCptX, m_nCptY, m_nCptZ);
			newLayer.m_nTileSize= m_nTileSize;
			
			m_arLayers.add(newLayer);
			m_arPhysObjToAdd.add(newLayer);
			
			m_isReadingData= true;
		}
	}
	public void endElement(String namespaceURI, String localName, String qualifiedName)	{	}
	public void ignorableWhitespace(char[] text, int start, int length) throws SAXException	{	}
	public void processingInstruction(String target, String data)	{	}
	public void skippedEntity(String name)	{	}
}
