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

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.test.ActivityInstrumentationTestCase2;
import android.view.View;

import com.google.android.apps.common.testing.ui.espresso.NoMatchingRootException;

import it.subito.smark.TestActivity.ActivityUnderTest;

import static com.google.android.apps.common.testing.ui.espresso.Espresso.onView;
import static com.google.android.apps.common.testing.ui.espresso.action.ViewActions.clearText;
import static com.google.android.apps.common.testing.ui.espresso.action.ViewActions.click;
import static com.google.android.apps.common.testing.ui.espresso.action.ViewActions.typeText;
import static com.google.android.apps.common.testing.ui.espresso.assertion.ViewAssertions.matches;
import static com.google.android.apps.common.testing.ui.espresso.matcher.RootMatchers.withDecorView;
import static com.google.android.apps.common.testing.ui.espresso.matcher.ViewMatchers.isDisplayed;
import static com.google.android.apps.common.testing.ui.espresso.matcher.ViewMatchers.withId;
import static com.google.android.apps.common.testing.ui.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.Matchers.is;

public class TestActivity extends ActivityInstrumentationTestCase2<ActivityUnderTest> {

    public TestActivity() {

        super(ActivityUnderTest.class);
    }

    @Override
    public void setUp() throws Exception {

        super.setUp();
        // Espresso will not launch our activity for us, we must launch it via getActivity().
        getActivity();

        ((SmarkTextView) getActivity().findViewById(R.id.smark_view)).clearHistory();
    }

    /**
     * bug #117
     */
    public void testSaveAfterActivityChanging() {

        onView(withId(R.id.smark_view_persistent)).perform(typeText("aaaa"));
        onView(withId(R.id.spawn_button)).perform(click());

        onView(withId(R.id.back_button)).perform(click());

        onView(withId(R.id.smark_view_persistent)).perform(clearText());
        onView(withId(R.id.smark_view_persistent)).perform(typeText("bbbb"));
        onView(withId(R.id.spawn_button)).perform(click());

        onView(withId(R.id.smark_view_persistent)).perform(clearText());
        onView(withId(R.id.smark_view_persistent)).perform(typeText("b"));
        onView(withText("bbbb"))
                .inRoot(withDecorView(not(is(getActivity().getWindow().getDecorView()))))
                .perform(click());

        onView(withId(R.id.smark_view_persistent)).check(matches(withText("bbbb")));
    }

    public void testTextInSameElementType() {

        onView(withId(R.id.smark_view_persistent)).perform(typeText("test1"));
        onView(withId(R.id.spawn_button)).perform(click());

        onView(withId(R.id.back_button)).perform(click());

        onView(withId(R.id.smark_view_persistent_2)).perform(typeText("t"));
        onView(withText("test1"))
                .inRoot(withDecorView(not(is(getActivity().getWindow().getDecorView()))))
                .perform(click());

        onView(withId(R.id.smark_view_persistent_2)).check(matches(withText("test1")));
    }

    public void testTextAutosave() {

        onView(withId(R.id.smark_view_persistent)).perform(typeText("test1"));
        onView(withId(R.id.smark_view_persistent)).check(matches(withText("test1")));

        final SmarkTextView smarkTextView =
                (SmarkTextView) getActivity().findViewById(R.id.smark_view_persistent);
        smarkTextView.setAutoSave(true);
        smarkTextView.getPersister().clear();

        onView(withId(R.id.smark_view_persistent)).perform(clearText());
        onView(withId(R.id.smark_view_persistent)).perform(typeText("t"));

        try {

            onView(withText("test1"))
                    .inRoot(withDecorView(not(is(getActivity().getWindow().getDecorView()))))
                    .check(matches(not(isDisplayed())));

            // Autocomplete dialog should not even exist

            fail();

        } catch (final NoMatchingRootException ignored) {

        }

        onView(withId(R.id.smark_view_persistent)).perform(clearText());
        onView(withId(R.id.smark_view_persistent)).perform(typeText("test1"));
        onView(withId(R.id.smark_view_persistent)).check(matches(withText("test1")));

        onView(withId(R.id.spawn_button)).perform(click());

        onView(withId(R.id.smark_view_persistent)).perform(clearText());

        onView(withId(R.id.smark_view_persistent)).perform(typeText("t"));
        onView(withText("test1"))
                .inRoot(withDecorView(not(is(getActivity().getWindow().getDecorView()))))
                .perform(click());

        onView(withId(R.id.smark_view_persistent)).check(matches(withText("test1")));
    }

    public void testTextInsert() {

        onView(withId(R.id.smark_view)).perform(typeText("test1"));
        onView(withId(R.id.smark_view)).check(matches(withText("test1")));
    }

    public void testTextSave() {

        onView(withId(R.id.smark_view)).perform(typeText("test1"));
        onView(withId(R.id.smark_view)).check(matches(withText("test1")));

        ((SmarkTextView) getActivity().findViewById(R.id.smark_view)).save();

        onView(withId(R.id.smark_view)).perform(clearText());

        onView(withId(R.id.smark_view)).perform(typeText("t"));
        onView(withText("test1"))
                .inRoot(withDecorView(not(is(getActivity().getWindow().getDecorView()))))
                .perform(click());

        onView(withId(R.id.smark_view)).check(matches(withText("test1")));
    }

    public void testTextSaveAll() {

        onView(withId(R.id.smark_view)).perform(typeText("test1"));
        onView(withId(R.id.smark_view)).check(matches(withText("test1")));

        SmarkTextView.saveAll();

        onView(withId(R.id.smark_view)).perform(clearText());

        onView(withId(R.id.smark_view)).perform(typeText("t"));
        onView(withText("test1"))
                .inRoot(withDecorView(not(is(getActivity().getWindow().getDecorView()))))
                .perform(click());

        onView(withId(R.id.smark_view)).check(matches(withText("test1")));
    }

    public void testViewExists() {

        onView(withId(R.id.smark_view)).check(matches(isDisplayed()));
    }

    public static class ActivityUnderTest extends Activity {

        public void back(final View view) {

            finish();
        }

        public void spawn(final View view) {

            startActivity(new Intent(this, ActivityUnderTest.class));
        }

        @Override
        protected void onCreate(final Bundle savedInstanceState) {

            super.onCreate(savedInstanceState);

            setContentView(R.layout.activity_layout);
        }
    }
}
