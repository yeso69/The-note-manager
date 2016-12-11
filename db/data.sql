DROP TABLE IF EXISTS categorie;

CREATE TABLE categorie (
 ID integer PRIMARY KEY AUTOINCREMENT,
 libelle text NOT NULL UNIQUE,
 id_parent integer CONSTRAINT fk_categorieparent REFERENCES categorie(id)
);

DROP TABLE IF EXISTS portion;

CREATE TABLE portion (
 ID integer PRIMARY KEY AUTOINCREMENT,
 message text NOT NULL,
 id_categorie integer NOT NULL CONSTRAINT fk_categorie REFERENCES categorie(id),
 keywords text
);
