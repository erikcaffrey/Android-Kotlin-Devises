/**
 * Copyright 2017 Erik Jhordan Rey.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package erikjhordanrey.android_kotlin_devises.view

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import erikjhordanrey.android_kotlin_devises.data.repository.CurrencyRepository
import erikjhordanrey.android_kotlin_devises.di.CurrencyApplication
import erikjhordanrey.android_kotlin_devises.domain.AvailableExchange
import erikjhordanrey.android_kotlin_devises.domain.Currency
import javax.inject.Inject

class CurrencyViewModel : ViewModel() {

  @Inject lateinit var currencyRepository: CurrencyRepository

  private var liveCurrencyData: LiveData<List<Currency>>? = null
  private var liveAvailableExchange: LiveData<AvailableExchange>? = null

  init {
    initializeDagger()
  }

  fun getAvailableExchange(currencies: String): LiveData<AvailableExchange>? {
    liveAvailableExchange = null
    liveAvailableExchange = MutableLiveData<AvailableExchange>()
    liveAvailableExchange = currencyRepository.getAvailableExchange(currencies)
    return liveAvailableExchange
  }


  fun loadCurrencyList(): LiveData<List<Currency>>? {
    if (liveCurrencyData == null) {
      liveCurrencyData = MutableLiveData<List<Currency>>()
      liveCurrencyData = currencyRepository.getCurrencyList()
    }
    return liveCurrencyData
  }

  private fun initializeDagger() = CurrencyApplication.appComponent.inject(this)

}


