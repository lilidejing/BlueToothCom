/*
 * fooree.net@hotmail.com
 */

package cn.com.jdsc;


import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;




public class MyAdapter extends BaseAdapter
{

  private LayoutInflater mInflater;
  private Bitmap mIcon1;  //root
  private Bitmap mIcon2;  //back
  private Bitmap mIcon3;  //folder
  private Bitmap mIcon4;  //file
  private Bitmap mIcon5;  //blankfolder
  private List<String> items;
  private List<String> paths;

  private int  selectItem = -1;
  
  public MyAdapter(Context context,List<String> it,List<String> pa)
  {
    mInflater = LayoutInflater.from(context);
    items = it;
    paths = pa;
    mIcon1 = BitmapFactory.decodeResource(context.getResources(), R.drawable.back01);
    mIcon2 = BitmapFactory.decodeResource(context.getResources(), R.drawable.back02);
    mIcon3 = BitmapFactory.decodeResource(context.getResources(), R.drawable.folder);
    mIcon5 = BitmapFactory.decodeResource(context.getResources(), R.drawable.audio);
    mIcon4 = BitmapFactory.decodeResource(context.getResources(), R.drawable.folderblank);
  }
   
  public  void setSelectItem(int selectItem) {  
      this.selectItem = selectItem;
  }  
   
  
  @Override
  public int getCount()
  {
    return items.size();
  }

  @Override
  public Object getItem(int position)
  {
    return items.get(position);
  }
  
  @Override
  public long getItemId(int position)
  {
    return position;
  }
  
  @Override
  public View getView(int position,View convertView,ViewGroup parent)
  {
    ViewHolder holder;
    
    if(convertView == null)
    {

      convertView = mInflater.inflate(R.layout.file_row, null);

      holder = new ViewHolder();
      holder.text = (TextView) convertView.findViewById(R.id.text);
      holder.icon = (ImageView) convertView.findViewById(R.id.icon);
      
      convertView.setTag(holder);
    }
    else
    {
      holder = (ViewHolder) convertView.getTag();
    }

    
    if(items.get(position).toString().equals("b1"))
    {
      holder.text.setText("返回/");
      holder.icon.setImageBitmap(mIcon1);
    }
    else if(items.get(position).toString().equals("b2"))
    {
      holder.text.setText("返回上级");
      holder.icon.setImageBitmap(mIcon2);
    }
    else if(items.get(position).toString().equals("b3"))
    {
    	holder.text.setText(paths.get(position).toString());
        holder.icon.setImageBitmap(mIcon3);
    } else if(items.get(position).toString().equals("b4")){
    	holder.text.setText(paths.get(position).toString());
        holder.icon.setImageBitmap(mIcon4);
    } else {
    	holder.text.setText(paths.get(position).toString());
    	holder.icon.setImageBitmap(mIcon5);
    }
    
    if (position == selectItem) {  
        convertView.setBackgroundColor(Color.BLUE);  
    }   
    else {  
        convertView.setBackgroundColor(Color.TRANSPARENT);  
    }
    
    return convertView;
  }
  
  
  /* class ViewHolder */
  private class ViewHolder
  {
    TextView text;
    ImageView icon;
  }
}