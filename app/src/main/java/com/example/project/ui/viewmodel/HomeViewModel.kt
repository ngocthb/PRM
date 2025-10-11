package com.example.project.ui.viewmodel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.project.api.ApiServices
import com.example.project.model.Currency
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import retrofit2.awaitResponse


class HomeViewModel : ViewModel() {

    private val _currency = MutableStateFlow<Currency?>(null)
    val currency: StateFlow<Currency?> = _currency

    fun getCurrencyRate() {
        viewModelScope.launch {
            try {
                val response = ApiServices.getApiService()
                    .convertToVND(
                        "843d4d34ae72b3882e3db642c51e28e6",
                        "VND",
                        "USD",
                        1
                    )
                    .awaitResponse()

                if (response.isSuccessful) {
                    _currency.value = response.body()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
