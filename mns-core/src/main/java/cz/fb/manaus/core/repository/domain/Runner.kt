package cz.fb.manaus.core.repository.domain

data class Runner(
        val selectionId: Long,
        val name: String,
        val handicap: Double,
        val sortPriority: Int
)