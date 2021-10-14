package com.uocp8.jigsawv2.dao;

import com.uocp8.jigsawv2.model.ImageEntity;

import java.util.List;

public interface ImageDao {
    /**
     * Save an image entity
     *
     * @param entity the entity to create
     * @return the generated id
     */
    Long create(ImageEntity entity);

    /**
     * Find entity by id
     *
     * @param id the id of the entity
     * @return the matching entity
     */
    ImageEntity find(Long id);

    /**
     * Find the jigsaw tiles for the original id
     *
     * @param id the original image id
     * @return the jigsaw entities
     */
    List<ImageEntity> findTiles(Long id);

    /**
     * Update the given image entity
     *
     * @param entity the entity to update
     * @return number of rows updated
     */
    int update(ImageEntity entity);

    /**
     * Delete the entity by id
     *
     * @param id the id of the entity
     * @return number of rows deleted
     */
    int delete(Long id);

}
