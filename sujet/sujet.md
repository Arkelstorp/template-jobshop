---
title: Méthodes Approchées pour la Résolution de Problèmes d'Ordonnancement
subtitle: Partie 1
author: Arthur Bit-Monnot, Marie-Jo Huguet
geometry: margin=3cm
...

# Étapes de mise en place

## Discord

 - rejoignez le serveur Discord pour ces TP : [https://discord.gg/KyUbCCT](https://discord.gg/KyUbCCT). 
 - indiquez votre *Prénom Nom* comme pseudo, pour que l'on puisse vous identifier
 - en cas de problème technique, vous pourrez vous adresser au chan *support-technique* de ce serveur.
 
## Document de suivi 
     
 - inscrivez vous dans le document de suivi : [https://docs.google.com/spreadsheets/d/1QAZlWaTCvrMlLLuwuVFmCD8Plr9QvOoPVpIR4g1PSBk/edit?usp=sharing](https://docs.google.com/spreadsheets/d/1QAZlWaTCvrMlLLuwuVFmCD8Plr9QvOoPVpIR4g1PSBk/edit?usp=sharing)
 - à chaque étape franchie dans le TP, ajountez un `X` dans la case correspondante 
 
## Récuperation du code

- Récupérez la base de code sur Github : [https://github.com/insa-4ir-meta-heuristiques/template-jobshop](https://github.com/insa-4ir-meta-heuristiques/template-jobshop)

- Suivez les instructions dans le README pour vous assurer que le code compile.
- Importez le projet dans votre IDE préféré. Tous doivent pouvoir supporter l'import de projet gradle et quelques liens sont donnés en bas du README. 

 
# Prise en main

## Représentation d'un problème de JobShop

Vous trouverez dans le dossier `instances/` un ensemble d'instances communément utilisées pour le problème de jobshop.
Si l'on considère l'instance vu dans les exercices, constituée de deux jobs avec trois tâches chacun :

\begin{table}[h!]
 	\centering
 		\begin{tabular}{c | c | c | c   }
 		  $J_1$  & $r_1, 3$ & $r_2, 3$ & $r_3, 2$ \\
 		  \hline
 		  $J_2$  & $r_2, 2$ & $r_1, 2$ & $r_3, 4$ \\
 		\end{tabular}
 	\label{tab:tache}
 \end{table}
 
L'instance est nommée `aaa1` décrite ci-dessus est donnée dans le fichier `instances/aaa1` :

```
    # Fichier instances/aaa1
    2 3 # 2 jobs and 3 tasks per job
    0 3 1 3 2 2 # Job 1 : (machine duration) for each task 
    1 2 0 2 2 4 # Job 2 : (machine duration) for each task 
```

La première ligne donnée le nombre de jobs et le nombre de tâches par jobs. Le nombre de machine est égal au nombre de tâches.
Chacune des lignes suivantes spécifie la machine (ici numérotées `0`, `1` et `2`) et la durée de chaque tâche. Par exemple la ligne
`0 3 1 3 2 2` spécifie que pour le premier job :

 - `0 3`: la première tâche s'execute sur la machine `0` et dure `3` unités de temps
 - `1 3`: la deuxième tâche s'execute sur la machine `1` et dure `3` unités de temps
 - `2 2`: la troisième tâche s'execute sur la machine `2` et dure `2` unités de temps

 
## Base de code

Il vous est fourni une base de code pour faciliter votre prise en main du problème.
Vous trouverez dans la classe `jobshop.Main` un point d'entrée pour réaliser des tests de performance de méthodes heuristiques.
Les autres points d'entrée du programme sont les tests déjà présent ou pouvant être ajoutés dans le dossier `src/test/java/`. 

### Problème et Solution
 
 - classe `jobshop.Instance` qui contient la représentation d'un problème et une méthode pour parser un fichier de problème.
 - classe `jobshop.Schedule` qui contient la représentation directe, associant à chaque tâche une date de début.
 La classe schedule sert notamment à la représentation d'une solution et toute autre représentation doit pouvoir être traduite dans un `Schedule`
 
### Représentation 
 
 - une classe abstraite `jobshop.Encoding` dont les sous classes sont des représentations d'une solution au JobShop. 
 - une classe `jobshop.encoding.NumJobs` qui contient une implémentation de la représentation par numéro de job
 
### Solveurs

Deux solveurs très simples basés sur une représentation par numéro de job. Les nouveaux solveurs doivent être ajoutés à la structure `jobshop.Main.solvers` pour être accessibles dans le programme principal.
 
 - `basic` : méthode pour la génération d'une solution pour une représentation par numéro de job
 - `random` : méthode de génération de solutions aléatoires par numéro de job



## À faire : manipulation de représentations

Ouvrez la méthode `EncodingTests.testJobNumbers()`. 

 - Pour les deux solutions en représentation par numéro de job, calculez (à la main) les dates de début de chaque tâche
 - implémentez la méthode `toString()` de la classe `Schedule` pour afficher les dates dé début de chaque tâche dans un schedule.
 - Vérifiez que ceci correspond bien aux calculs que vous aviez fait à la main.
 
Création d'une nouvelle representation par ordre de passage sur les ressources : 

- Créer une classe `jobshop.encodings.ResourceOrder` qui contient la représentation par ordre de passage sur ressources vue dans les exercices.
- Pour cette classe, implémentez la méthode `toSchedule()` qui permet d'extraire une représentation directe.  
- Ajouter des tests dans `src/test/java/jobshop` permettant de vérifier que vos méthodes fonctionnent bien pour les exemples traités en cours (instance `aaa1`).
Vous pouvez pour cela vous inspirer et ajouter des cas de test à `EncodingTests`. 

Changement de représentation : 

- pour les deux représentations `ResourceOrder` et `JobNums`, créez des méthodes permettant de créer cette représentation depuis un `Schedule`.
- utilisez là pour tester la conversion de `ResourceOrder` vers `JobNums` et vice-versa.


# Heuristiques gloutonne

Un schéma général d'heuristique est présenté dans l'article [@Blazewicz1996] et est résumé ci-dessous:

 #. se placer à une date $t$ égale à la plus petite date de début des opérations 
 #. construire l'ensemble des opérations pouvant être réalisées à la date $t$
 #. sélectionner l'operation $(i,j)$ réalisable de plus grande priorité
 #. placer $(i,j)$ au plus tôt sur la ressource $k$ qui la réalise (en respectant les contraintes de partage de ressources, c'est à dire en vérifiant la date de disponibilité de la ressource $k$)
 #. recommencer en (3) tant qu'il reste des opérations à ordonnancer 
 #. recommencer en (2) en incrémentant la date $t$ (à la prochaine date possible compte tenu des dates de début des opérations)


Selon la manière dont sont gérées les priorités entre les opérations on obtient différentes solutions d'ordonnancement. Les règles de piorité classiquement utilisées sont : 

 - SPT (Shortest Processing Time) : donne la priorité à la tâche ayant la plus petite durée;
 - LPT (Longest Processing Time) : donne la priorité à la tâche ayant la plus grande durée 

## À faire

 - Créer un nouveau solveur implémentant une recherche gloutonne pour les priorités SPT et LPT, basé sur la représentation par ordre de passage sur les ressources
 - Evaluer ces heuristiques sur les instances fournies et pour la métrique d'écart fournie.
 - (optionnel) Concevoir une version randomisée de ces heuristiques où une partie des choix est soumise à un tirage aléatoire
 - Débuter la rédaction d'un rapport présentant le travail effectué

Pour les tests de performance, on privilégiera les instance `ft06`, `ft10`, `ft20` et les instances de Lawrence `la01` à `la40`.

<!--
 - Concevoir version *randomisée* de ces recherches gloutonnes
 - Evaluer cette nouvelle heuristique sur les instances fournies et pour les métriques définies précédemment
 - Comparer les résultats obtenus par ces deux heuristiques par rapport à ceux de la littérature et à la méthode exacte
 - Débuter la rédaction d'un rapport présentant le travail effectué
-->

# Recherche exhaustive

Dans le but de garantir l'optimalité des solutions trouvée, implémentez une recherche exhaustive basée sur une recherche en profondeur d'abord (Depth-First Search).  
On notera que par rapport à la méthode gloutonne, une recherche exhaustive doit permettre de tester l'ensemble des opérations pouvant être réalisée à instant $t$ (étape (2) de la méthode gloutonne ci-dessus).
On ne cherchera pas à optimiser cette méthode, des techniques plus adaptées à la recherche exhaustive pour de tels problèmes seront traitées en 5ème année.

Quelle semble être la taille limite d'une instance pour permettre une recherche exhaustive ?

# Références 

<!--
# Méthode de descente

- implémenter chemin critique
- méthode de validation

- implémenter voisinage 
   - Laarhoven
   - Nowicki et Smutnicki (optionnel)

- méthode descente 

- méthode de descente avec départ multiples (optionnel)


# Métaheuristiques

Au choix : Méthode tabou ou Algorithme génétique



 -->
 
 
 