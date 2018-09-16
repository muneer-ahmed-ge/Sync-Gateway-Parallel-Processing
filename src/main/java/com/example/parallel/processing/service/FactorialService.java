package com.example.parallel.processing.service;

import java.util.ArrayList;
import java.util.List;
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

        if (number > 5) {
            callAnotherThread(number);
        }

        return factorial(number);
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
        int fact = 1;
        for (int count = number; count > 1; count--) {
            fact = fact * count;
        }
        log.debug("calculated input={} factorial={}", number, fact);
        return fact;
    }
}