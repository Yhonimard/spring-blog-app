create table posts
(
    id           varchar(100) not null,
    title        varchar(200) not null,
    content      text         not null,
    date_created timestamp(6) not null,
    date_updated timestamp(6) not null,
    primary key (id)
);

create table users
(
    username     varchar(100) not null,
    password     varchar(100) not null,
    first_name   varchar(100),
    last_name    varchar(100),
    date_created timestamp(6) not null,
    date_updated timestamp(6) not null,
    primary key (username)
);

create table comments
(
    id           varchar(100) not null,
    user_id      varchar(100) not null,
    post_id      varchar(100) not null,
    title        varchar(100) not null,
    date_created timestamp(6) not null,
    date_updated timestamp(6) not null,
    primary key (id),
    constraint fk_comments_post foreign key (post_id) references posts (id),
    constraint fk_comments_users foreign key (user_id) references users (username)
);

create table roles
(
    id   varchar(100) not null,
    name varchar(100) not null,
    primary key (id)
);


create table user_role
(
    user_id varchar(100) not null,
    role_id varchar(100) not null,
    constraint fk_user_role_users foreign key (user_id) references users (username),
    constraint fk_user_role_roles foreign key (role_id) references roles (id)
);


create table post_images
(
    id         varchar(100) not null,
    post_id    varchar(100) not null,
    image_name varchar(100) not null,
    image      bytea        not null,
    primary key (id),
    constraint fk_post_images_posts foreign key (post_id) references posts (id)
);



SELECT column_name, data_type, character_maximum_length
FROM information_schema.columns
WHERE table_name = 'post_images';
