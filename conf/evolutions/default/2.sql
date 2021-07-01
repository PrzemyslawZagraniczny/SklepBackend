# --- !Ups

INSERT INTO "category" ("name", "description") VALUES
("Plecak", "Plecaki wyprawowe > 50L"),
("Plecak", "Plecaki biegowe"),
("Plecak", "Plecaki na krótkie, kilkudniowe wycieczki"),
("Plecak", "Plecaki szkolne"),

("Buty", "Buty alpinistyczne"),
("Buty", "Buty w góry wysokie");
INSERT INTO "color" ("name", "value") VALUES
("black", "#000000"),
("red", "#CC0000"),
("green", "#00CC00"),
("blue", "#0000CC"),
("white", "#FFFFFF"),
("silver", "#AAAAAA");

INSERT INTO "discount" ("value", "name") VALUES
(0, "brak promocji"), (5, "majówkowe cięcie cen");

INSERT INTO "product" ("category","color", "name", "description","price", "discount") VALUES
(6, 2, "Zamberlan 8000 Everest EVO RR",  "Buty wyprawowe Zamberlan 8000 Everest EVO RR to model dla prawdziwych profesjonalistów wspinających się na ośmiotysięczniki, którzy oczekują maksymalnego wsparcie od swojego zaawansowanego ekwipunku. Nie inaczej jest w przypadku tej technicznie dopracowanej konstrukcji, która sprawi, że wejście na szczyt będzie lepiej zabezpieczone oraz skutecznie wsparte.", 312000, 1),
(6, 3, "Zamberlan 8000 Everest EVO RR",  "Buty wyprawowe Zamberlan 8000 Everest EVO RR to model dla prawdziwych profesjonalistów wspinających się na ośmiotysięczniki, którzy oczekują maksymalnego wsparcie od swojego zaawansowanego ekwipunku. Nie inaczej jest w przypadku tej technicznie dopracowanej konstrukcji, która sprawi, że wejście na szczyt będzie lepiej zabezpieczone oraz skutecznie wsparte.", 312000, 2),
(6, 1, "Garmont G-Radikal GTX",  "Buty wysokogórskie Garmont G-Radikal GTX to wysoce zaawansowana konstrukcja sprawdzająca się na trudnych szlakach i wymagających skalnych ścianach, gdy przychodzi Ci podejmować się aktywności typu alpejskiego. Zastosowano tutaj szereg rozwiązań mających zapewnić większe bezpieczeństwo w terenie, a jednocześnie wspierać użytkownika tego modelu na kolejnych kilometrach ścieżek lub metrach skalnych występu. Ta konstrukcja sprawdzi się w okresie przejściowym i latem, gdy mimo cieplejszej aury na wyższych partiach gór zalega śnieg.", 152700, 1),
(6, 2, "Garmont G-Radikal GTX",  "Buty wysokogórskie Garmont G-Radikal GTX to wysoce zaawansowana konstrukcja sprawdzająca się na trudnych szlakach i wymagających skalnych ścianach, gdy przychodzi Ci podejmować się aktywności typu alpejskiego. Zastosowano tutaj szereg rozwiązań mających zapewnić większe bezpieczeństwo w terenie, a jednocześnie wspierać użytkownika tego modelu na kolejnych kilometrach ścieżek lub metrach skalnych występu. Ta konstrukcja sprawdzi się w okresie przejściowym i latem, gdy mimo cieplejszej aury na wyższych partiach gór zalega śnieg.", 152700, 1),
(5, 2, "La Sportiva Trango Tower GTX",  "Buty wysokogórskie La Sportiva Trango Tower GTX to propozycja dla wytrawnych miłośników górskich wędrówek. Zapewnią niezbędne wsparcie techniczne na najbardziej wymagających odcinkach szlaków oraz zadbają o wygodę użytkowania i komfort termiczny, gdy wokół zapanuje mroźna zimowa aura.", 135700,2),

(3, 1, "Plecak turystyczny Gregory Zulu 30",  "Plecak turystyczny Gregory Zulu 30 - empire blue - pozwoli na podejmowanie się krótkich trekkingów lub szybkiego zdobywania szczytu odbywającego się w jeden dzień. Zmieścisz tutaj cały niezbędny ekwipunek, do którego zawsze będziesz miał wygodny dostęp. Gdziekolwiek wybierzesz się z tym modelem to liczyć możesz na maksymalną stabilność oraz komfortowe dopasowanie sprawiające, że pokonywanie kolejnych kilometrów stanie się o wiele łatwiejsze.", 52700,2),
(3, 3, "Plecak turystyczny Gregory Zulu 30",  "Plecak turystyczny Gregory Zulu 30 - empire blue - pozwoli na podejmowanie się krótkich trekkingów lub szybkiego zdobywania szczytu odbywającego się w jeden dzień. Zmieścisz tutaj cały niezbędny ekwipunek, do którego zawsze będziesz miał wygodny dostęp. Gdziekolwiek wybierzesz się z tym modelem to liczyć możesz na maksymalną stabilność oraz komfortowe dopasowanie sprawiające, że pokonywanie kolejnych kilometrów stanie się o wiele łatwiejsze.", 53800,2),
(3, 4, "Plecak turystyczny Gregory Zulu 30",  "Plecak turystyczny Gregory Zulu 30 - empire blue - pozwoli na podejmowanie się krótkich trekkingów lub szybkiego zdobywania szczytu odbywającego się w jeden dzień. Zmieścisz tutaj cały niezbędny ekwipunek, do którego zawsze będziesz miał wygodny dostęp. Gdziekolwiek wybierzesz się z tym modelem to liczyć możesz na maksymalną stabilność oraz komfortowe dopasowanie sprawiające, że pokonywanie kolejnych kilometrów stanie się o wiele łatwiejsze.", 53800,1),

(1, 2, "Plecak Osprey Kestrel 48",  "Plecak Osprey Kestrel 48 doskonale sprawdzi się na krótszych trekkingach, gdy w góry wybierasz się na kilka dni i chcesz spakować cały niezbędny ekwipunek. Jednak z tego modelu skorzystają także osoby podejmujące się turystyki lodowcowej, gdzie dochodzi konieczność zabierania ze sobą szpeju wspinaczkowego. Wynika to z jego niezwykle stabilnej konstrukcji oraz szeregu szpejarek, w które wepniesz wszego, czego potrzebujesz.", 77000,1),
(1, 4, "Plecak Osprey Kestrel 48",  "Plecak Osprey Kestrel 48 doskonale sprawdzi się na krótszych trekkingach, gdy w góry wybierasz się na kilka dni i chcesz spakować cały niezbędny ekwipunek. Jednak z tego modelu skorzystają także osoby podejmujące się turystyki lodowcowej, gdzie dochodzi konieczność zabierania ze sobą szpeju wspinaczkowego. Wynika to z jego niezwykle stabilnej konstrukcji oraz szeregu szpejarek, w które wepniesz wszego, czego potrzebujesz.", 78500,2);


--przykladowa rozmiarowka
INSERT INTO "size" ("size") VALUES
(0), (380), (385), (390), (395), (400), (405), (410), (415), (420), (425), (430), (435), (440), (445);

--zasoby magazunynu
INSERT INTO "stock" ("product", "size", "pieces") VALUES
(1, 2,9),
(1, 3,3),
(1, 4,12),
(1, 5,3),
(1, 6,6),
(1, 7,2),
(1, 8,1),

(2, 2,5),
(2, 3,4),
(2, 4,2),
(2, 5,2),
(2, 6,1),
(2, 8,1),

(3, 2,2),
(3, 3,1),
(3, 6,1),

(4, 5,2),
(4, 4,3),
(4, 7,5),
(4, 8,6),

(5, 3, 4),
(5, 5,2),
(5, 4,6),
(5, 6, 3),


(6, 1, 3),
(7, 1, 6),
(8, 1, 2),
(9, 1, 2),
(10, 1, 1);

INSERT INTO "client" ("first_name", "last_name", "NIP" ) VALUES("Przemysław", "Zagraniczny", "7742855054");

INSERT INTO "ptu" ("name", "value") VALUES
( 'A', 23),
( 'B', 8),
( 'C', 5),
( 'D', 0);

# --- !Downs

DELETE FROM "client";
DELETE FROM "stock";
DELETE FROM "size";
DELETE FROM "product";
DELETE FROM "color";
DELETE FROM "category";
DELETE FROM "discount";
DELETE FROM "ptu";