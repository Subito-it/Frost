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
package it.subito.smark.test.persister;

import android.test.AndroidTestCase;

import java.util.Arrays;
import java.util.List;

import it.subito.smark.store.InMemoryPersister;

import static org.fest.assertions.api.Assertions.assertThat;

public class InMemoryPersisterTest extends AndroidTestCase {

    public void testLoad() {

        InMemoryPersister persister = buildPersister();

        List<CharSequence> list = persister.load("test", "a");

        assertThat(list).isNotEmpty();
        assertThat(list).isEqualTo(Arrays.<CharSequence>asList("aaa", "aaaa"));

        assertThat(persister.getCount("test")).isEqualTo(4);
    }

    public void testRemove() {

        InMemoryPersister persister = buildPersister();

        persister.remove("test");

        List<CharSequence> list = persister.load("test", "a");

        assertThat(list).isEmpty();

        assertThat(persister.getCount("test")).isEqualTo(0);
    }

    public void testClear() {

        InMemoryPersister persister = buildPersister();
        persister.save("test2", "aaaa");

        persister.clear();

        assertThat(persister.getCount("test")).isEqualTo(0);
        assertThat(persister.getCount("test2")).isEqualTo(0);
    }

    private InMemoryPersister buildPersister() {

        InMemoryPersister persister = new InMemoryPersister();

        persister.save("test", "aaa");
        persister.save("test", "aaaa");
        persister.save("test", "bbb");
        persister.save("test", "ccc");

        return persister;
    }
}
