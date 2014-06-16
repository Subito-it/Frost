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

package it.subito.frost.demo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import it.subito.frost.FrostTextView;

public class DemoActivity extends Activity {

    private FrostTextView mFrostTextView;
    private CheckBox mCb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mFrostTextView = (FrostTextView) findViewById(R.id.editText);

        findViewById(R.id.button).setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                buttonClick();
            }
        });

        findViewById(R.id.button_clear).setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                buttonClear();
            }
        });

        findViewById(R.id.button_new).setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                buttonCreateActivity();
            }
        });

        mCb = (CheckBox) findViewById(R.id.checkbox_auto_save);
        mFrostTextView.setAutoSave(mCb.isChecked());
        mCb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mFrostTextView.setAutoSave(isChecked);
            }
        });
    }

    private void buttonClick() {

        //Do something with form

        mFrostTextView.save();
    }

    private void buttonClear() {

        mFrostTextView.clearHistory();
    }

    private  void buttonCreateActivity() {

        Intent intent = new Intent(this, DemoActivity.class);

        this.startActivity(intent);
    }
}
