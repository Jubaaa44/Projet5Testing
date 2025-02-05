# Projet 5 OC

## Description
Projet n°5 Openclassrooms Parcours "Développeur Full-Stack"

## Guide d'Installation et d'Utilisation

### 1. Installation Base de données
- Créez une Base nommée "projet5"
- Exécutez le fichier script.sql (ressources > sql) pour générer les tables

### 2. Prérequis et Installation du projet
#### Prérequis
Assurez-vous d'avoir installé :
- Java 11
- NodeJS 16
- MySQL
- Angular CLI 14

#### Installation
1. Récupérer le projet via le lien suivant : https://github.com/Jubaaa44/Projet5Testing
2. Installez les dépendances avec la commande :
```bash
npm install
```

### 3. Lancement du projet
1. Lancer la connexion à la base de données
2. Pour le back-end :
   - Click droit sur le projet
   - Sélectionnez "Run As..." 
   - Puis "3 Maven build..."
   - Insérez "springboot:run" dans "Goals"
   - Cliquez sur Run
3. Pour le front-end :
```bash
npm run start
```

### 4. Exécution des tests

#### Tests Back-end
1. Click droit sur le projet
2. Sélectionnez "Run As..."
3. Puis "2 JUnit Test"

#### Tests Front-end
- Pour les tests unitaires et d'intégration :
```bash
npm run test
```
- Pour les tests End-to-End :
```bash
npm run e2e
```

### 5. Génération des rapports de couverture

#### Rapports Back-end
1. Click droit sur le projet
2. Sélectionnez "Run As..."
3. Puis "3 Maven build..."
4. Insérez "clean test jacoco:report" dans "Goals"
5. Cliquez sur Run
6. Le rapport de couverture est disponible dans : `\back\target\site\jacoco\index.html`

#### Rapports Front-end
1. Exécutez les commandes :
```bash
npm run e2e:coverage
npm run test --coverage
```
2. Les rapports sont disponibles dans :
   - Tests End-to-end : `\front\coverage\lcov-report`
   - Tests unitaires et intégrations : `\front\coverage\jest\lcov-report`