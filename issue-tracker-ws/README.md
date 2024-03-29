Issue-Tracker-WS
================

Backend REST simple basé sur SpringBoot permettant de faire du CRUD
sur une entité représentant une tâche.

# Description de l'Appli

Ce projet utilise une base de données embarqué H2.
La console web est accessible à l'adresse :
http://localhost:9191/console
Pour les paramètres de connexion se référer au fichier application.properties

A l'adresse http://localhost:9191/swagger-ui.html#/issue-controller on va trouver le swagger correspondant aux différents endpoint de IssueController

A l'adresse http://localhost:9191/api/issues on va trouver la liste des 'issues' au format JSON

La liste des issues peut être filtré par status avec la requête suivante :

http://localhost:9191/api/issues?status=OPEN

La liste des issues peut être filtré par effort

http://localhost:9191/api/issues?effort_gte=4&effort_lte=16


# Construction et lancement du conteneur Docker

docker build -t ggn/issue-tracker-ws .

docker run -it --rm -p 9191:9191 ggn/issue-tracker-ws /bin/bash

Une fois le conteneur lancé, exécuter ensuite : (on est dans le répertoire /opt/issue_tracker_ws)

java -jar target/issue-tracker-ws-1.0-SNAPSHOT.jar
