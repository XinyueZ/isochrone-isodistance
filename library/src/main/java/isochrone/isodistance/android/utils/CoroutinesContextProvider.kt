package isochrone.isodistance.android.utils

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.android.Main

/**
 * Provide coroutines context.
 */
object CoroutinesContextProvider {
    val main = Dispatchers.Main
    val io = Dispatchers.Default
}
