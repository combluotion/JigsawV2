package com.uocp8.jigsawv2.dao.impl;

import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.uocp8.jigsawv2.dao.FireBaseScoreDao;
import com.uocp8.jigsawv2.model.FireBaseScore;

import java.util.List;

public class FireBaseScoreDaoImpl implements FireBaseScoreDao {

private DatabaseReference databaseReference;

public FireBaseScoreDaoImpl()
{
    FirebaseDatabase db = FirebaseDatabase.getInstance("https://jigsaw-f8f96-default-rtdb.europe-west1.firebasedatabase.app");
    databaseReference = db.getReference(FireBaseScore.class.getSimpleName());
}


    @Override
    public Task<Void> add(FireBaseScore score) {
        return databaseReference.push().setValue(score);
    }

    @Override
    public Query retrieve() {
        return databaseReference.orderByChild("time").limitToLast(10);
    }
}
