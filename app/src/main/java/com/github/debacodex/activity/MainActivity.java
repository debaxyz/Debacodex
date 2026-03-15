// MainActivity.java (updated)
package com.github.debacodex.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.viewpager.widget.ViewPager;
import com.github.debacodex.adapter.ViewPagerAdapter;
import com.github.debacodex.fragment.FragmentA;
import com.github.debacodex.fragment.FragmentB;
import com.github.debacodex.fragment.RNuasahi;

import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;
import net.lucode.hackware.magicindicator.MagicIndicator;
import net.lucode.hackware.magicindicator.ViewPagerHelper;
import net.lucode.hackware.magicindicator.buildins.UIUtil;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.CommonNavigator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.CommonNavigatorAdapter;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerIndicator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerTitleView;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.indicators.WrapPagerIndicator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.titles.ClipPagerTitleView;
import android.view.View;
import com.github.debacodex.R;

public class MainActivity extends AppCompatActivity {

	private Toolbar toolbar;
	private ViewPager viewPager;
	private ViewPagerAdapter viewPagerAdapter;
	private SearchView searchView;
	private MagicIndicator magicIndicator;

	private String[] tabTitles = { "ALL", "ABASING", "ALAJAL", "BAGHAMARI", "BALISAHI", "BANGUSAHI", "DEKUSAHI",
			"GUDISAHI", "JALLANGO", "KADAMBATAL", "KANDULASAHI", "LAXMIPURA", "MUNUSING", "PADIGAAN", "R,NUSAHI",
			"RAIDA", "RANHADA", "REGIDISING", "SINDISING", "SUNAPUR", "U.SINDISING", "U.SUNAPUR", "UKARSING" };
	private DrawerLayout drawerLayout;
	private NavigationView navigationView;

	private boolean doubleBackToExitPressedOnce = false;
	private Handler handler = new Handler();
	private Runnable resetBackPressFlag = () -> doubleBackToExitPressedOnce = false;
	private FragmentA fragmentA;
	private FragmentB fragmentB;
	private RNuasahi rNuasahi;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_main);

		

		// Setup Toolbar
		toolbar = findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);

		toolbar.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(MainActivity.this, SearchActivity.class);
				startActivity(intent);
				drawerLayout.closeDrawer(GravityCompat.START);

			}
		});
	
		viewPager = findViewById(R.id.viewpager);
		magicIndicator = findViewById(R.id.magic_indicator);
		setupMagicIndicator();

		drawerLayout = findViewById(R.id.drawer_layout);
		ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.nav_open,
				R.string.nav_close);
		drawerLayout.addDrawerListener(toggle);
		toggle.syncState();

		navigationView = findViewById(R.id.nav_view);
		navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
			@Override
			public boolean onNavigationItemSelected(MenuItem item) {
				int itemId = item.getItemId();

				Menu navigationMenu = navigationView.getMenu();

				for (int i = 0; i < navigationMenu.size(); i++) {
					if (navigationMenu.getItem(i).getItemId() == itemId) {
						viewPager.setCurrentItem(i);
						if (drawerLayout != null) {
							drawerLayout.closeDrawer(GravityCompat.START);
						}
						return true;
					}
				}
				return false;
			}
		});

		// Initial synchronization
		int initialPosition = 0;
		viewPager.setCurrentItem(initialPosition);
		navigationView.getMenu().getItem(initialPosition).setChecked(true);

		// Initialize fragments
		fragmentA = new FragmentA();
		fragmentB = new FragmentB();
		rNuasahi = new RNuasahi();

		// Setup ViewPager adapter
		viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
		viewPagerAdapter.addFragment(fragmentA, "Tab 1");
		viewPagerAdapter.addFragment(fragmentB, "Tab 2");
		viewPagerAdapter.addFragment(rNuasahi, "R.Nuasahi");
		viewPager.setAdapter(viewPagerAdapter);
	}

	// ... rest of the MainActivity code remains the same ...
	private void setupMagicIndicator() {
		CommonNavigator commonNavigator = new CommonNavigator(this);
		commonNavigator.setSkimOver(true);
		//	commonNavigator.setScrollPivotX(20.0f);
		//commonNavigator.setAdjustMode(true);
		int padding = UIUtil.getScreenWidth(this) / 50;
		//	commonNavigator.setRightPadding(padding);
		commonNavigator.setLeftPadding(padding);

		commonNavigator.setAdapter(new CommonNavigatorAdapter() {
			@Override
			public int getCount() {
				return tabTitles.length;
			}

			@Override
			public IPagerTitleView getTitleView(Context context, final int index) {
				ClipPagerTitleView clipPagerTitleView = new ClipPagerTitleView(context);
				clipPagerTitleView.setText(tabTitles[index]);
				clipPagerTitleView.setTextColor(getResources().getColor(R.color.magic_indicator_text));
				clipPagerTitleView.setClipColor(Color.parseColor("#FF4081"));
				//clipPagerTitleView.setBackgroundResource(R.drawable.ripple_effect);

				clipPagerTitleView.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						viewPager.setCurrentItem(index);
						drawerLayout.closeDrawer(GravityCompat.START);
						//	clipPagerTitleView.setBackgroundResource(android.R.drawable.selectableItemBackgroundBorderless);

					}
				});
				return clipPagerTitleView;
			}

			@Override
			public IPagerIndicator getIndicator(Context context) {
				WrapPagerIndicator indicator = new WrapPagerIndicator(context);
				indicator.setFillColor(Color.parseColor("#1BFF4081"));
				return indicator;
			}
		});
		magicIndicator.setNavigator(commonNavigator);

		ViewPagerHelper.bind(magicIndicator, viewPager);
		viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
			@Override
			public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
				// Not needed for binding, but part of the interface
			}

			@Override
			public void onPageSelected(int position) {
				// When a ViewPager page is selected, update the NavigationView
				Menu navigationMenu = navigationView.getMenu();
				if (position < navigationMenu.size()) {
					navigationMenu.getItem(position).setChecked(true);

				}
			}

			@Override
			public void onPageScrollStateChanged(int state) {
				// Not needed for binding, but part of the interface
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main_menu, menu);

		return true;
	}
/*
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == android.R.id.home) {
			finish();
		}

		switch (item.getItemId()) {
		case R.id.code:
			//	Intent intent = new Intent(MainActivity.this, LoginActivity.class);
			//	startActivity(intent);
			break;
		case R.id.about:

			break;
		}
		return super.onOptionsItemSelected(item);
	}
*/
	@Override
	public void onBackPressed() {
		if (doubleBackToExitPressedOnce) {
			super.onBackPressed();
			return;
		}

		this.doubleBackToExitPressedOnce = true;
		Snackbar.make(findViewById(android.R.id.content), "Press BACK again to exit", Snackbar.LENGTH_SHORT).show();

		handler.postDelayed(resetBackPressFlag, 2000); // 2 seconds to press again
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		handler.removeCallbacks(resetBackPressFlag);
	}

	private void redirectToLogin() {
		Intent intent = new Intent(MainActivity.this, LoginActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
		startActivity(intent);
		finish();
	}
}
