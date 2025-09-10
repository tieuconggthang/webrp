# napas-report-tidb

Spring Boot project (Java 17) connecting **TiDB** using the **MySQL JDBC driver**, package root `vn.napas.web.report`.

## 1) Prerequisites
- JDK 17
- Maven 3.9+
- A reachable TiDB endpoint (e.g. `tidb-host:4000`)

## 2) Configure
Edit `src/main/resources/application.yml`:
```yaml
spring:
  datasource:
    url: jdbc:mysql://TIDB_HOST:4000/your_db?... 
    username: root
    password: your_password
```

## 3) Run
```bash
mvn spring-boot:run
# or
mvn -DskipTests package && java -jar target/napas-report-tidb-1.0.0.jar
```

### Health check
```
GET http://localhost:8080/db/ping
# -> {"msg":"TiDB OK"}
```

### Dispute search (example body)
```
POST http://localhost:8080/dispute/search
Content-Type: application/json

{
  "vSystem": "ECOM",
  "vDisputeType": "TS",
  "vBusiness": "CRT",
  "vfrom_date": "01/08/2025",
  "vto_date":   "29/08/2025",
  "vfrom_time": "000000",
  "vto_time":   "235959",
  "vtranxID":   "",
  "vtranxREF":  "",
  "vPan":       "",
  "vBankID":    "",
  "viss":       "",
  "vacq":       "",
  "vTSV":       "",
  "vma_ts":     "",
  "vma_tl":     ""
}
```

## 4) Notes
- The repo expects two tables:
  - `ecom_gdts` (with a generated column `pan_mask` recommended).
  - `v_tsol_ecom` (a local table/view that mirrors your Oracle DB-link view).
- Optional index suggestions are in our chat. Use `ANALYZE TABLE` after loading data.

## 5) License
MIT
