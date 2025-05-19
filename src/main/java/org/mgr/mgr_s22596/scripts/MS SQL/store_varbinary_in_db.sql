-- stworzenie bazy
CREATE DATABASE StoreVARBINARY;
GO
USE StoreVARBINARY;
GO
--stworzenie tabeli
CREATE TABLE Example (
                         Opis NVARCHAR(100),
                         Wartosc VARBINARY(MAX)
);
--wstawienie liczby do tabeli jako VARBINARY(MAX)
INSERT INTO Example (Opis, Wartosc)
VALUES ('Liczba jako VARBINARY(MAX)', CAST(12345 AS VARBINARY(MAX)));
--odczyt danych z operacja CONVERT
SELECT
    Opis,
    CONVERT(INT, Wartosc) AS Liczba
FROM Example;