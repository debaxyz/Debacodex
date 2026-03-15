// MainActivity.java (updated)
package com.github.debacodex.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.inputmethod.InputMethodManager;
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

public class SearchActivity extends AppCompatActivity {

	private Toolbar toolbar;
	private ViewPager viewPager;
	private ViewPagerAdapter viewPagerAdapter;
	private SearchView searchView;
	private MagicIndicator magicIndicator;
	private String[] tabTitles = { "ALL", "DEKUSAHI", "SUNAPUR" };

	private DrawerLayout drawerLayout;
	private NavigationView navigationView;

	private FragmentA fragmentA;
	private FragmentB fragmentB;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_search);

		// Check network connection
		//	if (!isNetworkAvailable()) {
		//	showNoConnectionDialog();
		//	}

		// Setup Toolbar
		toolbar = findViewById(R.id.toolbar);

		setSupportActionBar(toolbar);

		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		viewPager = findViewById(R.id.viewpager);
		magicIndicator = findViewById(R.id.magic_indicator);
		setupMagicIndicator();

		// Initialize fragments
		fragmentA = new FragmentA();
		fragmentB = new FragmentB();

		// Setup ViewPager adapter
		viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
		viewPagerAdapter.addFragment(fragmentA, "Tab 1");
		viewPagerAdapter.addFragment(fragmentB, "Tab 2");
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

			}

			@Override
			public void onPageScrollStateChanged(int state) {
				// Not needed for binding, but part of the interface
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.search_menu, menu);

		MenuItem searchItem = menu.findItem(R.id.action_search);
		searchView = (SearchView) searchItem.getActionView();
		searchView.setQueryHint("Search...");
	//	searchView.setIconified(false);
	//	searchView.requestFocusFromTouch();
	//	searchView.clearFocus();
		searchView.onActionViewExpanded();
	//	searchView.setActivated(true);
		InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.showSoftInput(searchView, InputMethodManager.SHOW_IMPLICIT);

		searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
			@Override
			public boolean onQueryTextSubmit(String query) {
				// Filter both fragments
				if (fragmentA != null) {
					fragmentA.filter(query);
				}
				if (fragmentB != null) {
					fragmentB.filter(query);
				}
				return false;
			}

			@Override
			public boolean onQueryTextChange(String newText) {
				// Filter both fragments
				if (fragmentA != null) {
					fragmentA.filter(newText);
				}
				if (fragmentB != null) {
					fragmentB.filter(newText);
				}
				return true;
			}
		});

		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == android.R.id.home) {
			onBackPressed();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		overridePendingTransition(R.anim.fade_in, R.anim.fade_out);

	}
}