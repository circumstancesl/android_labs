package com.example.android_labs

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.viewModelScope
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import okhttp3.ResponseBody
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException

@ExperimentalCoroutinesApi
class ViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    private val testDispatcher = TestCoroutineDispatcher()
    private lateinit var api: RickAndMortyApi
    private lateinit var viewModel: ViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        api = mockk()
        viewModel = ViewModel(api)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        testDispatcher.cleanupTestCoroutines()
    }

    // успешное получение данных из api
    @Test
    fun fetchCharactersSuccess() = runTest {
        val mockCharacter1 = mockk<Character>()
        val mockCharacter2 = mockk<Character>()
        coEvery { api.getCharacter(1) } returns mockCharacter1
        coEvery { api.getCharacter(2) } returns mockCharacter2

        viewModel.fetchCharacters(listOf(1, 2))

        coVerify { api.getCharacter(1) }
        coVerify { api.getCharacter(2) }
        assert(viewModel.characterData.value == listOf(mockCharacter1, mockCharacter2))
    }

    // обработка ошибок при сетевых сбоях
    @Test
    fun fetchCharactersNetworkError() = runTest {
        coEvery { api.getCharacter(1) } throws IOException("Network error")

        viewModel.fetchCharacters(listOf(1, 2))

        coVerify { api.getCharacter(1) }
        assert(viewModel.errorMessage.value == "Ошибка сети. Проверьте подключение к интернету.")
    }

    // корректное обновление UI
    @Test
    fun fetchCharactersUpdatesUI() = runTest {
        val mockCharacter = mockk<Character>()
        coEvery { api.getCharacter(any()) } returns mockCharacter

        viewModel.fetchCharacters(listOf(1))

        advanceUntilIdle()
        assert(viewModel.characterData.value?.isNotEmpty() == true)
    }

    // обработка http
    @Test
    fun fetchCharactersHttpError() = runTest {
        coEvery { api.getCharacter(1) } throws HttpException(
            Response.error<Any>(404, ResponseBody.create(null, ""))
        )

        viewModel.fetchCharacters(listOf(1))

        assert(viewModel.errorMessage.value?.startsWith("Ошибка сервера: 404") == true)
    }

    // жизненный цикл корутин
    @Test
    fun cancelCoroutine() = runTest {
        val job = viewModel.viewModelScope.launch {
            viewModel.fetchCharacters(listOf(1, 100))
        }

        viewModel.viewModelScope.cancel()
        advanceUntilIdle()

        assert(viewModel.viewModelScope.coroutineContext[Job]?.isCancelled == true) {
            "Должен быть отменен"
        }
    }
}