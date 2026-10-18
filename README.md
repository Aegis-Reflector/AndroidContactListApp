# AndroidContactListApp



Application Android de gestion de contacts, réalisée pour le cours 420-5GM-BB – Applications mobiles (travail pratique 1).



Auteur : Emmanuel Jean Rigaud Desmornes



### Lancer le projet



1. Cloner le repo et l'ouvrir dans Android Studio.

2. Attendre la fin de la synchronisation Gradle.

3. Choisir l'émulateur Pixel 4a et appuyer sur Run.



## Fonctionnalités requises




– Liste des contacts affichée avec une photo (ou initiale), un nom, un numéro de téléphone et une étoile pour les favoris.

– Création, modification et suppression de contacts. Un clic sur un contact ouvre la page de modification.

– Un seul composable (`EditContacts`) sert à la création et à la modification, pour éviter la duplication de code.

– Champs d'un contact : prénom et nom (obligatoires), numéro de téléphone (obligatoire), courriel, adresse, âge, favori et photo.

– Les champs obligatoires vides affichent une erreur et empêchent la sauvegarde.

– Un identifiant unique (`uid`) est ajouté à la création et n'est jamais affiché.

– Photo du contact prise avec la caméra ou choisie dans la galerie.

– avec une dont le bouton d'action change : « + » sur la liste, corbeille sur la page de modification (seulement pour un contact existant).

– Sauvegarde locale avec Room : les contacts et leurs photos sont conservés après la fermeture de l'application.

– Rotation portrait/paysage : l'état est conservé (") et la page de modification défile en paysage.

– Messages pour l'ajout, la modification et la suppression.

– Ressources en français et en anglais.

– Thème « modifié » : couleur primaire personnalisée (mauve).


## Fonctionnalités requises manquantes



Aucune fonctionnalité requise n'est manquante.



## Fonctionnalités supplémentaires



– Recherche par nom ou par numéro de téléphone, avec un message quand aucun contact ne correspond.

– Tri de la liste : nom A-Z, nom Z-A ou favoris en premier.

– Appeler, envoyer un texto ou un courriel à partir de la page de modification (vers le téléphone, les messages et le courriel). Aucune permission n'est nécessaire, et un bouton est désactivé quand le champ est vide.

– Formatage du numéro de téléphone : (514) 555-0143 à l'affichage, alors que seuls les chiffres sont sauvegardés (10 chiffres au maximum).

– Messages d'erreur : un « ? » s'affiche si aucune application ne peut gérer une action (cartes ou courriel, par exemple).



## Structure du code



– : l'activité, l'énumération » et « 

– : la liste des contacts et une ligne de contact, avec les animations.

– : le formulaire de création et de modification, et le champ obligatoire réutilisable.

– : la barre de recherche et le menu de tri.

– : les options de tri et la logique de filtre et de tri (sans interface).

– : les boutons d'appel, de texto et de courriel, et la fonction qui lance les «

– : le formatage du numéro de téléphone.

– : le « qui » expose les contacts.

– : l'entité `Contact`, le DAO et la base de données Room.
