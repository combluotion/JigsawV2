package com.uocp8.jigsawv2.dao;

import com.uocp8.jigsawv2.model.ImgPath;

import java.util.ArrayList;
import java.util.List;

public interface ImgPathDao {
    Long create(ImgPath entity);
    ArrayList<String> retrievePaths();
}
