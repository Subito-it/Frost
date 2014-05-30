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

import java.util.List;

public interface Persister {

    public void clear();

    public int getCount(String savedKey);

    public List<CharSequence> load(String saveKey, CharSequence constraint);

    public void remove(String saveKey);

    public void save(String saveKey, CharSequence data);

    public void setContext(Context context);

    public void setObserver(DataObserver observer);

    public interface DataObserver {

        public void onDataChanged();
    }
}
