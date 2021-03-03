[![Java CI with Maven](https://github.com/Brest-Java-Course-2021/afedasiuk-hr/actions/workflows/maven.yml/badge.svg)](https://github.com/Brest-Java-Course-2021/afedasiuk-hr/actions/workflows/maven.yml)

# Human resources test project

![Java CI with Maven](https://github.com/Brest-Java-Course-2021/afedasiuk/workflows/Java%20CI%20with%20Maven/badge.svg)

# Brest Java Course 2021 (winter)


## Database examples

### Using Docker to simplify development (optional)

You can use Docker.
A number of docker-compose configuration are available in the [./docker](./docker) folder to launch required third party services.


### Postgresql

For example, to start a postgresql database in a docker container, run:

```
docker-compose -f docker/postgresql.yml up -d
```

To stop it and remove the container, run:

```
docker-compose -f docker/postgresql.yml down
```

### Oracle XE


Create Oracle XE image first, see:
[deliver-oracle-database-18c-express-edition-in-containers](https://blogs.oracle.com/oraclemagazine/deliver-oracle-database-18c-express-edition-in-containers)

For example, to start a Oracle XE database in a docker container, run:

sudo chown 54321:54321 ~/oracle/volumes/xedb/oradata


```
docker-compose -f docker/oraclexe.yml up -d
```

To stop it and remove the container, run:

```
docker-compose -f docker/oraclexe.yml down
```


## MySQL

```
docker-compose -f docker/mysql.yml up -d
```

To stop it and remove the container, run:

```
docker-compose -f docker/mysql.yml down
```


## MySQL Workbench

Install [https://dev.mysql.com/doc/workbench/en/wb-installing-linux.html](https://dev.mysql.com/doc/workbench/en/wb-installing-linux.html)

or use Docker:

```
docker-compose -f docker/mysql-workbench.yml up -d
```

To stop it and remove the container, run:

```
docker-compose -f docker/mysql-workbench.yml down
```

The application can be accessed at:

`http://localhost:3000/`

By default the user/pass is abc/abc, if you change your password or want to login manually to the GUI session for any reason use the following link:

`http://localhost:3000/?login=true`
