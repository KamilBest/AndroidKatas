package katas.ex22

import androidx.room.Database
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import java.time.Instant

// GIVEN: do not change

@Entity(tableName = "medications")
data class MedicationEntity(
    @PrimaryKey val id: String,
    val name: String,
    val doseMg: Int,
)

@Entity(
    tableName = "doses",
    foreignKeys = [ForeignKey(
        entity = MedicationEntity::class,
        parentColumns = ["id"],
        childColumns = ["medicationId"],
        onDelete = ForeignKey.CASCADE,
    )],
    indices = [Index("medicationId"), Index("scheduledAt")],
)
data class DoseEntity(
    @PrimaryKey val id: String,
    val medicationId: String,
    val scheduledAt: Instant,
    val takenAt: Instant? = null,
)

class Converters {
    @TypeConverter fun toEpochMillis(value: Instant?): Long? = value?.toEpochMilli()
    @TypeConverter fun fromEpochMillis(value: Long?): Instant? = value?.let(Instant::ofEpochMilli)
}

@Database(entities = [MedicationEntity::class, DoseEntity::class], version = 1, exportSchema = false)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun medicationDao(): MedicationDao
}
