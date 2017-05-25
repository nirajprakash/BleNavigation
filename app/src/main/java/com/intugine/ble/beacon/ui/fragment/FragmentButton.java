package com.intugine.ble.beacon.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.intugine.ble.beacon.R;

import static com.intugine.ble.beacon.util.LogUtils.makeLogTag;

/**
 * Created by niraj on 04-05-2017.
 */
public class FragmentButton  extends Fragment {

    private static final String TAG = makeLogTag(FragmentButton.class);
    public static Fragment getInstance(int position, Bundle bundle) {
        // TODO Auto-generated method stub
        FragmentButton fragment = new FragmentButton();
        Bundle args = new Bundle(bundle);
        args.putInt("position", position);
        fragment.setArguments(args);
        return fragment;
    }

    public static String[] FRAGMENT_VAL = new String[]{

            TestFragment11Ble.class.getSimpleName(),
//            Test22FragmentAuthGoogle.class.getSimpleName(),
//            Test31FragmentHardware.class.getSimpleName(),
//            Test32FragmentNet.class.getSimpleName(),
//            Test41FragmentNotification.class.getSimpleName(),
//            Test42AccessTokenCheck.class.getSimpleName(),
//            Test51FragmentAuthNotification.class.getSimpleName(),
//            FragmentTest52Recycler.class.getSimpleName(),
//            FragmentProfile.class.getSimpleName(),
//            Test62FragmentAuthFacebook.class.getSimpleName(),
//            Test72AccountFragment.class.getSimpleName(),
//            // FragmentTest2.class.getSimpleName(),
            // FragmentRestaurantView.class.getSimpleName(),
            // FragmentWelcome.class.getSimpleName()
    };

    private static final int[] FRAGMENTS = new int[]{11, 12, 21, 22, 31, 32, 41, 42, 51, 52, 61, 62};

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_button, container, false);
        return view;
    }

    public void startFragment(View v, FragmentManager fm){

        Fragment fragment = null;
        Bundle bundle = new Bundle();
        //bundle.putString(FRAGMENT_PARENT_TAG, DepricatedMainActivity.FRAGMENT_DEFAULT_VAL);
        FragmentTransaction ft = fm.beginTransaction();

        int id = v.getId();
        int fragmentPosition = 0;
        if (id == R.id.btn_test11) {
            fragment = TestFragment11Ble.newInstance(FRAGMENTS[0], bundle);
            fragmentPosition = 0;
            ft.replace(R.id.navigation_drawer_layout, fragment, FRAGMENT_VAL[fragmentPosition])
                    .addToBackStack(FRAGMENT_VAL[fragmentPosition]).commit();

        }/*else if(id== R.id.btn_test22){
            fragment = Test22FragmentAuthGoogle.newInstance(FRAGMENTS[1], bundle);
            fragmentPosition = 1;
            ft.replace(R.id.navigation_drawer_layout, fragment, FRAGMENT_VAL[fragmentPosition])
                    .addToBackStack(FRAGMENT_VAL[fragmentPosition]).commit();

        }else if(id== R.id.btn_test31){
            fragment = Test31FragmentHardware.newInstance(FRAGMENTS[2], bundle);
            fragmentPosition = 2;
            ft.replace(R.id.frame_content, fragment, FRAGMENT_VAL[fragmentPosition])
                    .addToBackStack(FRAGMENT_VAL[fragmentPosition]).commit();

        }else if(id== R.id.btn_test32){
            fragment = Test32FragmentNet.newInstance(FRAGMENTS[3], bundle);
            fragmentPosition = 3;
            ft.replace(R.id.frame_content, fragment, FRAGMENT_VAL[fragmentPosition])
                    .addToBackStack(FRAGMENT_VAL[fragmentPosition]).commit();

        }else if(id== R.id.btn_test41){
            fragment = Test41FragmentNotification.newInstance(FRAGMENTS[4], bundle);
            fragmentPosition = 4;
            ft.replace(R.id.frame_content, fragment, FRAGMENT_VAL[fragmentPosition])
                    .addToBackStack(FRAGMENT_VAL[fragmentPosition]).commit();

        }else if(id== R.id.btn_test42){
            fragment = Test42AccessTokenCheck.newInstance(FRAGMENTS[5], bundle);
            fragmentPosition = 5;
            ft.replace(R.id.navigation_drawer_layout, fragment, FRAGMENT_VAL[fragmentPosition])
                    .addToBackStack(FRAGMENT_VAL[fragmentPosition]).commit();

        }else if(id== R.id.btn_test51){
            fragment = Test51FragmentAuthNotification.newInstance(FRAGMENTS[6], bundle);
            fragmentPosition = 6;
            ft.replace(R.id.navigation_drawer_layout, fragment, FRAGMENT_VAL[fragmentPosition])
                    .addToBackStack(FRAGMENT_VAL[fragmentPosition]).commit();
        }else if(id== R.id.btn_test52){
            fragment = FragmentTest52Recycler.newInstance(FRAGMENTS[7], bundle);
            fragmentPosition = 7;
            ft.replace(R.id.frame_content, fragment, FRAGMENT_VAL[fragmentPosition])
                    .addToBackStack(FRAGMENT_VAL[fragmentPosition]).commit();
        }else if(id== R.id.btn_test62){
            fragment = Test62FragmentAuthFacebook.newInstance(FRAGMENTS[9], bundle);
            fragmentPosition = 9;
            ft.replace(R.id.navigation_drawer_layout, fragment, FRAGMENT_VAL[fragmentPosition])
                    .addToBackStack(FRAGMENT_VAL[fragmentPosition]).commit();
        }else if(id== R.id.btn_test72){
            fragment = Test72AccountFragment.newInstance(FRAGMENTS[10], bundle);
            fragmentPosition = 10;
            ft.replace(R.id.navigation_drawer_layout, fragment, FRAGMENT_VAL[fragmentPosition])
                    .addToBackStack(FRAGMENT_VAL[fragmentPosition]).commit();
        }*/

        /* else if (id == R.id.btn_test2) {
            fragment = FragmentTest2.getInstance(FRAGMENTS[1], bundle);
            fragmentPosition = 1;
        } else if (id == R.id.btn_test21) {
            fragment = FragmentImageViewer.getInstance(FRAGMENTS[2], bundle);
            fragmentPosition = 2;
        } else if (id == R.id.btn_rBtnWelcome) {
            fragment = FragmentWelcome.newInstance(FRAGMENTS[3], bundle);
            fragmentPosition = 3;
        } else {
            fragment = FragmentTest21.getInstance(FRAGMENTS[0], bundle);
            fragmentPosition = 0;
        }
        ft.replace(R.id.frame_content, fragment, FRAGMENT_VAL[fragmentPosition]).addToBackStack(FRAGMENT_VAL[fragmentPosition]).commit();
        */
    }

}