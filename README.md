# SYSTÈME DE SUIVI DES MÉDAILLES OLYMPIQUES
### Projet de Fin de Module - DÉVELOPPEMENT BACKEND


## Présentation du Projet
Ce projet consiste en une application backend complète développée avec **Spring Boot** pour le suivi en temps réel des médailles olympiques. L'application permet de gérer les pays, les athlètes, les compétitions et l'attribution des médailles, tout en fournissant des classements dynamiques et des statistiques détaillées.

## Fonctionnalités Principales
- **Gestion des Pays** : CRUD complet pour les nations participantes.
- **Gestion des Athlètes** : Suivi des profils d'athlètes et de leur rattachement national.
- **Gestion des Compétitions** : Planification et suivi du statut des épreuves par discipline.
- **Attribution des Médailles** : Enregistrement des résultats (Or, Argent, Bronze) avec association automatique aux pays et athlètes.
- **Classements Dynamiques** :
    - Classement par nombre total de médailles (par défaut).
    - Classement par nombre de médailles d'or.
    - Classement par système de points (Or=3, Argent=2, Bronze=1).
- **Statistiques & Analyses** :
    - Statistiques globales par pays.
    - Rapports détaillés par discipline pour chaque pays.

## Stack Technique
- **Langage** : Java 21
- **Framework** : Spring Boot 
- **Persistance** : Spring Data JPA / Hibernate
- **Base de données** : MySQL 9.1
- **Validation** : Jakarta Bean Validation
- **Logging** : SLF4J avec Logback
- **Gestionnaire de dépendances** : Maven
- **Tests** : JUnit 5 & Mockito

## Configuration et Installation
1.  **Prérequis** :
    - JDK 21 installé.
    - MySQL.

2. **Cloner le projet**
  - git clone https://github.com/fayelise/medal_tracker.git

3. **Creer la base de donnees MySQL avec la commande suivante**
   - Create DATABASE medal;(garder le meme nom pour eviter des erreurs ou sinon changer aussi le nom dans **application.properties**)

4.  **Configuration Base de Données** :
    Modifiez le fichier `src/main/resources/application.properties` avec vos identifiants :
    ```properties
    spring.datasource.url=jdbc:mysql://localhost:3306/medal
    spring.datasource.username=VOTRE_USERNAME
    spring.datasource.password=VOTRE_PASSWORD
    ```
5.  **Lancement de l'application** :
    ```bash
    ./mvnw spring-boot:run
    ```

## Spécifications API REST (v1)

### Pays
- `GET /api/v1/pays` : Liste paginée des pays.
- `GET /api/v1/pays/{id}` : Détails d'un pays.
- `POST /api/v1/pays` : Créer un pays.
- `PUT /api/v1/pays/{id}` : Modifier un pays.
- `DELETE /api/v1/pays/{id}` : Supprimer un pays.

### Athlètes
- `GET /api/v1/athletes` : Liste paginée des athlètes.
- `GET /api/v1/athletes/{id}` : Détails d'un athlète.
- `GET /api/v1/athletes/pays/{paysId}` : Athlètes d'un pays spécifique (paginé).
- `POST /api/v1/athletes` : Créer un athlète.
- `PUT /api/v1/athletes/{id}` : Modifier un athlète.
- `DELETE /api/v1/athletes/{id}` : Supprimer un athlète.

### Compétitions
- `GET /api/v1/competitions` : Liste paginée des compétitions.
- `GET /api/v1/competitions/{id}` : Détails d'une compétition.
- `POST /api/v1/competitions` : Créer une compétition.
- `PUT /api/v1/competitions/{id}` : Modifier une compétition.
- `DELETE /api/v1/competitions/{id}` : Supprimer une compétition.

### Médailles
- `GET /api/v1/medailles` : Liste de toutes les médailles.
- `POST /api/v1/medailles` : Attribuer une médaille à un athlète pour une compétition.
- `GET /api/v1/medailles/athlete/{athleteId}` : Historique des médailles d'un athlète.
- `GET /api/v1/medailles/competition/{competitionId}` : Résultats d'une compétition.

### Classement & Statistiques
- `GET /api/v1/classement` : Classement général (Total).
- `GET /api/v1/classement?tri=or` : Classement par médailles d'or.
- `GET /api/v1/classement?tri=points` : Classement par système de points.
- `GET /api/v1/classement/pays/{id}` : Statistiques globales d'un pays.


## Tests
Exécutez les tests unitaires et d'intégration via Maven :
```bash
./mvnw test
```

## Architecture
Le projet suit une architecture en couches (Layered Architecture) :
1.  **Controller** : Gestion des requêtes HTTP et réponses REST.
2.  **Service** : Logique métier et orchestrations.
3.  **Repository** : Accès aux données via JPA.
4.  **Entity** : Modélisation des objets de la base de données.
5.  **DTO** : Transfert de données sécurisé et validé.


