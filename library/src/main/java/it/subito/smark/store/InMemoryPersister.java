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
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class InMemoryPersister implements Persister {

    private Map<String, Set<CharSequence>> mStore;

    public InMemoryPersister() {

        mStore = new HashMap<String, Set<CharSequence>>();
    }

    @Override
    public void setContext(final Context context) {

        //No operation here
    }

    @Override
    public void save(final String saveKey, final CharSequence data) {

        if (TextUtils.isEmpty(data)) {

            return;
        }

        Set<CharSequence> list = mStore.get(saveKey);

        if (list == null) {

            list = new HashSet<CharSequence>(1);
            mStore.put(saveKey, list);
        }

        list.add(data.toString());
    }

    @Override
    public List<CharSequence> load(final String saveKey, final String constraint) {

        final Set<CharSequence> list = mStore.get(saveKey);

        if (list != null) {

            List<CharSequence> filtered = new ArrayList<CharSequence>(list.size());

            for (CharSequence item : list) {

                //if (item.toString().startsWith(constraint)) {

                    filtered.add(item);
                //}
            }

            return Collections.unmodifiableList(filtered);
        }

        return Collections.emptyList();
    }

    @Override
    public void remove(final String saveKey) {

        mStore.remove(saveKey);
    }

    @Override
    public void clear() {

        mStore.clear();
    }

    @Override
    public int getCount(final String saveKey) {

        final Set<CharSequence> list = mStore.get(saveKey);

        if (list != null) {

            return list.size();
        }

        return 0;
    }
}
