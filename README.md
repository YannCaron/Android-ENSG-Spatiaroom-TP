# Android-ENSG-Spatiaroom-TP

## Introduction

Dans ce TP noté, nous allons revoir tout ce qui a été dit et effectué durant le cours, donc si vous avez bien suivi, ça devrait jouer (expression romande inside).
Il vous est demandé de réaliser une application complète et utilisable.

### Vous devez :
- Créer une application qui compile et s'exécute
- Vous avez toute la journée, de 9h du matin à 15h30
- Copier le devoir sur repertoire partagé de l'école à 15h30 **uniquement**

### Vous avez le droit :
- De vous servir du cours
- De poser des questions à Monsieur **Google** en personne, s'il vous répond :-)
- De m'appeler en dernier recours, si vous êtes coincés et que ça ne compile définitivement pas !

## Cahier des charges

Vous devez créer une application de relevé géographique qui s'articule en 4 grandes fonctionnalités :
- Créer une interface utilisateur avec 3 écrans :
  - Une carte **google map** `(2 points)`
  - Un formulaire de saisie des **points d'intérêts** `(2 points)`
  - Un formulaire de saisie des **relevés topographiques** `(1 point)`
- Créer une base de données non géographique qui gère:
  - les points d'intérêts `(3 points)`
  - les relevés topologiques `(2 points)`
- Enrichir l'application avec la possibilité de relever des données géographiques:
  - être notifié par le changement de position du GPS `(3 points)`
  - enregistrer une liste de points dans le cadre d'un relevé topographique `(2 points)`
- Instrumenter la base de données pour la basculer en mode Spatial avec [SpatiaRoom](https://github.com/anboralabs/spatia-room)
  - enregistrer et afficher des points d'intérêt `(3 points)`
  - enregistrer et afficher des relevés topographiques `(2 points)`
- Bonus `(3 points)` enrichir les données avec du reverse geocoding en utilisant les services de google
- Bonus Bis `(2 points)` enrichir l'application avec vos idées, soyez créatif :wink:

### Partie I : Création de l'interface

> :warning: **Attention :** Dans cette partie du TP, il ne vous est demandé que les **écrans** et la **navigation** entre ceux-ci. Les fonctionnalités seront demandées par la suite.

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

3. De même que dans le [TP google service](https://github.com/VSasyan/AndroidENSG/tree/master/3_google_services) ajoutez la clé d'API :

```ini
# ...
MAPS_API_KEY=AIzaSyBRR1tCxqn8PJqtDX1e0mE7___________
```

> :information_source: **Remarque :** La clé vous a été envoyée par e-mail à l'adresse `ing21@ensg.eu` !

1. Concernant la vue `activity_maps.xml` vous devez encapsuler le `fragment` dans un `ConstraintLayout`.

Pour ce faire, lors de l'édition du layout, cliquez sur le bouton ![code](resources/ide_code_button.png) et remplace le code source par celui-ci:

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

> :warning: **Attention :** Cette étape est primordiale pour la suite du TP !

#### Enchainement des écrans

Vous devez présenter 3 écrans qui auront l'enchaînement suivant:
![Enchaînement des écrans](resources/screen-logic.png)

> :information_source: **Remarque :** Ne perdez pas trop de temps à placer les éléments exactement comme ils sont présentés. L'important c'est que ce soit utilisable :wink:!

#### Maps

L'écran `Maps` est l'écran qui a été créé par défaut lors de la création de l'application.
Le but étant d'ajouter deux boutons **en sur-impression**, càd par dessus, comme un calque dans Photoshop.

![Ecran Maps](resources/screen-maps.png)

#### Création d'un point d'intérêt

L'écran de création d'un **point d'intérêt** est un formulaire classique comme vu en cours.
Veuillez à bien utiliser les `Layout`.

![Ecran Maps](resources/screen-point.png)

> :warning: **Attention :** Le **style** n'est pas obligatoire, seul la mise en place compte.

#### Création d'un relevé

Même remarques que précédemment !

![Ecran Maps](resources/screen-topology.png)

### Partie II : Création de la base de donnée

Pour créer la base de donnée de notre projet, nous allons utiliser la librairie `Room`. [Room](https://developer.android.com/training/data-storage/room?hl=fr) est un ORM (Object Relational Mapping) qui nous abstrait des aspects bas niveau de création et de requêtage des bases de données.

1. Dans un premier temps, il faut ajouter les dépendances dans le fichier `build.graddle` du module :

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
- Une pour l'entité (le PoJo qui représente la donnée) ;
- Une pour le DAO (l'abstraction qui spécifie les opération CRUD à effectuer sur la base de donnée).

> :warning: **Attention :** pour faciliter la création des entités, il faut ajouter l'option `autogenerate=true` à l'annotation `@PrimaryKey`

Comme ceci:
```java
@Entity
public class Marker {

    @PrimaryKey(autoGenerate = true)
    public int uid;

    // ...
}
```

4. Ensuite, il faudra créer une nouvelle class `AppDatabase.java` :

```java
@Database(
        entities = {Marker.class, Topology.class},
        version = 1,
        exportSchema = false
)

// @TypeConverters(GeometryConverters.class) // A conserver pour plus tard, lors de l'utilisation de SpatiaRoom
public abstract class AppDatabase extends RoomDatabase {
    public abstract MarkerDao markerDao();

    public abstract TopologyDao topologyDao();
}

```

5. Et enfin, il faut utiliser la base de données dans les activités :
- `MarkerActivity`, pour sauver les marqueurs créés (nous nous chargerons des coordonnées dans la partie IIII) ;
- `TopologyActivity`, pour sauver les tracés (même remarque que précédemment).

```java
// database utils
AppDatabase db = Room.databaseBuilder(getApplicationContext(),
    AppDatabase.class, "geoSurvey.sqlite").allowMainThreadQueries().build();

// si besoin, récupère le dao de la table marker
markerDao = db.markerDao();

// si besoin, récupère le dao de la table topology
topologyDao = db.topologyDao();
```

### Partie III : Relevé GPS

A l'aide du [tutoriel de Valentin](https://github.com/VSasyan/AndroidENSG/blob/master/3_google_services/README.md) implémentez le relevé des données GPS.
L'idée est d'être notifié à chaque changement de coordonnée uniquement (inutile de récupérer la dernière position connue).

1. Il faudra ajouter les bonnes permissions dans le manifest ;
2. Il faudra demander les permissions d'accès à la position dans la classe JAva ;
3. Ajouter le code qui permet d'être notifié lors d'un changement de coordonnée GPS `getCurrentLocation()` ;
4. Une fois cette partie technique implémentée, il faudra l'instrumenter dans la class `MapActivity` afin de stocker la position courante dans une variable d'instance appelée `private LatLng currentLatLng;`.

La partie concernant le relevé topologique est un peu différente.
A partir du bouton `Record Topo` il faudra entretenir un booléen `isRecording` afin de gérer un toggle :
- Lorsque l'utilisateur clique une fois, l'activity se met à enregistrer dans une liste les latlng du GPS. Le text du button change pour 'Save Topo' ;
- Lorsque l'utilisateur clique une seconde fois sur le bouton, l'activity arrête l'enregistrement, et appelle l'activity `TopologyActivity` en lui passant la liste des coordonnées en paramètre.

5. Il faut créer les variables d'instance nécessaires :
```java
private boolean isRecording = false;
private ArrayList<LatLng> topo;
```

Voici comment fonctionne le *toggeling* :
```java
// inverse boolean (toggling)
isRecording = !isRecording;

if (isRecording) {
    // toggle button text
    bt_topology.setText("Save Topo"); // Attention, pensez aux traductions

    // initialize path to record new one
    topo = new ArrayList<>();
} else {
    // toggle button text
    bt_topology.setText("Record Topo");  // Attention, pensez aux traductions

    // Appeler l'activity TopologyActivity
}
```

> :warning: **Attention!** Pour passer une liste de `LatLng`, il faut utiliser la méthode `putParcelableArrayListExtra()` de l'intent. c.f. cours les [Android IHM Bases](https://github.com/YannCaron/Android-ENSG/blob/master/03%20-%20IHM%20Bases.pdf)

### Partie IV : Base de donnée spatiale

Afin d'avoir la possibilité de sauvegarder les coordonnées géographiques, il est nécessaire de convertir la base de données Sqlite dans sa version spatiale : `SpatiaLite`.
Spatialite est une surcouche qui assure la conversion, l'indexation, ainsi que les calculs des données géographiques.
Comme nous avions utilisé l'ORM `Room` pour accéder à notre base de données, nous allons utiliser `SpatiaRoom` pour convertir celle-ci dans sa version spatiale.

Vous trouverez la documentation de [spatia-room ici](https://github.com/anboralabs/spatia-room)

1. Dans un premier temps, il faut ajouter le repository `jitpack` dans le fichier `settings.gradle` :
```Groovy
dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
        maven { url 'https://jitpack.io' }
    }
}
```

2. Dans un second temps, il faut ajouter la dépendance au fichier `build.gradle` du module pour acceder à cette bibliothèque :

```groovy
dependencies {
        implementation 'com.github.anboralabs:spatia-room:0.2.4'
}
```

> :warning: **Attention !** il faut bien penser à synchroniser le fichier `gradle` avec le bouton contextuel :
> ![sync graddle file](resources/ide_graddle_sync.png)

2. Ensuite, il faut remplacer le `databaseBuilder` par celui de `spatia-room` chaque fois qu'il est utilisé par celui-ci

```java
AppDatabase db = SpatiaRoom.INSTANCE.databaseBuilder(
    getApplicationContext(),
    AppDatabase.class, DB_NAME).allowMainThreadQueries().build();
```

3. Et enfin, il faut ajouter l'annotation `@TypeConverters` dans la class `AppDatabase` afin d'indiquer comment convertir les données géographiques :

```java
@TypeConverters(GeometryConverters.class)
public abstract class AppDatabase extends RoomDatabase {
    public abstract MarkerDao markerDao();

    public abstract TopologyDao topologyDao();
}
```

4. Maintenant que vous avez converti votre base de données en base de données spatiale, il vous reste à créer les champs dans les entités :
- `public Point position;` dans l'entité `Marker` qui servira à sauvegarder le `LatLng` depuis l'activity `MarkerActivity`
- `public LineString path;` dans l'entité `Topology` qui servira à sauvegarder la `List<LatLng>` depuis l'activité `TopologyActivity`

Soit modifier les entités comme suit :
```mermaid
classDiagram
class Marker {
    String name
    String address
    String comment
    Point position
}
class Topology {
    String name
    String comment
    LineString path
}
```

5. Maintenant que les Entités peuvent sauvegarder des coordonnées, il faut modifier les Activités `MarkerActivity` et `TopologyActivity` respectivement afin de sauvegarder les données géographiques.

> :warning: **Attention!** afin de sauvegarder les données géographiques, il faudra les convertir. En effet, GoogleMap comprend des `LatLng` et spatia-room des `POINT`, `POLYGON` et des `LINESTRING`.

Pour ce faire, j'ai écrit une classe `GeoConverters.java` pour vous aider, que vous pouvez copier dans votre projet :

```java
public class GeoConverters {

    public static final int SRID = 4326;

    public static Point latLng2Point(LatLng latLng) {
        return new Point(latLng.longitude, latLng.latitude, SRID);
    }

    public static LatLng point2LatLng(Point point) {
        return new LatLng(point.getY(), point.getX());
    }

    public static LineString latLngs2LineString(List<LatLng> latLngs) {
        return new LineString(latLngs.stream().map(GeoConverters::latLng2Point).collect(Collectors.toList()));
    }

    public static ArrayList<LatLng> lineString2LatLng(LineString lineString) {
        return (ArrayList<LatLng>) lineString.getPoints().stream().map(GeoConverters::point2LatLng).collect(Collectors.toList());
    }

}
```

6. Enfin, maintenant que notre application est capable de stoquer nos coordonnées géographiques, il vous faut les lire et les exploiter dans l'activity `MapsActivity`
- Lire les coordonnées des markers sauvegardés et les afficher sur la carte
- Lire les relevés topographiques et créer des polygones à l'aide de la méthode `addPolyLine` de l'objet Map.
Vous pouvez vous aider de la classe `GeoConverters` que je vous ai fournis avec l'utilisation des méthodes `point2LatLng` et `lineString2LatLng` respectivement pour les points et les lineStrings.

Que vous pourrez utiliser ainsi :
```java
GeoConverters.latLng2Point(currentLatLng)
```

#### Tester le GPS

1. Afin de pouvoir tester les étapes précédentes, vous pouvez lancer les `Extended Controls` de l'émulateur :
![Extended Controls](resources/ide_extended_button.png)

2. Ensuite il vous suffit de séléctionner l'onglet `Location` puis `Routes` et enfin de cliquer sur `Import GPX/KLM` :
![Extended Controls](resources/ide_extended_controls.png)

3. Vous pouvez télécharger le fichier GPX suivant: [Randonnée-Moustier-Sainte-Marie](resources/download/randonnee-moustier.zip)

4. Et enfin, vous pouvez envoyer des données GPS à votre emulateur en cliquant sur le bouton `PLAY ROUTE`:
![Play Route](resources/ide_ext_play_route.png)


### Partie V (Bonus): Geocoding Google

Dans cette partie, vous allez accéder au service Google de Geocoding.
Il ne vous sera pas utile d'exécuter le tutoriel de Valentin, nous allons directement utiliser la fonction de geocodage dans le Thread de l'activity. Cette méthode n'est pas conseillée par Google, mais nous permet de simplifier le code.

Il vous est demandé de suivre la documentation sur le site [developer.android.com](https://developer.android.com/reference/android/location/Geocoder)

Vous allez devoir :

1. Dans la gestion de l'événement du bouton "GET ADDRESS", créer une nouvelle instance de la classe `GeoCoder`. Attention à vos imports !

2. afin de recevoir l'adresse par rapport à vos coordonnées, appelez la méthode `getFromLocation` de cet objet.

> :warning: **Attention**: Vous aller devoir gérer une exception de type `IOException`, pour ce faire vous pouvez encapsuler votre code dans le traitement suivant:
```Java
try {
    // Ici le code qui génère l'éventuelle exception
} catch (IOException e) {
    throw new RuntimeException(e);
}
```

3. Maintenant que l'adresse est reçu par le **Geocoder**, il faut mettre cette adresse dans le champs **adresse** de l'activity.

## Points Bonus

Maintenant que vous avez fini votre application, vous pouvez l'améliorer :
- Changer l’icône par défaut avec une icône que vous pourrez télécharger sur [IconFinder](https://www.iconfinder.com) et la transformer pour votre application Android avec [Android Asset Studio](https://romannurik.github.io/AndroidAssetStudio/icons-launcher.html)
- Changer le thème et les couleurs de l'application
- Gérer de multiples langues (internationalisation) et créer une traduction
- Améliorer le style et le placement des composants dans les vues ;
- Changer les icônes pour les marqueurs ainsi que pour les relevés topographiques ;
- Une page de présentation / a propos / aide
- La gestion du zoom de la carte
- Acceder aux données météo ou tout autre type d'appel à des API externe
- Une aide contextuelle, des tooltips
- Des toast pour prévenir l'utilisateur de certaines actions
- Ou toute autre idée, soyez créatifs ! :wink:

Chaque amélioration se verra rétribuer d'1 ou 2 points :wink: alors bon courage. :smile:

## Rendu

Pour rendre votre travail, utilisez l'explorateur de fichiers Windows pour zipper (ZIP ou 7z) votre dossier **après avoir fermé Android Studio** (clic droit => 7z => Ajouter à "GeoSurvey.7z")) :

![Création archive 7z](resources/screen-7zip-1.png)

Renommez alors l'archive selon le modèle "NOM_Prenom.7z" :

![Renommage archive 7z](resources/screen-7zip-2.png)

Enfin, déposez le fichier sur `\\FormationTemp\IGN2\Android\Rendus`.

**Conservez une copie du fichier en cas de problème.**

## Remerciements

Merci à vous tous d'avoir participé à ce cours, et j'espère que Valentin et moi même vous aurons appris quelques petites choses sur Android et les application mobiles. :-)
Bonne continuation et bonne réussite dans vos projets.

Yann et Valentin
