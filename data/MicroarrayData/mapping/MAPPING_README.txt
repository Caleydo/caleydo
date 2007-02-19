ACCESSION to GENE_ID Mapping is currently read from:
ftp://ftp.ncbi.nlm.nih.gov/genomes/H_sapiens/mapview/rna.q.gz

Approach to get the final mapping:
1. Import to Excel
2. Remove redundant colums (only ACCESSION and GENE_ID remain)
3. Remove duplicate rows
   - Make a new column with formula =IF(A1=A2;1;0)
   - Drag formula to be inserted in all rows.
   - Then the forumlas need to be replaced by the values (0, 1)
     (OpenOffice: Mark all rows in new column, press CTRL+C, then press SHIFT+CTRL+V,
     then deselect everything in the menu except "numbers")
4. Export file to text file

gene2refseq and gene2accession would basically contain the same data.
Source: ftp://ftp.ncbi.nlm.nih.gov/gene/DATA/
The files are huge (approx. 500 MB!!)
Because they contain all mappings to all Gene Identifiers.
That's the reason why we use the file from above (see rna.q.gz).
