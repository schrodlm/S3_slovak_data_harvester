ALTER TABLE batch_info
    ADD CONSTRAINT batch_name_unique UNIQUE (batch_name);