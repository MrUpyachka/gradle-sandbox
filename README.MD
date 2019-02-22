# General
Sandbox to test SSL connection with certificates

# Keystores

- Create server keystore with certificate
```
keytool -genkeypair -keystore server_keystore.p12 -storetype PKCS12 -storepass server -alias self-signed -keyalg RSA -keysize 2048 -validity 365 -dname "CN=Test SSL certificate, OU=Sandbox, O=Sandbox, L=Sandbox, ST=Sandbox, C=ZZ" -ext san=dns:localhost,ip:127.0.0.1
```
- Export server certificate
```
keytool -exportcert -keystore server_keystore.p12 -storepass server -alias self-signed -rfc -file public-certificate.pem
```
- Create client keystore and import certificate
```
keytool -importcert -file public-certificate.pem -alias server -keystore client_keystore.p12 -storepass client
```