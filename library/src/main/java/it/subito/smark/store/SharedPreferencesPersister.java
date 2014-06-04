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
package it.subito.smark.store;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.text.TextUtils;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Class implementing a {@link Persister} storing all the data into the application shared
 * preferences.
 */
public class SharedPreferencesPersister implements Persister, OnSharedPreferenceChangeListener {

    public static final String CHARSET_NAME = "UTF-8";

    public static final String STRING_SEPARATOR = "&";

    private final ArrayList<String> mCachedList = new ArrayList<String>();

    private boolean mCacheUpdated;

    private DataObserver mDataObserver;

    private String mLastConstraint = "";

    private SharedPreferences mSharedPref;

    @Override
    public void clear() {

        final Editor editor = mSharedPref.edit();

        editor.clear();

        commit(editor);
    }

    @Override
    public int getCount(final String savedKey) {

        return load(savedKey, null).size();
    }

    @Override
    public List<CharSequence> load(final String saveKey, final CharSequence constraint) {

        final String start;

        if (!TextUtils.isEmpty(constraint)) {

            start = constraint.toString();

        } else {

            start = "";
        }

        final ArrayList<String> sorted = mCachedList;

        if (!mCacheUpdated || !mLastConstraint.equals(start)) {

            mCacheUpdated = false;

            mLastConstraint = start;

            final HashSet<String> values = new HashSet<String>();

            getStringSet(saveKey, values);

            sorted.clear();

            if (!TextUtils.isEmpty(start)) {

                for (final String s : values) {

                    if (s.startsWith(start)) {

                        sorted.add(s);
                    }
                }

            } else {

                sorted.addAll(values);
            }

            Collections.sort(sorted);
        }

        return new ArrayList<CharSequence>(sorted);
    }

    @Override
    public void remove(final String saveKey) {

        final Editor editor = mSharedPref.edit();

        editor.remove(saveKey);

        commit(editor);
    }

    @Override
    public void remove(final String saveKey, final CharSequence... data) {

        if ((data != null) && (data.length > 0)) {

            remove(saveKey, Arrays.asList(data));
        }
    }

    @Override
    public void remove(final String saveKey, final Collection<CharSequence> data) {

        final SharedPreferences sharedPref = mSharedPref;

        final HashSet<String> values = new HashSet<String>();

        getStringSet(saveKey, values);

        if (values.isEmpty()) {

            return;
        }

        boolean changed = false;

        for (final CharSequence datum : data) {

            if (TextUtils.isEmpty(datum)) {

                continue;
            }

            if (values.remove(datum.toString())) {

                changed = true;
            }
        }

        if (changed) {

            final Editor editor = sharedPref.edit();

            if (values.isEmpty()) {

                editor.remove(saveKey);

            } else {

                putStringSet(editor, saveKey, values);
            }

            commit(editor);
        }
    }

    @Override
    public void save(final String saveKey, final CharSequence data) {

        if (TextUtils.isEmpty(data)) {

            return;
        }

        final SharedPreferences sharedPref = mSharedPref;

        final HashSet<String> values = new HashSet<String>();

        getStringSet(saveKey, values);

        if (values.add(data.toString())) {

            final Editor editor = sharedPref.edit();

            putStringSet(editor, saveKey, values);

            commit(editor);
        }
    }

    @Override
    public void setContext(final Context context) {

        mSharedPref = context.getSharedPreferences(context.getPackageName() + ".smarkHistory",
                                                   Context.MODE_PRIVATE);
        mSharedPref.registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void setObserver(final DataObserver observer) {

        mDataObserver = observer;
    }

    @Override
    public void onSharedPreferenceChanged(final SharedPreferences preferences, final String s) {

        mCacheUpdated = false;

        mCachedList.clear();

        final DataObserver observer = mDataObserver;

        if (observer != null) {

            observer.onDataChanged();
        }
    }

    private void commit(final Editor editor) {

        if (VERSION.SDK_INT > VERSION_CODES.FROYO) {

            editor.apply();

        } else {

            editor.commit();
        }

        mCacheUpdated = false;

        mCachedList.clear();
    }

    private void getStringSet(final String saveKey, final Set<String> values) {

        if (VERSION.SDK_INT >= VERSION_CODES.HONEYCOMB) {

            values.addAll(mSharedPref.getStringSet(saveKey, values));

        } else {

            final String text = mSharedPref.getString(saveKey, null);

            if (text == null) {

                return;
            }

            try {

                for (final String s : text.split(STRING_SEPARATOR)) {

                    values.add(URLDecoder.decode(s, CHARSET_NAME));
                }

            } catch (final UnsupportedEncodingException ignored) {

            }
        }
    }

    private void putStringSet(final Editor editor, final String saveKey, final Set<String> values) {

        if (VERSION.SDK_INT >= VERSION_CODES.HONEYCOMB) {

            editor.putStringSet(saveKey, values);

        } else {

            final StringBuilder builder = new StringBuilder();

            for (final String value : values) {

                try {

                    final String encoded = URLEncoder.encode(value, CHARSET_NAME);

                    if (builder.length() > 0) {

                        builder.append(STRING_SEPARATOR);
                    }

                    builder.append(encoded);

                } catch (final UnsupportedEncodingException ignored) {

                }
            }

            editor.putString(saveKey, builder.toString());
        }
    }
}
