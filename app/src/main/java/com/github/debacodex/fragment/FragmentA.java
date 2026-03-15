package com.github.debacodex.fragment;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import com.github.debacodex.adapter.MyAdapter;
import com.github.debacodex.model.Item;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;
import com.github.debacodex.R;

public class FragmentA extends Fragment {
	private RecyclerView recyclerView;
	private MyAdapter adapter;
	private List<Item> itemList;
	private List<Item> itemListFull;
	private RequestQueue requestQueue;
	private ProgressBar progressBar;
	private TextView emptyView;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_a, container, false);

		recyclerView = view.findViewById(R.id.recyclerViewA);
		progressBar = view.findViewById(R.id.progressBar);
		emptyView = view.findViewById(R.id.emptyView);

		recyclerView.setHasFixedSize(true);
		recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

		itemList = new ArrayList<>();
		itemListFull = new ArrayList<>();
		requestQueue = Volley.newRequestQueue(getContext());

		parseJSON();

		return view;
	}

	private void parseJSON() {
		progressBar.setVisibility(View.VISIBLE);
		recyclerView.setVisibility(View.GONE);
		emptyView.setVisibility(View.GONE);

		String url = "https://raw.githubusercontent.com/debaxyz/Data/refs/heads/main/All.json";

		JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null,
				new Response.Listener<JSONArray>() {
					@Override
					public void onResponse(JSONArray response) {
						progressBar.setVisibility(View.GONE);

						try {
							for (int i = 0; i < response.length(); i++) {
								JSONObject hit = response.getJSONObject(i);
								int id = hit.getInt("id");
								String title = hit.getString("name");
								//	String description = "Description for item " + id;
								String description = hit.getString("description");
								String imageUrl = hit.getString("imageUrl");

								String village = hit.getString("village");
								String rice = hit.getString("rice");
								String money = hit.getString("money");
								String things = hit.getString("things");
								String status = hit.getString("status");

								itemList.add(new Item(id, title, description, imageUrl, village, rice, money, things,
										status));
							}

							itemListFull.addAll(itemList);
							adapter = new MyAdapter(getContext(), itemList);
							recyclerView.setAdapter(adapter);

							if (itemList.isEmpty()) {
								emptyView.setVisibility(View.VISIBLE);
								recyclerView.setVisibility(View.GONE);
							} else {
								emptyView.setVisibility(View.GONE);
								recyclerView.setVisibility(View.VISIBLE);
							}
						} catch (JSONException e) {
							e.printStackTrace();
							showErrorDialog("Data parsing error", "Failed to parse the data from server.");
						}
					}
				}, new Response.ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError error) {
						progressBar.setVisibility(View.GONE);
						showErrorDialog("Network error", "Failed to load data. Please check your internet connection.");
					}
				});

		requestQueue.add(request);
	}

	private void showErrorDialog(String title, String message) {
		// Inflate the custom layout
		View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.error_dialogs, null);

		// Initialize views from the custom layout
		TextView titleView = dialogView.findViewById(R.id.dialog_title);
		TextView messageView = dialogView.findViewById(R.id.dialog_message);
		Button exitButton = dialogView.findViewById(R.id.exit_button);
		Button retryButton = dialogView.findViewById(R.id.retry_button);

		// Set the title and message
		titleView.setText(title);
		messageView.setText(message);

		// Create the dialog
		AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
		builder.setView(dialogView);
		AlertDialog dialog = builder.create();
		dialog.setCancelable(false);

		// Set click listeners for the buttons
		exitButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (getActivity() != null) {
					getActivity().finish();
				}
				dialog.dismiss();
			}
		});

		retryButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				dialog.dismiss();
				parseJSON(); // Retry the request
			}
		});

		dialog.show();
	}

	public void filter(String text) {
		itemList.clear();
		if (text.isEmpty()) {
			itemList.addAll(itemListFull);
		} else {
			text = text.toLowerCase();
			for (Item item : itemListFull) {
				if (item.getTitle().toLowerCase().contains(text) || item.getDescription().toLowerCase().contains(text)
						|| String.valueOf(item.getId()).contains(text)) {
					itemList.add(item);
				}
			}
		}

		adapter.setSearchText(text);
		adapter.notifyDataSetChanged();

		if (itemList.isEmpty()) {
			emptyView.setVisibility(View.VISIBLE);
			recyclerView.setVisibility(View.GONE);
			emptyView.setText("No results found for \"" + text + "\"");
		} else {
			emptyView.setVisibility(View.GONE);
			recyclerView.setVisibility(View.VISIBLE);
		}
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		if (requestQueue != null) {
			requestQueue.cancelAll(this);
		}
	}
}