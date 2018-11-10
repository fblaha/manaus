package cz.fb.manaus.repository.domain

data class Runner(
        val selectionId: Long,
        val name: String,
        val handicap: Double,
        val sortPriority: Int
)