package com.uocp8.jigsawv2.ui.home;

import android.app.Application;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.uocp8.jigsawv2.MainActivity;
import com.uocp8.jigsawv2.dao.ScoreDao;
import com.uocp8.jigsawv2.dao.impl.ScoreDaoImpl;
import com.uocp8.jigsawv2.model.Score;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class HomeViewModel extends AndroidViewModel {

    private MutableLiveData<String> mText;
    private MutableLiveData<ArrayList<String>> mListView;

    public HomeViewModel(@NonNull Application application) {
        super(application);
        mText = new MutableLiveData<>();
        mText.setValue("This is home fragment");

        mListView = new MutableLiveData<>();

    }

    public LiveData<String> getText() {
        return mText;
    }

    public LiveData<ArrayList<String>> loadScore() {
        ScoreDao scoreDao = new ScoreDaoImpl(getApplication().getApplicationContext());

        List<Score> scores = scoreDao.retrieveScores();
        ArrayList<String> scoreStrings = new ArrayList<String>();

        scores.forEach((x) -> scoreStrings.add(x.getName()+", "+x.getTime() + ", el día " +x.getDate()));
        mListView.setValue(scoreStrings);

        return mListView;

    }
}