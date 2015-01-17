package com.skroll.classifier;

/**
 * Created by saurabhagarwal on 1/5/15.
 */
public class Category {

    int id;
    String name;

    public Category(int id, String name){
        this.id =id;
        this.name=name;
    }
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public String toString(){
        return "id("+id+"),name("+name +")";
    }
}


