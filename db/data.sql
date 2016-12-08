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
 keywords text NOT NULL
);

INSERT INTO portion VALUES ('1','1ere portion de ouf style FDGDFG DFGDFGDF DFGDFGDFG DFGGDFG DFGTDFGDFG GDFGDFG DFGGDFFSDFSDFSDFSDFSDFSDFSDFSDFSDFSDFSDFSDFSDG', '1', "motclé1,keyword1");
INSERT INTO portion VALUES ('2','2eme portion',  '1', "motclé1,keyword1");
INSERT INTO portion VALUES ('3','3eme portion', '2', "Vida,loca,espagna,playa,fiesta,corrida,muchachos,hermanos,revolucion");
INSERT INTO portion VALUES ('4','4eme portion', '2', "aller,marcher,courir,avancer,tout droit,couscous?,couloir, pk ????");
INSERT INTO portion VALUES ('5','5eme portion', '3', "stop,fin,fini,end,trottoir");
INSERT INTO portion VALUES ('6','6eme portion', '3', "motclé1,keyword1");
INSERT INTO portion VALUES ('7','7eme portion', '4', "motclé1,keyword1");
INSERT INTO portion VALUES ('8','8eme portion', '4', "motclé1,keyword1");
INSERT INTO portion VALUES ('9','9eme portion', '4', "motclé1,keyword1");
INSERT INTO portion VALUES ('10','10eme portion', '6', "motclé1,keyword1");
INSERT INTO portion VALUES ('11','11eme portion', '6', "motclé1,keyword1");
INSERT INTO portion VALUES ('12','12eme portion', '6', "motclé1,keyword1");
INSERT INTO portion VALUES ('13','13eme sans catégorie', '0', "motclé1,keyword1");

INSERT INTO categorie VALUES ('1', 'sql', '0');
INSERT INTO categorie VALUES ('2', 'test', '1');
INSERT INTO categorie VALUES ('3', 'Tell', '1');
INSERT INTO categorie VALUES ('4', 'Me', '1');
INSERT INTO categorie VALUES ('5', 'If', '1');
INSERT INTO categorie VALUES ('6', 'Works', '0');
INSERT INTO categorie VALUES ('7', 'Fine !', '4');
INSERT INTO categorie VALUES ('8', 'OW !', '7');
INSERT INTO categorie VALUES ('9', 'POOOOW !', '8');
