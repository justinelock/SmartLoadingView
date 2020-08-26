package com.lihang.mysmartloadingview;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.gyf.barlibrary.ImmersionBar;
import com.lihang.mysmartloadingview.databinding.ActivityMainBinding;
import com.lihang.smartloadview.SmartLoadingView;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    //沉浸式状态栏
    protected ImmersionBar mImmersionBar;
    ActivityMainBinding binding;
    private int follow4Tag = 0;
    private int follow5Tag = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        binding.setOnClickListener(this);
        mImmersionBar = ImmersionBar.with(this);
        mImmersionBar.init();


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.smartLoadingView_1_:
                binding.smartLoadingView1.start();
                Observable.timer(2000, TimeUnit.MILLISECONDS)
                        .observeOn(AndroidSchedulers.mainThread()).subscribe(along -> {
                    binding.smartLoadingView1.onSuccess(MainActivity.this, SecondActivity.class);
                });
                break;

            case R.id.smartLoadingView_2_:
                binding.smartLoadingView2.start();
                Observable.timer(2000, TimeUnit.MILLISECONDS)
                        .observeOn(AndroidSchedulers.mainThread()).subscribe(along -> {
                    binding.smartLoadingView2.onSuccess(new SmartLoadingView.AnimationFullScreenListener() {
                        @Override
                        public void animationFullScreenFinish() {
                            Toast.makeText(MainActivity.this, "监听动画结束", Toast.LENGTH_SHORT).show();
                        }
                    });
                });
                break;

            case R.id.smartLoadingView_3_:
                binding.smartLoadingView3.start();
                Observable.timer(2000, TimeUnit.MILLISECONDS)
                        .observeOn(AndroidSchedulers.mainThread()).subscribe(along -> {
                    binding.smartLoadingView3.netFail();
                });
                break;
            case R.id.smartLoadingView_4_:
                if (follow4Tag % 2 == 0) {
                    //这里是模拟关注
                    binding.smartLoadingView4.start();
                    Observable.timer(2000, TimeUnit.MILLISECONDS)
                            .observeOn(AndroidSchedulers.mainThread()).subscribe(along -> {
                        binding.smartLoadingView4.netFail("关注成功");
                    });
                } else {
                    //这里是模拟取消关注
                    binding.smartLoadingView4.reset();
                }
                follow4Tag++;

                break;


            case R.id.smartLoadingView_5_:
                //不设置模式，那么默认为正常模式 SmartLoadingView.OKAnimationType.NORMAL
                if (follow5Tag % 2 == 0) {
                    binding.smartLoadingView5.start();
                    Observable.timer(2000, TimeUnit.MILLISECONDS)
                            .observeOn(AndroidSchedulers.mainThread()).subscribe(along -> {
                        binding.smartLoadingView5.onSuccess(new SmartLoadingView.AnimationOKListener() {
                            @Override
                            public void animationOKFinish() {
                                Toast.makeText(MainActivity.this, "关注成功", Toast.LENGTH_SHORT).show();
                            }
                        });
                    });
                } else {
                    binding.smartLoadingView5.reset();
                }
                follow5Tag++;
                break;


            case R.id.smartLoadingView_hide:
                binding.smartLoadingViewHide.start();
                Observable.timer(2000, TimeUnit.MILLISECONDS)
                        .observeOn(AndroidSchedulers.mainThread()).subscribe(along -> {
                    binding.smartLoadingViewHide.onSuccess(new SmartLoadingView.AnimationOKListener() {
                        @Override
                        public void animationOKFinish() {
                            Toast.makeText(MainActivity.this, "关注成功", Toast.LENGTH_SHORT).show();
                        }
                    }, SmartLoadingView.OKAnimationType.HIDE);
                });
                break;

            case R.id.smartLoadingView_center:
                binding.smartLoadingViewCenter.start();
                Observable.timer(2000, TimeUnit.MILLISECONDS)
                        .observeOn(AndroidSchedulers.mainThread()).subscribe(along -> {
                    binding.smartLoadingViewCenter.onSuccess(new SmartLoadingView.AnimationOKListener() {
                        @Override
                        public void animationOKFinish() {
                            Toast.makeText(MainActivity.this, "关注成功", Toast.LENGTH_SHORT).show();
                        }
                    }, SmartLoadingView.OKAnimationType.TRANSLATION_CENTER);
                });
                break;

            case R.id.smartLoadingView_6_:
                binding.smartLoadingView6.start();
                Observable.timer(2000, TimeUnit.MILLISECONDS)
                        .observeOn(AndroidSchedulers.mainThread()).subscribe(along -> {
                    Toast.makeText(MainActivity.this, "关注失败，回到开始", Toast.LENGTH_SHORT).show();
                    binding.smartLoadingView6.backToStart();
                });
                break;

            case R.id.btnListen:
                //binding.btnListen.start2();
                binding.btnListen.loading();
                Observable.timer(2000, TimeUnit.MILLISECONDS)
                        .observeOn(AndroidSchedulers.mainThread()).subscribe(along -> {
                    Toast.makeText(MainActivity.this, "听", Toast.LENGTH_SHORT).show();
                    binding.btnListen.unloading();
                });
                break;

            case R.id.btnListen2:
                binding.btnListen.loading();
                Observable.timer(2000, TimeUnit.MILLISECONDS)
                        .observeOn(AndroidSchedulers.mainThread()).subscribe(along -> {
                    Toast.makeText(MainActivity.this, "听2", Toast.LENGTH_SHORT).show();
                    binding.btnListen.unloading();
                });
                break;

            case R.id.smartLoadingView_7_:
                binding.smartLoadingView7.start();
                Observable.timer(2000, TimeUnit.MILLISECONDS)
                        .observeOn(AndroidSchedulers.mainThread()).subscribe(along -> {
                    binding.smartLoadingView7.netFail();
                });
                break;
            case R.id.smartLoadingView_8_:
                binding.smartLoadingView8.start();
                Observable.timer(2000, TimeUnit.MILLISECONDS)
                        .observeOn(AndroidSchedulers.mainThread()).subscribe(along -> {
                    binding.smartLoadingView8.netFail();
                });
                break;

            case R.id.smartLoadingView_9_:
                Toast.makeText(MainActivity.this, "点击了", Toast.LENGTH_SHORT).show();
                binding.smartLoadingView9.setSmartClickable(false);
                break;

            case R.id.smartLoadingView_login_demo:
                binding.smartLoadingViewLoginDemo.start();
                Observable.timer(2000, TimeUnit.MILLISECONDS)
                        .observeOn(AndroidSchedulers.mainThread()).subscribe(along -> {
                    binding.smartLoadingViewLoginDemo.onSuccess(new SmartLoadingView.AnimationFullScreenListener() {
                        @Override
                        public void animationFullScreenFinish() {
                            startActivity(new Intent(MainActivity.this, LoginActivity.class));
                            overridePendingTransition(R.anim.scale_test_home, R.anim.scale_test2);
                        }
                    });
                });
                break;
            case R.id.smartLoadingView_follow_demo:
                binding.smartLoadingViewFollowDemo.start();
                Observable.timer(2000, TimeUnit.MILLISECONDS)
                        .observeOn(AndroidSchedulers.mainThread()).subscribe(along -> {
                    binding.smartLoadingViewFollowDemo.onSuccess(new SmartLoadingView.AnimationFullScreenListener() {
                        @Override
                        public void animationFullScreenFinish() {
                            startActivity(new Intent(MainActivity.this, FollowActivity.class));
                            overridePendingTransition(R.anim.scale_test_home, R.anim.scale_test2);
                        }
                    });
                });
                break;
        }

    }


    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mImmersionBar.destroy();
    }


}
