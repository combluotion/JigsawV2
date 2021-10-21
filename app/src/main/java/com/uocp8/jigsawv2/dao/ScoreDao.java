package com.uocp8.jigsawv2.dao;


import com.uocp8.jigsawv2.model.Score;

import java.util.List;

public interface ScoreDao {

    Long create(Score entity);
    List<Score> findTiles(Long id);
}
