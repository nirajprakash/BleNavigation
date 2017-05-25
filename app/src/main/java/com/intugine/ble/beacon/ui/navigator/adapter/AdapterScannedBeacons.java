package com.intugine.ble.beacon.ui.navigator.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.intugine.ble.beacon.ui.scanner.ModelBle;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static com.intugine.ble.beacon.util.LogUtils.makeLogTag;

/**
 * Created by niraj on 04-05-2017.
 */
public class AdapterScannedBeacons extends RecyclerView.Adapter<HolderScannedBeacon> {

    private static final String TAG = makeLogTag(AdapterScannedBeacons.class);

    public List<ModelBle> modelBleList;
    private final LayoutInflater mInflater;

    public AdapterScannedBeacons(Context context) {
        this.mInflater = LayoutInflater.from(context);
        modelBleList= new ArrayList<ModelBle>();

    }


    @Override
    public HolderScannedBeacon onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(HolderScannedBeacon.getLayoutResId(), parent, false);
        return new HolderScannedBeacon(view);
    }

    @Override
    public void onBindViewHolder(HolderScannedBeacon holder, int position) {
        holder.bind(modelBleList.get(position));

    }

    @Override
    public int getItemCount() {
        return modelBleList.size();
    }

    public void changeitem(ModelBle modelView, int index) {
        modelBleList.set(index, modelView);
        notifyItemChanged(index);
    }
    private void addItem(int position, ModelBle model) {
        modelBleList.add(position, model);
        notifyItemInserted(position);
    }
    protected ModelBle removeItem(int position) {
        final ModelBle model = modelBleList.remove(position);
        notifyItemRemoved(position);
        return model;
    }

    public void addItem(ModelBle modelBle){
        if(modelBle==null){
            return;
        }
        int index = modelBleList.indexOf(modelBle);
        if(index >= 0){
            changeitem(modelBle, index);
        }else{
            addItem(0, modelBle);
        }
    }

    private void animateAllRemovals(){
        for (int i = modelBleList.size() - 1; i >= 0; i--) {
            //final ModelView model = mModelViewSet.get(i);
            removeItem(i);

        }
    }


    private void removalItems(List<ModelBle> removeModels) {

        for (int i = modelBleList.size() - 1; i >= 0; i--) {
            final ModelBle model = modelBleList.get(i);
            if (removeModels.contains(model)) {
                //LOGW(TAG ,"removing: "+ model.bluetoothDevice.getAddress());
                removeItem(i);

            }
        }
    }

    public void resetItems(@NonNull List<ModelBle> newDataSet) {
        animateAllRemovals();
        animateAllAdditions(newDataSet);

    }
    private void animateAllAdditions(List<ModelBle> newModels) {

        if(newModels!=null) {
            for (int i = 0, count = newModels.size(); i < count; i++) {
                final ModelBle model = newModels.get(i);
                addItem(i, model);
            }
        }
    }

    public void checkToRemoveDeadBle() {
        long time = Calendar.getInstance().getTimeInMillis();
        List<ModelBle> removeList = new ArrayList<ModelBle>();
        for (ModelBle modelBle :
                modelBleList) {
            long timeDelta = time-modelBle.time;
            //LOGV(TAG, "time Delta: "+ timeDelta);
            if((timeDelta)>5000){
                //LOGW(TAG, "time Delta: "+ timeDelta);

                removeList.add(modelBle);
            }
        }
        if(removeList.size()>0){
            removalItems(removeList);
        }
    }


    //public


}
