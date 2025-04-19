# Stage 1: Build-Phase für Node.js
FROM node:latest AS build

# Arbeitsverzeichnis erstellen und festlegen
WORKDIR /app

# Package-Dateien kopieren und Abhängigkeiten installieren
COPY package*.json ./
RUN npm install

# Quellcode kopieren und Build erstellen
COPY . .
RUN npm run build

FROM nginx:latest
COPY --from=build /app/build /usr/share/nginx/html
COPY config /etc/nginx