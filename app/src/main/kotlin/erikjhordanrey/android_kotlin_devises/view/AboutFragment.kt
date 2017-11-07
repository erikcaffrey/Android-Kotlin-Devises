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

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import erikjhordanrey.android_kotlin_devises.R
import kotlinx.android.synthetic.main.about_fragment.*


class AboutFragment : Fragment() {

  companion object {

    const val PROJECT_BLOG_POST = "https://erikcaffrey.github.io/ANDROID-kotlin-arch-components/"
    const val PROJECT_SOURCE_CODE = "https://github.com/erikcaffrey/Android-Architecture-Components-Kotlin"

    fun newInstance() = AboutFragment()
  }

  override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
    return inflater.inflate(R.layout.about_fragment, container, false)
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    initUI()
  }

  private fun initUI() {
    show_me_post.setOnClickListener { startActivityActionView(PROJECT_BLOG_POST) }
    show_me_code.setOnClickListener { startActivityActionView(PROJECT_SOURCE_CODE) }
  }


  private fun startActivityActionView(url: String) {
    startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
  }
}

