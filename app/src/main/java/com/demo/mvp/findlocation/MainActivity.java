package com.demo.mvp.findlocation;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.demo.mvp.R;

public final class MainActivity extends AppCompatActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		onActivityCreated();
	}

	private void onActivityCreated() {
		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);
		final FindLocationContract.Viewer viewer = (FindLocationContract.Viewer) getSupportFragmentManager().findFragmentById(R.id.main_fragment);
		new FindLocationPresenter(viewer);
	}
}
