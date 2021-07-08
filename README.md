# Internal alerts service

### Development notes

test commit

run local docker:

`docker run --name postgres11 -e POSTGRES_PASSWORD=postgres -p 5432:5432 -d postgres:11`


create schema :

`docker exec -it -u postgres postgres11 sh -c "psql -c 'create database internal_alerts'"`



        
