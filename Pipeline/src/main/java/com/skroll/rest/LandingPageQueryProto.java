package com.skroll.rest;


import java.util.ArrayList;
import java.util.List;

/**
 * Created by saurabhagarwal on 1/14/16.
 */
public class LandingPageQueryProto {
    List<SelectedChip> selectedChips = new ArrayList<>();
    String searchText ="";

    public LandingPageQueryProto() {
    }

    public void addChip(String id, String field1, String field2, String type){
        SelectedChip selectedChip = new SelectedChip(id,field1,field2,type);
        selectedChips.add(selectedChip);
    }
    @Override
    public String toString() {
        return "LandingPageQueryProto{" +
                "selectedChips=" + selectedChips +
                ", searchText='" + searchText + '\'' +
                '}';
    }

    static class SelectedChip {
        String id;
        String field1;
        String field2;
        String type;

        public SelectedChip(String id, String field1, String field2, String type) {
            this.id = id;
            this.field1 = field1;
            this.field2 = field2;
            this.type = type;
        }

        @Override
        public String toString() {
            return "SelectedChip{" +
                    "id='" + id + '\'' +
                    ", field1='" + field1 + '\'' +
                    ", field2='" + field2 + '\'' +
                    ", type='" + type + '\'' +
                    '}';
        }
    }
}
