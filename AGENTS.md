# AGENTS.md

> Guide pour agents IA & outils d’automatisation (Copilot Agents, Codeium, Cursor, etc.) afin de contribuer efficacement au plugin **MoreUnit for Eclipse**.

## TL;DR
- Projet Eclipse/OSGi construit avec **Maven Tycho**.  
- Point d’entrée build : `org.moreunit.build/` → génère les plugins, features et l’update site P2.  
- Les artefacts (update site P2 zippé, JARs) sont publiés dans les **GitHub Releases**.

---

## Objectif du dépôt
**MoreUnit** aide à écrire et naviguer entre les tests et leurs classes cibles dans Eclipse (Java notamment) : création de squelettes de tests, navigation test↔classe, préférences de nommage, prise en charge des mocks, etc.  
Documentation utilisateur : <https://moreunit.github.io/MoreUnit-Eclipse/>

---

## Carte du code (modules principaux)
Dossiers racine (non exhaustif) :
- **org.moreunit.core** : cœur fonctionnel (mécanismes de correspondance test ↔ classe).  
- **org.moreunit.plugin** : plug-in UI Eclipse (handlers, actions, préférences).  
- **org.moreunit.mock** / **.mock.feature** / **.mock.test** / **.mock.it** : génération et tests liés aux mocks.  
- **org.moreunit.feature** / **org.moreunit.light.feature** : features P2 complètes/légères.  
- **org.moreunit.updatesite** : site P2 (catégories, agrégation).  
- **org.moreunit.core.test**, **org.moreunit.swtbot.test** : tests unitaires et UI (SWTBot).  
- **org.moreunit.test.dependencies** : dépendances de test empaquetées pour OSGi.  
- **org.moreunit.build** : build Tycho (agrégateur, profiles, versions).

> Astuce agent : commencer par lire `MANIFEST.MF`, `plugin.xml`, `feature.xml` de chaque module, puis chercher l’implémentation dans `org.moreunit.core` et les points d’extension dans `org.moreunit.plugin`.

---

## Prérequis de build (agents & CI)
- **JDK** : Java 11 minimum pour l’exécution du plugin. Pour le build, Java 17+ fonctionne en général avec Tycho récents ; en cas d’échec, rétrograder à Java 11.  
- **Maven** : 3.8+ (3.9 recommandé).  
- **Environnements GUI** : les tests **SWTBot** nécessitent un environnement graphique (ou Xvfb en CI).

> Vérifier la matrice de compatibilité exacte (Eclipse/Java) sur la page **Releases** du dépôt et dans les notes de version.

---

## Commandes essentielles
Exécuter depuis la racine du dépôt (ou en ciblant le module de build).

```bash
# Build complet (plugins + features + update site + tests)
mvn -f org.moreunit.build/pom.xml clean verify

# Skipper les tests (itérations rapides)
mvn -f org.moreunit.build/pom.xml clean verify -DskipTests

# Générer l'update site zippé (artefacts sous org.moreunit.updatesite/target/)
mvn -f org.moreunit.build/pom.xml clean package

# Lancer uniquement les tests unitaires (sans SWTBot)
mvn -f org.moreunit.build/pom.xml -pl org.moreunit.core.test,org.moreunit.mock.test -am test

# SWTBot en headless (ex. CI Linux)
mvn -f org.moreunit.build/pom.xml -pl org.moreunit.swtbot.test -am verify   -Dtycho.localArtifacts=ignore
```

Résultats attendus :
- Bundles `org.moreunit.*.jar`, features `org.moreunit.*.jar` et **update site** (répertoire P2 + zip) dans `org.moreunit.updatesite/target/`.  
- Les releases publiques publient ces artefacts (zip d’update site + JARs).

---

## Lancer le plugin dans Eclipse (développement)
1. Importer tous les projets `org.moreunit.*` comme **Plug-in Projects**.  
2. Définir un **Target Platform** aligné sur une distribution Eclipse supportée (voir Releases).  
3. Démarrer une **Eclipse Application** (PDE) depuis `org.moreunit.plugin`.  
4. Vérifier les menus/raccourcis (Jump to Test / Jump to Test Subject).

---

## Conventions & style
- **Langage** : Java (OSGi/Eclipse).  
- **Organisation** : logique pure dans `core`, UI et handlers dans `plugin`.  
- **Tests** : privilégier les tests unitaires dans `*.core.test` / `*.mock.test` ; UI dans `*.swtbot.test`.  
- **Manifestes** : maintenir `Bundle-RequiredExecutionEnvironment`, `Import-Package` et versions cohérents avec les features.  
- **Commits/PRs** : titres concis, description du comportement utilisateur impacté, liens issues.  
- **Compat** : éviter d’introduire des dépendances rompant la compatibilité annoncée dans les Releases.

---

## Procédures types (agents)

### Ajouter une règle de détection de tests / nouveau motif de nommage
1. Ajuster la logique de mapping dans `org.moreunit.core` (recherche de classes de test/sujet).  
2. Exposer la préférence si nécessaire via `org.moreunit.plugin` (pages de préférences).  
3. Couvrir par tests dans `org.moreunit.core.test`.  
4. Vérifier l’UI (SWTBot) si un flux ou un menu change.

### Corriger un problème de génération de mocks
1. Localiser la partie parsing/configuration dans `org.moreunit.mock`.  
2. Ajouter/adapter la dépendance OSGi si nécessaire (attention aux bibliothèques retirées du JDK depuis Java 11).  
3. Couvrir par tests dans `org.moreunit.mock.test` et vérifier la non‑régression UI.

### Préparer une release interne
1. **Versionner** via Tycho (update des versions OSGi/features/poms si nécessaire).  
2. `mvn -f org.moreunit.build/pom.xml clean verify`  
3. Vérifier `org.moreunit.updatesite/target/` (répertoire P2 + zip).  
4. Créer une **GitHub Release** et attacher les artefacts générés (pattern identique aux releases existantes).

---

## Liens utiles
- **Repo** : <https://github.com/MoreUnit/MoreUnit-Eclipse>  
- **Site / documentation courte** : <https://moreunit.github.io/MoreUnit-Eclipse/>  
- **Installation (Marketplace / update site)** : voir README et Releases du dépôt.

---

## Notes pour agents
- Toujours **reconstruire** depuis `org.moreunit.build/` pour conserver des versions cohérentes (features / update site).  
- En cas d’échec Tycho lié au JDK, tester Java 11 vs Java 17.  
- Respecter la séparation **core** (métier) / **plugin** (UI PDE) pour faciliter les tests.

---

_Mis à jour : 11 août 2025 (vérifier la page Releases pour la dernière version publiée)._  
