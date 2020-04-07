package com.mssdevlab.baselib.upgrade;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.recyclerview.widget.RecyclerView;

import com.mssdevlab.baselib.BR;

import java.util.List;

public class UpgradeOptionsAdapter extends RecyclerView.Adapter<UpgradeOptionsAdapter.GenericViewHolder> {
    private int mLayoutId;
    private List<UpgradeOptionModel> mOptions;
    private UpgradeOptionsViewModel mViewModel;

    public UpgradeOptionsAdapter(@LayoutRes int layoutId, UpgradeOptionsViewModel viewModel) {
        this.mLayoutId = layoutId;
        this.mViewModel = viewModel;
    }

    private int getLayoutIdForPosition(int position) {
        return mLayoutId;
    }

    @Override
    public int getItemCount() {
        return mOptions == null ? 0 : mOptions.size();
    }

    @Nullable
    public UpgradeOptionModel getItemAt(int index){
        return mOptions == null ? null : mOptions.get(index);
    }


    @Override
    @NonNull
    public GenericViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        ViewDataBinding binding = DataBindingUtil.inflate(layoutInflater, viewType, parent, false);

        return new GenericViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull GenericViewHolder holder, int position) {
        holder.bind(mViewModel, position);
    }

    @Override
    public int getItemViewType(int position) {
        return getLayoutIdForPosition(position);
    }

    public void setOptions(List<UpgradeOptionModel> options) {
        this.mOptions = options;
    }

    static class GenericViewHolder extends RecyclerView.ViewHolder {
        final ViewDataBinding mBinding;

        GenericViewHolder(ViewDataBinding binding) {
            super(binding.getRoot());
            this.mBinding = binding;
        }

        void bind(UpgradeOptionsViewModel viewModel, Integer position) {
            mBinding.setVariable(BR.viewModel, viewModel);
            mBinding.setVariable(BR.position, position);
            mBinding.executePendingBindings();
        }

    }
}
