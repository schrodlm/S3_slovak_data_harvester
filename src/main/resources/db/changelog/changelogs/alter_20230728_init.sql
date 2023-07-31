CREATE TABLE batch_info (
    id SERIAL,
    batch_name VARCHAR(255) NOT NULL,
    exportDate DATE NOT NULL,
    path_to_file VARCHAR(1024) NOT NULL,
    PRIMARY KEY (id)
);
