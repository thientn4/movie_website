create schema idm;

create table idm.token_status(
	id int not null, primary key(id),
    value varchar(32) not null
);

create table idm.user_status(
	id int not null, primary key(id),
    value varchar(32) not null
);

create table idm.role(
	id int not null,primary key(id),
    name varchar(32) not null,
    description varchar(128) not null,
    precedence int not null
);

create table idm.user(
	id int not null auto_increment,primary key(id),
    email varchar(32) not null,unique(email),
    user_status_id int not null,
    salt char(8) not null,
    hashed_password char(88) not null,
    FOREIGN KEY (user_status_id) REFERENCES idm.user_status (id) ON UPDATE CASCADE ON DELETE RESTRICT
);

create table idm.refresh_token(
	id int not null auto_increment,primary key(id),
    token char(36) not null, unique(token),
    user_id int not null,
    token_status_id int not null,
    expire_time timestamp not null,
    max_life_time timestamp not null,
    FOREIGN KEY (user_id) REFERENCES idm.user (id) ON UPDATE CASCADE ON DELETE CASCADE,
	FOREIGN KEY (token_status_id) REFERENCES idm.token_status (id) ON UPDATE CASCADE ON DELETE RESTRICT
);

create table idm.user_role(
	user_id int not null,
    role_id int not null,
    PRIMARY KEY (user_id, role_id),
	FOREIGN KEY (user_id) REFERENCES idm.user (id) ON UPDATE CASCADE ON DELETE CASCADE,
	FOREIGN KEY (role_id) REFERENCES idm.role (id) ON UPDATE CASCADE ON DELETE RESTRICT
);

