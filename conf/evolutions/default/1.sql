# --- !Ups

PRAGMA foreign_keys = ON;

CREATE TABLE "ptu" (
 "id" INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
 "name" CHAR(1) NOT NULL,
 "value" INTEGER NOT NULL
);


CREATE TABLE "color" (
 "id" INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
 "name" VARCHAR(20) NOT NULL,
 "value" VARCHAR(27) NOT NULL
);

-- kategorie towarow
CREATE TABLE "category" (
	"id" INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
	"name" VARCHAR(220) NOT NULL,
	"description" VARCHAR(500) NOT NULL
);

-- promocje i obnizki
CREATE TABLE "discount" (
	"id" INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
     "name" VARCHAR(200) NOT NULL,
     "value" INTEGER  NOT NULL DEFAULT 0
);


CREATE TABLE "product" (
	"id" INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
    "category" INTEGER NOT NULL,
    "color" INTEGER DEFAULT NULL,
	"name" VARCHAR(200) NOT NULL,
	"description" VARCHAR(500) NOT NULL,
	"price" INTEGER NOT NULL,   -- price / 100 == PLN
	"discount" INTEGER NOT NULL,
	FOREIGN KEY(discount) REFERENCES discount(id) ON DELETE CASCADE,
	FOREIGN KEY(category) references category(id) ON DELETE CASCADE,
	FOREIGN KEY(color) references color(id) ON DELETE CASCADE
);

CREATE TABLE "size" (
	"id" INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
    "size" INTEGER  --CHECK( "size" BETWEEN 0 AND 470 OR "size" = 0) -- rozmiar obuwia EU = 10
);

CREATE TABLE "stock" (
	"id" INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
    "product" INTEGER NOT NULL REFERENCES product(id),
    "size" INTEGER NOT NULL REFERENCES "size"(id),
    "pieces" INTEGER NOT NULL DEFAULT 0
);

CREATE TABLE "client" (
	"id"	      INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
	"name"	      VARCHAR NOT NULL,
    "providerId"  VARCHAR NOT NULL,
    "providerKey" VARCHAR NOT NULL,
    "email"       VARCHAR NOT NULL,
    "NIP"         VARCHAR(12) DEFAULT NULL
);


CREATE TABLE "basket" (
	"id" INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
	"session" INTEGER NOT NULL,
	"product" INTEGER NOT NULL REFERENCES product(id),
    "pieces" INTEGER NOT NULL
);


CREATE TABLE "paragon" (
	"id" INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
	"client" INTEGER NOT NULL REFERENCES client(id),
	"basket" INTEGER NOT NULL REFERENCES basket(session),
	"timestamp" DATETIME DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE "user"
(
    "id"          INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
    "providerId"  VARCHAR NOT NULL,
    "providerKey" VARCHAR NOT NULL,
    "email"       VARCHAR NOT NULL
);


CREATE TABLE "oAuth2Info"
(
    "id"          INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
    "providerId"  VARCHAR NOT NULL,
    "providerKey" VARCHAR NOT NULL,
    "accessToken" VARCHAR NOT NULL,
    "tokenType"   VARCHAR,
    "expiresIn"   INTEGER
);


CREATE TABLE "authToken"
(
    "id"     INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
    "userId" INT     NOT NULL,
    FOREIGN KEY (userId) references app_user (id)
);

CREATE TABLE "passwordInfo"
(
    "id"          INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
    "providerId"  VARCHAR NOT NULL,
    "providerKey" VARCHAR NOT NULL,
    "hasher"      VARCHAR NOT NULL,
    "password"    VARCHAR NOT NULL,
    "salt"        VARCHAR
);

# --- !Downs

DROP TABLE IF EXISTS "user";
DROP TABLE IF EXISTS "authToken";
DROP TABLE IF EXISTS "passwordInfo";
DROP TABLE IF EXISTS "oAuth2Info";

DROP TABLE IF EXISTS "paragon";
DROP TABLE IF EXISTS "basket";
DROP TABLE IF EXISTS "stock";
DROP TABLE IF EXISTS "size";
DROP TABLE IF EXISTS "product";
DROP TABLE IF EXISTS "category";
DROP TABLE IF EXISTS "discount";
DROP TABLE IF EXISTS "client";
DROP TABLE IF EXISTS "color";
DROP TABLE IF EXISTS "ptu";