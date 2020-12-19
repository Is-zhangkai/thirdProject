package com.example.thirdproject;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentTransaction;

import android.annotation.SuppressLint;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.example.thirdproject.fragment.ShoppingFragment;
import com.example.thirdproject.fragment.StoreFragment;
import com.example.thirdproject.fragment.UserCenterFragment;

public class MainActivity extends AppCompatActivity implements RadioGroup.OnCheckedChangeListener{

    private ShoppingFragment shoppingFragment;
    private StoreFragment storeFragment;
    private UserCenterFragment userCenterFragment;
    private RadioGroup radioGroup;
    private RadioButton rbtn_shopping;
    private RadioButton rbtn_store;
    private RadioButton rbtn_user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_main);
         radioGroup = findViewById(R.id.radioGroup);
        radioGroup.setOnCheckedChangeListener(this);
         rbtn_shopping = findViewById(R.id.btn_main_shopping);
         rbtn_store = findViewById(R.id.btn_main_store);
         rbtn_user = findViewById(R.id.btn_main_user);
        rbtn_shopping.setChecked(true);
        initview();
    }


    private void initview(){


        //图片的优化，其他三个图片做类似处理
         //底部导航的时候会发生图片的颜色变化，所以radiobutton中的照片不是一张，而是引用了自定义的选择器照片
         //本来使用的是getResources.getDrawable,不过已经过时，所以使用ContextCompat

        // 当这个图片被绘制时，给他绑定一个矩形规定这个矩形
        // 参数前两个对应图片相对于左上角的新位置，后两个为图片的长宽
        Drawable shopping = ContextCompat.getDrawable(this, R.drawable.shopping);
        assert shopping != null;
        shopping.setBounds(0, 0, 60, 60);
        rbtn_shopping.setCompoundDrawables(null, shopping, null, null);

        Drawable store = ContextCompat.getDrawable(this, R.drawable.store);
        assert store != null;
        store.setBounds(0, 0, 60, 60);
        rbtn_store.setCompoundDrawables(null, store, null, null);

        Drawable user = ContextCompat.getDrawable(this, R.drawable.user);
        assert user != null;
        user.setBounds(0, 0, 60, 60);
        rbtn_user.setCompoundDrawables(null, user, null, null);


    }

    @SuppressLint("ResourceAsColor")
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        hideAllFragment(transaction);
        switch (checkedId) {
            case R.id.btn_main_shopping:
                if (shoppingFragment == null) {
                    shoppingFragment = new ShoppingFragment();
                    transaction.add(R.id.frameLayout, shoppingFragment);

                } else {

                    transaction.show(shoppingFragment);
                }
                break;
            case R.id.btn_main_store:
                if (storeFragment == null) {
                    storeFragment = new StoreFragment();
                    transaction.add(R.id.frameLayout, storeFragment);
                } else {
                    transaction.show(storeFragment);
                }
                break;
            case R.id.btn_main_user:
                if (userCenterFragment == null) {
                    userCenterFragment = new UserCenterFragment();
                    transaction.add(R.id.frameLayout, userCenterFragment);
                } else {
                    transaction.show(userCenterFragment);
                    rbtn_user.setTextCursorDrawable(R.color.colorWhite);
                }
                break;

        }
        transaction.commit();
    }

    public void hideAllFragment(FragmentTransaction transaction){
        if(shoppingFragment!=null){
            transaction.hide(shoppingFragment);
        }
        if(storeFragment!=null){
            transaction.hide(storeFragment);
        }
        if(userCenterFragment!=null){
            transaction.hide(userCenterFragment);
        }

    }
}