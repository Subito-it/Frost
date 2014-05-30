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

package it.subito.smark;

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

import java.util.Collections;
import java.util.List;

import it.subito.smark.store.InMemoryPersister;
import it.subito.smark.store.Persister;
import it.subito.smark.store.Persister.DataObserver;

public class SmarkTextView extends MultiAutoCompleteTextView implements DataObserver {

    private static final String DEFAULT_SAVEKEY = "default_smark";

    private ListAdapter mAdapter;

    private boolean mAutoSave;

    private Persister mPersister;

    private String mSaveKey;

    public SmarkTextView(final Context context) {

        super(context);
        init(null, 0);
    }

    public SmarkTextView(final Context context, final AttributeSet attrs) {

        super(context, attrs);
        init(attrs, 0);
    }

    public SmarkTextView(final Context context, final AttributeSet attrs, final int defStyle) {

        super(context, attrs, defStyle);
        init(attrs, defStyle);
    }

    @Override
    public void onDataChanged() {

        refresh();
    }

    public void save() {

        onPersistValue();
    }

    @Override
    public <T extends ListAdapter & Filterable> void setAdapter(final T adapter) {

        mAdapter = adapter;

        super.setAdapter(adapter);
    }

    @Override
    protected void onDetachedFromWindow() {

        // TODO: autosave as parameter
        onPersistValue();

        super.onDetachedFromWindow();
    }

    protected void onPersistValue() {

        final String saveKey = mSaveKey;
        final Editable text = getText();

        if (!TextUtils.isEmpty(saveKey) && !TextUtils.isEmpty(text)) {

            mPersister.save(saveKey, text);
        }
    }

    private void init(final AttributeSet attrs, final int defStyle) {

        final TypedArray typedArray =
                getContext().obtainStyledAttributes(attrs, R.styleable.SmarkTextView, defStyle, 0);

        mAutoSave = typedArray.getBoolean(R.styleable.SmarkTextView_auto_save, false);
        mSaveKey = typedArray.getString(R.styleable.SmarkTextView_key);

        if (TextUtils.isEmpty(mSaveKey)) {

            mSaveKey = DEFAULT_SAVEKEY;
        }

        final String persisterClassName = typedArray.getString(R.styleable.SmarkTextView_persister);

        if (!TextUtils.isEmpty(persisterClassName)) {

            try {

                mPersister = (Persister) Class.forName(persisterClassName).newInstance();

            } catch (Exception e) {

                throw new IllegalArgumentException(e);
            }
        }

        if (mPersister == null) {

            mPersister = new InMemoryPersister();
        }

        mPersister.setContext(getContext());
        mPersister.setObserver(this);

        typedArray.recycle();

        setThreshold(1);

        // TODO: separator as parameter
        setTokenizer(new BlankSpaceTokenizer());

        final SimpleCursorAdapter adapter;

        // TODO: row layout as parameter
        // TODO: text id as parameter
        if (VERSION.SDK_INT >= VERSION_CODES.HONEYCOMB) {

            adapter = new SimpleCursorAdapter(getContext(),
                                              android.R.layout.simple_dropdown_item_1line,
                                              new ListCursor(),
                                              new String[]{ListCursor.TEXT_COLUMN_NAME},
                                              new int[]{android.R.id.text1}, 0);

        } else {

            adapter = new SimpleCursorAdapter(getContext(),
                                              android.R.layout.simple_dropdown_item_1line,
                                              new ListCursor(),
                                              new String[]{ListCursor.TEXT_COLUMN_NAME},
                                              new int[]{android.R.id.text1});
        }

        adapter.setFilterQueryProvider(new FilterQueryProvider() {

            @Override
            public Cursor runQuery(final CharSequence charSequence) {

                final List<CharSequence> items = mPersister.load(mSaveKey, charSequence);

                return new ListCursor(items);
            }
        });

        adapter.setCursorToStringConverter(new CursorToStringConverter() {

            @Override
            public CharSequence convertToString(final Cursor cursor) {

                return cursor.getString(1);
            }
        });

        setAdapter(adapter);
    }

    private void refresh() {

        final ListAdapter adapter = mAdapter;

        if (adapter instanceof BaseAdapter) {

            ((BaseAdapter) adapter).notifyDataSetChanged();
        }
    }

    private static class BlankSpaceTokenizer implements Tokenizer {

        @Override
        public int findTokenStart(final CharSequence text, final int cursor) {

            for (int i = cursor - 1; i >= 0; i--) {

                if (Character.isSpaceChar(text.charAt(i))) {

                    return i + 1;
                }
            }

            return 0;
        }

        @Override
        public int findTokenEnd(final CharSequence text, final int cursor) {

            final int length = text.length();

            for (int i = cursor; i < length; i++) {

                if (Character.isSpaceChar(text.charAt(i))) {

                    return i;
                }
            }

            return length;
        }

        @Override
        public CharSequence terminateToken(final CharSequence text) {

            return text;
        }
    }

    private static class ListCursor extends AbstractCursor {

        public static final String ID_COLUMN_NAME = "_id";

        public static final String TEXT_COLUMN_NAME = "text";

        private static final String[] COLUMN_NAMES = new String[]{ID_COLUMN_NAME, TEXT_COLUMN_NAME};

        private final List<CharSequence> mItems;

        public ListCursor() {

            mItems = Collections.emptyList();
        }

        public ListCursor(final List<CharSequence> items) {

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

                final String string = value.toString();

                if (i == 0) {

                    return Integer.toString(string.hashCode());
                }

                return string;
            }

            return null;
        }

        @Override
        public short getShort(final int i) {

            final String string = getString(i);

            if (!TextUtils.isEmpty(string)) {

                if (i == 0) {

                    return (short) string.hashCode();
                }

                try {

                    return Short.parseShort(string);

                } catch (final NumberFormatException ignored) {

                }
            }

            return 0;
        }

        @Override
        public int getInt(final int i) {

            final String string = getString(i);

            if (!TextUtils.isEmpty(string)) {

                if (i == 0) {

                    return string.hashCode();
                }

                try {

                    return Integer.parseInt(string);

                } catch (final NumberFormatException ignored) {

                }
            }

            return 0;
        }

        @Override
        public long getLong(final int i) {

            final String string = getString(i);

            if (!TextUtils.isEmpty(string)) {

                if (i == 0) {

                    return string.hashCode();
                }

                try {

                    return Long.parseLong(string);

                } catch (final NumberFormatException ignored) {

                }
            }

            return 0;
        }

        @Override
        public float getFloat(final int i) {

            final String string = getString(i);

            if (!TextUtils.isEmpty(string)) {

                if (i == 0) {

                    return string.hashCode();
                }

                try {

                    return Float.parseFloat(string);

                } catch (final NumberFormatException ignored) {

                }
            }

            return 0;
        }

        @Override
        public double getDouble(final int i) {

            final String string = getString(i);

            if (!TextUtils.isEmpty(string)) {

                if (i == 0) {

                    return string.hashCode();
                }

                try {

                    return Double.parseDouble(string);

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
}
