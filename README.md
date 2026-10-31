# 🌱 Cultivar

Progetto di studio full stack: un piccolo **catalogo e negozio di cultivar** (varietà di piante coltivate) con back-end Spring Boot, database Oracle, deploy su Docker e Kubernetes, e — in arrivo — un front-end e un **consigliere basato su intelligenza artificiale**.

## Cosa fa

**Catalogo cultivar e terreni**
- Gestione delle cultivar (inserimento, modifica, ricerca) collegate al loro tipo di terreno
- **Cultivar consigliate**: dato un terreno e un pH, restituisce le cultivar adatte, ordinate dalla più sicura alla più al limite
- **Riepilogo per terreno**: per ogni terreno, numero di cultivar, pH massimo medio e nomi delle cultivar (anche i terreni senza cultivar)
- Statistiche per specie e per terreno, tolleranza al pH, classifiche

**Vivaio**
- Catalogo di prodotti (piante, attrezzi, semi) con schede, prezzi scontati e riepiloghi per categoria
- Prodotti da riordinare
- **Acquisto** di una pianta con controlli (quantità, scorte) e scontrino

## Tecnologie

| Area | Tecnologie |
|---|---|
| Linguaggio | **Java 25** (record, `sealed`, pattern matching, stream e lambda) |
| Framework | **Spring Boot 4**, Spring Web, Spring Data JPA / Hibernate, Bean Validation |
| Database | **Oracle Database** (profilo `oracle`) e **H2** in memoria (sviluppo) |
| Mapping e utilità | MapStruct, Lombok |
| Documentazione API | SpringDoc OpenAPI / **Swagger UI** |
| Test | JUnit 5, Mockito |
| Build | Maven (wrapper incluso) |
| Container | **Docker**, Docker Compose |
| Orchestrazione | **Kubernetes** (manifest nella cartella `k8s/`, provato in locale con kind) |

## Avviare il progetto

### Con H2 (nessun database da installare)

```bash
./mvnw spring-boot:run
```

Swagger: http://localhost:8080/swagger-ui.html — console H2: http://localhost:8080/h2-console

### Con Oracle

Il profilo `oracle` legge utente e password da **variabili d'ambiente** (nessuna password nel codice):

```bash
export SPRING_PROFILES_ACTIVE=oracle
export DB_USERNAME=<utente>
export DB_PASSWORD=<password>
./mvnw spring-boot:run
```

Lo schema del database non viene creato dall'applicazione: Hibernate lo valida soltanto (`ddl-auto=validate`).

### Con Docker

```bash
./mvnw clean package
docker build -t cultivar:1.0 .
docker run -p 8080:8080 cultivar:1.0
```

### Su Kubernetes (cluster locale con kind)

```bash
kind create cluster
kind load docker-image cultivar:1.0
kubectl apply -f k8s/
kubectl port-forward service/cultivar 9090:8080
```

## Roadmap

- [ ] **Front-end**: un piccolo negozio di cultivar che usa le API del back-end (framework da definire)
- [ ] **Consigliere AI**: l'utente descrive il suo terreno e le sue condizioni, il back-end chiede a **Claude** (API di Anthropic) e risponde con cultivar del catalogo, usando i dati reali. Il front-end non chiama mai direttamente l'API di Claude: la chiave resta sul server, in una variabile d'ambiente o in un Secret di Kubernetes
- [ ] Docker Compose con applicazione + Oracle
- [ ] Deploy su Kubernetes collegato a Oracle (ConfigMap e Secret)
- [ ] Strumenti di sviluppo con Claude Code: `CLAUDE.md`, skill e subagenti

## Autrice

**Martina** — sviluppatrice Java ([@1Martina1](https://github.com/1Martina1))
