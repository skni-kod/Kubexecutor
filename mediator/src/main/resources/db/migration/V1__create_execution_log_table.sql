CREATE TABLE Logs
(
    ID       INTEGER PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    EMAIL    VARCHAR(255) NOT NULL,
    LANGUAGE VARCHAR(64)  NOT NULL,
    CODE     BYTEA        NOT NULL,
    STDOUT   BYTEA        NOT NULL,
    STDERR   BYTEA        NOT NULL,
    EXITCODE INTEGER      NOT NULL
);