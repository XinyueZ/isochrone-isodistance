package isochrone.isodistance.android.utils

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.android.UI
import kotlin.coroutines.CoroutineContext

/**
 * Provide coroutines context.
 */
object CoroutinesContextProvider {
    val main: CoroutineContext = UI
    val io: CoroutineContext = Dispatchers.Default
}
