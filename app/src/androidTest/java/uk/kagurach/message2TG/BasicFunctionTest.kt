package uk.kagurach.message2TG

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Test
import org.junit.runner.RunWith
import uk.kagurach.message2TG.diagnose.DiagnoseHelper

@RunWith(AndroidJUnit4::class)

/**
 * Diagnosing system-related functionalities, also `DiagnoseHelper` itself.
 */
class BasicFunctionTest {
  @Test
  fun useAppContext() {
    val appContext = InstrumentationRegistry.getInstrumentation().targetContext

    val result = DiagnoseHelper.diagnose(appContext)

    result.forEach {
      assert(it.second == true){
        it.first + " failed"
      }
    }
  }
}