package com.fsl.mcu.kinetislist;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.content.res.XmlResourceParser;
import android.util.Log;

public class ParseXmlResource {
	private static final String TAG = "ParseXmlResource";

	private static final String	FEATURES = "FEATURES";
	private static final String	HEADER = "HEADER";
	private static final String	FAMILY = "FAMILY";
	private static final String	DEVICE = "DEVICE";
	private static final String	ITEM = "ITEM";

	private static final String	HEADER_NAME = DBdefine.Header.COLUMN_NAME_NAME;
	private static final String	HEADER_TITLE = DBdefine.Header.COLUMN_NAME_TITLE;
	private static final String	HEADER_TYPE = DBdefine.Header.COLUMN_NAME_TYPE;
	private static final String	HEADER_WIDTH = DBdefine.Header.COLUMN_NAME_WIDTH;
	private static final String	HEADER_SHOW = DBdefine.Header.COLUMN_NAME_SHOW;
	
	private static final String	ATTR_Name = "Name";
	private static final String	ATTR_Data = "Data";

	private boolean decode_header = false;
	private boolean decode_device = false;
	private int itemIndex=0;
	
	private int NumColumns = 0;
	private final HashMap<String, Integer> HeaderMap = new HashMap<String, Integer>();
	
	private String[] xDeviceRow = null;
	private String familyName;
	
	private static XmlResourceParser[] xmlParsers;
	public ParseXmlResource(XmlResourceParser... xmlParsers) {
		ParseXmlResource.xmlParsers = xmlParsers;
		
	}

	public void getData(ArrayList<DBdefine.xHeaderItem> xmlHeader, ArrayList<String[]> xmlDevice)
				throws XmlPullParserException, IOException
	{
	for (XmlResourceParser xmlParser: xmlParsers){
		familyName = new String();
        String SourceName = new String();
		int eventType = xmlParser.getEventType();
		String thisTag;
		while (eventType != XmlPullParser.END_DOCUMENT) {
			switch (eventType) {
			case XmlPullParser.START_DOCUMENT:
				break;
			case XmlPullParser.START_TAG:
				thisTag = xmlParser.getName();
				if (thisTag.equals(ITEM)) {
					if (decode_device) {
						String itemName = xmlParser.getAttributeValue(null, ATTR_Name);
						Integer column = HeaderMap.get(itemName);
						if (column != null)
							xDeviceRow[column.intValue()] = xmlParser.getAttributeValue(null, ATTR_Data);
					}
					else if (decode_header) {
						DBdefine.xHeaderItem xHeader = new DBdefine.xHeaderItem();

						xHeader.Name = xmlParser.getAttributeValue(null, HEADER_NAME);
						xHeader.Title = xmlParser.getAttributeValue(null, ATTR_Data);	// COLUMN_NAME_TITLE
						xHeader.Type = xmlParser.getAttributeValue(null, HEADER_TYPE);
						String str = xmlParser.getAttributeValue(null, HEADER_WIDTH);
						try {
							xHeader.Width = Integer.valueOf(str.trim());
						} catch (NumberFormatException eNumber) {
							xHeader.Width = Integer.valueOf(160);	// default value
						}
                        String visible = xmlParser.getAttributeValue(null, HEADER_SHOW);
                        if (visible == null) {
                            xHeader.Visible = true;
                        }else {
                            xHeader.Visible = Boolean.valueOf(visible);
                        }
						if(!HeaderMap.containsKey(xHeader.Name)){
							HeaderMap.put(xHeader.Name, itemIndex);	// for creating the device row
							xmlHeader.add(xHeader);
						}
					}
					itemIndex++;
					break;
				}
				if (thisTag.equals(DEVICE)) {
					decode_device = true;
					itemIndex = 0;
			// Ignore the device name	
			//		xmlParser.getAttributeValue(null, ATTR_Name);
					xDeviceRow = new String[NumColumns+1];
					xDeviceRow[NumColumns] = SourceName;
					Integer column = HeaderMap.get("SubFamily");
					if (column != null)
						xDeviceRow[column] = familyName;
					break;
				}
				if (thisTag.equals(FAMILY)) {
					familyName = xmlParser.getAttributeValue(null, ATTR_Name);
					break;
				}
				if (thisTag.equals(HEADER)) {
					decode_header = true;
					itemIndex = 0;
					break;
				}
				if (thisTag.equals(FEATURES)) {
                    SourceName = xmlParser.getAttributeValue(null, ATTR_Name);
                    break;
                }
				break;
			case XmlPullParser.END_TAG:
				thisTag = xmlParser.getName();
				if (thisTag.equals(HEADER)) {
					decode_header = false;
					NumColumns = itemIndex;
					break;
				}
				if (thisTag.equals(DEVICE)) {
					decode_device = false;
					xmlDevice.add(xDeviceRow);
					break;
				}
				if (thisTag.equals(FAMILY)) {
					familyName = null;
				}	
				break;
			case XmlPullParser.TEXT:
				System.out.println("Text "+ xmlParser.getText());
				break;
			}
			
			eventType = xmlParser.next();
		}
    }
	}
}
