# Changelog

Tous changements importants seront journalisés dans ce fichier.

Basé sur [Keep a Changelog](https://keepachangelog.com/en/1.0.0/).

## [0.3.6] - 2024-06-11

### Changed

- [opendata-marqueblanche#32](https://github.com/megalis-bretagne/opendata-marqueblanche/issues/32) synchro des infos utilisateurs vers Keycloak : renommage de l'attribut "role" en "role_opendata"
- [Config] Migration de données pour les comptes Keycloak existants : renommage de l'attribut utilisateur "role" en "role_opendata"

## [0.3.5] - 2024-04-03

### Changed
- [Config]  no-reply@prae.megalis.bretagne.bzh en lieu et place de no-reply@megalis.bretagne.bzh

### Fixed

- [R-230830] - Init du pays implantation à france par defaut
- [HOTFIX][R-188186]- Correction IdExterneParent envoyé à la SDM valorisé correctement

## [0.3.0] - 2023-06-20

### Changed
- [MEGALIS-297](https://dev.sib.fr/bts/browse/MEGALIS-297):  Branchement API SIREN V3 (Tests de non-régression par rapport à API V2)
- [MEGALIS-319](https://dev.sib.fr/bts/browse/MEGALIS-319):  Montée de version de tapestry 5.8.2
- [MEGALIS-292](https://dev.sib.fr/bts/browse/MEGALIS-292): Ajouter le lien de régénération de mot de passe sur mail d'expiration
- [MEGALIS-295](https://dev.sib.fr/bts/browse/MEGALIS-295): Ajouter de la date de création du compte dans l'export des agents effectué depuis le socle
- [MEGALIS-311](https://dev.sib.fr/bts/browse/MEGALIS-311):  Listes utilisateurs : ajout de l'identifiant mail et du rôle de "gestion de mon entité"
- [MEGALIS-313](https://dev.sib.fr/bts/browse/MEGALIS-313):  Recherche d'un utilisateur par un agent, avoir un moyen de recherche ou qu'il soit trié au mieux. Barre de recherche à mettre en place sur le tableau.
- [MEGALIS-315](https://dev.sib.fr/bts/browse/MEGALIS-315):  Afficher le nom de l’organisme de l’utilisateur à la place de l’image ‘Mon Organisme Public’
- [MEGALIS-320](https://dev.sib.fr/bts/browse/MEGALIS-320):  Avoir accès avec le compte SA aux XML envoyées aux applications

### Fixed

- [BUGFIX] Recherche sur l'email et l'identifiant en même temps provoque une erreur
- Ajout script des scripts d'initialisation de la base de données

## [0.2.8] - 2023-06-14

### Fixed
-[HOTFIX][R-185713] Update entreprise api token

## [0.2.7] - 2023-02-07

### Fixed

- [MEGALIS-278](https://dev.sib.fr/bts/browse/MEGALIS-278): Changement de wording dans l'email envoyé
- Modification de la limite du nombre de caractères dans l'expression régulière utilisée pour vérifier la validité de l'adresse mail d'un compte en cours de création (ticket iTop R-164295).

### Changed

