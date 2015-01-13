package com.skroll.classifier.category;

import com.skroll.classifier.Category;

public interface CategoryDAO {

    Category fetch(String aId) throws DataAccessException;

    void add(Category aCategory) throws DataAccessException;

    void change(Category aCategory) throws DataAccessException;

    void delete(Category aCategory) throws DataAccessException;
}

// This will be based by Derby database

final class CategoryDAORelational implements CategoryDAO {

  /*
   * The constructor will usually be passed any required config data.
   */

    @Override public void add(Category aCategory) throws DataAccessException {

    }

    @Override public void change(Category aCategory) throws DataAccessException {

    }

    @Override public void delete(Category aCategory) throws DataAccessException {

    }

    @Override public Category fetch(String aId) throws DataAccessException {
        return null;
    }

}

/**
 Returns all DAO instances.

 Reads a configuration item (defined by your program) to decide
 which family of DAO objects it should be returning, for example, it could be based on
 file-based or relational-based.

 The configuration mechanism may be a System property, a properties file, an
 XML file, and so on. The config is often read when the system initializes,
 perhaps using a static initializer.
 */
final class CategoryFactory {

    CategoryDAO getCategoryDAO(){
        return new CategoryDAORelational();
    }


}