-- liquibase formatted sql

--changeset bornartem:1
create table telegram_api(
id bigserial primary key,
chat_id bigint not null,
notification_text varchar not null,
data timestamp not null
);