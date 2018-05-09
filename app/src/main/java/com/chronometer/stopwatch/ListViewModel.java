package com.chronometer.stopwatch;

public class ListViewModel {

    private String main_count_down_timer;
    private String current_count_down_timer;
    private int counter;

    @Override
    public String toString() {
        return "ListViewModel{" +
                "main_count_down_timer='" + main_count_down_timer + '\'' +
                ", current_count_down_timer='" + current_count_down_timer + '\'' +
                ", counter=" + counter +
                '}';
    }

    ListViewModel(String main_count_down_timer, String current_count_down_timer, int counter) {
        this.main_count_down_timer = main_count_down_timer;
        this.current_count_down_timer = current_count_down_timer;
        this.counter = counter;
    }

    int getCounter() {

        return counter;
    }

    String getMain_count_down_timer() {
        return main_count_down_timer;
    }

    String getCurrent_count_down_timer() {
        return current_count_down_timer;
    }

}
