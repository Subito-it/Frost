package it.subito.smark.demo;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;

import closelist.mymodule.app.R;
import it.subito.smark.SmarkTextView;


public class DemoActivity extends Activity {

    private SmarkTextView mSmarkTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mSmarkTextView = (SmarkTextView) findViewById(R.id.editText);

        findViewById(R.id.button).setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                buttonClick();
            }
        });
    }

    private void buttonClick() {

        //Do something with form

        mSmarkTextView.save();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
