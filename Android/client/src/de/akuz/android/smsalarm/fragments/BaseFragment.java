package de.akuz.android.smsalarm.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.actionbarsherlock.app.SherlockFragment;

public class BaseFragment extends SherlockFragment {

	protected View root;
	protected LayoutInflater inflater;
	protected ViewGroup parent;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		this.parent = container;
		this.inflater = inflater;
		return root;
	}

	@SuppressWarnings("unchecked")
	public <T extends View> T findView(int id) {
		return (T) root.findViewById(id);
	}

	protected View inflateLayout(int layoutId) {
		root = inflater.inflate(layoutId, parent);
		return root;
	}

}
