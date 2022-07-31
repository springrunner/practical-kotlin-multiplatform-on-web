drop table if exists `todo`;

create table `todos`(
    `id` varchar(36) not null comment '일련번호',
    `text` varchar(255) not null comment '할 일',
    `state` int not null comment '상태',
    `created_date` datetime(6) not null comment '등록일시',
    `last_modified_date` datetime(6) not null comment '변경일시',
    primary key (`id`)
);
