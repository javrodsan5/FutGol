DROP TABLE vet_specialties IF EXISTS;
DROP TABLE vets IF EXISTS;
DROP TABLE specialties IF EXISTS;
DROP TABLE visits IF EXISTS;
DROP TABLE pets IF EXISTS;
DROP TABLE types IF EXISTS;
DROP TABLE owners IF EXISTS;
DROP TABLE usuarios_ligas IF EXISTS;
DROP TABLE ligas IF EXISTS;
DROP TABLE usuarios IF EXISTS;
DROP TABLE authorities IF EXISTS;
DROP TABLE users IF EXISTS;



CREATE TABLE users (
    id       INTEGER IDENTITY PRIMARY KEY,
    username VARCHAR(255) NOT NULL,
    password VARCHAR(255) NOT NULL,
    enabled BOOLEAN NOT NULL
);

CREATE INDEX user_name ON users (username);

CREATE TABLE authorities (
    id INTEGER IDENTITY PRIMARY KEY,
    username VARCHAR(255) NOT NULL,
    authority VARCHAR(255) NOT NULL
);

ALTER TABLE authorities ADD CONSTRAINT fk_user_authority FOREIGN KEY (username) REFERENCES users (username);

CREATE INDEX authority_name ON authorities (authority);

CREATE TABLE usuarios (
    id INTEGER IDENTITY PRIMARY KEY,
    name VARCHAR(30) NOT NULL,
    email  VARCHAR(255) NOT NULL,
    username   VARCHAR(255) NOT NULL
);

ALTER TABLE usuarios ADD CONSTRAINT fk_authority_usuario FOREIGN KEY (username) REFERENCES users (username);

CREATE INDEX usuario_name ON usuarios (name);


CREATE TABLE ligas (
    id INTEGER IDENTITY PRIMARY KEY,
    name VARCHAR(30) NOT NULL,
    admin VARCHAR(255) NOT NULL
);
CREATE INDEX liga_name ON ligas (name);
ALTER TABLE ligas ADD CONSTRAINT fk_liga_usuario FOREIGN KEY (admin) REFERENCES users (username);

CREATE TABLE usuarios_ligas (
    liga_id INTEGER NOT NULL,
    usuario_id INTEGER NOT NULL
);
ALTER TABLE usuarios_ligas ADD CONSTRAINT fk_usuarios_ligas_ligas FOREIGN KEY (liga_id) REFERENCES ligas (id);
ALTER TABLE usuarios_ligas ADD CONSTRAINT fk_usuarios_ligas_usuarios FOREIGN KEY (usuario_id) REFERENCES usuarios (id);


CREATE TABLE vets (
  id         INTEGER IDENTITY PRIMARY KEY,
  first_name VARCHAR(30),
  last_name  VARCHAR(30)
);
CREATE INDEX vets_last_name ON vets (last_name);

CREATE TABLE specialties (
  id   INTEGER IDENTITY PRIMARY KEY,
  name VARCHAR(80)
);
CREATE INDEX specialties_name ON specialties (name);

CREATE TABLE vet_specialties (
  vet_id       INTEGER NOT NULL,
  specialty_id INTEGER NOT NULL
);
ALTER TABLE vet_specialties ADD CONSTRAINT fk_vet_specialties_vets FOREIGN KEY (vet_id) REFERENCES vets (id);
ALTER TABLE vet_specialties ADD CONSTRAINT fk_vet_specialties_specialties FOREIGN KEY (specialty_id) REFERENCES specialties (id);

CREATE TABLE types (
  id   INTEGER IDENTITY PRIMARY KEY,
  name VARCHAR(80)
);
CREATE INDEX types_name ON types (name);

CREATE TABLE owners (
  id         INTEGER IDENTITY PRIMARY KEY,
  first_name VARCHAR(30),
  last_name  VARCHAR_IGNORECASE(30),
  address    VARCHAR(255),
  city       VARCHAR(80),
  telephone  VARCHAR(20)
);
CREATE INDEX owners_last_name ON owners (last_name);

CREATE TABLE pets (
  id         INTEGER IDENTITY PRIMARY KEY,
  name       VARCHAR(30),
  birth_date DATE,
  type_id    INTEGER NOT NULL,
  owner_id   INTEGER NOT NULL
);
ALTER TABLE pets ADD CONSTRAINT fk_pets_owners FOREIGN KEY (owner_id) REFERENCES owners (id);
ALTER TABLE pets ADD CONSTRAINT fk_pets_types FOREIGN KEY (type_id) REFERENCES types (id);
CREATE INDEX pets_name ON pets (name);

CREATE TABLE visits (
  id          INTEGER IDENTITY PRIMARY KEY,
  pet_id      INTEGER NOT NULL,
  visit_date  DATE,
  description VARCHAR(255)
);
ALTER TABLE visits ADD CONSTRAINT fk_visits_pets FOREIGN KEY (pet_id) REFERENCES pets (id);
CREATE INDEX visits_pet_id ON visits (pet_id);
