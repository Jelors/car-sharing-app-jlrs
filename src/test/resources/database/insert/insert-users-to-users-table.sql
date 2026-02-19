insert into users (id, email, first_name, last_name, password, is_deleted)
VALUES
    (1, 'charlesBill@example.com', 'Charles', 'Bill', '$2a$10$JVJIs6etszWD4YsLEyz13OlPFd5oSz5z2SAXnJsBczYKHn9oeeb3a', 0),
    (2, 'johnSmith@example.com', 'John', 'Smith', '$2a$10$SYDyezwHhMugRIj6dNztU.CADwzrDR9yGcQ69d7zTOi8pTIpy0zSO', 0);

# $2a$10$JVJIs6etszWD4YsLEyz13OlPFd5oSz5z2SAXnJsBczYKHn9oeeb3a -> adminPassword
# $2a$10$SYDyezwHhMugRIj6dNztU.CADwzrDR9yGcQ69d7zTOi8pTIpy0zSO -> userPassword