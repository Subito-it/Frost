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
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemoryPersister implements Persister {

    private DataObserver mDataObserver;

    private Map<String, ArrayList<String>> mStore;

    public InMemoryPersister() {

        mStore = new HashMap<String, ArrayList<String>>();
    }

    @Override
    public void clear() {

        mStore.clear();
    }

    @Override
    public int getCount(final String saveKey) {

        final ArrayList<String> list = mStore.get(saveKey);

        if (list != null) {

            return list.size();
        }

        return 0;
    }

    @Override
    public List<CharSequence> load(final String saveKey, final CharSequence constraint) {

        final ArrayList<String> list = mStore.get(saveKey);

        if (list == null) {

            return Collections.emptyList();
        }

        final ArrayList<CharSequence> filtered = new ArrayList<CharSequence>();

        if (!TextUtils.isEmpty(constraint)) {

            final String start = constraint.toString();

            for (final String s : list) {

                if (s.startsWith(start)) {

                    filtered.add(s);
                }
            }

        } else {

            filtered.addAll(list);
        }

        return Collections.unmodifiableList(filtered);
    }

    @Override
    public void remove(final String saveKey) {

        mStore.remove(saveKey);
    }

    @Override
    public void save(final String saveKey, final CharSequence data) {

        if (TextUtils.isEmpty(data)) {

            return;
        }

        final Map<String, ArrayList<String>> store = mStore;

        ArrayList<String> list = store.get(saveKey);

        if (list == null) {

            list = new ArrayList<String>(1);
            store.put(saveKey, list);
        }

        final String string = data.toString();

        if (!list.contains(string)) {

            list.add(string);

            final DataObserver observer = mDataObserver;

            if (observer != null) {

                observer.onDataChanged();
            }
        }
    }

    @Override
    public void setContext(final Context context) {

        //No operation here
    }

    @Override
    public void setObserver(final DataObserver observer) {

        mDataObserver = observer;
    }
}
