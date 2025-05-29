package com.likeminds.sampleusertagging

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import com.likeminds.sampleusertagging.databinding.ActivityMainBinding
import com.likeminds.usertagging.UserTagging
import com.likeminds.usertagging.model.TagUser
import com.likeminds.usertagging.model.UserTaggingConfig
import com.likeminds.usertagging.util.UserTaggingViewListener

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    private val listOfUsers = arrayListOf(
        TagUser.Builder()
            .name("Iron Main")
            .description("@tonyStark3000")
            .id(1003)
            .build(),
        TagUser.Builder()
            .name("Captain America")
            .description("@steveRogers")
            .id(1004)
            .build(),
        TagUser.Builder()
            .name("Hulk")
            .description("@bruceBanner")
            .id(1005)
            .build(),
        TagUser.Builder()
            .name("Hawkeye")
            .description("@clintBarton")
            .id(1006)
            .build(),
        TagUser.Builder()
            .name("Thor")
            .description("@thorOdinson")
            .id(1007)
            .build()
    )

    private val secondPage = arrayListOf(
        TagUser.Builder()
            .name("Black Widow")
            .description("@natashaRomanOff")
            .id(1008)
            .build(),
        TagUser.Builder()
            .name("Fury")
            .description("@nickFury")
            .id(1009)
            .build(),
        TagUser.Builder()
            .name("Hill")
            .description("@mariaHill")
            .id(1010)
            .build(),
        TagUser.Builder()
            .name("Spider Man")
            .description("@peterParker")
            .id(1011)
            .build(),
        TagUser.Builder()
            .name("Vision")
            .description("@vision")
            .id(1012)
            .build()
    )

    companion object {
        const val TAG = "UserTaggingTest"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.container) { view, windowInsets ->
            val innerPadding = windowInsets.getInsets(
                // Notice we're using systemBars, not statusBar
                WindowInsetsCompat.Type.systemBars()
                        // Notice we're also accounting for the display cutouts
                        or WindowInsetsCompat.Type.displayCutout()
                        // If using EditText, also add
                        or WindowInsetsCompat.Type.ime()
            )
            // Apply the insets as padding to the view. Here, set all the dimensions
            // as appropriate to your layout. You can also update the view's margin if
            // more appropriate.
            view.setPadding(0, innerPadding.top, 0, innerPadding.bottom)

            // Return CONSUMED if you don't want the window insets to keep passing down
            // to descendant views.
            WindowInsetsCompat.CONSUMED
        }

        initMemberTaggingView()

        binding.apply {
            fabSend.setOnClickListener {
                val text = binding.editTextSample.text
                val updatedText = binding.userTaggingView.replaceSelectedMembers(text).trim()

                tvActualStringHeading.isVisible = true
                tvDisplayedStringHeading.isVisible = true
                tvActualString.isVisible = true
                tvDisplayedString.isVisible = true

                tvActualString.text = updatedText
                tvDisplayedString.text = text
            }
        }
    }

    /**
     * initializes the [userTaggingView] with the edit text
     * also sets listener to the [userTaggingView]
     */
    private fun initMemberTaggingView() {
        val listener = object : UserTaggingViewListener {
            override fun onUserTagged(user: TagUser) {
                Log.d(
                    TAG, """
                    tagged user: 
                    Name: ${user.name}
                    id: ${user.id}
                """.trimIndent()
                )
            }

            override fun callApi(page: Int, searchName: String) {
                Log.d(
                    "PUI", """
                    page:$page
                    searchName: $searchName                    
                """.trimIndent()
                )
                if (page == 1) {
                    binding.userTaggingView.setMembers(listOfUsers)
                } else {
                    binding.userTaggingView.addMembers(secondPage)
                }
            }
        }
        val config = UserTaggingConfig.Builder()
            .editText(binding.editTextSample)
            .maxHeightInPercentage(0.4f)
            .color(R.color.red)
            .build()

        UserTagging.initialize(binding.userTaggingView, config, listener)
    }
}