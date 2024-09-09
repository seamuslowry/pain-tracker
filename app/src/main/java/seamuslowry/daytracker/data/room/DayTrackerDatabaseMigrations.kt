package seamuslowry.daytracker.data.room

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

val MIGRATION_6_7: Migration = object : Migration(6, 7) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("ALTER TABLE Item ADD COLUMN comment TEXT")
    }
}

val MIGRATION_7_8: Migration = object : Migration(7, 8) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("ALTER TABLE Item_Configuration ADD COLUMN orderOverride INTEGER;")
        db.execSQL("ALTER TABLE Item_Configuration ADD COLUMN lastModifiedDate INTEGER NOT NULL DEFAULT 0;")
        // trigger to update last modified date on updates
        db.execSQL(
            """
            CREATE TRIGGER update_lastModifiedDate_trigger
            AFTER UPDATE ON Item_Configuration
            FOR EACH ROW
            WHEN NEW.lastModifiedDate = OLD.lastModifiedDate
            BEGIN
                UPDATE Item_Configuration
                SET lastModifiedDate = unixepoch('subsec') * 1000
                WHERE id = OLD.id;
            END;
        """,
        )
    }
}
