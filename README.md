#Smark

This library provides a useful widget class mimicking the behavior of edit texts in a web form, which retain the entered values in a local history and automatically suggest previously stored texts as soon as the user starts typing.

##Usage

The typical usage is to declare the widget directly into the layout XML file.
For example:

```xml
    <it.subito.smark.SmarkTextView
        android:id="@+id/editText"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:layout_alignParentTop="true"
        android:layout_centerHorizontal="true"
        android:layout_marginTop="100dp"/>
```

The widget supports all the attributes of a [MultiAutoCompleteTextView][1] and a number of additional ones used to customized the specific behaviors in an easy and flexible way. Moreover, an even more advanced level of customization can be achieved by accessing the view programmatically through your Java code.

The widget makes use of a *Persister* to store and retrieve back the collected data associated with the specific view. In fact, the data are stored in separate collections identified by a unique key, thus enabling different views of the same kind (for example: a user name or address field) to share the same list of suggestions.

##Attributes

