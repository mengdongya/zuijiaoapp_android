package net.zuijiao.android.zuijiao;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.zuijiao.adapter.GourmetCommentAdapter;
import com.zuijiao.android.util.functional.LambdaExpression;
import com.zuijiao.android.util.functional.OneParameterExpression;
import com.zuijiao.android.zuijiao.model.Comments;
import com.zuijiao.android.zuijiao.network.Router;

/**
 * Created by xiaqibo on 2015/5/29.
 */
@ContentView(R.layout.activity_gourmet_comment)
public class GourmetCommentActivity extends BaseActivity {
    @ViewInject(R.id.gourmet_comment_list)
    private ListView mListView = null;
    @ViewInject(R.id.gourmet_comment_toolbar)
    private Toolbar mToolbar = null;
    @ViewInject(R.id.gourmet_comment_et_comment)
    private EditText mEtComment;
    @ViewInject(R.id.gourmet_comment_comment_commit)
    private ImageButton mCommitButton;
    private int mIdentify = 0;
    private GourmetCommentAdapter mAdapter = null;
    private Comments mComments = null;
    private LayoutInflater mInflater = null;
    private boolean bModified = false;
    private Integer mReplyId = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void registerViews() {
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mInflater = LayoutInflater.from(this);
        mIdentify = mTendIntent.getIntExtra("gourmet_identify", 0);
        fetchCommentList();
        mListView.setOnItemClickListener(mCommentListListener);
        mCommitButton.setOnClickListener(mCommitListener);
    }


    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        intent.putExtra("comment_modified", bModified);
        setResult(RESULT_OK, intent);
        finish();
    }

    private View.OnClickListener mCommitListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (!Router.getInstance().getCurrentUser().isPresent()) {
                View contentView = mInflater.inflate(R.layout.alert_login_dialog, null);
                TextView tv = (TextView) contentView.findViewById(R.id.fire_login);
                final AlertDialog dialog = new AlertDialog.Builder(GourmetCommentActivity.this).setView(contentView).create();
                tv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                        startActivity(intent);
                        dialog.dismiss();
                        finalizeDialog();
                    }
                });
                dialog.show();
                return;
            }
            String comment = mEtComment.getText().toString().trim();
            if (comment.equals("")) {
                Toast.makeText(getApplicationContext(), getString(R.string.notify_empty_comment), Toast.LENGTH_SHORT);
                return;
            }
            InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            if (imm != null) {
                imm.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(),
                        0);
            }
            mEtComment.setHint(getString(R.string.comment_hint));
            mEtComment.setText("");
            createDialog();
            if (mReplyId != null) {
                Router.getGourmetModule().replyCommentTo(mReplyId, comment, new LambdaExpression() {
                    @Override
                    public void action() {
                        fetchCommentList();
                        bModified = true;
                        Toast.makeText(getApplicationContext(), getString(R.string.reply_success), Toast.LENGTH_SHORT).show();
                        finalizeDialog();
                    }
                }, new LambdaExpression() {
                    @Override
                    public void action() {
                        Toast.makeText(getApplicationContext(), getString(R.string.reply_failed), Toast.LENGTH_SHORT).show();
                        finalizeDialog();
                    }
                });
            } else {
                Router.getGourmetModule().postComment(mIdentify, comment, new LambdaExpression() {
                    @Override
                    public void action() {
                        bModified = true;
                        fetchCommentList();
                        Toast.makeText(getApplicationContext(), getString(R.string.comment_success), Toast.LENGTH_SHORT).show();
                        finalizeDialog();
                    }
                }, new LambdaExpression() {
                    @Override
                    public void action() {
                        Toast.makeText(getApplicationContext(), getString(R.string.comment_failed), Toast.LENGTH_SHORT).show();
                        finalizeDialog();
                    }
                });
            }
        }
    };
    private AdapterView.OnItemClickListener mCommentListListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            if (!Router.getInstance().getCurrentUser().isPresent()) {
                View contentView = mInflater.inflate(R.layout.alert_login_dialog, null);
                TextView tv = (TextView) contentView.findViewById(R.id.fire_login);
                final AlertDialog dialog = new AlertDialog.Builder(GourmetCommentActivity.this).setView(contentView).create();
                tv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                        startActivity(intent);
                        dialog.dismiss();
                        finalizeDialog();

                    }
                });
                dialog.show();
            } else {
                if (Router.getInstance().getCurrentUser().get().getIdentifier().equals(mComments.getCommentList().get(position).getUser().getIdentifier())) {
                    View deleteView = LayoutInflater.from(getApplicationContext()).inflate(
                            R.layout.alert_delete_comment, null);
                    AlertDialog.Builder builder = new AlertDialog.Builder(
                            GourmetCommentActivity.this);
                    AlertDialog dialog = builder.setView(deleteView).create();
                    dialog.show();
                    deleteView.findViewById(R.id.alert_delete_comment).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                            Router.getGourmetModule().removeComment(mComments.getCommentList().get(position).getIdentifier(), new LambdaExpression() {
                                @Override
                                public void action() {
                                    bModified = true;
                                    fetchCommentList();
                                }
                            }, new LambdaExpression() {
                                @Override
                                public void action() {
                                    Toast.makeText(getApplicationContext(), getString(R.string.fail_delete_comment), Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    });
                } else {
                    mEtComment.setFocusable(true);
                    mEtComment.setFocusableInTouchMode(true);
                    mEtComment.requestFocus();
                    InputMethodManager inputManager =
                            (InputMethodManager) mEtComment.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    inputManager.showSoftInput(mEtComment, 0);
                    mEtComment.setHint(String.format(getString(R.string.reply_to), mComments.getCommentList().get(position).getUser().getNickName()));
                    mReplyId = mComments.getCommentList().get(position).getIdentifier();
                }
            }
        }
    };

    private void fetchCommentList() {
        createDialog();
        Router.getGourmetModule().fetchComments(mIdentify, null, null, 500, new OneParameterExpression<Comments>() {
            @Override
            public void action(Comments comments) {
                mComments = comments;
                if (mAdapter == null)
                    mAdapter = new GourmetCommentAdapter(GourmetCommentActivity.this, mComments);
                else
                    mAdapter.setData(mComments);
                if (mListView.getAdapter() == null)
                    mListView.setAdapter(mAdapter);
                else
                    mAdapter.notifyDataSetChanged();
                finalizeDialog();
            }
        }, new OneParameterExpression<String>() {
            @Override
            public void action(String s) {
                Toast.makeText(mContext, getString(R.string.notify_net2), Toast.LENGTH_SHORT).show();
                finalizeDialog();
            }
        });
    }
}
