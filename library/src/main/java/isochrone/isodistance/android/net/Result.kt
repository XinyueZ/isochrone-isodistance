package isochrone.isodistance.android.net

import retrofit2.Response

/**
 * A generic class that holds a value with its loading status.
 * @param <T>
 */
sealed class Result<out T : Any> {

    data class Success<out T : Any>(val content: T) : Result<T>()
    data class Error(val exception: Exception) : Result<Nothing>()
    object Loading : Result<Nothing>()

    override fun toString(): String {
        return when (this) {
            is Success<*> -> "Success[content=$content]"
            is Error -> "Error[exception=$exception]"
            Loading -> "Loading"
        }
    }
}

internal inline fun <E : Any> Response<E>.getResult(onError: () -> Result.Error): Result<E> {
    if (isSuccessful) {
        val body = body()
        if (body != null) {
            return Result.Success(body)
        }
    }
    return onError()
}