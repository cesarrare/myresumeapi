-- Store optional profile images as base64 text with mime type metadata.
ALTER TABLE personal_info
    ADD COLUMN photo_data TEXT,
    ADD COLUMN photo_mime_type VARCHAR(100);

COMMENT ON COLUMN personal_info.photo_data IS 'Optional base64-encoded profile image (no data URI prefix).';
COMMENT ON COLUMN personal_info.photo_mime_type IS 'Optional MIME type for photo_data, e.g. image/png.';
