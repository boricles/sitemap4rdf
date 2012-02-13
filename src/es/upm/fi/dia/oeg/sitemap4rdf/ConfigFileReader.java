/*
 * @(#) ConfigFileReader.java 0.1 2010/08/05	
 * 					  
 * Copyright (C) 2010 cygri,boricles	
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.

 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 */

package es.upm.fi.dia.oeg.sitemap4rdf;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Attr;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import es.upm.fi.dia.oeg.sitemap4rdf.Constants;

/**
 *
 * ConfigFileReader Allows to read the configuration file and set up the parameters
 * @version 0.2 05 Aug 2010
 * @author boricles
 *
 */
public class ConfigFileReader implements Constants {
	
	/** fileName variable for the configuration file  */
	protected String fileName;
	
	/** generator variable for the object generator */
	protected Generator generator;
	
	
    private static final Logger logger = LoggerFactory.getLogger(ConfigFileReader.class);
	
	/**
	 * Default constructor 
	 */	
	public ConfigFileReader() {
	}

	/**
	 * Loads the specific parameter and set up the parameter value into the generator object
	 * @param name a string for the name
	 * @param value a string for the value
	 */	
	private void loadParam(String name, String value) {
		if (name.equals(lastmodTag)) 
			generator.setLastMod(value);
		if (name.equals(changefreqTag))
			generator.setChangeFreq(value);
		if (name.equals(siterootParam))
			generator.setSiteRoot(value);
		if (name.equals(outputdirParam))
			generator.setOutputDir(value);
		if (name.equals(excludeParam))
			generator.setExclude(value);
		if (name.equals(datadumpParam))
			generator.setDatasetDumpLocation(value);
		if (name.equals(gzipParam))
			generator.setGzip(value);
	}

	/**
	 * Reads the content of the configuration file, i.e. the set of parameters and sets up into the generator object 
	 * @param root Root element of the configuration file 
	 * @exception java.lang.Exception if some mandatory param is missing (e.g. endpoint and uripattern)
	 */	
	private void readConfigDescription(Element root) throws Exception {
		Attr attEndPoint = root.getAttributeNode(endPointAtt);
		String endPoint;
		if (attEndPoint!=null) 
			endPoint = attEndPoint.getValue();
		else
			throw new java.lang.Exception("Incomplete information on the " + fileName + " file");
		
		Attr attUriPattern = root.getAttributeNode(uriPatternAtt);
		String uriPattern;
		if (attUriPattern!=null) 
			uriPattern = attUriPattern.getValue();
		else
			throw new java.lang.Exception("Incomplete information on the " + fileName + " file");
		
		generator.setEndPoint(endPoint);
		generator.setUriPattern(uriPattern);
		
		NodeList paramNodes = root.getElementsByTagName(paramElement);
		
		if (paramNodes==null)		//return if there is not any parameter
			return;
		Node sNode;
		
		int len = paramNodes.getLength();
		for (int i=0; i<len; i++) {
			sNode = paramNodes.item(i);
			String name = getAttribute(sNode, nameAttr);
			String value = getAttribute(sNode, valueAttr);
			loadParam(name,value);
		}
	}
	
	/**
	 * Reads the content of the configuration file, i.e. the set of parameters and sets up into the generator object 
	 */	
	public void read() {
		try {
			File configFileDesc = new File(fileName); 
			FileInputStream configFIS = new FileInputStream(configFileDesc);
			if (configFIS == null || configFIS.available() <= 0)
				throw new FileNotFoundException("Empty " + fileName + "File");
				
			Document doc = null;
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			factory.setValidating(false);
			DocumentBuilder docBuilder = factory.newDocumentBuilder();
			doc = docBuilder.parse(configFIS);
			configFIS.close();
			
			Element root = doc.getDocumentElement();
			readConfigDescription(root);
			
		} catch (FileNotFoundException e) {
			logger.debug("FileNotFoundException ",e);
			System.err.println(e.getMessage());
			System.exit(3);
		} catch (IOException ex) {
			logger.debug("IOException ",ex);
			System.err.println(ex.getMessage());
			System.exit(3);
		} catch (ParserConfigurationException e) {
			logger.debug("ParserConfigurationException ",e);
			System.err.println(e.getMessage());
			System.exit(3);
		} catch (SAXException e) {
			logger.debug("SAXException ",e);
			System.err.println(e.getMessage());
			System.exit(3);
		} catch (Exception exe) {
			logger.debug("Exception ",exe);
			System.err.println(exe.getMessage());
			System.exit(3);
		}
	}

	/**
	 * Gets file name of the configuration file 
	 * @return filename of the configuration file 
	 */	
	public String getFileName() {
		return fileName;
	}

	/**
	 * Sets the name of the configuration file 
	 * @param fileName of the configuration file 
	 */	
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	/**
	 * Gets the generator object 
	 * @return generator object  
	 */	
	public Generator getGenerator() {
		return generator;
	}
	
	/**
	 * Sets the generator object, for setting the parameters of the configuration file 
	 * @param generator object  
	 */	
	public void setGenerator(Generator generator) {
		this.generator = generator;
	}

	/**
	 * Return the attribute value from a given node
	 * @param node the given node
	 * @param attributeName the name of the attribute to get
	 * @return the attribute vale from the given node, null if the attributeName does not exist 
	 */	
	private String getAttribute(Node node, String attributeName) {
		NamedNodeMap map = node.getAttributes();
		if (map != null) {
			Node n = map.getNamedItem(attributeName);
			if (n != null) {
				return n.getNodeValue();
			}
		}
		return null;		
	}


}
