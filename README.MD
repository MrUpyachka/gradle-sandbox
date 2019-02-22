# General
Sandbox to test SSL connection with certificates

# Keystores

- Create server keystore with certificate
```
keytool -genkey -alias self-signed -storetype PKCS12 -keyalg RSA -keystore server_keystore.p12 -validity 3650 -dname "CN=localhost, OU=Sandbox, O=Sandbox, L=Sandbox, ST=ZZ, C=ZZ" -storepass server
```
- Export server certificate
```
keytool -export -alias self-signed -keystore server_keystore.p12 -file public-certificate -storepass server
```
- Create client keystore and import certificate
```
keytool -importcert -storetype PKCS12 -keystore client_keystore.p12 -alias public-certificate -storepass client -file public-certificate -noprompt
```
