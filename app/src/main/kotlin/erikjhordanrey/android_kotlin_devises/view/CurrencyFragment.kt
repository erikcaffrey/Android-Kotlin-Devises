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

import android.os.Build
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import erikjhordanrey.android_kotlin_devises.R
import erikjhordanrey.android_kotlin_devises.databinding.CurrencyFragmentBinding
import erikjhordanrey.android_kotlin_devises.di.App
import erikjhordanrey.android_kotlin_devises.di.DaggerCurrencyComponent
import javax.inject.Inject

class CurrencyFragment : Fragment() {

    private val currencies = ArrayList<String>()
    private lateinit var currenciesAdapter: ArrayAdapter<String>
    private lateinit var currencyFrom: String
    private lateinit var currencyTo: String

    @Inject
    lateinit var currencyViewModel: CurrencyViewModel

    private lateinit var binding: CurrencyFragmentBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = CurrencyFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initializeDagger()
        initViewModel()
        initUI()
        populateSpinnerAdapter()

        currencyViewModel.isProgressVisible.observe(viewLifecycleOwner, Observer { visible ->
            binding.progressBar.visibility = if (visible) VISIBLE else GONE
        })
    }

    private fun initializeDagger() {
        DaggerCurrencyComponent.builder().appComponent(App.appComponent).currencyFragment(this).build().inject(this)
    }

    private fun initViewModel() {
        currencyViewModel.let { lifecycle.addObserver(it) }
        currencyViewModel.initLocalCurrencies()
    }

    private fun initUI() {
        initSpinners()
        initConvertButton()
    }

    private fun populateSpinnerAdapter() {
        currencyViewModel.loadCurrencyList()?.observe(viewLifecycleOwner, Observer { currencyList ->
            currencyList?.forEach {
                currencies.add(it.code + "  " + it.country)
            }
            currenciesAdapter.setDropDownViewResource(R.layout.item_spinner)
            currenciesAdapter.notifyDataSetChanged()
        })

    }

    private fun initSpinners() {
        currenciesAdapter = ArrayAdapter(requireContext(), R.layout.item_spinner, currencies)
        binding.fromCurrencySpinner.adapter = currenciesAdapter
        binding.fromCurrencySpinner.setSelection(0)
        binding.toCurrencySpinner.adapter = currenciesAdapter
        binding.toCurrencySpinner.setSelection(0)
    }

    private fun initConvertButton() {
        binding.convertButton.setOnClickListener {
            convert()
            currencyViewModel.progressVisible()
        }
    }

    // You can move all this logic to the view model

    private fun convert() {
        val quantity = binding.currencyEdit.text.toString()
        currencyFrom = getCurrencyCode(binding.fromCurrencySpinner.selectedItem.toString())
        currencyTo = getCurrencyCode(binding.toCurrencySpinner.selectedItem.toString())
        val currencies = "$currencyFrom,$currencyTo"

        if (quantity.isNotEmpty() && currencyFrom != currencyTo) {
            currencyViewModel.getAvailableExchange(currencies)?.observe(viewLifecycleOwner, Observer { availableExchange ->
                availableExchange?.run {
                    exchange(quantity.toDouble(), availableExchangesMap)
                }
            })

        } else {
            currencyViewModel.progressInvisible()
            Toast.makeText(activity, "Could not convert.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun exchange(quantity: Double, availableExchangesMap: Map<String, Double>) {
        val exchangesKeys = availableExchangesMap.keys.toList()
        val exchangesValues = availableExchangesMap.values.toList()

        val fromCurrency = exchangesValues[0]
        val toCurrency = exchangesValues[1]

        val fromCurrencyKey = getCurrencyCodeResult(exchangesKeys[0])
        val toCurrencyKey = getCurrencyCodeResult(exchangesKeys[1])

        val usdExchange = quantity.div(fromCurrency)
        val exchangeResult = usdExchange.times(toCurrency)

        val result = quantity.toString() + " " + fromCurrencyKey + " = " + exchangeResult.format(4) + " " + toCurrencyKey
        showResult(result)
        currencyViewModel.progressInvisible()
    }

    private fun showResult(result: String) {
        val builder: AlertDialog.Builder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            AlertDialog.Builder(requireContext(), R.style.AppCompatAlertDialogStyle)
        } else {
            AlertDialog.Builder(requireContext())
        }

        val setMessage = TextView(activity)
        setMessage.text = result
        setMessage.gravity = Gravity.CENTER_HORIZONTAL
        builder.setView(setMessage)
        builder.setTitle(getString(R.string.currency_converter))
                .setPositiveButton(android.R.string.yes, null)
                .setIcon(R.drawable.ic_attach_money_black_24dp)
                .show()
    }

    private fun getCurrencyCode(currency: String) = currency.substring(0, 3)

    private fun getCurrencyCodeResult(currency: String) = currency.substring(3)

    private fun Double.format(digits: Int) = java.lang.String.format("%.${digits}f", this)

    companion object {
        fun newInstance() = CurrencyFragment()
    }
}
