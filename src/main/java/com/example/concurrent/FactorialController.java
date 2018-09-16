package com.example.concurrent;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
public class FactorialController {

    @Autowired
    private FactorialService factorialService;

    @Autowired
    private ConcurrentExecutor concurrentExecutor;

    @GetMapping("/")
    public String factorial(@RequestParam(name = "first") int first,
                            @RequestParam(name = "second", defaultValue = "3", required = false) int second) throws Exception {

        log.debug("Http Request to calculate factorial input first={} second={}", first, second);

        List<Callable<Integer>> tasks = new ArrayList<>();

        tasks.add(() -> {
            return factorial(first);
        });

        tasks.add(() -> {
            return factorial(second);
        });

        List<Future<Integer>> result = concurrentExecutor.execute("Calculating Factorials", tasks);

        StringBuffer buffer = new StringBuffer();
        buffer.append("First Factorial = " + result.get(0).get());
        buffer.append(" Second Factorial = " + result.get(1).get());

        return "Allah ! " + buffer.toString();
    }

    private Integer factorial(int number) {
        try {
            return factorialService.calculate(number);
        } catch (InvalidParamaterException e) {
            log.error("Failed to execute factorial", e);
            throw e;
        }
    }

}