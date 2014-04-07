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

package it.subito.smark.test;

import android.app.Activity;
import android.os.Bundle;
import android.test.ActivityInstrumentationTestCase2;

import it.subito.smark.R;
import it.subito.smark.test.TestActivity.ActivityUnderTest;

import static com.google.android.apps.common.testing.ui.espresso.Espresso.onView;
import static com.google.android.apps.common.testing.ui.espresso.matcher.ViewMatchers.withId;

public class TestActivity extends ActivityInstrumentationTestCase2<ActivityUnderTest> {

    public TestActivity() {

        super(ActivityUnderTest.class);
    }

    @Override
    public void setUp() throws Exception {

        super.setUp();
        // Espresso will not launch our activity for us, we must launch it via getActivity().
        getActivity();
    }

    public void testViewExists() {

        onView(withId(R.id.smark_view));
    }

    public static class ActivityUnderTest extends Activity {

        @Override
        protected void onCreate(final Bundle savedInstanceState) {

            super.onCreate(savedInstanceState);

            setContentView(R.layout.activity_layout);
        }
    }
}
