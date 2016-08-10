/*
 * Copyright (c) 2015, 张涛.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.ibesteeth.git_keybordtest.emoji;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.ibesteeth.git_keybordtest.R;

import java.util.ArrayList;
import java.util.List;

/**
 * 表情适配器
 *
 * @author kymjs (http://www.kymjs.com)
 */
public class EmojiGridAdapter extends BaseAdapter {

    private List<Emojicon> datas;
    private final Context cxt;

    public EmojiGridAdapter(Context cxt, List<Emojicon> datas) {
        this.cxt = cxt;
        if (datas == null) {
            datas = new ArrayList<Emojicon>(0);
        }
        this.datas = datas;
    }

    public void refresh(List<Emojicon> datas) {
        if (datas == null) {
            datas = new ArrayList<Emojicon>(0);
        }
        this.datas = datas;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return datas.size();
    }

    @Override
    public Object getItem(int position) {
        return datas.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    private static class ViewHolder {
        ImageView image;
        TextView textView;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            holder = new ViewHolder();

//            convertView=new LinearLayout(cxt);
            convertView = new TextView(cxt);
//            convertView = new ImageView(cxt);

            int bound = (int) cxt.getResources().getDimension(R.dimen.space_49);
            LayoutParams params = new LayoutParams(bound, bound);
            convertView.setLayoutParams(params);
            int padding = (int) cxt.getResources().getDimension(
                    R.dimen.space_10);
            convertView.setPadding(padding, padding, padding, padding);

//            holder.image = (ImageView) convertView;
            holder.textView = (TextView) convertView;

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
//        Log.e("getResId===",datas.get(position).getResId()+"");
        if (datas.get(position).getValue() == 0) {
            holder.textView.setText("");
            holder.textView.setBackgroundResource(datas.get(position).getResId());
        } else {
            holder.textView.setText(getEmijoByUnicode(datas.get(position).getResId()));
//            holder.textView.setText(encode(datas.get(position).getEmojiStr()));
        }


//        holder.image.setImageResource(datas.get(position).getResId());
        return convertView;
    }

    public String getEmijoByUnicode(int unicode) {
//        int code=Integer.parseInt(unicode,16);
        int code = 0x1F637;
        Log.e("code===", code + "");
        String chars=new String(Character.toChars(unicode));

        Log.e("chars===", chars + "");

        return chars;
    }
}
