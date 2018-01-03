# CDBT
CDBT is a Database tool for converting SQL data to YAML fixtures for use with the Play Framework.

SQL is exported as .csv, and with a configuration .yml file rules are applied to transform and anonymize the data.
Current functions include column rename and data replace, with a view to adding advanced functionality later.

Uses external libraries not included in git, place in CDBT/lib/

Apache commons:
commons-cli-1.4.jar
commons-io-2.6.jar

SnakeYaml:
snakyaml-1.19.jar