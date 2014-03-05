#docu:
# 1. export the data of your chosen stratification: e.g. mRNA_3CNMF.csv
# 2. export the ids of your chosen group: e.g. mRNA_3CNMF_g1.csv
# 3. the KEGG_db ids are already exported to a gmt file (simple <pathway>,<pathway>(,<gene>)+ format)
# 4. run test testGSEA function as in the example
# 5. the result will be in the given result parameter file
# 6. import that score as an External score to the KEGG database:
#   select just the ES column
#   no normalization and the range is from -1 to +1


#source('GSEA.1.0.R')
#source("http://bioconductor.org/biocLite.R")
#biocLite("PGSEA")
#require(PGSEA)

testGSEA = function(input.ds.name, group_id, genesets, result,transpose=F,nperm=1000,startWith=2) {
  #data
  input.ds = as.data.frame(read.table(input.ds.name,header=T,check.names=F,sep="\t"))
  rownames(input.ds) = input.ds[,1]
  input.ds = input.ds[,startWith:ncol(input.ds)]
  if (transpose)
  	input.ds = as.data.frame(t(input.ds))
  {
    input.cn = colnames(input.ds)
    input.selected = as.character(read.table(group_id,nrows=1,as.is=T,sep="\t")[1,-1])
    phen = ifelse(sapply(input.cn, function(x) { x %in% input.selected}),0,1) #convert to assignments
    #classification query group rest
    input.cls = list(class.v=as.vector(phen),phen=c("ALL","QUERY"))
  }
  
  #print(length(input.cn))
  #write.table(t(ifelse(sapply(input.cn, function(x) { x %in% input.selected}),"QUERY","ALL")),"pheno.cls",col.names=F,row.names=F,quote=F)
  #tt = cbind(rep("",nrow(input.ds)),input.ds)
  #write.table(tt,"data.gct",col.names=T,row.names=T,quote=F,sep="\t")
  
  
  #matching data, i.e pathways
  gs.db = genesets
  
  if (!file.exists("result"))
    dir.create("result")
  #source('GSEA.1.0.R')
  r = GSEA(
    # Input/Output Files :-------------------------------------------
   input.ds =  input.ds,           # Input gene expression Affy dataset file in RES or GCT format
   input.cls = input.cls,           # Input class vector (phenotype) file in CLS format
   gs.db =     gs.db,         # Gene set database in GMT format
   output.directory      = "",        # Directory where to store output and results (default: "")
   #  Program parameters :-------------------------------------------------------------------------------------------------------------------------
   doc.string            = "result",   # Documentation string used as a prefix to name result files (default: "GSEA.analysis")
   non.interactive.run   = T,               # Run in interactive (i.e. R GUI) or batch (R command line) mode (default: F)
   reshuffling.type      = "sample.labels", # Type of permutation reshuffling: "sample.labels" or "gene.labels" (default: "sample.labels" 
   nperm                 = nperm,            # Number of random permutations (default: 1000)
   weighted.score.type   =  1,              # Enrichment correlation-based weighting: 0=no weight (KS), 1= weigthed, 2 = over-weigthed (default: 1)
   nom.p.val.threshold   = -1,              # Significance threshold for nominal p-vals for gene sets (default: -1, no thres)
   fwer.p.val.threshold  = -1,              # Significance threshold for FWER p-vals for gene sets (default: -1, no thres)
   fdr.q.val.threshold   = 0.25,            # Significance threshold for FDR q-vals for gene sets (default: 0.25)
   topgs                 = 20,              # Besides those passing test, number of top scoring gene sets used for detailed reports (default: 10)
   adjust.FDR.q.val      = F,               # Adjust the FDR q-vals (default: F)
   gs.size.threshold.min = 10,              # Minimum size (in genes) for database gene sets to be considered (default: 25)
   gs.size.threshold.max = 500,             # Maximum size (in genes) for database gene sets to be considered (default: 500)
   reverse.sign          = F,               # Reverse direction of gene list (pos. enrichment becomes negative, etc.) (default: F)
   preproc.type          = 0,               # Preproc.normalization: 0=none, 1=col(z-score)., 2=col(rank) and row(z-score)., 3=col(rank). (def: 0)
   random.seed           = 3338,            # Random number generator seed. (default: 123456)
   perm.type             = 0,               # For experts only. Permutation type: 0 = unbalanced, 1 = balanced (default: 0)
   fraction              = 1.0,             # For experts only. Subsampling fraction. Set to 1.0 (no resampling) (default: 1.0)
   replace               = F,               # For experts only, Resampling mode (replacement or not replacement) (default: F)
   save.intermediate.results = F,           # For experts only, save intermediate results (e.g. matrix of random perm. scores) (default: F)
   OLD.GSEA              = F,               # Use original (old) version of GSEA (default: F)
   use.fast.enrichment.routine = T          # Use faster routine to compute enrichment for random permutations (default: T)
    )
  #-----------------------------------------------------------------------------------------------------------------------------------------------
  
  #store result
	cr=rbind(as.data.frame(r$report1),as.data.frame(r$report2))
	cr$ES = as.numeric(levels(cr$ES))[cr$ES]
	write.table(cr,result,row.names=F,sep=";",quote=F)
  
	r
}

testPGSEA = function(input.ds, group_id, genesets, result) {
  input.ds = read.table(input.ds,header=T,check.names=F)
  rownames(input.ds) = input.ds[,1]
  input.ds = input.ds[,-1]

}

#r = testGSEA("mRNA_3CNMF.csv", "mRNA_3CNMF_g1.csv", "KEGG_db.gmt", "result.csv",nperm=10)

#rm1 = testGSEA("mrnaseq.csv", "mrnaseq_1.csv", "KEGG_db.gmt", "mrnaseq_r_1.csv",transpose=T,nperm=10,startWith=22)
#rm2 = testGSEA("mrnaseq.csv", "mrnaseq_2.csv", "KEGG_db.gmt", "mrnaseq_r_2.csv",transpose=T,nperm=10,startWith=22)
#rp1 = testGSEA("rppa.csv", "rppa_1.csv", "KEGG_db.gmt", "rppa_r_1.csv",transpose=T,nperm=10)
#rp2 = testGSEA("rppa.csv", "rppa_2.csv", "KEGG_db.gmt", "rppa_r_2.csv",transpose=T,nperm=10)