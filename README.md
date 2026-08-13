# Android User Tagging Library

The Android User Tagging Library is a powerful tool that allows developers to easily implement user
tagging functionality in their Android applications. This library simplifies the process of tagging
users within an app, enabling features such as mentions, notifications, and user search.

# Features

- User Tagging: The library provides easy-to-use methods to tag users within your app's interface.
  Users can be tagged in various contexts, such as comments, posts, messages, or any other
  user-generated content.

- Mentions: With the library, you can implement mentions functionality by allowing users to tag
  other users in their posts or comments. The library handles the process of finding and suggesting
  usernames, making it simple for users to tag others.

- Customization: The library provides various customization options, allowing you to adapt the user
  tagging functionality to match your app's design and branding. You can customize the tag
  appearance, search results, and notifications to provide a seamless user experience.

## Getting Started

To start using the Android User Tagging Library in your project, follow these steps:

- Clone this repository in your project.

- Add the UserTaggingSuggestionListView in your xml file.

```xml

<com.likeminds.usertagging.view.UserTaggingSuggestionListView android:id="@+id/user_tagging_view"
    android:layout_width="0dp" android:layout_height="wrap_content" android:translationZ="5dp"
    app:layout_constraintBottom_toTopOf="@id/edit_text_sample"
    app:layout_constraintEnd_toEndOf="parent" app:layout_constraintStart_toStartOf="parent"
    tools:visibility="gone" />
```

- Initialize the library by creating a object of `UserTaggingConfig` and
  call `UserTagging.initialize()` with created config object.

```kotlin
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
        binding.userTaggingView.setMembers(listOfUsers)
    }
}
val config = UserTaggingConfig.Builder()
    .editText(binding.editTextSample)
    .maxHeightInPercentage(0.4f)
    .color(R.color.red)
    .build()

UserTagging.initialize(binding.userTaggingView, config, listener)
```

Now, user tagging is enabled in your app.

## License

Licensed under the Apache License, Version 2.0. See [LICENSE](LICENSE) for details.
