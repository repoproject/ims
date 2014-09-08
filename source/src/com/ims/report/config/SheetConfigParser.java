/**
 * 
 */
package com.ims.report.config;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;



/**
 * 单利模式构建配置文件的读取
 * @author ChengNing
 * @date   2014-9-6
 */
public class SheetConfigParser {
	private static Logger logger = Logger.getLogger(SheetConfigParser.class);
	private Document cfg;
	private Element root;
	
	private static SheetConfigParser instance = new SheetConfigParser();
	
	/**
	 * 私有构造函数
	 */
	private SheetConfigParser(){
		loadConfig();
	}

	/**
	 * 获取本类的实例
	 * @return
	 */
	public static SheetConfigParser getInstance(){
		instance.loadConfig();
		instance.parseXml();
		return instance;
	}
	
	/**
	 * 加载配置文件
	 * @return
	 */
	private void loadConfig(){
		String configPath = ExcelConfig.getPath();
		File configFile = new File(configPath);
		if(!configFile.exists()){
			logger.error("没有找到配置文件，路径:" + configPath);
			return;
		}
		SAXReader reader = new SAXReader();
		try {
			this.cfg = reader.read(configFile);
		} catch (DocumentException e) {
			logger.error("配置文件解析失败");
			e.printStackTrace();
		}
	}
	
	/**
	 * 解析xml配置文件
	 */
	private void parseXml(){
		if(this.cfg == null)
			return;
		this.root = this.cfg.getRootElement();
		
	}
	
	/**
	 * 得到指定的sheet节点
	 * @param sheetName
	 * @return
	 */
	public Sheet getSheet(String sheetName){
		Element sheetEle = getSheetElement(sheetName);
		return marshal(sheetEle);
	}
	
	/**
	 * 得到所有配置的节点
	 * @return
	 */
	public List<Sheet> getSheetList(){
		List<Sheet> sheetList = new LinkedList<Sheet>();
		for(Iterator i=root.elementIterator();i.hasNext();){
			Element ele = (Element)i.next();
			Sheet sheetObj = new Sheet();
			sheetObj = marshal(ele);
			sheetList.add(sheetObj);
		}
		return sheetList;
	}
	
	/**
	 * 得到指定的sheet节点
	 * @param name
	 * @return
	 */
	private Element getSheetElement(String sheetName){
		Element sheetEle = null;
		for(Iterator i=root.elementIterator();i.hasNext();){
			Element ele = (Element)i.next();
			if(ele.attributeValue("name").equals(sheetName)){
				sheetEle = ele;
				break;
			}
		}
		return sheetEle;
	}
	
	/**
	 * 对象化sheet节点
	 * @param ele
	 * @return
	 */
	private Sheet marshal(Element ele){
		Sheet obj = new Sheet();
		obj.setName(ele.attributeValue("name"));
		obj.setClassName(ele.attributeValue("class"));
		obj.setSql(ele.elementText("sql"));
		String dataRowNum = ele.attributeValue("dataRowNum");
		String dataStartNum = ele.attributeValue("dataStartNum");
		String footerRowNum = ele.attributeValue("footerRowNum");
		obj.setDataRowNum(Integer.valueOf(dataRowNum));
		obj.setDataStartNum(Integer.valueOf(dataStartNum));
		obj.setFooterRowNum(Integer.valueOf(footerRowNum));
		List<Column> colsObj = new ArrayList<Column>();
		List<Element> colsEle = ele.elements("col");
		Column col = null;
		for(int i=0;i<colsEle.size();i++){
			col = marshalColumn(colsEle.get(i));
			colsObj.add(col);
		}
		obj.setCols(colsObj);
		return obj;
	}
	
	/**
	 * 对象化col节点
	 * @param colEle
	 * @return
	 */
	private Column marshalColumn(Element colEle){
		String index=colEle.attributeValue("index");
		String type=colEle.attributeValue("type");
		String value = colEle.getText();
		Column obj = new Column(Integer.valueOf(index), type, value);
		return obj;
	}
}
