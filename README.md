Frost
=====

This library provides a useful widget class mimicking the behavior of edit texts in a web form, which retains the entered values in a local history and automatically suggests previously stored texts as soon as the user starts typing.

Usage
-----

The typical usage is to declare the widget directly into the layout XML file.
For example:

```xml
<it.subito.frost.FrostTextView
    android:id="@+id/frost_text_view"
    android:layout_width="match_parent"
    android:layout_height="wrap_content" />
```

The widget supports all the attributes of a [MultiAutoCompleteTextView][1] and a number of additional ones used to customize the specific behaviors in an easy and flexible way. Moreover, an even more advanced level of customization can be achieved by accessing the view programmatically through your Java code.

The widget makes use of a `Persister` to store and retrieve back the collected data associated with the specific view. In fact, the data are stored in separate collections identified by a unique key, thus enabling different views of the same kind (for example: a user name or address field) to share the same list of suggestions.

Attributes
-----

Besides the standard `android:` attributes, custom ones has been defined to be able to set specific parameter related to the view.

Below a list of the attributes, their default value (if any) and their correspondent Java method (if any):

Attribute | Default | Description
--- | --- | --- | ---
`frost:auto_save` `setAutoSave(boolean)` | `true` | Tells the view whether to automatically try to decide the best moment to save the edited content. In case the algorithm was not working as expected you should disable it and rely on the explicit `save()` method instead.
`frost:item_layout` | `android.R.layout.simple_dropdown_item_1line` | Sets the ID of the layout to be used for displaying the items in the autocomplete drop down list.
`frost:key` `setSaveKey(String)`  | `DEFAULT_SAVEKEY` | Sets the key used to store and retrieve the data associated to the view.
`frost:persister` `setPersister(Persister)` | `it.subito.frost.store.SharedPreferencesPersister` | Tells the view which Persister implementation class to instantiate. Note that the persister instance might not be accessed whether the view adapter has been modified.
`frost:text_view_id` | `android.R.id.text1` | Sets the ID of the view in the autocomplete item layout where to set the suggested text.
`frost:token_separators` | `""` |  Sets the separator characters used to isolate a token in the edited text. By default it is the empty string, meaning that the whole text is always identified as a unique token.

**Note**: remember to add the proper namespace to the layout XML file:

```xml
xmlns:frost="http://schemas.android.com/apk/res-auto"
```
Data customization
------------------

In order to customize or modify data right before they are persisted and right after they are loaded, the view can be extended and the two methods `onSave(CharSequence)` and `onLoad(CharSequence)` can be overridden respectively.

By overriding the save method the inheriting class could intercept and modify every stored text:

```java
@Override
protected void onSave(final CharSequence data) {

    super.onSave(data.toString().toLowerCase());
}
```
In a dual way, by overriding the load method, the inheriting class could filter, modify or change the order of the loaded list:

```java
@Override
protected List<CharSequence> onLoad(final CharSequence constraint) {

    final List<CharSequence> items = super.onLoad(constraint);

    return items.subList(0, Math.min(items.size(), 10));
}
```

License
-------

    Copyright (C) 2014 Subito.it S.r.l (www.subito.it)

	Licensed under the Apache License, Version 2.0 (the "License");
	you may not use this file except in compliance with the License.
	You may obtain a copy of the License at
	
	     http://www.apache.org/licenses/LICENSE-2.0
	
	Unless required by applicable law or agreed to in writing, software
	distributed under the License is distributed on an "AS IS" BASIS,
	WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
	See the License for the specific language governing permissions and
	limitations under the License.

[1]:http://developer.android.com/reference/android/widget/MultiAutoCompleteTextView.html
