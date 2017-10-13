package com.example;

import de.greenrobot.daogenerator.DaoGenerator;
import de.greenrobot.daogenerator.Entity;
import de.greenrobot.daogenerator.Property;
import de.greenrobot.daogenerator.Schema;

public class DbGenerate {
    public static void main(String[] args){
        // 通过schema创建应该实体：Son
        Schema schema = new Schema(1,"gdd.cy.even.com");

        Entity son = schema.addEntity("Son");
        son.addStringProperty("name");
        son.addIntProperty("age");
        son.addIdProperty();
//        Property fatherId = son.addLongProperty("fatherId").getProperty();

        Entity father = schema.addEntity("Father");
        father.addIdProperty();
        father.addStringProperty("name");
        father.addIntProperty("age");
        Property sonId = father.addLongProperty("sonId").getProperty();

        father.addToOne(son,sonId);
        son.addToMany(father,sonId).setName("fathers");

//        son.addToOne(father,fatherId);

        try {
            new DaoGenerator().generateAll(schema,"app/src/main/java");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
