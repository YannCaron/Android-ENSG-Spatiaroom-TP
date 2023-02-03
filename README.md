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
- Créer une interface utilisateur avec 3 écrans :
  - Une carte **google map** `(2 points)`
  - Un ecran de saisie des **points d'intérêts** `(2 points)`
  - Un ecran de saisie des **relevés topographique** `(1 point)`
- Créer une base de donnée non géographique qui gère:
  - les points d'intérêts `(3 points)`
  - les relevés topologiques `(2 points)`
- Enrichir l'application avec la possibilité de relever des données géographiques:
  - être notifié par le changement de position du gps `(3 points)`
  - enregistrer une liste de points dans le cadre d'un relevé topographique `(2 points)`
- Instrumenter la base de donnée pour la basculer en mode Spatial avec SpatiaRoom
  - enregistrer les points d'intérêt `(3 points)`
  - enregistrer les relevés topographiques `(2 points)`
- Bonus `(3 points)` enrichir les données avec du reverse geocoding en utilisant les services de google

### Partie I : Création de l'interface

> :warning: **Attention:** Dans cette partie du TP, il ne vous est demandé que les **écrans** et la **navigation** entre ceux-ci. Les fonctionnalités seront demandées par la suite.

#### Création du projet

1. Choisissez une activité `Google Map Activity` :

![Écran de création](resources/new-project-type.png)

2. Configurez votre projet :
   1. appelez-le `geoSurvey` ;
   2. choisissez un nom de package (par exemple `fr.ign.geosurvey`) ;
   3. choisissez un emplacement de sauvegarde (sur `D:\`) ;
   4. sélectionnez le language Java ;
   5. sélectionnez l'API 28: Android 9.0 (Pie).

![Écran de création](resources/new-project-name.png)

3. De même que dans le [TP google service](https://github.com/VSasyan/AndroidENSG/tree/master/3_google_services) ajouter la clé d'API:

```ini
# ...
MAPS_API_KEY=AIzaSyBRR1tCxqn8PJqtDX1e0mE7___________
```

> :information_source: **Remarque:** La clé vous a été envoyé par e-mail hier à l'adresse `ing21@ensg.eu`!

4. Concernant la vue `activity_maps.xml` vous devez encapsuler le `fragment` dans un ConstraintLayout.

Pour ce faire, lors de l'édition du layout, clickez sur le bouton ![code](resources/ide_code_button.png) et remplace le code source par celui-ci:

```java
<?xml version="1.0" encoding="utf-8"?>
<androidx.constraintlayout.widget.ConstraintLayout
    xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:map="http://schemas.android.com/apk/res-auto"
    xmlns:tools="http://schemas.android.com/tools"
    android:layout_width="match_parent"
    android:layout_height="match_parent">

    <fragment
        android:id="@+id/map"
        android:name="com.google.android.gms.maps.SupportMapFragment"
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        tools:context=".MapsActivity"
        tools:ignore="MissingConstraints" />

</androidx.constraintlayout.widget.ConstraintLayout>

```

> :warning: **Attention:** Cette étape est primordiale pour la suite du TP!

#### Enchainement des écrans

Vous devez présenter 3 écrans qui auront l'enchaînement suivant:
![Enchaînement des écrans](resources/screen-logic.png)

> :information_source: **Remarque:** Ne perdez pas trop de temps à placer les éléments exactement comme ils sont présentés. L'important c'est que ce soit utilisable :wink:!

#### Maps

L'écran `Maps` est l'écran qui a été créé par défaut lors de la création de l'application.
Le but étant d'ajouter deux boutons **en sur-impression**, càd par dessus, comme un calque dans photoshop.

![Ecran Maps](resources/screen-maps.png)

#### Création d'un point d'intérêt

L'écran de création d'un **point d'intérêt** est un formulaire classique comme vu en cours.
Veuillez à bien utiliser les `Layout`. 

![Ecran Maps](resources/screen-point.png)

> :warning: **Attention:** Le **style** n'est pas obligatoire, seul la mise en place compte.

#### Création d'un relevé

Même remarques que précédement !

![Ecran Maps](resources/screen-topology.png)

### Partie II : Création de la base de donnée

1. Dans un premier temps, il faut ajouter les dépendances dans el fichier `build.graddle` du module

```groovy
dependencies {
    def room_version = "2.5.0"

    implementation "androidx.room:room-runtime:$room_version"
    annotationProcessor "androidx.room:room-compiler:$room_version"
}
```

2. Ensuite, pour que la modification doit prise en compte, il faut synchroniser le fichier à l'aide du bandeau contextuel :
![sync graddle file](resources/ide_graddle_sync.png)

3. Puis, en vous aidant de la documentation de [developer android room](https://developer.android.com/training/data-storage/room?hl=fr#java), créez les deux entités suivantes:

```mermaid
classDiagram
class Marker {
    String name
    String address
    String comment
}
class Topology {
    String name
    String comment
}
```
Pour chaque entité, il faut créer 2 classes :
- Une pour l'entité (le PoJo qui représente la donnée)
- Une pour le DAO (l'abstraction qui spécifie les opération CRUD à effectuer sur la base de donnée)

> :warning: **Attention:** pour facilité la création des entités, il faut ajouter l'option `autogenerate=true` à l'annotaiton `@PrimaryKey` 

Comme ceci:
```java
@Entity
public class Marker {

    @PrimaryKey(autoGenerate = true)
    public int uid;

    // ...
}
```

4. Ensuite, il faudra créer une nouvelle class `AppDatabase.java`

```java
package fr.ign.geosurvey.data;

import androidx.room.Database;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import co.anbora.labs.spatia.geometry.GeometryConverters;

@Database(
        entities = {Marker.class, Topology.class},
        version = 1,
        exportSchema = false
)

// @TypeConverters(GeometryConverters.class) // Concerver pour plus tard, lors de l'utilisation de SpatiaRoom
public abstract class AppDatabase extends RoomDatabase {
    public abstract MarkerDao markerDao();

    public abstract TopologyDao topologyDao();
}

```

5. Et enfin, il faut utiliser la base de donnée dans les activités :
- MarkerActivity, pour sauver les maquers créer (nous nous chargerons des coordonnée dans la partie IIII)
- TopologyActivity, pour tauver les tracés (même remarque que précédement)

```java
// database utils
AppDatabase db = Room.databaseBuilder(getApplicationContext(),
    AppDatabase.class, 'geoSurvey.sqlite').allowMainThreadQueries().build();

// si besoin, récupère le dao de la table marker
markerDao = db.markerDao();

// si besoin, récupère le dao de la table topology
topologyDao = db.topologyDao();
```

### Partie III : Relevé GPS

A l'aide du [tutoriel de Valentin](https://github.com/VSasyan/AndroidENSG/blob/master/3_google_services/README.md) implémentez le relevé des données GPS.
L'idée est d'être notifié à chaque changement de coordonée uniquement (inutile de récuper la dernière position connue).

1. Dans la classe Il faudra ajouter les bonnes permissions dans le manifest
2. Demande la permission d'accès à la position
3. Ajouter le code qui permet d'être notifié lors d'un changement de coordonnée GPS `getCurrentLocation()`

4. Une fois cette partie technique implémentée, il faudra l'instrumenter dans la class `MapActivity` afin de stoquer la position courante dans une variable d'instance appelé `private LatLng currentLatLng;`

La partie concernant le relevé topologique est un peu différente.
A partir du bouton `Record Topo` il faudra entretenir un booléen `isRecording` afin de gérer un toggle :
- Lorsque l'utilisateur clique une fois, l'activity se met à enregistrer dans une liste les latlng du GPS. Le text du button change pour 'Save Topo'
- Lorsque l'utilisateur clique une seconde fois sur le bouton, l'activity stope l'enregistrement, et appel l'activity `TopologyActivity` en lui passant la liste des coordonnées en paramètre.

1. Il faut créer les variables d'instance nécéssaires:
```java
private boolean isRecording = false;
private ArrayList<LatLng> topo;
```

Voici comment fonctionne le toggeling:
```java
// inverse boolean (toggling)
isRecording = !isRecording;

if (isRecording) {
    // toggle button text
    bt_topology.setText('Save Topo'); // Attention, pensez aux traductions possibles

    // initialize path to record new one
    topo = new ArrayList<>();
} else {
    // toggle button text
    bt_topology.setText('Record Topo');  // Attention, pensez aux traductions possibles

    // Appeller l'activity TopologyActivity
}
```

> :warning: **Attention!** Pour passer une liste de `LatLng`, il faut utiliser la méthode `putParcelableArrayListExtra()` de l'intent. c.f. cours les [Android IHM Bases](https://github.com/YannCaron/Android-ENSG/blob/master/03%20-%20IHM%20Bases.pdf)



