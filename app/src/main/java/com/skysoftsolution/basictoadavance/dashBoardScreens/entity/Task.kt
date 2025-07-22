package com.skysoftsolution.basictoadavance.dashBoardScreens.entity
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ForeignKey

@Entity(
    tableName = "tasks",
    foreignKeys = [
        ForeignKey(
            entity = Category::class,
            parentColumns = ["categoryId"],
            childColumns = ["categoryId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class Task(
    @PrimaryKey(autoGenerate = true) val taskId: Int = 0,
    val taskName: String,
    val description: String?,
    val categoryId: Int?, // Foreign key to Category table
    val priority: Int, // 0 = Low, 1 = Medium, 2 = High
    val dueDate: String?, // Format: YYYY-MM-DD
    val reminderTime: String?, // Format: YYYY-MM-DD HH:mm
    val status: Int = 0, // 0 = Pending, 1 = Completed
    val createdAt: String = System.currentTimeMillis().toString(),
    val updatedAt: String = System.currentTimeMillis().toString()
)

