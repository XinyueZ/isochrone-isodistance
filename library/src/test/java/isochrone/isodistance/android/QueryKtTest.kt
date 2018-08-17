package isochrone.isodistance.android

import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.reset
import com.nhaarman.mockito_kotlin.times
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.whenever
import io.kotlintest.properties.Gen
import isochrone.isodistance.android.algorithm.getResult
import isochrone.isodistance.android.algorithm.toLatLng
import isochrone.isodistance.android.domain.geocode.Geocode
import isochrone.isodistance.android.domain.geocode.Geometry
import isochrone.isodistance.android.domain.geocode.Location
import isochrone.isodistance.android.domain.geocode.ResultsItem
import isochrone.isodistance.android.net.Result
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import retrofit2.Response

class QueryKtTest {
    @Test
    fun test_Geocode_toLatLng_ext() {
        val someLocation =
            Location(Gen.double().random().iterator().next(), Gen.double().random().iterator().next())
        val results = mock<List<ResultsItem>> {
            on { get(0) } doReturn ResultsItem(geometry = Geometry(location = someLocation))
        }

        // Test for valid location
        val geocode = Geocode(results)
        val latLng = geocode.toLatLng()
        assertNotNull(latLng)
        assertEquals(latLng?.latitude, someLocation.lat)
        assertEquals(latLng?.longitude, someLocation.lng)

        // Test for invalid location
        val nullResultsGeocode = Geocode(null)
        assertNull(nullResultsGeocode.toLatLng())
    }

    @Test
    fun test_getResult_ext() {
        val mockError = mock<() -> Result.Error>()
        val response = mock<Response<String>>()

        // Success response, get Result.Success
        whenever(response.body()).thenReturn(Gen.string().random().iterator().next())
        whenever(response.isSuccessful).thenReturn(true)
        val result = response.getResult(mockError)
        assertTrue(result is Result.Success<String>)
        verify(mockError, times(0)).invoke()

        reset(mockError)

        // Success response with null content body, get onError and run it
        whenever(mockError.invoke()).thenReturn(Result.Error(RuntimeException()))
        val nullResponse = mock<Response<String>>()
        whenever(nullResponse.isSuccessful).thenReturn(true)
        whenever(nullResponse.body()).thenReturn(null)
        val getResult = nullResponse.getResult(mockError)
        assertTrue(getResult is Result.Error)
        verify(mockError, times(1)).invoke()

        reset(mockError)

        // Not-success response with whatever in content body, get onError and run it
        whenever(mockError.invoke()).thenReturn(Result.Error(RuntimeException()))
        val  nullResponse2 = mock<Response<String>>()
        whenever(nullResponse2.isSuccessful).thenReturn(false)
        whenever(nullResponse2.body()).thenReturn(Gen.string().random().iterator().next())
        val getResult2 = nullResponse2.getResult(mockError)
        assertTrue(getResult2 is Result.Error)
        verify(mockError, times(1)).invoke()
    }
}