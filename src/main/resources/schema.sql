drop table if exists mpas cascade;
CREATE TABLE IF NOT EXISTS mpas (
    id INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    name varchar(50) NOT NULL UNIQUE
);

drop table if exists genres cascade;
CREATE TABLE IF NOT EXISTS genres (
    id INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    name varchar(50) NOT NULL UNIQUE
);

drop table if exists films cascade;
CREATE TABLE IF NOT EXISTS films (
    id INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    name varchar(100) NOT NULL,
    description varchar(200),
    release_date date,
    duration integer,
    mpa INTEGER REFERENCES mpas (id) ON UPDATE cascade ON DELETE restrict
);

drop table if exists m2m_film_genre cascade;
CREATE TABLE IF NOT EXISTS m2m_film_genre (
    f_id INTEGER REFERENCES films (id) ON UPDATE cascade ON DELETE cascade,
    g_id INTEGER REFERENCES genres (id) ON UPDATE cascade ON DELETE restrict,
    CONSTRAINT film_genre_pk PRIMARY KEY (f_id, g_id)
);

drop table if exists users cascade;
CREATE TABLE IF NOT EXISTS users (
    id INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    email varchar(200) NOT NULL UNIQUE,
    login varchar(200) NOT NULL UNIQUE,
    name varchar(200) NOT NULL,
    birthday date
);

drop table if exists m2m_likes cascade;
CREATE TABLE IF NOT EXISTS m2m_likes (
    f_id INTEGER REFERENCES films (id) ON UPDATE cascade ON DELETE cascade,
    u_id INTEGER REFERENCES users (id) ON UPDATE cascade ON DELETE cascade,
    CONSTRAINT likes_pk PRIMARY KEY (f_id, u_id)
);

drop table if exists m2m_friends cascade;
CREATE TABLE IF NOT EXISTS m2m_friends (
    u_id INTEGER REFERENCES users (id) ON UPDATE cascade ON DELETE cascade,
    friend_id INTEGER REFERENCES users (id) ON UPDATE cascade ON DELETE cascade,
    status boolean NOT NULL,
    CONSTRAINT friends_pk PRIMARY KEY (u_id, friend_id)
);