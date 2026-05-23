FROM postgres:15-alpine

# Default database user, password, and database used by the backend.
# Override these at runtime as needed.
ENV POSTGRES_USER=techtrek
ENV POSTGRES_PASSWORD=secret
ENV POSTGRES_DB=techtrek

# Optional initialization script path. If you need extra DB setup,
# add SQL files to /docker-entrypoint-initdb.d.
COPY ./init.sql /docker-entrypoint-initdb.d/01-init.sql

EXPOSE 5432
