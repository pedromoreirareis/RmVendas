package com.pedromoreirareisgmail.rmvendas.activitys;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.pedromoreirareisgmail.rmvendas.Fire.Fire;
import com.pedromoreirareisgmail.rmvendas.R;
import com.pedromoreirareisgmail.rmvendas.Utils.PrefsUser;
import com.pedromoreirareisgmail.rmvendas.models.User;

public class SettingsActivity extends AppCompatActivity {

    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        mContext = SettingsActivity.this;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case android.R.id.home:

                if (!PrefsUser.getCompanyCnpj(mContext).isEmpty()) {

                    User user = new User();

                    user.setName(Fire.getUser().getDisplayName());
                    user.setCpf(PrefsUser.getCompanyCpf(mContext));
                    user.setCompanyId(PrefsUser.getCompanyCnpj(mContext));
                    user.setCompanyName(PrefsUser.getCompanyName(mContext));
                    user.setToken(Fire.getDeviceToken());

                    Fire.getRefColUser(user.getCompanyId()).document(user.getName())
                            .set(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {


                        }
                    });
                }

                finish();

                return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
