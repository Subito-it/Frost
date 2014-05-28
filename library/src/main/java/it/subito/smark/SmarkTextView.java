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
import android.widget.MultiAutoCompleteTextView;

import java.util.List;

import it.subito.smark.store.InMemoryPersister;
import it.subito.smark.store.Persister;

public class SmarkTextView extends MultiAutoCompleteTextView {

    private PersisterAdapter mAdapter;

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
            mAdapter.refresh();
        }
    }

    private void init(AttributeSet attrs, int defStyle) {

        final TypedArray a =
                getContext().obtainStyledAttributes(attrs, R.styleable.SmarkTextView, defStyle, 0);

        mAutoSave = a.getBoolean(R.styleable.SmarkTextView_autoSave, false);
        mSaveKey = a.getString(R.styleable.SmarkTextView_key);

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

        // TODO: add resource id
        mAdapter = new PersisterAdapter(getContext(), -1);

        setAdapter(mAdapter);
    }

    private class PersisterAdapter extends ArrayAdapter<CharSequence> {

        public PersisterAdapter(final Context context, final int resource) {

            super(context, resource);

            refresh();
        }

        private void refresh() {

            clear();

            // TODO: add constraint
            final List<CharSequence> items = mPersister.load(mSaveKey, "");

            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {

                if (items.isEmpty()) {

                    return;
                }

                for (final CharSequence item : items) {

                    setNotifyOnChange(false);

                    add(item);
                }

                notifyDataSetChanged();

            } else {

                addAll(items);
            }
        }
    }
}
