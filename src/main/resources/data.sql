insert into USER (ID, USERNAME, PASSWORD) values (1, 'john' ,'{bcrypt}$2a$10$iMMb7iGNjDAlqkwlR4TJHuhwtMGq.sMGL5v3TEiCt53vIiGke0cpa');
insert into USER (ID, USERNAME, PASSWORD) values (2, 'admin' ,'{noop}secret2');
insert into USER (ID, USERNAME, PASSWORD) values (3, 'bob' ,'{noop}password');
insert into USER (ID, USERNAME, PASSWORD) values (4, 'alice' ,'{noop}swordfish');
insert into USER (ID, USERNAME, PASSWORD) values (5, 'deleteme' ,'{noop}swordfish');
insert into USER (ID, USERNAME, PASSWORD) values (6, 'deleteme2' ,'{noop}swordfish');
insert into USER (ID, USERNAME, PASSWORD) values (7, 'jane' ,'{noop}swordfish');
insert into USER (ID, USERNAME, PASSWORD) values (8, 'dave' ,'{noop}swordfish');
INSERT INTO AUTHORITY ( USER_ID ,AUTHORITY ) values (1, 'ROLE_USER');
INSERT INTO AUTHORITY ( USER_ID ,AUTHORITY ) values (2, 'ROLE_ADMIN');
INSERT INTO AUTHORITY ( USER_ID ,AUTHORITY ) values (3, 'ROLE_USER');
INSERT INTO AUTHORITY ( USER_ID ,AUTHORITY ) values (4, 'ROLE_USER');
INSERT INTO AUTHORITY ( USER_ID ,AUTHORITY ) values (5, 'ROLE_USER');
INSERT INTO AUTHORITY ( USER_ID ,AUTHORITY ) values (6, 'ROLE_USER');
INSERT INTO AUTHORITY ( USER_ID ,AUTHORITY ) values (7, 'ROLE_USER');
INSERT INTO AUTHORITY ( USER_ID ,AUTHORITY ) values (8, 'ROLE_USER');


insert into CLIENT (ID, NAME, SECRET, REDIRECT_URI, SCOPE)
values (1, 'client','{bcrypt}$2a$10$CVLUeCYqZQpLRm0PpaXXTuvskBujQelGhmxoCXXU0RylBrTQOiqQW' ,'http://localhost:7070/profile', 'read');

insert into GRANT (CLIENT_ID, GRANT  ) values (1,  'authorization_code' );
insert into GRANT (CLIENT_ID, GRANT  ) values (1,  'password' );
insert into GRANT (CLIENT_ID, GRANT  ) values (1,  'client_credentials');
insert into GRANT (CLIENT_ID, GRANT  ) values (1,  'refresh_token' );


insert into CLIENT (ID, NAME, SECRET, REDIRECT_URI, SCOPE)
values (2, 'client2','{bcrypt}$2a$10$CVLUeCYqZQpLRm0PpaXXTuvskBujQelGhmxoCXXU0RylBrTQOiqQW' ,'http://localhost:7070/advice', 'advice');

insert into GRANT (CLIENT_ID, GRANT  ) values (2,  'authorization_code' );
insert into GRANT (CLIENT_ID, GRANT  ) values (2,  'password' );
insert into GRANT (CLIENT_ID, GRANT  ) values (2,  'client_credentials');
insert into GRANT (CLIENT_ID, GRANT  ) values (2,  'refresh_token' );