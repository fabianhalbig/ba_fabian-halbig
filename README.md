# Bachelorarbeit Halbig, Fabian

Dieses Repository enthält die implmentieren Services für die Bachelorabeit mit folgendem Thema:
> Migration einer Standalone Spring-Anwendung nach Architektur von Microservices über Kubernetes

Durch die Implementierung der Microservice-Anwendung ist es möglich, die Anwendung in Docker Compose oder Kubernetes auszuführen.

## Auführung in Docker Compose
Für die Ausführung der Microservices muss zunächst für jede Anwendung ein Docker-Image erstellt werden, weshalb in der Docker Compose Datei die Bezeichnung der Images entsprechend zu aktualisieren sind.

### Spring-Boot Services
Insgesamt muss für die folgenden Packages aus den Spring-Boot Anwendungen ein Docker-Image erstellt werden:
- eureka-server
- api-gateway
- costumer-service
- user-service

Dazu sind die folgenden Schritte Notwendig:
1. Anpassung der Referenzen zu dem Eureka-Server und der Keycloak entsprechend dem auskommentierten Code in dem `application.yml` Dateien
2. Build des Spring-Boot Projektes in eine JAR-Datei: `mvn clean install`
3. Erstellung des Images: `docker build -t <Image-Name> .` 

### Angular Anwendungen
Für die Erstellung der Images für die gelisteten Angular Packages sind die folgenden Schritte notwendig:
- public-angular
- keycloak-angular

1. Benötigte Packages installieren: `npm install`
2. Kompilieren der Angular Anwendung: `ng build --prod`
3. Erstellung der beiden Images: `docker build -t <Image-Name> .`

Nachdem die Images erstellt sind, ist es möglich, mit der entsprechenden Referenzierung in der Docker Compose Datei und dem Befehl `docker-compose up` aus den erstellten Images eine Laufzeitumgebung zu generieren. Zudem werden durch diesen Befehl mit der Datei drei Volumes für eine dauerhafte Datenspeicherung erzeugt, die in den beiden Postgres-Datenbankservices sowie in dem MySQL-Container benötigt werden.

## Deployment in Kubernetes (GCP)
Für das Deployment der Services der Applikationslogik in Kubernetes müssen zunächste die benötigten Images erstellt werden.

Durch die erstellten Images ist es möglich mit dem folgendem Vorgehen ein Deployment in einem GCP-Kubernets-Cluster durchzuführen:
1. Erstellung eines Container-Clusters: `gcloud container clusters create scheduler-services --region europe-west1 --node-locations europe-west1-b europe-west1-c`
2. Erstellung der PVC, der Storageklassen sowie der Postgres-Datenbankdienste:
3. Deployment des Keycloak-Services
    - `kubectl apply -f keycloak.yml`
    - Erstellung eines Realms
    - Import der Konfiguration durch die Datei `realm-export.json`
4. Bereitstellung der Spring-Services
    - Erstellung eines Secretes mit den aktualisierenden Keycloak-Informationen: `kubectl apply -f keycloak-secret.yml`
    - Deployment der Anguler-Services 

Nach dieser Bereitstellung der Services in Kubernets können die Endpunkte von dem User-Service und dem Course-Service durch HTTP-Requests mit einem entsprechenden JWT-Token angesprochen werden.
