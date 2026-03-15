package com.github.debacodex.adapter;

import android.app.Dialog;
import android.content.Context;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.github.debacodex.model.Item;
import java.util.List;
import java.util.Locale;
import com.github.debacodex.R;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {
	private Context context;
	private List<Item> itemList;
	private String searchText = "";
	private int highlightColor;
	
	public MyAdapter(Context context, List<Item> itemList) {
		this.context = context;
		this.itemList = itemList;
		this.highlightColor = ContextCompat.getColor(context, android.R.color.white);
	}
	
	public void setSearchText(String searchText) {
		this.searchText = searchText.toLowerCase(Locale.getDefault());
		notifyDataSetChanged();
	}
	
	@NonNull
	@Override
	public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
		View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list, parent, false);
		    // Set selectableItemBackgroundBorderless as item background
      
		return new ViewHolder(view);
	}
	
	@Override
	public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
		Item currentItem = itemList.get(position);
		
		// Set highlighted text
		holder.textViewTitle.setText(highlightText(currentItem.getTitle()));
		holder.textViewDescription.setText(highlightText(currentItem.getVillage()));
		holder.textViewId.setText(highlightText("ID: " + currentItem.getId()));
		
		Glide.with(context).load(currentItem.getImageUrl()).centerCrop().placeholder(R.drawable.ic_launcher_foreground)
		.into(holder.imageView);
		
		// Set click listener
		holder.itemView.setOnClickListener(v -> showDetailDialog(currentItem));
		
		int anim = R.anim.slide_in_right;
		holder.itemView.setAnimation(AnimationUtils.loadAnimation(holder.itemView.getContext(), anim));
	}
	
	private void showDetailDialog(Item item) {
		final Dialog dialog = new Dialog(context);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setContentView(R.layout.dialog_item_detail);
		dialog.setCancelable(false);
		// Set dialog width and height
		Window window = dialog.getWindow();
		if (window != null) {
			window.setLayout(
			(int) (context.getResources().getDisplayMetrics().widthPixels * 0.8),
			(int) (context.getResources().getDisplayMetrics().heightPixels * 0.5)
			);
		}
		
		ImageView detailImage = dialog.findViewById(R.id.detail_image);
		TextView detailTitle = dialog.findViewById(R.id.detail_title);
		TextView detailDescription = dialog.findViewById(R.id.detail_description);
		TextView detailId = dialog.findViewById(R.id.detail_id);
		ImageButton closeButton = dialog.findViewById(R.id.detail_close_button);
		TextView detailVillage = dialog.findViewById(R.id.detail_village);
		TextView detailRice = dialog.findViewById(R.id.detail_rice);
		TextView detailMoney = dialog.findViewById(R.id.detail_money);
		TextView detailThings = dialog.findViewById(R.id.detail_things);
		TextView detailStatus = dialog.findViewById(R.id.detail_status);
		
		Glide.with(context).load(item.getImageUrl()).placeholder(R.drawable.ic_launcher_foreground).into(detailImage);
		
		detailTitle.setText(item.getTitle());
		detailDescription.setText(item.getDescription());
		detailId.setText("ID: " + item.getId());
		detailVillage.setText("01. Village: " + item.getVillage());
		detailRice.setText("02. Rice: " + item.getRice());
		detailMoney.setText("03. Money: " + item.getMoney());
		detailThings.setText("04. Things: " + item.getThing());
		detailStatus.setText("05. Status: " + item.getStatus());
		
		closeButton.setOnClickListener(v -> dialog.dismiss());
		
		dialog.show();
	}
	
	private Spannable highlightText(String text) {
		Spannable spannable = new SpannableString(text);
		
		if (!searchText.isEmpty() && text.toLowerCase(Locale.getDefault()).contains(searchText)) {
			int startPos = text.toLowerCase(Locale.getDefault()).indexOf(searchText);
			int endPos = startPos + searchText.length();
			
			spannable.setSpan(new ForegroundColorSpan(ContextCompat.getColor(context, R.color.red)), startPos, endPos,
			Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		}
		
		return spannable;
	}
	
	@Override
	public int getItemCount() {
		return itemList.size();
	}
	
	public static class ViewHolder extends RecyclerView.ViewHolder {
		public ImageView imageView;
		public TextView textViewTitle;
		public TextView textViewDescription;
		public TextView textViewId;
		
		public ViewHolder(@NonNull View itemView) {
			super(itemView);
			imageView = itemView.findViewById(R.id.imageView);
			textViewTitle = itemView.findViewById(R.id.textViewTitle);
			textViewDescription = itemView.findViewById(R.id.textViewDescription);
			textViewId = itemView.findViewById(R.id.textViewId);
		}
	}
}