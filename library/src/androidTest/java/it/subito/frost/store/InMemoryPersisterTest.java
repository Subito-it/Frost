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
package it.subito.frost.store;

import android.test.AndroidTestCase;

import java.util.Arrays;
import java.util.List;

import static org.fest.assertions.api.Assertions.assertThat;

public class InMemoryPersisterTest extends AndroidTestCase {

    private Persister mPersister;

    public void testClear() {

        final Persister persister = mPersister;
        persister.save("test2", "aaaa");

        persister.clear();

        assertThat(persister.getCount("test")).isEqualTo(0);
        assertThat(persister.getCount("test2")).isEqualTo(0);
    }

    public void testLoad() {

        final Persister persister = mPersister;

        final List<CharSequence> list = persister.load("test", "a");

        assertThat(list).isNotEmpty();
        assertThat(list).containsExactly("aaa", "aaaa");

        assertThat(persister.getCount("test")).isEqualTo(4);
    }

    public void testRemove() {

        final Persister persister = mPersister;

        persister.remove("test");

        List<CharSequence> list = persister.load("test", "a");

        assertThat(list).isEmpty();

        assertThat(persister.getCount("test")).isEqualTo(0);
    }

    public void testRemoveArray() {

        final Persister persister = mPersister;

        persister.remove("test", "aaa", "aaaa");

        List<CharSequence> list = persister.load("test", "a");

        assertThat(list).isEmpty();

        assertThat(persister.getCount("test")).isEqualTo(2);
    }

    public void testRemoveList() {

        final Persister persister = mPersister;

        persister.remove("test", Arrays.asList((CharSequence) "aaa", "aaaa"));

        final List<CharSequence> list = persister.load("test", "a");

        assertThat(list).isEmpty();

        assertThat(persister.getCount("test")).isEqualTo(2);
    }

    public void testRemoveNone() {

        final Persister persister = mPersister;

        persister.remove("test", "a");

        final List<CharSequence> list = persister.load("test", "a");

        assertThat(list).isNotEmpty();
        assertThat(list).containsExactly("aaa", "aaaa");

        assertThat(persister.getCount("test")).isEqualTo(4);
    }

    public void testRemovePartialArray() {

        final Persister persister = mPersister;

        persister.remove("test", "aaa", "aaab");

        final List<CharSequence> list = persister.load("test", "a");

        assertThat(list).isNotEmpty();
        assertThat(list).containsExactly("aaaa");

        assertThat(persister.getCount("test")).isEqualTo(3);
    }

    public void testRemovePartialList() {

        final Persister persister = mPersister;

        persister.remove("test", Arrays.asList((CharSequence) "aaa", "aaab"));

        final List<CharSequence> list = persister.load("test", "a");

        assertThat(list).isNotEmpty();
        assertThat(list).containsExactly("aaaa");

        assertThat(persister.getCount("test")).isEqualTo(3);
    }

    public void testRemoveSingle() {

        final Persister persister = mPersister;

        persister.remove("test", "aaa");

        final List<CharSequence> list = persister.load("test", "a");

        assertThat(list).isNotEmpty();
        assertThat(list).containsExactly("aaaa");

        assertThat(persister.getCount("test")).isEqualTo(3);
    }

    protected Persister buildPersister() {

        return new InMemoryPersister();
    }

    @Override
    protected void setUp() throws Exception {

        super.setUp();

        mPersister = buildPersister();
        initPersister(mPersister);
    }

    private void initPersister(final Persister persister) {

        persister.setContext(getContext());

        persister.clear();

        persister.save("test", "aaa");
        persister.save("test", "aaaa");
        persister.save("test", "bbb");
        persister.save("test", "ccc");
    }
}
