package com.intugine.ble.beacon.ui.accelerometer.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.intugine.ble.beacon.ui.accelerometer.model.ModelBleAccelerometer;

import java.util.ArrayList;
import java.util.List;

import static com.intugine.ble.beacon.util.LogUtils.makeLogTag;

/**
 * Created by niraj on 04-05-2017.
 */
public class AdapterAccelerometerScannedDevice extends RecyclerView.Adapter<HolderAccelerometerScannedDevice> {

    private static final String TAG = makeLogTag(AdapterAccelerometerScannedDevice.class);

    public List<ModelBleAccelerometer> modelBleList;
    private final LayoutInflater mInflater;

    public AdapterAccelerometerScannedDevice(Context context) {
        this.mInflater = LayoutInflater.from(context);
        modelBleList= new ArrayList<ModelBleAccelerometer>();
    }

    @Override
    public HolderAccelerometerScannedDevice onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(HolderAccelerometerScannedDevice.getLayoutResId(), parent, false);
        return new HolderAccelerometerScannedDevice(view);
    }

    @Override
    public void onBindViewHolder(HolderAccelerometerScannedDevice holder, int position) {
        holder.bind(modelBleList.get(position));
    }

    @Override
    public int getItemCount() {
        return modelBleList.size();
    }

    public void changeitem(ModelBleAccelerometer modelView, int index) {
        modelBleList.set(index, modelView);
        notifyItemChanged(index);
    }
    private void addItem(int position, ModelBleAccelerometer model) {
        modelBleList.add(position, model);
        notifyItemInserted(position);
    }
    protected ModelBleAccelerometer removeItem(int position) {
        final ModelBleAccelerometer model = modelBleList.remove(position);
        notifyItemRemoved(position);
        return model;
    }

    public void addItem(ModelBleAccelerometer modelBle){
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

    public void resetItems(@NonNull List<ModelBleAccelerometer> newDataSet) {
        animateAllRemovals();
        animateAllAdditions(newDataSet);

    }
    private void animateAllAdditions(List<ModelBleAccelerometer> newModels) {

        if(newModels!=null) {
            for (int i = 0, count = newModels.size(); i < count; i++) {
                final ModelBleAccelerometer model = newModels.get(i);
                addItem(i, model);
            }
        }
    }

    //public
}
