package cz.fb.manaus.spring

interface ManausProfiles {
    companion object {
        const val PRODUCTION = "production"
        const val DB = "database"
        const val TEST = "test"

        val PRODUCTION_REQUIRED = arrayOf(PRODUCTION, DB)
    }
}
