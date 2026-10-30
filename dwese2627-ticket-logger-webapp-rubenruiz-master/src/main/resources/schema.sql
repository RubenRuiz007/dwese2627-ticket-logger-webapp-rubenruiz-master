-- Crear tabla para las Comunidades Autónomas de España
CREATE TABLE IF NOT EXISTS regions
(
    id
    INT
    AUTO_INCREMENT
    PRIMARY
    KEY,
    code
    VARCHAR
(
    10
) NOT NULL UNIQUE,
    name VARCHAR
(
    100
) NOT NULL
    );

-- Crear tabla para las provincias españolas
CREATE TABLE IF NOT EXISTS provinces
(
    id
    INT
    AUTO_INCREMENT
    PRIMARY
    KEY,
    code
    VARCHAR
(
    10
) NOT NULL UNIQUE,
    name VARCHAR
(
    100
) NOT NULL,
    region_id INT NOT NULL,
    FOREIGN KEY
(
    region_id
) REFERENCES regions
(
    id
)
    );