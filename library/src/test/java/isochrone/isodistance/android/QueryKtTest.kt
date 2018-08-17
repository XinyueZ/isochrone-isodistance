package isochrone.isodistance.android

import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.mock
import io.kotlintest.properties.Gen
import isochrone.isodistance.android.algorithm.toLatLng
import isochrone.isodistance.android.domain.geocode.Geocode
import isochrone.isodistance.android.domain.geocode.Geometry
import isochrone.isodistance.android.domain.geocode.Location
import isochrone.isodistance.android.domain.geocode.ResultsItem
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Test

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
}