## Workshop

### Setups

- [Simple](./simple/)
- [Multi](./multi/)
- [Security](./security/)

### Notes

#### Keycloak

http://localhost:8082/realms/workshop/.well-known/openid-configuration

##### Clients

ehrbase_template_uploader:   MI6sN84CwM90RomVcD4x3LqKJZ66mfqX

ehrbase : mWvJLsDWkVutxxtAmSt3ZLyM0vdyoFO0

fhir-bridge: pYWxtFTPZhfx9E9wdtW8vLVpVGtVKzIk

```shell
curl \
  -d "client_id=fhir-bridge" \
  -d "client_secret=pYWxtFTPZhfx9E9wdtW8vLVpVGtVKzIk" \
  -d "grant_type=client_credentials" \
  "http://localhost:8082/realms/workshop/protocol/openid-connect/token"
```

#### Docker

````shell
docker stats
````

###### Running as Service

https://gist.github.com/mosquito/b23e1c1e5723a7fd9e6568e5cf91180f