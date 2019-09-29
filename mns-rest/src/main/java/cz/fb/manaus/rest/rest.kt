package cz.fb.manaus.rest

import org.springframework.http.ResponseEntity

internal fun <T> handleNotFound(footprint: T?): ResponseEntity<T> {
    return if (footprint != null) {
        ResponseEntity.ok(footprint)
    } else {
        ResponseEntity.notFound().build()
    }
}
