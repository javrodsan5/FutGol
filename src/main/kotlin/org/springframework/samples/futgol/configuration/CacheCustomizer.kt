package org.springframework.samples.futgol.configuration

import org.springframework.boot.autoconfigure.cache.CacheManagerCustomizer
import org.springframework.cache.concurrent.ConcurrentMapCacheManager
import org.springframework.stereotype.Component


@Component
class CacheCustomizer : CacheManagerCustomizer<ConcurrentMapCacheManager> {
    override fun customize(cacheManager: ConcurrentMapCacheManager) {
        cacheManager.setCacheNames(listOf("ligas", "detallesLiga", "jornadas", "detallesUsuario","detallesJugador"))
    }
}
