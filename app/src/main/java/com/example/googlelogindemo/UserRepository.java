package com.example.googlelogindemo;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Room;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import java.util.List;

public class UserRepository {

    private String DB_NAME = "db_flight";
    UserDataBase userDataBase;
    Context context;

    public UserRepository(Context context){
        this.context=context;
        userDataBase = Room.databaseBuilder(context,UserDataBase.class,DB_NAME).build();

    }



    public void register(final User user){

        final Long[] userId = new Long[1];

        new AsyncTask<Void, Void, Long>() {
            @Override
            protected Long doInBackground(Void... voids) {


                Long id =  userDataBase.userDao().register(user);

                return id;
            }

            @Override
            protected void onPostExecute(Long aLong) {
                super.onPostExecute(aLong);

                userId[0] = aLong;
                Log.e("userId",aLong+"");

                if(aLong>0){
                    Toast.makeText(context, "User has been registered successfully", Toast.LENGTH_SHORT).show();
                 /*   Intent intent = new Intent(context,Home.class);
                    Bundle bundle = new Bundle();
                    bundle.putLong("userid",aLong);
                    intent.putExtras(bundle);
                    context.startActivity(intent);*/

                    //context

                }else {
                    Toast.makeText(context, "User not register successfully", Toast.LENGTH_SHORT).show();
                }
                //return id[0];
            }
        }.execute();


    }


   /* public Long register(User user){
        return userDataBase.userDao().register(user);
    }*/

    public void updateUser(final User user){

        final Long[] userId = new Long[1];

        new AsyncTask<Void, Void, Long>() {
            @Override
            protected Long doInBackground(Void... voids) {


                 userDataBase.userDao().updateUser(user);

               return null;
            }

            @Override
            protected void onPostExecute(Long aLong) {
                super.onPostExecute(aLong);

                Toast.makeText(context, "User has been updated successfully", Toast.LENGTH_SHORT).show();
            }
        }.execute();


    }
    public LiveData<User> login(String username, String password){

        return userDataBase.userDao().login(username,password);

    }


    public LiveData<User> userdetails(Long id){

        return userDataBase.userDao().userDetails(id);

    }




}
