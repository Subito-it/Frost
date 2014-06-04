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

import java.util.Collection;
import java.util.List;

/**
 * Interface defining an object managing saved data associated with a specific autocomplete view.
 * <p/>
 * Note that the implementing class should define a default constructor since it might be instatiate
 * via reflection.
 */
public interface Persister {

    /**
     * Clears all the data managed by this persister instance.
     */
    public void clear();

    /**
     * Gets the total count of entries in the list associated with the specified key.
     *
     * @param savedKey The save key.
     * @return The entry count.
     */
    public int getCount(String savedKey);

    /**
     * Loads the list of entries associated with the specified key and starting with the specified
     * sequence of characters.
     *
     * @param saveKey    The save key.
     * @param constraint The starting sequence.
     * @return The list of entries (MUST never be null).
     */
    public List<CharSequence> load(String saveKey, CharSequence constraint);

    /**
     * Removes all the entries associated with the specified key.
     *
     * @param saveKey The save key.
     */
    public void remove(String saveKey);

    /**
     * Removes all the passed entries from the ones associated with the specified key.
     *
     * @param saveKey The save key.
     * @param data    The entries to remove.
     */
    public void remove(String saveKey, CharSequence... data);

    /**
     * Removes all the passed entries from the ones associated with the specified key.
     *
     * @param saveKey The save key.
     * @param data    The entries to remove.
     */
    public void remove(String saveKey, Collection<CharSequence> data);

    /**
     * Saves the passed entry by adding it to the ones already associated with the specified key.
     *
     * @param saveKey The save key.
     * @param data    The entry to add.
     */
    public void save(String saveKey, CharSequence data);

    /**
     * Sets the context of this persister instance.
     *
     * @param context
     */
    public void setContext(Context context);

    /**
     * Sets the data observer to be notified each time the saved data change.
     *
     * @param observer The observer instance or null.
     */
    public void setObserver(DataObserver observer);

    /**
     * Interface defining an observer of data changes.
     */
    public interface DataObserver {

        /**
         * Called when the saved data change.
         */
        public void onDataChanged();
    }
}
