package com.intugine.ble.beacon.ui.base;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.intugine.ble.beacon.R;

import java.lang.reflect.Field;

/**
 * Created by niraj on 04-05-2017.
 */
public abstract class FragmentBase extends Fragment implements Toolbar.OnMenuItemClickListener {

    private boolean mIsCollapsingToolbar;
    private boolean mIsTitleAddedInToolbar;
    public Toolbar vToolbar;
    private CollapsingToolbarLayout vCollapsingToolbar;
    private TextView vTVToolbar;

    public String mTitle = "nav test 41";
    public abstract boolean setNavigationHomeAsBack();
    protected abstract String getToolBarDefaultTitle();
    public abstract int getLayoutResourceId();


    @Override
    public void onSaveInstanceState(Bundle outState) {

        System.gc();
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        System.gc();
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(getLayoutResourceId(),container, false);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setToolbarTitle(getToolBarDefaultTitle());
    }

    public void initToolbar(View view , int toolbarId, int navigationDrawable) {
        vToolbar = (Toolbar) view.findViewById(toolbarId);
        vToolbar.setNavigationIcon(navigationDrawable);
        vToolbar.setOnMenuItemClickListener(this);
        if(setNavigationHomeAsBack()){
            vToolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // TODO Auto-generated method stub
                    getActivity().onBackPressed();
                }
            });
        };
    }

    public void initCollapsingToolbar(boolean isTitleAddedInToolbar, View view,
                                      int collapsingToolbarId, int toolbarId, int navigationDrawable) {
        mIsCollapsingToolbar = true;
        mIsTitleAddedInToolbar = isTitleAddedInToolbar;
        initToolbar(view, toolbarId, navigationDrawable);
        if (!mIsTitleAddedInToolbar) {
            vCollapsingToolbar = (CollapsingToolbarLayout) view.findViewById(collapsingToolbarId);
            //vCollapsingToolbar.setTitle(mTitle);
            vToolbar.setTitle("");
        } else {
            //LOGV(TAG, isTitleAddedInToolbar+" ");
            vToolbar.setTitle("");
            initToolbarTextView();
            //setToolbarTVTitle();
        }
    }


    private void initToolbarTextView() {
        vTVToolbar = new TextView(this.getContext());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            vTVToolbar.setMarqueeRepeatLimit(-1);
            vTVToolbar.setMaxLines(1);
            vTVToolbar.setEllipsize(TextUtils.TruncateAt.END);
            vTVToolbar.setTextAppearance(R.style.App_Base_TextAppearance_Toolbar_Light);
        } else {

            vTVToolbar.setMarqueeRepeatLimit(-1);
            vTVToolbar.setMaxLines(1);
            vTVToolbar.setEllipsize(TextUtils.TruncateAt.END);
            vTVToolbar.setTextAppearance(this.getContext(), R.style.App_Base_TextAppearance_Toolbar_Light);
        }
        vToolbar.addView(vTVToolbar);
    }

    public void setToolbarTitle(String pTitle) {
        if(vToolbar!=null) {
            if (pTitle == null) {
                mTitle = getToolBarDefaultTitle();
            } else {
                mTitle = pTitle;
            }

            if (mIsCollapsingToolbar) {
                if (mIsTitleAddedInToolbar && vTVToolbar != null) {
                    vTVToolbar.setText(mTitle);
                } else if (vCollapsingToolbar != null) {
                    vCollapsingToolbar.setTitle(mTitle);
                }
            } else {
                vToolbar.setTitle(mTitle);
            }
        }
        /*if(mToolbar!=null){
            setToolbarTVTitle();
        }*/
    }


    public void setTitle(String title) {
        this.mTitle = title;
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        return false;
    }

    @Override
    public void onDetach() {
        super.onDetach();

        try {
            Field childFragmentManager = Fragment.class.getDeclaredField("mChildFragmentManager");
            childFragmentManager.setAccessible(true);
            childFragmentManager.set(this, null);

        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public String getTitle() {
        return mTitle;
    }

    public TextView getToolbarTextView(){
        return  vTVToolbar;
    }
}