/**
 * Copyright (C) 2014 Subito.it S.r.l (www.subito.it)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package it.subito.frost;

import android.content.Context;
import android.content.res.TypedArray;
import android.database.AbstractCursor;
import android.database.Cursor;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.text.Editable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.widget.BaseAdapter;
import android.widget.FilterQueryProvider;
import android.widget.Filterable;
import android.widget.ListAdapter;
import android.widget.MultiAutoCompleteTextView;
import android.widget.SimpleCursorAdapter;
import android.widget.SimpleCursorAdapter.CursorToStringConverter;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.WeakHashMap;

import it.subito.frost.store.Persister;
import it.subito.frost.store.Persister.DataObserver;
import it.subito.frost.store.SharedPreferencesPersister;

public class FrostTextView extends MultiAutoCompleteTextView implements DataObserver {

    public static final String DEFAULT_SAVEKEY = "default_smark";

    private static final String AUTOSAVE_KEY_SUFFIX = "_auto_";

    private static final String DEFAULT_SEPARATORS = "";

    private static final WeakHashMap<FrostTextView, Void> sViews =
            new WeakHashMap<FrostTextView, Void>();

    private ListAdapter mAdapter;

    private boolean mAutoSave;

    private Persister mPersister;

    private String mSaveKey;

    private String mTokenSeparators;

    /**
     * Overrides {@link android.widget.MultiAutoCompleteTextView#MultiAutoCompleteTextView(android.content.Context)}.
     */
    public FrostTextView(final Context context) {

        super(context);
        init(null, 0);
    }

    /**
     * Overrides {@link android.widget.MultiAutoCompleteTextView#MultiAutoCompleteTextView(android.content.Context, android.util.AttributeSet)}.
     */
    public FrostTextView(final Context context, final AttributeSet attrs) {

        super(context, attrs);
        init(attrs, 0);
    }

    /**
     * Overrides {@link android.widget.MultiAutoCompleteTextView#MultiAutoCompleteTextView(android.content.Context, android.util.AttributeSet, int)}.
     */
    public FrostTextView(final Context context, final AttributeSet attrs, final int defStyle) {

        super(context, attrs, defStyle);
        init(attrs, defStyle);
    }

    /**
     * Returns the key used by the auto-save feature to temporarily store the last entered text.
     * In fact, when auto-save feature is enabled, the entered text is automatically add to the
     * history list when the view is definitely destroyed.
     * <p/>
     * Note that, in order for auto-save to work properly, the view must have a unique ID set.
     * <p/>
     * Note also that, to consistently clear the history associated with a specific save key, also
     * the corresponding auto-save history should be cleared.
     *
     * @param saveKey The save key associated with the autocomplete history.
     * @return The key or an empty string.
     */
    public static String autoSaveKey(final String saveKey) {

        if (!TextUtils.isEmpty(saveKey)) {

            return saveKey + AUTOSAVE_KEY_SUFFIX;
        }

        return "";
    }

    /**
     * Saves the currently selected text in all the views into the autocomplete history.
     */
    public static void saveAll() {

        for (final FrostTextView textView : sViews.keySet()) {

            textView.save();
        }
    }

    /**
     * Clears the history associated with this view.
     */
    public void clearHistory() {

        final Persister persister = mPersister;

        persister.remove(mSaveKey);
        persister.remove(autoSaveKey());
    }

    /**
     * Gets the persister associated with this view.
     *
     * @return The persister instance or null.
     */
    public Persister getPersister() {

        if (mPersister instanceof MockPersister) {

            return null;
        }

        return mPersister;
    }

    /**
     * Sets the persister associated with this view.
     *
     * @param persister The persister instance or null.
     */
    public void setPersister(final Persister persister) {

        mPersister = persister;

        if (persister == null) {

            mPersister = new MockPersister();
        }

        mPersister.setContext(getContext());
        mPersister.setObserver(this);
    }

    @Override
    public void onDataChanged() {

        refresh();
    }

    /**
     * Saves the currently selected text into the autocomplete history.
     */
    public void save() {

        onSave(getText());
    }

    @Override
    @SuppressWarnings("NullableProblems")
    public <T extends ListAdapter & Filterable> void setAdapter(final T adapter) {

        mAdapter = adapter;

        super.setAdapter(adapter);
    }

    @Override
    public void onWindowFocusChanged(final boolean hasWindowFocus) {

        if (!hasWindowFocus && mAutoSave) {

            autoSave();
        }

        super.onWindowFocusChanged(hasWindowFocus);
    }

    @Override
    protected void onAttachedToWindow() {

        super.onAttachedToWindow();

        autoRestore();
    }

    /**
     * Enables/disables the auto-save feature.
     *
     * @param autoSave Whether the auto-save feature is enabled.
     */
    public void setAutoSave(final boolean autoSave) {

        mAutoSave = autoSave;
    }

    /**
     * Sets the key used to store the selected texts.
     *
     * @param saveKey The key.
     */
    public void setSaveKey(final String saveKey) {

        mSaveKey = saveKey;

        refresh();
    }

    /**
     * Sets the separators used to isolate a token in the selected text.
     *
     * @param separators The separator characters.
     */
    public void setTokenSeparators(final String separators) {

        mTokenSeparators = separators;
    }

    /**
     * Returns the key used by the auto-save feature to temporarily store the last entered text.
     *
     * @see #autoSaveKey(String)
     */
    protected String autoSaveKey() {

        return autoSaveKey(mSaveKey);
    }

    /**
     * Returns the save key used by the auto-save feature to temporarily store the last entered
     * text.
     *
     * @see #autoSaveKey(String)
     */
    protected String autoSavePrefix() {

        final int id = getId();

        if (id != NO_ID) {

            return id + ":";
        }

        return "";
    }

    /**
     * Called when a request to load all the data starting with the specified text is made.
     *
     * @param constraint The starting sequence.
     * @return The list of entries (MUST never be null).
     */
    protected List<CharSequence> onLoad(final CharSequence constraint) {

        return mPersister.load(mSaveKey, constraint);
    }

    /**
     * Called when a request to persist the specified text is made.
     *
     * @param data The text to save.
     */
    protected void onSave(final CharSequence data) {

        final String saveKey = mSaveKey;

        if (!TextUtils.isEmpty(saveKey) && !TextUtils.isEmpty(data)) {

            mPersister.save(saveKey, data);
        }
    }

    private void autoRestore() {

        final String autoSaveKey = autoSaveKey();
        final String constraint = autoSavePrefix();

        if (!TextUtils.isEmpty(autoSaveKey) && !TextUtils.isEmpty(constraint)) {

            final Persister persister = mPersister;

            final List<CharSequence> list = persister.load(autoSaveKey, constraint);

            if (!list.isEmpty()) {

                final CharSequence autoSaved = list.get(0);
                final CharSequence savedText =
                        autoSaved.subSequence(constraint.length(), autoSaved.length()).toString();

                final Editable text = getText();

                if (TextUtils.isEmpty(text) || !savedText.toString().equals(text.toString())) {

                    onSave(savedText);
                }

                persister.remove(autoSaveKey, autoSaved);
            }
        }
    }

    private void autoSave() {

        final String autoSaveKey = autoSaveKey();
        final String prefix = autoSavePrefix();
        final Editable text = getText();

        if (!TextUtils.isEmpty(autoSaveKey) && !TextUtils.isEmpty(prefix) && !TextUtils
                .isEmpty(text)) {

            mPersister.save(autoSaveKey, prefix + text);
        }
    }

    private void init(final AttributeSet attrs, final int defStyle) {

        sViews.put(this, null);

        // Read attributes

        final TypedArray typedArray =
                getContext().obtainStyledAttributes(attrs, R.styleable.FrostTextView, defStyle, 0);

        final boolean autoSave = typedArray.getBoolean(R.styleable.FrostTextView_auto_save, true);
        String saveKey = typedArray.getString(R.styleable.FrostTextView_key);

        if (TextUtils.isEmpty(saveKey)) {

            saveKey = DEFAULT_SAVEKEY;
        }

        final String persisterClassName = typedArray.getString(R.styleable.FrostTextView_persister);

        final int itemLayout = typedArray.getResourceId(R.styleable.FrostTextView_item_layout,
                                                        android.R.layout.simple_dropdown_item_1line);
        final int textViewId = typedArray
                .getResourceId(R.styleable.FrostTextView_text_view_id, android.R.id.text1);

        String tokenSeparators = typedArray.getString(R.styleable.FrostTextView_token_separators);

        if (TextUtils.isEmpty(tokenSeparators)) {

            tokenSeparators = DEFAULT_SEPARATORS;
        }

        typedArray.recycle();

        // Setup the multi autocomplete tokenizer

        setTokenSeparators(tokenSeparators);

        setTokenizer(new SmarkTokenizer());

        // Setup the adapter

        final SimpleCursorAdapter adapter;

        if (VERSION.SDK_INT >= VERSION_CODES.HONEYCOMB) {

            adapter = new SimpleCursorAdapter(getContext(), itemLayout, new ListCursor(),
                                              new String[]{ListCursor.TEXT_COLUMN_NAME},
                                              new int[]{textViewId},
                                              SimpleCursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);

        } else {

            //noinspection deprecation
            adapter = new SimpleCursorAdapter(getContext(), itemLayout, new ListCursor(),
                                              new String[]{ListCursor.TEXT_COLUMN_NAME},
                                              new int[]{textViewId});
        }

        adapter.setFilterQueryProvider(new FilterQueryProvider() {

            @Override
            public Cursor runQuery(final CharSequence charSequence) {

                final List<CharSequence> items = onLoad(charSequence);

                return new ListCursor(items);
            }
        });

        adapter.setCursorToStringConverter(new CursorToStringConverter() {

            @Override
            public CharSequence convertToString(final Cursor cursor) {

                return cursor.getString(ListCursor.TEXT_COLUMN_INDEX);
            }
        });

        setAdapter(adapter);

        // Create the persister instance

        Persister persister = null;

        if (!TextUtils.isEmpty(persisterClassName)) {

            try {

                persister = (Persister) Class.forName(persisterClassName).newInstance();

            } catch (final Exception e) {

                throw new IllegalArgumentException(e);
            }
        }

        if (persister == null) {

            persister = new SharedPreferencesPersister();
        }

        setPersister(persister);

        // Final setup

        setAutoSave(autoSave);
        setSaveKey(saveKey);
    }

    private void refresh() {

        final ListAdapter adapter = mAdapter;

        if (adapter instanceof BaseAdapter) {

            ((BaseAdapter) adapter).notifyDataSetChanged();
        }
    }

    private static class ListCursor extends AbstractCursor {

        public static final int ID_COLUMN_INDEX = 0;

        public static final String ID_COLUMN_NAME = "_id";

        public static final int TEXT_COLUMN_INDEX = 1;

        public static final String TEXT_COLUMN_NAME = "text";

        private static final String[] COLUMN_NAMES = new String[]{ID_COLUMN_NAME, TEXT_COLUMN_NAME};

        private final List<? extends CharSequence> mItems;

        public ListCursor() {

            mItems = Collections.emptyList();
        }

        public ListCursor(final List<? extends CharSequence> items) {

            mItems = items;
        }

        @Override
        public int getCount() {

            return mItems.size();
        }

        @Override
        public String[] getColumnNames() {

            return COLUMN_NAMES;
        }

        @Override
        public String getString(final int i) {

            final CharSequence value = mItems.get(getPosition());

            if (value != null) {

                final String text = value.toString();

                if (i == ID_COLUMN_INDEX) {

                    return Integer.toString(text.hashCode());
                }

                return text;
            }

            return null;
        }

        @Override
        public short getShort(final int i) {

            final String text = getString(i);

            if (!TextUtils.isEmpty(text)) {

                if (i == ID_COLUMN_INDEX) {

                    return (short) text.hashCode();
                }

                try {

                    return Short.parseShort(text);

                } catch (final NumberFormatException ignored) {

                }
            }

            return 0;
        }

        @Override
        public int getInt(final int i) {

            final String text = getString(i);

            if (!TextUtils.isEmpty(text)) {

                if (i == ID_COLUMN_INDEX) {

                    return text.hashCode();
                }

                try {

                    return Integer.parseInt(text);

                } catch (final NumberFormatException ignored) {

                }
            }

            return 0;
        }

        @Override
        public long getLong(final int i) {

            final String text = getString(i);

            if (!TextUtils.isEmpty(text)) {

                if (i == ID_COLUMN_INDEX) {

                    return text.hashCode();
                }

                try {

                    return Long.parseLong(text);

                } catch (final NumberFormatException ignored) {

                }
            }

            return 0;
        }

        @Override
        public float getFloat(final int i) {

            final String text = getString(i);

            if (!TextUtils.isEmpty(text)) {

                if (i == ID_COLUMN_INDEX) {

                    return text.hashCode();
                }

                try {

                    return Float.parseFloat(text);

                } catch (final NumberFormatException ignored) {

                }
            }

            return 0;
        }

        @Override
        public double getDouble(final int i) {

            final String text = getString(i);

            if (!TextUtils.isEmpty(text)) {

                if (i == ID_COLUMN_INDEX) {

                    return text.hashCode();
                }

                try {

                    return Double.parseDouble(text);

                } catch (final NumberFormatException ignored) {

                }
            }

            return 0;
        }

        @Override
        public boolean isNull(final int i) {

            return (getString(i) == null);
        }
    }

    private static class MockPersister implements Persister {

        @Override
        public void clear() {

        }

        @Override
        public int getCount(final String savedKey) {

            return 0;
        }

        @Override
        public List<CharSequence> load(final String saveKey, final CharSequence constraint) {

            return Collections.emptyList();
        }

        @Override
        public void remove(final String saveKey) {

        }

        @Override
        public void remove(final String saveKey, final CharSequence... data) {

        }

        @Override
        public void remove(final String saveKey, final Collection<CharSequence> data) {

        }

        @Override
        public void save(final String saveKey, final CharSequence data) {

        }

        @Override
        public void setContext(final Context context) {

        }

        @Override
        public void setObserver(final DataObserver observer) {

        }
    }

    private class SmarkTokenizer implements Tokenizer {

        @Override
        public int findTokenStart(final CharSequence text, final int cursor) {

            final String separators = mTokenSeparators;

            for (int i = (cursor - 1); i >= 0; --i) {

                if (separators.indexOf(text.charAt(i)) >= 0) {

                    return i + 1;
                }
            }

            return 0;
        }

        @Override
        public int findTokenEnd(final CharSequence text, final int cursor) {

            final String separators = mTokenSeparators;

            final int length = text.length();

            for (int i = cursor; i < length; ++i) {

                if (separators.indexOf(text.charAt(i)) >= 0) {

                    return i;
                }
            }

            return length;
        }

        @Override
        public CharSequence terminateToken(final CharSequence text) {

            return text + mTokenSeparators;
        }
    }
}
