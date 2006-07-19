package cerberus.application.kegg.test;

import keggapi.KEGGLocator;
import keggapi.KEGGPortType;
import keggapi.SSDBRelation;

class KEGG_Test {
        public static void main(String[] args) throws Exception {
                KEGGLocator    locator  = new KEGGLocator();
                KEGGPortType   serv     = locator.getKEGGPort();

                String         query    = args[0];
                SSDBRelation[] results  = null;

                results = serv.get_best_neighbors_by_gene(query, 1, 50);

                for (int i = 0; i < results.length; i++) {
                        String gene1  = results[i].getGenes_id1();
                        String gene2  = results[i].getGenes_id2();
                        int    score  = results[i].getSw_score();
                        System.out.println(gene1 + "\t" + gene2 + "\t" + score);
                }
        }
}

/*import keggapi.*;

class KEGG_Test {
        public static void main(String[] args) throws Exception {
                KEGGLocator  locator = new KEGGLocator();
                KEGGPortType serv = locator.getKEGGPort();

                String   query   = "eco:b1002";
                String[] results = serv.get_genes_by_pathway(query);
                
                System.out.println("here we go...");
                for (int i = 0; i < results.length; i++) {
                        System.out.println(results[i]);
                }
        }
}*/
