CREATE SCHEMA netology_diploma;
create table netology_diploma.users
(
    id       serial,
    username varchar(255) not null,
    password varchar(255) not null,
    primary key (id)
);

create table netology_diploma.roles
(
    id   serial,
    name varchar(50) not null,
    primary key (id)
);

create table netology_diploma.users_role
(
    user_id bigint not null,
    role_id int    not null,
    primary key (user_id, role_id),
    foreign key (user_id) references netology_diploma.users (id),
    foreign key (role_id) references netology_diploma.roles (id)
);

create table netology_diploma.files
(
    id        serial ,
    filename  varchar(255),
    date      date,
    type      varchar(255),
    file_data oid,
    size      bigint,
    user_id   bigint not null references netology_diploma.users(id),
    primary key (id)
);

insert into netology_diploma.roles(name)
values ('ROLE_USER'), ('ROLE_ADMIN');

insert into netology_diploma.users(username, password)
VALUES
    ('user@gmail.com', '$2a$04$Fx/SX9.BAvtPlMyIIqqFx.hLY2Xp8nnhpzvEEVINvVpwIPbA3v/.i'),  -- pass: 100
    ('admin@gmail.com', '$2a$04$Fx/SX9.BAvtPlMyIIqqFx.hLY2Xp8nnhpzvEEVINvVpwIPbA3v/.i'); -- pass: 100

insert into netology_diploma.users_role(user_id, role_id)
values
(1,1),
(2,2)