# Android-ENSG-Spatiaroom-TP

## Introduction

Dans ce TP noté, nous allons revoir tout ce qui a été dit et effectué durant le cours, donc si vous avez bien suivi, ça devrait jouer (expression romande inside).
Il vous est demandé de réaliser une application complète et utilisable.

### Vous devez:
- Créer une application qui compile et s'exécute
- Vous avez toute la journée, de 9h du matin à 15h30
- Copier le devoir sur repertoire partagé de l'école à 15h30 **uniquement**

### Vous avez le droit:
- De vous servir du cours
- De poser des questions à Monsieur **Google** en personne, s'il vous répond :-)
- De m'appeller en dernier recours, si vous êtes coincé et que ça ne compile définitivement pas !

## Cahier des charges

Vous devez créer une application de relevé géographique qui s'articule en 4 grande fonctionnalité:
- Créer une interface utilisateur avec 3 écrans (5 points) :
	- Une carte google map
	- Un ecran de saisie des points d'intérêts
	- Un ecran de saisie des relevés topographique
- Créer une base de donnée qui gère les points d'intérêts ainsi que les relevés (5 points) 
- Instrumenter la base de donnée pour la basculer en mode Spatial avec SpatiaRoom (5 points) 
- Enrichir l'application avec la possibilité de relever des données géographiques (5 points)
- Bonus (3 points) enrichir les données avec du geocoding en utilisant les service de google

### Création de l'interface

#### Création du projet

1. Choisissez une activité `Google Map Activity` :

![Écran de création](resources/new-project-type.png)

2. Configurez votre projet :
   1. appelez-le `geoSurvey` ;
   2. choisissez un nom de package (par exemple `fr.ign.geosurvey`) ;
   3. choisissez un emplacement de sauvegarde (sur `D:\`) ;
   3. sélectionnez le language Java ;
   4. sélectionnez l'API 28: Android 9.0 (Pie).

![Écran de création](resources/new-project-name.png)

3. De même que dans le [TP google service](https://github.com/VSasyan/AndroidENSG/tree/master/3_google_services) ajouter la clé d'API:

```ini
# ...
MAPS_API_KEY=AIzaSyBRR1tCxqn8PJqtDX1e0mE7___________
```

#### Layout des écrans

