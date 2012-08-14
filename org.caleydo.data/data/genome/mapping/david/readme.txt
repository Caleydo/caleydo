1) Filter DAVID2TAX_ID.txt

cat DAVID2TAX_ID.txt | grep 9606 > DAVID_HOMO_SAPIENS.txt
cat DAVID2TAX_ID.txt | grep 10090 > DAVID_MUS_MUSCULUS.txt

2) Remove all columns but DAVID ID
