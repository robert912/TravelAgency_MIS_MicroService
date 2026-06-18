FROM quay.io/keycloak/keycloak:26.6.1

# Realm pre-importado: Keycloak lo carga con --import-realm al iniciar
COPY travel-realm-realm.json /opt/keycloak/data/import/

# Tema personalizado
COPY travelapp /opt/keycloak/themes/travelapp

EXPOSE 9090
CMD ["start-dev", "--import-realm", "--http-port=9090"]
