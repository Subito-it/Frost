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
import android.os.Build;
import android.text.Editable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.widget.ArrayAdapter;
import android.widget.Filterable;
import android.widget.ListAdapter;
import android.widget.MultiAutoCompleteTextView;

import java.util.List;

import it.subito.smark.store.InMemoryPersister;
import it.subito.smark.store.Persister;

public class SmarkTextView extends MultiAutoCompleteTextView {

    private static final String DEFAULT_SAVEKEY = "default_smark";

    private ArrayAdapter<CharSequence> mAdapter;

    private boolean mAutoSave;

    private String mSaveKey;

    private Persister mPersister;

    public SmarkTextView(Context context) {

        super(context);
        init(null, 0);
    }

    public SmarkTextView(Context context, AttributeSet attrs) {

        super(context, attrs);
        init(attrs, 0);
    }

    public SmarkTextView(Context context, AttributeSet attrs, int defStyle) {

        super(context, attrs, defStyle);
        init(attrs, defStyle);
    }

    public void save() {

        onPersistValue();
    }

    @Override
    protected void onDetachedFromWindow() {

        onPersistValue();

        super.onDetachedFromWindow();
    }

    protected void onPersistValue() {

        final String saveKey = mSaveKey;
        final Editable text = getText();

        if (!TextUtils.isEmpty(saveKey) && !TextUtils.isEmpty(text)) {

            mPersister.save(saveKey, text);

            refresh();
        }
    }

    private void refresh() {

        mAdapter.clear();

        // TODO: add constraint
        final List<CharSequence> items = mPersister.load(mSaveKey, getText().toString());

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {

            if (items.isEmpty()) {

                return;
            }

            for (final CharSequence item : items) {

                mAdapter.setNotifyOnChange(false);

                mAdapter.add(item);
            }

            mAdapter.notifyDataSetChanged();

        } else {

            mAdapter.addAll(items);
        }
    }

    private void init(AttributeSet attrs, int defStyle) {

        final TypedArray a =
                getContext().obtainStyledAttributes(attrs, R.styleable.SmarkTextView, defStyle, 0);

        mAutoSave = a.getBoolean(R.styleable.SmarkTextView_auto_save, false);
        mSaveKey = a.getString(R.styleable.SmarkTextView_key);

        if (TextUtils.isEmpty(mSaveKey)) {

            mSaveKey = DEFAULT_SAVEKEY;
        }

        final String persisterClassName = a.getString(R.styleable.SmarkTextView_persister);

        if (TextUtils.isEmpty(persisterClassName)) {

            mPersister = new InMemoryPersister();

        } else {

            try {

                mPersister = (Persister) Class.forName(persisterClassName).newInstance();

            } catch (Exception e) {

                throw new IllegalArgumentException(e);
            }
        }

        mPersister.setContext(getContext());

        a.recycle();

        setThreshold(1);

        setTokenizer(new Tokenizer() {

            @Override
            public int findTokenStart(CharSequence text, int cursor) {

                return 0;
            }

            @Override
            public int findTokenEnd(CharSequence text, int cursor) {

                return text.length();
            }

            @Override
            public CharSequence terminateToken(CharSequence text) {

                return "";
            }
        });

        setAdapter(new ArrayAdapter<CharSequence>(getContext(),
                                                  android.R.layout.simple_dropdown_item_1line));
    }

    @Override
    public <T extends ListAdapter & Filterable> void setAdapter(T adapter) {

        try {

            mAdapter = (ArrayAdapter<CharSequence>) adapter;

            super.setAdapter(adapter);

        } catch (ClassCastException cce) {

            throw new IllegalArgumentException("Suca!");
        }
    }
}
