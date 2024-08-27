CREATE TABLE todos (
    id UUID DEFAULT gen_random_uuid(),
    text TEXT,
    PRIMARY KEY(id)
);
