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
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

public class SharedPreferencesPersister implements Persister, OnSharedPreferenceChangeListener {

    public static final String CHARSET_NAME = "UTF-8";

    public static final String STRING_SEPARATOR = "&";

    private DataObserver mObserver;

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

        if (VERSION.SDK_INT >= VERSION_CODES.HONEYCOMB) {

            final HashSet<String> values = new HashSet<String>();

            mSharedPref.getStringSet(saveKey, values);

            final ArrayList<CharSequence> filtered = new ArrayList<CharSequence>();

            if (!TextUtils.isEmpty(constraint)) {

                final String start = constraint.toString();

                for (final String s : values) {

                    if (s.startsWith(start)) {

                        filtered.add(s);
                    }
                }

            } else {

                filtered.addAll(values);
            }

            return Collections.unmodifiableList(filtered);
        }

        final String string = mSharedPref.getString(saveKey, null);

        if (string == null) {

            return Collections.emptyList();
        }

        final ArrayList<CharSequence> filtered = new ArrayList<CharSequence>();

        final String start;

        if (!TextUtils.isEmpty(constraint)) {

            start = constraint.toString();

        } else {

            start = "";
        }

        try {

            for (final String s : string.split(STRING_SEPARATOR)) {

                final String decoded = URLDecoder.decode(s, CHARSET_NAME);

                if (decoded.startsWith(start)) {

                    filtered.add(decoded);
                }
            }

        } catch (final UnsupportedEncodingException ignored) {

        }

        return Collections.unmodifiableList(filtered);
    }

    @Override
    public void remove(final String saveKey) {

        final Editor editor = mSharedPref.edit();

        editor.remove(saveKey);

        commit(editor);
    }

    @Override
    public void save(final String saveKey, final CharSequence data) {

        if (data == null) {

            return;
        }

        final Editor editor = mSharedPref.edit();

        if (VERSION.SDK_INT >= VERSION_CODES.HONEYCOMB) {

            final HashSet<String> values = new HashSet<String>();

            mSharedPref.getStringSet(saveKey, values);

            values.add(data.toString());

            editor.putStringSet(saveKey, values);

        } else {

            try {

                final String encoded = URLEncoder.encode(data.toString(), CHARSET_NAME);

                final String string = mSharedPref.getString(saveKey, null);

                if (string == null) {

                    editor.putString(saveKey, encoded);

                } else if (!string.contains(encoded)) {

                    editor.putString(saveKey, string + STRING_SEPARATOR + encoded);
                }

            } catch (final UnsupportedEncodingException ignored) {

            }
        }

        commit(editor);
    }

    @Override
    public void setContext(final Context context) {

        mSharedPref = context.getSharedPreferences(context.getPackageName() + ".smarkHistory",
                                                   Context.MODE_PRIVATE);
        mSharedPref.registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void setObserver(final DataObserver observer) {

        mObserver = observer;
    }

    @Override
    public void onSharedPreferenceChanged(final SharedPreferences preferences, final String s) {

        if (mObserver != null) {

            mObserver.onDataChanged();
        }
    }

    private void commit(final Editor editor) {

        if (VERSION.SDK_INT > VERSION_CODES.FROYO) {

            editor.apply();

        } else {

            editor.commit();
        }
    }
}
