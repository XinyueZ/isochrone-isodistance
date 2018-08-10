package isochrone.isodistance.android.net

import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.android.UI
import kotlin.coroutines.experimental.CoroutineContext

/**
 * Provide coroutines context.
 */
object CoroutinesContextProvider {
    val main: CoroutineContext = UI
    val io: CoroutineContext = CommonPool
}
