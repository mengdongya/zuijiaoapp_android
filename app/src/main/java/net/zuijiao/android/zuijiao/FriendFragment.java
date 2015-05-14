/*
 * Copyright (C) 2013 Andreas Stuetz <andreas.stuetz@gmail.com>
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

package net.zuijiao.android.zuijiao;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.zuijiao.android.zuijiao.model.user.TinyUser;
import com.zuijiao.android.zuijiao.network.Router;

import java.util.List;

public class FriendFragment extends Fragment {

    private static final String ARG_POSITION = "position";
    private static final int FOLLOWING = 0;
    private static final int FOLLOWER = 1;
    private static final int UNKNOWN = -1;
    private int position = UNKNOWN;
    private static LayoutInflater mInflater = null;
    private View mContentView = null;
    private ListView mListView = null;
    private Activity mActivity = null;
    private static TinyUser mTinyUser = null;
    private List<TinyUser> data;
    private AdapterView.OnItemClickListener mListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        }
    };
    private BaseAdapter mAdapter = new BaseAdapter() {
        @Override
        public int getCount() {
            if (data != null) {
                return data.size();
            }
            return 0;
        }

        @Override
        public Object getItem(int position) {
            return position;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            FriendViewHolder holder = null;
            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.friend_list_item, null);
                holder = new FriendViewHolder();
                holder.headView = (ImageView) convertView.findViewById(R.id.friend_item_head);
                holder.userName = (TextView) convertView.findViewById(R.id.friend_item_user_name);
                holder.userInfo = (TextView) convertView.findViewById(R.id.friend_item_user_msg);
                holder.follow = (Button) convertView.findViewById(R.id.friend_item_btn);
                convertView.setTag(holder);
            } else {
                holder = (FriendViewHolder) convertView.getTag();
            }
            holder.follow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(getActivity().getApplicationContext(), "follow", Toast.LENGTH_SHORT).show();
                }
            });
            holder.follow.setFocusable(false);
            return convertView;
        }
    };

    public FriendFragment() {
        data = null;
    }

    public static FriendFragment newInstance(int position, TinyUser
            user) {
        mTinyUser = user;
        FriendFragment f = new FriendFragment();
        Bundle b = new Bundle();
        b.putInt(ARG_POSITION, position);
        f.setArguments(b);
        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        position = getArguments().getInt(ARG_POSITION);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (mInflater == null) {
            mInflater = inflater;
        }
        mActivity = getActivity();
        mContentView = inflater.inflate(R.layout.fragment_friend, null);
        mListView = (ListView) mContentView.findViewById(R.id.friend_list);
        mListView.setAdapter(mAdapter);
        mListView.setOnItemClickListener(mListener);
        fetchFriendShip();

        return mContentView;
    }

    private void fetchFriendShip() {
        if (position == FOLLOWER) {
            Router.getSocialModule().getFollowers(null, mTinyUser.getIdentifier(), social -> {
                if (social.getUsers() != null) {

                    mAdapter.notifyDataSetChanged();
                }
            }, errorMsg -> {

            });
        } else if (position == FOLLOWING) {

        }
    }

    class FriendViewHolder {
        ImageView headView;
        TextView userName;
        TextView userInfo;
        Button follow;
    }

}