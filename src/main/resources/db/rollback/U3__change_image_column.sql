-- Rollback for V3__change_image_column.sql
ALTER TABLE personal_info
    DROP COLUMN IF EXISTS photo_data,
    DROP COLUMN IF EXISTS photo_mime_type;
