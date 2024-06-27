CREATE TABLE IF NOT EXISTS adresse (
    id              uuid PRIMARY KEY USING INDEX TABLESPACE studentspace,
    plz             char(5) NOT NULL CHECK ( plz ~ '\d{5}' ),
    ort             varchar(40) NOT NULL
) TABLESPACE studentspace;
CREATE INDEX IF NOT EXISTS adresse_plz_idx ON adresse(plz) TABLESPACE studentspace;

CREATE TABLE IF NOT EXISTS student (
    id              uuid PRIMARY KEY USING INDEX TABLESPACE studentspace,
    version         integer NOT NULL DEFAULT 0,
    nachname        varchar(40) NOT NULL,
    name            varchar(40) NOT NULL,
    email           varchar(40) NOT NULL UNIQUE USING INDEX TABLESPACE studentspace,
    geburtsdatum    date CHECK ( geburtsdatum < current_date ),
    semester        varchar(20) NOT NULL,
    adresseid       uuid NOT NULL UNIQUE USING INDEX TABLESPACE studentspace REFERENCES adresse,
    module          varchar(32),
    username        varchar(20) NOT NULL,
    erzeugt         timestamp NOT NULL,
    aktualisiert    timestamp NOT NULL
) TABLESPACE studentspace;

CREATE INDEX IF NOT EXISTS student_nachname_idx ON student(nachname) tablespace studentspace;

CREATE TABLE IF NOT EXISTS guthaben (
    id              uuid PRIMARY KEY USING INDEX TABLESPACE studentspace,
    betrag          decimal(10,2) NOT NULL ,
    waehrung        char(3) NOT NULL CHECK (waehrung ~ '[A-Z]{3}'),
    student_id      uuid REFERENCES  student,
    idx             integer NOT NULL DEFAULT 0
) TABLESPACE studentspace;
CREATE INDEX IF NOT EXISTS guthaben_student_id_idx ON guthaben(student_id) TABLESPACE studentspace;
