import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log

class DatabaseHelper(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "character_creation.db"
        private const val DATABASE_VERSION = 1

        const val TABLE_ANCESTRIES = "ancestries"
        const val TABLE_CULTURES = "cultures"
        const val TABLE_CAREERS = "careers"
        const val TABLE_CLASSES = "classes"
        const val TABLE_KITS = "kits"
        const val TABLE_FREE_STRIKES = "free_strikes"
        const val TABLE_COMPLICATIONS = "complications"
    }

    override fun onCreate(db: SQLiteDatabase) {
        // Create tables
        val createAncestryTable = """
        CREATE TABLE $TABLE_ANCESTRIES (
            id INTEGER PRIMARY KEY AUTOINCREMENT,
            name TEXT
        )
    """
        val createCultureTable = """
        CREATE TABLE $TABLE_CULTURES (
            id INTEGER PRIMARY KEY AUTOINCREMENT,
            name TEXT
        )
    """
        val createCareerTable = """
        CREATE TABLE $TABLE_CAREERS (
            id INTEGER PRIMARY KEY AUTOINCREMENT,
            name TEXT
        )
    """
        val createClassTable = """
        CREATE TABLE $TABLE_CLASSES (
            id INTEGER PRIMARY KEY AUTOINCREMENT,
            name TEXT
        )
    """
        val createKitTable = """
        CREATE TABLE $TABLE_KITS (
            id INTEGER PRIMARY KEY AUTOINCREMENT,
            name TEXT
        )
    """
        val createFreeStrikesTable = """
        CREATE TABLE $TABLE_FREE_STRIKES (
            id INTEGER PRIMARY KEY AUTOINCREMENT,
            name TEXT
        )
    """
        val createComplicationsTable = """
        CREATE TABLE $TABLE_COMPLICATIONS (
            id INTEGER PRIMARY KEY AUTOINCREMENT,
            name TEXT
        )
    """

        // Execute queries to create tables
        db.execSQL(createAncestryTable)
        db.execSQL(createCultureTable)
        db.execSQL(createCareerTable)
        db.execSQL(createClassTable)
        db.execSQL(createKitTable)
        db.execSQL(createFreeStrikesTable)
        db.execSQL(createComplicationsTable)

        // Insert default data after creating the tables
        insertDefaultData(db)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_ANCESTRIES")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_CULTURES")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_CAREERS")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_CLASSES")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_KITS")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_FREE_STRIKES")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_COMPLICATIONS")
        onCreate(db)
    }

    // Helper function to insert options into the database
    private fun insertOption(db: SQLiteDatabase, tableName: String, name: String) {
        val query = "INSERT INTO $tableName (name) VALUES ('$name')"
        db.execSQL(query)
    }

    // Insert default data during database creation
    private fun insertDefaultData(db: SQLiteDatabase) {

        insertAncestriesDefaultData(db)

        insertOption(db, TABLE_CULTURES, "Noble")
        insertOption(db, TABLE_CULTURES, "Peasant")
        insertOption(db, TABLE_CAREERS, "Warrior")
        insertOption(db, TABLE_CAREERS, "Mage")
        insertOption(db, TABLE_CLASSES, "Fighter")
        insertOption(db, TABLE_CLASSES, "Wizard")
        insertOption(db, TABLE_KITS, "Basic Kit")
        insertOption(db, TABLE_KITS, "Advanced Kit")
        insertOption(db, TABLE_FREE_STRIKES, "One")
        insertOption(db, TABLE_FREE_STRIKES, "Two")
        insertOption(db, TABLE_COMPLICATIONS, "None")
        insertOption(db, TABLE_COMPLICATIONS, "Minor Injury")
    }

    private fun insertAncestriesDefaultData(db: SQLiteDatabase) {
        insertOption(db, TABLE_ANCESTRIES, "Devil")
        insertOption(db, TABLE_ANCESTRIES, "Dragon Knight")
        insertOption(db, TABLE_ANCESTRIES, "Dwarf")
        insertOption(db, TABLE_ANCESTRIES, "Wode Elf")
        insertOption(db, TABLE_ANCESTRIES, "High Elf")
        insertOption(db, TABLE_ANCESTRIES, "Hakaan")
        insertOption(db, TABLE_ANCESTRIES, "Human")
        insertOption(db, TABLE_ANCESTRIES, "Memonek")
        insertOption(db, TABLE_ANCESTRIES, "Orc")
        insertOption(db, TABLE_ANCESTRIES, "Polder")
        insertOption(db, TABLE_ANCESTRIES, "Time Raider")

    }

    // Method to get all options from a table
    fun getAllOptions(tableName: String): List<String> {
        val options = mutableListOf<String>()
        val db = readableDatabase

        // Query to fetch all names from the given table
        val cursor = db.rawQuery("SELECT name FROM $tableName", null)

        // Check if the column "name" exists in the cursor
        val nameColumnIndex = cursor.getColumnIndex("name")
        if (nameColumnIndex == -1) {
            // Handle the case where the column is not found (error or no data)
            Log.e("Database", "Column 'name' not found in table: $tableName")
            cursor.close()
            db.close()
            return options
        }

        // Add data to list
        while (cursor.moveToNext()) {
            options.add(cursor.getString(nameColumnIndex))
        }

        // Close cursor and db
        cursor.close()
        db.close()

        return options
    }


}