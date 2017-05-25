package com.intugine.ble.beacon.ui.scanner;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import static com.intugine.ble.beacon.util.LogUtils.makeLogTag;

/**
 * Created by niraj on 04-05-2017.
 */
public class AdapterScannedDevice extends RecyclerView.Adapter<HolderScannedDevice> {

    private static final String TAG = makeLogTag(AdapterScannedDevice.class);

    public List<ModelBle> modelBleList;
    private final LayoutInflater mInflater;

    public AdapterScannedDevice(Context context) {
        this.mInflater = LayoutInflater.from(context);
        modelBleList= new ArrayList<ModelBle>();

    }


    @Override
    public HolderScannedDevice onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(HolderScannedDevice.getLayoutResId(), parent, false);
        return new HolderScannedDevice(view);
    }

    @Override
    public void onBindViewHolder(HolderScannedDevice holder, int position) {
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






    //public


}
