#Smark

This library provides a useful widget class mimicking the behavior of edit texts in a web form, which retain the entered values in a local history and automatically suggest previously stored texts as soon as the user starts typing.

##Usage

The typical usage is to declare the widget directly into the layout XML file.
For example:

    <it.subito.smark.SmarkTextView
        android:id="@+id/editText"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:layout_alignParentTop="true"
        android:layout_centerHorizontal="true"
        android:layout_marginTop="100dp"/>

The widget supports all the attributes of a [MultiAutoCompleteTextView][1] and a number of additional ones used to customized the specific behaviors in an easy and flexible way. Moreover, an even more advanced level of customization can be achieved by accessing the view programmatically through your Java code.

The widget makes use of a *Persister* to store and retrieve back the collected data associated with the specific view. In fact, the data are stored in separate collections identified by a unique key, thus enabling different views of the same kind (for example: a user name or address field) to share the same list of suggestions.

##Attributes

Besides the standard *android:* attributes, custom ones has been defined to be able to set specific parameter related to the view.

Below a list of the attributes, their default value (if any) and their correspondent Java method (if any):

<table>
    <tr>
        <th>Attribute</th><th>Default</th><th>Method</th><th>Description</th>
    </tr>
    <tr>
        <td>smark:auto_save</td><td align="center"><i>true</i></td><td>setAutoSave(boolean)</td><td>Tells the view whether to automatically try to decide the best moment to save the edited content. In case the algorithm was not working as expected you should disable it and rely on the explicit "save()" method instead.</td>
    </tr>
    <tr>
        <td>smark:item_layout</td><td align="center"><i>android.R.layout.simple_dropdown_item_1line</i></td><td></td><td>Sets the ID of the layout to be used for displaying the items in the autocomplete drop down list.</td>
    </tr>
    <tr>
        <td>smark:key</td><td align="center"><i>DEFAULT_SAVEKEY</i></td><td>setSaveKey(String)</td><td>Sets the key used to store and retrieve the data associated to the view.</td>
    </tr>
    <tr>
        <td>smark:persister</td><td align="center">"it.subito.smark.store.SharedPreferencesPersister"</td><td>setPersister(Persister)</td><td>Tells the view which Persister implementation class to instantiate. Note that the persister instance might not be accessed whether the view adapter has been modified.</td>
    </tr>
    <tr>
        <td>smark:text_view_id</td><td align="center"><i>android.R.id.text1</i></td><td></td><td>Sets the ID of the view in the autocomplete item layout where to set the suggested text.</td>
    </tr>
    <tr>
        <td>smark:token_separators</td><td align="center">""</td><td></td><td>Sets the separator characters used to isolate a token in the edited text. By default it is the empty string, meaning that the whole text is always identified as a unique token.</td>
    </tr>
</table>

**Note**: remember to add the proper namespace to the layout XML file:
 
    xmlns:smark="http://schemas.android.com/apk/res-auto"

##


[1]:http://developer.android.com/reference/android/widget/MultiAutoCompleteTextView.html