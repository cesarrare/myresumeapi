CREATE UNIQUE INDEX IF NOT EXISTS ux_users_google_id
    ON users (google_id)
    WHERE google_id IS NOT NULL;
