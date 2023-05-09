DROP TABLE IF EXISTS users, items, bookings, requests, comments CASCADE;

CREATE TABLE IF NOT EXISTS users
(
    id              BIGINT          GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    name            VARCHAR(128)    NOT NULL,
    email           VARCHAR(128)    UNIQUE NOT NULL
);

CREATE TABLE IF NOT EXISTS items
(
    id              BIGINT          GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    name            VARCHAR(128)    NOT NULL,
    description     VARCHAR         NOT NULL,
    is_available    BOOLEAN         DEFAULT FALSE,
    owner_id        BIGINT          REFERENCES users (id),
    request_id      BIGINT
);


CREATE TABLE IF NOT EXISTS bookings
(
    id              BIGINT          GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    start_date      TIMESTAMP       WITHOUT TIME ZONE NOT NULL,
    end_date        TIMESTAMP       WITHOUT TIME ZONE NOT NULL,
    item_id         BIGINT          REFERENCES items (id),
    booker_id       BIGINT          REFERENCES users (id),
    status          VARCHAR(32)
);

CREATE TABLE IF NOT EXISTS requests
(
    id              BIGINT             GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    description     VARCHAR(512)       NOT NULL,
    requester_id    BIGINT             REFERENCES users (id),
    created         TIMESTAMP          WITHOUT TIME ZONE
);

CREATE TABLE IF NOT EXISTS comments
(
    id              BIGINT             GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    text            VARCHAR(256),
    item_id         BIGINT             REFERENCES items (id),
    author_id       BIGINT             REFERENCES users (id),
    created         TIMESTAMP          WITHOUT TIME ZONE
);