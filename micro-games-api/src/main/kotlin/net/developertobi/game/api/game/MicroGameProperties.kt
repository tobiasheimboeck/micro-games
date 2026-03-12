package net.developertobi.game.api.game

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class MicroGameProperties(
    val id: String,
    val name: String,
    val version: String,
    val apiVersion: Int,
)

/**
 * Reads [MicroGameProperties] from a [MicroGame] implementation via reflection.
 * @throws IllegalStateException if the class is not annotated with [MicroGameProperties]
 */
fun MicroGame.getProperties(): MicroGameProperties =
    this::class.java.getAnnotation(MicroGameProperties::class.java)
        ?: throw IllegalStateException("MicroGame ${this::class.java.name} must be annotated with @MicroGameProperties")
