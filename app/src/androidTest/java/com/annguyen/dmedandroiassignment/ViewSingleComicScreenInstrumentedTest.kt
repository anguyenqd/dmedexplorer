package com.annguyen.dmedandroiassignment

import android.content.Intent
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.core.app.launchActivity
import androidx.test.espresso.Espresso
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers
import com.annguyen.dmed.data.repository.RepositoryImpl
import com.annguyen.dmed.data.repository.local.LocalDataSource
import com.annguyen.dmed.data.repository.local.LocalDataSourceImpl
import com.annguyen.dmed.data.repository.network.NetworkDataSource
import com.annguyen.dmed.domain.repository.Repository
import com.annguyen.dmed.view.R
import com.annguyen.dmed.view.screen.EXTRA_COMIC_ID
import com.annguyen.dmed.view.screen.ViewSingleComicActivity
import com.annguyen.dmedandroiassignment.di.MarvelRepositoryModule
import com.annguyen.dmedandroiassignment.fake.FakeNetworkDataSource
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import dagger.hilt.components.SingletonComponent
import org.junit.After

import org.junit.Test

import org.junit.Rule
import javax.inject.Singleton

@UninstallModules(MarvelRepositoryModule::class)
@HiltAndroidTest
class ViewSingleComicScreenInstrumentedTest {

    @Module
    @InstallIn(SingletonComponent::class)
    abstract class TestRepositoryModule {

        @Singleton
        @Binds
        abstract fun provideRepository(
            repository: RepositoryImpl
        ): Repository

        @Singleton
        @Binds
        abstract fun provideLocalDataSource(
            localDataSource: LocalDataSourceImpl
        ): LocalDataSource

        @Singleton
        @Binds
        abstract fun provideNetworkDataSource(
            networkDataSource: FakeNetworkDataSource
        ): NetworkDataSource
    }

    @get:Rule(order = 1)
    var hiltRule = HiltAndroidRule(this)

    lateinit var scenario: ActivityScenario<ViewSingleComicActivity>

    @After
    fun cleanUp() {
        scenario.close()
    }

    @Test
    fun simpleLaunch() {
        hiltRule.inject()
        val intent =
            Intent(ApplicationProvider.getApplicationContext(), ViewSingleComicActivity::class.java)
                .putExtra(EXTRA_COMIC_ID, 1)
        scenario = launchActivity(intent)
    }

    @Test
    fun testSingleComicScreen() {
        hiltRule.inject()
        val intent =
            Intent(ApplicationProvider.getApplicationContext(), ViewSingleComicActivity::class.java)
                .putExtra(EXTRA_COMIC_ID, 1)
        scenario = launchActivity(intent)

        Espresso.onView(ViewMatchers.withId(R.id.tv_title)).check(
            ViewAssertions.matches(
                ViewMatchers.withText("Amazing Spider man 1")
            )
        )
    }
}
