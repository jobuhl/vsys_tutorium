package ex3.part1;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class TimeCounter {

    private ArrayList<Long> processingTimes;
    private ArrayList<Long> waitingTimes;
    private ArrayList<Long> communicationTimes;


    public TimeCounter(){
        communicationTimes = new ArrayList();
        processingTimes = new ArrayList();
        waitingTimes = new ArrayList();
    }

    public void addCommunicationTime(Long time) {
        communicationTimes.add(time);
    }

    public void addProcessingTime(Long time) {
        processingTimes.add(time);
    }

    public void addWaitingTime(Long time) {
        waitingTimes.add(time);
    }

    public Map<String, Long> getAverages(){
        Map<String, Long> averages = new HashMap<String, Long>();
        averages.put("wt", this.calculateAvarage(this.waitingTimes));
        averages.put("pt", this.calculateAvarage(this.processingTimes));
        averages.put("ct", this.calculateAvarage(this.communicationTimes));
        return averages;
    }

    private Long calculateAvarage(ArrayList<Long> times) {
        long sum = 0;
        for (Long time: times) {
            sum += time;
        }
        return sum/times.size();
    }
}

