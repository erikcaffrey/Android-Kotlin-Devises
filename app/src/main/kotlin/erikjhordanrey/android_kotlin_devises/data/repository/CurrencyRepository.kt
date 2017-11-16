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

package erikjhordanrey.android_kotlin_devises.data.repository

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import erikjhordanrey.android_kotlin_devises.data.remote.CurrencyResponse
import erikjhordanrey.android_kotlin_devises.data.remote.RemoteCurrencyDataSource
import erikjhordanrey.android_kotlin_devises.data.room.CurrencyEntity
import erikjhordanrey.android_kotlin_devises.data.room.RoomCurrencyDataSource
import erikjhordanrey.android_kotlin_devises.domain.AvailableExchange
import erikjhordanrey.android_kotlin_devises.domain.Currency
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CurrencyRepository @Inject constructor(
    private val roomCurrencyDataSource: RoomCurrencyDataSource,
    private val remoteCurrencyDataSource: RemoteCurrencyDataSource
) : Repository {

  val allCompositeDisposable: MutableList<Disposable> = arrayListOf()

  override fun getTotalCurrencies() = roomCurrencyDataSource.currencyDao().getCurrenciesTotal()

  override fun addCurrencies() {
    val currencyEntityList = RoomCurrencyDataSource.getAllCurrencies()
    roomCurrencyDataSource.currencyDao().insertAll(currencyEntityList)
  }

  override fun getCurrencyList(): LiveData<List<Currency>> {
    val roomCurrencyDao = roomCurrencyDataSource.currencyDao()
    val mutableLiveData = MutableLiveData<List<Currency>>()
    val disposable = roomCurrencyDao.getAllCurrencies()
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe({ currencyList ->
          mutableLiveData.value = transform(currencyList)
        }, { t: Throwable? -> t!!.printStackTrace() })
    allCompositeDisposable.add(disposable)
    return mutableLiveData
  }

  private fun transform(currencies: List<CurrencyEntity>): List<Currency> {
    val currencyList = ArrayList<Currency>()
    currencies.forEach {
      currencyList.add(Currency(it.countryCode, it.countryName))
    }
    return currencyList
  }

  override fun getAvailableExchange(currencies: String): LiveData<AvailableExchange> {
    val mutableLiveData = MutableLiveData<AvailableExchange>()
    val disposable = remoteCurrencyDataSource.requestAvailableExchange(currencies)
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe({ currencyResponse ->
          if (currencyResponse.isSuccess) {
            mutableLiveData.value = transform(currencyResponse)
          } else {
            throw Throwable("CurrencyRepository -> on Error occurred")
          }
        }, { t: Throwable? -> t!!.printStackTrace() })
    allCompositeDisposable.add(disposable)
    return mutableLiveData
  }

  private fun transform(exchangeMap: CurrencyResponse): AvailableExchange {
    return AvailableExchange(exchangeMap.currencyQuotes)
  }

}
