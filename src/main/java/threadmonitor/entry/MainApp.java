/*
 * Copyright (c) 2018年01月10日 by XuanWu Wireless Technology Co.Ltd. 
 *             All rights reserved                         
 */
package threadmonitor.entry;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * @Description
 * @Author <a href="mailto:haosonglin@wxchina.com">songlin.Hao</a>
 * @Date 2018/1/10
 * @Version 1.0.0
 */
public class MainApp {

    private ObservableList<Progress> personData = FXCollections.observableArrayList();

    /**
     * Constructor
     */
    public MainApp() {
        // Add some sample data
        personData.add(new Progress("Hans", "Muster"));
        personData.add(new Progress("Ruth", "Mueller"));
        personData.add(new Progress("Heinz", "Kurz"));
        personData.add(new Progress("Cornelia", "Meier"));
        personData.add(new Progress("Werner", "Meyer"));
        personData.add(new Progress("Lydia", "Kunz"));
        personData.add(new Progress("Anna", "Best"));
        personData.add(new Progress("Stefan", "Meier"));
        personData.add(new Progress("Martin", "Mueller"));
    }

    /**
     * Returns the data as an observable list of Persons.
     * @return
     */
    public ObservableList<Progress> getPersonData() {
        return personData;
    }

}
