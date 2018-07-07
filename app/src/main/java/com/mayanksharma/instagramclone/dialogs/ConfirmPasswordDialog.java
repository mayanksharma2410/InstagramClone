package com.mayanksharma.instagramclone.dialogs;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.mayanksharma.instagramclone.R;

public class ConfirmPasswordDialog extends DialogFragment {
    private final static String TAG = "ConfirmPasswordDialog";

    TextView mPassword;

    public interface OnConfirmPasswordListener{
        public void onConfirmPassword(String password);
    }

    OnConfirmPasswordListener mOnConfirmPasswordListener;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_confirm_password, container, false);
        Log.d(TAG, "onCreateView: started dialog...");

        mPassword = (TextView)view.findViewById(R.id.confirm_password);

        TextView confirm = (TextView)view.findViewById(R.id.dialogConfirm);
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: captured firebase and confirming");
                String password = mPassword.getText().toString();

                if (!password.equals("")) {
                    mOnConfirmPasswordListener.onConfirmPassword(password);
                    getDialog().dismiss();
                }else
                {
                    Toast.makeText(getActivity(), "please enter the password", Toast.LENGTH_SHORT).show();
                }
            }
        });

        TextView cancel = (TextView)view.findViewById(R.id.dialogCancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: canceling the dialog...");
                getDialog().dismiss();
            }
        });

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try
        {
            mOnConfirmPasswordListener = (OnConfirmPasswordListener) getTargetFragment();

        }catch (ClassCastException e)
        {
            Log.d(TAG, "onAttach: ClassCastException  " + e.getMessage());
        }
    }
}
