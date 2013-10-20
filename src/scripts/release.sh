export TARGET="/home/gentili/videogamez.ca/htdocs/demurrage/GameClient/"
echo Installing ${releaseName} to videogamez.ca...
scp *.TXT gentili@mcpnet.ca:${TARGET}
scp ../${releaseName} gentili@mcpnet.ca:${TARGET}
