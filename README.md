# Automatisation des Tests - Drest.tn

Projet de tests automatisés réalisé dans le cadre de mon stage de fin d'études chez **Odest Consulting**.

## Contexte

Drest.tn est une plateforme e-commerce tunisienne développée sous WooCommerce.
Ce projet vise à remplacer les tests manuels par des tests automatisés afin de garantir
la qualité et la stabilité de la plateforme.

## Technologies utilisées

- Java 17
- Selenium 4.27
- TestNG 7.10
- Allure Report
- Maven
- GitHub Actions

## Structure du projet
src/test/java/Tests/

├── BaseTest.java          → Configuration et initialisation du driver

├── HomePageTest.java      → Tests de la page d'accueil

├── NavigationTest.java    → Tests de navigation dans les menus

├── SearchTest.java        → Tests de la barre de recherche

├── ProductTest.java       → Tests des fiches produits

├── CartTest.java          → Tests du panier d'achat

├── LoginTest.java         → Tests d'authentification (OK + KO)

└── E2ETest.java           → Tests End-to-End parcours utilisateur
## Lancer les tests

Depuis IntelliJ IDEA, panneau Maven :
- `Lifecycle → clean → test`

## Rapport Allure

Le rapport est disponible en ligne :
👉 https://samarbenrhouma-esprit.github.io/drest-selenium-tests/
## CI/CD

Les tests se lancent automatiquement chaque nuit à 2h00 via GitHub Actions.
Le rapport est envoyé par email après chaque exécution.

## Auteur

**Samar Ben Rhouma** — Étudiante ingénieure ESPRIT  
Stage chez Odest Consulting — 2026