INSERT INTO users (login, password)
VALUES ('user@test.ru', '$2a$12$k4ngwII93/bIx9spgDIVUOCkSQEZHlSjMAIi38cUkQqopLSTUSKG2'),
       ('admin@test.ru', '$2a$12$DhDNzhun1P.hyhOzXc0cjuAuiAMkMTmvBgXZLy6mYgj9rMyfsozym');

INSERT INTO user_roles(user_id, roles)
VALUES (1, 'ROLE_USER'),
       (2, 'ROLE_ADMIN')