package com.skroll.classification.category;

public final class Application {

    /**
     This code is completely unaware of the underlying datastore.
     It could be a text file, or a relational database.
     */
    void addNewCategory(Category aCategory){
        DAOFactory factory = new DAOFactory();
        try {
            factory.getCategoryDAO().add(aCategory);
        }
        catch (DataAccessException ex) {
            ex.printStackTrace();
        }
    }

}
