package com.lihang.mysmartloadingview;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.gyf.barlibrary.ImmersionBar;
import com.lihang.mysmartloadingview.databinding.ActivityFollowBinding;
import com.lihang.smartloadview.SmartLoadingView;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;

/**
 * Created by leo
 * on 2019/5/27.
 */
public class FollowActivity extends AppCompatActivity implements View.OnClickListener {

    private ActivityFollowBinding binding;
    //沉浸式状态栏
    protected ImmersionBar mImmersionBar;
    private int follow1Tag = 0;
    private int follow2Tag = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_follow);
        binding.setOnclickListener(this);
        mImmersionBar = ImmersionBar.with(this);
        mImmersionBar.init();

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.smartLoadingView_normal:
                if (follow1Tag % 2 == 0) {
                    //这里是模拟关注
                    binding.smartLoadingViewNormal.start();
                    Observable.timer(2000, TimeUnit.MILLISECONDS)
                            .observeOn(AndroidSchedulers.mainThread()).subscribe(along -> {
                        binding.smartLoadingViewNormal.netFail("关注成功");
                    });
                } else {
                    //这里是模拟取消关注
                    binding.smartLoadingViewNormal.reset();
                }
                follow1Tag++;
                break;


            case R.id.smartLoadingView_ok:
                if (follow2Tag % 2 == 0) {
                    //这里是模拟关注
                    binding.smartLoadingViewOk.start();
                    Observable.timer(2000, TimeUnit.MILLISECONDS)
                            .observeOn(AndroidSchedulers.mainThread()).subscribe(along -> {
                        binding.smartLoadingViewOk.onSuccess(new SmartLoadingView.AnimationOKListener() {
                            @Override
                            public void animationOKFinish() {
                                Toast.makeText(FollowActivity.this, "关注成功", Toast.LENGTH_SHORT).show();
                            }
                        });
                    });
                } else {
                    //这里是模拟取消关注
                    binding.smartLoadingViewOk.reset();
                }
                follow2Tag++;
                break;

            case R.id.smartLoadingView_ok_hide:
                binding.smartLoadingViewOkHide.start();
                Observable.timer(2000, TimeUnit.MILLISECONDS)
                        .observeOn(AndroidSchedulers.mainThread()).subscribe(along -> {
                    binding.smartLoadingViewOkHide.onSuccess(new SmartLoadingView.AnimationOKListener() {
                        @Override
                        public void animationOKFinish() {
                            Toast.makeText(FollowActivity.this, "关注成功", Toast.LENGTH_SHORT).show();
                        }
                    }, SmartLoadingView.OKAnimationType.HIDE);
                });
                break;

            case R.id.smartLoadingView_ok_remind:
                binding.smartLoadingViewOkRemind.start();
                Observable.timer(2000, TimeUnit.MILLISECONDS)
                        .observeOn(AndroidSchedulers.mainThread()).subscribe(along -> {
                    binding.smartLoadingViewOkRemind.onSuccess(new SmartLoadingView.AnimationOKListener() {
                        @Override
                        public void animationOKFinish() {
                            Toast.makeText(FollowActivity.this, "关注成功", Toast.LENGTH_SHORT).show();
                        }
                    }, SmartLoadingView.OKAnimationType.TRANSLATION_CENTER);
                });
                break;


            case R.id.smartLoadingView_ok_remind_2:
                binding.smartLoadingViewOkRemind2.start();
                Observable.timer(2000, TimeUnit.MILLISECONDS)
                        .observeOn(AndroidSchedulers.mainThread()).subscribe(along -> {
                    binding.smartLoadingViewOkRemind2.onSuccess(new SmartLoadingView.AnimationOKListener() {
                        @Override
                        public void animationOKFinish() {
                            Toast.makeText(FollowActivity.this, "关注成功", Toast.LENGTH_SHORT).show();
                        }
                    }, SmartLoadingView.OKAnimationType.TRANSLATION_CENTER);
                });
                break;

            case R.id.smartLoadingView_ok_remind_3:
                binding.smartLoadingViewOkRemind3.start();
                Observable.timer(2000, TimeUnit.MILLISECONDS)
                        .observeOn(AndroidSchedulers.mainThread()).subscribe(along -> {
                    binding.smartLoadingViewOkRemind3.onSuccess(new SmartLoadingView.AnimationOKListener() {
                        @Override
                        public void animationOKFinish() {
                            Toast.makeText(FollowActivity.this, "关注成功", Toast.LENGTH_SHORT).show();
                        }
                    }, SmartLoadingView.OKAnimationType.TRANSLATION_CENTER);
                });
                break;
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        mImmersionBar.destroy();
    }

}
