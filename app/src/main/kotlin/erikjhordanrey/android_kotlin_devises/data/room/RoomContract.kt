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

package erikjhordanrey.android_kotlin_devises.data.room

class RoomContract {

  companion object {

    const val DATABASE_CURRENCY = "currency.db"

    const val TABLE_CURRENCIES = "currencies"

    private const val SELECT_COUNT = "SELECT COUNT(*) FROM "
    private const val SELECT_FROM = "SELECT * FROM "

    const val SELECT_CURRENCIES_COUNT = SELECT_COUNT + TABLE_CURRENCIES
    const val SELECT_CURRENCIES = SELECT_FROM + TABLE_CURRENCIES

  }
}

