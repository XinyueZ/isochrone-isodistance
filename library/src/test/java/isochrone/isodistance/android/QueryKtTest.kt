package isochrone.isodistance.android

import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.reset
import com.nhaarman.mockito_kotlin.times
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.whenever
import io.kotlintest.properties.Gen
import isochrone.isodistance.android.algorithm.TravelMode
import isochrone.isodistance.android.algorithm.queryGeocodeAddress
import isochrone.isodistance.android.algorithm.queryMatrix
import isochrone.isodistance.android.algorithm.toLatLngString
import isochrone.isodistance.android.algorithm.toLatLngStringArray
import isochrone.isodistance.android.algorithm.toLocation
import isochrone.isodistance.android.algorithm.toPipelineJoinedString
import isochrone.isodistance.android.api.GoogleApi
import isochrone.isodistance.android.domain.geocode.Geocode
import isochrone.isodistance.android.domain.geocode.Geometry
import isochrone.isodistance.android.domain.geocode.Location
import isochrone.isodistance.android.domain.geocode.ResultsItem
import isochrone.isodistance.android.domain.matrix.Matrix
import isochrone.isodistance.android.net.Result
import isochrone.isodistance.android.net.getResult
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Ignore
import org.junit.Test
import retrofit2.Response

class QueryKtTest {

    private val key = Gen.string().random().first()

    @Test
    fun test_Geocode_toLatLng_ext() {
        val someLocation =
            Location(
                Gen.double().random().iterator().next(),
                Gen.double().random().iterator().next()
            )
        val results = mock<List<ResultsItem>> {
            on { get(0) } doReturn ResultsItem(geometry = Geometry(location = someLocation))
        }

        // Test for valid location
        val geocode = Geocode(results)
        val latLng = geocode.toLocation()
        assertNotNull(latLng)
        assertEquals(latLng?.lat, someLocation.lat)
        assertEquals(latLng?.lng, someLocation.lng)

        // Test for invalid location
        val nullResultsGeocode = Geocode(null)
        assertNull(nullResultsGeocode.toLocation())
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
        val nullResponse2 = mock<Response<String>>()
        whenever(nullResponse2.isSuccessful).thenReturn(false)
        whenever(nullResponse2.body()).thenReturn(Gen.string().random().iterator().next())
        val getResult2 = nullResponse2.getResult(mockError)
        assertTrue(getResult2 is Result.Error)
        verify(mockError, times(1)).invoke()
    }

    @Test
    fun test_TravelMode_enum() {
        assertTrue(TravelMode.DRIVING.value.contentEquals("driving"))
        assertTrue(TravelMode.TRANSIT.value.contentEquals("transit"))
        assertTrue(TravelMode.BICYCLING.value.contentEquals("bicycling"))
        assertTrue(TravelMode.WALKING.value.contentEquals("walking"))
    }

    @Test
    @Ignore // Temp
    fun test_queryMatrix_ext() = runBlocking {
        val travelMode = TravelMode.WALKING

        val origin = Location(Gen.double().random().first(), Gen.double().random().first())

        val destinations = arrayOf(
            Location(Gen.double().random().first(), Gen.double().random().first()),
            Location(Gen.double().random().first(), Gen.double().random().first()),
            Location(Gen.double().random().first(), Gen.double().random().first())
        )
        val destinationsFormatted = destinations.toLatLngStringArray().toPipelineJoinedString()

        val mockResponse = mock<Response<Matrix>>()
        val mockRet = mock<Deferred<Response<Matrix>>>()
        whenever(mockRet.await()).thenReturn(mockResponse)
        val mockGoogleApi = mock<GoogleApi> {
            on {
                getMatrix(
                    travelMode.value,
                    origin.toLatLngString(),
                    destinationsFormatted,
                    key
                )
            } doReturn mockRet
        }
        travelMode.queryMatrix(
            origin,
            destinations,
            mockGoogleApi,
            key
        )
        verify(mockGoogleApi, times(1)).getMatrix(
            travelMode.value,
            origin.toLatLngString(),
            destinationsFormatted,
            key
        )
        Unit
    }

    @Test
    @Ignore // Temp
    fun test_queryGeocodeAddress() = runBlocking {
        val address = Gen.string().random().filter { it.isNotBlank() }.first()
        val mockResponse = mock<Response<Geocode>>()
        val mockRet = mock<Deferred<Response<Geocode>>>()
        whenever(mockRet.await()).thenReturn(mockResponse)
        val mockGoogleApi = mock<GoogleApi> {
            on { getGeocode(address, key) } doReturn mockRet
        }
        queryGeocodeAddress(address, mockGoogleApi, key)
        verify(mockGoogleApi, times(1)).getGeocode(address, key)
        Unit
    }

    @Test
    fun test_toLatLngString_ext() {
        val latLng = Location(Gen.double().random().first(), Gen.double().random().first())
        assertTrue(latLng.toLatLngString().contentEquals("${latLng.lat},${latLng.lng}"))
    }

    @Test
    fun test_toPipelineJoinedString_ext() {
        val arr = Gen.string().constants().toList().toTypedArray()
        assertTrue(arr.toPipelineJoinedString().contentEquals(arr.joinToString("|")))
    }

    @Test
    fun test_toLatLngStringArray_ext() {
        val arr = arrayOf(
            Location(
                Gen.double().random().iterator().next(),
                Gen.double().random().iterator().next()
            ),
            Location(
                Gen.double().random().iterator().next(),
                Gen.double().random().iterator().next()
            ),
            Location(
                Gen.double().random().iterator().next(),
                Gen.double().random().iterator().next()
            ),
            Location(Gen.double().random().iterator().next(), Gen.double().random().iterator().next())
        )
        val typed = arr.toLatLngStringArray()
        var i = 0
        arr.forEach {
            assertTrue(it.toLatLngString().contentEquals(typed[i++]))
        }
    }
}