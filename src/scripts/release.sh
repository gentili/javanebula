echo Installing ${releaseName} to videogamez.ca...
echo ${project.version} > version.txt
echo ${releaseName} > current.txt
scp *.txt gentili@mcpnet.ca:videogamez.ca/htdocs/demurrage/GameClient
scp ../${releaseName} gentili@mcpnet.ca:videogamez.ca/htdocs/demurrage/GameClient
