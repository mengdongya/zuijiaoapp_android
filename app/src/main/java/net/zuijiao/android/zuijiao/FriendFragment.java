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
import android.content.Intent;
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

import com.squareup.picasso.Picasso;
import com.zuijiao.android.util.functional.LambdaExpression;
import com.zuijiao.android.util.functional.OneParameterExpression;
import com.zuijiao.android.zuijiao.model.user.SocialEntities;
import com.zuijiao.android.zuijiao.model.user.SocialEntity;
import com.zuijiao.android.zuijiao.model.user.TinyUser;
import com.zuijiao.android.zuijiao.network.Router;
import com.zuijiao.db.DBOpenHelper;

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
    private List<SocialEntity> data;
    private View mEmptyView = null;
    private AdapterView.OnItemClickListener mListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Intent intent = new Intent();
            intent.setClass(getActivity(), UserInfoActivity.class);
            intent.putExtra("tiny_user", data.get(position));
            startActivity(intent);
        }
    };
    public BaseAdapter mAdapter = new BaseAdapter() {
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
            SocialEntity user = data.get(position);
            holder.userName.setText(user.getNickName());
            if (user.getAvatarURLSmall().isPresent())
                Picasso.with(mActivity.getApplicationContext()).load(user.getAvatarURLSmall().get()).placeholder(R.drawable.default_user_head).into(holder.headView);
            else holder.headView.setImageResource(R.drawable.default_user_head);
            String location = DBOpenHelper.getmInstance(mActivity.getApplicationContext()).getLocationByIds(user.getProvinceId(), user.getCityId());
            if (location == null || location.equals("")) {
                location = "";
            }
            int recommendationCount = user.getRecommendationGourmetCount();
            String locationAndRecommendCount = location;
            if (recommendationCount > 0) {
                if (!location.equals("")) {
                    location = location + getString(R.string.splite_dot);
                }
                locationAndRecommendCount = location + String.format(getString(R.string.recommendation_count), recommendationCount);
            }
            holder.userInfo.setText(locationAndRecommendCount);
            if (user.isFollowing() && user.isFollower()) {
                holder.follow.setText(getString(R.string.followed_each));
            } else if (user.isFollowing() && !user.isFollower()) {
                holder.follow.setText(getString(R.string.followed));
            } else {
                holder.follow.setText(getString(R.string.follow));
            }
            holder.follow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!Router.getInstance().getCurrentUser().isPresent()) {
                        ((BaseActivity) mActivity).notifyLogin(null);
                        return;
                    }
                    if (user.isFollowing()) {
                        Router.getSocialModule().unFollow(user.getIdentifier(), new LambdaExpression() {
                            @Override
                            public void action() {
                                if (mTinyUser.getIdentifier() == Router.getInstance().getCurrentUser().get().getIdentifier() && position == FOLLOWING) {
                                    data.remove(user);
                                }
                                user.setIsFollowing(false);
                                mAdapter.notifyDataSetChanged();
                            }
                        }, new OneParameterExpression<String>() {
                            @Override
                            public void action(String s) {
                                Toast.makeText(mActivity, getString(R.string.notify_net2), Toast.LENGTH_SHORT).show();
                            }
                        });
                    } else {
                        Router.getSocialModule().unFollow(user.getIdentifier(), new LambdaExpression() {
                            @Override
                            public void action() {
                                if (mTinyUser.getIdentifier() == Router.getInstance().getCurrentUser().get().getIdentifier()) {
//                                mAdapter.notifyDataSetChanged();
//                            }
                                    user.setIsFollowing(true);
                                    mAdapter.notifyDataSetChanged();
                                }
                            }
                        }, new OneParameterExpression<String>() {
                            @Override
                            public void action(String s) {
                                Toast.makeText(mActivity, getString(R.string.notify_net2), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
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
        mEmptyView = mContentView.findViewById(R.id.friend_empty_content);
        mListView.setAdapter(mAdapter);
        mListView.setOnItemClickListener(mListener);
        fetchFriendShip();
        return mContentView;
    }

    private void fetchFriendShip() {
        if (position == FOLLOWER) {
            Router.getSocialModule().getFollowersOfUserId(mTinyUser.getIdentifier(), null, 500, new OneParameterExpression<SocialEntities>() {
                @Override
                public void action(SocialEntities socialEntities) {
                    data = socialEntities.getUsers();
                    if (data == null || data.size() == 0) {
                        mListView.setVisibility(View.GONE);
                        mEmptyView.setVisibility(View.VISIBLE);
                        ((TextView) mEmptyView.findViewById(R.id.fragment_friend_no_content_text_view)).setText(getString(R.string.no_fans));
                    } else {
                        mAdapter.notifyDataSetChanged();
                    }
                }
            }, new OneParameterExpression<String>() {
                @Override
                public void action(String s) {
                    Toast.makeText(mActivity, getString(R.string.notify_net2), Toast.LENGTH_SHORT).show();
                }
            });
        } else if (position == FOLLOWING) {
            Router.getSocialModule().getFollowingsOfUserId(mTinyUser.getIdentifier(), null, 500, new OneParameterExpression<SocialEntities>() {
                @Override
                public void action(SocialEntities socialEntities) {
                    data = socialEntities.getUsers();
                    if (data == null || data.size() == 0) {
                        mListView.setVisibility(View.GONE);
                        mEmptyView.setVisibility(View.VISIBLE);
                        ((TextView) mEmptyView.findViewById(R.id.fragment_friend_no_content_text_view)).setText(getString(R.string.no_follow));
                    } else {
                        mAdapter.notifyDataSetChanged();
                    }
                }
            }, new OneParameterExpression<String>() {
                @Override
                public void action(String s) {
                    Toast.makeText(mActivity, getString(R.string.notify_net2), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    class FriendViewHolder {
        ImageView headView;
        TextView userName;
        TextView userInfo;
        Button follow;
    }


}