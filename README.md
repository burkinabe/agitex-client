Ceci est un projet de test pour le récrutement AGITEX BF

Pour exécuter le projet, il faut se placer dans le dossier. Ensuite, il faut taper les commandes suivantes:

    ./mvnw clean
    ./mvnw -Pprod package -DskipTests

    docker build -t .
    docker run -d -p 8080:8080 --name test-client hash_de_limage


Pour accéder et tester les différents endpoints, il faut aller aux liens suivant avec Postman

### Pour charger un fichier

http://localhost:8080/api/v1/clients/upload-file

### Pour faire la liste des clients
http://localhost:8080/api/v1/clients 

### Pour calculer la moyenne des salaires d'une profession
http://localhost:8080/api/v1/clients/salary-average?profession=comptable
