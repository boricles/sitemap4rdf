/*
 * @(#) Constants.java	0.1	2010/08/05
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

/**
 * Constants interface contains some useful variables
 * @version 0.2 05 Aug 2010
 * @author boricles
 */
public interface Constants {
	
	/** URIPATTERN constant value to be replaced for the uriPrefix parameter */
	public static final String URIPATTERN = "@@URI_PATERN@@";
	
	/** EXCLUDE_FILTER constant value to be replaced for the exclude parameter */
	public static final String EXCLUDE_FILTER_PATTERN = "\"\"";
	
	/** DEFAULT_ENCODING default encoding */
	public static final String DEFAULT_ENCODING = "UTF-8";
	
	/** DEFAULT_VERSION default version */	
	public static final String DEFAULT_VERSION = "1.0";

	/** EXCLUDE_FILTER constant value to be replaced for the exclude parameter */	
	public static final String EXCLUDE_FILTER = "@@EXCLUDE_FILTER@@";
	
	/** EMPTY_STRING constant value to be replaced if there is no exclude parameter */
	public static final String EMPTY_STRING = "";

	/** EXCLUDE_FILTER_PRESENT constant value if the exclude parameter is given */
	public static final String EXCLUDE_FILTER_PRESENT = " FILTER (!REGEX(STR(?n),\"\")). "; 
	
	/** sparqlQuery query for extracting the urls */
	public static String sparqlQuery = "SELECT DISTINCT ?n WHERE {?n a [] . FILTER (REGEX(STR(?n), \""+ URIPATTERN + "\" )). " + EXCLUDE_FILTER + " } ";
	
	/** queryVariable constant variable for the query */
	public static final String queryVariable = "n";
	
	/** outputFile variable for the default output filename */
	public static final String outputFile = "sitemap";
	
	/** outputFile variable for the default output filename */
	public static final String indexOutputFile = "sitemap_index.xml";
	
	/** zipFileExtension variable for the default zip file extension*/	
	public static final String zipFileExtension = ".gz";
	
	/** extensionOutputFile variable for the default xml file extension*/	
	public static final String extensionOutputFile = ".xml";
	
	/** XMLNS variable for the xmlns */
	public static final String XMLNS = "xmlns";
	
	/** urlTag variable for the url tag */
	public static final String urlTag = "url";
	
	/** locTag variable for the loc tag */
	public static final String locTag = "loc";	
	
	/** urlSetTag variable for the urlSet tag */
	public static final String urlSetTag = "urlset";
	
	/** namespace variable for the default namespace */	
	public static final String namespace = "http://www.sitemaps.org/schemas/sitemap/0.9";
	
	/** lastmodTag variable for the lastmod tag */
	public static final String lastmodTag = "lastmod";
	
	/** changefreqTag variable for the changeFreg tag */	
	public static final String changefreqTag = "changefreq";
	
	/** siteMapIndexTag variable for the sitemapindex tag */
	public static final String siteMapIndexTag = "sitemapindex";
	
	/** siteMapTag variable for the sitemap tag */
	public static final String siteMapTag = "sitemap";
	
	/** MAX_URLS maximum number of urls */
	public static final int MAX_URLS = 50000;//50000;
	
	/** DEFAULT_DATEFORMAT constant for the default dateformat  */
	public static final String DEFAULT_DATEFORMAT = "yyyy-MM-dd";
	
	/** defaultDocumentType constant for the default xml document type  */	
	public static final String defaultDocumentType = "XML";
	
	/** defaultDocumentType constant for the name of the attribute indent number */
	public static final String xmlAttributeIdentNumber = "indent-number";
	
	/** NS_SEMANTIC_SITEMAP constant for the schema of the semantic sitemap */	
	public static final String NS_SEMANTIC_SITEMAP = "http://sw.deri.org/2007/07/sitemapextension/scschema.xsd";
	
	/** PREFIX_SEMANTIC_SITEMAP constant for the prefix of the semantic sitemap */	
	public static final String PREFIX_SEMANTIC_SITEMAP = "sc";
	
	
	/** datasetTag constant for the dataset of the semantic sitemap */
	public static final String datasetTag = "dataset";

	/** linkedDataPrefixTag constant for the linkedDataPrefix of the semantic sitemap */	
	public static final String linkedDataPrefixTag = "linkedDataPrefix";
	
	/** dataDumpLocationTag constant for the dataDumpLocation of the semantic sitemap */	
	public static final String dataDumpLocationTag = "dataDumpLocation";

	/** endPointAtt constant for the sparqlEndpoint of the configuration file */
	public static final String endPointAtt = "sparqlEndpoint";

	/** uriPatternAtt constant for the uriPrefix of the configuration file */	
	public static final String uriPatternAtt = "uriPrefix";
	
	/** paramElement constant for the Param of the configuration file */
	public static final String paramElement = "Param";
	
	/** nameAttr constant for the name of the configuration file */	
	public static final String nameAttr = "name";

	/** valueAttr constant for the value of the configuration file */	
	public static final String valueAttr = "value";
	
	/** siterootParam constant for the siteroot of the configuration file */	
	public static final String siterootParam = "siteroot";
	
	/** outputdirParam constant for the outputdir of the configuration file */
	public static final String outputdirParam = "outputdir";	
	
	/** excludeParam constant for the exclude of the configuration file */	
	public static final String excludeParam = "exclude";
	
	/** gzipParam constant for the gzip of the configuration file */	
	public static final String gzipParam = "gzip";	
	
	/** datadumpParam constant for the datadump of the configuration file */	
	public static final String datadumpParam = "datadump";
	
	/** datasetLinkedDataPrefix constant for the datasetLinkedDataPrefix of the configuration file */	
	public static final String datasetLinkedDataPrefix = "datasetLinkedDataPrefix";
	
	/** datasetdataDumpLocation constant for the datasetDataDumpLocation of the configuration file */	
	public static final String datasetdataDumpLocation = "datasetDataDumpLocation";
	
	/** sparqlEndPointLocation constant for the sparqlEndpointLocation of the configuration file */	
	public static String sparqlEndPointLocation = "sparqlEndpointLocation";
	
	public static final String TRUE = "1";
	
	public static final String FALSE = "0";
	
	public static final String WINDOWS_FILE_SEPARATOR = "\\";
	
}
