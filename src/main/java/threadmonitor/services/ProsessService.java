/*
 * Copyright (c) 2018年01月10日 by XuanWu Wireless Technology Co.Ltd. 
 *             All rights reserved                         
 */
package threadmonitor.services;

import java.util.Arrays;
import java.util.Date;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import threadmonitor.entry.Progress;

/**
 * @Description
 * @Author <a href="mailto:haosonglin@wxchina.com">songlin.Hao</a>
 * @Date 2018/1/10
 * @Version 1.0.0
 */
public class ProsessService {

    SshService sshService = new SshService();
    DbService dbService = new DbService();

    private BlockingQueue<ObservableList<Progress>> blockingQueue = new ArrayBlockingQueue<ObservableList<Progress>>(
            10);

    public void init() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    updateProgress();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }, "queryprosess").start();
    }

    public void updateProgress() throws InterruptedException {
        while (true) {
            ObservableList<Progress> progresses = FXCollections.observableArrayList();
            String[] prosessArray = sshService.excute("ps aux").split("\n");
            for (int i = 1; i < prosessArray.length; i++) {
                String[] prosess = prosessArray[i].split(" +");
                prosess[10] = String.join(" ", Arrays.copyOfRange(prosess, 10, prosess.length));
                progresses.add(convertToObsList(prosess));
            }
            blockingQueue.put(progresses);
            dbService.insertProsess(progresses);
            try {
                Thread.sleep(TimeUnit.SECONDS.toMillis(3));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public ObservableList<Progress> getProgress() {
        try {
            return blockingQueue.take();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return FXCollections.observableArrayList();
    }

    private Progress convertToObsList(String[] prosessInfo) {
        Progress prosess = new Progress();
        prosess.setUserColumn(prosessInfo[0]);
        prosess.setPidColumn(prosessInfo[1]);
        prosess.setCpuColumn(prosessInfo[2]);
        prosess.setMemColumn(prosessInfo[3]);
        prosess.setVszColumn(KbToMb(prosessInfo[4]));
        prosess.setRssColumn(KbToMb(prosessInfo[5]));
        prosess.setTtyColumn(prosessInfo[6]);
        prosess.setStatColumn(prosessInfo[7]);
        prosess.setStartColumn(prosessInfo[8]);
        prosess.setTimeColumn(prosessInfo[9]);
        prosess.setCommandColumn(prosessInfo[10]);
        prosess.setDate(new Date());
        return prosess;
    }

    private String KbToMb(String kb){
        return String.valueOf(Integer.valueOf(kb)/1024);
    }
}
