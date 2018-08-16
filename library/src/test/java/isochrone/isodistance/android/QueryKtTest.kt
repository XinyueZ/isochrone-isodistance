package isochrone.isodistance.android;

import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.mock
import io.kotlintest.properties.Gen
import isochrone.isodistance.android.algorithm.toLatLng
import isochrone.isodistance.android.domain.geocode.Geocode
import isochrone.isodistance.android.domain.geocode.Geometry
import isochrone.isodistance.android.domain.geocode.Location
import isochrone.isodistance.android.domain.geocode.ResultsItem
import org.junit.Assert
import org.junit.Test

class QueryKtTest {
    @Test
    fun test_Geocode_toLatLng_ext() {
        val someLocation =
            Location(Gen.double().constants().first(), Gen.double().constants().first())
        val results = mock<List<ResultsItem>> {
            on { get(0) } doReturn ResultsItem(geometry = Geometry(location = someLocation))
        }

        // Test for valid location
        val geocode = Geocode(results)
        val latLng = geocode.toLatLng()
        Assert.assertNotNull(latLng)
        Assert.assertEquals(latLng?.latitude, someLocation.lat)
        Assert.assertEquals(latLng?.longitude, someLocation.lng)

        // Test for invalid location
        val nullResultsGeocode = Geocode(null)
        Assert.assertNull(nullResultsGeocode.toLatLng())
    }
}