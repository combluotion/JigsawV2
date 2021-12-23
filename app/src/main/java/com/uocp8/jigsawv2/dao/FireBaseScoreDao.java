package com.uocp8.jigsawv2.dao;


import com.google.android.gms.tasks.Task;
import com.google.firebase.database.Query;
import com.uocp8.jigsawv2.model.FireBaseScore;

import java.util.List;

public interface FireBaseScoreDao {

public Task<Void> add (FireBaseScore score);
public Query retrieve ();


}
