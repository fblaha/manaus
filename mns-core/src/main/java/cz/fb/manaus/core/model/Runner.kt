package cz.fb.manaus.core.model

data class Runner(
    val selectionId: Long,
    val name: String,
    val handicap: Double,
    val sortPriority: Int
)