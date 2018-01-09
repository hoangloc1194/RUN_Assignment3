package com.androidtutorialpoint.glogin;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import java.util.List;

public class CustomListAdapter  extends BaseAdapter {

    private List<Feature> listData;
    private LayoutInflater layoutInflater;
    private Context context;

    public CustomListAdapter(Context aContext,  List<Feature> listData) {
        this.context = aContext;
        this.listData = listData;
        layoutInflater = LayoutInflater.from(aContext);
    }




    @Override
    public int getCount() {
        return listData.size();
    }

    @Override
    public Object getItem(int position) {
        return listData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.list_item_layout, null);
            holder = new ViewHolder();
            holder.featureView = (ImageView) convertView.findViewById(R.id.imageView_feature);
            holder.featureNameView = (TextView) convertView.findViewById(R.id.textView_featureName);
            holder.discriptionView = (TextView) convertView.findViewById(R.id.textView_discription);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Feature feature = this.listData.get(position);
        holder.featureNameView.setText(feature.getFeatureName());
        holder.discriptionView.setText("Discription: " + feature.getDiscription());

        int imageId = this.getMipmapResIdByName(feature.getFeatureIcon());

        holder.featureView.setImageResource(imageId);

        return convertView;
    }

    // Finding image ID matching name of its name.
    public int getMipmapResIdByName(String resName)  {
        String pkgName = context.getPackageName();

        // Return 0 if can't match
        int resID = context.getResources().getIdentifier(resName , "mipmap", pkgName);
        Log.i("CustomListView", "Res Name: "+ resName+"==> Res ID = "+ resID);
        return resID;
    }

    static class ViewHolder {
        ImageView featureView;
        TextView featureNameView;
        TextView discriptionView;
    }

}
