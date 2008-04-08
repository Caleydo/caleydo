/*
 * Project: GenView
 * 
 * Author: Marc Streit
 * 
 *  creation date: 03-07-2006
 *  
 */
package org.caleydo.core.test.soap.kegg;

import keggapi.KEGGLocator;
import keggapi.KEGGPortType;

class KEGGQueryTest {

	public static void main(String[] args) throws Exception {

		KEGGLocator locator = new KEGGLocator();
		KEGGPortType serv = locator.getKEGGPort();

		String[] res = new String[50];
		
		// Returns all the GENES entry IDs in E.coli genome which are assigned
		// EC number ec:1.2.1.1
		res = serv.get_genes_by_enzyme("ec:3.3.1.1", "hsa");

		for (int i = 0; i < res.length; i++)
		{
			System.out.println("Enzyme 3.3.1.1 is activated by gene with geneID: "
					+res[i]);
		}
	}
}

/*
 * import keggapi.*;
 * 
 * class KEGG_Test { public static void main(String[] args) throws Exception {
 * KEGGLocator locator = new KEGGLocator(); KEGGPortType serv =
 * locator.getKEGGPort();
 * 
 * String query = "eco:b1002"; String[] results =
 * serv.get_genes_by_pathway(query);
 * 
 * System.out.println("here we go..."); for (int i = 0; i < results.length; i++) {
 * System.out.println(results[i]); } } }
 */
