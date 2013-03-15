transposefile = function(fileName) {
    d = read.csv(fileName,sep=";",header=FALSE)
    write.table(t(d), paste(fileName,".csv",sep=""),quote=FALSE,sep="\t",row.names=FALSE,col.names=FALSE)
}
