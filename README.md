Frost
=====

This library provides a useful widget class mimicking the behavior of edit texts in a web form, which retains the entered values in a local history and automatically suggests previously stored texts as soon as the user starts typing.

Download
--------

Grab the latest version from Maven Central:

```groovy
compile 'it.subito:frost-library:1.+'
```

Usage
-----

The typical usage is to declare the widget directly into the layout XML file.
For example:

```xml
<it.subito.frost.FrostTextView
    android:id="@+id/user_email"
    frost:key="user_email"
    android:layout_width="match_parent"
    android:layout_height="wrap_content" />
```

The widget supports all the attributes of a [MultiAutoCompleteTextView][1] and a number of additional ones used to customize the specific behaviors in an easy and flexible way. Moreover, an even more advanced level of customization can be achieved by accessing the view programmatically through your Java code.

The widget makes use of a `Persister` to store and retrieve back the collected data associated with the specific view. In fact, the data are stored in separate collections identified by a unique key, thus enabling different views of the same kind (for example: a user name or address field) to share the same list of suggestions.

Customisation
-------------

Please see the [Customisation page](https://github.com/Subito-it/Frost/wiki/Customisation) for more information on how to change the behaviour of the View.

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
