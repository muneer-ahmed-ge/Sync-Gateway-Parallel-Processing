package com.example.parallel.processing.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Callable;

import com.example.parallel.processing.utils.ConcurrentExecutor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class FactorialService {

    @Autowired
    private ConcurrentExecutor concurrentExecutor;

    public Integer calculate(int number) throws InvalidParamaterException {

        if (number > 10) {
            log.error("Number cannot be greater than 5");
            throw new InvalidParamaterException("Number cannot be greater than 5");
        }

        int f = factorial(number);

        if (number > 5) {
            callAnotherThread(number);
        }

        return f;
    }

    private void callAnotherThread(int number) {
        List<Callable<Integer>> tasks = new ArrayList<>();

        tasks.add(() -> {
            return subTask(number);
        });

        concurrentExecutor.execute("Sub Task", tasks);
    }

    private Integer subTask(int number) {
        try {
            return factorial(number);
        } catch (InvalidParamaterException e) {
            log.error("Failed to execute factorial", e);
            throw e;
        }
    }

    private int factorial(int number) {

        sleep();

        int fact = 1;
        for (int count = number; count > 1; count--) {
            fact = fact * count;
        }
        // log.debug("calculated input={} factorial={}", number, fact);
        return fact;
    }

    private static void sleep() {
        try {
            int i = new Random().nextInt((10 - 1) + 1) + 1;
            log.debug("sleeping for " + i + " seconds");
            Thread.currentThread().sleep(i * 1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}