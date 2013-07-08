#first take a world university ranking and grab (e.g. using downthemall) all university meta data html files


extract = function(fileName) {
	fileContent = readChar(fileName, file.info(fileName)$size)
	gsub(".*page-title\">([^<]*)</h1>.*<span class=\"country\">([^<]*)</span>.*","\\1;\\2",fileContent)
}

fs = list.files()
sapply(fs, extract, USE.NAMES=FALSE)

extract = function(fileName) {
	fileContent = readChar(fileName, file.info(fileName)$size)
	gsub(".*page-title\">([^<]*)</h1>.*","\\1",fileContent)
}
