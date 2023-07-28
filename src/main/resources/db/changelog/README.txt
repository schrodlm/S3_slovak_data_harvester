V této složce se nachází soubory ke správnému fungování nástroje Liquibase.
Do složky changelogs se vkládají .sql soubory, které mění schéma databáze.
Tyto .sql scripty se vykonají při spuštění aplikace v abecedním pořadí

V rámci naší konvence se soubory .sql pojmenovávají alter_datum_komentář.sql.
Například "alter_20230322_add_column_to_table_something.sql"
Každý soubor by měl ideálně obsahovat jen jeden příkaz.

Při problémech s liquibase, nebo při nejasnostech či změnách
následující odkazy popisují liquibase konvenci.

References:
Jak se chovat k changesetům:
    https://www.liquibase.org/get-started/best-practices
Jak psát sql changesety:
    https://docs.liquibase.com/concepts/changelogs/sql-format.html
Liquibase ve Springu:
    https://www.baeldung.com/liquibase-refactor-schema-of-java-app
