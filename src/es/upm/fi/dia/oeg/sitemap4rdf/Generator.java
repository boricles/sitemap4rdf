/*
 * @(#) Generator.java	0.1	2010/08/05
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

import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.zip.GZIPOutputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.PosixParser;
import org.apache.xml.serialize.OutputFormat;
import org.apache.xml.serialize.XMLSerializer;

import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.RDFNode;


/**
 * Generator Allows to generate a sitemap.xml file and sitemap_index.xml depending of the number of URLs
 * @version 0.2 05 Aug 2010
 * @author boricles
 */
public class Generator implements Constants {

	/** uriPattern variable for the uri pattern */
	protected String uriPattern;
	
	/** endPoint variable for the endPoint  */
	protected String endPoint;
	
	/** lastMod variable for the lastMod tag value  */
	protected String lastMod;

	/** changeFreq variable for the changeFre tag value  */	
	protected String changeFreq;

	/** remoteLocation variable for the remote location of the sitemap.xml file  */	
	protected String siteroot;
	
	/** outputDir variable for the output directory of the files */	
	protected String outputDir;
	
	/** exclude variable for specifying a regular expression, and anything not matching that expression will not be included */
	protected String exclude;
	
	/** variable to set if generated files are zipped or not. */
	protected String gzip;
	
	/** variable to check if we already generated the semantic Section */
	protected boolean semanticSectionGenerated = false;
	
	/** datasetLinkedDataPrefix variable for the linkedDataPrefix of the semantic map section */	
	protected String datasetLinkedDataPrefix;
	
	/** datasetDumpLocation variable for the dataDumpLocation of the semantic map section */	
	protected List<String> datasetDumpLocation;

	/** listFile variable for the dataDumpLocation of the semantic map section */	
	protected Map<String,Document> files;
	
	/** confFile variable for reading the configuration parameters from the configuration file */
	protected ConfigFileReader confFile;
		
    private static final Logger logger = LoggerFactory.getLogger(Generator.class);
    
    private static Options options;


	
	/**
	 * Default constructor 
	 */	
	public Generator() {
		
	}
	
	/**
	 * Returns the uripattern
	 * @return a string with the uripattern 
	 */	
	public String getUriPattern() {
		return uriPattern;
	}

	/**
	 * Sets the uripattern
	 * @param uriPattern a string with the uripattern
	 */	
	public void setUriPattern(String uriPattern) {
		this.uriPattern = uriPattern;
	}

	/**
	 * Returns the endPoint
	 * @return a string with the endPoint 
	 */	
	public String getEndPoint() {
		return endPoint;
	}

	/**
	 * Sets the endPoint
	 * @param endPoint a string with the endPoint
	 */	
	public void setEndPoint(String endPoint) {
		this.endPoint = endPoint;
	}

	/**
	 * Generates the URLsection of the sitemap file
	 * @param loc string for the locTag value
	 * @param doc the XML Document
	 * @param elem the Element for the url element
	 */	
	private void generateURLSection(String loc, Document doc, Element elem ) {
		try {
			Element urlElement = doc.createElement(urlTag);
			Element locElement = doc.createElement(locTag);
			locElement.setTextContent(loc);
			urlElement.appendChild(locElement);
			
			if (lastMod!= null && !lastMod.isEmpty()) {
				Element lastModElement = doc.createElement(lastmodTag);
				lastModElement.setTextContent(lastMod);
				urlElement.appendChild(lastModElement);
			}
			
			if (changeFreq!=null && !changeFreq.isEmpty()) {
				Element changeFreqElement = doc.createElement(changefreqTag);
				changeFreqElement.setTextContent(changeFreq);
				urlElement.appendChild(changeFreqElement);				
			}

			elem.appendChild(urlElement);
			
		} catch (Exception e) {
			logger.debug("Exception ",e);
			System.err.println(e.getMessage());
			System.exit(3);
		}
	}
	
	/**
	 * Generates the SitemapsSection of the sitemap index file
	 * @param doc the XML Document
	 * @param elem the Element for the sitemapindex element
	 */	
	protected void generateSitemapsSection(Element root, Document doc) {
		try {
			DateFormat dateFormat = new SimpleDateFormat(DEFAULT_DATEFORMAT);
			
			Iterator<String> iterator = (Iterator<String>)files.keySet().iterator();// Iterate on keys
			while ( iterator.hasNext() ){
				String fileName = ( String ) iterator.next();
				if (gzip!=null && gzip.equals(TRUE))
					fileName = fileName + zipFileExtension;
				
				Element sitemap = doc.createElement(siteMapTag);
				Element loc = doc.createElement(locTag);
				Element lastMod = doc.createElement(lastmodTag);
				
				loc.setTextContent(siteroot + fileName);
				Date date = new Date();
				
				lastMod.setTextContent(dateFormat.format(date));
				
				sitemap.appendChild(loc);
				sitemap.appendChild(lastMod);
				
				root.appendChild(sitemap);
	
			}
		} catch (DOMException e) {
			logger.debug("DOMException ",e);
			System.err.println(e.getMessage());
			System.exit(3);

		}
	}

	/**
	 * Generates the sitemap_index.xml file
-	 */	
	protected void generateSiteMapIndex() {
		if (siteroot==null || siteroot.isEmpty()) {
			//Logger.getLogger(Generator.class.getName()).log(Level.SEVERE, null, new java.lang.Exception());
			System.err.print("You should provide the siteroot parameter needed because a sitemapindex is being created");
			System.exit(3);
		}
		
		try {
	        DocumentBuilderFactory dbfac = DocumentBuilderFactory.newInstance();
	        DocumentBuilder docBuilder = dbfac.newDocumentBuilder();
	        Document doc = docBuilder.newDocument();
	        String indexFileName = indexOutputFile;
	        if (outputDir!=null && !outputDir.isEmpty())
	        	indexFileName = outputDir + indexOutputFile;
	        
	        Element root = doc.createElement(siteMapIndexTag);
	        root.setAttribute(XMLNS, namespace);
	        doc.appendChild(root);
			
	        generateSitemapsSection(root,doc);
	        
			OutputFormat format = new OutputFormat(defaultDocumentType,DEFAULT_ENCODING,true);
	        format.setIndenting(true);
	        format.setIndent(4);
	        Writer output = new BufferedWriter( new FileWriter(indexFileName) );
	        XMLSerializer serializer = new XMLSerializer(output, format);
	        serializer.serialize(doc);
	        

		} catch (ParserConfigurationException e) {
			logger.debug("ParserConfigurationException ",e);
			System.err.println(e.getMessage());
			System.exit(3);
		} catch (IOException e) {
			logger.debug("IOException ",e);
			System.err.println(e.getMessage());
			System.exit(3);
		}
	}

	/**
	 * Generates the loc section from the endPoint
	 * @param doc the XML Document
	 * @param elem the Element for the loc element
	 * @param resultSet an existing ResultSet if we did cross the limit of URLs per sitemap.xml file
	 * @return resultSet in the case we did cross the limit and we have to generate another sitemap.xml, null if we finished below the limit
	 */	
	protected ResultSet generateFromEndPoint(Document doc, Element elem, ResultSet resultSet) {
		try {
			ResultSet result = null;
			if (resultSet==null) {
				String query = sparqlQuery.replace(URIPATTERN, uriPattern);
				if (exclude!=null && !exclude.isEmpty()) {
					query = query.replace(EXCLUDE_FILTER, EXCLUDE_FILTER_PRESENT);
					query = query.replace(EXCLUDE_FILTER_PATTERN, "\"" + exclude + "\"");
				}
				else
					query = query.replace(EXCLUDE_FILTER, EMPTY_STRING);
					
				SPARQLEndPointClient s = new SPARQLEndPointClient();
				result = s.execQueryEndPoint(endPoint, query);
			}
			else
				result = resultSet;
			QuerySolution soln;
			RDFNode x;
			int counter = 0;
			while (result.hasNext()) {
				soln = result.nextSolution();
				x = soln.get(queryVariable);
				generateURLSection(x.toString(),doc,elem);
				if (++counter >= MAX_URLS)
					return result;
	        } 
		} catch (Exception ex) {
			logger.debug("Exception ",ex);
			System.err.println(ex.getMessage());
			System.exit(3);
	    }
		return null;
	}
	
	/**
	 * Generates the semantic sitemap section 
	 * @param doc the XML Document
	 * @param elem the Element for the semantic sitemap
	 */	
	protected void generateSemanticSection (Document doc, Element root) {
        if (!semanticSectionGenerated) {
        	Element dataSetElement = doc.createElement(PREFIX_SEMANTIC_SITEMAP+":"+datasetTag);
        	Element linkedDataPrefixElement = doc.createElement(PREFIX_SEMANTIC_SITEMAP+":"+linkedDataPrefixTag);
        	Element sparqlEndPointLocationElement = doc.createElement(PREFIX_SEMANTIC_SITEMAP+":"+sparqlEndPointLocation);

        	linkedDataPrefixElement.setTextContent(uriPattern);
        	sparqlEndPointLocationElement.setTextContent(endPoint);
        	dataSetElement.appendChild(linkedDataPrefixElement);
        	dataSetElement.appendChild(sparqlEndPointLocationElement);
        	
        	if (datasetDumpLocation!=null && !datasetDumpLocation.isEmpty()) {
        		for (String dmp : datasetDumpLocation) {
        			Element dataDumpLocationElement = doc.createElement(PREFIX_SEMANTIC_SITEMAP+":"+dataDumpLocationTag);
        			dataDumpLocationElement.setTextContent(dmp);
        			dataSetElement.appendChild(dataDumpLocationElement);
        		}
        	}
        	
        	root.appendChild(dataSetElement);
        	
        	semanticSectionGenerated = true; //we want to generate the semantic section only once
        }
	}
	
	/**
	 * Generates the sitemap.xml file
	 * @param result an existing ResultSet if we did cross the limit of URLs per sitemap.xml file
	 * @param index of the current generation of the sitemap file, useful when we are creating sitemap_index.xml files 
	 */	
	protected void generateSiteMap(ResultSet result, int index) {
		try {
            DocumentBuilderFactory dbfac = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = dbfac.newDocumentBuilder();
            Document doc = docBuilder.newDocument();
            
            Element root = doc.createElement(urlSetTag);
            root.setAttribute(XMLNS, namespace);
            
            if (!semanticSectionGenerated) {
            	root.setAttribute(XMLNS+":"+PREFIX_SEMANTIC_SITEMAP, NS_SEMANTIC_SITEMAP);
            }
            
            doc.appendChild(root);
            generateSemanticSection(doc,root);
           
            ResultSet rs = generateFromEndPoint(doc,root,result);
            doc.setXmlStandalone(true);

	        String outputFileName = (index==0) ? ( outputFile + extensionOutputFile) : (outputFile + index + extensionOutputFile);
	        //String outputFileNameDirIncluded = outputFileName;
	        //if (outputDir!=null && !outputDir.isEmpty())
	        	//outputFileNameDirIncluded = outputDir+outputFileName;
	        
	        if (files==null)
	        	files = new HashMap<String,Document>();
	        
	        files.put(outputFileName, doc);
	        
	        if (rs!=null)
	        	generateSiteMap(rs,++index);

		} catch (ParserConfigurationException e) {
			logger.debug("ParserConfigurationException ",e);
			System.err.println(e.getMessage());
			System.exit(3);
		}
	}

	/**
	 * Save as zip file the given XML Document using a given file name 
	 * @param fileName the name of the generated zip file
	 * @param doc the XML document to store in the zip file 
	 */	
	protected void saveZipFile(String fileName, Document doc) {
		String outfileName = fileName + zipFileExtension;
		if (outputDir!=null && !outputDir.isEmpty())
			outfileName = outputDir+outfileName;
        		
		try {
	        
	        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
	        Source xmlSource = new DOMSource(doc);
	        
	        Result outputTarget = new StreamResult(new OutputStreamWriter(outputStream, DEFAULT_ENCODING));
	        TransformerFactory tf = TransformerFactory.newInstance();
	        tf.setAttribute(xmlAttributeIdentNumber, new Integer(4));
	        
	        Transformer transformer = tf.newTransformer();
	        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
	        transformer.setOutputProperty(OutputKeys.ENCODING,DEFAULT_ENCODING );
	        transformer.setOutputProperty("{http://xml. customer .org/xslt}indent-amount", "4");
	        transformer.transform(xmlSource, outputTarget);
	        
	        InputStream in = new ByteArrayInputStream(outputStream.toString(DEFAULT_ENCODING).getBytes());
			
			byte[] buf = new byte[1024];
			//create the zip file
			GZIPOutputStream out = new GZIPOutputStream (new FileOutputStream(outfileName));

			//Transfer bytes from the inputstream to the ZIP
			int len;
			while ((len=in.read(buf))>0) {
				out.write(buf,0,len);
			}
			
			//Complete the entry
			in.close();
			
			//complete the zip file
			out.finish();
			out.close();
		} catch (FileNotFoundException e) {
			logger.debug("FileNotFoundException ",e);
			System.err.println(e.getMessage());
			System.exit(3);
		} catch (IOException e) {
			logger.debug("IOException ",e);
			System.err.println(e.getMessage());
			System.exit(3);
		} catch (TransformerConfigurationException e) {
			logger.debug("TransformerConfigurationException ",e);
			System.err.println(e.getMessage());
			System.exit(3);
		} catch (TransformerException e) {
			logger.debug("TransformerException ",e);
			System.err.println(e.getMessage());
			System.exit(3);
		} catch (TransformerFactoryConfigurationError e) {
			logger.debug("TransformerFactoryConfigurationError ",e);
			System.err.println(e.getMessage());
			System.exit(3);
		}
	}

	/**
	 * Save as a XML file the given XML Document 
	 * @param fileName the name of the generated file
	 * @param doc the XML document to store in the file 
	 */	
	protected void saveXMLFile(String fileName, Document doc) {
		try {
			if (outputDir!=null && !outputDir.isEmpty())
				fileName = outputDir+fileName;

			OutputFormat format = new OutputFormat(defaultDocumentType,DEFAULT_ENCODING,true);
	        format.setIndenting(true);
	        format.setIndent(2);
	        Writer output = new BufferedWriter( new FileWriter(fileName) );
	        XMLSerializer serializer = new XMLSerializer(output, format);
	        serializer.serialize(doc);

		} catch (IOException e) {
			logger.debug("IOException ",e);
			System.err.println(e.getMessage());
			System.exit(3);
		}
	}
	
	/**
	 * Save to a file the given XML Document. The file can be saved as an XML or gzip file 
	 * @param fileName the name of the generated file
	 * @param doc the XML document to store in the file 
	 */	
	protected void saveFile(String fileName, Document doc) {
		if (gzip!=null) {
			if (gzip.equals(TRUE)) {
				saveZipFile(fileName,doc);
				return;
			}
		}
		saveXMLFile(fileName, doc);
	}

	/**
	 * Save the files stored in the files collection  
	 */	
	protected void saveFiles() {
		Iterator<String> iterator = (Iterator<String>)files.keySet().iterator();// Iterate on keys
		while ( iterator.hasNext() ){
			String fileName = ( String ) iterator.next();
			Document doc = ( Document ) files.get( fileName );
			saveFile(fileName,doc);
		}
	}
	
	/**
	 * Generates the sitemap.xml file(s). If there is more than one sitemap.xml, it generates the sitemap_index.xml.
	 * 
	 * */	
	public void generate() {
			generateSiteMap(null,0);
			if (files.size()>1)
				generateSiteMapIndex();
			saveFiles();
	}

	/**
	 * Returns the lastMod tag value
	 * @return a string with the lastMod tag value 
	 */	
	public String getLastMod() {
		return lastMod;
	}

	/**
	 * Sets the lasMod tag value
	 * @param lastMod a string with the last mod tag value
	 */	
	public void setLastMod(String lastMod) {
		this.lastMod = lastMod;
	}

	/**
	 * Returns the changeFreq tag value
	 * @return a string with the changeFreq 
	 */	
	public String getChangeFreq() {
		return changeFreq;
	}

	/**
	 * Sets the changeFreq tag value
	 * @param changeFreq a string with the changeFreq tag value
	 */	
	public void setChangeFreq(String changeFreq) {
		this.changeFreq = changeFreq;
	}

	/**
	 * Returns the siteroot parameter value
	 * @return a string with the siteroot parameter value 
	 */	
	public String getSiteRoot() {
		return siteroot;
	}

	/**
	 * Sets the siteroot parameter value
	 * @param remoteLocation a string with the sitemapBase parameter value
	 */	
	public void setSiteRoot(String remoteLocation) {
		if (!remoteLocation.endsWith("/"))	//minor verification
			remoteLocation = remoteLocation + "/";
		this.siteroot = remoteLocation;
	}

	/**
	 * Returns the outputdir parameter value
	 * @return a string with the outputdir parameter value 
	 */	
	public String getOutputDir() {
		return outputDir;
	}

	/**
	 * Sets the outputDir 
	 * @param outputDir a string with the output directory
	 */	
	public void setOutputDir(String outputDir) {
		String separator = System.getProperty("file.separator");
		if (!outputDir.endsWith(separator))
			outputDir = outputDir + separator;
		if (System.getProperty("file.separator").equals(WINDOWS_FILE_SEPARATOR)) {
			outputDir = outputDir.replace("\\", "\\\\");
		}
		this.outputDir = outputDir;
	}

	/**
	 * Returns the exclude pattern parameter value
	 * @return a string with the exclude pattern parameter value 
	 */	
	public String getExclude() {
		return exclude;
	}

	/**
	 * Sets the exclude tag value
	 * @param exclude a string with the exclude tag value
	 */	
	public void setExclude(String exclude) {
		if (exclude.charAt(0)=='\"' && exclude.charAt(exclude.length()-1)=='\"') {
			exclude = exclude.substring(1);
			exclude = exclude.substring(exclude.lastIndexOf("\""));
		}
		this.exclude = exclude;
	}

	/**
	 * Returns the datasetLinkedDataPrefix parameter value
	 * @return a string with the datasetLinkedDataPrefix parameter value 
	 */	
	public String getDatasetLinkedDataPrefix() {
		return datasetLinkedDataPrefix;
	}

	/**
	 * Sets the datasetLinkedDataPrefix tag value
	 * @param datasetLinkedDataPrefix a string with the datasetLinkedDataPrefix tag value
	 */	
	public void setDatasetLinkedDataPrefix(String datasetLinkedDataPrefix) {
		this.datasetLinkedDataPrefix = datasetLinkedDataPrefix;
	}

	/**
	 * Returns the datasetDumpLocation parameter value
	 * @return a string with the datasetDumpLocation parameter value 
	 */	
	public List<String> getDatasetDumpLocation() {
		return datasetDumpLocation;
	}

	/**
	 * Sets the pridatasetDumpLocationority tag value
	 * @param datasetDumpLocation a string with the datasetDumpLocation tag value
	 */	
	public void setDatasetDumpLocation(String datasetDumpLocation) {
		if (this.datasetDumpLocation==null)
			this.datasetDumpLocation = new ArrayList<String>();
		this.datasetDumpLocation.add(datasetDumpLocation);
	}
	
	/**
	 * Returns the zip parameter value
	 * @return a string with the zip parameter value 
	 */	
	public String getGzip() {
		return gzip;
	}

	/**
	 * Sets the zip tag value
	 * @param gzip a string with the datasetDumpLocation tag value
	 */	
	public void setGzip(String gzip) {
		if (gzip.equalsIgnoreCase("Y") || gzip.equalsIgnoreCase("Yes") || gzip.equals("1") )
			this.gzip = TRUE;
		else
			this.gzip = FALSE;
					
	}
	

	/**
	 * Sets the configuration file name, reads the paremeters 
	 * @param fileName a string with the configuration fileName
	 */	
	public void setConfigurationFile(String fileName) {
		confFile = new ConfigFileReader();
		confFile.setGenerator(this);
		confFile.setFileName(fileName);
		confFile.read();
	}

	/**
	 * Create the options for the command line 
	 */	
	public static void createOptions() {
		options = new Options();
		options.addOption(new Option("o","outputdir",true,"output directory for sitemap files, defaults to current directory"));
        options.addOption(new Option("f","changefreq",true,"one of always, hourly, daily, weekly, monthly, yearly, never"));
        options.addOption(new Option("l","lastmod",true,"date of last modification of the dataset"));
        options.addOption(new Option("x","exclude",true,"URLs matching this regular expression will be excluded"));
        options.addOption(new Option("z","gzip",false,"compress sitemap files"));
        options.addOption(new Option("r","siteroot",true,"base location of the sitemap files, e.g., http://example.com/"));
        options.addOption(new Option("d","datadump",true,"location of an RDF data dump file, e.g., http://example.com/data/catalogdump.rdf.gz"));        
	}

	/**
	 * Shows the help to the console 
	 */	
    private static void printHelp() {
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp("sitemap4rdf sparqlEndpoint uriPrefix", options, true);
        System.out.println("OR\n");
        System.out.println("usage: sitemap4rdf --config <arg>");
        System.out.println(" -c,--config configuration file");         
        
    }

	/**
	 * Checks if the configuration file option is presented
	 * @param args the set of arguments for the application
	 * @return boolean true if the configfileoption is present, false otherwise 
	 */	
	private static boolean isConfigFileOptionPresent(String[] args) {
		if (args.length == 2) {
			if (args[0].equals("-c") || args[0].equals("--config"))
			return true;
		}
		return false;
	}
    
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		if (!isConfigFileOptionPresent(args)) {
			createOptions();
			CommandLineParser parser = new PosixParser();
		    CommandLine cmd = null;
	        try {
	            cmd = parser.parse(options, args);
	        } catch (ParseException e) {
	            System.err.println(e.getMessage());
	            System.exit(-1);
	        }
			
	        if (cmd.hasOption("h")) {
	            printHelp();
	            System.exit(0);
	        }
	        if (cmd.getArgs().length != 2) {
	            printHelp();
	            System.exit(-1);
	        }

	        Generator generator = new Generator();
	        
	        generator.setEndPoint(cmd.getArgs()[0]);
	        generator.setUriPattern(cmd.getArgs()[1]);
	        
	        if (cmd.hasOption("o"))
	        	generator.setOutputDir(cmd.getOptionValue("o"));
	        
	        if (cmd.hasOption("f"))
	        	generator.setChangeFreq(cmd.getOptionValue("f"));
	        	
	        if (cmd.hasOption("l"))
	        	generator.setLastMod(cmd.getOptionValue("l"));
	        
	        if (cmd.hasOption("x"))
	        	generator.setExclude(cmd.getOptionValue("x"));

	        if (cmd.hasOption("z"))
	        	generator.setGzip(TRUE);	        
	        
	        if (cmd.hasOption("r"))
	        	generator.setSiteRoot(cmd.getOptionValue("r"));
	        
	        if (cmd.hasOption("d")) {
	        	for (String ddl : cmd.getOptionValues("d"))
	        		generator.setDatasetDumpLocation(ddl);
	        }
	        generator.generate();
	        
		}
		else {
			Generator generator = new Generator();
			generator.setConfigurationFile(args[1]);
	        generator.generate();
		}
		
	}


}


