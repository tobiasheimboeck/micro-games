package net.developertobi.game.api

/**
 * Provider for the Micro Games API.
 * Set by the plugin on startup; games and other code access functionality via [api].
 */
object MicroGamesProvider {
    @Volatile
    var api: MicroGamesApi? = null
        private set

    /**
     * Called by the plugin on startup. Do not call from game code.
     */
    @JvmStatic
    fun setApi(api: MicroGamesApi) {
        this.api = api
    }

    /**
     * Called by the plugin on shutdown.
     */
    @JvmStatic
    fun clearApi() {
        this.api = null
    }
}
