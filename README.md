# Hinweise zum Programmierbeispiel

[Juergen Zimmermann](mailto:Juergen.Zimmermann@h-ka.de)

> Bevor man mit der Projektarbeit an der 2. Abgabe beginnt, sichert man sich
> die 1. Abgabe, u.a. weil für die 2. Abgabe auch die Original-Implementierung
> aus der 1. Abgabe benötigt wird.

Inhalt

- [Eigener Namespace in Kubernetes](#eigener-namespace-in-kubernetes)
- [Relationale Datenbanksysteme](#relationale-datenbanksysteme)
  - [PostgreSQL](#postgresql)
  - [MySQL](#mySQL)
  - [Oracle](#oracle)
- [Übersetzung und lokale Ausführung](#übersetzung-und-lokale-ausführung)
  - [Ausführung in IntelliJ IDEA](#ausführung-in-intellij-idea)
  - [Start und Stop des Servers in der Kommandozeile](#start-und-stop-des-servers-in-der-kommandozeile)
  - [Image erstellen](#image-erstellen)
- [Postman](#postman)
- [OpenAPI mit Swagger](#openapi-mit-swagger)
- [Unit Tests und Integrationstests](#unit-tests-und-integrationstests)
- [Rechnername in der Datei hosts](#rechnername-in-der-datei-hosts)
- [Kubernetes, Helm und Skaffold](#kubernetes-helm-und-skaffold)
  - [WICHTIG: Schreibrechte für die Logdatei](#wichtig-schreibrechte-für-die-logdatei)
  - [Helm als Package Manager für Kubernetes](#helm-als-package-manager-für-kubernetes)
  - [Installation mit helmfile und Port Forwarding](#installation-mit-helmfile-und-port-forwarding)
  - [Continuous Deployment mit Skaffold](#continuous-deployment-mit-skaffold)
  - [kubectl top](#kubectl-top)
  - [Validierung der Installation](#validierung-der-installation)
- [Statische Codeanalyse](#statische-codeanalyse)
  - [Checkstyle und SpotBugs](#checkstyle-und-spotbugs)
  - [SonarQube](#sonarqube)
- [Analyse von Sicherheitslücken](#analyse-von-sicherheitslücken)
  - [OWASP Security Check](#owasp-security-check)
  - [Docker Scout](#docker-scout)
  - [Trivy von Aquasec](#trivy-von-aquasec)
- [Dokumentation](#dokumentation)
  - [Dokumentation durch AsciiDoctor und PlantUML](#dokumentation)
  - [API Dokumentation durch javadoc](#api-dokumentation-durch-javadoc)

---

## Eigener Namespace in Kubernetes

Genauso wie in Datenbanksystemen gibt es in Kubernetes _keine_ untergeordneten
Namespaces. Vor allem ist es in Kubernetes empfehlenswert für die eigene
Software einen _neuen_ Namespace anzulegen und __NICHT__ den Default-Namespace
zu benutzen. Das wurde bei der Installation von Kubernetes durch den eigenen
Namespace `acme` bereits erledigt. Außerdem wurde aus Sicherheitsgründen beim
defaultmäßigen Service-Account das Feature _Automounting_ deaktiviert und der
Kubernetes-Cluster wurde intern defaultmäßig so abgesichert, dass

- über das Ingress-Gateway keine Requests von anderen Kubernetes-Services zulässig sind
- über das Egress-Gateway keine Requests an andere Kubernetes-Services zulässig sind.

---

## Relationale Datenbanksysteme

### PostgreSQL

#### Docker Compose für PostgreSQL und pgadmin

Wenn man den eigenen Microservice direkt mit Windows - nicht mit Kubernetes -
laufen lässt, kann man PostgreSQL und das Administrationswerkzeug pgadmin
einfach mit _Docker Compose_ starten und später auch herunterfahren.

> ❗ Vor dem 1. Start von PostgreSQL muss man das Skript `create-db-kunde.sql`
> aus dem Verzeichnis `extras\db\postgres\sql` nach
> `C:\Zimmermann\volumes\postgres\sql` kopieren und die Anleitung ausführen.
> Für die Windows-Verzeichnisse `C:\Zimmermann\volumes\postgres\data`,
> `C:\Zimmermann\volumes\postgres\tablespace` und
> `C:\Zimmermann\volumes\postgres\tablespace\kunde` muss außerdem Vollzugriff
> gewährt werden, was über das Kontextmenü mit _Eigenschaften_ und den
> Karteireiter _Sicherheit_ für die Windows-Gruppe _Benutzer_ eingerichtet
> werden kann.
> Übrigens ist das Emoji für das Ausrufezeichen von https://emojipedia.org.

```powershell
    cd extras\compose\postgres
    # In extras\compose\postgres\compose.yml:
    # Zeile mit "command:" und nachfolgende bis einschliesslich "]" auskommentieren
    #   damit der PostgreSQL-Server ohne TLS gestartet wird
    # bei den Listenelemente unterhalb von "volumes:" die Zeilen mit "read_only:" bei private-key.pem und certificate.crt auskommentieren
    #   damit die Zugriffsrechte fuer den privaten Schluessel und das Zertifikat gesetzt werden koennen
    # Zeile mit "user:" auskommentieren
    #   damit der PostgreSQL-Server implizit mit dem Linux-User "root" gestartet wird
    docker compose up

    # 2. Shell: DB, DB-User und Tablespace anlegen
    cd extras\compose\postgres
    docker compose exec postgres bash
        chown postgres:postgres /var/lib/postgresql/tablespace
        chown postgres:postgres /var/lib/postgresql/tablespace/kunde
        chown postgres:postgres /var/lib/postgresql/private-key.pem
        chown postgres:postgres /var/lib/postgresql/certificate.crt
        chmod 600 /var/lib/postgresql/private-key.pem
        chmod 600 /var/lib/postgresql/certificate.crt
        exit
    docker compose down
    # in compose.yml die obigen Kommentare wieder entfernen, d.h.
    #   PostgreSQL-Server mit TLS starten
    #   private-key.pem und certificate.crt als readonly
    #   den Linux-User "postgres" wieder aktivieren
    # 1. Shell:
    docker compose up postgres
    # 2. Shell:
    docker compose exec postgres bash
        psql --dbname=postgres --username=postgres --file=/sql/create-db-kunde.sql
        psql --dbname=kunde --username=kunde --file=/sql/create-schema-kunde.sql
        exit
    docker compose down
```

Der Name des Docker-Containers lautet `postgres` und ebenso lautet der
_virtuelle Rechnername_ `postgres`. Der virtuelle Rechnername `postgres`
wird später auch als Service-Name für PostgreSQL in Kubernetes verwendet.
Der neue Datenbank-User `kunde` wurde zum Owner der Datenbank `kunde`.

Statt eine PowerShell zu verwenden, kann man Docker Compose auch direkt in
IntelliJ aufrufen, indem man mit der rechten Maustaste `compose.postgres.yml`
anklickt und den Menüpunkt `Run compose/compose.post...` auswählt.

Jetzt läuft der PostgreSQL- bzw. DB-Server. Die Datenbank-URL für den eigenen
Microservice als DB-Client lautet: `postgresql://localhost/kunde`, dabei ist
`localhost` aus Windows-Sicht der Rechnername, der Port defaultmäßig `5432`
und der Datenbankname `kunde`.

Außerdem kann _pgadmin_ zur Administration verwendet werden. pgadmin läuft
ebenfalls als Docker-Container und ist über ein virtuelles Netzwerk mit dem
Docker-Container des DB-Servers verbunden. Deshalb muss beim Verbinden mit dem
DB-Server auch der virtuelle Rechnername `postgres` statt `localhost` verwendet
werden. pgadmin kann man mit einem Webbrowser und der URL `http://localhost:8888`
aufrufen. Die Emailadresse `pgadmin@acme.com` und das Passwort `p` sind voreingestellt.
Da pgadmin ist übrigens mit Chromium implementiert ist.

Beim 1. Einloggen konfiguriert man einen Server-Eintrag mit z.B. dem Namen
`localhost` und verwendet folgende Werte:

- Host: `postgres` (virtueller Rechnername des DB-Servers im Docker-Netzwerk.
  __BEACHTE__: `localhost` ist im virtuellen Netzwerk der Name des
  pgadmin-Containers selbst !!!)
- Port: `5432` (Defaultwert)
- Username: `postgres` (Superuser beim DB-Server)
- Password: `p`

Es empfiehlt sich, das Passwort abzuspeichern, damit man es künftig nicht jedes
Mal beim Einloggen eingeben muss.

#### helmfile für PostgreSQL und pgadmin

Wenn der eigene Microservice in Kubernetes gestartet werden soll (s.u.), muss
_PostgreSQL_ zuvor in Kubernetes gestartet werden, was mit _helmfile_ gemacht
werden kann. Nachdem PostgreSQL in Kubernetes gestartet ist, ist ein manuelles
Port-Forwarding noch notwendig.

```powershell
    cd extras\kubernetes\postgres
    helmfile apply
    .\port-forward.ps1

    # Deinstallieren
    helmfile destroy
    .\delete-pvc.ps1
```

Das PowerShell-Skript `delete-pvc.ps1` im Verzeichnis `extras\kubernetes\postgres`
löscht die verbleibenden _PersistentVolumeClaims_.

#### Skaffold für PostgreSQL und pgadmin

Statt [helmfile](#helmfile-für-postgresql-und-pgadmin) kann auch Skaffold verwendet
werden. Wenn die Umgebungsvariable `SKAFFOLD_PROFILE` auf den Wert `dev`
gesetzt ist, dann wird das Profile `dev` verwendet, welches bei Helm zusätzlich
die Datei `dev.yaml` verwendet. Bis der Endpoint für PostgreSQL aktiviert ist,
muss man ein bisschen warten.

```powershell
    cd extras\kubernetes\postgres
    skaffold dev --no-prune=false --cache-artifacts=false
    <Strg>C
    skaffold delete
```

Dabei wurde auch das Administrationswerkzeug _pgadmin_ innerhalb von Kubernetes
gestartet und kann wegen Port-Forwarding mit `http://localhost:8888` aufgerufen
werden.

Mit `<Strg>C` kann die Installation wieder zurückgerollt werden und man ruft
abschließend `skaffold delete` auf.

Ohne die beiden Optionen muss man noch manuell die _PersistentVolumeClaims_
löschen, da bei `metadata.finalizers` der Wert auf `kubernetes.io/pvc-protection`
gesetzt ist und auch durch `kubectl patch pvc <PVC_NAME> -p '{"metadata":{"finalizers": []}}' --type=merge`
nicht entfernt werden kann. Dazu gibt es das PowerShell-Skript `delete-pvc.ps1`
im Verzeichnis `extras\kubernetes\postgres`.

---

### MySQL

#### Docker Compose für MySQL und phpMyAdmin

Wenn man den eigenen Microservice direkt mit Windows - nicht mit Kubernetes -
laufen lässt, kann man MySQL und das Administrationswerkzeug phpMyAdmin einfach
mit _Docker Compose_ starten und später auch herunterfahren.

> ❗ Vor dem 1. Start von MySQL muss man das Skript `create-db-kunde.sql` aus
> dem Projektverzeichnis `extras\db\mysql\sql` nach
> `C:\Zimmermann\volumes\mysql\sql` kopieren und die Anleitung ausführen.
> Dabei wird der DB-User `kunde` und dessen Datenbank `kunde` angelegt, d.h.
> der neue Datenbank-User `kunde` wird zum Owner der Datenbank `kunde`.
> Dazu muss man sich mit dem Docker-Container mit Namen `mysql` verbinden und
> im Docker-Container das SQL-Skript ausführen:

```powershell
    cd extras\compose\mysql
    docker compose up mysql

    # 2. Shell: DB-User "kunde" und dessen Datenbank "kunde" anlegen
    cd extras\compose\mysql
    docker compose exec mysql sh
        mysql --user=root --password=p < /sql/create-db-kunde.sql
        exit
    docker compose mysql down
```

Statt eine PowerShell zu verwenden, kann man Docker Compose auch direkt in
IntelliJ aufrufen, indem man mit der rechten Maustaste `compose.mysql.yml`
anklickt und den Menüpunkt `Run compose/compose.mysq...` auswählt.

Jetzt läuft der DB-Server. Die Datenbank-URL für den eigenen Microservice als
DB-Client lautet: `mysql://localhost/kunde`. Dabei ist `localhost` aus
Windows-Sicht der Rechnername, der Port defaultmäßig `3306` und der
Datenbankname `kunde`.

Außerdem kann _phpMyAdmin_ oder _dbeaver_ zur Administration verwendet werden.
phpMyAdmin läuft ebenfalls als Docker-Container und ist über ein virtuelles
Netzwerk mit dem Docker-Container des DB-Servers verbunden. Deshalb muss beim
Verbinden mit dem DB-Server auch der virtuelle Rechnername `mysql` verwendet werden.
phpMyAdmin ruft man mit einem Webbrowser und der URL `http://localhost:8889`
auf. Zum Einloggen verwendet man folgende Werte:

- Server: `mysql` (virtueller Rechnername des DB-Servers im Docker-Netzwerk.
  __BEACHTE__: `localhost` ist im virtuellen Netzwerk der Name des
  phpMyAdmin-Containers selbst !!!)
- Benutzername: `root` (Superuser beim DB-Server)
- Password: `p`

#### helmfile für MySQL und phpMyAdmin

Wenn der eigene Microservice in Kubernetes gestartet werden soll (s.u.), muss
_MySQL_ zuvor in Kubernetes gestartet werden, was mit _helmfile_ gemacht
werden kann. Nachdem MySQL in Kubernetes gestartet ist, ist ein manuelles
Port-Forwarding noch notwendig.

```powershell
    cd extras\kubernetes\mysql
    helmfile apply
    .\port-forward.ps1

    # Deinstallieren
    helmfile destroy
    .\delete-pvc.ps1
```

Das PowerShell-Skript `delete-pvc.ps1` im Verzeichnis `extras\kubernetes\mysql`
löscht die verbleibenden _PersistentVolumeClaims_.

#### Skaffold für MySQL und phpMyAdmin

Statt [helmfile](#helmfile-für-mysql-und-phpmyadmin) kann auch Skaffold verwendet
werden. Wenn die Umgebungsvariable `SKAFFOLD_PROFILE` auf den Wert `dev`
gesetzt ist, dann wird das Profile `dev` verwendet, welches bei Helm zusätzlich
die Datei `dev.yaml` verwendet. Bis der Endpoint für MySQL aktiviert ist,
muss man ein bisschen warten.

```powershell
    cd extras\kubernetes\mysql
    skaffold dev --no-prune=false --cache-artifacts=false
    <Strg>C
    skaffold delete
```

Dabei wurde auch das Administrationswerkzeug _phpMyAdmin_ innerhalb von Kubernetes
gestartet und kann wegen Port-Forwarding mit `http://localhost:8889` aufgerufen
werden.

Mit `<Strg>C` kann die Installation wieder zurückgerollt werden.

Ohne die beiden Optionen muss man noch manuell die _PersistentVolumeClaims_
löschen, da bei `metadata.finalizers` der Wert auf `kubernetes.io/pvc-protection`
gesetzt ist und auch durch `kubectl patch pvc <PVC_NAME> -p '{"metadata":{"finalizers": []}}' --type=merge`
nicht entfernt werden kann. Dazu gibt es das PowerShell-Skript `delete-pvc.ps1`
im Verzeichnis `extras\kubernetes\mysql`.

---

### Oracle

#### Docker Compose für Oracle

Wenn man den eigenen Microservice direkt mit Windows - nicht mit Kubernetes -
laufen lässt, kann man Oracle einfach mit _Docker Compose_ starten und später
auch herunterfahren.

> ❗ Das erstmalige Hochfahren von Oracle XE kann einige Minuten dauern.
> Dabei werden auch die beiden üblichen Oracle-User `SYS` und `SYSTEM` jeweils
> mit dem Passwort `p` angelegt.

```powershell
    cd extras\compose\oracle
    docker compose up

    # 2. Shell: DB-User "kunde" und dessen Datenbank "kunde" anlegen
    cd extras\compose
    docker compose exec db bash
        sqlplus SYS/p@FREEPDB1 as SYSDBA '@/sql/create-user-tablespace-kunde.sql'
        sqlplus kunde/p@FREEPDB1 '@/sql/create-schema-kunde.sql'
        exit
    docker compose down
```

Der Name des Docker-Containers und des _virtuellen Rechners_ lautet `oracle`.
Der virtuelle Rechnername wird später auch als Service-Name für
Oracle in Kubernetes verwendet.

> ❗ Nach dem 1. Start des DB-Servers muss man einmalig den Datenbank-User
> `kunde`, den Tablespace `kundespace` und das Schema `kunde` für den gleichnamigen
> User anlegen (s.o.).

Statt eine PowerShell zu verwenden, kann man Docker Compose auch direkt in
IntelliJ aufrufen, indem man mit der rechten Maustaste `compose.oracle.yml` anklickt
und den Menüpunkt `Run compose/compose.orac...` auswählt.

Die Datenbank-URL für den eigenen Microservice und auch für _SQL Developer_
als grafischen DB-Client lautet: `oracle:thin:kunde/p@localhost/FREEPDB1`.
Dabei ist

- `kunde` der Benutzername,
- `p` das Passwort
- `localhost` aus Windows-Sicht der Rechnername
- der Port defaultmäßig `1521` und
- `FREEPDB1` (PDB = Portable Database) der Name der Default-Datenbank nach dem 1. Start.

---

## Übersetzung und lokale Ausführung

### Ausführung in IntelliJ IDEA

Bei Gradle: Am rechten Rand auf den Button _Gradle_ klicken und in _Tasks_ > _application_
durch einen Doppelklick auf _bootRun_ starten.

Bei Maven: Am rechten Rand auf den Button _Maven_ klicken und innerhalb vom Projekt
_Plugins_ > _spring-boot_ durch einen Doppelklick auf _spring-boot:run_ starten.

Danach gibt es bei Gradle in der Titelleiste am oberen Rand den Eintrag _kunde [bootRun]_
im Auswahlmenü und man kann von nun an den Server auch damit (neu-) starten,
stoppen und ggf. debuggen.

---

### Start und Stop des Servers in der Kommandozeile

Nachdem der DB-Server gestartet wurde, kann man in einer Powershell den Server
mit dem Profil `dev` starten. Standardmäßig wird dabei PostgreSQL verwendet.
Wenn man MySQL, Oracle oder H2 verwenden möchte, muss man in `src\main\resourcces\application.yml`
die Property `spring.datasource.url` entsprechend setzen.

```powershell
    # Gradle:
    .\gradlew bootRun

    # Maven:
    .\mvnw spring-boot:run
```

Mit `<Strg>C` kann man den Server herunterfahren, weil in `application.yml`
auch die Property für _graceful shutdown_ konfiguriert ist.

Außerdem kann man in `application.yml`

* die Property `server.port` auf z.B. `8081` setzen, um den voreingestellten Port
  `8080` umzukonfigurieren,
* die Property `server.ssl.bundle` auskommentieren, um den Server ohne TLS laufen zu lassen.

---

### Image erstellen

Bei Verwendung der Buildpacks werden ggf. einige Archive von Github heruntergeladen,
wofür es leider kein Caching gibt. Ein solches Image kann mit dem Linux-User `cnb`
gestartet werden. Mit der Task bootBuildImage kann man im Verzeichnis für das
Projekt "bestellung" ebenfalls ein Docker-Image erstellen.

```powershell
    # Gradle und Buildpacks mit Bellsoft Liberica
    .\gradlew bootBuildImage

    # Maven und Buildpacks mit Bellsoft Liberica
    .\mvnw spring-boot:build-image -D'maven.test.skip=true'

    # Eclipse Temurin mit Ubuntu Jammy (2022.04) als Basis-Image
    docker build --tag=juergenzimmermann/kunde:2024.04.0-eclipse .

    # Azul Zulu mit Ubuntu Jammy (2022.04) als Basis-Image
    docker build --tag=juergenzimmermann/kunde:2024.04.0-azul --file=Dockerfile.azul .
```

Mit dem Unterkommando `inspect` von docker kann man die Metadaten, z.B. Labels,
zu einem Image inspizieren:

```powershell
    docker inspect juergenzimmermann/kunde:2024.04.1-buildpacks-bellsoft
```

Mit _dive_ kann man dann ein Docker-Image und die einzelnen Layer inspizieren:

```powershell
    cd extras
    # Image mit Buildpacks sowie Bellsoft Liberica und Ubuntu Jammy als Basis-Image
    .\dive.ps1
```

Statt _dive_ kann man auch das "Tool Window" _Services_ von IntelliJ IDEA verwenden.

Mit der PowerShell kann man Docker-Images folgendermaßen auflisten und löschen,
wobei das Unterkommando `rmi` die Kurzform für `image rm` ist:

```powershell
    docker images | sort
    docker rmi myImage:myTag
```

Im Laufe der Zeit kann es immer wieder Images geben, bei denen der Name
und/oder das Tag `<none>` ist, sodass das Image nicht mehr verwendbar und
deshalb nutzlos ist. Solche Images kann man mit dem nachfolgenden Kommando
herausfiltern und dann unter Verwendung ihrer Image-ID, z.B. `9dd7541706f0`
löschen:

```powershell
    docker rmi 9dd7541706f0
```

### Docker Compose für einen Container mit dem eigenen Server

Wenn das Image gebaut ist, kann man durch _Docker Compose_ die Services für
den DB-Server, den DB-Browser und den eigenen Microservice auf einmal starten.
Dabei ist der Service _kunde_ so konfiguriert, dass er erst dann gestartet wird,
wenn der "healthcheck" des DB-Servers "ready" meldet.

```powershell
    cd extras\compose\kunde

    # PowerShell fuer kunde einschl. DB-Server und Mailserver
    # Image mit Cloud-Native Buildpacks und z.B. Bellsoft Liberica (siehe compose.yml)
    docker compose up

    # Nur zur Fehlersuche bei z.B. Buildpacks: weitere PowerShell für bash
    cd extras\compose\kunde
    docker compose exec kunde bash
        id
        ps -ef
        env
        ls -l /layers
        ls -l /layers/paketo-buildpacks_bellsoft-liberica/jre
        #ls -l /layers/paketo-buildpacks_adoptium/jre
        #ls -l /layers/paketo-buildpacks_azul-zulu/jre
        pwd
        hostname
        cat /etc/os-release
        exit

    # Fehlersuche im Netzwerk:
    docker compose -f compose.busybox.yml up
    docker compose exec busybox sh
        nslookup postgres
        exit

    # 2. Powershell: kunde einschl. DB-Server und Mailserver herunterfahren
    docker compose down
```

Wenn nicht PostgreSQL verwendet wird, so müssen in `compose.env` die
Umgebungsvariablen für MySQL, Oracle und H2 entsprechend gesetzt werden.

Der eigene Server wird mit `docker compose down` einschließlich DB-Server
heruntergefahren, weil beide über ein virtuelles Netz verbunden sein müssen
und deshalb die YAML-Datei für den DB-Server in `compose.yml` inkludiert
wird. Will man nur den Service _kunde_ ohne den DB-Server herunterfahren, so
lautet das Kommando: `docker compose down kunde`.

## Postman

Im Verzeichnis `extras\postman` gibt es Dateien für den Import in Postman.
Zuerst importiert man die Datei `*_environment.json`, um Umgebungsvariable
anzulegen, und danach die Dateien `*_collection.json`, um Collections für Requests
anzulegen.

---

### OpenAPI mit Swagger

Mit der URL `https://localhost:8080/swagger-ui.html` kann man in einem
Webbrowser den RESTful Web Service über eine Weboberfläche nutzen, die
von _Swagger_ auf der Basis von der Spezifikation _OpenAPI_ generiert wurde.
Die _Swagger JSON Datei_ kann man mit `https://localhost:8080/v3/api-docs`
abrufen.

## Unit Tests und Integrationstests

Wenn der DB-Server erfolgreich gestartet ist, können auch die Unit- und
Integrationstests gestartet werden.

```powershell
    # Gradle
    .\gradlew test

    # Maven
    .\mvnw test jacoco:report
```

__WICHTIGER__ Hinweis zu den Tests für den zweiten Microservice, der den ersten
Microservice aufruft: Da die Tests direkt mit Windows laufen, muss Port-Forwarding
für den aufzurufenden, ersten Microservice gestartet sein, falls dieser in Kubernetes
läuft.

Um das Testergebnis mit _Allure_ zu inspizieren, ruft man in Gradle einmalig
`.\gradlew downloadAllure` auf. Fortan kann man den generierten Webauftritt mit
den Testergebnissen folgendermaßen aufrufen:

```powershell
    # Gradle
    .\gradlew allureServe

    # Maven (leerer Report!):
    mkdir target\allure-results
    mvn allure:report
```

---

## Rechnername in der Datei hosts

Wenn man mit Kubernetes arbeitet, bedeutet das auch, dass man i.a. über TCP
kommuniziert. Deshalb sollte man überprüfen, ob in der Datei
`C:\Windows\System32\drivers\etc\hosts` der eigene Rechnername mit seiner
IP-Adresse eingetragen ist. Zum Editieren dieser Datei sind Administrator-Rechte
notwendig.

---

## Kubernetes, Helm und Skaffold

### WICHTIG: Schreibrechte für die Logdatei

Wenn die Anwendung in Kubernetes läuft, ist die Log-Datei `application.log` im
Verzeichnis `C:\Zimmermann\volumes\kunde-v2`. Das bedeutet auch zwangsläufig,
dass diese Datei durch den _Linux-User_ vom (Kubernetes-) Pod angelegt und
geschrieben wird, wozu die erforderlichen Berechtigungen in Windows gegeben
sein müssen.

Wenn man z.B. die Anwendung zuerst mittels _Cloud Native Buildpacks_ laufen
lässt, dann wird `application.log` vom Linux-User `cnb` erstellt.

### Helm als Package Manager für Kubernetes

_Helm_ ist ein _Package Manager_ für Kubernetes mit einem _Template_-Mechanismus
auf der Basis von _Go_.

Zunächst muss man z.B. mit dem Gradle- oder Maven-Plugin von Spring Boot ein
Docker-Image erstellen ([s.o.](#image-erstellen)).

Die Konfiguration für Helm ist im Unterverzeichnis `extras\kubernetes\kunde`.
Die Metadaten für das _Helm-Chart_ sind in der Default-Datei `Chart.yaml` und
die einzelnen Manifest-Dateien für das Helm-Chart sind im Unterverzeichis
`templates` im Format YAML. In diesen Dateien gibt es Platzhalter ("templates")
mit der Syntax der Programmiersprache Go. Die Defaultwerte für diese Platzhalter
sind in der Default-Datei `values.yaml` und können beim Installieren durch weitere
YAML-Dateien überschrieben werden. Im unten stehenden Beispiel wird so ein
_Helm-Service_ dem _Release-Namen_ kunde mit dem Helm-Chart `Chart.yaml` aus
dem aktuellen Verzeicnis in Kubernetes installiert. Dabei muss die Umgebungsvariable
`HELM_NAMESPACE` auf den Wert `acme` gesetzt sein.

```powershell
    # Ueberpruefen, ob die Umgebungsvariable HELM_NAMESPACE gesetzt ist:
    Write-Output $env:HELM_NAMESPACE

    cd extras\kubernetes\kunde
    helm lint --strict .
    helm-docs

    # einfacher: helmfile oder Skaffold
    helm install kunde . -f values.yaml -f dev.yaml
    helm list
    helm status kunde

    # MySQL statt PostgreSQL:
    helm install kunde . -f values.yaml -f dev.yaml -f dev-mysql.yaml

    # H2 statt PostgreSQL:
    helm install kunde . -f values.yaml -f dev.yaml -f dev-h2.yaml
```

Später kann das Helm-Chart mit dem Release-Namen _kunde_ auch deinstalliert werden:

```powershell
    cd extras\kubernetes\kunde
    helm uninstall kunde
```

### Installation mit helmfile und Port Forwarding

```powershell
    helmfile apply

    helm list
    helm status kunde

    kubectl describe svc/kunde -n acme
    # in Lens: Network > Endpoints
    kubectl get ep -n acme

    .\port-forward.ps1

    # Deinstallieren
    helmfile destroy
```

Wenn _MySQL_ statt _PostgreSQL_ verwendet werden soll, muss man in der Datei
`helmfile` in der Zeile mit `dev-mysql.yaml` den Kommentar entfernen und die
andere Zeile mit `values:` auskommentieren. Analog für H2 statt PostgreSQL.

Gegenüber Skaffold (s.u.) hat helmfile allerdings folgende __Defizite__:

- helmfile funktioniert nur mit Helm, aber nicht mit _Kustomize_, _kubectl_, _kpt_
- Continuous Deployment wird nicht unterstützt
- Die Konsole des Kubernetes-Pods sieht man nicht in der aufrufenden PowerShell.
- Port-Forwarding muss man selbst einrichten bzw. aufrufen

Um beim Entwickeln von localhost (und damit von außen) auf einen
Kubernetes-Service zuzugreifen, ist _Port-Forwarding_ die einfachste
Möglichkeit, indem das nachfolgende Kommando für den installierten Service mit
Name _kunde_ aufgerufen wird. Alternativ kann auch das Skript `port-forward.ps1`
aufgerufen werden.

```powershell
    kubectl port-forward service/kunde 8080 --namespace acme
```

Nach dem Port-Forwarding kann man auf den in Kubernetes laufenden Service zugreifen:

- _Postman_
- Cmdlet `Invoke-WebRequest` von PowerShell
- _cURL_

Ein _Ingress Controller_ ist zuständig für das _Traffic Management_ bzw. Routing
der eingehenden Requests zu den Kubernetes Services. Ein solcher Ingress Controller
wurde durch `extras\kubernetes\kunde\templates\ingress.yaml` installiert und kann von
außen z.B. folgendermaßen aufgerufen werden, falls der eigentliche Kommunikationsendpunkt
in Kubernetes verfügbar ist.

```powershell
    # ca. 2. Min. warten, bis der Endpoint bei kunde verfuegbar ist (in Lens: Network > Endpoints)
    kubectl get ep -n acme

    $secpasswd = ConvertTo-SecureString p -AsPlainText -Force
    $credential = New-Object System.Management.Automation.PSCredential('admin', $secpasswd)

    # GET-Request fuer REST-Schnittstelle mit Invoke-WebRequest:
    $response = Invoke-WebRequest https://kubernetes.docker.internal/kunden/00000000-0000-0000-0000-000000000001 `
        -Headers @{Accept = 'application/hal+json'} `
        -SslProtocol Tls13 -HttpVersion 2 -SkipCertificateCheck `
        -Authentication Basic -Credential $credential
    Write-Output $response.RawContent

    # GraphQL mit Invoke-WebRequest:
    $response = Invoke-WebRequest https://kubernetes.docker.internal/kunden/graphql `
        -Method Post -Body '{"query": "query { kunde(id: \"00000000-0000-0000-0000-000000000001\") { nachname } }"}' `
        -ContentType 'application/json' `
        -SslProtocol Tls13 -HttpVersion 2 -SkipCertificateCheck `
        -Authentication Basic -Credential $credential
    Write-Output $response.RawContent

    # GET-Request fuer REST-Schnittstelle mit cURL:
    curl --verbose --user admin:p --tlsv1.3 --http2 --insecure https://kubernetes.docker.internal/kunden/00000000-0000-0000-0000-000000000001

    # GraphQL mit cURL:
    curl --verbose --data '{"query": "query { kunde(id: \"00000000-0000-0000-0000-000000000001\") { nachname } }"}' `
        --header 'Content-Type: application/json' `
        --tlsv1.3 --insecure `
        --user admin:p `
        https://kubernetes.docker.internal/kunden/graphql
```

### Continuous Deployment mit Skaffold

_Skaffold_ ist ein Werkzeug, das den eigenen Quellcode beobachtet. Wenn Skaffold
Änderungen festestellt, wird das Image automatisch neu gebaut und ein Redeployment
in Kubernetes durchgeführt.

Um das Image mit dem Tag `2024.04.1-buildpacks-bellsoft` zu bauen, muss die Umgebungsvariable
`TAG` auf den Wert `2024.04.1-buildpacks-bellsoft` gesetzt werden. Dabei ist auf die Großschreibung
bei der Umgebungsvariablen zu achten.

In `skaffold.yaml` ist konfiguriert, dass das Image mit _Cloud Native
Buildpacks_ gebaut wird.

Weiterhin gibt es in Skaffold die Möglichkeit, _Profile_ zu definieren, um z.B.
verschiedene Werte bei der Installation mit Helm zu verwenden. Dazu ist in
skaffold.yaml beispielsweise konfiguriert, dass die Umgebungsvariable
`SKAFFOLD_PROFILE` auf `dev` gesetzt sein muss, um bei Helm zusätzlich die Datei
`dev.yaml` zu verwenden.

Das Deployment wird mit Skaffold nun folgendermaßen durchgeführt und kann mit
`<Strg>C` abgebrochen bzw. zurückgerollt werden:

```powershell
    $env:TAG = '2024.04.1-buildpacks-bellsoft'
    skaffold dev

    helm list
    helm status kunde

    kubectl describe svc/kunde -n acme
    # in Lens: Network > Endpoints
    kubectl get ep -n acme

    <Strg>C
    skaffold delete
```

Bis das Port-Forwarding, das in `skaffold.yaml` konfiguriert ist und nicht
manuell eingerichtet werden muss, auch ausgeführt wird, muss man ggf. ein
bisschen warten. Aufgrund der Einstellungen für _Liveness_ und _Readiness_
kann es einige Minuten dauern, bis in der PowerShell angezeigt wird, dass die
Installation erfolgreich war. Mit Lens kann man jedoch die Log-Einträge
inspizieren und so vorher sehen, ob die Installation erfolgreich war. Sobald
Port-Forwarding aktiv ist, sieht man in der PowerShell auch die Konsole des
gestarteten (Kubernetes-) Pods.

Außerdem generiert Skaffold noch ein SHA-Tag zusätzlich zu `2024.04.1-buildpacks-bellsoft`.
Das kann man mit `docker images | sort` sehen. Von Zeit zu Zeit sollte man
mittels `docker rmi <image:tag>` aufräumen.

Wenn man nun in IntelliJ IDEA den Quellcode des Microservice editiert und dieser
durch IJ unmittelbar übersetzt wird, dann überwacht dabei Skaffold die
Quellcode-Dateien, baut ein neues Image und führt einen neuen Deployment-Vorgang
aus. Deshalb spricht man von __Continuous Deployment__.

### kubectl top

Mit `kubectl top pods -n acme` kann man sich die CPU- und RAM-Belegung der Pods
anzeigen lassen. Ausgehend von diesen Werten kann man `resources.requests` und
`resources.limits` in `dev.yaml` ggf. anpassen.

Voraussetzung für `kubectl top` ist, dass der `metrics-server` für Kubernetes
im Namespace `kube-system` installiert wurde.
https://kubernetes.io/docs/tasks/debug/debug-cluster/resource-metrics-pipeline

### Validierung der Installation

#### Polaris

Ob _Best Practices_ bei der Installation eingehalten wurden, kann man mit
_Polaris_ überprüfen. Um den Aufruf zu vereinfachen, gibt es im Unterverzeichnis
`extras\kubernetes` das Skript `polaris.ps1`:

```powershell
    cd extras\kubernetes
    .\polaris.ps1
```

Nun kann Polaris in einem Webbrowser mit der URL `http://localhost:8008`
aufgerufen werden.

#### kubescape

Ob _Best Practices_ bei den _Manifest-Dateien_ eingehalten wurden, kann man mit
_kubescape_ überprüfen. Um den Aufruf zu vereinfachen, gibt es im
Unterverzeichnis `extras\kubernetes` das Skript `kubescape.ps1`:

```powershell
    cd extras\kubernetes
    .\kubescape.ps1
```

#### Pluto

Ob _deprecated_ APIs bei den _Manifest-Dateien_ verwendet wurden, kann man mit
_Pluto_ überprüfen. Um den Aufruf zu vereinfachen, gibt es im
Unterverzeichnis `extras\kubernetes` das Skript `pluto.ps1`:

```powershell
    cd extras\kubernetes
    .\pluto.ps1
```

---

## Statische Codeanalyse

### Checkstyle und SpotBugs

Eine statische Codeanalyse ist durch die Werkzeuge _Checkstyle_, _SpotBugs_,
_Spotless_ und _Modernizer_ möglich, indem man die folgenden Tasks aufruft:

```powershell
    # Gradle:
    .\gradlew checkstyleMain spotbugsMain checkstyleTest spotbugsTest spotlessApply modernizer

    # Maven:
    .\mvnw checkstyle:checkstyle spotbugs:check spotless:check modernizer:modernizer jxr:jxr
```

### SonarQube

Für eine statische Codeanalyse durch _SonarQube_ muss zunächst der
SonarQube-Server mit _Docker Compose_ als Docker-Container gestartet werden:

```powershell
    cd extras\compose\sonarqube
    docker compose up
```

Wenn der Server zum ersten Mal gestartet wird, ruft man in einem Webbrowser die
URL `http://localhost:9000` auf. In der Startseite muss man sich einloggen und
verwendet dazu als Loginname `admin` und ebenso als Password `admin`. Danach
wird man weitergeleitet, um das initiale Passwort zu ändern.

Nun wählt man in der Webseite rechts oben das Profil aus und klickt auf den
Karteireiter _Security_. Im Abschnitt _Generate Tokens_ macht nun die folgende
Eingaben:

* _Name_: z.B. Softwarearchitektur
* _Type_: _Global Analysis Token_ auswählen
* _Expires in_: z.B. _90 days_ auswählen

Abschließend klickt man auf den Button _Generate_ und trägt den generierten
Token in `gradle.properties` bei der Property `sonarToken` ein.

Nachdem der Server gestartet ist, wird der SonarQube-Scanner in einer zweiten
PowerShell mit `.\gradlew sonar` bzw. `.\mvnw sonar:sonar` gestartet.
Das Resultat kann dann in der Webseite des zuvor gestarteten Servers über die
URL `http://localhost:9000` inspiziert werden.

Abschließend wird der oben gestartete Server heruntergefahren.

```powershell
    cd extras\compose
    docker compose down
```

---

## Analyse von Sicherheitslücken

### OWASP Security Check

In `build.gradle.kts` bzw. `pom.xml` sind _dependencies_ konfiguriert, um
Java Archive, d.h. .jar-Dateien, von Drittanbietern zu verwenden, z.B. die
JARs für Spring oder für Jackson. Diese Abhängigkeiten lassen sich mit
_OWASP Dependency Check_ analysieren:

```powershell
    # Gradle:
    .\gradlew dependencyCheckAnalyze --info

    # Maven:
    .\mvnw dependency-check:check
```

#### Docker Scout

Mit dem Unterkommando `quickview` von _Scout_ kann man sich zunächst einen
groben Überblick verschaffen, wieviele Sicherheitslücken in den Bibliotheken im
Image enthalten sind:

```powershell
    docker scout quickview juergenzimmermann/kunde:2024.04.1-buildpacks-bellsoft
```

Dabei bedeutet:

* C ritical
* H igh
* M edium
* L ow

Sicherheitslücken sind als _CVE-Records_ (CVE = Common Vulnerabilities and Exposures)
katalogisiert: https://www.cve.org (ursprünglich: https://cve.mitre.org/cve).
Übrigens bedeutet _CPE_ in diesem Zusammenhang _Common Platform Enumeration_.
Die Details zu den CVE-Records im Image kann man durch das Unterkommando `cves`
von _Scout_ auflisten:

```powershell
    # Analyse des Images mit Cloud-Native Buildpacks und Bellsoft Liberica
    docker scout cves juergenzimmermann/kunde:2024.04.1-buildpacks-bellsoft
    docker scout cves --format only-packages juergenzimmermann/kunde:2024.04.1-buildpacks-bellsoft
````

Statt der Kommandozeile kann man auch den Menüpunkt "Docker Scout" im
_Docker Dashboard_ verwenden.

### Trivy von Aquasec

Von Aquasec gibt es _Trivy_, um Docker-Images auf Sicherheitslücken zu analysieren.
Trivy gibt es auch als Docker-Image. In `compose.trivy.yml` ist ein Service für Trivy
so konfiguriert, dass das Image `kunde` analysiert wird:

```powershell
    cd extras\compose\trivy
    # Analyse des Images mit Cloud-Native Buildpacks und z.B. Bellsoft Liberica (siehe compose.yml)
    docker compose up
```

---

## Dokumentation

### Dokumentation durch AsciiDoctor und PlantUML

Eine HTML- und PDF-Dokumentation aus AsciiDoctor-Dateien, die ggf. UML-Diagramme
mit PlantUML enthalten, wird durch folgende Tasks erstellt:

```powershell
    # Gradle:
    .\gradlew asciidoctor asciidoctorPdf

    # Maven:
    .\mvnw asciidoctor:process-asciidoc asciidoctor:process-asciidoc@pdf -Pasciidoctor
```

### API Dokumentation durch javadoc

Eine API-Dokumentation in Form von HTML-Seiten kann man durch das Gradle- bzw.
Maven-Plugin erstellen:

```powershell
    # Gradle:
    .\gradlew javadoc

    # Maven:
    .\mvnw compile javadoc:javadoc
```
