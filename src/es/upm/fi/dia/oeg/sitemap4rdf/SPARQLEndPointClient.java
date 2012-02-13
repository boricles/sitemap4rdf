/*
 * @(#) SPARQLEndPointClient.java	0.1	2010/08/05
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

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.query.Syntax;


/**
 * SPARQLEndPointClient Allows to execute a sparql query on an endpoint
 * @version 0.2 05 Aug 2010
 * @author boricles
 */

public class SPARQLEndPointClient implements Constants {
	
	private static final Logger logger = LoggerFactory.getLogger(SPARQLEndPointClient.class);
	
/**
 * Default constructor 
 */	
    public SPARQLEndPointClient() {
    }

	/**
	 * Returns results of the execution of a sparql query into a particular endpoint
	 * @param repositoryString endpoint to query
	 * @param queryString the query
	 * @return a resultset with the set of reults
	 * @exception generic java.lang.Exception  
	 */	
    public ResultSet execQueryEndPoint(String repositoryString, String queryString) throws Exception {
        ResultSet results = null;
        //allows to execute a sparql query on an endpoint
        Query query = QueryFactory.create(queryString, Syntax.syntaxARQ);
        QueryExecution qexec = QueryExecutionFactory.sparqlService(repositoryString, query);
        try {
            results = qexec.execSelect();
        } catch (Exception e) {
			logger.debug("Exception ",e);
			System.err.println("Exception when executing the query to the sparql endpoint");
			System.err.println(e.getMessage());
			System.exit(3);
            //return results;
        } finally {
            //qexec.close() ;
        }
        return results;
    }

}


