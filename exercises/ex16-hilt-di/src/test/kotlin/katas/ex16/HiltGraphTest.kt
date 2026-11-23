package katas.ex16

import dagger.Module
import dagger.Provides
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.HiltTestApplication
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Rule
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import javax.inject.Inject
import javax.inject.Provider
import javax.inject.Singleton
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotSame
import kotlin.test.assertSame

// GIVEN: do not change

class FakePrescriptionApi : PrescriptionApi {
    override suspend fun prescriptions() = listOf("Zyrtec", "Apap")
}

/** Replaces the production ApiModule with a fake in all Hilt tests in this module. */
@Module
@TestInstallIn(components = [SingletonComponent::class], replaces = [ApiModule::class])
object FakeApiModule {
    @Provides @Singleton
    fun api(): PrescriptionApi = FakePrescriptionApi()
}

@HiltAndroidTest
@RunWith(RobolectricTestRunner::class)
@Config(application = HiltTestApplication::class)
class HiltGraphTest {
    @get:Rule val hilt = HiltAndroidRule(this)

    @Inject lateinit var repositoryA: PrescriptionsRepository
    @Inject lateinit var repositoryB: PrescriptionsRepository
    @Inject @IoDispatcher lateinit var io: CoroutineDispatcher
    @Inject lateinit var formatters: Provider<HeaderFormatter>

    @Before fun setUp() = hilt.inject()

    @Test fun `repository is bound to default implementation as singleton`() {
        assertSame(repositoryA, repositoryB)
        assertEquals(DefaultPrescriptionsRepository::class, repositoryA::class)
    }

    @Test fun `io dispatcher is provided with qualifier`() {
        assertSame(Dispatchers.IO, io)
    }

    @Test fun `formatter is unscoped`() {
        assertNotSame(formatters.get(), formatters.get())
    }

    @Test fun `test module replaces the api`() = runBlocking {
        assertEquals(listOf("Apap", "Zyrtec"), repositoryA.all())
    }
}
