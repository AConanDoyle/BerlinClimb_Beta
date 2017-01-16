package de.example.navdrawemap_2.maptest.ListViewSpots;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.text.NumberFormat;

import de.example.navdrawemap_2.maptest.R;

public class CustomList extends ArrayAdapter<String> {

    private String[] heads, krouten, brouten, adress, webadress, type, price, inout, material;
    private double[] lat, longC;
    private int[] imageid;
    private Activity context;

    public CustomList(Activity context, String[] heads, int[] imageid, String[] type, String[] inout,
                      String[] krouten, String[] brouten, String[] material, String[] price,
                      String[] adress, double[] lat, double[] longC, String[] webadress) {
        super(context, R.layout.content_overwiev, heads);
        this.context = context;
        this.heads = heads;
        this.imageid = imageid;
        this.type = type;
        this.inout = inout;
        this.krouten = krouten;
        this.brouten = brouten;
        this.material = material;
        this.price = price;
        this.adress = adress;
        this.lat = lat;
        this.longC = longC;
        this.webadress = webadress;
    }

    // puts the data into the text- and image views
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = context.getLayoutInflater();
        View listViewItem = inflater.inflate(R.layout.content_overwiev, null, true);
        TextView textViewName = (TextView) listViewItem.findViewById(R.id.textViewHeads);
        ImageView imageView = (ImageView) listViewItem.findViewById(R.id.imageView);
        TextView textViewType = (TextView) listViewItem.findViewById(R.id.textViewType);
        TextView textViewInOut = (TextView) listViewItem.findViewById(R.id.textViewInOUT);
        TextView textViewKRouten = (TextView) listViewItem.findViewById(R.id.textViewKRouten);
        TextView textViewBRouten = (TextView) listViewItem.findViewById(R.id.textViewBRouten);
        TextView textViewMaterial = (TextView) listViewItem.findViewById(R.id.textViewMaterial);
        TextView textViewPrice = (TextView) listViewItem.findViewById(R.id.textViewPrice);
        TextView textViewAdress = (TextView) listViewItem.findViewById(R.id.textViewAdress);
        TextView textViewLat = (TextView) listViewItem.findViewById(R.id.textViewLat);
        TextView textViewLong = (TextView) listViewItem.findViewById(R.id.textViewLong);
        TextView textViewWebadress = (TextView) listViewItem.findViewById(R.id.textViewWebadress);

        NumberFormat nm = NumberFormat.getNumberInstance();

        // execute() insert call
        // Sets the image for a spot or a default image
        if (imageid[position] != 0) {
            Picasso.with(context).load(imageid[position]).into(imageView);
            // imageView.setImageResource(imageid[position]);
        } else {
            Picasso.with(context).load(R.mipmap.ic_launcher).into(imageView);
            // imageView.setImageResource(R.mipmap.ic_launcher);
        }

        // set the values in the text
        textViewName.setText(heads[position]);
        textViewType.setText(type[position]);
        textViewInOut.setText(inout[position]);
        textViewKRouten.setText(krouten[position]);
        textViewBRouten.setText(brouten[position]);
        textViewMaterial.setText(material[position]);
        textViewPrice.setText(price[position]);
        textViewAdress.setText(adress[position]);
        textViewLat.setText(nm.format(lat[position]));
        textViewLong.setText(nm.format(longC[position]));
        textViewWebadress.setText(webadress[position]);

        return listViewItem;
    }

    // returns the listview Item by position
    public View getViewByPosition(int pos, ListView listView) {

        final int firstListItemPosition = listView.getFirstVisiblePosition();
        final int lastListItemPosition = firstListItemPosition + listView.getChildCount() - 1;

        if (pos < firstListItemPosition || pos > lastListItemPosition) {
            return listView.getAdapter().getView(pos, null, listView);
        } else {
            final int childIndex = pos - firstListItemPosition;
            return listView.getChildAt(childIndex);

        }
    }

}
